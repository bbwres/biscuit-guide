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
import cn.bbwres.biscuit.security.oauth2.constants.Oauth2SystemConstants;
import cn.bbwres.biscuit.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户端信息获取
 *
 * @author zhanglinfeng
 */
@Slf4j
@Repository
public class AuthRegisteredClientRepository implements RegisteredClientRepository {

    private OauthClientDetailsCacheService oauthClientDetailsCacheService;

    @Autowired
    public void setOauthClientDetailsCacheService(OauthClientDetailsCacheService oauthClientDetailsCacheService) {
        this.oauthClientDetailsCacheService = oauthClientDetailsCacheService;
    }

    /**
     * Saves the registered client.
     *
     * <p>
     * IMPORTANT: Sensitive information should be encoded externally from the
     * implementation, e.g. {@link RegisteredClient#getClientSecret()}
     *
     * @param registeredClient the {@link RegisteredClient}
     */
    @Override
    public void save(RegisteredClient registeredClient) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the registered client identified by the provided {@code id}, or
     * {@code null} if not found.
     *
     * @param id the registration identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Nullable
    @Override
    public RegisteredClient findById(String id) {
        OauthClientDetailsEntity oauthClientDetails = oauthClientDetailsCacheService.getOauthClientDetails(id);
        return castRegisteredClientByEntity(oauthClientDetails);
    }

    /**
     * Returns the registered client identified by the provided {@code clientId}, or
     * {@code null} if not found.
     *
     * @param clientId the client identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Nullable
    @Override
    public RegisteredClient findByClientId(String clientId) {
        OauthClientDetailsEntity oauthClientDetails = oauthClientDetailsCacheService.getOauthClientDetails(clientId);
        return castRegisteredClientByEntity(oauthClientDetails);
    }

    /**
     * 根据OauthClientDetailsEntity 转换数据
     *
     * @param entity
     * @return
     */
    private RegisteredClient castRegisteredClientByEntity(OauthClientDetailsEntity entity) {
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        // 防御：null 字段采用合理默认值，避免下游 OAuth2 库抛 IllegalArgument
        return RegisteredClient.withId(entity.getId())
                .clientId(entity.getId())
                .clientSecret(entity.getClientSecret())
                .clientAuthenticationMethods(methods -> {
                    List<String> list = StringUtils.arrayStr2List(entity.getClientAuthenticationMethods());
                    if (ObjectUtils.isEmpty(list)) {
                        // entity 未配置时使用 OAuth2 默认值，避免空集
                        list = java.util.Arrays.asList("client_secret_basic");
                    }
                    methods.addAll(list.stream()
                            .map(ClientAuthenticationMethod::new)
                            .toList());
                })
                .authorizationGrantTypes(grantTypes -> {
                    List<String> list = StringUtils.arrayStr2List(entity.getAuthorizedGrantTypes());
                    if (ObjectUtils.isEmpty(list)) {
                        // entity 未配置时使用 OAuth2 默认 grant type
                        list = java.util.Arrays.asList("authorization_code", "refresh_token");
                    }
                    grantTypes.addAll(list.stream()
                            .map(AuthorizationGrantType::new)
                            .toList());
                })
                .redirectUri(entity.getWebServerRedirectUri())
                .postLogoutRedirectUri(entity.getPostLogoutRedirectUri())
                .scopes(sc -> {
                    if (!ObjectUtils.isEmpty(entity.getScopes())) {
                        sc.addAll(StringUtils.arrayStr2List(entity.getScopes()));
                    }
                })
                .tokenSettings(TokenSettings.builder()
                        //设置不透明token(accessTokenFormat 为 null 时使用默认 self-contained)
                        .accessTokenFormat(ObjectUtils.isEmpty(entity.getAccessTokenFormat())
                                ? OAuth2TokenFormat.SELF_CONTAINED
                                : new OAuth2TokenFormat(entity.getAccessTokenFormat()))
                        //刷新token只能使用一次(null 默认 true)
                        .reuseRefreshTokens(entity.getReuseRefreshToken() == null
                                ? true : entity.getReuseRefreshToken())
                        .accessTokenTimeToLive(Duration.ofSeconds(
                                entity.getAccessTokenValidity() == null ? 3600L : entity.getAccessTokenValidity()))
                        .refreshTokenTimeToLive(Duration.ofSeconds(
                                entity.getRefreshTokenValidity() == null ? 86400L : entity.getRefreshTokenValidity()))
                        .build())
                .clientSettings(ClientSettings.builder()
                        .setting(Oauth2SystemConstants.CLIENT_SETTING_SINGLE_USER_LOGIN, entity.getSingleUserLogin())
                        .requireProofKey(true).build())
                .build();
    }
}
