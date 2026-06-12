package cn.bbwres.biscuit.module.auth.utils;

import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 菜单树的工具类
 *
 * @author zhanglinfeng
 */
public class MenuTreeUtils {


    /**
     * 加载树形节点
     *
     * @param parentTree
     * @param entityId
     * @param childrenTree
     * @return
     */
    public static List<MenuTreeRespVO> buildMenuTree(Function<String, List<MenuTreeRespVO>> parentTree, String entityId,
                                                     Function<String, List<MenuTreeRespVO>> childrenTree) {
        //查询出根节点
        List<MenuTreeRespVO> menuTreeRespList = parentTree.apply(entityId);
        if (CollectionUtils.isEmpty(menuTreeRespList)) {
            return menuTreeRespList;
        }
        //查询所有子级
        for (MenuTreeRespVO menuTreeResp : menuTreeRespList) {
            //只查询下一级
            List<MenuTreeRespVO> childrenMenuTreeRespList = childrenTree.apply(menuTreeResp.getTreePath() + "/");
            if (CollectionUtils.isEmpty(childrenMenuTreeRespList)) {
                continue;
            }
            buildTree(menuTreeResp, childrenMenuTreeRespList);
        }
        return menuTreeRespList;
    }

    /**
     * 加载树形结构
     *
     * @param menuTreeResp
     * @param childrenMenuTreeRespList
     * @return
     */
    private static void buildTree(MenuTreeRespVO menuTreeResp, List<MenuTreeRespVO> childrenMenuTreeRespList) {
        Map<String, List<MenuTreeRespVO>> nextChildrenMap = new HashMap<>(16);
        for (MenuTreeRespVO menuTreeRespVO : childrenMenuTreeRespList) {
            // 防御：跳过 children 列表中的 null 元素，避免下游 NPE
            if (menuTreeRespVO == null) {
                continue;
            }
            List<MenuTreeRespVO> nextChildrenList = nextChildrenMap.get(menuTreeRespVO.getParentId());
            if (CollectionUtils.isEmpty(nextChildrenList)) {
                nextChildrenList = new ArrayList<>(16);
                nextChildrenMap.put(menuTreeRespVO.getParentId(), nextChildrenList);
            }
            nextChildrenList.add(menuTreeRespVO);
        }
        buildChildren(menuTreeResp, nextChildrenMap);
    }


    /**
     * 根据parentId将扁平菜单列表构建为树形结构
     *
     * @param flatList 扁平菜单列表
     * @return 树形结构列表
     */
    public static List<MenuTreeRespVO> buildMenuTreeByParentId(List<MenuTreeRespVO> flatList) {
        if (CollectionUtils.isEmpty(flatList)) {
            return new ArrayList<>();
        }
        Map<String, MenuTreeRespVO> map = new HashMap<>(flatList.size());
        List<MenuTreeRespVO> roots = new ArrayList<>();
        for (MenuTreeRespVO node : flatList) {
            if (node == null) continue;
            node.setChildren(null);
            map.put(node.getId(), node);
        }
        for (MenuTreeRespVO node : flatList) {
            if (node == null) continue;
            String parentId = node.getParentId();
            if (parentId == null || parentId.isEmpty() || "0".equals(parentId) || !map.containsKey(parentId)) {
                roots.add(node);
            } else {
                MenuTreeRespVO parent = map.get(parentId);
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(node);
            }
        }
        return roots;
    }


    /**
     * 为当前节点递归挂载子节点
     * <p>
     * 使用 {@link Iterator#remove()} 替代直接 {@link Map#remove(Object)}，避免
     * 在遍历 keySet 时修改 Map 抛出 {@link java.util.ConcurrentModificationException}。
     *
     * @param currentNode 当前节点
     * @param nodeMap     所有节点的ID映射
     */
    private static void buildChildren(MenuTreeRespVO currentNode, Map<String, List<MenuTreeRespVO>> nodeMap) {
        Iterator<Map.Entry<String, List<MenuTreeRespVO>>> iterator = nodeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MenuTreeRespVO>> entry = iterator.next();
            if (currentNode.getId().equals(entry.getKey())) {
                List<MenuTreeRespVO> menuTreeResp = entry.getValue();
                currentNode.setChildren(menuTreeResp);
                // 通过 Iterator.remove() 删除当前条目，避免 CME
                iterator.remove();
                for (MenuTreeRespVO menuTree : menuTreeResp) {
                    buildChildren(menuTree, nodeMap);
                }
                // 找到匹配项后即可跳出本层递归
                return;
            }
        }
    }


}
