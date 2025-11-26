package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;

import static cn.bbwres.biscuit.module.auth.entity.table.RoleEntityTableDef.ROLE_ENTITY;


/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<RoleEntity, RolePageReqVO> selectPage(Page<RoleEntity, RolePageReqVO> reqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.where(ROLE_ENTITY.ID.eq(reqVO.getQuery().getId()));
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderBy(ROLE_ENTITY.ID, false);

        com.mybatisflex.core.paginate.Page<RoleEntity> page = paginate(reqVO.getCurrent(), reqVO.getSize(), queryWrapper);
        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotalRow());
        reqVO.calculationPages();
        return reqVO;
    }

    /**
     * 根据角色编码和客户端id查询数据
     *
     * @param roleCode
     * @param clientId
     * @return
     */
    default RoleEntity findByRoleCodeAndClientId(String roleCode, String clientId) {
        return selectOneByCondition(ROLE_ENTITY.ROLE_CODE.eq(roleCode)
                .and(ROLE_ENTITY.CLIENT_ID.eq(clientId)));
    }
}

