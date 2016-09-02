package com.getxinfo.authentication.event;


import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import com.getxinfo.audit.AuditEvent;
import com.getxinfo.audit.AuditEventType;
import com.getxinfo.user.UaaUser;

public class UnverifiedUserAuthenticationEvent extends AbstractUaaAuthenticationEvent {

    private final UaaUser user;

    public UnverifiedUserAuthenticationEvent(UaaUser user, Authentication authentication) {
        super(authentication);
        Assert.notNull(user, "UaaUser object cannot be null");
        this.user = user;
    }

    @Override
    public AuditEvent getAuditEvent() {
        return createAuditRecord(user.getId(), AuditEventType.UnverifiedUserAuthentication, user.getUsername());
    }

    public UaaUser getUser() {
        return user;
    }
}
