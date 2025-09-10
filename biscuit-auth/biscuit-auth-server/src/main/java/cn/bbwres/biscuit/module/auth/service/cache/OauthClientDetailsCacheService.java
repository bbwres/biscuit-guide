package cn.bbwres.biscuit.module.auth.service.cache;


import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;

/**
 * <p>
 * 认证客户端信息表 缓存接口
 * </p>
 *
 * @author zhanglinfeng
 * @Date 2021-02-26
 */
public interface OauthClientDetailsCacheService {


    /**
     * 根据主键获取数据
     *
     * @param id 主键
     * @return 返回对象
     */
    OauthClientDetailsEntity getOauthClientDetails(String id);


    /**
     * 更新数据
     *
     * @param entity 更新的数据
     * @return OauthClientDetails
     */
    OauthClientDetailsEntity updateById(OauthClientDetailsEntity entity);
}
