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

package cn.bbwres.biscuit.module.auth.service.cache;

import cn.bbwres.biscuit.module.auth.constants.AuthSystemConstant;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.service.LoginAccountService;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisCheckUserLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * 登录用户的缓存信息
 *
 * @author zhanglinfeng
 */
@Service
public class LoginAccountCacheServiceImpl implements LoginAccountCacheService {

    private LoginAccountService loginAccountService;
    private RedisCheckUserLockService redisCheckUserLockService;

    @Autowired
    public void setRedisCheckUserLockService(RedisCheckUserLockService redisCheckUserLockService) {
        this.redisCheckUserLockService = redisCheckUserLockService;
    }

    @Autowired
    public void setLoginAccountService(LoginAccountService loginAccountService) {
        this.loginAccountService = loginAccountService;
    }

    /**
     * 根据用户名称查询数据
     *
     * @param username 用户名称
     * @return LoginAccountEntity
     */
    @Override
    @Cacheable(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY,
            key = "targetClass.name+':'+#tenantId+':'+#username", unless = "#result eq null")
    public LoginAccountEntity findByLoginUsername(String tenantId, String username) {
        return loginAccountService.findByLoginUsernameNoTenant(tenantId, username);
    }

    /**
     * 删除缓存
     *
     * @param username 用户名称
     */
    @Override
    @Caching(evict = {@CacheEvict(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY,
            key = "targetClass.name+':'+#tenantId+':'+#username")})
    public void deleteCache(String tenantId, String username) {
        //删除锁定信息
        redisCheckUserLockService.deleteLoginFailLock(tenantId, username);
    }

}
