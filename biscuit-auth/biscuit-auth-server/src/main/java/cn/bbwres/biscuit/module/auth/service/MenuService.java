package cn.bbwres.biscuit.module.auth.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;

import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author zlf
 * @Date 2025-10-25
 */
public interface MenuService {

    /**
     * 获得菜单权限表
     *
     * @param id 编号
     * @return 菜单权限表
     */
    MenuEntity getMenu(String id);

    /**
     * 获得菜单权限表列表
     *
     * @param ids 编号
     * @return 菜单权限表列表
     */
    List<MenuEntity> getMenuList(Collection<String> ids);

    /**
     * 获得菜单权限表分页
     *
     * @param pageReqVO 分页查询
     * @return 菜单权限表分页
     */
    Page<MenuEntity, MenuPageReqVO> getMenuPage(Page<MenuEntity, MenuPageReqVO> pageReqVO);


    /**
     * 新增菜单
     *
     * @param menuEntity menuEntity
     * @param parentMenu parentMenu 父级信息
     * @param apiList    关联的接口列表（可空）
     */
    void addMenu(MenuEntity menuEntity, MenuEntity parentMenu, List<MenuApiEntity> apiList);

    /**
     * 修改数据
     *
     * @param oldEntity    原菜单
     * @param updateEntity 修改后的菜单
     * @param parentMenu   父级菜单
     * @param apiList      关联的接口列表（全量替换，可空表示清空）
     */
    void editMenu(MenuEntity oldEntity, MenuEntity updateEntity, MenuEntity parentMenu, List<MenuApiEntity> apiList);

    /**
     * 修改菜单状态
     *
     * @param entity
     */
    void editMenuStatus(MenuEntity entity);

    /**
     * 根据菜单id获取出整个树形结构（含每个节点的 menuApiList）
     *
     * @param entityId
     * @return
     */
    List<MenuTreeRespVO> getMenuTreeById(String entityId);

    /**
     * 根据角色id查询出关联的菜单信息
     *
     * @param roleId
     * @return
     */
    List<MenuEntity> findByRoleId(String roleId);

    /**
     * 根据角色id查询出关联菜单的所有接口（用于资源鉴权）
     *
     * @param roleId 角色id
     * @return 接口列表
     */
    List<MenuApiEntity> findApisByRoleId(String roleId);

    /**
     * 根据菜单id查询出 关联的角色id
     *
     * @param menuId
     * @return
     */
    List<String> findRolesByMenuId(String menuId);

    /**
     * 删除菜单（需校验是否有子菜单）
     *
     * @param id 菜单id
     */
    void deleteMenu(String id);

    /**
     * 根据菜单 id 查询关联的接口列表
     *
     * @param menuId 菜单 id
     * @return 接口列表
     */
    List<MenuApiEntity> findApiListByMenuId(String menuId);

    /**
     * 批量根据菜单 id 查询关联的接口列表
     *
     * @param menuIds 菜单 id 集合
     * @return 接口列表
     */
    List<MenuApiEntity> findApiListByMenuIds(Collection<String> menuIds);

}
