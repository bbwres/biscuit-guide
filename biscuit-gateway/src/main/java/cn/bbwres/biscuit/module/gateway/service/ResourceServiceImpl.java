package cn.bbwres.biscuit.module.gateway.service;

import cn.bbwres.biscuit.constants.SystemAuthConstant;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.gateway.service.ResourceService;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源服务
 *
 * @author zhanglinfeng
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    /**
     * 检查并解析token
     *
     * @param token
     * @return
     */
    @Override
    public UserBaseInfo<?> checkToken(String token) {
        UserBaseInfo<String> userBaseInfo = new UserBaseInfo<>();
        userBaseInfo.setUserId("1111");
        userBaseInfo.setUsername("zlf");
        userBaseInfo.setTenantId("111111");
        userBaseInfo.setClientId("1-mp");
        userBaseInfo.setZhName("张三");
        userBaseInfo.setAuthorities(List.of("admin","user"));

        return userBaseInfo;
    }

    /**
     * 获取仅需要登陆认证的资源地址
     *
     * @return
     */
    @Override
    public List<String> getLoginAuthResource() {
        return null;
    }

    /**
     * 根据角色信息获取出当前角色拥有的资源信息
     *
     * @param roleId 角色id
     * @return
     */
    @Override
    public List<String> getResourceByRole(String roleId) {
        return List.of("/auth/loginAccount/**");
    }

    /**
     * 获取登陆地址
     *
     * @return
     */
    @Override
    public String getLoginUrl() {
        return ResourceService.super.getLoginUrl();
    }

    /**
     * 获取登陆地址
     *
     * @param state
     * @return
     */
    @Override
    public String getLoginUrlBuildState(String state) {
        return ResourceService.super.getLoginUrlBuildState(state);
    }


}
