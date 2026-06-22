# biscuit-guide

基于 [biscuit](https://github.com/bbwres/biscuit) 微服务框架的参考模板工程，演示如何使用 biscuit Starter 搭建一个完整的微服务应用。

## 功能演示

- **OAuth2 认证中心**：Password / Authorization Code / Client Credentials 等多种授权模式
- **RBAC 权限管理**：用户 → 角色 → 菜单（目录/菜单/按钮三级），支持按钮级权限控制
- **文件管理**：上传、业务绑定、临时文件清理
- **API 网关**：统一入口、认证过滤、接口文档聚合
- **Spring Boot Admin**：服务监控与健康检查

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 构建 | Maven | 3.9+ |
| 框架 | Spring Boot | 3.5.9 |
| 微服务 | Spring Cloud + Nacos | 2.5 |
| 数据库 | PostgreSQL + MyBatis-Plus | 16 |
| 缓存 | Redis | 7 |
| 文档 | Knife4j (OpenAPI3) | 4.4.0 |
| 转换 | MapStruct | 1.6.3 |
| 简化 | Lombok | 1.18.38 |

## 项目结构

```
biscuit-guide/
├── biscuit-gateway/          # API 网关
│   └── 路由、认证过滤、Knife4j 文档聚合
├── biscuit-auth/             # 认证与权限模块
│   ├── biscuit-auth-api/     #   Feign 接口 + VO（供其他服务依赖）
│   └── biscuit-auth-server/  #   OAuth2 Server + RBAC 管理
├── biscuit-basic/            # 基础服务模块
│   ├── biscuit-basic-api/    #   Feign 接口 + VO
│   └── biscuit-basic-server/ #   文件管理
├── biscuit-common/           # 公共配置（异常处理、全局 Bean）
└── biscuit-boot-admin/       # Spring Boot Admin 监控
```

各模块内部遵循统一的分层结构：

```
模块-server/src/main/java/cn/bbwres/biscuit/module/{module}/
├── {Module}Application.java   # 启动类
├── config/                    # 模块配置
├── controller/                # REST 接口
│   └── vo/                    #   请求/响应 VO
├── convert/                   # MapStruct 转换器
├── dao/                       # MyBatis-Plus Mapper
├── entity/                    # 数据库实体
├── service/                   # 业务逻辑
│   └── cache/                 #   Redis 缓存
├── constants/                 # 错误码常量
└── utils/                     # 工具类
```

## 前置条件

### 1. 安装 biscuit 框架

biscuit-guide 依赖 biscuit 框架的 Starter 包，需要先在本地安装：

```bash
git clone https://github.com/bbwres/biscuit.git
cd biscuit
mvn clean install -DskipTests
```

### 2. 启动基础设施

```bash
cd docker
docker compose up -d
```

| 服务 | 端口 | 说明 |
|------|------|------|
| PostgreSQL | 5432 | 数据库 |
| Redis | 6379 | 缓存 |
| Nacos | 8848 | 注册中心 + 配置中心 |

数据库初始化脚本位于 `docker/init-scripts/`，容器启动时会自动执行。

## 快速开始

### 构建

```bash
# 全量编译
mvn clean install -DskipTests

# 运行测试
mvn clean test
```

### 启动顺序

```
1. Nacos (8848)     ← Docker Compose 已启动
2. PostgreSQL (5432) ← Docker Compose 已启动
3. Redis (6379)      ← Docker Compose 已启动
4. biscuit-auth-server
5. biscuit-basic-server
6. biscuit-gateway
7. biscuit-boot-admin (可选)
```

各模块启动类：

```bash
# auth-server
mvn spring-boot:run -pl biscuit-auth/biscuit-auth-server

# basic-server
mvn spring-boot:run -pl biscuit-basic/biscuit-basic-server

# gateway
mvn spring-boot:run -pl biscuit-gateway

# boot-admin
mvn spring-boot:run -pl biscuit-boot-admin
```

### 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway | 28000 / 27000 (management) | 统一入口 |
| auth-server | Nacos 动态分配 | 不建议直接暴露 |
| basic-server | Nacos 动态分配 | 不建议直接暴露 |
| boot-admin | 28100 | 监控面板 |

### 访问入口

- **API 网关**：http://localhost:28000
- **接口文档（Knife4j）**：http://localhost:28000/doc.html
- **监控面板**：http://localhost:28100

## 架构

### 服务拓扑

```
客户端 → biscuit-gateway (网关/认证/路由) → biscuit-auth-server (OAuth2 + RBAC)
                                           → biscuit-basic-server (文件管理)
                      biscuit-boot-admin (Spring Boot Admin 监控)
```

### 认证流程

1. 客户端请求 `/auth/oauth2/token` 获取 Token（放行路径，不走 AuthFilter）
2. 后续请求经 Gateway 的 AuthFilter 验证 Token
3. 下游服务通过 `biscuit-security-boot-starter` 作为 Resource Server 校验权限
4. 服务间调用通过 `biscuit-rpc-boot-starter` 签名校验

### 配置管理

- 各服务通过 `bootstrap.yaml` 连接 Nacos 获取远程配置
- 本地开发时可在 Nacos 中配置各服务的 `application.yaml`
- 紧急情况下可设置 `spring.cloud.nacos.config.enabled=false` 回退使用本地配置文件

## 开发指南

### 错误码管理

每个模块在 `constants` 包下维护独立的 `XxxErrorCodeConstants.java`：

```java
public interface AuthErrorCodeConstants {
    ErrorCode ACCOUNT_PASSWORD_ERROR = new ErrorCode("201001001", "auth.account_password_error");
}
```

- 错误码格式：`20{moduleXX}01{seq}`，9 位数字
- 消息 key 需同步到 `src/main/resources/i18n/{module}_messages*.properties`

### 代码生成

各 `*-server` 模块包含 `generator.properties`，可使用 biscuit 代码生成器一键生成 CRUD 代码。

### 事务规范

涉及多步写操作（跨表/跨 SQL/跨缓存）必须标注 `@Transactional(rollbackFor = RuntimeException.class)`：

```java
@Transactional(rollbackFor = RuntimeException.class)
public void addMenu(MenuEntity menuEntity, MenuEntity parentMenu) {
    menuMapper.insert(menuEntity);
    menuMapper.updateTreePathByParentId(...);
}
```

### 前端工程

前端工程基于 Vue 3 + TypeScript + Element Plus，位于同级的 `biscuit-guide-frontend` 目录。

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `NACOS_SERVER` | `localhost:8848` | Nacos 地址 |
| `NACOS_NAMESPACE` | `public` | Nacos 命名空间 |
| `NACOS_USERNAME` | — | Nacos 用户名（开启鉴权后必填） |
| `NACOS_PASSWORD` | — | Nacos 密码（开启鉴权后必填） |

## License

[Apache License 2.0](LICENSE)