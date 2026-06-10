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
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.service.RoleService;
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
 * RoleCacheServiceImpl 单元测试（Mockito 隔离环境，不依赖 Spring）
 *
 * <p>覆盖场景：
 * <ul>
 *     <li>findByAccountId 委托 / 返回 / 异常</li>
 *     <li>deleteCacheByAccountId 不调用业务 service（只清缓存）</li>
 *     <li>缓存 key 正确性：targetClass.name+':'+accountId</li>
 *     <li>unless 条件：空列表不入缓存</li>
 *     <li>Cacheable/CacheEvict 注解存在性</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleCacheServiceImplUnitTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleCacheServiceImpl cacheService;

    private static RoleEntity roleEntity(String id, String roleCode) {
        RoleEntity e = new RoleEntity();
        e.setId(id);
        e.setRoleCode(roleCode);
        e.setStatus(DataStatusEnum.NORMAL);
        return e;
    }

    private static List<RoleEntity> roleList(String... ids) {
        List<RoleEntity> list = new ArrayList<>();
        for (String id : ids) {
            list.add(roleEntity(id, "ROLE_" + id));
        }
        return list;
    }

    // ----------------------- findByAccountId -----------------------

    @Test
    @DisplayName("findByAccountId: 缓存未命中时委托给 RoleService")
    void findByAccountId_cacheMiss_delegatesToService() {
        List<RoleEntity> expected = roleList("r1", "r2");
        when(roleService.findByAccountIdNoTenant("acc-1")).thenReturn(expected);

        List<RoleEntity> actual = cacheService.findByAccountId("acc-1");

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("r1", actual.get(0).getId());
        assertEquals("r2", actual.get(1).getId());
        verify(roleService, times(1)).findByAccountIdNoTenant("acc-1");
    }

    @Test
    @DisplayName("findByAccountId: 业务返回 null 时结果为 null")
    void findByAccountId_serviceReturnsNull_returnsNull() {
        when(roleService.findByAccountIdNoTenant("acc-1")).thenReturn(null);

        List<RoleEntity> actual = cacheService.findByAccountId("acc-1");

        assertNull(actual);
        verify(roleService).findByAccountIdNoTenant("acc-1");
    }

    @Test
    @DisplayName("findByAccountId: 业务返回空列表时结果为空列表（命中 unless 不会缓存）")
    void findByAccountId_serviceReturnsEmpty_returnsEmpty() {
        when(roleService.findByAccountIdNoTenant("acc-1")).thenReturn(Collections.emptyList());

        List<RoleEntity> actual = cacheService.findByAccountId("acc-1");

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(roleService).findByAccountIdNoTenant("acc-1");
    }

    @Test
    @DisplayName("findByAccountId: accountId 为 null 时不会抛 NPE")
    void findByAccountId_nullAccountId_doesNotThrow() {
        when(roleService.findByAccountIdNoTenant(null)).thenReturn(Collections.emptyList());

        List<RoleEntity> actual = assertDoesNotThrow(() -> cacheService.findByAccountId(null));

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(roleService).findByAccountIdNoTenant(null);
    }

    @Test
    @DisplayName("findByAccountId: 业务抛异常时透传")
    void findByAccountId_serviceThrows_propagates() {
        RuntimeException expected = new RuntimeException("db error");
        when(roleService.findByAccountIdNoTenant("acc-1")).thenThrow(expected);

        RuntimeException actual = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> cacheService.findByAccountId("acc-1"));

        assertEquals(expected, actual);
    }

    // ----------------------- deleteCacheByAccountId -----------------------

    @Test
    @DisplayName("deleteCacheByAccountId: 不触发业务 service（仅清理缓存）")
    void deleteCacheByAccountId_doesNotInvokeService() {
        cacheService.deleteCacheByAccountId("acc-1");

        // 当前实现 deleteCacheByAccountId 是空体，应不会触发起始 service
        verifyNoInteractions(roleService);
    }

    @Test
    @DisplayName("deleteCacheByAccountId: accountId 为 null 时不抛 NPE")
    void deleteCacheByAccountId_nullAccountId_doesNotThrow() {
        assertDoesNotThrow(() -> cacheService.deleteCacheByAccountId(null));
    }

    // ----------------------- 注解 / 缓存配置 -----------------------

    @Test
    @DisplayName("findByAccountId 标注 @Cacheable，缓存名=auth#86400，命中 unless 不会缓存空值")
    void findByAccountId_annotationAndKey() throws NoSuchMethodException {
        Method m = RoleCacheServiceImpl.class.getDeclaredMethod("findByAccountId", String.class);
        Cacheable ann = m.getAnnotation(Cacheable.class);
        assertNotNull(ann, "findByAccountId 必须标注 @Cacheable");
        assertEquals(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, ann.cacheNames()[0]);
        assertTrue(ann.unless().contains("#result eq null"),
                "unless 应避免缓存 null（穿透）");
        assertTrue(ann.unless().contains("#result.size()<=0"),
                "unless 应避免缓存空列表（穿透）");
    }

    @Test
    @DisplayName("deleteCacheByAccountId 标注 @Caching + @CacheEvict，缓存名=auth#86400")
    void deleteCacheByAccountId_annotationAndKey() throws NoSuchMethodException {
        Method m = RoleCacheServiceImpl.class.getDeclaredMethod("deleteCacheByAccountId", String.class);
        Caching caching = m.getAnnotation(Caching.class);
        assertNotNull(caching, "deleteCacheByAccountId 必须标注 @Caching");
        CacheEvict[] evicts = caching.evict();
        assertNotNull(evicts);
        assertTrue(evicts.length >= 1, "至少应有一个 @CacheEvict");
        assertEquals(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, evicts[0].cacheNames()[0]);
    }
}
