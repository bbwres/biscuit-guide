package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static cn.bbwres.biscuit.module.auth.entity.table.MenuEntityTableDef.MENU_ENTITY;


/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-10-25
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<MenuEntity, MenuPageReqVO> selectPage(Page<MenuEntity, MenuPageReqVO> reqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.where(MENU_ENTITY.ID.eq(reqVO.getQuery().getId()));
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderBy(MENU_ENTITY.ID, false);

        com.mybatisflex.core.paginate.Page<MenuEntity> page = paginate(reqVO.getCurrent(), reqVO.getSize(), queryWrapper);
        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotalRow());
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
        UpdateChain.of(MenuEntity.class)
                .set(MENU_ENTITY.STATUS, status)
                .where(MENU_ENTITY.ID.eq(id))
                .update();
    }

    /**
     * 根据父级的treePath，修改所有子级的状态
     *
     * @param treePath
     * @param status
     */
    default void updateStatusByParentTreePath(String treePath, DataStatusEnum status) {
        UpdateChain.of(MenuEntity.class)
                .set(MENU_ENTITY.STATUS, status)
                .where(MENU_ENTITY.TREE_PATH.likeLeft(treePath))
                .update();
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

