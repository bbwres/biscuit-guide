package cn.bbwres.biscuit.module.auth.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsPageReqVO;
import cn.bbwres.biscuit.module.auth.dao.OauthClientDetailsMapper;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 认证客户端信息表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Service
public class OauthClientDetailsServiceImpl implements OauthClientDetailsService {

    private final OauthClientDetailsMapper oauthClientDetailsMapper;


    /**
     * 获得认证客户端信息表
     *
     * @param id 编号
     * @return 认证客户端信息表
     */
    @Override
    public OauthClientDetailsEntity getOauthClientDetails(String id) {
        return oauthClientDetailsMapper.selectById(id);
    }

    /**
     * 获得认证客户端信息表列表
     *
     * @param ids 编号
     * @return 认证客户端信息表列表
     */
    @Override
    public List<OauthClientDetailsEntity> getOauthClientDetailsList(Collection<String> ids) {
        return oauthClientDetailsMapper.selectByIds(ids);
    }

    /**
     * 获得认证客户端信息表分页
     *
     * @param pageReqVO 分页查询
     * @return 认证客户端信息表分页
     */
    @Override
    public Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> getOauthClientDetailsPage(Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> pageReqVO) {
        return oauthClientDetailsMapper.selectPage(pageReqVO);
    }

    /**
     * 根据id更新数据
     *
     * @param entity
     * @return
     */
    @Override
    public boolean updateById(OauthClientDetailsEntity entity) {
        return oauthClientDetailsMapper.updateById(entity) > 0;
    }


}
