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
package com.getxinfo.authentication.event;


import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import com.getxinfo.audit.AuditEvent;
import com.getxinfo.audit.AuditEventType;
import com.getxinfo.user.UaaUser;

/**
 * Event which indicates that a user authentication failed.
 * 
 * This implies that the wrong credentials were supplied for a valid username.
 * 
 * @author Luke Taylor
 */
public class UserAuthenticationFailureEvent extends AbstractUaaAuthenticationEvent {
    private final UaaUser user;

    public UserAuthenticationFailureEvent(UaaUser user, Authentication authentication) {
        super(authentication);
        Assert.notNull(user, "UaaUser object cannot be null");
        this.user = user;
    }

    @Override
    public AuditEvent getAuditEvent() {
        return createAuditRecord(user.getId(), AuditEventType.UserAuthenticationFailure, user.getUsername());
    }

    public UaaUser getUser() {
        return user;
    }
}
