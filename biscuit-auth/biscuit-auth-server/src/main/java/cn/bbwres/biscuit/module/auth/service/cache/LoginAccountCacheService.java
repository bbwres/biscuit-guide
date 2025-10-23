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

import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;

/**
 * 登录用户的缓存信息
 *
 * @author zhanglinfeng
 */
public interface LoginAccountCacheService {

    /**
     * 根据用户名称查询数据
     *
     * @param username 用户名称
     * @return LoginAccountEntity
     */
    LoginAccountEntity findByLoginUsername(String username);

    /**
     * 删除缓存
     *
     * @param username 用户名称
     */
    void deleteCache(String username);

}
