package cn.bbwres.biscuit.module.auth.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 认证客户端信息表 分页 Request VO
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Schema(description = "认证客户端信息表分页 Request VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class OauthClientDetailsPageReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 客户端id
     */
    @Schema(description = "客户端id")
    private String id;


    /**
     * 客户端所能访问的资源id集合,多个资源时用逗号(,)分隔
     */
    @Schema(description = "客户端所能访问的资源id集合,多个资源时用逗号(,)分隔")
    private String resourceIds;

    /**
     * 用于指定客户端(client)的访问密匙
     */
    @Schema(description = "用于指定客户端(client)的访问密匙")
    private String clientSecret;

    /**
     * 指定客户端申请的权限范围,可选值包括read,write,trust;
     */
    @Schema(description = "指定客户端申请的权限范围,可选值包括read,write,trust;")
    private String scope;

    /**
     * 指定客户端支持的grant_type
     */
    @Schema(description = "指定客户端支持的grant_type")
    private String authorizedGrantTypes;

    /**
     * 客户端的重定向URI,可为空
     */
    @Schema(description = "客户端的重定向URI,可为空")
    private String webServerRedirectUri;

    /**
     * 指定客户端所拥有的权限值
     */
    @Schema(description = "指定客户端所拥有的权限值")
    private String authorities;

    /**
     * 设定客户端的access_token的有效时间值(单位:秒)
     */
    @Schema(description = "设定客户端的access_token的有效时间值(单位:秒)")
    private Integer accessTokenValidity;

    /**
     * 设定客户端的refresh_token的有效时间值(单位:秒)
     */
    @Schema(description = "设定客户端的refresh_token的有效时间值(单位:秒)")
    private Integer refreshTokenValidity;

    /**
     * 租户编码
     */
    @Schema(description = "租户编码")
    private String tenantId;


    /**
     * accessToken的类型，reference-不透明的token，self-contained-jwt类型的token
     */
    @Schema(description = "accessToken的类型")
    private String accessTokenFormat;


    /**
     * 是否复用刷新令牌
     * 为true则复用刷新令牌（refresh token），为false则签发新的刷新令牌。
     */
    @Schema(description = "是否复用刷新令牌")
    private Boolean reuseRefreshToken;


    /**
     * 用户是否单一登录
     * true则用户每次登录失效其他token，为false则允许用户同时登录多次
     */
    @Schema(description = "用户是否单一登录")
    private Boolean singleUserLogin;

    /**
     * 客户端支持的认证方式
     */
    @Schema(description = "客户端支持的认证方式")
    private String clientAuthenticationMethods;


}
