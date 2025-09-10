package cn.bbwres.biscuit.module.auth.service.cache;

import cn.bbwres.biscuit.module.auth.config.AuthSystemConstant;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.module.auth.service.OauthClientDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * 认证客户端信息 的缓存
 *
 * @author zhanglinfeng
 */
@Slf4j
@Service
public class OauthClientDetailsCacheServiceImpl implements OauthClientDetailsCacheService {

    private OauthClientDetailsService oauthClientDetailsService;

    @Autowired
    public void setOauthClientDetailsService(OauthClientDetailsService oauthClientDetailsService) {
        this.oauthClientDetailsService = oauthClientDetailsService;
    }

    /**
     * 根据主键获取数据
     *
     * @param id 主键
     * @return 返回对象
     */
    @Override
    @Cacheable(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, key = "targetClass.name+'_'+#id", unless = "#result eq null")
    public OauthClientDetailsEntity getOauthClientDetails(String id) {
        return oauthClientDetailsService.getOauthClientDetails(id);
    }

    /**
     * 更新数据
     *
     * @param entity 更新的数据
     * @return OauthClientDetails
     */
    @Override
    @Caching(evict = {@CacheEvict(cacheNames = AuthSystemConstant.CACHE_NAME_AUTH_ONE_DAY, key = "targetClass.name+'_'+#entity.getId()")})
    public OauthClientDetailsEntity updateById(OauthClientDetailsEntity entity) {
        if (oauthClientDetailsService.updateById(entity)) {
            return entity;
        }
        return null;
    }


}
