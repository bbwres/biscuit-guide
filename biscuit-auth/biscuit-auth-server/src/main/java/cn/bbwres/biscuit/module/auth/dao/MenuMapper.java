package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
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

import java.util.List;


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
     * 根据旧 treePath 前缀批量更新所有后代节点的 treePath
     * <p>
     * 修复说明：原实现以 {@code parent_id = #{parentId}} 为条件，只能更新直接子节点，
     * 孙级及更深层节点的 treePath 不会更新，导致移动菜单后多级子节点路径失真。
     * 改为按 {@code tree_path} 前缀匹配，覆盖所有后代节点。
     *
     * @param parentId    父节点 id（保留参数以兼容调用方，但 SQL 中不再使用）
     * @param oldTreePath 当前节点移动前的完整路径（不含通配符）
     * @param newTreePath 当前节点移动后的完整路径
     */
    @Update("""
            update t_menu set tree_path = REPLACE(tree_path, #{oldTreePath}, #{newTreePath})
            where tree_path LIKE CONCAT(#{oldTreePath}, '/%')
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

    /**
     * 根据id和状态查询数据，当id为空是查询所有父节点为空的数据
     *
     * @param entityId
     * @param dataStatus
     * @return
     */
    List<MenuTreeRespVO> getMenuTreeByIdAndStatus(@Param("entityId") String entityId, @Param("dataStatus") DataStatusEnum dataStatus);

    /**
     * 根据treePath和状态查询数据
     *
     * @param treePath
     * @param dataStatus
     * @return
     */
    List<MenuTreeRespVO> getMenuTreeByTreePathAndStatus(@Param("treePath") String treePath, @Param("dataStatus") DataStatusEnum dataStatus);
}

