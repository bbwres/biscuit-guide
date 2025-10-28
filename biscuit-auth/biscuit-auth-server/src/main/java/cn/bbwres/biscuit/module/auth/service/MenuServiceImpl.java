package cn.bbwres.biscuit.module.auth.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.dao.MenuMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleMenuMapper;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.utils.MenuTreeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


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
     * @param menuEntity menuEntity
     * @param menuEntity parentMenu 父级信息
     */
    @Override
    public void addMenu(MenuEntity menuEntity, MenuEntity parentMenu) {
        //查询出当前层级最大的id数据
        String id = menuMapper.findMaxId();
        if (ObjectUtils.isEmpty(id)) {
            id = "1000";
        }
        //查询父级数据是否存在
        menuEntity.setId((NumberUtils.parseNumber(id, Long.class) + 1L) + "");
        menuEntity.setStatus(DataStatusEnum.NORMAL);
        menuEntity.setTreePath(ObjectUtils.isEmpty(parentMenu) ? menuEntity.getId() : parentMenu.getTreePath() + "/" + menuEntity.getId());
        menuMapper.insert(menuEntity);


    }

    /**
     * 修改数据
     *
     * @param oldEntity
     * @param updateEntity
     * @param parentMenu
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void editMenu(MenuEntity oldEntity, MenuEntity updateEntity, MenuEntity parentMenu) {
        oldEntity.setName(Objects.isNull(updateEntity.getName()) ? oldEntity.getName() : updateEntity.getName());
        oldEntity.setMenuSort(Objects.isNull(updateEntity.getMenuSort()) ? oldEntity.getMenuSort() : updateEntity.getMenuSort());
        oldEntity.setMenuType(Objects.isNull(updateEntity.getMenuType()) ? oldEntity.getMenuType() : updateEntity.getMenuType());
        oldEntity.setIcon(Objects.isNull(updateEntity.getIcon()) ? oldEntity.getIcon() : updateEntity.getIcon());
        oldEntity.setComponent(Objects.isNull(updateEntity.getComponent()) ? oldEntity.getComponent() : updateEntity.getComponent());
        oldEntity.setComponentName(Objects.isNull(updateEntity.getComponentName()) ? oldEntity.getComponentName() : updateEntity.getComponentName());
        oldEntity.setVisible(Objects.isNull(updateEntity.getVisible()) ? oldEntity.getVisible() : updateEntity.getVisible());
        oldEntity.setKeepAlive(Objects.isNull(updateEntity.getKeepAlive()) ? oldEntity.getKeepAlive() : updateEntity.getKeepAlive());
        oldEntity.setAlwaysShow(Objects.isNull(updateEntity.getAlwaysShow()) ? oldEntity.getAlwaysShow() : updateEntity.getAlwaysShow());
        oldEntity.setApiUrlMethod(Objects.isNull(updateEntity.getApiUrlMethod()) ? oldEntity.getApiUrlMethod() : updateEntity.getApiUrlMethod());
        oldEntity.setApiUrl(Objects.isNull(updateEntity.getApiUrl()) ? oldEntity.getApiUrl() : updateEntity.getApiUrl());

        if (checkNoChangeParent(oldEntity, updateEntity)) {
            //没有修改层级
            log.info("当前用户修改了菜单信息:[{}]为:[{}]，未修改层级，则不处理层级关系", oldEntity, updateEntity);
            menuMapper.updateById(oldEntity);
            return;
        }
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
     * 根据菜单id获取出整个树形结构
     *
     * @param entityId
     * @return
     */
    @Override
    public List<MenuTreeRespVO> getMenuTreeById(String entityId) {
        return MenuTreeUtils.buildMenuTree(id ->
                        menuMapper.getMenuTreeByIdAndStatus(id, DataStatusEnum.NORMAL), entityId,
                treePath -> menuMapper.getMenuTreeByTreePathAndStatus(treePath, DataStatusEnum.NORMAL));
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


}
