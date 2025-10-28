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

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import cn.bbwres.biscuit.module.auth.service.cache.LoginAccountCacheService;
import cn.bbwres.biscuit.module.auth.service.cache.RoleCacheService;
import cn.bbwres.biscuit.security.oauth2.vo.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * 加载用户
 *
 * @author zhanglinfeng
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    public static final String TENANT_SPLIT = ",";

    private LoginAccountCacheService loginAccountCacheService;
    private RoleCacheService roleCacheService;

    @Autowired
    public void setLoginAccountCacheService(LoginAccountCacheService loginAccountCacheService) {
        this.loginAccountCacheService = loginAccountCacheService;
    }

    @Autowired
    public void setRoleCacheService(RoleCacheService roleCacheService) {
        this.roleCacheService = roleCacheService;
    }

    /**
     * 加载用户
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String user = username;
        String tenantId = null;
        String clientId = null;

        if (username.contains(TENANT_SPLIT)) {
            String[] split = username.split(TENANT_SPLIT);
            user = new String(Base64.getDecoder().decode(split[0]), StandardCharsets.UTF_8);
            clientId = split[1];
            tenantId = split[2];
        }
        LoginAccountEntity loginAccount = loginAccountCacheService.findByLoginUsername(tenantId, user);
        if (ObjectUtils.isEmpty(loginAccount)) {
            throw new UsernameNotFoundException(username);
        }
        if (LoginAccountStatusEnum.LOCKED.equals(loginAccount.getStatus())) {
            throw new LockedException(username);
        }
        if (LoginAccountStatusEnum.DISABLED.equals(loginAccount.getStatus())) {
            throw new DisabledException(username);
        }
        if (!LoginAccountStatusEnum.NORMAL.equals(loginAccount.getStatus())) {
            throw new UsernameNotFoundException(username);
        }
        List<RoleEntity> roleEntityList = roleCacheService.findByAccountId(loginAccount.getId());
        if (CollectionUtils.isEmpty(roleEntityList)) {
            throw new SystemRuntimeException(AuthErrorCodeConstants.ACCOUNT_NO_ROLE_ERROR);
        }
        String queryClientId = clientId;
        //查询角色信息
        UserDetails userDetails = User.builder()
                .username(loginAccount.getLoginName())
                .password(loginAccount.getLoginPassword())
                .roles(roleEntityList.stream()
                        .filter(roleEntity -> roleEntity.getClientId().equals(queryClientId))
                        .map(RoleEntity::getId)
                        .toArray(String[]::new))
                .build();

        return new AuthUser(userDetails, loginAccount.getName(), loginAccount.getId(), loginAccount.getTenantId());
    }
}
