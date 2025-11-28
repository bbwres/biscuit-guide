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

package cn.bbwres.biscuit.module.boot.admin;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * 服务监控
 *
 * @author zhanglinfeng
 */
@EnableDiscoveryClient
@EnableAdminServer
@SpringBootApplication
public class BootAdminApplication {


    public static void main(String[] args) {
        SpringApplication.run(BootAdminApplication.class, args);
    }


    /**
     * 安全配置
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      AdminServerProperties adminServerProperties) throws Exception {

        String adminContextPath = adminServerProperties.getContextPath();
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");
        http.authorizeHttpRequests((authorizeHttpRequests) -> {
                    authorizeHttpRequests.requestMatchers(adminContextPath + "/assets/**").permitAll()
                            .requestMatchers(adminContextPath + "/login").permitAll()
                            .anyRequest().authenticated();
                })
                .formLogin(formLogin -> {
                    formLogin.loginPage(adminContextPath + "/login").successHandler(successHandler);

                })
                .logout(logout -> {
                    logout.logoutUrl(adminContextPath + "/logout");
                })
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(adminContextPath + "/instances",
                                adminContextPath + "/actuator/**"));


        return http.build();
    }
}