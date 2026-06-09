/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.module.auth.controller.vo;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoginAccountAddRoleReqVO 校验注解测试
 *
 * <p>覆盖问题 #10：roleIds 字段原来是 {@code @NotBlank}（不适用于 List），修复为
 * {@code @NotEmpty} + {@code List<@NotBlank String>}。
 */
class LoginAccountAddRoleReqVOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() {
        if (factory != null) {
            factory.close();
        }
    }

    private Set<String> violationProperties(Set<ConstraintViolation<LoginAccountAddRoleReqVO>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("#10 正常数据：通过校验")
    void validRequest_passes() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("account-1");
        req.setRoleIds(Arrays.asList("role-1", "role-2"));

        Set<ConstraintViolation<LoginAccountAddRoleReqVO>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "正常数据不应有校验错误");
    }

    @Test
    @DisplayName("#10 roleIds 为 null 触发 @NotEmpty 校验")
    void roleIdsNull_violatesNotEmpty() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("account-1");
        req.setRoleIds(null);

        Set<ConstraintViolation<LoginAccountAddRoleReqVO>> violations = validator.validate(req);
        assertEquals(1, violations.size());
        assertTrue(violationProperties(violations).contains("roleIds"));
    }

    @Test
    @DisplayName("#10 roleIds 为空集合触发 @NotEmpty 校验")
    void roleIdsEmpty_violatesNotEmpty() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("account-1");
        req.setRoleIds(Collections.emptyList());

        Set<ConstraintViolation<LoginAccountAddRoleReqVO>> violations = validator.validate(req);
        assertEquals(1, violations.size());
        assertTrue(violationProperties(violations).contains("roleIds"));
    }

    @Test
    @DisplayName("#10 roleIds 含空白元素触发 @NotBlank 校验")
    void roleIdsContainsBlank_violatesNotBlank() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("account-1");
        req.setRoleIds(Arrays.asList("role-1", "  ", "role-2"));

        Set<ConstraintViolation<LoginAccountAddRoleReqVO>> violations = validator.validate(req);
        assertEquals(1, violations.size());
        ConstraintViolation<LoginAccountAddRoleReqVO> v = violations.iterator().next();
        // 元素级别的 @NotBlank 路径形如 roleIds[1].<list element>
        assertTrue(v.getPropertyPath().toString().contains("roleIds"),
                "违规路径应包含 roleIds: " + v.getPropertyPath());
    }

    @Test
    @DisplayName("#10 id 为空白字符串触发 @NotBlank 校验")
    void idBlank_violatesNotBlank() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("   ");
        req.setRoleIds(List.of("role-1"));

        Set<ConstraintViolation<LoginAccountAddRoleReqVO>> violations = validator.validate(req);
        assertEquals(1, violations.size());
        assertTrue(violationProperties(violations).contains("id"));
    }

    @Test
    @DisplayName("#10 id 和 roleIds 同时违规：报告两条")
    void bothInvalid_reportsTwoViolations() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("");
        req.setRoleIds(null);

        Set<ConstraintViolation<LoginAccountAddRoleReqVO>> violations = validator.validate(req);
        assertEquals(2, violations.size());
        Set<String> properties = violationProperties(violations);
        assertTrue(properties.contains("id"));
        assertTrue(properties.contains("roleIds"));
    }
}
