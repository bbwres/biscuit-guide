package cn.bbwres.biscuit.module.auth.convert;

import java.util.*;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.dto.Page;



/**
 * <p>
 * 认证客户端信息表 Convert
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface OauthClientDetailsConvert {

    /**
     * 转换对象
     */
     OauthClientDetailsConvert INSTANCE = Mappers.getMapper(OauthClientDetailsConvert.class);


    /**
     * 对象转换
     * @param bean 分页查询条件
     * @return
     */
     OauthClientDetailsRespVO convert(OauthClientDetailsEntity bean);

    /**
     * 转换list字段
     * @param list 请求list
     * @return
     */
    List<OauthClientDetailsRespVO> convertList(List<OauthClientDetailsEntity> list);

    /**
     * 分页查询数据
     * @param page 分页数据
     * @return
     */
    Page<OauthClientDetailsRespVO,OauthClientDetailsPageReqVO> convertPage(Page<OauthClientDetailsEntity,OauthClientDetailsPageReqVO> page);


}
