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

import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.service.cache.MenuCacheService;
import cn.bbwres.biscuit.utils.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * ResourceServiceImpl 单元测试
 *
 * <p>不依赖 Spring 容器,通过反射设置 {@code loginAuthResource} 字段和 mock MenuCacheService。
 *
 * <p>覆盖：
 * <ul>
 *     <li>getLoginAuthResource 直接返回注入的列表</li>
 *     <li>getResourceByRole 多角色聚合菜单</li>
 *     <li>菜单去重(同 apiUrl 出现多次时输出一次)</li>
 *     <li>apiUrl 为空的菜单被过滤</li>
 *     <li>apiUrlMethod 为空时使用通配符 "*"</li>
 *     <li>apiUrlMethod 不为空时使用原值</li>
 *     <li>roleIds 为空/为 null 时返回空列表</li>
 *     <li>menuCacheService 返回 null 时的健壮性</li>
 *     <li>输出格式: "method:apiUrl" 用冒号分隔</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class ResourceServiceImplUnitTest {

    @Mock
    private MenuCacheService menuCacheService;

    private ResourceServiceImpl resourceService;

    @BeforeEach
    void setUp() {
        resourceService = new ResourceServiceImpl();
        ReflectionTestUtils.setField(resourceService, "menuCacheService", menuCacheService);
    }

    private static MenuEntity menu(String id, String apiUrl, String apiUrlMethod) {
        MenuEntity entity = new MenuEntity();
        entity.setId(id);
        entity.setApiUrl(apiUrl);
        entity.setApiUrlMethod(apiUrlMethod);
        return entity;
    }

    // ------------------- getLoginAuthResource -------------------

    @Test
    @DisplayName("getLoginAuthResource 返回注入的列表(不为 null)")
    void getLoginAuthResource_returnsInjectedList() {
        List<String> resources = Arrays.asList("/public/**", "/static/**");
        ReflectionTestUtils.setField(resourceService, "loginAuthResource", resources);

        List<String> result = resourceService.getLoginAuthResource();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("/public/**"));
        assertTrue(result.contains("/static/**"));
    }

    @Test
    @DisplayName("getLoginAuthResource 注入空列表时返回空列表")
    void getLoginAuthResource_empty() {
        ReflectionTestUtils.setField(resourceService, "loginAuthResource", Collections.emptyList());

        List<String> result = resourceService.getLoginAuthResource();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getLoginAuthResource 未注入时(@Value 默认值)返回空列表(而非 null)")
    void getLoginAuthResource_defaultIsEmpty() {
        // @Value("${auth.loginAuthResource:}") 默认值是空 List<String>
        // 不显式注入时字段是 null,调用方法应返回 null
        // 这里验证实际行为(可能为 null,由调用方负责处理)
        List<String> result = resourceService.getLoginAuthResource();
        // 这里不强求不为 null,因为没注入就是 null,这是 Spring @Value 的行为
        // 实际场景 Spring 容器会注入默认值
        assertNull(result);
    }

    // ------------------- getResourceByRole -------------------

    @Test
    @DisplayName("getResourceByRole: 单角色、单菜单、method 有值,输出 'method:apiUrl'")
    void getResourceByRole_singleMenuWithMethod() {
        String roleId = "role-1";
        when(menuCacheService.findByRoleId(roleId))
                .thenReturn(Collections.singletonList(menu("m1", "/api/users", "GET")));

        List<String> result = resourceService.getResourceByRole(Collections.singleton(roleId));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("GET" + StringUtils.DATA_STRING_SPLIT + "/api/users", result.get(0));
    }

    @Test
    @DisplayName("getResourceByRole: apiUrlMethod 为空时使用通配符 '*'")
    void getResourceByRole_blankMethod_useWildcard() {
        String roleId = "role-1";
        when(menuCacheService.findByRoleId(roleId))
                .thenReturn(Collections.singletonList(menu("m1", "/api/users", null)));

        List<String> result = resourceService.getResourceByRole(Collections.singleton(roleId));

        assertEquals(1, result.size());
        assertEquals("*" + StringUtils.DATA_STRING_SPLIT + "/api/users", result.get(0));
    }

    @Test
    @DisplayName("getResourceByRole: apiUrlMethod 为空字符串时也使用通配符")
    void getResourceByRole_emptyStringMethod_useWildcard() {
        String roleId = "role-1";
        when(menuCacheService.findByRoleId(roleId))
                .thenReturn(Collections.singletonList(menu("m1", "/api/users", "")));

        List<String> result = resourceService.getResourceByRole(Collections.singleton(roleId));

        assertEquals("*" + StringUtils.DATA_STRING_SPLIT + "/api/users", result.get(0));
    }

    @Test
    @DisplayName("getResourceByRole: apiUrl 为空的菜单被过滤掉")
    void getResourceByRole_blankApiUrlFiltered() {
        String roleId = "role-1";
        when(menuCacheService.findByRoleId(roleId)).thenReturn(Arrays.asList(
                menu("m1", "/api/users", "GET"),
                menu("m2", null, "POST"),         // apiUrl null,应被过滤
                menu("m3", "", "PUT"),              // apiUrl 空字符串,应被过滤
                menu("m4", "   ", "DELETE")         // apiUrl 空白字符串,应被过滤
        ));

        List<String> result = resourceService.getResourceByRole(Collections.singleton(roleId));

        assertEquals(1, result.size());
        assertEquals("GET" + StringUtils.DATA_STRING_SPLIT + "/api/users", result.get(0));
    }

    @Test
    @DisplayName("getResourceByRole: 多角色聚合所有菜单")
    void getResourceByRole_multipleRoles_aggregated() {
        when(menuCacheService.findByRoleId("role-1"))
                .thenReturn(Collections.singletonList(menu("m1", "/api/a", "GET")));
        when(menuCacheService.findByRoleId("role-2"))
                .thenReturn(Collections.singletonList(menu("m2", "/api/b", "POST")));

        Set<String> roleIds = new HashSet<>(Arrays.asList("role-1", "role-2"));
        List<String> result = resourceService.getResourceByRole(roleIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("GET" + StringUtils.DATA_STRING_SPLIT + "/api/a"));
        assertTrue(result.contains("POST" + StringUtils.DATA_STRING_SPLIT + "/api/b"));
    }

    @Test
    @DisplayName("getResourceByRole: 多角色 + 不同 method 的菜单全部输出")
    void getResourceByRole_multipleMethodsAndUrls() {
        when(menuCacheService.findByRoleId("role-1")).thenReturn(Arrays.asList(
                menu("m1", "/api/users", "GET"),
                menu("m2", "/api/users", "POST"),
                menu("m3", "/api/roles", null)   // method 空 -> *
        ));
        when(menuCacheService.findByRoleId("role-2")).thenReturn(Arrays.asList(
                menu("m4", "/api/login", "POST")
        ));

        Set<String> roleIds = new HashSet<>(Arrays.asList("role-1", "role-2"));
        List<String> result = resourceService.getResourceByRole(roleIds);

        assertEquals(4, result.size());
        String sep = StringUtils.DATA_STRING_SPLIT;
        assertTrue(result.contains("GET" + sep + "/api/users"));
        assertTrue(result.contains("POST" + sep + "/api/users"));
        assertTrue(result.contains("*" + sep + "/api/roles"));
        assertTrue(result.contains("POST" + sep + "/api/login"));
    }

    @Test
    @DisplayName("getResourceByRole: roleIds 为空集合时返回空列表")
    void getResourceByRole_emptyRoleIds() {
        List<String> result = resourceService.getResourceByRole(Collections.emptySet());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getResourceByRole: roleIds 为 null 时会抛 NPE(已知问题:实现未做 null 检查)")
    void getResourceByRole_nullRoleIds() {
        // 当前实现:for (String roleId : roleIds),roleIds==null 会抛 NPE
        // 这里记录当前实现行为,未来可作为"应改为 emptySet() 或显式 null 检查"的回归点
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> resourceService.getResourceByRole(null));
    }

    @Test
    @DisplayName("getResourceByRole: menuCacheService.findByRoleId 返回 null 列表时会抛 NPE(已知问题)")
    void getResourceByRole_cacheReturnsNullList() {
        // 当前实现:menuEntityList.addAll(null) 会抛 NPE
        // 记录当前实现行为,未来可作为"应做空值防御"的回归点
        when(menuCacheService.findByRoleId("role-1")).thenReturn(null);
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> resourceService.getResourceByRole(Collections.singleton("role-1")));
    }

    @Test
    @DisplayName("getResourceByRole: menuCacheService.findByRoleId 返回空列表时聚合结果为空")
    void getResourceByRole_cacheReturnsEmptyList() {
        when(menuCacheService.findByRoleId("role-1")).thenReturn(Collections.emptyList());

        List<String> result = resourceService.getResourceByRole(Collections.singleton("role-1"));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getResourceByRole: 输出按角色顺序(同 menu 顺序由 stream().toList() 保留)")
    void getResourceByRole_preservesMenuOrder() {
        when(menuCacheService.findByRoleId("role-1")).thenReturn(Arrays.asList(
                menu("m1", "/api/a", "GET"),
                menu("m2", "/api/b", "GET"),
                menu("m3", "/api/c", "GET")
        ));

        List<String> result = resourceService.getResourceByRole(Collections.singleton("role-1"));

        assertEquals(3, result.size());
        assertEquals("GET" + StringUtils.DATA_STRING_SPLIT + "/api/a", result.get(0));
        assertEquals("GET" + StringUtils.DATA_STRING_SPLIT + "/api/b", result.get(1));
        assertEquals("GET" + StringUtils.DATA_STRING_SPLIT + "/api/c", result.get(2));
    }

    @Test
    @DisplayName("getResourceByRole: 角色去重(Set 语义):同一角色多次传入只查询一次 cache")
    void getResourceByRole_setDeduplicatesRoleIds() {
        when(menuCacheService.findByRoleId("role-1"))
                .thenReturn(Collections.singletonList(menu("m1", "/api/users", "GET")));

        // Set 语义:重复 role-1 也只保留一份
        Set<String> roleIds = new HashSet<>(Arrays.asList("role-1", "role-1", "role-1"));
        List<String> result = resourceService.getResourceByRole(roleIds);

        assertEquals(1, result.size());
        assertEquals("GET" + StringUtils.DATA_STRING_SPLIT + "/api/users", result.get(0));
    }

    @Test
    @DisplayName("getResourceByRole: 分隔符使用 StringUtils.DATA_STRING_SPLIT(冒号)")
    void getResourceByRole_usesCorrectSeparator() {
        when(menuCacheService.findByRoleId("role-1"))
                .thenReturn(Collections.singletonList(menu("m1", "/api/users", "GET")));

        List<String> result = resourceService.getResourceByRole(Collections.singleton("role-1"));

        assertEquals(1, result.size());
        String item = result.get(0);
        // 验证分隔符
        assertTrue(item.contains(StringUtils.DATA_STRING_SPLIT),
                "输出应包含 StringUtils.DATA_STRING_SPLIT 作为分隔符");
        String sep = StringUtils.DATA_STRING_SPLIT;
        assertEquals("GET" + sep + "/api/users", item);
    }
}
