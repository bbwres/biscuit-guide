package cn.bbwres.biscuit.module.auth.service;

import java.util.*;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.RoleMenuEntity;
import cn.bbwres.biscuit.dto.Page;





/**
 * <p>
 * 角色所属的资源 服务类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
public interface RoleMenuService {

    /**
     * 获得角色所属的资源
     *
     * @param id 编号
     * @return 角色所属的资源
     */
    RoleMenuEntity getRoleMenu(String id);

    /**
     * 获得角色所属的资源列表
     *
     * @param ids 编号
     * @return 角色所属的资源列表
     */
    List<RoleMenuEntity> getRoleMenuList(Collection<String> ids);

    /**
     * 获得角色所属的资源分页
     *
     * @param pageReqVO 分页查询
     * @return 角色所属的资源分页
     */
    Page<RoleMenuEntity,RoleMenuPageReqVO> getRoleMenuPage(Page<RoleMenuEntity,RoleMenuPageReqVO> pageReqVO);


}
