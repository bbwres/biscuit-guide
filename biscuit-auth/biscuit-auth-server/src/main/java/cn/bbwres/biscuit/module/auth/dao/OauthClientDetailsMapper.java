package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsPageReqVO;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;

import static cn.bbwres.biscuit.module.auth.entity.table.OauthClientDetailsEntityTableDef.OAUTH_CLIENT_DETAILS_ENTITY;


/**
 * <p>
 * 认证客户端信息表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface OauthClientDetailsMapper extends BaseMapper<OauthClientDetailsEntity> {

        /**
    * 分页查询数据
    * @param reqVO 分页查询条件
    * @return
    */
    default Page<OauthClientDetailsEntity,OauthClientDetailsPageReqVO> selectPage(Page<OauthClientDetailsEntity,OauthClientDetailsPageReqVO> reqVO){
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.where(OAUTH_CLIENT_DETAILS_ENTITY.ID.eq(reqVO.getQuery().getId()));
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderBy(OAUTH_CLIENT_DETAILS_ENTITY.ID, false);

        com.mybatisflex.core.paginate.Page<OauthClientDetailsEntity> page = paginate(reqVO.getCurrent(), reqVO.getSize(), queryWrapper);
        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotalRow());
        reqVO.calculationPages();
        return reqVO;
    }

}

