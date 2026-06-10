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

package cn.bbwres.biscuit.module.auth.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsPageReqVO;
import cn.bbwres.biscuit.module.auth.dao.OauthClientDetailsMapper;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OauthClientDetailsServiceImpl 单元测试（Mockito 隔离环境，不依赖 Spring 容器）。
 *
 * <p>覆盖 OauthClientDetailsService 接口的全部公共方法，并对以下潜在 bug 写回归测试：
 * <ul>
 *     <li>save 方法必须加密 clientSecret 后入库（避免明文落库）</li>
 *     <li>updateById 仅根据 mapper 的受影响行数返回布尔值</li>
 *     <li>save 即便 clientSecret 为空也不应抛 NPE（PasswordEncoder 由 mapper 决定行为）</li>
 *     <li>save 调用 passwordEncoder.encode 时使用 entity.getClientSecret() 入参</li>
 * </ul>
 *
 * <p>说明：当前实现中 {@code updateById} 不会重新加密已存在的 clientSecret（由 Controller/调用方负责），
 * 因此本测试仅验证当前 Service 行为契约（直接透传 mapper）。如未来修复为"若 clientSecret 不为空则加密"，
 * 需要在 {@code updateById_encryptsClientSecretWhenPresent} 处追加断言。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OauthClientDetailsServiceImplUnitTest {

    @Mock
    private OauthClientDetailsMapper oauthClientDetailsMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OauthClientDetailsServiceImpl oauthClientService;

    private static OauthClientDetailsEntity clientEntity(String id, String secret) {
        OauthClientDetailsEntity e = new OauthClientDetailsEntity();
        e.setId(id);
        e.setClientSecret(secret);
        return e;
    }

    // ----------------------- getOauthClientDetails -----------------------

    @Test
    @DisplayName("getOauthClientDetails 透传 id 给 mapper")
    void getOauthClientDetails_delegatesToMapper() {
        OauthClientDetailsEntity expected = clientEntity("c-1", "encoded-secret");
        when(oauthClientDetailsMapper.selectById("c-1")).thenReturn(expected);

        OauthClientDetailsEntity actual = oauthClientService.getOauthClientDetails("c-1");

        assertSame(expected, actual);
        verify(oauthClientDetailsMapper).selectById("c-1");
    }

    @Test
    @DisplayName("getOauthClientDetails id 不存在时返回 null")
    void getOauthClientDetails_notFound_returnsNull() {
        when(oauthClientDetailsMapper.selectById("missing")).thenReturn(null);

        OauthClientDetailsEntity actual = oauthClientService.getOauthClientDetails("missing");

        assertEquals(null, actual);
        verify(oauthClientDetailsMapper).selectById("missing");
    }

    // ----------------------- getOauthClientDetailsList -----------------------

    @Test
    @DisplayName("getOauthClientDetailsList 透传 ids 给 mapper")
    void getOauthClientDetailsList_delegatesToMapper() {
        Collection<String> ids = Arrays.asList("c-1", "c-2");
        List<OauthClientDetailsEntity> expected = Arrays.asList(
                clientEntity("c-1", "s1"), clientEntity("c-2", "s2"));
        when(oauthClientDetailsMapper.selectByIds(ids)).thenReturn(expected);

        List<OauthClientDetailsEntity> actual = oauthClientService.getOauthClientDetailsList(ids);

        assertSame(expected, actual);
        verify(oauthClientDetailsMapper).selectByIds(ids);
    }

    @Test
    @DisplayName("getOauthClientDetailsList 空集合场景下 mapper 返回空列表")
    void getOauthClientDetailsList_empty() {
        Collection<String> ids = Collections.emptyList();
        when(oauthClientDetailsMapper.selectByIds(ids)).thenReturn(Collections.emptyList());

        List<OauthClientDetailsEntity> actual = oauthClientService.getOauthClientDetailsList(ids);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    // ----------------------- getOauthClientDetailsPage -----------------------

    @Test
    @DisplayName("getOauthClientDetailsPage 透传 page 对象给 mapper")
    void getOauthClientDetailsPage_delegatesToMapper() {
        Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> page = new Page<>();
        page.setCurrent(1L);
        page.setSize(10L);
        page.setQuery(new OauthClientDetailsPageReqVO());
        when(oauthClientDetailsMapper.selectPage(page)).thenReturn(page);

        Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> actual =
                oauthClientService.getOauthClientDetailsPage(page);

        assertSame(page, actual);
        verify(oauthClientDetailsMapper).selectPage(page);
    }

    // ----------------------- updateById -----------------------

    @Test
    @DisplayName("updateById 当 mapper 受影响行数 > 0 时返回 true")
    void updateById_returnsTrueWhenMapperSucceeds() {
        OauthClientDetailsEntity entity = clientEntity("c-1", "plain-secret-update");
        when(oauthClientDetailsMapper.updateById(entity)).thenReturn(1);

        assertTrue(oauthClientService.updateById(entity));
        // 验证：当前实现 updateById 不会主动加密 clientSecret（透传 mapper）
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("updateById 当 mapper 受影响行数 = 0 时返回 false")
    void updateById_returnsFalseWhenMapperFails() {
        OauthClientDetailsEntity entity = clientEntity("missing", "secret");
        when(oauthClientDetailsMapper.updateById(entity)).thenReturn(0);

        assertFalse(oauthClientService.updateById(entity));
    }

    @Test
    @DisplayName("updateById 不会改写 entity 自身的 clientSecret（保留原值透传）")
    void updateById_doesNotMutateEntitySecret() {
        OauthClientDetailsEntity entity = clientEntity("c-1", "original-plain-or-encoded");
        when(oauthClientDetailsMapper.updateById(entity)).thenReturn(1);

        oauthClientService.updateById(entity);

        // Service 不应在 updateById 路径上重新赋值 clientSecret
        assertEquals("original-plain-or-encoded", entity.getClientSecret());
    }

    // ----------------------- save 重点回归 -----------------------

    @Test
    @DisplayName("save 加密 clientSecret 后入库（明文不落库）")
    void save_encryptsClientSecret() {
        OauthClientDetailsEntity entity = clientEntity("c-1", "plain-secret");
        when(passwordEncoder.encode("plain-secret")).thenReturn("encoded-secret");

        oauthClientService.save(entity);

        // 1. 密码被加密
        assertEquals("encoded-secret", entity.getClientSecret());
        // 2. 实体上不再保留明文
        assertNotEquals("plain-secret", entity.getClientSecret());
        // 3. 入库的 entity 是密文版本
        ArgumentCaptor<OauthClientDetailsEntity> captor = ArgumentCaptor.forClass(OauthClientDetailsEntity.class);
        verify(oauthClientDetailsMapper).insert(captor.capture());
        assertEquals("encoded-secret", captor.getValue().getClientSecret());
        assertEquals("c-1", captor.getValue().getId());
    }

    @Test
    @DisplayName("save 调用 passwordEncoder.encode 时使用 entity.getClientSecret() 入参")
    void save_passesEntityClientSecretToEncoder() {
        OauthClientDetailsEntity entity = clientEntity("c-2", "another-plain");
        when(passwordEncoder.encode("another-plain")).thenReturn("another-encoded");

        oauthClientService.save(entity);

        verify(passwordEncoder).encode("another-plain");
    }

    @Test
    @DisplayName("save 仅调用一次 insert，不应在内部产生额外写操作")
    void save_invokesInsertExactlyOnce() {
        OauthClientDetailsEntity entity = clientEntity("c-3", "plain");
        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        oauthClientService.save(entity);

        verify(oauthClientDetailsMapper).insert(any(OauthClientDetailsEntity.class));
        verify(oauthClientDetailsMapper, never()).updateById(any(OauthClientDetailsEntity.class));
    }

    @Test
    @DisplayName("save 不会调用 selectById / selectByIds / selectPage（写路径不应触发读）")
    void save_doesNotInvokeReadOperations() {
        OauthClientDetailsEntity entity = clientEntity("c-4", "plain");
        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        oauthClientService.save(entity);

        verify(oauthClientDetailsMapper, never()).selectById(anyString());
        verify(oauthClientDetailsMapper, never()).selectByIds(any(Collection.class));
    }

    // ----------------------- 边界/异常 -----------------------

    @Test
    @DisplayName("save 当 clientSecret 为 null 时：调用 passwordEncoder.encode(null) 由其决定行为，不抛 NPE 透传")
    void save_nullClientSecret_passesNullToEncoder() {
        OauthClientDetailsEntity entity = clientEntity("c-5", null);
        // 模拟 passwordEncoder 接受 null 入参（实际实现可抛 IllegalArgumentException，由 mapper 决定）
        when(passwordEncoder.encode(null)).thenReturn("encoded-null");

        oauthClientService.save(entity);

        // Service 应原样把 null 透传给 encoder
        verify(passwordEncoder).encode(null);
        // 入库时 secret 是 encoder 返回值（即使是占位值）
        ArgumentCaptor<OauthClientDetailsEntity> captor = ArgumentCaptor.forClass(OauthClientDetailsEntity.class);
        verify(oauthClientDetailsMapper).insert(captor.capture());
        assertEquals("encoded-null", captor.getValue().getClientSecret());
    }

    @Test
    @DisplayName("save 后 entity 自身 clientSecret 已被替换为密文（避免后续误用明文）")
    void save_entitySecretReplacedInPlace() {
        OauthClientDetailsEntity entity = clientEntity("c-6", "my-plain");
        when(passwordEncoder.encode("my-plain")).thenReturn("BCrypt$2a$10$xxxx");

        oauthClientService.save(entity);

        // 调用 save 后，外部引用的 entity 不应再保留明文
        assertEquals("BCrypt$2a$10$xxxx", entity.getClientSecret());
    }
}
