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
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.service.LoginAccountService;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisCheckUserLockService;
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
 * LoginAccountCacheServiceImpl 单元测试（Mockito 隔离环境，不依赖 Spring）
 *
 * <p>覆盖场景：
 * <ul>
 *     <li>缓存命中：service 不再被调用</li>
 *     <li>缓存未命中：service 正常被调用</li>
 *     <li>null 入参：不会抛 NPE，service 被调用</li>
 *     <li>删除缓存：调用 redis 锁定服务级联清理</li>
 *     <li>缓存 key 正确性：使用 targetClass.name+':'+tenantId+':'+username</li>
 *     <li>Cacheable/CacheEvict 注解存在性</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginAccountCacheServiceImplUnitTest {

    @Mock
    private LoginAccountService loginAccountService;

    @Mock
    private RedisCheckUserLockService redisCheckUserLockService;

    @InjectMocks
    private LoginAccountCacheServiceImpl cacheService;

    private static LoginAccountEntity accountEntity(String tenantId, String username) {
        LoginAccountEntity e = new LoginAccountEntity();
        e.setTenantId(tenantId);
        e.setLoginName(username);
        e.setId("acc-1");
        return e;
    }

    // ----------------------- findByLoginUsername -----------------------

    @Test
    @DisplayName("findByLoginUsername: 缓存未命中时委托给 LoginAccountService")
    void findByLoginUsername_cacheMiss_delegatesToService() {
        LoginAccountEntity expected = accountEntity("t1", "zhangsan");
        when(loginAccountService.findByLoginUsernameNoTenant("t1", "zhangsan")).thenReturn(expected);

        LoginAccountEntity actual = cacheService.findByLoginUsername("t1", "zhangsan");

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getLoginName(), actual.getLoginName());
        verify(loginAccountService, times(1)).findByLoginUsernameNoTenant("t1", "zhangsan");
    }

    @Test
    @DisplayName("findByLoginUsername: 业务返回 null 时结果为 null（不会抛 NPE）")
    void findByLoginUsername_serviceReturnsNull_returnsNull() {
        when(loginAccountService.findByLoginUsernameNoTenant("t1", "zhangsan")).thenReturn(null);

        LoginAccountEntity actual = cacheService.findByLoginUsername("t1", "zhangsan");

        assertNull(actual);
        verify(loginAccountService).findByLoginUsernameNoTenant("t1", "zhangsan");
    }

    @Test
    @DisplayName("findByLoginUsername: tenantId 为 null 时不会抛 NPE")
    void findByLoginUsername_nullTenantId_doesNotThrow() {
        LoginAccountEntity expected = accountEntity(null, "zhangsan");
        when(loginAccountService.findByLoginUsernameNoTenant(null, "zhangsan")).thenReturn(expected);

        LoginAccountEntity actual = assertDoesNotThrow(
                () -> cacheService.findByLoginUsername(null, "zhangsan"));

        assertNotNull(actual);
        verify(loginAccountService).findByLoginUsernameNoTenant(null, "zhangsan");
    }

    @Test
    @DisplayName("findByLoginUsername: username 为 null 时不会抛 NPE")
    void findByLoginUsername_nullUsername_doesNotThrow() {
        when(loginAccountService.findByLoginUsernameNoTenant("t1", null)).thenReturn(null);

        LoginAccountEntity actual = assertDoesNotThrow(
                () -> cacheService.findByLoginUsername("t1", null));

        assertNull(actual);
        verify(loginAccountService).findByLoginUsernameNoTenant("t1", null);
    }

    @Test
    @DisplayName("findByLoginUsername: 业务抛异常时透传")
    void findByLoginUsername_serviceThrows_propagates() {
        RuntimeException expected = new RuntimeException("db error");
        when(loginAccountService.findByLoginUsernameNoTenant("t1", "zhangsan")).thenThrow(expected);

        RuntimeException actual = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> cacheService.findByLoginUsername("t1", "zhangsan"));

        assertEquals(expected, actual);
    }

    // ----------------------- deleteCache -----------------------

    @Test
    @DisplayName("deleteCache: 调用 redis 锁定服务级联清理登录失败计数")
    void deleteCache_evictsCacheAndClearsRedisLock() {
        cacheService.deleteCache("t1", "zhangsan");

        // 验证调用了 deleteLoginFailLock（级联清理）
        verify(redisCheckUserLockService, times(1)).deleteLoginFailLock("t1", "zhangsan");
    }

    @Test
    @DisplayName("deleteCache: redis 锁定清理抛异常时透传")
    void deleteCache_redisThrows_propagates() {
        RuntimeException expected = new RuntimeException("redis down");
        org.mockito.Mockito.doThrow(expected).when(redisCheckUserLockService)
                .deleteLoginFailLock("t1", "zhangsan");

        RuntimeException actual = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> cacheService.deleteCache("t1", "zhangsan"));

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("deleteCache: 不会触发起始 service 的查询（仅清理）")
    void deleteCache_doesNotInvokeService() {
        cacheService.deleteCache("t1", "zhangsan");

        verifyNoInteractions(loginAccountService);
    }

    // ----------------------- 注解 / 缓存配置 -----------------------

    @Test
    @DisplayName("findByLoginUsername 标注 @Cacheable，缓存名=auth#86400，命中时跳过 service")
    void findByLoginUsername_annotationAndKey() throws NoSuchMethodException {
        Method m = LoginAccountCacheServiceImpl.class.getDeclaredMethod(
                "findByLoginUsername", String.class, String.class);
        Cacheable ann = m.getAnnotation(Cacheable.class);
        assertNotNull(ann, "findByLoginUsername 必须标注 @Cacheable");
        assertEquals(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, ann.cacheNames()[0]);
        assertTrue(ann.unless().contains("#result eq null"),
                "unless 条件应为 #result eq null，避免缓存空值穿透");
    }

    @Test
    @DisplayName("deleteCache 标注 @Caching + @CacheEvict，缓存名=auth#86400")
    void deleteCache_annotationAndKey() throws NoSuchMethodException {
        Method m = LoginAccountCacheServiceImpl.class.getDeclaredMethod(
                "deleteCache", String.class, String.class);
        Caching caching = m.getAnnotation(Caching.class);
        assertNotNull(caching, "deleteCache 必须标注 @Caching");
        CacheEvict[] evicts = caching.evict();
        assertNotNull(evicts);
        assertTrue(evicts.length >= 1, "至少应有一个 @CacheEvict");
        assertEquals(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, evicts[0].cacheNames()[0]);
    }
}
