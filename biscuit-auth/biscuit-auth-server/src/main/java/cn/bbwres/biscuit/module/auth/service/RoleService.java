package cn.bbwres.biscuit.module.auth.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;

import java.util.Collection;
import java.util.List;


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
    Page<RoleEntity, RolePageReqVO> getRolePage(Page<RoleEntity, RolePageReqVO> pageReqVO);


    /**
     * 根据角色编码和客户端id查询数据
     *
     * @param roleCode
     * @param clientId
     * @return
     */
    RoleEntity findByRoleCodeAndClientId(String roleCode, String clientId);

    /**
     * 新增角色
     *
     * @param roleEntity
     */
    void addRole(RoleEntity roleEntity);

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    RoleEntity findById(String id);

    /**
     * 修改数据
     * @param entity
     */
    void updateById(RoleEntity entity);

}
