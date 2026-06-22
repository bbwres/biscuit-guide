package cn.bbwres.biscuit.module.auth.dao;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import cn.bbwres.biscuit.dto.Page;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.ObjectUtils;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;


/**
 * <p>
 * 认证客户端信息表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface OauthClientDetailsMapper extends BatchBaseMapper<OauthClientDetailsEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> selectPage(Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> reqVO) {
        LambdaQueryWrapper<OauthClientDetailsEntity> queryWrapper = Wrappers.lambdaQuery(OauthClientDetailsEntity.class);
        OauthClientDetailsPageReqVO query = reqVO.getQuery();
        if (!ObjectUtils.isEmpty(query)) {
            // 模糊匹配：客户端 id（也是 t_oauth_client_details.id 主键，业务上即"客户端编号"），
            // 实体没有单独的 clientName 字段，前端"客户端名称"暂不参与过滤
            queryWrapper.like(!ObjectUtils.isEmpty(query.getId()),
                    OauthClientDetailsEntity::getId, query.getId());

            // 精确匹配：以下字段一般完全相等才有意义（grant type、token 格式等）
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getAuthorizedGrantTypes()),
                    OauthClientDetailsEntity::getAuthorizedGrantTypes, query.getAuthorizedGrantTypes());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getAccessTokenFormat()),
                    OauthClientDetailsEntity::getAccessTokenFormat, query.getAccessTokenFormat());
            queryWrapper.eq(query.getReuseRefreshToken() != null,
                    OauthClientDetailsEntity::getReuseRefreshToken, query.getReuseRefreshToken());
            queryWrapper.eq(query.getSingleUserLogin() != null,
                    OauthClientDetailsEntity::getSingleUserLogin, query.getSingleUserLogin());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getClientAuthenticationMethods()),
                    OauthClientDetailsEntity::getClientAuthenticationMethods, query.getClientAuthenticationMethods());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(OauthClientDetailsEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OauthClientDetailsEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }

}

