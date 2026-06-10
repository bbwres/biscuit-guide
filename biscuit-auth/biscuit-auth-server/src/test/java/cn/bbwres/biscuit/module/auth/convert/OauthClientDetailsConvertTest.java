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

package cn.bbwres.biscuit.module.auth.convert;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsAddOrUpdateReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsRespVO;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OauthClientDetailsConvert 单元测试
 *
 * <p>重点验证 {@code scopes} 字段映射(历史上因 Entity/VO 名称不一致导致
 * 静默丢字段,Convert 接口已加 {@code unmappedTargetPolicy = WARN},本测试
 * 锁定该行为)。
 */
class OauthClientDetailsConvertTest {

    private static OauthClientDetailsEntity buildEntity() {
        OauthClientDetailsEntity entity = new OauthClientDetailsEntity();
        entity.setId("client-1");
        entity.setClientSecret("secret-xxx");
        entity.setClientAuthenticationMethods("client_secret_basic,client_secret_post");
        // scopes 字段 (历史 bug:曾因 Entity 是 scope 单数导致丢失)
        entity.setScopes("read,write,trust");
        entity.setAuthorizedGrantTypes("authorization_code,refresh_token");
        entity.setWebServerRedirectUri("https://example.com/callback");
        entity.setPostLogoutRedirectUri("https://example.com/logout");
        entity.setAccessTokenValidity(3600);
        entity.setRefreshTokenValidity(7200);
        entity.setAccessTokenFormat("reference");
        entity.setReuseRefreshToken(Boolean.TRUE);
        entity.setSingleUserLogin(Boolean.FALSE);
        entity.setCreator("admin");
        entity.setCreateTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        entity.setUpdater("admin");
        entity.setUpdateTime(LocalDateTime.of(2025, 1, 2, 0, 0, 0));
        return entity;
    }

    @Test
    @DisplayName("scopes 重点验证: Entity.scopes -> RespVO.scopes 一一映射,不能因名称差异丢字段")
    void convert_scopesMapping() {
        OauthClientDetailsEntity entity = buildEntity();

        OauthClientDetailsRespVO vo = OauthClientDetailsConvert.INSTANCE.convert(entity);

        assertNotNull(vo);
        // 核心:scopes 字段必须传递过去(不是 null,也不是空字符串)
        assertEquals("read,write,trust", vo.getScopes(),
                "scopes 字段必须正确从 Entity 映射到 RespVO,这是历史 bug 的回归点");
    }

    @Test
    @DisplayName("convert: 所有字段一一映射,RespVO 包含 Entity 的全部属性")
    void convert_allFields() {
        OauthClientDetailsEntity entity = buildEntity();

        OauthClientDetailsRespVO vo = OauthClientDetailsConvert.INSTANCE.convert(entity);

        assertNotNull(vo);
        assertEquals("client-1", vo.getId());
        assertEquals("secret-xxx", vo.getClientSecret());
        assertEquals("client_secret_basic,client_secret_post", vo.getClientAuthenticationMethods());
        assertEquals("read,write,trust", vo.getScopes());
        assertEquals("authorization_code,refresh_token", vo.getAuthorizedGrantTypes());
        assertEquals("https://example.com/callback", vo.getWebServerRedirectUri());
        assertEquals("https://example.com/logout", vo.getPostLogoutRedirectUri());
        assertEquals(3600, vo.getAccessTokenValidity());
        assertEquals(7200, vo.getRefreshTokenValidity());
        assertEquals("reference", vo.getAccessTokenFormat());
        assertEquals(Boolean.TRUE, vo.getReuseRefreshToken());
        assertEquals(Boolean.FALSE, vo.getSingleUserLogin());
        assertEquals("admin", vo.getCreator());
        assertEquals(entity.getCreateTime(), vo.getCreateTime());
        assertEquals("admin", vo.getUpdater());
        assertEquals(entity.getUpdateTime(), vo.getUpdateTime());
    }

    @Test
    @DisplayName("convert: 传入 null 时返回 null")
    void convert_null() {
        assertNull(OauthClientDetailsConvert.INSTANCE.convert((OauthClientDetailsEntity) null));
    }

    @Test
    @DisplayName("convert: scopes 为 null 时,RespVO.scopes 也应为 null(不抛 NPE)")
    void convert_scopesNull() {
        OauthClientDetailsEntity entity = buildEntity();
        entity.setScopes(null);

        OauthClientDetailsRespVO vo = OauthClientDetailsConvert.INSTANCE.convert(entity);

        assertNotNull(vo);
        assertNull(vo.getScopes());
    }

    @Test
    @DisplayName("convert: scopes 为空字符串时,RespVO.scopes 也应为空字符串")
    void convert_scopesEmpty() {
        OauthClientDetailsEntity entity = buildEntity();
        entity.setScopes("");

        OauthClientDetailsRespVO vo = OauthClientDetailsConvert.INSTANCE.convert(entity);

        assertNotNull(vo);
        assertEquals("", vo.getScopes());
    }

    @Test
    @DisplayName("convertList: 多个 Entity 转换为 RespVO 列表,每个 scopes 字段都正确")
    void convertList_scopesMapping() {
        OauthClientDetailsEntity e1 = buildEntity();
        e1.setId("client-A");
        e1.setScopes("read");
        OauthClientDetailsEntity e2 = buildEntity();
        e2.setId("client-B");
        e2.setScopes("write,trust");

        List<OauthClientDetailsRespVO> result =
                OauthClientDetailsConvert.INSTANCE.convertList(Arrays.asList(e1, e2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("read", result.get(0).getScopes());
        assertEquals("write,trust", result.get(1).getScopes());
    }

    @Test
    @DisplayName("convertList: 空列表返回空列表")
    void convertList_empty() {
        List<OauthClientDetailsRespVO> result =
                OauthClientDetailsConvert.INSTANCE.convertList(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("convertPage: 分页数据正确转换,query 引用保留")
    void convertPage() {
        OauthClientDetailsEntity e1 = buildEntity();
        OauthClientDetailsEntity e2 = buildEntity();
        e2.setId("client-2");
        e2.setScopes("read");

        Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> source = new Page<>();
        source.setRecords(Arrays.asList(e1, e2));
        source.setTotal(2L);
        source.setSize(10L);
        source.setCurrent(1L);
        OauthClientDetailsPageReqVO query = new OauthClientDetailsPageReqVO();
        query.setId("client");
        source.setQuery(query);

        Page<OauthClientDetailsRespVO, OauthClientDetailsPageReqVO> result =
                OauthClientDetailsConvert.INSTANCE.convertPage(source);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(2L, result.getTotal());
        assertEquals(query, result.getQuery());
        // scopes 字段也要确保在分页转换中传递过去
        assertEquals("read,write,trust", result.getRecords().get(0).getScopes());
        assertEquals("read", result.getRecords().get(1).getScopes());
    }

    @Test
    @DisplayName("covertAddOrUpdateReq: ReqVO -> Entity,scopes 等所有字段映射过去(注意方法名拼写)")
    void covertAddOrUpdateReq() {
        // 注意:该方法名是 covertAddOrUpdateReq(开发笔误),但仍然测试其行为
        OauthClientDetailsAddOrUpdateReqVO req = new OauthClientDetailsAddOrUpdateReqVO();
        req.setId("client-100");
        req.setClientSecret("new-secret");
        req.setClientAuthenticationMethods("client_secret_basic");
        req.setScopes("read,write");
        req.setAuthorizedGrantTypes("authorization_code");
        req.setWebServerRedirectUri("https://example.com/cb");
        req.setPostLogoutRedirectUri("https://example.com/pl");
        req.setAccessTokenValidity(1800);
        req.setRefreshTokenValidity(3600);
        req.setAccessTokenFormat("self-contained");
        req.setReuseRefreshToken(Boolean.FALSE);
        req.setSingleUserLogin(Boolean.TRUE);

        OauthClientDetailsEntity entity = OauthClientDetailsConvert.INSTANCE.covertAddOrUpdateReq(req);

        assertNotNull(entity);
        assertEquals("client-100", entity.getId());
        assertEquals("new-secret", entity.getClientSecret());
        assertEquals("client_secret_basic", entity.getClientAuthenticationMethods());
        assertEquals("read,write", entity.getScopes());
        assertEquals("authorization_code", entity.getAuthorizedGrantTypes());
        assertEquals("https://example.com/cb", entity.getWebServerRedirectUri());
        assertEquals("https://example.com/pl", entity.getPostLogoutRedirectUri());
        assertEquals(1800, entity.getAccessTokenValidity());
        assertEquals(3600, entity.getRefreshTokenValidity());
        assertEquals("self-contained", entity.getAccessTokenFormat());
        assertEquals(Boolean.FALSE, entity.getReuseRefreshToken());
        assertEquals(Boolean.TRUE, entity.getSingleUserLogin());
    }

    @Test
    @DisplayName("covertAddOrUpdateReq: 传入 null 时返回 null")
    void covertAddOrUpdateReq_null() {
        assertNull(OauthClientDetailsConvert.INSTANCE.covertAddOrUpdateReq(null));
    }

    @Test
    @DisplayName("covertAddOrUpdateReq: scopes 为 null 时也能正常转换(不抛 NPE)")
    void covertAddOrUpdateReq_scopesNull() {
        OauthClientDetailsAddOrUpdateReqVO req = new OauthClientDetailsAddOrUpdateReqVO();
        req.setId("client-200");
        req.setClientSecret("s");
        req.setClientAuthenticationMethods("client_secret_basic");
        req.setScopes(null);
        req.setAuthorizedGrantTypes("authorization_code");
        req.setAccessTokenValidity(60);
        req.setRefreshTokenValidity(120);

        OauthClientDetailsEntity entity = OauthClientDetailsConvert.INSTANCE.covertAddOrUpdateReq(req);

        assertNotNull(entity);
        assertNull(entity.getScopes());
    }
}
