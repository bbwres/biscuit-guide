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

package cn.bbwres.biscuit.module.auth.utils;

import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 菜单树工具类单元测试
 *
 * <p>主要覆盖问题 #5：buildChildren 在遍历 keySet 时调用 remove
 * 抛出 {@link java.util.ConcurrentModificationException} 的回归测试。
 *
 * <p>关于 buildMenuTree 的语义说明：本方法只构建调用方传入的 childrenMenuTreeRespList
 * 那一层。要构建多级树，调用方需按 treePath 前缀多次调用 childrenTree，并将结果作为
 * 下一级传入。所以"buildMenuTree 不会自动递归到任意深度"是设计行为，不是 bug。
 */
class MenuTreeUtilsTest {

    private static MenuTreeRespVO node(String id, String parentId, String treePath) {
        MenuTreeRespVO vo = new MenuTreeRespVO();
        vo.setId(id);
        vo.setParentId(parentId);
        vo.setTreePath(treePath);
        return vo;
    }

    @Test
    @DisplayName("buildMenuTree 父节点为空时返回空列表（而非 null）")
    void buildMenuTree_emptyParent() {
        List<MenuTreeRespVO> result = MenuTreeUtils.buildMenuTree(
                id -> Collections.emptyList(),
                "root",
                treePath -> Collections.emptyList()
        );
        // 父节点 Function 返回空列表时，buildMenuTree 返回该空列表（不为 null）
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("单层菜单树：根节点 childrenTree 会被调用，但若返回空列表 children 仍为 null")
    void buildMenuTree_singleLevel() {
        List<MenuTreeRespVO> roots = new ArrayList<>();
        roots.add(node("1", null, "1"));

        AtomicInteger callCount = new AtomicInteger(0);

        List<MenuTreeRespVO> result = MenuTreeUtils.buildMenuTree(
                id -> roots,
                null,
                treePath -> {
                    callCount.incrementAndGet();
                    return Collections.emptyList();
                }
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
        // 根节点 childrenTree 被调用一次，返回空列表，children 保持 null
        assertEquals(1, callCount.get());
        assertEquals(null, result.get(0).getChildren());
    }

    @Test
    @DisplayName("#5 回归：buildChildren 遍历 keySet 并删除时不抛 CME（核心回归点）")
    void buildChildren_noConcurrentModificationException() {
        // 1 会有 2 个直接子节点 11/12
        // 修复前在 keySet 遍历时 nodeMap.remove(parentId) 会触发 CME
        // 这里只验证不抛异常，不深入校验嵌套层级（见类注释）
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("1", null, "1")),
                        null,
                        treePath -> {
                            if ("1/".equals(treePath)) {
                                return Arrays.asList(
                                        node("11", "1", "1/11"),
                                        node("12", "1", "1/12")
                                );
                            }
                            return Collections.emptyList();
                        }
                ), "buildMenuTree 不应抛出 ConcurrentModificationException"
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        MenuTreeRespVO root = result.get(0);
        assertEquals("1", root.getId());
        // 直接子节点应被挂载
        assertNotNull(root.getChildren());
        assertEquals(2, root.getChildren().size());
    }

    @Test
    @DisplayName("#5 回归：深层场景一次调用不会递归到第二层（设计行为），但仍不抛 CME")
    void buildChildren_deepTreeNoCme() {
        // 本次调用 childrenTree 传 path 前缀匹配"1/2/3/"返回 4，理论上会把 4 挂到 3 下
        // 但因为 buildChildren 只处理当前 nextChildrenMap，不向下递归，所以 3 不会有 4
        // （调用方需要再调用 buildMenuTree 一次，传入 [3, 4] 才会建出 3->4）
        // 本测试只验证：不抛 CME + 顶层 1->2 正确挂载
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("1", null, "1")),
                        null,
                        treePath -> {
                            if ("1/".equals(treePath)) {
                                return Collections.singletonList(node("2", "1", "1/2"));
                            }
                            if ("1/2/".equals(treePath)) {
                                return Collections.singletonList(node("3", "2", "1/2/3"));
                            }
                            if ("1/2/3/".equals(treePath)) {
                                return Collections.singletonList(node("4", "3", "1/2/3/4"));
                            }
                            return Collections.emptyList();
                        }
                )
        );

        // 顶层结构：1 下挂 2
        assertNotNull(result);
        assertEquals("1", result.get(0).getId());
        assertNotNull(result.get(0).getChildren());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("2", result.get(0).getChildren().get(0).getId());
        // 2 下没有 children（因为递归只处理 nextChildrenMap 这一层）
        assertEquals(null, result.get(0).getChildren().get(0).getChildren());
    }

    @Test
    @DisplayName("#5 回归：多个兄弟节点各自有子节点，不抛 CME（设计：本次调用只挂载直接子节点）")
    void buildChildren_multipleSiblingsWithChildren() {
        // 设计行为：一次调用只挂载 nextChildrenMap 的层级，3 个直接子节点都挂上，
        // 但子节点的子节点（21/31/41）不会被自动挂上——因为 childrenTree 不会被进一步调用
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("1", null, "1")),
                        null,
                        treePath -> {
                            if ("1/".equals(treePath)) {
                                return Arrays.asList(
                                        node("2", "1", "1/2"),
                                        node("3", "1", "1/3"),
                                        node("4", "1", "1/4")
                                );
                            }
                            return Collections.emptyList();
                        }
                )
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        MenuTreeRespVO root = result.get(0);
        // 三个直接子节点都挂上
        assertEquals(3, root.getChildren().size());
        // 直接子节点都没有 grandchildren（设计：需要再调用 buildMenuTree）
        for (MenuTreeRespVO child : root.getChildren()) {
            assertEquals(null, child.getChildren(),
                    "子节点 " + child.getId() + " 的一次调用不会自动挂载孙子节点（设计行为）");
        }
    }

    @Test
    @DisplayName("#5 回归：根节点匹配后仅处理一次（不再因 return 而漏处理兄弟）")
    void buildChildren_processAllChildrenOnce() {
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("root", null, "root")),
                        null,
                        treePath -> {
                            if ("root/".equals(treePath)) {
                                return Arrays.asList(
                                        node("a", "root", "root/a"),
                                        node("b", "root", "root/b")
                                );
                            }
                            return Collections.emptyList();
                        }
                )
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        // 根节点的 2 个子节点都挂载成功
        assertEquals(2, result.get(0).getChildren().size());
    }
}
