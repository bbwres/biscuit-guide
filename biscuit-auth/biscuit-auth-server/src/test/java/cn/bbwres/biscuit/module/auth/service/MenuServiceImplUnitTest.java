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

import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.dao.MenuApiMapper;
import cn.bbwres.biscuit.module.auth.dao.MenuMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleMenuMapper;
import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * MenuServiceImpl 单元测试（Mockito 隔离环境，不依赖 Spring）
 *
 * <p>覆盖修复点：
 * <ul>
 *     <li>#4 移动菜单只更新直接子节点（验证 SQL 调用参数正确）</li>
 *     <li>#7 菜单编辑缺"不能移动到自身/子孙"校验</li>
 *     <li>#8 菜单 ID max(id)+1 并发风险（验证已切换为雪花 ID）</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MenuServiceImplUnitTest {

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private RoleMenuMapper roleMenuMapper;

    @Mock
    private MenuApiMapper menuApiMapper;

    @InjectMocks
    private MenuServiceImpl menuService;

    /** 空接口列表，避免重复 new */
    private static final List<MenuApiEntity> NO_APIS = Collections.emptyList();

    private static MenuEntity menuEntity(String id, String parentId, String treePath) {
        MenuEntity e = new MenuEntity();
        e.setId(id);
        e.setParentId(parentId);
        e.setTreePath(treePath);
        e.setStatus(DataStatusEnum.NORMAL);
        return e;
    }

    // ----------------------- #8 雪花 ID -----------------------

    @Test
    @DisplayName("#8 addMenu 不再调用 max(id)+1，ID 由雪花算法生成")
    void addMenu_usesSnowflakeId() {
        // 模拟 addMenu: insert 后回填 id（模拟 MyBatis-Plus 雪花算法）
        doAnswer(invocation -> {
            MenuEntity entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId("snowflake-" + System.nanoTime());
            }
            return 1;
        }).when(menuMapper).insert(any(MenuEntity.class));

        MenuEntity newMenu = new MenuEntity();
        newMenu.setName("测试菜单");

        menuService.addMenu(newMenu, null, NO_APIS);

        ArgumentCaptor<MenuEntity> captor = ArgumentCaptor.forClass(MenuEntity.class);
        verify(menuMapper).insert(captor.capture());
        MenuEntity inserted = captor.getValue();

        // 验证雪花 ID 已自动填充
        assertNotNull(inserted.getId(), "ID 应由雪花算法自动填充");
        // 验证 findMaxId 不再被调用
        verify(menuMapper, never()).findMaxId();
    }

    @Test
    @DisplayName("#8 addMenu 在有父级时使用父级 treePath 拼装")
    void addMenu_withParent_buildsTreePath() {
        doAnswer(invocation -> {
            MenuEntity entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId("snowflake-xyz");
            }
            return 1;
        }).when(menuMapper).insert(any(MenuEntity.class));

        MenuEntity parent = menuEntity("parent-1", null, "parent-1");
        MenuEntity newMenu = new MenuEntity();
        newMenu.setName("子菜单");

        menuService.addMenu(newMenu, parent, NO_APIS);

        ArgumentCaptor<MenuEntity> captor = ArgumentCaptor.forClass(MenuEntity.class);
        verify(menuMapper).insert(captor.capture());
        MenuEntity inserted = captor.getValue();

        assertNotNull(inserted.getId());
        // treePath 在 insert 前是占位符 + parent 路径
        assertTrue(inserted.getTreePath().startsWith("parent-1/"),
                "treePath 应以父级路径开头: " + inserted.getTreePath());
    }

    // ----------------------- #7 自循环校验 -----------------------

    @Test
    @DisplayName("#7 editMenu 不能将父节点设置为自己")
    void editMenu_parentIsSelf_throws() {
        MenuEntity oldEntity = menuEntity("1", "2", "2/1");
        MenuEntity updateEntity = menuEntity("1", "1", "1");

        SystemRuntimeException ex = assertThrows(SystemRuntimeException.class,
                () -> menuService.editMenu(oldEntity, updateEntity, null, NO_APIS));

        assertEquals(AuthErrorCodeConstants.MENU_PARENT_SELF_ERROR.getCode(), ex.getErrorCode());
        verify(menuMapper, never()).updateById(any(MenuEntity.class));
    }

    @Test
    @DisplayName("#7 editMenu 不能将父节点设置为自身直接子节点")
    void editMenu_parentInDirectSubtree_throws() {
        MenuEntity oldEntity = menuEntity("1", null, "1");
        MenuEntity updateEntity = menuEntity("1", "11", "11/1");
        MenuEntity targetParent = menuEntity("11", "1", "1/11");

        SystemRuntimeException ex = assertThrows(SystemRuntimeException.class,
                () -> menuService.editMenu(oldEntity, updateEntity, targetParent, NO_APIS));

        assertEquals(AuthErrorCodeConstants.MENU_PARENT_IN_SUBTREE_ERROR.getCode(), ex.getErrorCode());
        verify(menuMapper, never()).updateById(any(MenuEntity.class));
    }

    @Test
    @DisplayName("#7 editMenu 不能将父节点设置为自身孙子节点（多级）")
    void editMenu_parentInGrandchildSubtree_throws() {
        MenuEntity oldEntity = menuEntity("1", null, "1");
        MenuEntity updateEntity = menuEntity("1", "111", "111");
        MenuEntity targetParent = menuEntity("111", "11", "1/11/111");

        SystemRuntimeException ex = assertThrows(SystemRuntimeException.class,
                () -> menuService.editMenu(oldEntity, updateEntity, targetParent, NO_APIS));

        assertEquals(AuthErrorCodeConstants.MENU_PARENT_IN_SUBTREE_ERROR.getCode(), ex.getErrorCode());
    }

    @Test
    @DisplayName("#7 editMenu 跨级移动到其他分支合法")
    void editMenu_moveToOtherBranch_ok() {
        MenuEntity oldEntity = menuEntity("1", null, "1");
        MenuEntity updateEntity = menuEntity("1", "5", "10/5");
        MenuEntity targetParent = menuEntity("5", "10", "10/5");

        menuService.editMenu(oldEntity, updateEntity, targetParent, NO_APIS);
        verify(menuMapper).updateById(any(MenuEntity.class));
    }

    @Test
    @DisplayName("#7 editMenu 前缀相似但不冲突：treePath=1/10 不会被误判为 1/1 的子孙")
    void editMenu_similarPrefixNotMatched_ok() {
        MenuEntity oldEntity = menuEntity("1", null, "1");
        MenuEntity updateEntity = menuEntity("1", "10", "10");
        MenuEntity targetParent = menuEntity("10", null, "10");

        menuService.editMenu(oldEntity, updateEntity, targetParent, NO_APIS);
        verify(menuMapper).updateById(any(MenuEntity.class));
    }

    @Test
    @DisplayName("#7 editMenu 未修改 parentId 时不进入校验逻辑")
    void editMenu_parentUnchanged_skipsValidation() {
        MenuEntity oldEntity = menuEntity("1", "5", "5/1");
        MenuEntity updateEntity = menuEntity("1", "5", "5/1");
        updateEntity.setName("新名字");
        MenuEntity targetParent = menuEntity("5", null, "5");

        menuService.editMenu(oldEntity, updateEntity, targetParent, NO_APIS);
        verify(menuMapper).updateById(any(MenuEntity.class));
    }

    // ----------------------- #4 移动菜单只更新直接子节点 -----------------------

    @Test
    @DisplayName("#4 editMenu 修改层级时调用 updateTreePathByParentId 传旧/新 treePath")
    void editMenu_changeLevel_callsTreePathUpdate() {
        MenuEntity oldEntity = menuEntity("1", null, "1");
        MenuEntity updateEntity = menuEntity("1", "5", "5/1");
        MenuEntity targetParent = menuEntity("5", null, "5");

        menuService.editMenu(oldEntity, updateEntity, targetParent, NO_APIS);

        ArgumentCaptor<String> oldPathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newPathCaptor = ArgumentCaptor.forClass(String.class);
        verify(menuMapper).updateTreePathByParentId(anyString(), oldPathCaptor.capture(), newPathCaptor.capture());
        assertEquals("1", oldPathCaptor.getValue());
        assertEquals("5/1", newPathCaptor.getValue());
    }

    @Test
    @DisplayName("#4 修复后 updateTreePathByParentId 入参包含完整旧/新 treePath")
    void updateTreePathByParentId_passesTreePaths() {
        // 验证：parentId、oldTreePath、newTreePath 三个参数都被正确传递
        // SQL 内部已切换为 LIKE CONCAT(oldTreePath, '/%') 前缀匹配
        menuService.editMenu(
                menuEntity("1", null, "1"),
                menuEntity("1", "5", "5/1"),
                menuEntity("5", null, "5"),
                NO_APIS
        );

        verify(menuMapper).updateTreePathByParentId(
                eq("1"),   // parentId（保留参数兼容）
                eq("1"),   // oldTreePath
                eq("5/1")  // newTreePath
        );
    }

    // ----------------------- 事务注解 -----------------------

    @Test
    @DisplayName("addMenu / editMenu / editMenuStatus 都带 @Transactional 注解")
    void transactionalAnnotationsPresent() throws NoSuchMethodException {
        // addMenu 包含两次写操作（insert + updateTreePathByParentId），必须事务化保证原子性
        Method addMenu = MenuServiceImpl.class.getDeclaredMethod("addMenu",
                MenuEntity.class, MenuEntity.class, List.class);
        assertNotNull(addMenu.getAnnotation(Transactional.class),
                "addMenu 必须标注 @Transactional，否则 insert 与 treePath 校正会失去原子性");

        Method editMenu = MenuServiceImpl.class.getDeclaredMethod("editMenu",
                MenuEntity.class, MenuEntity.class, MenuEntity.class, List.class);
        assertNotNull(editMenu.getAnnotation(Transactional.class),
                "editMenu 应保持 @Transactional");

        Method editMenuStatus = MenuServiceImpl.class.getDeclaredMethod("editMenuStatus",
                MenuEntity.class);
        assertNotNull(editMenuStatus.getAnnotation(Transactional.class),
                "editMenuStatus 应保持 @Transactional");
    }
}
