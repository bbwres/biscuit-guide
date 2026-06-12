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

package cn.bbwres.biscuit.module.auth.service;

import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootTest
class LoginAccountServiceTest {

    @Autowired
    private LoginAccountService loginAccountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void getLoginAccount() {
        System.out.println(passwordEncoder.encode("zlf123"));
    }

    @Test
    void save() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setLoginName("zlf");
        entity.setLoginPassword("zlf123");
        entity.setPhone("18765555555");
        entity.setUserId("9999999999999");
        entity.setStatus(LoginAccountStatusEnum.NORMAL);
        entity.setName("张三");
        entity.setTenantId("1");
        entity.setCreateTime(LocalDateTime.now());
        entity.setCreator("test");
        entity.setLastUpdatePasswordTime(LocalDateTime.now());

        loginAccountService.save(entity);
    }

    @Test
    void findById() {
        LoginAccountEntity loginAccount = loginAccountService.getLoginAccount("1980926223358472193");
        assert loginAccount != null;
    }
}