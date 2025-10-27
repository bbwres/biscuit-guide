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

package cn.bbwres.biscuit.module.auth.constants;

import cn.bbwres.biscuit.exception.constants.ErrorCode;

/**
 * auth 服务错误嘛
 *
 * @author zhanglinfeng
 */
public interface AuthErrorCodeConstants {

    /**
     * 数据已经存在
     */
    ErrorCode DATA_ALREADY_EXISTS_ERROR = new ErrorCode("201001001", "auth.data_already_exists_error");

    /**
     * 数据不存在
     */
    ErrorCode DATA_NO_EXISTS_ERROR = new ErrorCode("201001002", "auth.data_no_exists_error");

    /**
     * 账户未配置角色信息
     */
    ErrorCode ACCOUNT_NO_ROLE_ERROR = new ErrorCode("201001003", "auth.account_no_role_error");
    /**
     * 密码错误
     */
    ErrorCode ACCOUNT_PASSWORD_ERROR = new ErrorCode("201001004", "auth.account_password_error");
}
