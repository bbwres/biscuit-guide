package cn.bbwres.biscuit.module.auth.convert;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsAddOrUpdateReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsRespVO;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * <p>
 * 认证客户端信息表 Convert
 * </p>
 *
 * <p>使用 {@code unmappedTargetPolicy = ReportingPolicy.WARN} 在编译期暴露未映射的
 * 目标字段，避免 MapStruct 默认行为下静默丢字段（如历史 bug: scope/scopes 名称不一致）。
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OauthClientDetailsConvert {

    /**
     * 转换对象
     */
    OauthClientDetailsConvert INSTANCE = Mappers.getMapper(OauthClientDetailsConvert.class);


    /**
     * 对象转换
     *
     * @param bean 分页查询条件
     * @return
     */
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.WARN)
    OauthClientDetailsRespVO convert(OauthClientDetailsEntity bean);

    /**
     * 转换list字段
     *
     * @param list 请求list
     * @return
     */
    List<OauthClientDetailsRespVO> convertList(List<OauthClientDetailsEntity> list);

    /**
     * 分页查询数据
     *
     * @param page 分页数据
     * @return
     */
    Page<OauthClientDetailsRespVO, OauthClientDetailsPageReqVO> convertPage(Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> page);


    /**
     * 转换请求参数
     *
     * @param req
     * @return
     */
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.WARN)
    OauthClientDetailsEntity covertAddOrUpdateReq(OauthClientDetailsAddOrUpdateReqVO req);

}
