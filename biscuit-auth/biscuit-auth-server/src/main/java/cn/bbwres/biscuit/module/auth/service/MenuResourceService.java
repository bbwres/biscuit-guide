package cn.bbwres.biscuit.module.auth.service;

import java.util.*;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.MenuResourceEntity;
import cn.bbwres.biscuit.dto.Page;





/**
 * <p>
 * 菜单资源表 服务类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
public interface MenuResourceService {

    /**
     * 获得菜单资源表
     *
     * @param id 编号
     * @return 菜单资源表
     */
    MenuResourceEntity getMenuResource(String id);

    /**
     * 获得菜单资源表列表
     *
     * @param ids 编号
     * @return 菜单资源表列表
     */
    List<MenuResourceEntity> getMenuResourceList(Collection<String> ids);

    /**
     * 获得菜单资源表分页
     *
     * @param pageReqVO 分页查询
     * @return 菜单资源表分页
     */
    Page<MenuResourceEntity,MenuResourcePageReqVO> getMenuResourcePage(Page<MenuResourceEntity,MenuResourcePageReqVO> pageReqVO);


}
