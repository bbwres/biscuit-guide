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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增或者修改客户端信息请求参数
 *
 * @author zhanglinfeng
 */
@Data
public class OauthClientDetailsAddOrUpdateReqVO implements Serializable {
    private static final long serialVersionUID = -3821898428871268832L;



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


}
