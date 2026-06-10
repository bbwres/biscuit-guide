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
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleAddMenuReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.dao.RoleAccountMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleMenuMapper;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleMenuEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RoleServiceImpl 单元测试（Mockito 隔离环境，不依赖 Spring 容器）。
 *
 * <p>覆盖 RoleService 接口的全部公共方法，并对以下潜在 bug 写回归测试：
 * <ul>
 *     <li>roleMenuConfig 删除旧关联条件必须为 {@code eq(RoleMenuEntity::getRoleId, roleId)}，避免误删全表</li>
 *     <li>roleMenuConfig 正常路径：先删后插，构造的 RoleMenuEntity 字段正确（roleId/menuId/roleCode/clientId）</li>
 *     <li>roleMenuConfig menuIds 为空/null 时只删不插</li>
 *     <li>addRole 强制设置 status 为 NORMAL</li>
 *     <li>findByAccountIdNoTenant 透传 NORMAL 状态</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleServiceImplUnitTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleMenuMapper roleMenuMapper;

    @Mock
    private RoleAccountMapper roleAccountMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private static RoleEntity roleEntity(String id, String roleCode, String clientId) {
        RoleEntity e = new RoleEntity();
        e.setId(id);
        e.setRoleCode(roleCode);
        e.setClientId(clientId);
        e.setStatus(DataStatusEnum.DISABLED);
        return e;
    }

    // ----------------------- getRole / findById -----------------------

    @Test
    @DisplayName("getRole 透传 id 给 mapper")
    void getRole_delegatesToMapper() {
        RoleEntity expected = roleEntity("r-1", "ADMIN", "client-1");
        when(roleMapper.selectById("r-1")).thenReturn(expected);

        RoleEntity actual = roleService.getRole("r-1");

        assertSame(expected, actual);
        verify(roleMapper).selectById("r-1");
    }

    @Test
    @DisplayName("findById 透传 id 给 mapper")
    void findById_delegatesToMapper() {
        RoleEntity expected = roleEntity("r-1", "ADMIN", "client-1");
        when(roleMapper.selectById("r-1")).thenReturn(expected);

        RoleEntity actual = roleService.findById("r-1");

        assertSame(expected, actual);
        verify(roleMapper).selectById("r-1");
    }

    // ----------------------- getRoleList -----------------------

    @Test
    @DisplayName("getRoleList 透传 ids 集合给 mapper")
    void getRoleList_delegatesToMapper() {
        Collection<String> ids = Arrays.asList("r-1", "r-2");
        List<RoleEntity> expected = Arrays.asList(roleEntity("r-1", "A", "c1"), roleEntity("r-2", "B", "c1"));
        when(roleMapper.selectByIds(ids)).thenReturn(expected);

        List<RoleEntity> actual = roleService.getRoleList(ids);

        assertSame(expected, actual);
        verify(roleMapper).selectByIds(ids);
    }

    // ----------------------- getRolePage -----------------------

    @Test
    @DisplayName("getRolePage 透传 page 对象给 mapper")
    void getRolePage_delegatesToMapper() {
        Page<RoleEntity, RolePageReqVO> page = new Page<>();
        page.setCurrent(1L);
        page.setSize(10L);
        page.setQuery(new RolePageReqVO());
        when(roleMapper.selectPage(page)).thenReturn(page);

        Page<RoleEntity, RolePageReqVO> actual = roleService.getRolePage(page);

        assertSame(page, actual);
        verify(roleMapper).selectPage(page);
    }

    // ----------------------- findByRoleCodeAndClientId -----------------------

    @Test
    @DisplayName("findByRoleCodeAndClientId 透传 roleCode/clientId")
    void findByRoleCodeAndClientId_delegatesToMapper() {
        RoleEntity expected = roleEntity("r-1", "ADMIN", "client-1");
        when(roleMapper.findByRoleCodeAndClientId("ADMIN", "client-1")).thenReturn(expected);

        RoleEntity actual = roleService.findByRoleCodeAndClientId("ADMIN", "client-1");

        assertSame(expected, actual);
        verify(roleMapper).findByRoleCodeAndClientId("ADMIN", "client-1");
    }

    // ----------------------- addRole -----------------------

    @Test
    @DisplayName("addRole 强制覆盖状态为 NORMAL 后入库")
    void addRole_setsNormalStatus() {
        RoleEntity entity = roleEntity(null, "ADMIN", "client-1");
        entity.setStatus(DataStatusEnum.DISABLED);

        roleService.addRole(entity);

        // 不论入参状态是什么，最终都必须为 NORMAL
        assertEquals(DataStatusEnum.NORMAL, entity.getStatus());
        verify(roleMapper).insert(entity);
    }

    @Test
    @DisplayName("addRole 入参已为 NORMAL 时仍能正常入库")
    void addRole_keepsNormalStatus() {
        RoleEntity entity = roleEntity(null, "ADMIN", "client-1");
        entity.setStatus(DataStatusEnum.NORMAL);

        roleService.addRole(entity);

        assertEquals(DataStatusEnum.NORMAL, entity.getStatus());
        verify(roleMapper).insert(entity);
    }

    // ----------------------- updateById -----------------------

    @Test
    @DisplayName("updateById 透传给 mapper")
    void updateById_delegatesToMapper() {
        RoleEntity entity = roleEntity("r-1", "ADMIN", "client-1");

        roleService.updateById(entity);

        verify(roleMapper).updateById(entity);
    }

    // ----------------------- roleMenuConfig 重点回归 -----------------------

    @Test
    @DisplayName("roleMenuConfig 正常路径：先删后插，构造的 RoleMenuEntity 字段完整")
    void roleMenuConfig_normalPath() {
        RoleEntity role = roleEntity("r-1", "ADMIN", "client-1");
        RoleAddMenuReqVO req = new RoleAddMenuReqVO();
        req.setId("r-1");
        req.setMenuIds(Arrays.asList("m-1", "m-2", "m-3"));

        roleService.roleMenuConfig(role, req);

        // 1. 先调用删除（无论后续是否插入都先清旧关联）
        verify(roleMenuMapper).deleteByRoleId("r-1");

        // 2. 再批量插入，断言每个实体字段都被正确填充
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RoleMenuEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(roleMenuMapper).insert(captor.capture());
        List<RoleMenuEntity> inserted = captor.getValue();

        assertEquals(3, inserted.size());
        for (int i = 0; i < inserted.size(); i++) {
            RoleMenuEntity rm = inserted.get(i);
            assertEquals("r-1", rm.getRoleId());
            assertEquals(req.getMenuIds().get(i), rm.getMenuId());
            assertEquals("ADMIN", rm.getRoleCode());
            assertEquals("client-1", rm.getClientId());
        }
    }

    @Test
    @DisplayName("roleMenuConfig menuIds 为空集合时：仅删除，不插入")
    void roleMenuConfig_emptyMenuIds() {
        RoleEntity role = roleEntity("r-1", "ADMIN", "client-1");
        RoleAddMenuReqVO req = new RoleAddMenuReqVO();
        req.setId("r-1");
        req.setMenuIds(Collections.emptyList());

        roleService.roleMenuConfig(role, req);

        verify(roleMenuMapper).deleteByRoleId("r-1");
        verify(roleMenuMapper, never()).insert(anyCollection());
    }

    @Test
    @DisplayName("roleMenuConfig menuIds 为 null 时：仅删除，不抛 NPE")
    void roleMenuConfig_nullMenuIds() {
        RoleEntity role = roleEntity("r-1", "ADMIN", "client-1");
        RoleAddMenuReqVO req = new RoleAddMenuReqVO();
        req.setId("r-1");
        // menuIds = null
        req.setMenuIds(null);

        roleService.roleMenuConfig(role, req);

        verify(roleMenuMapper).deleteByRoleId("r-1");
        verify(roleMenuMapper, never()).insert(anyCollection());
    }

    @Test
    @DisplayName("roleMenuConfig 单菜单场景：删除+插入1条")
    void roleMenuConfig_singleMenu() {
        RoleEntity role = roleEntity("r-1", "ADMIN", "client-1");
        RoleAddMenuReqVO req = new RoleAddMenuReqVO();
        req.setId("r-1");
        req.setMenuIds(Collections.singletonList("m-1"));

        roleService.roleMenuConfig(role, req);

        verify(roleMenuMapper).deleteByRoleId("r-1");
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RoleMenuEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(roleMenuMapper).insert(captor.capture());
        List<RoleMenuEntity> inserted = captor.getValue();
        assertEquals(1, inserted.size());
        assertEquals("r-1", inserted.get(0).getRoleId());
        assertEquals("m-1", inserted.get(0).getMenuId());
    }

    // ----------------------- findByAccountIdNoTenant -----------------------

    @Test
    @DisplayName("findByAccountIdNoTenant 始终以 NORMAL 状态调用 mapper")
    void findByAccountIdNoTenant_usesNormalStatus() {
        when(roleAccountMapper.findByAccountId("acc-1", DataStatusEnum.NORMAL))
                .thenReturn(Collections.emptyList());

        roleService.findByAccountIdNoTenant("acc-1");

        // 关键回归：状态常量必须固定为 NORMAL，不能用入参替代
        verify(roleAccountMapper).findByAccountId(eq("acc-1"), eq(DataStatusEnum.NORMAL));
    }

    @Test
    @DisplayName("findByAccountIdNoTenant 透传返回的角色列表")
    void findByAccountIdNoTenant_returnsMapperResult() {
        List<RoleEntity> expected = Arrays.asList(
                roleEntity("r-1", "ADMIN", "c1"),
                roleEntity("r-2", "USER", "c1"));
        when(roleAccountMapper.findByAccountId("acc-1", DataStatusEnum.NORMAL)).thenReturn(expected);

        List<RoleEntity> actual = roleService.findByAccountIdNoTenant("acc-1");

        assertSame(expected, actual);
    }

    // ----------------------- findAccountsByRoleId -----------------------

    @Test
    @DisplayName("findAccountsByRoleId 透传给 mapper")
    void findAccountsByRoleId_delegatesToMapper() {
        List<String> expected = Arrays.asList("a-1", "a-2");
        when(roleAccountMapper.findAccountsByRoleId("r-1")).thenReturn(expected);

        List<String> actual = roleService.findAccountsByRoleId("r-1");

        assertSame(expected, actual);
        verify(roleAccountMapper).findAccountsByRoleId("r-1");
    }

    @Test
    @DisplayName("findAccountsByRoleId 未配置账户时返回空列表")
    void findAccountsByRoleId_empty() {
        when(roleAccountMapper.findAccountsByRoleId("r-1")).thenReturn(Collections.emptyList());

        List<String> actual = roleService.findAccountsByRoleId("r-1");

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    // ----------------------- 事务注解 -----------------------

    @Test
    @DisplayName("roleMenuConfig 标注 @Transactional(rollbackFor = RuntimeException.class)")
    void roleMenuConfig_transactionalAnnotationPresent() throws NoSuchMethodException {
        Method method = RoleServiceImpl.class.getDeclaredMethod(
                "roleMenuConfig", RoleEntity.class, RoleAddMenuReqVO.class);
        assertNotNull(method.getAnnotation(Transactional.class),
                "roleMenuConfig 必须标注 @Transactional（删除+插入跨表写，需要原子性）");
    }

    // ----------------------- 边界/异常 -----------------------

    @Test
    @DisplayName("roleMenuConfig 即便 roleEntity 缺少 roleCode/clientId 也不会 NPE")
    void roleMenuConfig_blankRoleFields_insertWithBlanks() {
        // 故意构造一个 roleCode/clientId 为空的角色，验证不抛 NPE
        RoleEntity role = new RoleEntity();
        role.setId("r-1");
        // roleCode/clientId 不设置
        RoleAddMenuReqVO req = new RoleAddMenuReqVO();
        req.setId("r-1");
        req.setMenuIds(Collections.singletonList("m-1"));

        roleService.roleMenuConfig(role, req);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RoleMenuEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(roleMenuMapper).insert(captor.capture());
        List<RoleMenuEntity> inserted = captor.getValue();
        assertEquals(1, inserted.size());
        // roleCode/clientId 为 null 不会导致 NPE
        assertEquals(null, inserted.get(0).getRoleCode());
        assertEquals(null, inserted.get(0).getClientId());
    }
}
