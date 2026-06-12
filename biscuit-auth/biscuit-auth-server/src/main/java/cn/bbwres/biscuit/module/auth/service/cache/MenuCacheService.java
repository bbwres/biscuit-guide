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

import cn.bbwres.biscuit.module.auth.entity.MenuEntity;

import java.util.List;

/**
 * 菜单缓存信息
 *
 * @author zhanglinfeng
 */
public interface MenuCacheService {


    /**
     * 根据角色id查询关联的菜单信息
     *
     * @param roleId 角色id
     * @return List<MenuEntity>
     */
    List<MenuEntity> findByRoleId(String roleId);

    /**
     * 删除缓存
     *
     * @param roleId 角色id
     */
    void deleteCacheByRoleId(String roleId);

    /**
     * 刷新所有菜单缓存
     */
    void refreshAllCache();
}
