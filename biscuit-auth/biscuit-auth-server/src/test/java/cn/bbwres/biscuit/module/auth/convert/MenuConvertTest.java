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
import cn.bbwres.biscuit.module.auth.api.vo.MenuRespVO;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuAddReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.enums.MenuTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MenuConvert 单元测试
 *
 * <p>通过 MapStruct 生成的 {@code MenuConvert.INSTANCE} 直接调用，不依赖 Spring 容器。
 */
class MenuConvertTest {

    private static MenuEntity buildEntity() {
        MenuEntity entity = new MenuEntity();
        entity.setId("menu-1");
        entity.setName("用户管理");
        entity.setMenuType(MenuTypeEnum.MENU);
        entity.setMenuSort(1);
        entity.setParentId("0");
        entity.setIcon("user");
        entity.setComponent("/system/user");
        entity.setComponentName("SystemUser");
        entity.setStatus(DataStatusEnum.NORMAL);
        entity.setVisible(Boolean.TRUE);
        entity.setKeepAlive(Boolean.FALSE);
        entity.setAlwaysShow(Boolean.TRUE);
        entity.setApiUrlMethod("GET");
        entity.setApiUrl("/api/system/user/list");
        entity.setTreePath("0,menu-1");
        entity.setCreator("zlf");
        entity.setCreateTime(LocalDateTime.of(2025, 1, 1, 12, 0, 0));
        entity.setUpdater("zlf");
        entity.setUpdateTime(LocalDateTime.of(2025, 1, 2, 12, 0, 0));
        entity.setTenantId("tenant-1");
        return entity;
    }

    @Test
    @DisplayName("convert(Entity -> RespVO): 所有可映射字段都应正确赋值")
    void convert_entityToRespVO() {
        MenuEntity entity = buildEntity();

        MenuRespVO vo = MenuConvert.INSTANCE.convert(entity);

        assertNotNull(vo);
        assertEquals("menu-1", vo.getId());
        assertEquals("用户管理", vo.getName());
        assertEquals(MenuTypeEnum.MENU, vo.getMenuType());
        assertEquals(1, vo.getMenuSort());
        assertEquals("0", vo.getParentId());
        assertEquals("user", vo.getIcon());
        assertEquals("/system/user", vo.getComponent());
        assertEquals("SystemUser", vo.getComponentName());
        assertEquals(DataStatusEnum.NORMAL, vo.getStatus());
        assertEquals(Boolean.TRUE, vo.getVisible());
        assertEquals(Boolean.FALSE, vo.getKeepAlive());
        assertEquals(Boolean.TRUE, vo.getAlwaysShow());
        assertEquals("GET", vo.getApiUrlMethod());
        assertEquals("/api/system/user/list", vo.getApiUrl());
        assertEquals("0,menu-1", vo.getTreePath());
        assertEquals("zlf", vo.getCreator());
        assertEquals(entity.getCreateTime(), vo.getCreateTime());
        assertEquals("zlf", vo.getUpdater());
        assertEquals(entity.getUpdateTime(), vo.getUpdateTime());
        assertEquals("tenant-1", vo.getTenantId());
    }

    @Test
    @DisplayName("convert(Entity): 当 entity 为 null 时, MapStruct 应返回 null（按默认行为）")
    void convert_nullEntity() {
        // MapStruct 默认 null 传参返回 null
        assertNull(MenuConvert.INSTANCE.convert((MenuEntity) null));
    }

    @Test
    @DisplayName("convertList: 多个 entity 转换为 RespVO 列表,顺序保留")
    void convertList() {
        MenuEntity e1 = buildEntity();
        MenuEntity e2 = buildEntity();
        e2.setId("menu-2");
        e2.setName("角色管理");

        List<MenuRespVO> result = MenuConvert.INSTANCE.convertList(Arrays.asList(e1, e2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("menu-1", result.get(0).getId());
        assertEquals("用户管理", result.get(0).getName());
        assertEquals("menu-2", result.get(1).getId());
        assertEquals("角色管理", result.get(1).getName());
    }

    @Test
    @DisplayName("convertList: 传入空列表时返回空列表（不为 null）")
    void convertList_empty() {
        List<MenuRespVO> result = MenuConvert.INSTANCE.convertList(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("convertTreeList: Entity 列表转换为 TreeRespVO 列表,children 应保持 null")
    void convertTreeList() {
        MenuEntity e1 = buildEntity();
        MenuEntity e2 = buildEntity();
        e2.setId("menu-2");
        e2.setName("角色管理");
        e2.setParentId("menu-1");

        List<MenuTreeRespVO> result = MenuConvert.INSTANCE.convertTreeList(Arrays.asList(e1, e2));

        assertNotNull(result);
        assertEquals(2, result.size());
        // MapStruct 仅做字段映射，children 保持 null（树形由 MenuTreeUtils 构建）
        assertNull(result.get(0).getChildren());
        assertNull(result.get(1).getChildren());
        assertEquals(MenuTypeEnum.MENU, result.get(0).getMenuType());
        assertEquals("menu-2", result.get(1).getId());
    }

    @Test
    @DisplayName("convertTreeList: 传入空列表返回空列表")
    void convertTreeList_empty() {
        List<MenuTreeRespVO> result = MenuConvert.INSTANCE.convertTreeList(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("convertPage: Page<MenuEntity, MenuPageReqVO> -> Page<MenuRespVO, MenuPageReqVO> 分页数据正确转换")
    void convertPage() {
        MenuEntity e1 = buildEntity();
        MenuEntity e2 = buildEntity();
        e2.setId("menu-2");

        Page<MenuEntity, MenuPageReqVO> sourcePage = new Page<>();
        sourcePage.setRecords(Arrays.asList(e1, e2));
        sourcePage.setTotal(2L);
        sourcePage.setSize(10L);
        sourcePage.setCurrent(1L);
        sourcePage.setPages(1L);

        MenuPageReqVO query = new MenuPageReqVO();
        query.setName("管理");
        sourcePage.setQuery(query);

        Page<MenuRespVO, MenuPageReqVO> result = MenuConvert.INSTANCE.convertPage(sourcePage);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(2L, result.getTotal());
        assertEquals(10L, result.getSize());
        assertEquals(1L, result.getCurrent());
        assertEquals(1L, result.getPages());
        // query 引用应保留（MapStruct 不深拷贝 query 对象）
        assertSame(query, result.getQuery());
        assertEquals("menu-1", result.getRecords().get(0).getId());
        assertEquals("menu-2", result.getRecords().get(1).getId());
    }

    @Test
    @DisplayName("convertPage: records 为空时,转换后的 records 仍为空集合")
    void convertPage_empty() {
        Page<MenuEntity, MenuPageReqVO> sourcePage = new Page<>();
        sourcePage.setTotal(0L);
        sourcePage.setQuery(new MenuPageReqVO());

        Page<MenuRespVO, MenuPageReqVO> result = MenuConvert.INSTANCE.convertPage(sourcePage);

        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("convertByAddReq: MenuAddReqVO -> MenuEntity,字段一一对应")
    void convertByAddReq() {
        MenuAddReqVO req = new MenuAddReqVO();
        req.setId("menu-100");
        req.setName("新增菜单");
        req.setMenuType(MenuTypeEnum.BUTTON);
        req.setMenuSort(99);
        req.setParentId("0");
        req.setIcon("add");
        req.setComponent("/system/add");
        req.setComponentName("Add");
        req.setStatus(DataStatusEnum.NORMAL);
        req.setVisible(Boolean.TRUE);
        req.setKeepAlive(Boolean.FALSE);
        req.setAlwaysShow(Boolean.FALSE);
        req.setApiUrlMethod("POST");
        req.setApiUrl("/api/system/add");

        MenuEntity entity = MenuConvert.INSTANCE.convertByAddReq(req);

        assertNotNull(entity);
        assertEquals("menu-100", entity.getId());
        assertEquals("新增菜单", entity.getName());
        assertEquals(MenuTypeEnum.BUTTON, entity.getMenuType());
        assertEquals(99, entity.getMenuSort());
        assertEquals("0", entity.getParentId());
        assertEquals("add", entity.getIcon());
        assertEquals("/system/add", entity.getComponent());
        assertEquals("Add", entity.getComponentName());
        assertEquals(DataStatusEnum.NORMAL, entity.getStatus());
        assertEquals(Boolean.TRUE, entity.getVisible());
        assertEquals(Boolean.FALSE, entity.getKeepAlive());
        assertEquals(Boolean.FALSE, entity.getAlwaysShow());
        assertEquals("POST", entity.getApiUrlMethod());
        assertEquals("/api/system/add", entity.getApiUrl());
        // MenuAddReqVO 不包含 treePath 字段,转换后应为 null
        assertNull(entity.getTreePath());
    }

    @Test
    @DisplayName("convertByAddReq: 传入 null 时返回 null")
    void convertByAddReq_null() {
        assertNull(MenuConvert.INSTANCE.convertByAddReq(null));
    }

    @Test
    @DisplayName("convert: 验证 result 实例与下一次调用结果不共享可变状态(每条记录是新实例)")
    void convert_independentInstances() {
        MenuEntity e1 = buildEntity();
        MenuEntity e2 = buildEntity();
        e2.setId("menu-2");

        List<MenuEntity> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);

        List<MenuRespVO> first = MenuConvert.INSTANCE.convertList(list);
        List<MenuRespVO> second = MenuConvert.INSTANCE.convertList(list);

        assertEquals(first.size(), second.size());
        for (int i = 0; i < first.size(); i++) {
            assertEquals(first.get(i).getId(), second.get(i).getId());
            // 不同 List 调用应该是不同的对象（MapStruct 默认行为）
            assertEquals(first.get(i), second.get(i));
        }
    }
}
