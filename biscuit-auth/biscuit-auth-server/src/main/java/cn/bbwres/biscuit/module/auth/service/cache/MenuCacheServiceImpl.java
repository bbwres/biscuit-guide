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
import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色菜单缓存服务
 *
 * @author zhanglinfeng
 */
@Service
public class MenuCacheServiceImpl implements MenuCacheService {

    private MenuService menuService;

    private CacheManager cacheManager;

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * 根据角色id查询关联的菜单信息
     *
     * @param roleId 角色id
     * @return List<MenuEntity>
     */
    @Override
    @Cacheable(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY,
            key = "targetClass.name+':'+#roleId", unless = "#result eq null or #result.size()<=0")
    public List<MenuEntity> findByRoleId(String roleId) {
        return menuService.findByRoleId(roleId);
    }

    /**
     * 根据角色id查询关联菜单的所有接口
     *
     * @param roleId 角色id
     * @return 接口列表
     */
    @Override
    @Cacheable(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY,
            key = "targetClass.name+':apis:'+#roleId", unless = "#result eq null or #result.size()<=0")
    public List<MenuApiEntity> findApisByRoleId(String roleId) {
        return menuService.findApisByRoleId(roleId);
    }

    /**
     * 删除缓存
     *
     * @param roleId 角色id
     */
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY,
                    key = "targetClass.name+':'+#roleId"),
            @CacheEvict(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY,
                    key = "targetClass.name+':apis:'+#roleId")
    })
    public void deleteCacheByRoleId(String roleId) {

    }

    /**
     * 刷新所有菜单缓存
     */
    @Override
    public void refreshAllCache() {
        Cache cache = cacheManager.getCache(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY);
        if (cache != null) {
            cache.clear();
        }
    }
}
