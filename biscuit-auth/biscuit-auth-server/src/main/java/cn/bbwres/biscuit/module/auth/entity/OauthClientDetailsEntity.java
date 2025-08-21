package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class OauthClientDetailsEntity extends BaseTenantEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 客户端id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 客户端所能访问的资源id集合,多个资源时用逗号(,)分隔
     */
    @TableField("resource_ids")
    private String resourceIds;


    /**
     * 用于指定客户端(client)的访问密匙
     */
    @TableField("client_secret")
    private String clientSecret;


    /**
     * 指定客户端申请的权限范围,可选值包括read,write,trust;
     */
    @TableField("scope")
    private String scope;


    /**
     * 指定客户端支持的grant_type
     */
    @TableField("authorized_grant_types")
    private String authorizedGrantTypes;


    /**
     * 客户端的重定向URI,可为空
     */
    @TableField("web_server_redirect_uri")
    private String webServerRedirectUri;


    /**
     * 指定客户端所拥有的权限值
     */
    @TableField("authorities")
    private String authorities;


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


}