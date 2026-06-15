package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 菜单接口关联表 Mapper 接口
 * </p>
 *
 * @author zlf
 */
@Mapper
public interface MenuApiMapper extends BatchBaseMapper<MenuApiEntity> {

    /**
     * 根据 menuId 删除所有接口关联
     *
     * @param menuId 菜单 id
     */
    default void deleteByMenuId(String menuId) {
        delete(Wrappers.lambdaQuery(MenuApiEntity.class)
                .eq(MenuApiEntity::getMenuId, menuId));
    }

    /**
     * 根据 menuId 查询接口列表
     *
     * @param menuId 菜单 id
     * @return 接口列表
     */
    default List<MenuApiEntity> findByMenuId(String menuId) {
        return selectList(Wrappers.lambdaQuery(MenuApiEntity.class)
                .eq(MenuApiEntity::getMenuId, menuId));
    }

    /**
     * 批量根据 menuId 查询接口列表（用于树形结构一次性加载，避免 N+1）
     *
     * @param menuIds 菜单 id 集合
     * @return 接口列表
     */
    default List<MenuApiEntity> findByMenuIds(Collection<String> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return selectList(Wrappers.lambdaQuery(MenuApiEntity.class)
                .in(MenuApiEntity::getMenuId, menuIds));
    }

}
