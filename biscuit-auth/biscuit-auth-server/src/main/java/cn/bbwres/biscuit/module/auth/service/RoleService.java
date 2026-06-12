package cn.bbwres.biscuit.module.auth.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleAddMenuReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
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
     *
     * @param id
     * @return
     */
    RoleEntity findById(String id);

    /**
     * 修改数据
     *
     * @param entity
     */
    void updateById(RoleEntity entity);

    /**
     * 新增角色菜单配置
     *
     * @param roleEntity
     * @param roleAddMenuReq
     */
    void roleMenuConfig(RoleEntity roleEntity, RoleAddMenuReqVO roleAddMenuReq);

    /**
     * 根据账户id查询关联的角色信息
     *
     * @param accountId
     * @return
     */
    List<RoleEntity> findByAccountIdNoTenant(String accountId);




    /**
     * 根据账户id查询关联的角色信息
     *
     * @param accountId
     * @return
     */
    List<RoleEntity> findByAccountId(String accountId);

    /**
     * 根据账户id查询关联的角色信息，支持按客户端id筛选
     *
     * @param accountId
     * @param clientId  可选，为空则查询所有
     * @return
     */
    List<RoleEntity> findByAccountIdAndClientId(String accountId, String clientId);

    /**
     * 根据角色id查询出角色关联的账户信息
     *
     * @param roleId
     * @return
     */
    List<String> findAccountsByRoleId(String roleId);

    /**
     * 根据角色id查询关联的菜单信息
     *
     * @param roleId
     * @return
     */
    List<MenuEntity> findMenusByRoleId(String roleId);

}
