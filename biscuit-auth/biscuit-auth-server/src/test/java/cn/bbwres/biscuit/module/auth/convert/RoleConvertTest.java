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
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleAddReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleRespVO;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
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
 * RoleConvert 单元测试
 *
 * <p>不依赖 Spring 容器,直接通过 {@code RoleConvert.INSTANCE} 调用。
 */
class RoleConvertTest {

    private static RoleEntity buildEntity() {
        RoleEntity entity = new RoleEntity();
        entity.setId("role-1");
        entity.setRoleCode("ADMIN");
        entity.setRoleName("管理员");
        entity.setStatus(DataStatusEnum.NORMAL);
        entity.setRemark("超级管理员");
        entity.setClientId("client-A");
        entity.setCreator("zlf");
        entity.setCreateTime(LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        entity.setUpdater("zlf");
        entity.setUpdateTime(LocalDateTime.of(2025, 1, 2, 0, 0, 0));
        entity.setTenantId("tenant-1");
        return entity;
    }

    @Test
    @DisplayName("convert: Entity -> RespVO,所有字段一一映射")
    void convert_entityToRespVO() {
        RoleEntity entity = buildEntity();

        RoleRespVO vo = RoleConvert.INSTANCE.convert(entity);

        assertNotNull(vo);
        assertEquals("role-1", vo.getId());
        assertEquals("ADMIN", vo.getRoleCode());
        assertEquals("管理员", vo.getRoleName());
        assertEquals(DataStatusEnum.NORMAL, vo.getStatus());
        assertEquals("超级管理员", vo.getRemark());
        assertEquals("client-A", vo.getClientId());
        assertEquals("zlf", vo.getCreator());
        assertEquals(entity.getCreateTime(), vo.getCreateTime());
        assertEquals("zlf", vo.getUpdater());
        assertEquals(entity.getUpdateTime(), vo.getUpdateTime());
        assertEquals("tenant-1", vo.getTenantId());
    }

    @Test
    @DisplayName("convert: 传入 null 时返回 null")
    void convert_null() {
        assertNull(RoleConvert.INSTANCE.convert((RoleEntity) null));
    }

    @Test
    @DisplayName("convertList: 多个 Entity 转换为 RespVO 列表,顺序保留")
    void convertList() {
        RoleEntity e1 = buildEntity();
        RoleEntity e2 = buildEntity();
        e2.setId("role-2");
        e2.setRoleCode("USER");
        e2.setRoleName("普通用户");

        List<RoleRespVO> result = RoleConvert.INSTANCE.convertList(Arrays.asList(e1, e2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("role-1", result.get(0).getId());
        assertEquals("ADMIN", result.get(0).getRoleCode());
        assertEquals("role-2", result.get(1).getId());
        assertEquals("USER", result.get(1).getRoleCode());
    }

    @Test
    @DisplayName("convertList: 空列表返回空列表")
    void convertList_empty() {
        List<RoleRespVO> result = RoleConvert.INSTANCE.convertList(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("convertPage: 分页数据正确转换,query 对象引用保留")
    void convertPage() {
        RoleEntity e1 = buildEntity();
        RoleEntity e2 = buildEntity();
        e2.setId("role-2");

        Page<RoleEntity, RolePageReqVO> source = new Page<>();
        source.setRecords(Arrays.asList(e1, e2));
        source.setTotal(50L);
        source.setSize(20L);
        source.setCurrent(1L);
        source.setPages(3L);
        RolePageReqVO query = new RolePageReqVO();
        query.setRoleCode("A");
        source.setQuery(query);

        Page<RoleRespVO, RolePageReqVO> result = RoleConvert.INSTANCE.convertPage(source);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(50L, result.getTotal());
        assertEquals(20L, result.getSize());
        assertEquals(1L, result.getCurrent());
        assertEquals(3L, result.getPages());
        assertEquals(query, result.getQuery());
        assertEquals("role-1", result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("convertByAddReq: ReqVO -> Entity,所有必填字段映射过去")
    void convertByAddReq() {
        RoleAddReqVO req = new RoleAddReqVO();
        req.setId("role-100");
        req.setRoleCode("AUDIT");
        req.setRoleName("审计员");
        req.setRemark("审计");
        req.setClientId("client-B");
        req.setStatus(DataStatusEnum.NORMAL);

        RoleEntity entity = RoleConvert.INSTANCE.convertByAddReq(req);

        assertNotNull(entity);
        assertEquals("role-100", entity.getId());
        assertEquals("AUDIT", entity.getRoleCode());
        assertEquals("审计员", entity.getRoleName());
        assertEquals("审计", entity.getRemark());
        assertEquals("client-B", entity.getClientId());
        assertEquals(DataStatusEnum.NORMAL, entity.getStatus());
    }

    @Test
    @DisplayName("convertByAddReq: 传入 null 时返回 null")
    void convertByAddReq_null() {
        assertNull(RoleConvert.INSTANCE.convertByAddReq(null));
    }

    @Test
    @DisplayName("convert: 状态为 DISABLED 时也能正确映射")
    void convert_disabledStatus() {
        RoleEntity entity = buildEntity();
        entity.setStatus(DataStatusEnum.DISABLED);

        RoleRespVO vo = RoleConvert.INSTANCE.convert(entity);

        assertEquals(DataStatusEnum.DISABLED, vo.getStatus());
    }
}
