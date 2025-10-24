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


import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class OauthClientDetailsServiceTest {

    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;


    @Test
    void save() {
        OauthClientDetailsEntity client = new OauthClientDetailsEntity();
        client.setId("1-mp");
        client.setClientSecret("zlf");
        client.setClientAuthenticationMethods(StringUtils.list2ArrayStr(List.of("client_secret_basic", "client_secret_post", "none")));
        client.setScopes(StringUtils.list2ArrayStr(List.of("app", "user_info")));
        client.setAuthorizedGrantTypes(StringUtils.list2ArrayStr(List.of(AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
                AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(),AuthorizationGrantType.REFRESH_TOKEN.getValue(),"password")));
        client.setWebServerRedirectUri("http://www.baidu.com");
        client.setPostLogoutRedirectUri("http://www.baidu.com");
        client.setAccessTokenValidity(1800);
        client.setRefreshTokenValidity(18000);
        client.setAccessTokenFormat(OAuth2TokenFormat.REFERENCE.getValue());
        client.setReuseRefreshToken(true);
        client.setSingleUserLogin(true);
        client.setCreateTime(LocalDateTime.now());
        client.setCreator("test");

        oauthClientDetailsService.save(client);
    }
}