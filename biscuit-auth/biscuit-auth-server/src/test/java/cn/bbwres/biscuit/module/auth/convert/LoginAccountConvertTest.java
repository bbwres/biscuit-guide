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
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountAddOrUpdateReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountRespVO;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
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
 * LoginAccountConvert 单元测试
 *
 * <p>不依赖 Spring 容器,直接通过 {@code LoginAccountConvert.INSTANCE} 调用。
 *
 * <p>注意：{@code loginPassword} 字段在 Entity 中存在,但 RespVO 中不存在,转换时应被忽略；
 * 反之亦然：ReqVO 包含的 loginPassword 转换到 Entity 时应被映射过去。
 */
class LoginAccountConvertTest {

    private static LoginAccountEntity buildEntity() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId("account-1");
        entity.setLoginName("zlf");
        entity.setLoginPassword("encoded-pwd");
        entity.setPhone("13800000000");
        entity.setUserId("user-1");
        entity.setStatus(LoginAccountStatusEnum.NORMAL);
        entity.setLockedTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0));
        entity.setName("张三");
        entity.setLastUpdatePasswordTime(LocalDateTime.of(2025, 1, 2, 12, 0, 0));
        entity.setCreator("admin");
        entity.setCreateTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        entity.setUpdater("admin");
        entity.setUpdateTime(LocalDateTime.of(2025, 1, 3, 0, 0, 0));
        entity.setTenantId("tenant-1");
        return entity;
    }

    @Test
    @DisplayName("convert: Entity -> RespVO,所有公共字段正确映射(loginPassword 不出现在 RespVO 中)")
    void convert_entityToRespVO() {
        LoginAccountEntity entity = buildEntity();

        LoginAccountRespVO vo = LoginAccountConvert.INSTANCE.convert(entity);

        assertNotNull(vo);
        assertEquals("account-1", vo.getId());
        assertEquals("zlf", vo.getLoginName());
        assertEquals("13800000000", vo.getPhone());
        assertEquals("user-1", vo.getUserId());
        assertEquals(LoginAccountStatusEnum.NORMAL, vo.getStatus());
        assertEquals(entity.getLockedTime(), vo.getLockedTime());
        assertEquals("张三", vo.getName());
        assertEquals(entity.getLastUpdatePasswordTime(), vo.getLastUpdatePasswordTime());
        assertEquals("admin", vo.getCreator());
        assertEquals(entity.getCreateTime(), vo.getCreateTime());
        assertEquals("admin", vo.getUpdater());
        assertEquals(entity.getUpdateTime(), vo.getUpdateTime());
        assertEquals("tenant-1", vo.getTenantId());
    }

    @Test
    @DisplayName("convert: 传入 null 时返回 null(MapStruct 默认行为)")
    void convert_null() {
        assertNull(LoginAccountConvert.INSTANCE.convert((LoginAccountEntity) null));
    }

    @Test
    @DisplayName("convertList: 多个 Entity 转换为 RespVO 列表,顺序保留")
    void convertList() {
        LoginAccountEntity e1 = buildEntity();
        LoginAccountEntity e2 = buildEntity();
        e2.setId("account-2");
        e2.setLoginName("lisi");

        List<LoginAccountRespVO> result = LoginAccountConvert.INSTANCE.convertList(Arrays.asList(e1, e2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("account-1", result.get(0).getId());
        assertEquals("zlf", result.get(0).getLoginName());
        assertEquals("account-2", result.get(1).getId());
        assertEquals("lisi", result.get(1).getLoginName());
    }

    @Test
    @DisplayName("convertList: 空列表返回空列表")
    void convertList_empty() {
        List<LoginAccountRespVO> result = LoginAccountConvert.INSTANCE.convertList(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("convertPage: 分页对象整体转换,records/total/pages/query 都正确传递")
    void convertPage() {
        LoginAccountEntity e1 = buildEntity();
        LoginAccountEntity e2 = buildEntity();
        e2.setId("account-2");

        Page<LoginAccountEntity, LoginAccountPageReqVO> source = new Page<>();
        source.setRecords(Arrays.asList(e1, e2));
        source.setTotal(100L);
        source.setSize(10L);
        source.setCurrent(2L);
        source.setPages(10L);
        LoginAccountPageReqVO query = new LoginAccountPageReqVO();
        query.setLoginName("z");
        source.setQuery(query);

        Page<LoginAccountRespVO, LoginAccountPageReqVO> result = LoginAccountConvert.INSTANCE.convertPage(source);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(100L, result.getTotal());
        assertEquals(10L, result.getSize());
        assertEquals(2L, result.getCurrent());
        assertEquals(10L, result.getPages());
        assertEquals("account-1", result.getRecords().get(0).getId());
        assertEquals("account-2", result.getRecords().get(1).getId());
        // query 对象引用应保留(MapStruct 不深拷贝 query)
        assertEquals(query, result.getQuery());
    }

    @Test
    @DisplayName("convertByAddReq: ReqVO -> Entity,包含 loginPassword 的安全敏感字段也应映射过去")
    void convertByAddReq() {
        LoginAccountAddOrUpdateReqVO req = new LoginAccountAddOrUpdateReqVO();
        req.setId("account-100");
        req.setLoginName("wangwu");
        req.setLoginPassword("raw-pwd-123");
        req.setPhone("13900000000");
        req.setUserId("user-100");
        req.setName("王五");
        req.setStatus(LoginAccountStatusEnum.NORMAL);

        LoginAccountEntity entity = LoginAccountConvert.INSTANCE.convertByAddReq(req);

        assertNotNull(entity);
        assertEquals("account-100", entity.getId());
        assertEquals("wangwu", entity.getLoginName());
        // loginPassword 字段会被映射到 Entity
        assertEquals("raw-pwd-123", entity.getLoginPassword());
        assertEquals("13900000000", entity.getPhone());
        assertEquals("user-100", entity.getUserId());
        assertEquals("王五", entity.getName());
        assertEquals(LoginAccountStatusEnum.NORMAL, entity.getStatus());
    }

    @Test
    @DisplayName("convertByAddReq: ReqVO 为 null 时返回 null")
    void convertByAddReq_null() {
        assertNull(LoginAccountConvert.INSTANCE.convertByAddReq(null));
    }

    @Test
    @DisplayName("convert: 状态枚举 LOCKED 也应能正常映射")
    void convert_lockedStatus() {
        LoginAccountEntity entity = buildEntity();
        entity.setStatus(LoginAccountStatusEnum.LOCKED);

        LoginAccountRespVO vo = LoginAccountConvert.INSTANCE.convert(entity);

        assertEquals(LoginAccountStatusEnum.LOCKED, vo.getStatus());
    }
}
