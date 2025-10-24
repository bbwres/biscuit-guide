package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseEntity;
import cn.bbwres.biscuit.entity.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 认证客户端信息表
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_oauth_client_details")
public class OauthClientDetailsEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 客户端id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 用于指定客户端(client)的访问密钥
     */
    @TableField("client_secret")
    private String clientSecret;

    /**
     * 客户端支持的认证方式
     */
    @TableField("client_authentication_methods")
    private String clientAuthenticationMethods;


    /**
     * 指定客户端申请的权限范围,可选值包括read,write,trust;
     */
    @TableField("scopes")
    private String scopes;


    /**
     * 指定客户端支持的grant_type
     */
    @TableField("authorized_grant_types")
    private String authorizedGrantTypes;


    /**
     * 客户端的重定向URI
     */
    @TableField("web_server_redirect_uri")
    private String webServerRedirectUri;


    /**
     * postLogoutRedirectUri
     */
    @TableField("post_logout_redirect_uri")
    private String postLogoutRedirectUri;


    /**
     * 设定客户端的access_token的有效时间值(单位:秒)
     */
    @TableField("access_token_validity")
    private Integer accessTokenValidity;


    /**
     * 设定客户端的refresh_token的有效时间值(单位:秒)
     */
    @TableField("refresh_token_validity")
    private Integer refreshTokenValidity;


    /**
     * accessToken的类型，reference-不透明的token，self-contained-jwt类型的token
     */
    @TableField("access_token_format")
    private String accessTokenFormat;


    /**
     * 是否复用刷新令牌
     * 为true则复用刷新令牌（refresh token），为false则签发新的刷新令牌。
     */
    @TableField("reuse_refresh_token")
    private Boolean reuseRefreshToken;


    /**
     * 用户是否单一登录
     * true则用户每次登录失效其他token，为false则允许用户同时登录多次
     */
    @TableField("single_user_login")
    private Boolean singleUserLogin;


}