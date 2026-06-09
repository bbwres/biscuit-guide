# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

biscuit-guide 是基于 biscuit 框架的参考模板工程，演示如何使用 biscuit Starter 搭建微服务应用。当前版本 `1.0.0-SNAPSHOT`，依赖 biscuit `3.0.3-SNAPSHOT`。

技术栈：Java 21、Maven 3.9+、Spring Boot 3.5.9、Lombok、MapStruct 1.6.3、Knife4j 4.4.0。

## 前置条件

**必须先本地安装 biscuit 框架**，否则编译失败：
```bash
# 在 biscuit 工程目录下执行
cd ../biscuit && mvn clean install -DskipTests
```

## 构建命令

```bash
# 全量构建
mvn clean install -DskipTests

# 运行测试（测试集中在 biscuit-auth-server）
mvn clean test

# 指定模块测试
mvn clean test -pl biscuit-auth/biscuit-auth-server
```

## 本地开发环境

```bash
# 启动基础设施（PostgreSQL 16、Redis 7、Nacos 2.5 standalone）
cd docker && docker-compose up -d
```

| 服务 | 端口 | 说明 |
|------|------|------|
| PostgreSQL | 5432 | 用户 postgresql / 密码 admin，初始库 postgresql |
| Redis | 6379 | 无密码 |
| Nacos | 8848/9848 | standalone 模式，已开启鉴权 |

数据库初始化脚本在 `docker/init-scripts/` 下。

## 架构

### 服务拓扑

```
客户端 → biscuit-gateway (网关/认证/路由) → biscuit-auth-server (OAuth2 授权 + RBAC)
                                        → biscuit-basic-server (文件管理)
                    biscuit-boot-admin (Spring Boot Admin 监控)
```

### 模块组织

```
biscuit-guide/
├── biscuit-gateway/                # API 网关（Knife4j 文档聚合，AuthFilter 统一认证）
├── biscuit-auth/
│   ├── biscuit-auth-api/          # Feign API 接口 + VO + 枚举（供其他服务依赖）
│   └── biscuit-auth-server/       # OAuth2 Server + RBAC 管理接口
├── biscuit-basic/
│   ├── biscuit-basic-api/         # Feign API 接口 + VO（供其他服务依赖）
│   └── biscuit-basic-server/      # 文件管理（上传、业务绑定、临时文件）
├── biscuit-common/                 # 全局通用配置（异常处理、公共 Bean）
└── biscuit-boot-admin/            # Spring Boot Admin（多环境：sit/prod）
```

### 业务模块内部结构（以 biscuit-auth-server 为例）

```
biscuit-auth-server/src/main/java/cn/bbwres/biscuit/module/auth/
├── AuthApplication.java            # 启动类
├── config/                         # Spring Security / OAuth2 配置
├── controller/                     # REST 接口
│   └── vo/                         #   请求/响应 VO
├── convert/                        # MapStruct 转换器接口
├── dao/                            # MyBatis-Plus Mapper
├── entity/                         # 数据库实体
├── service/                        # 业务逻辑
│   └── cache/                      #   Redis 缓存服务
├── constants/                      # 错误码、系统常量
└── utils/                          # 模块工具类
```

### 关键架构决策

- **api/server 分层**：业务模块拆分为 `*-api`（Feign 接口 + VO，供其他服务引用）和 `*-server`（实现），避免服务间循环依赖
- **注解处理器顺序**：`maven-compiler-plugin` 配置 `annotationProcessorPaths`，Lombok 先于 MapStruct 执行，确保 MapStruct 能正确处理 Lombok 生成的方法
- **Nacos 配置**：各服务通过 `bootstrap.yaml` 连接 Nacos，本地开发可设置 `spring.cloud.nacos.config.enabled=false` 回退到本地 `application.yaml`
- **网关路由**：biscuit-gateway 使用 biscuit 的 `biscuit-gateway-boot-starter`，支持 Nacos 动态路由，Knife4j 聚合所有下游服务的 API 文档
- **认证流程**：Gateway AuthFilter 验证 Token → 下游服务通过 `biscuit-security-boot-starter` 做 Resource Server → 服务间调用通过 `biscuit-rpc-boot-starter` 签名校验
- **代码生成**：各 `*-server` 模块含 `generator.properties`，可通过 biscuit 的 `biscuit-generator-code-maven-plugin` 一键生成增删改查代码
