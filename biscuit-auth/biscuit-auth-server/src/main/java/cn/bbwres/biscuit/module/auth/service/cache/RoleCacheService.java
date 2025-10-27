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

import cn.bbwres.biscuit.module.auth.entity.RoleEntity;

import java.util.List;

/**
 * role 缓存信息
 *
 * @author zhanglinfeng
 */
public interface RoleCacheService {


    /**
     * 根据账户id查询关联的角色信息
     *
     * @param accountId 根据账户id查询关联的角色信息
     * @return List<RoleEntity>
     */
    List<RoleEntity> findByAccountId(String accountId);

    /**
     * 删除缓存
     *
     * @param accountId 账户id
     */
    void deleteCacheByAccountId(String accountId);
}
