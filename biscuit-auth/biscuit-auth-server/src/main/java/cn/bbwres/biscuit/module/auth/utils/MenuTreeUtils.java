package cn.bbwres.biscuit.module.auth.utils;

import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
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
     * 为当前节点递归挂载子节点
     *
     * @param currentNode 当前节点
     * @param nodeMap     所有节点的ID映射
     */
    private static void buildChildren(MenuTreeRespVO currentNode, Map<String, List<MenuTreeRespVO>> nodeMap) {
        for (String parentId : nodeMap.keySet()) {
            if (currentNode.getId().equals(parentId)) {
                List<MenuTreeRespVO> menuTreeResp = nodeMap.get(parentId);
                currentNode.setChildren(menuTreeResp);
                nodeMap.remove(parentId);
                for (MenuTreeRespVO menuTree : menuTreeResp) {
                    buildChildren(menuTree, nodeMap);
                }
            }

        }
    }


}
