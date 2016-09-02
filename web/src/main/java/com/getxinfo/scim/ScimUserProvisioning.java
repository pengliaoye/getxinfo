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
package com.getxinfo.scim;

import com.getxinfo.resources.Queryable;
import com.getxinfo.resources.ResourceManager;
import com.getxinfo.scim.exception.InvalidPasswordException;
import com.getxinfo.scim.exception.InvalidScimResourceException;
import com.getxinfo.scim.exception.ScimResourceNotFoundException;

/**
 * @author Luke Taylor
 * @author Dave Syer
 */
public interface ScimUserProvisioning extends ResourceManager<ScimUser>, Queryable<ScimUser> {

    public ScimUser createUser(ScimUser user, String password) throws InvalidPasswordException,
        InvalidScimResourceException;

    public void changePassword(String id, String oldPassword, String newPassword)
        throws ScimResourceNotFoundException;

    public ScimUser verifyUser(String id, int version) throws ScimResourceNotFoundException,
        InvalidScimResourceException;

    public boolean checkPasswordMatches(String id, String password) throws ScimResourceNotFoundException;

}

