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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

/**
 * 客户端获取
 *
 * @author zhanglinfeng
 */
@Slf4j
@Component("myClientDetailsService")
public class ClientDetailsServiceImpl implements ClientDetailsService {

    private OauthClientDetailsCacheService oauthClientDetailsCacheService;

    @Autowired
    public void setOauthClientDetailsCacheService(OauthClientDetailsCacheService oauthClientDetailsCacheService) {
        this.oauthClientDetailsCacheService = oauthClientDetailsCacheService;
    }


    /**
     * Load a client by the client id. This method must not return null.
     *
     * @param clientId The client id.
     * @return The client details (never null).
     * @throws ClientRegistrationException If the client account is locked, expired, disabled, or invalid for any other reason.
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        OauthClientDetailsEntity details = oauthClientDetailsCacheService.getOauthClientDetails(clientId);
        if (details == null) {
            log.info("根据clientId:[{}],没有查询到客户端信息!", clientId);
            return null;
        }
        BaseClientDetails baseClientDetails = new BaseClientDetails(details.getId(), details.getResourceIds(),
                details.getScope(), details.getAuthorizedGrantTypes(), details.getAuthorities());
        baseClientDetails.setAccessTokenValiditySeconds(details.getAccessTokenValidity());
        baseClientDetails.setRefreshTokenValiditySeconds(details.getRefreshTokenValidity());
        baseClientDetails.setClientSecret(details.getClientSecret());
        return baseClientDetails;
    }
}
