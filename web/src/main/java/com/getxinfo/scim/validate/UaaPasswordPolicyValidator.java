package com.getxinfo.scim.validate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.Rule;
import org.passay.RuleResult;
import org.springframework.stereotype.Component;

import com.getxinfo.provider.PasswordPolicy;
import com.getxinfo.scim.exception.InvalidPasswordException;

/**
 * ****************************************************************************
 * Cloud Foundry
 * Copyright (c) [2009-2015] Pivotal Software, Inc. All Rights Reserved.
 * <p>
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 * <p>
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 * *****************************************************************************
 */
@Component
public class UaaPasswordPolicyValidator implements PasswordValidator {

    private final PasswordPolicy policy;

    public UaaPasswordPolicyValidator(PasswordPolicy policy) {
        this.policy = policy;
    }

    @Override
    public void validate(String password) throws InvalidPasswordException {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        org.passay.PasswordValidator validator = getPasswordValidator(policy);
        RuleResult result = validator.validate(new PasswordData(password));
        if (!result.isValid()) {
            List<String> errorMessages = new LinkedList<>();
            for (String s : validator.getMessages(result)) {
                errorMessages.add(s);
            }
            if (!errorMessages.isEmpty()) {
                throw new InvalidPasswordException(errorMessages);
            }
        }
    }

    public org.passay.PasswordValidator getPasswordValidator(PasswordPolicy policy) {
        List<Rule> rules = new ArrayList<>();
        if (policy.getMinLength()>=0 && policy.getMaxLength()>0) {
            rules.add(new LengthRule(policy.getMinLength(), policy.getMaxLength()));
        }
        if (policy.getRequireUpperCaseCharacter()>0) {
            rules.add(new CharacterRule(EnglishCharacterData.UpperCase));
        }
        if (policy.getRequireLowerCaseCharacter()>0) {
            rules.add(new CharacterRule(EnglishCharacterData.LowerCase));
        }
        if (policy.getRequireDigit()>0) {
            rules.add(new CharacterRule(EnglishCharacterData.Digit));
        }
        if (policy.getRequireSpecialCharacter() > 0) {
            rules.add(new CharacterRule(EnglishCharacterData.Special));
        }
        return new org.passay.PasswordValidator(rules);
    }
}
