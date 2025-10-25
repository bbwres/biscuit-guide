package cn.bbwres.biscuit.module.auth.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.dao.RoleMapper;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;


    /**
     * 获得角色表
     *
     * @param id 编号
     * @return 角色表
     */
    @Override
    public RoleEntity getRole(String id) {
        return roleMapper.selectById(id);
    }

    /**
     * 获得角色表列表
     *
     * @param ids 编号
     * @return 角色表列表
     */
    @Override
    public List<RoleEntity> getRoleList(Collection<String> ids) {
        return roleMapper.selectByIds(ids);
    }

    /**
     * 获得角色表分页
     *
     * @param pageReqVO 分页查询
     * @return 角色表分页
     */
    @Override
    public Page<RoleEntity, RolePageReqVO> getRolePage(Page<RoleEntity, RolePageReqVO> pageReqVO) {
        return roleMapper.selectPage(pageReqVO);
    }

    /**
     * 根据角色编码和客户端id查询数据
     *
     * @param roleCode
     * @param clientId
     * @return
     */
    @Override
    public RoleEntity findByRoleCodeAndClientId(String roleCode, String clientId) {
        return roleMapper.findByRoleCodeAndClientId(roleCode, clientId);
    }

    /**
     * 新增角色
     *
     * @param roleEntity
     */
    @Override
    public void addRole(RoleEntity roleEntity) {
        roleEntity.setStatus(DataStatusEnum.NORMAL);
        roleMapper.insert(roleEntity);
    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    @Override
    public RoleEntity findById(String id) {
        return roleMapper.selectById(id);
    }

    /**
     * 修改数据
     *
     * @param entity
     */
    @Override
    public void updateById(RoleEntity entity) {
        roleMapper.updateById(entity);
    }


}
