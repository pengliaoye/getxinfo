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
package com.getxinfo.audit.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

import com.getxinfo.audit.AuditEvent;
import com.getxinfo.audit.AuditEventType;
import com.getxinfo.audit.UaaAuditService;

/**
 * Base class for UAA events that want to publish audit records.
 *
 * @author Luke Taylor
 * @author Dave Syer
 *
 */
public abstract class AbstractUaaEvent extends ApplicationEvent {

    private static final long serialVersionUID = -7639844193401892160L;

    private Authentication authentication;

    protected AbstractUaaEvent(Object source) {
        super(source);
        if (source instanceof Authentication) {
            this.authentication = (Authentication)source;
        }
    }

    public void process(UaaAuditService auditor) {
        auditor.log(getAuditEvent());
    }

    protected AuditEvent createAuditRecord(String principalId, AuditEventType type) {
        return createAuditRecord(principalId, type, null);
    }

    protected AuditEvent createAuditRecord(String principalId, AuditEventType type, String data) {
        return new AuditEvent(type, principalId, data, System.currentTimeMillis());
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public abstract AuditEvent getAuditEvent();

}
