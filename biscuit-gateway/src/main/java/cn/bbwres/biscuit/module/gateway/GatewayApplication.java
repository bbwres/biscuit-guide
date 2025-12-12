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

package cn.bbwres.biscuit.module.gateway;

import cn.bbwres.biscuit.BootstrapProfile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

/**
 * 网关服务
 *
 * @author zhanglinfeng
 */
@SpringBootApplication
public class GatewayApplication {


    public static void main(String[] args) {
        BootstrapProfile.setBootstrapProfile();
        //作用是启用「自动上下文传播」机制—— 让线程上下文（如 MDC 日志上下文、事务上下文、安全上下文、请求头上下文等）在异步线程、虚拟线程、线程池任务、CompletableFuture 等场景下，
        // 自动从父线程传递到子线程 / 异步任务中，无需手动传递上下文
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(GatewayApplication.class, args);
    }
}