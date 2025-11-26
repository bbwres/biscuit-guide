package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleMenuEntity;
import cn.bbwres.biscuit.module.auth.entity.table.RoleMenuEntityTableDef;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
public interface RoleMenuMapper extends BaseMapper<RoleMenuEntity> {


    /**
     * 根据roleId删除所有配置的菜单数据
     *
     * @param roleId
     */
    default void deleteByRoleId(String roleId) {
        deleteByCondition(RoleMenuEntityTableDef.ROLE_MENU_ENTITY.ROLE_ID.eq(roleId));
    }


    /**
     * 根据roleId 查询关联的菜单信息
     *
     * @param roleId
     * @param status
     * @return
     */
    @Select("""
            select m.*  from t_role_menu rm left join  t_menu m on m.id = rm.menu_id
            where rm.role_id = #{roleId,jdbcType=VARCHAR}
            and m.status = #{status}
            """)
    List<MenuEntity> findByRoleIdNoTenant(@Param("roleId") String roleId, @Param("status") DataStatusEnum status);

    /**
     * 根据菜单id查询出关联的角色id
     *
     * @param menuId
     * @return
     */
    default List<String> findRolesByMenuId(String menuId) {
        return selectObjectListByQueryAs(QueryWrapper.create()
                .where(RoleMenuEntityTableDef.ROLE_MENU_ENTITY.MENU_ID.eq(menuId))
                .select(RoleMenuEntityTableDef.ROLE_MENU_ENTITY.ROLE_ID), String.class);
    }

}

