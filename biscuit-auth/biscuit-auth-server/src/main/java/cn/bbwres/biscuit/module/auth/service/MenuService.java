package cn.bbwres.biscuit.module.auth.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
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
     */
    void addMenu(MenuEntity menuEntity, MenuEntity parentMenu);

    /**
     * 修改数据
     *
     * @param oldEntity
     * @param updateEntity
     * @param parentMenu
     */
    void editMenu(MenuEntity oldEntity, MenuEntity updateEntity, MenuEntity parentMenu);

    /**
     * 修改菜单状态
     *
     * @param entity
     */
    void editMenuStatus(MenuEntity entity);

}
