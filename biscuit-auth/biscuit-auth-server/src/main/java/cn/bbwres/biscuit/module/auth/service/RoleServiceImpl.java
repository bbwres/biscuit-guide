package cn.bbwres.biscuit.module.auth.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleAddMenuReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.dao.RoleAccountMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleMenuMapper;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleMenuEntity;
import cn.bbwres.biscuit.module.auth.service.cache.MenuCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
    private final RoleMenuMapper roleMenuMapper;

    private final RoleAccountMapper roleAccountMapper;
    private final MenuCacheService menuCacheService;


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

    /**
     * 新增角色菜单配置
     *
     * @param roleEntity
     * @param roleAddMenuReq
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void roleMenuConfig(RoleEntity roleEntity, RoleAddMenuReqVO roleAddMenuReq) {
        //删除目前的角色资源配置
        roleMenuMapper.deleteByRoleId(roleEntity.getId());
        if (CollectionUtils.isEmpty(roleAddMenuReq.getMenuIds())) {
            log.info("当前角色id:[{}]配置的菜单信息为空!", roleAddMenuReq.getId());
            return;
        }
        List<RoleMenuEntity> roleMenuEntities = new ArrayList<>(16);
        for (String menuId : roleAddMenuReq.getMenuIds()) {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setRoleId(roleEntity.getId());
            roleMenuEntity.setMenuId(menuId);
            roleMenuEntity.setRoleCode(roleEntity.getRoleCode());
            roleMenuEntity.setClientId(roleEntity.getClientId());
            roleMenuEntities.add(roleMenuEntity);
        }
        roleMenuMapper.insert(roleMenuEntities);
        // 清除该角色的菜单缓存，使配置立即生效
        menuCacheService.deleteCacheByRoleId(roleEntity.getId());

    }

    /**
     * 根据账户id查询关联的角色信息
     *
     * @param accountId
     * @return
     */
    @Override
    public List<RoleEntity> findByAccountIdNoTenant(String accountId) {
        return roleAccountMapper.findByAccountIdNoTenant(accountId,DataStatusEnum.NORMAL);
    }

    /**
     * 根据账户id查询关联的角色信息
     *
     * @param accountId
     * @return
     */
    @Override
    public List<RoleEntity> findByAccountId(String accountId) {
        return roleAccountMapper.findByAccountId(accountId,DataStatusEnum.NORMAL);
    }

    /**
     * 根据账户id查询关联的角色信息，支持按客户端id筛选
     *
     * @param accountId
     * @param clientId  可选，为空则查询所有
     * @return
     */
    @Override
    public List<RoleEntity> findByAccountIdAndClientId(String accountId, String clientId) {
        return roleAccountMapper.findByAccountIdAndClientId(accountId, DataStatusEnum.NORMAL, clientId);
    }

    /**
     * 根据角色id查询出角色关联的账户信息
     *
     * @param roleId
     * @return
     */
    @Override
    public List<String> findAccountsByRoleId(String roleId) {
        return roleAccountMapper.findAccountsByRoleId(roleId);
    }

    /**
     * 根据角色id查询关联的菜单信息
     *
     * @param roleId
     * @return
     */
    @Override
    public List<MenuEntity> findMenusByRoleId(String roleId) {
        return roleMenuMapper.findByRoleId(roleId, DataStatusEnum.NORMAL);
    }


}
