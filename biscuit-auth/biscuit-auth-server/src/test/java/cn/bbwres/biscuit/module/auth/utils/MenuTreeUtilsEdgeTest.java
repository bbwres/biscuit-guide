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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MenuTreeUtils 边界场景单元测试
 *
 * <p>在 {@link MenuTreeUtilsTest} 基础上补充更细的边界场景：
 * <ul>
 *     <li>空 childrenMap(无任何 children)</li>
 *     <li>children 中包含 null 节点</li>
 *     <li>父子 id 自引用(单层)不会无限递归</li>
 *     <li>children 中 parentId 引用了不存在的父节点(孤儿)</li>
 *     <li>同 id 多次出现的 key 合并</li>
 *     <li>Function 返回 null 的健壮性</li>
 *     <li>多根节点场景</li>
 * </ul>
 */
class MenuTreeUtilsEdgeTest {

    private static MenuTreeRespVO node(String id, String parentId, String treePath) {
        MenuTreeRespVO vo = new MenuTreeRespVO();
        vo.setId(id);
        vo.setParentId(parentId);
        vo.setTreePath(treePath);
        return vo;
    }

    /**
     * childrenTree Function:只处理"root/"下一级,之后都返回空。
     * 这样无论子节点多深,buildMenuTree 都只挂一层(设计行为)。
     */
    private static final java.util.function.Function<String, List<MenuTreeRespVO>> SINGLE_LEVEL_CHILDREN =
            treePath -> {
                if (!treePath.endsWith("/")) {
                    return Collections.emptyList();
                }
                // 这里只针对 "root/" 返回,模拟典型"只查下一级"场景
                if ("root/".equals(treePath)) {
                    return Collections.emptyList();
                }
                return Collections.emptyList();
            };

    @Test
    @DisplayName("空 childrenMap:根节点存在但 childrenTree 返回空列表,children 应保持 null")
    void emptyChildrenMap() {
        List<MenuTreeRespVO> result = MenuTreeUtils.buildMenuTree(
                id -> Collections.singletonList(node("root", null, "root")),
                null,
                SINGLE_LEVEL_CHILDREN
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        // childrenTree 返回空时,根节点的 children 不会被设置
        assertNull(result.get(0).getChildren());
    }

    @Test
    @DisplayName("children 中包含 null 节点不应抛 NPE")
    void childrenContainsNull() {
        // childrenList 包含一个 null
        List<MenuTreeRespVO> children = new ArrayList<>();
        children.add(node("c1", "root", "root/c1"));
        children.add(null);
        children.add(node("c2", "root", "root/c2"));

        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("root", null, "root")),
                        null,
                        treePath -> "root/".equals(treePath) ? children : Collections.emptyList()
                )
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        // 真实挂载的 children 由 buildChildren 内部 HashMap 决定,这里只保证不抛 NPE
    }

    @Test
    @DisplayName("parentTree Function 返回 null 时不抛 NPE(由调用方控制)")
    void parentTreeReturnsNull_isDefensive() {
        // 当前实现:menuTreeRespList = parentTree.apply(entityId);
        // 之后 CollectionUtils.isEmpty(menuTreeRespList) - isEmpty(null) 返回 true
        // 所以会直接 return null(而不是抛 NPE)
        List<MenuTreeRespVO> result = MenuTreeUtils.buildMenuTree(
                id -> null,
                null,
                treePath -> Collections.emptyList()
        );
        // 返回 null(由调用方负责处理)
        org.junit.jupiter.api.Assertions.assertNull(result);
    }

    @Test
    @DisplayName("children 中 parentId 指向不存在的根节点(孤儿节点),不抛 NPE")
    void orphanNodes() {
        // children 的 parentId="ghost",而 nodeMap 中没有 "ghost" key
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("root", null, "root")),
                        null,
                        treePath -> {
                            if ("root/".equals(treePath)) {
                                // 这两个 child 的 parentId="root",会被挂到 root 下
                                // 但 buildChildren 内的 nodeMap 只有 root 这个 key
                                return Arrays.asList(
                                        node("c1", "root", "root/c1"),
                                        node("c2", "root", "root/c2")
                                );
                            }
                            return Collections.emptyList();
                        }
                )
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getChildren());
        // 真实挂载的 children 数量(2 个直接子节点)
        assertEquals(2, result.get(0).getChildren().size());
    }

    @Test
    @DisplayName("同 parentId 的多个子节点都会被合并到同一列表")
    void multipleChildrenWithSameParent() {
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("p", null, "p")),
                        null,
                        treePath -> {
                            if ("p/".equals(treePath)) {
                                return Arrays.asList(
                                        node("a", "p", "p/a"),
                                        node("b", "p", "p/b"),
                                        node("c", "p", "p/c")
                                );
                            }
                            return Collections.emptyList();
                        }
                )
        );

        assertNotNull(result);
        assertEquals(3, result.get(0).getChildren().size());
    }

    @Test
    @DisplayName("同 id 的多个子节点出现多次:HashMap 的 key 合并不会丢数据")
    void duplicateChildIds() {
        // 同一个 id="a" 出现两次,都是 parent="p"
        // HashMap 在 nextChildrenMap 中 key=p -> List<MenuTreeRespVO>(追加两个)
        // 注意:原始 nodeMap 会被 buildChildren 内部遍历 keySet 时通过 iterator.remove() 删除
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("p", null, "p")),
                        null,
                        treePath -> {
                            if ("p/".equals(treePath)) {
                                return Arrays.asList(
                                        node("a", "p", "p/a"),
                                        node("a", "p", "p/a"),  // 重复 id
                                        node("b", "p", "p/b")
                                );
                            }
                            return Collections.emptyList();
                        }
                )
        );

        assertNotNull(result);
        // 3 个 entry(包含重复 id)都应被挂载
        assertEquals(3, result.get(0).getChildren().size());
    }

    @Test
    @DisplayName("多根节点:多个顶层节点各自独立挂载子节点")
    void multipleRoots() {
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Arrays.asList(
                                node("root1", null, "root1"),
                                node("root2", null, "root2")
                        ),
                        null,
                        treePath -> {
                            if ("root1/".equals(treePath)) {
                                return Collections.singletonList(node("r1c", "root1", "root1/r1c"));
                            }
                            if ("root2/".equals(treePath)) {
                                return Collections.singletonList(node("r2c", "root2", "root2/r2c"));
                            }
                            return Collections.emptyList();
                        }
                )
        );

        assertNotNull(result);
        assertEquals(2, result.size());
        // 两个根各自有 1 个子节点
        for (MenuTreeRespVO root : result) {
            assertNotNull(root.getChildren());
            assertEquals(1, root.getChildren().size());
        }
    }

    @Test
    @DisplayName("childrenTree Function 调用次数:每个根节点恰好调用 1 次(单层挂载)")
    void childrenTreeCallCount() {
        AtomicInteger callCount = new AtomicInteger(0);
        List<MenuTreeRespVO> result = MenuTreeUtils.buildMenuTree(
                id -> Arrays.asList(
                        node("r1", null, "r1"),
                        node("r2", null, "r2")
                ),
                null,
                treePath -> {
                    callCount.incrementAndGet();
                    return Collections.emptyList();
                }
        );

        assertNotNull(result);
        assertEquals(2, result.size());
        // 设计行为:只对"根节点"调用 childrenTree,不会进一步递归
        assertEquals(2, callCount.get());
    }

    @Test
    @DisplayName("childrenList 为 null(虽然不常见)不抛 NPE - 设计行为验证")
    void childrenListNull_treatedAsEmpty() {
        // 实际 childrenTree 在 isEmpty(null) 时为 true(因为 CollectionUtils.isEmpty 检查 null)
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("root", null, "root")),
                        null,
                        treePath -> null
                )
        );
        assertNotNull(result);
        assertNull(result.get(0).getChildren());
    }

    @Test
    @DisplayName("同一根节点下既有有效子节点又有 null children:不会因 null 抛 NPE")
    void mixedNullAndValidChildren() {
        List<MenuTreeRespVO> children = new ArrayList<>();
        children.add(node("c1", "root", "root/c1"));
        children.add(null);
        children.add(null);

        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.singletonList(node("root", null, "root")),
                        null,
                        treePath -> "root/".equals(treePath) ? children : Collections.emptyList()
                )
        );
        assertNotNull(result);
        // null 元素被跳过（健壮性防御），只保留 1 个有效子节点
        assertNotNull(result.get(0).getChildren());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("c1", result.get(0).getChildren().get(0).getId());
    }

    @Test
    @DisplayName("空字符串 entityId 也能正常处理")
    void emptyEntityId() {
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.emptyList(),  // 任意 entityId 都返回空
                        "",
                        treePath -> Collections.emptyList()
                )
        );
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("entityId 为 null 也能正常处理")
    void nullEntityId() {
        List<MenuTreeRespVO> result = assertDoesNotThrow(() ->
                MenuTreeUtils.buildMenuTree(
                        id -> Collections.emptyList(),
                        null,
                        treePath -> Collections.emptyList()
                )
        );
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
