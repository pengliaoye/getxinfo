/*******************************************************************************
 *     Cloud Foundry
 *     Copyright (c) [2009-2016] Pivotal Software, Inc. All Rights Reserved.
 *
 *     This product is licensed to you under the Apache License, Version 2.0 (the "License").
 *     You may not use this product except in compliance with the License.
 *
 *     This product includes a number of subcomponents with
 *     separate copyright notices and license terms. Your use of these
 *     subcomponents is subject to the terms and conditions of the
 *     subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
package com.getxinfo.authentication.manager;

import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.getxinfo.authentication.AccountNotVerifiedException;
import com.getxinfo.authentication.AuthenticationPolicyRejectionException;
import com.getxinfo.authentication.UaaAuthentication;
import com.getxinfo.authentication.UaaAuthenticationDetails;
import com.getxinfo.authentication.UaaPrincipal;
import com.getxinfo.authentication.event.UnverifiedUserAuthenticationEvent;
import com.getxinfo.authentication.event.UserAuthenticationFailureEvent;
import com.getxinfo.authentication.event.UserAuthenticationSuccessEvent;
import com.getxinfo.authentication.event.UserNotFoundEvent;
import com.getxinfo.provider.PasswordPolicy;
import com.getxinfo.user.UaaUser;
import com.getxinfo.user.UaaUserDatabase;

/**
 * @author Luke Taylor
 * @author Dave Syer
 *
 */
@Component
public class AuthzAuthenticationManager implements AuthenticationManager, ApplicationEventPublisherAware {

    private final Log logger = LogFactory.getLog(getClass());
    private PasswordEncoder encoder;
    @Autowired
    private UaaUserDatabase userDatabase;
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private AccountLoginPolicy accountLoginPolicy;
    @Autowired
    private PasswordPolicy passwordPolicy;

    private String origin;
    private boolean allowUnverifiedUsers = true;
    
    public AuthzAuthenticationManager(){
    	this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication req) throws AuthenticationException {
        logger.debug("Processing authentication request for " + req.getName());

        if (req.getCredentials() == null) {
            BadCredentialsException e = new BadCredentialsException("No password supplied");
            publish(new AuthenticationFailureBadCredentialsEvent(req, e));
            throw e;
        }

        UaaUser user = getUaaUser(req);

        if (user == null) {
            logger.debug("No user named '" + req.getName() + "' was found for origin:"+ origin);
            publish(new UserNotFoundEvent(req));
        } else {
            if (!accountLoginPolicy.isAllowed(user, req)) {
                logger.warn("Login policy rejected authentication for " + user.getUsername() + ", " + user.getId()
                        + ". Ignoring login request.");
                AuthenticationPolicyRejectionException e = new AuthenticationPolicyRejectionException("Your account has been locked because of too many failed attempts to login.");
                publish(new AuthenticationFailureLockedEvent(req, e));
                throw e;
            }

            boolean passwordMatches = ((CharSequence) req.getCredentials()).length() != 0 && encoder.matches((CharSequence) req.getCredentials(), user.getPassword());

            if (!passwordMatches) {
                logger.debug("Password did not match for user " + req.getName());
                publish(new UserAuthenticationFailureEvent(user, req));
            } else {
                logger.debug("Password successfully matched for userId["+user.getUsername()+"]:"+user.getId());

                if (!(allowUnverifiedUsers && user.isLegacyVerificationBehavior()) && !user.isVerified()) {
                    publish(new UnverifiedUserAuthenticationEvent(user, req));
                    logger.debug("Account not verified: " + user.getId());
                    throw new AccountNotVerifiedException("Account not verified");
                }

                int expiringPassword = getPasswordExpiresInMonths();
                if (expiringPassword>0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(user.getPasswordLastModified().getTime());
                    cal.add(Calendar.MONTH, expiringPassword);
                    if (cal.getTimeInMillis() < System.currentTimeMillis()) {
                        throw new CredentialsExpiredException("Your current password has expired. Please reset your password.");
                    }
                }

                Authentication success = new UaaAuthentication(
                        new UaaPrincipal(user),
                        user.getAuthorities(),
                        (UaaAuthenticationDetails) req.getDetails());

                publish(new UserAuthenticationSuccessEvent(user, success));

                return success;
            }
        }

        BadCredentialsException e = new BadCredentialsException("Bad credentials");
        publish(new AuthenticationFailureBadCredentialsEvent(req, e));
        throw e;
    }

    protected int getPasswordExpiresInMonths() {
        return passwordPolicy.getExpirePasswordInMonths();
    }

    private UaaUser getUaaUser(Authentication req) {
        try {
            UaaUser user = userDatabase.retrieveUserByName(req.getName().toLowerCase(Locale.US), getOrigin());
            if (user!=null) {
                return user;
            }
        } catch (UsernameNotFoundException e) {
        }
        return null;
    }

    private void publish(ApplicationEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public AccountLoginPolicy getAccountLoginPolicy() {
        return this.accountLoginPolicy;
    }

    public void setAccountLoginPolicy(AccountLoginPolicy accountLoginPolicy) {
        this.accountLoginPolicy = accountLoginPolicy;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setAllowUnverifiedUsers(boolean allowUnverifiedUsers) {
        this.allowUnverifiedUsers = allowUnverifiedUsers;
    }
}
