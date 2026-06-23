package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleMenuEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 角色所属的资源 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface RoleMenuMapper extends BatchBaseMapper<RoleMenuEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<RoleMenuEntity, RoleMenuEntity> selectPage(Page<RoleMenuEntity, RoleMenuEntity> reqVO) {
        LambdaQueryWrapper<RoleMenuEntity> queryWrapper = Wrappers.lambdaQuery(RoleMenuEntity.class);
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.eq(!ObjectUtils.isEmpty(reqVO.getQuery().getId()),
                    RoleMenuEntity::getId, reqVO.getQuery().getId());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(RoleMenuEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RoleMenuEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }

    /**
     * 根据roleId删除所有配置的菜单数据
     *
     * @param roleId
     */
    default void deleteByRoleId(String roleId) {
        delete(Wrappers.lambdaQuery(RoleMenuEntity.class)
                .eq(RoleMenuEntity::getRoleId, roleId));
    }


    /**
     * 根据roleId 查询关联的菜单信息
     *
     * @param roleId
     * @param status
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<MenuEntity> findByRoleIdNoTenant(@Param("roleId") String roleId, @Param("status") DataStatusEnum status);

    /**
     * 根据菜单id查询出关联的角色id
     *
     * @param menuId
     * @return
     */
    default List<String> findRolesByMenuId(String menuId) {
        return selectObjs(Wrappers.lambdaQuery(RoleMenuEntity.class)
                .eq(RoleMenuEntity::getMenuId, menuId)
                .select(RoleMenuEntity::getRoleId));
    }


    /**
     * 根据角色ID获取菜单数据
     * @param roleId
     * @param status
     * @return
     */
    List<MenuEntity> findByRoleId(@Param("roleId") String roleId, @Param("status") DataStatusEnum status);

    /**
     * 根据菜单id删除所有关联的角色数据
     *
     * @param menuId 菜单id
     */
    default void deleteByMenuId(String menuId) {
        delete(Wrappers.lambdaQuery(RoleMenuEntity.class)
                .eq(RoleMenuEntity::getMenuId, menuId));
    }

    /**
     * 根据角色id 查询出关联菜单的所有接口（忽略租户隔离）
     *
     * @param roleId 角色id
     * @param status 菜单状态
     * @return 接口列表
     */
    @InterceptorIgnore(tenantLine = "true")
    List<MenuApiEntity> findApisByRoleIdNoTenant(@Param("roleId") String roleId, @Param("status") DataStatusEnum status);

}

