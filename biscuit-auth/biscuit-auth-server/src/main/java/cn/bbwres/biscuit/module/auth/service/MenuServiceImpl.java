package cn.bbwres.biscuit.module.auth.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.convert.MenuConvert;
import cn.bbwres.biscuit.module.auth.dao.MenuApiMapper;
import cn.bbwres.biscuit.module.auth.dao.MenuMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleMenuMapper;
import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.utils.MenuTreeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-10-25
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    private final RoleMenuMapper roleMenuMapper;

    private final MenuApiMapper menuApiMapper;


    /**
     * 获得菜单权限表
     *
     * @param id 编号
     * @return 菜单权限表
     */
    @Override
    public MenuEntity getMenu(String id) {
        return menuMapper.selectById(id);
    }

    /**
     * 获得菜单权限表列表
     *
     * @param ids 编号
     * @return 菜单权限表列表
     */
    @Override
    public List<MenuEntity> getMenuList(Collection<String> ids) {
        return menuMapper.selectByIds(ids);
    }

    /**
     * 获得菜单权限表分页
     *
     * @param pageReqVO 分页查询
     * @return 菜单权限表分页
     */
    @Override
    public Page<MenuEntity, MenuPageReqVO> getMenuPage(Page<MenuEntity, MenuPageReqVO> pageReqVO) {
        return menuMapper.selectPage(pageReqVO);
    }

    /**
     * 新增菜单
     *
     * <p>ID 由 MyBatis-Plus {@code IdType.ASSIGN_ID} 雪花算法生成，{@code insert} 前 id 为 null
     * 会自动填充。修复说明：原实现使用 {@code select max(id)+1} 存在并发冲突，已删除。
     *
     * <p>本方法包含两次写操作：{@code insert} 创建菜单，再根据雪花算法生成的真实 id
     * 校正 {@code treePath}（{@code updateTreePathByParentId}）。为保证原子性，
     * 必须包裹在事务中，否则在校正失败时会出现"菜单已创建但 treePath 为占位符"的不一致状态。
     *
     * @param menuEntity menuEntity
     * @param parentMenu parentMenu 父级信息
     * @param apiList    关联的接口列表（可空）
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void addMenu(MenuEntity menuEntity, MenuEntity parentMenu, List<MenuApiEntity> apiList) {
        // 重置 id 为 null，由 MyBatis-Plus 雪花算法自动填充，避免并发 max(id)+1 冲突
        menuEntity.setId(null);
        menuEntity.setStatus(DataStatusEnum.NORMAL);
        // 临时保存一个占位 id 用于拼 treePath，insert 后回写正确路径
        String pendingId = Long.toString(System.currentTimeMillis());
        menuEntity.setTreePath(ObjectUtils.isEmpty(parentMenu) ? pendingId : parentMenu.getTreePath() + "/" + pendingId);
        menuMapper.insert(menuEntity);
        // 重新计算 treePath：以真实 id 替换占位符
        String realId = menuEntity.getId();
        String realTreePath = ObjectUtils.isEmpty(parentMenu) ? realId : parentMenu.getTreePath() + "/" + realId;
        menuMapper.updateTreePathByParentId(menuEntity.getId(), menuEntity.getTreePath(), realTreePath);
        // 写入菜单关联的接口数据
        saveMenuApiList(realId, apiList);
    }

    /**
     * 修改数据
     *
     * @param oldEntity    原菜单
     * @param updateEntity 修改后的菜单
     * @param parentMenu   父级菜单
     * @param apiList      关联的接口列表（全量替换，可空表示清空）
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void editMenu(MenuEntity oldEntity, MenuEntity updateEntity, MenuEntity parentMenu, List<MenuApiEntity> apiList) {
        oldEntity.setName(Objects.isNull(updateEntity.getName()) ? oldEntity.getName() : updateEntity.getName());
        oldEntity.setMenuSort(Objects.isNull(updateEntity.getMenuSort()) ? oldEntity.getMenuSort() : updateEntity.getMenuSort());
        oldEntity.setMenuType(Objects.isNull(updateEntity.getMenuType()) ? oldEntity.getMenuType() : updateEntity.getMenuType());
        oldEntity.setIcon(Objects.isNull(updateEntity.getIcon()) ? oldEntity.getIcon() : updateEntity.getIcon());
        oldEntity.setComponent(Objects.isNull(updateEntity.getComponent()) ? oldEntity.getComponent() : updateEntity.getComponent());
        oldEntity.setComponentName(Objects.isNull(updateEntity.getComponentName()) ? oldEntity.getComponentName() : updateEntity.getComponentName());
        oldEntity.setVisible(Objects.isNull(updateEntity.getVisible()) ? oldEntity.getVisible() : updateEntity.getVisible());
        oldEntity.setKeepAlive(Objects.isNull(updateEntity.getKeepAlive()) ? oldEntity.getKeepAlive() : updateEntity.getKeepAlive());
        oldEntity.setAlwaysShow(Objects.isNull(updateEntity.getAlwaysShow()) ? oldEntity.getAlwaysShow() : updateEntity.getAlwaysShow());

        // 全量替换 menuApiList：先删后插
        replaceMenuApiList(oldEntity.getId(), apiList);

        if (checkNoChangeParent(oldEntity, updateEntity)) {
            //没有修改层级
            log.info("当前用户修改了菜单信息:[{}]为:[{}]，未修改层级，则不处理层级关系", oldEntity, updateEntity);
            menuMapper.updateById(oldEntity);
            return;
        }
        // 修改了层级：先做自循环校验，避免形成 treePath 死循环
        validateParentNotInSubtree(oldEntity, updateEntity, parentMenu);

        //修改了层级
        String oldTreePath = oldEntity.getTreePath();
        oldEntity.setParentId(updateEntity.getParentId());
        oldEntity.setTreePath(ObjectUtils.isEmpty(parentMenu) ? oldEntity.getId() : parentMenu.getTreePath() + "/" + oldEntity.getId());

        menuMapper.updateById(oldEntity);
        if (ObjectUtils.isEmpty(oldEntity.getParentId())) {
            menuMapper.updateParentIdById(oldEntity.getId(), oldEntity.getParentId());
        }
        menuMapper.updateTreePathByParentId(oldEntity.getId(), oldTreePath, oldEntity.getTreePath());

    }

    /**
     * 修改菜单状态
     *
     * @param entity
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void editMenuStatus(MenuEntity entity) {
        String treePath = entity.getTreePath();
        menuMapper.updateStatusById(entity.getId(), entity.getStatus());
        //修改子级的数据状态
        menuMapper.updateStatusByParentTreePath(treePath, entity.getStatus());

    }

    /**
     * 根据菜单id获取出整个树形结构（含每个节点的 menuApiList）
     *
     * @param entityId
     * @return
     */
    @Override
    public List<MenuTreeRespVO> getMenuTreeById(String entityId) {
        List<MenuTreeRespVO> tree = MenuTreeUtils.buildMenuTree(id ->
                        menuMapper.getMenuTreeByIdAndStatus(id, DataStatusEnum.NORMAL), entityId,
                treePath -> menuMapper.getMenuTreeByTreePathAndStatus(treePath, DataStatusEnum.NORMAL));
        // 一次性查询所有节点的接口列表，避免 N+1
        List<String> menuIds = new ArrayList<>();
        collectMenuIds(tree, menuIds);
        if (!menuIds.isEmpty()) {
            List<MenuApiEntity> apiList = menuApiMapper.findByMenuIds(menuIds);
            Map<String, List<MenuApiEntity>> apiByMenu = apiList.stream()
                    .collect(Collectors.groupingBy(MenuApiEntity::getMenuId));
            attachMenuApiList(tree, apiByMenu);
        }
        return tree;
    }

    /**
     * 递归收集所有节点的 id
     */
    private static void collectMenuIds(List<MenuTreeRespVO> tree, List<String> out) {
        if (CollectionUtils.isEmpty(tree)) {
            return;
        }
        for (MenuTreeRespVO node : tree) {
            if (node == null) continue;
            out.add(node.getId());
            collectMenuIds(node.getChildren(), out);
        }
    }

    /**
     * 递归为每个节点挂载关联的接口列表
     */
    private static void attachMenuApiList(List<MenuTreeRespVO> tree, Map<String, List<MenuApiEntity>> apiByMenu) {
        if (CollectionUtils.isEmpty(tree)) {
            return;
        }
        for (MenuTreeRespVO node : tree) {
            if (node == null) continue;
            List<MenuApiEntity> apis = apiByMenu.get(node.getId());
            node.setMenuApiList(MenuConvert.INSTANCE.convertApiList(apis));
            attachMenuApiList(node.getChildren(), apiByMenu);
        }
    }

    /**
     * 根据角色id查询出关联的菜单信息
     *
     * @param roleId
     * @return
     */
    @Override
    public List<MenuEntity> findByRoleId(String roleId) {
        return roleMenuMapper.findByRoleIdNoTenant(roleId, DataStatusEnum.NORMAL);
    }

    /**
     * 根据角色id查询出关联菜单的所有接口（用于资源鉴权）
     *
     * @param roleId 角色id
     * @return 接口列表
     */
    @Override
    public List<MenuApiEntity> findApisByRoleId(String roleId) {
        return roleMenuMapper.findApisByRoleIdNoTenant(roleId, DataStatusEnum.NORMAL);
    }

    /**
     * 根据菜单id查询出 关联的角色id
     *
     * @param menuId
     * @return
     */
    @Override
    public List<String> findRolesByMenuId(String menuId) {
        return roleMenuMapper.findRolesByMenuId(menuId);
    }

    /**
     * 删除菜单（需校验是否有子菜单）
     *
     * @param id 菜单id
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteMenu(String id) {
        long childCount = menuMapper.countByParentId(id);
        if (childCount > 0) {
            throw new SystemRuntimeException(AuthErrorCodeConstants.MENU_HAS_CHILDREN_ERROR);
        }
        menuMapper.deleteById(id);
        // 删除角色-菜单关联数据
        roleMenuMapper.deleteByMenuId(id);
        // 删除菜单-接口关联数据
        menuApiMapper.deleteByMenuId(id);
    }

    /**
     * 根据菜单 id 查询关联的接口列表
     *
     * @param menuId 菜单 id
     * @return 接口列表
     */
    @Override
    public List<MenuApiEntity> findApiListByMenuId(String menuId) {
        if (ObjectUtils.isEmpty(menuId)) {
            return Collections.emptyList();
        }
        return menuApiMapper.findByMenuId(menuId);
    }

    /**
     * 批量根据菜单 id 查询关联的接口列表
     *
     * @param menuIds 菜单 id 集合
     * @return 接口列表
     */
    @Override
    public List<MenuApiEntity> findApiListByMenuIds(Collection<String> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }
        return menuApiMapper.findByMenuIds(menuIds);
    }


    /**
     * 写入菜单关联接口（仅用于新增场景）
     *
     * @param menuId  菜单 id
     * @param apiList 接口列表
     */
    private void saveMenuApiList(String menuId, List<MenuApiEntity> apiList) {
        if (CollectionUtils.isEmpty(apiList)) {
            return;
        }
        for (MenuApiEntity api : apiList) {
            if (api == null || ObjectUtils.isEmpty(api.getApiUrl())) {
                continue;
            }
            api.setId(null);
            api.setMenuId(menuId);
            menuApiMapper.insert(api);
        }
    }

    /**
     * 全量替换菜单关联接口：先删后插
     *
     * @param menuId  菜单 id
     * @param apiList 新的接口列表（空表示清空）
     */
    private void replaceMenuApiList(String menuId, List<MenuApiEntity> apiList) {
        menuApiMapper.deleteByMenuId(menuId);
        saveMenuApiList(menuId, apiList);
    }


    /**
     * 判断是否修改父节点
     *
     * @param oldEntity
     * @param updateEntity
     * @return
     */
    private static boolean checkNoChangeParent(MenuEntity oldEntity, MenuEntity updateEntity) {
        if (oldEntity.getParentId() == null && updateEntity.getParentId() == null) {
            return true;
        }
        if (oldEntity.getParentId() == null) {
            return false;
        }
        if (updateEntity.getParentId() == null) {
            return false;
        }
        return oldEntity.getParentId().equals(updateEntity.getParentId());

    }

    /**
     * 校验修改父节点时不能形成自循环
     * <ul>
     *     <li>不能将菜单的父节点设置为自己</li>
     *     <li>不能将菜单的父节点设置为自己的子孙节点（避免 treePath 形成环）</li>
     * </ul>
     */
    private void validateParentNotInSubtree(MenuEntity oldEntity, MenuEntity updateEntity, MenuEntity parentMenu) {
        // 1. 不能将父节点设置为自己
        if (Objects.equals(oldEntity.getId(), updateEntity.getParentId())) {
            throw new SystemRuntimeException(AuthErrorCodeConstants.MENU_PARENT_SELF_ERROR);
        }
        // 2. 不能将父节点设置为自己的子孙节点
        //    判定方法：目标 parentMenu 的 treePath 以当前菜单的 treePath 开头
        //    （注意：分隔符必须是 /，避免前缀误匹配，例如 1/10 vs 1/1）
        if (parentMenu != null && oldEntity.getTreePath() != null && parentMenu.getTreePath() != null) {
            String currentTreePath = oldEntity.getTreePath();
            String targetParentTreePath = parentMenu.getTreePath();
            if (targetParentTreePath.equals(currentTreePath) || targetParentTreePath.startsWith(currentTreePath + "/")) {
                throw new SystemRuntimeException(AuthErrorCodeConstants.MENU_PARENT_IN_SUBTREE_ERROR);
            }
        }
    }


}
