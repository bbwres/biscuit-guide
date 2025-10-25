package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.util.ObjectUtils;


/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-10-25
 */
@Mapper
public interface MenuMapper extends BatchBaseMapper<MenuEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<MenuEntity, MenuPageReqVO> selectPage(Page<MenuEntity, MenuPageReqVO> reqVO) {
        LambdaQueryWrapper<MenuEntity> queryWrapper = Wrappers.lambdaQuery(MenuEntity.class);
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.eq(!ObjectUtils.isEmpty(reqVO.getQuery().getId()),
                    MenuEntity::getId, reqVO.getQuery().getId());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByAsc(MenuEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MenuEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }


    /**
     * 查询出最大的id
     *
     * @return
     */
    @Select("select max(id) from t_menu")
    String findMaxId();


    /**
     * 根据parentId 修改treePath数据
     *
     * @param parentId
     * @param oldTreePath
     * @param newTreePath
     */
    @Update("""
            update t_menu set tree_path = REPLACE(tree_path, #{oldTreePath}, #{newTreePath})
            where parent_id=#{parentId}
            """)
    void updateTreePathByParentId(@Param("parentId") String parentId, @Param("oldTreePath") String oldTreePath,
                                  @Param("newTreePath") String newTreePath);


    /**
     * 更新parentId
     *
     * @param id
     * @param parentId
     */
    @Update("""
            update t_menu set parent_id = #{parentId,jdbcType=VARCHAR}
            where id=#{id}
            """)
    void updateParentIdById(@Param("id") String id, @Param("parentId") String parentId);

    /**
     * 根据id修改状态
     *
     * @param id
     * @param status
     */
    default void updateStatusById(String id, DataStatusEnum status) {
        update(Wrappers.lambdaUpdate(MenuEntity.class)
                .set(MenuEntity::getStatus, status)
                .eq(MenuEntity::getId, id));
    }

    /**
     * 根据父级的treePath，修改所有子级的状态
     *
     * @param treePath
     * @param status
     */
    default void updateStatusByParentTreePath(String treePath, DataStatusEnum status) {
        update(Wrappers.lambdaUpdate(MenuEntity.class)
                .set(MenuEntity::getStatus, status)
                .likeLeft(MenuEntity::getTreePath, treePath));
    }
}

