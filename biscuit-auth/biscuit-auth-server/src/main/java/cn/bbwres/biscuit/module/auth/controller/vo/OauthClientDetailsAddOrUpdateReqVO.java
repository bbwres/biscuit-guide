/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.module.auth.controller.vo;

import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidateEditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增或者修改客户端信息请求参数
 *
 * <p>字段与 {@link cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity} 一一对应，
 * 避免 MapStruct 因名称不一致静默丢字段。
 *
 * @author zhanglinfeng
 */
@Data
public class OauthClientDetailsAddOrUpdateReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3821898428871268832L;


    /**
     * 客户端id
     */
    @Schema(description = "客户端id,修改时必填")
    @NotBlank(groups = ValidateEditGroup.class)
    private String id;


    /**
     * 用于指定客户端(client)的访问密匙
     */
    @Schema(description = "用于指定客户端(client)的访问密匙")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String clientSecret;

    /**
     * 客户端支持的认证方式
     */
    @Schema(description = "客户端支持的认证方式,如 client_secret_basic")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String clientAuthenticationMethods;

    /**
     * 指定客户端申请的权限范围,可选值包括read,write,trust;
     */
    @Schema(description = "指定客户端申请的权限范围,可选值包括read,write,trust;")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String scopes;

    /**
     * 指定客户端支持的grant_type
     */
    @Schema(description = "指定客户端支持的grant_type")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String authorizedGrantTypes;

    /**
     * 客户端的重定向URI,可为空
     */
    @Schema(description = "客户端的重定向URI,可为空")
    private String webServerRedirectUri;

    /**
     * 登出后重定向URI,可为空
     */
    @Schema(description = "登出后重定向URI,可为空")
    private String postLogoutRedirectUri;

    /**
     * 设定客户端的access_token的有效时间值(单位:秒)
     */
    @Schema(description = "设定客户端的access_token的有效时间值(单位:秒)")
    @NotNull(groups = {ValidateAddGroup.class})
    private Integer accessTokenValidity;

    /**
     * 设定客户端的refresh_token的有效时间值(单位:秒)
     */
    @Schema(description = "设定客户端的refresh_token的有效时间值(单位:秒)")
    @NotNull(groups = {ValidateAddGroup.class})
    private Integer refreshTokenValidity;

    /**
     * accessToken的类型，reference-不透明的token，self-contained-jwt类型的token
     */
    @Schema(description = "accessToken的类型,reference 或 self-contained")
    private String accessTokenFormat;

    /**
     * 是否复用刷新令牌
     */
    @Schema(description = "是否复用刷新令牌")
    private Boolean reuseRefreshToken;

    /**
     * 用户是否单一登录
     */
    @Schema(description = "用户是否单一登录")
    private Boolean singleUserLogin;


}
