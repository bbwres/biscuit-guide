package cn.bbwres.biscuit.module.auth.service;

import java.util.*;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.RoleAccountEntity;
import cn.bbwres.biscuit.dto.Page;





/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
public interface RoleAccountService {

    /**
     * 获得用户角色表
     *
     * @param id 编号
     * @return 用户角色表
     */
    RoleAccountEntity getRoleAccount(String id);

    /**
     * 获得用户角色表列表
     *
     * @param ids 编号
     * @return 用户角色表列表
     */
    List<RoleAccountEntity> getRoleAccountList(Collection<String> ids);

    /**
     * 获得用户角色表分页
     *
     * @param pageReqVO 分页查询
     * @return 用户角色表分页
     */
    Page<RoleAccountEntity,RoleAccountPageReqVO> getRoleAccountPage(Page<RoleAccountEntity,RoleAccountPageReqVO> pageReqVO);


}
