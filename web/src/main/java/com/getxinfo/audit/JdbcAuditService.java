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
package com.getxinfo.audit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Luke Taylor
 */
public class JdbcAuditService implements UaaAuditService {

    private final JdbcTemplate template;

    public JdbcAuditService(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    protected JdbcTemplate getJdbcTemplate() {
        return template;
    }

    @Override
    public List<AuditEvent> find(String principal, long after) {
        return template.query("select event_type, principal_id, event_data, created from sec_audit where " +
                        "principal_id=? and created > ? order by created desc", new AuditEventRowMapper(), principal,
                        new Timestamp(after));
    }

    @Override
    public void log(AuditEvent auditEvent) {
        String data = auditEvent.getData();
        data = data == null ? "" : data;
        data = data.length() > 255 ? data.substring(0, 255) : data;
        template.update("insert into sec_audit (principal_id, event_type, event_data) values (?,?,?,?,?)",
                        auditEvent.getPrincipalId(), auditEvent.getType().getCode(), data);
    }

    private class AuditEventRowMapper implements RowMapper<AuditEvent> {
        @Override
        public AuditEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            AuditEventType eventType = AuditEventType.fromCode(rs.getInt(1));
            String principalId = nullSafeTrim(rs.getString(2));
            String data = nullSafeTrim(rs.getString(4));
            long time = rs.getTimestamp(5).getTime();
            return new AuditEvent(eventType, principalId, data, time);
        }
    }

    private static String nullSafeTrim(String s) {
        return s == null ? null : s.trim();
    }
}
