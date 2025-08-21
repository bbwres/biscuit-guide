package cn.bbwres.biscuit.module.auth.service;

import java.util.*;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.dto.Page;





/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
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
    Page<MenuEntity,MenuPageReqVO> getMenuPage(Page<MenuEntity,MenuPageReqVO> pageReqVO);


}
