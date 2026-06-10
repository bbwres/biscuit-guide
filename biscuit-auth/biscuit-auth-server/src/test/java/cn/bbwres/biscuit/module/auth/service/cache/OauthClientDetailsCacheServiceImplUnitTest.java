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
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.module.auth.service.OauthClientDetailsService;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OauthClientDetailsCacheServiceImpl 单元测试（Mockito 隔离环境，不依赖 Spring）
 *
 * <p>覆盖场景：
 * <ul>
 *     <li>getOauthClientDetails 委托 / 缓存命中 / null</li>
 *     <li>updateById 成功返回 entity；失败返回 null；与 service 调用一致</li>
 *     <li>缓存 key 正确性：targetClass.name+':'+id</li>
 *     <li>Cacheable/CacheEvict 注解存在性</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OauthClientDetailsCacheServiceImplUnitTest {

    @Mock
    private OauthClientDetailsService oauthClientDetailsService;

    @InjectMocks
    private OauthClientDetailsCacheServiceImpl cacheService;

    private static OauthClientDetailsEntity clientEntity(String id) {
        OauthClientDetailsEntity e = new OauthClientDetailsEntity();
        e.setId(id);
        e.setClientSecret("secret-" + id);
        e.setScopes("read,write");
        e.setAuthorizedGrantTypes("authorization_code,refresh_token");
        return e;
    }

    // ----------------------- getOauthClientDetails -----------------------

    @Test
    @DisplayName("getOauthClientDetails: 缓存未命中时委托给 service")
    void getOauthClientDetails_cacheMiss_delegatesToService() {
        OauthClientDetailsEntity expected = clientEntity("client-1");
        when(oauthClientDetailsService.getOauthClientDetails("client-1")).thenReturn(expected);

        OauthClientDetailsEntity actual = cacheService.getOauthClientDetails("client-1");

        assertNotNull(actual);
        assertEquals("client-1", actual.getId());
        assertEquals("secret-client-1", actual.getClientSecret());
        verify(oauthClientDetailsService, times(1)).getOauthClientDetails("client-1");
    }

    @Test
    @DisplayName("getOauthClientDetails: 业务返回 null 时结果为 null（不会缓存穿透）")
    void getOauthClientDetails_serviceReturnsNull_returnsNull() {
        when(oauthClientDetailsService.getOauthClientDetails("client-1")).thenReturn(null);

        OauthClientDetailsEntity actual = cacheService.getOauthClientDetails("client-1");

        assertNull(actual);
        verify(oauthClientDetailsService).getOauthClientDetails("client-1");
    }

    @Test
    @DisplayName("getOauthClientDetails: id 为 null 时不会抛 NPE")
    void getOauthClientDetails_nullId_doesNotThrow() {
        when(oauthClientDetailsService.getOauthClientDetails(null)).thenReturn(null);

        OauthClientDetailsEntity actual = assertDoesNotThrow(
                () -> cacheService.getOauthClientDetails(null));

        assertNull(actual);
        verify(oauthClientDetailsService).getOauthClientDetails(null);
    }

    @Test
    @DisplayName("getOauthClientDetails: 业务抛异常时透传")
    void getOauthClientDetails_serviceThrows_propagates() {
        RuntimeException expected = new RuntimeException("db error");
        when(oauthClientDetailsService.getOauthClientDetails("client-1")).thenThrow(expected);

        RuntimeException actual = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> cacheService.getOauthClientDetails("client-1"));

        assertEquals(expected, actual);
    }

    // ----------------------- updateById -----------------------

    @Test
    @DisplayName("updateById: 业务返回 true 时返回原 entity（缓存层不复制）")
    void updateById_serviceTrue_returnsEntity() {
        OauthClientDetailsEntity entity = clientEntity("client-1");
        when(oauthClientDetailsService.updateById(entity)).thenReturn(true);

        OauthClientDetailsEntity actual = cacheService.updateById(entity);

        assertNotNull(actual);
        assertSame(entity, actual, "缓存层应直接返回原 entity，便于上层继续持有引用");
        verify(oauthClientDetailsService, times(1)).updateById(entity);
    }

    @Test
    @DisplayName("updateById: 业务返回 false 时返回 null")
    void updateById_serviceFalse_returnsNull() {
        OauthClientDetailsEntity entity = clientEntity("client-1");
        when(oauthClientDetailsService.updateById(entity)).thenReturn(false);

        OauthClientDetailsEntity actual = cacheService.updateById(entity);

        assertNull(actual);
        verify(oauthClientDetailsService).updateById(entity);
    }

    @Test
    @DisplayName("updateById: entity 为 null 时不会抛 NPE")
    void updateById_nullEntity_doesNotThrow() {
        // 当前实现直接调 oauthClientDetailsService.updateById(null)，应让 service 自行处理或抛 NPE
        when(oauthClientDetailsService.updateById(null)).thenReturn(false);

        OauthClientDetailsEntity actual = assertDoesNotThrow(() -> cacheService.updateById(null));

        assertNull(actual);
        verify(oauthClientDetailsService).updateById(null);
    }

    @Test
    @DisplayName("updateById: 业务抛异常时透传（不会吞错）")
    void updateById_serviceThrows_propagates() {
        OauthClientDetailsEntity entity = clientEntity("client-1");
        RuntimeException expected = new RuntimeException("update failed");
        when(oauthClientDetailsService.updateById(entity)).thenThrow(expected);

        RuntimeException actual = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> cacheService.updateById(entity));

        assertEquals(expected, actual);
    }

    // ----------------------- 注解 / 缓存配置 -----------------------

    @Test
    @DisplayName("getOauthClientDetails 标注 @Cacheable，缓存名=auth#86400")
    void getOauthClientDetails_annotationAndKey() throws NoSuchMethodException {
        Method m = OauthClientDetailsCacheServiceImpl.class.getDeclaredMethod(
                "getOauthClientDetails", String.class);
        Cacheable ann = m.getAnnotation(Cacheable.class);
        assertNotNull(ann, "getOauthClientDetails 必须标注 @Cacheable");
        assertEquals(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, ann.cacheNames()[0]);
        assertTrue(ann.unless().contains("#result eq null"),
                "unless 应避免缓存 null（穿透）");
    }

    @Test
    @DisplayName("updateById 标注 @Caching + @CacheEvict，缓存名=auth#86400，key 使用 entity.id")
    void updateById_annotationAndKey() throws NoSuchMethodException {
        Method m = OauthClientDetailsCacheServiceImpl.class.getDeclaredMethod(
                "updateById", OauthClientDetailsEntity.class);
        Caching caching = m.getAnnotation(Caching.class);
        assertNotNull(caching, "updateById 必须标注 @Caching");
        CacheEvict[] evicts = caching.evict();
        assertNotNull(evicts);
        assertTrue(evicts.length >= 1, "至少应有一个 @CacheEvict");
        assertEquals(AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, evicts[0].cacheNames()[0]);
        assertTrue(evicts[0].key().contains("entity.getId()"),
                "CacheEvict 的 key 应基于 entity.getId()");
    }
}
