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

package cn.bbwres.biscuit.module.auth.config;

import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.module.auth.service.cache.OauthClientDetailsCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * AuthRegisteredClientRepository 单元测试
 *
 * <p>不依赖 Spring 容器,通过 mock OauthClientDetailsCacheService 并直接 new Repository。
 *
 * <p>覆盖：
 * <ul>
 *     <li>save() 抛 UnsupportedOperationException</li>
 *     <li>findById/findByClientId 在 cache 返回 null 时返回 null(不抛 NPE)</li>
 *     <li>findById/findByClientId 正常转换 Entity -> RegisteredClient(scopes/auth methods/grant types 都正确)</li>
 *     <li>scopes 为空时 RegisteredClient.getScopes() 也为空集合(不抛 NPE)</li>
 *     <li>scopes/auth methods/grant types 为 null 时也安全转换</li>
 *     <li>tokenSettings/clientSettings 字段正确传递</li>
 *     <li>clientSecret 正确传递</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AuthRegisteredClientRepositoryTest {

    @Mock
    private OauthClientDetailsCacheService oauthClientDetailsCacheService;

    private AuthRegisteredClientRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AuthRegisteredClientRepository();
        repository.setOauthClientDetailsCacheService(oauthClientDetailsCacheService);
    }

    private static OauthClientDetailsEntity buildEntity() {
        OauthClientDetailsEntity entity = new OauthClientDetailsEntity();
        entity.setId("client-1");
        entity.setClientSecret("secret-xxx");
        entity.setClientAuthenticationMethods("client_secret_basic,client_secret_post");
        entity.setScopes("read,write,trust");
        entity.setAuthorizedGrantTypes("authorization_code,refresh_token");
        entity.setWebServerRedirectUri("https://example.com/cb");
        entity.setPostLogoutRedirectUri("https://example.com/pl");
        entity.setAccessTokenValidity(3600);
        entity.setRefreshTokenValidity(7200);
        entity.setAccessTokenFormat("reference");
        entity.setReuseRefreshToken(Boolean.TRUE);
        entity.setSingleUserLogin(Boolean.FALSE);
        return entity;
    }

    // ------------------- save() -------------------

    @Test
    @DisplayName("save() 抛 UnsupportedOperationException(未实现)")
    void save_throwsUnsupported() {
        assertThrows(UnsupportedOperationException.class,
                () -> repository.save(null));
    }

    // ------------------- findById 正常路径 -------------------

    @Test
    @DisplayName("findById: cache 返回 null 时返回 null(不抛 NPE)")
    void findById_cacheNull_returnsNull() {
        when(oauthClientDetailsCacheService.getOauthClientDetails("missing-id")).thenReturn(null);

        RegisteredClient result = repository.findById("missing-id");

        assertNull(result);
    }

    @Test
    @DisplayName("findById: 正常转换 Entity -> RegisteredClient")
    void findById_normalConversion() {
        OauthClientDetailsEntity entity = buildEntity();
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertEquals("client-1", result.getId());
        assertEquals("client-1", result.getClientId());
        assertEquals("secret-xxx", result.getClientSecret());
    }

    @Test
    @DisplayName("findById: clientAuthenticationMethods 正确映射为 ClientAuthenticationMethod 集合")
    void findById_clientAuthMethods() {
        OauthClientDetailsEntity entity = buildEntity();
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertTrue(result.getClientAuthenticationMethods().contains(new ClientAuthenticationMethod("client_secret_basic")));
        assertTrue(result.getClientAuthenticationMethods().contains(new ClientAuthenticationMethod("client_secret_post")));
        assertEquals(2, result.getClientAuthenticationMethods().size());
    }

    @Test
    @DisplayName("findById: authorizedGrantTypes 正确映射为 AuthorizationGrantType 集合")
    void findById_grantTypes() {
        OauthClientDetailsEntity entity = buildEntity();
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertTrue(result.getAuthorizationGrantTypes().contains(new AuthorizationGrantType("authorization_code")));
        assertTrue(result.getAuthorizationGrantTypes().contains(new AuthorizationGrantType("refresh_token")));
        assertEquals(2, result.getAuthorizationGrantTypes().size());
    }

    @Test
    @DisplayName("findById: scopes 正确映射(对应 OauthClientDetailsConvert 中已加 WARN 验证的字段)")
    void findById_scopes() {
        OauthClientDetailsEntity entity = buildEntity();
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertTrue(result.getScopes().contains("read"));
        assertTrue(result.getScopes().contains("write"));
        assertTrue(result.getScopes().contains("trust"));
        assertEquals(3, result.getScopes().size());
    }

    @Test
    @DisplayName("findById: redirectUris/postLogoutRedirectUris 正确映射")
    void findById_redirectUris() {
        OauthClientDetailsEntity entity = buildEntity();
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertTrue(result.getRedirectUris().contains("https://example.com/cb"));
        assertTrue(result.getPostLogoutRedirectUris().contains("https://example.com/pl"));
    }

    @Test
    @DisplayName("findById: tokenSettings(accessToken/refreshToken 时长) 正确转换")
    void findById_tokenSettings() {
        OauthClientDetailsEntity entity = buildEntity();
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        TokenSettings ts = result.getTokenSettings();
        assertNotNull(ts);
        assertEquals(Duration.ofSeconds(3600), ts.getAccessTokenTimeToLive());
        assertEquals(Duration.ofSeconds(7200), ts.getRefreshTokenTimeToLive());
        // reuseRefreshTokens=true
        assertTrue(ts.isReuseRefreshTokens());
    }

    @Test
    @DisplayName("findById: clientSettings(singleUserLogin) 正确转换")
    void findById_clientSettings() {
        OauthClientDetailsEntity entity = buildEntity();
        // singleUserLogin=false
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        ClientSettings cs = result.getClientSettings();
        assertNotNull(cs);
        // requireProofKey=true (写死的)
        assertTrue(cs.isRequireProofKey());
        // singleUserLogin 字段值(由 CLIENT_SETTING_SINGLE_USER_LOGIN key 标记)
        assertFalse((Boolean) cs.getSettings().getOrDefault("settings.client.single-user-login", false));
    }

    // ------------------- findById 边界场景 -------------------

    @Test
    @DisplayName("findById: scopes 为 null 时 RegisteredClient.getScopes() 为空集合(不抛 NPE)")
    void findById_scopesNull() {
        OauthClientDetailsEntity entity = buildEntity();
        entity.setScopes(null);
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertNotNull(result.getScopes());
        assertTrue(result.getScopes().isEmpty());
    }

    @Test
    @DisplayName("findById: scopes 为空字符串时 RegisteredClient.getScopes() 为空集合")
    void findById_scopesEmpty() {
        OauthClientDetailsEntity entity = buildEntity();
        entity.setScopes("");
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertNotNull(result.getScopes());
        assertTrue(result.getScopes().isEmpty());
    }

    @Test
    @DisplayName("findById: clientAuthenticationMethods 为 null 时使用 OAuth2 默认值，不抛 NPE")
    void findById_clientAuthMethodsNull() {
        OauthClientDetailsEntity entity = buildEntity();
        entity.setClientAuthenticationMethods(null);
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertNotNull(result.getClientAuthenticationMethods());
        // 修复后：entity 为 null 时 Repository 使用 OAuth2 默认值 client_secret_basic
        assertFalse(result.getClientAuthenticationMethods().isEmpty());
        assertTrue(result.getClientAuthenticationMethods()
                .contains(new ClientAuthenticationMethod("client_secret_basic")));
    }

    @Test
    @DisplayName("findById: authorizedGrantTypes 为 null 时使用 OAuth2 默认值，不抛 NPE")
    void findById_grantTypesNull() {
        OauthClientDetailsEntity entity = buildEntity();
        entity.setAuthorizedGrantTypes(null);
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        assertNotNull(result.getAuthorizationGrantTypes());
        // 修复后：entity 为 null 时 Repository 使用 OAuth2 默认 grant type
        assertFalse(result.getAuthorizationGrantTypes().isEmpty());
        assertTrue(result.getAuthorizationGrantTypes()
                .contains(new AuthorizationGrantType("authorization_code")));
    }

    @Test
    @DisplayName("findById: accessTokenFormat 为 null 时不抛 NPE(OAuth2TokenFormat 接受 null)")
    void findById_accessTokenFormatNull() {
        OauthClientDetailsEntity entity = buildEntity();
        entity.setAccessTokenFormat(null);
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findById("client-1");

        assertNotNull(result);
        // TokenSettings 仍然能被创建,getAccessTokenFormat 可能为 null
        assertNotNull(result.getTokenSettings());
    }

    // ------------------- findByClientId -------------------

    @Test
    @DisplayName("findByClientId: cache 返回 null 时返回 null(不抛 NPE)")
    void findByClientId_cacheNull_returnsNull() {
        when(oauthClientDetailsCacheService.getOauthClientDetails("missing-client")).thenReturn(null);

        RegisteredClient result = repository.findByClientId("missing-client");

        assertNull(result);
    }

    @Test
    @DisplayName("findByClientId: 正常转换 Entity -> RegisteredClient(与 findById 行为一致)")
    void findByClientId_normalConversion() {
        OauthClientDetailsEntity entity = buildEntity();
        when(oauthClientDetailsCacheService.getOauthClientDetails("client-1")).thenReturn(entity);

        RegisteredClient result = repository.findByClientId("client-1");

        assertNotNull(result);
        assertEquals("client-1", result.getId());
        assertEquals("client-1", result.getClientId());
        // scopes 也要正确
        assertEquals(3, result.getScopes().size());
    }
}
