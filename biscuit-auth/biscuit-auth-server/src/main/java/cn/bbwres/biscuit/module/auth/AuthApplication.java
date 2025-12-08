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

package cn.bbwres.biscuit.module.auth;

import cn.bbwres.biscuit.BootstrapProfile;
import cn.bbwres.biscuit.i18n.I18nProperties;
import cn.bbwres.biscuit.i18n.support.SystemMessageSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 认证鉴权服务
 *
 * @author zhanglinfeng
 */
@SpringBootApplication
public class AuthApplication {



    /**
     * 系统默认的messageSource
     *
     * @return
     */
    @Bean("authMessageBasename")
    public SystemMessageSource authMessageBasename(I18nProperties i18nProperties) {
        return new SystemMessageSource(i18nProperties.getMessageSourceCacheSeconds(), "i18n.auth_messages");
    }


    public static void main(String[] args) {
        BootstrapProfile.setBootstrapProfile();
        SpringApplication.run(AuthApplication.class, args);
    }
}

