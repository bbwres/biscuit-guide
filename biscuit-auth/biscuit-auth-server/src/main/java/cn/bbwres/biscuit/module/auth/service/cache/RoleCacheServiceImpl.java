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
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色 缓存服务
 *
 * @author zhanglinfeng
 */
@Service
public class RoleCacheServiceImpl implements RoleCacheService {

    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 根据账户id查询关联的角色信息
     *
     * @param accountId 根据账户id查询关联的角色信息
     * @return List<RoleEntity>
     */
    @Override
    @Cacheable(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY,
            key = "targetClass.name+':'+#accountId", unless = "#result eq null or #result.size()<=0")
    public List<RoleEntity> findByAccountId(String accountId) {
        return roleService.findByAccountIdNoTenant(accountId);
    }

    /**
     * 删除缓存
     *
     * @param accountId 账户id
     */
    @Override
    @Caching(evict = {@CacheEvict(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY,
            key = "targetClass.name+':'+#accountId")})
    public void deleteCacheByAccountId(String accountId) {

    }
}
