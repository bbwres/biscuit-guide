package cn.bbwres.biscuit.module.auth.service;

import java.util.*;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.dto.Page;





/**
 * <p>
 * 认证客户端信息表 服务类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
public interface OauthClientDetailsService {

    /**
     * 获得认证客户端信息表
     *
     * @param id 编号
     * @return 认证客户端信息表
     */
    OauthClientDetailsEntity getOauthClientDetails(String id);

    /**
     * 获得认证客户端信息表列表
     *
     * @param ids 编号
     * @return 认证客户端信息表列表
     */
    List<OauthClientDetailsEntity> getOauthClientDetailsList(Collection<String> ids);

    /**
     * 获得认证客户端信息表分页
     *
     * @param pageReqVO 分页查询
     * @return 认证客户端信息表分页
     */
    Page<OauthClientDetailsEntity,OauthClientDetailsPageReqVO> getOauthClientDetailsPage(Page<OauthClientDetailsEntity,OauthClientDetailsPageReqVO> pageReqVO);


}
