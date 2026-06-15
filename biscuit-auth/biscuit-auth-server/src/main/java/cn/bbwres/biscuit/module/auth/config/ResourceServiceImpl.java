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

import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.module.auth.service.cache.MenuCacheService;
import cn.bbwres.biscuit.security.oauth2.endpoint.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 资源权限信息获取
 *
 * @author zhanglinfeng
 */
@RefreshScope
@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    /**
     * 登录鉴权的数据
     */
    @Value("${auth.loginAuthResource:}")
    private List<String> loginAuthResource;

    private MenuCacheService menuCacheService;

    @Autowired
    public void setMenuCacheService(MenuCacheService menuCacheService) {
        this.menuCacheService = menuCacheService;
    }

    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return a {@link List} object
     */
    @Override
    public List<String> getLoginAuthResource() {
        return loginAuthResource;
    }

    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleIds 角色id
     * @return a {@link List} object
     */
    @Override
    public List<String> getResourceByRole(Set<String> roleIds) {
        List<MenuApiEntity> apiList = new ArrayList<>(16);
        for (String roleId : roleIds) {
            apiList.addAll(menuCacheService.findApisByRoleId(roleId));
        }
        //过滤掉没有 api 的数据
        return apiList.stream()
                .filter(api -> StringUtils.isNotBlank(api.getApiUrl()))
                .map(api -> {
                    if (StringUtils.isBlank(api.getApiUrlMethod())) {
                        return "*" + cn.bbwres.biscuit.utils.StringUtils.DATA_STRING_SPLIT + api.getApiUrl();
                    }
                    return api.getApiUrlMethod() + cn.bbwres.biscuit.utils.StringUtils.DATA_STRING_SPLIT + api.getApiUrl();
                }).toList();
    }
}
