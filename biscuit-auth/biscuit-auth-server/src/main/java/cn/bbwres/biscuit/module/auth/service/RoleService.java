package cn.bbwres.biscuit.module.auth.service;

import java.util.*;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.dto.Page;





/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
public interface RoleService {

    /**
     * 获得角色表
     *
     * @param id 编号
     * @return 角色表
     */
    RoleEntity getRole(String id);

    /**
     * 获得角色表列表
     *
     * @param ids 编号
     * @return 角色表列表
     */
    List<RoleEntity> getRoleList(Collection<String> ids);

    /**
     * 获得角色表分页
     *
     * @param pageReqVO 分页查询
     * @return 角色表分页
     */
    Page<RoleEntity,RolePageReqVO> getRolePage(Page<RoleEntity,RolePageReqVO> pageReqVO);


}
