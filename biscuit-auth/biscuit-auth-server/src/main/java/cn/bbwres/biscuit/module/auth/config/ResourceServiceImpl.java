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

import cn.bbwres.biscuit.security.oauth2.endpoint.ResourceService;
import jakarta.validation.constraints.Max;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 资源权限信息获取
 *
 * @author zhanglinfeng
 */
@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {
    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return a {@link List} object
     */
    @Override
    public List<String> getLoginAuthResource() {
        return List.of("/auth/**");
    }

    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleIds 角色id
     * @return a {@link List} object
     */
    @Override
    public List<String> getResourceByRole(Set<String> roleIds) {
        return List.of();
    }
}
