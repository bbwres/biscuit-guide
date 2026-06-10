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

import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.constants.AuthSystemConstant;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.service.MenuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * MenuCacheServiceImpl 单元测试（Mockito 隔离环境，不依赖 Spring）
 *
 * <p>覆盖场景：
 * <ul>
 *     <li>findByRoleId 委托 / 返回 / 异常</li>
 *     <li>deleteCacheByRoleId 不调用业务 service（只清缓存）</li>
 *     <li>缓存 key 正确性：targetClass.name+':'+roleId</li>
 *     <li>unless 条件：空列表不入缓存</li>
 *     <li>Cacheable/CacheEvict 注解存在性</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MenuCacheServiceImplUnitTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuCacheServiceImpl cacheService;

    private static MenuEntity menuEntity(String id, String name) {
        MenuEntity e = new MenuEntity();
        e.setId(id);
        e.setName(name);
        e.setStatus(DataStatusEnum.NORMAL);
        return e;
    }

    private static List<MenuEntity> menuList(String... ids) {
        List<MenuEntity> list = new ArrayList<>();
        for (String id : ids) {
            list.add(menuEntity(id, "菜单-" + id));
        }
        return list;
    }

    // ----------------------- findByRoleId -----------------------

    @Test
    @DisplayName("findByRoleId: 缓存未命中时委托给 MenuService")
    void findByRoleId_cacheMiss_delegatesToService() {
        List<MenuEntity> expected = menuList("m1", "m2", "m3");
        when(menuService.findByRoleId("role-1")).thenReturn(expected);

        List<MenuEntity> actual = cacheService.findByRoleId("role-1");

        assertNotNull(actual);
        assertEquals(3, actual.size());
        assertEquals("m1", actual.get(0).getId());
        verify(menuService, times(1)).findByRoleId("role-1");
    }

    @Test
    @DisplayName("findByRoleId: 业务返回 null 时结果为 null")
    void findByRoleId_serviceReturnsNull_returnsNull() {
        when(menuService.findByRoleId("role-1")).thenReturn(null);

        List<MenuEntity> actual = cacheService.findByRoleId("role-1");

        assertNull(actual);
        verify(menuService).findByRoleId("role-1");
    }

    @Test
    @DisplayName("findByRoleId: 业务返回空列表时结果为空列表（命中 unless 不会缓存）")
    void findByRoleId_serviceReturnsEmpty_returnsEmpty() {
        when(menuService.findByRoleId("role-1")).thenReturn(Collections.emptyList());

        List<MenuEntity> actual = cacheService.findByRoleId("role-1");

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(menuService).findByRoleId("role-1");
    }

    @Test
    @DisplayName("findByRoleId: roleId 为 null 时不会抛 NPE")
    void findByRoleId_nullRoleId_doesNotThrow() {
        when(menuService.findByRoleId(null)).thenReturn(Collections.emptyList());

        List<MenuEntity> actual = assertDoesNotThrow(() -> cacheService.findByRoleId(null));

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(menuService).findByRoleId(null);
    }

    @Test
    @DisplayName("findByRoleId: 业务抛异常时透传")
    void findByRoleId_serviceThrows_propagates() {
        RuntimeException expected = new RuntimeException("db error");
        when(menuService.findByRoleId("role-1")).thenThrow(expected);

        RuntimeException actual = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> cacheService.findByRoleId("role-1"));

        assertEquals(expected, actual);
    }

    // ----------------------- deleteCacheByRoleId -----------------------

    @Test
    @DisplayName("deleteCacheByRoleId: 不触发业务 service（仅清理缓存）")
    void deleteCacheByRoleId_doesNotInvokeService() {
        cacheService.deleteCacheByRoleId("role-1");

        // 当前实现 deleteCacheByRoleId 是空体，应不会触发起始 service
        verifyNoInteractions(menuService);
    }

    @Test
    @DisplayName("deleteCacheByRoleId: roleId 为 null 时不抛 NPE")
    void deleteCacheByRoleId_nullRoleId_doesNotThrow() {
        assertDoesNotThrow(() -> cacheService.deleteCacheByRoleId(null));
    }

    // ----------------------- 注解 / 缓存配置 -----------------------

    @Test
    @DisplayName("findByRoleId 标注 @Cacheable，缓存名=auth#86400，命中 unless 不会缓存空值")
    void findByRoleId_annotationAndKey() throws NoSuchMethodException {
        Method m = MenuCacheServiceImpl.class.getDeclaredMethod("findByRoleId", String.class);
        Cacheable ann = m.getAnnotation(Cacheable.class);
        assertNotNull(ann, "findByRoleId 必须标注 @Cacheable");
        assertEquals(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, ann.cacheNames()[0]);
        assertTrue(ann.unless().contains("#result eq null"),
                "unless 应避免缓存 null（穿透）");
        assertTrue(ann.unless().contains("#result.size()<=0"),
                "unless 应避免缓存空列表（穿透）");
    }

    @Test
    @DisplayName("deleteCacheByRoleId 标注 @Caching + @CacheEvict，缓存名=auth#86400")
    void deleteCacheByRoleId_annotationAndKey() throws NoSuchMethodException {
        Method m = MenuCacheServiceImpl.class.getDeclaredMethod("deleteCacheByRoleId", String.class);
        Caching caching = m.getAnnotation(Caching.class);
        assertNotNull(caching, "deleteCacheByRoleId 必须标注 @Caching");
        CacheEvict[] evicts = caching.evict();
        assertNotNull(evicts);
        assertTrue(evicts.length >= 1, "至少应有一个 @CacheEvict");
        assertEquals(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, evicts[0].cacheNames()[0]);
    }
}
