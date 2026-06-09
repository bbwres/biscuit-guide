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

## 错误码维护约定

每个业务模块在 `cn.bbwres.biscuit.module.{module}.constants` 包下维护一个独立的 `{ModuleName}ErrorCodeConstants` 接口文件，用于集中管理该模块的所有业务错误码。

### 现有错误码文件

| 模块 | 文件 | 包路径 |
|------|------|--------|
| auth | `AuthErrorCodeConstants.java` | `cn.bbwres.biscuit.module.auth.constants` |
| basic | `BasicErrorCodeConstants.java` | `cn.bbwres.biscuit.module.basic.constants` |

新增模块时需按此约定创建对应的 `XxxErrorCodeConstants.java`。

### 编码规范

```java
package cn.bbwres.biscuit.module.{module}.constants;

import cn.bbwres.biscuit.exception.constants.ErrorCode;

public interface {ModuleName}ErrorCodeConstants {
    /**
     * 业务错误描述
     */
    ErrorCode {SEMANTIC_NAME} = new ErrorCode("{错误码}", "{module}.{message_key}");
}
```

- **结构**：interface（自动 `public static final`）
- **错误码格式**：`20{moduleXX}01{seq}`，9 位数字
  - `201001001` → auth 模块第 1 类业务第 001 个错误
  - `202001001` → basic 模块第 1 类业务第 001 个错误
- **message key 格式**：`{module}.{snake_case_key}`，与 i18n 资源文件（`src/main/resources/i18n/{module}_messages*.properties`）对应
- **使用方式**：在 Service/Controller 中通过 `throw new SystemBusinessRuntimeException(AuthErrorCodeConstants.ACCOUNT_PASSWORD_ERROR)` 抛出

### i18n 资源同步

每个新错误码的 message key 必须同步添加到对应模块的 i18n 资源文件中：

- `biscuit-{module}/biscuit-{module}-server/src/main/resources/i18n/{module}_messages.properties`（默认）
- `..._messages_zh_CN.properties`（简体中文）
- `..._messages_en_US.properties`（英文）
- `..._messages_zh_HK.properties`（繁体）

例：auth 模块的错误码 `auth.account_password_error` 对应 `auth_messages*.properties` 中的 `auth.account_password_error=...`。

## 事务使用约定

Service 层方法涉及**多步写操作**（含跨表、跨 SQL、跨缓存）时，必须标注 `@Transactional(rollbackFor = RuntimeException.class)`，确保任一步失败能完整回滚，避免出现"部分成功"的不一致状态。

### 必须加事务的典型场景

| 场景 | 例子 | 原因 |
|------|------|------|
| 多 SQL 写 | `addMenu` 包含 `insert` + `updateTreePathByParentId` 两步 | 后者依赖前者生成的 id 校正 `treePath`，必须原子化 |
| 跨表写入 | `roleMenuConfig` 删除旧关联 + 插入新关联 | 不加事务会出现"删除成功但插入失败"的孤儿数据 |
| 写库 + 清理缓存 | 实体更新后失效 Redis 缓存 | 缓存清理失败应回滚数据库修改 |
| 主从写入 | 父表 insert + 子表 insert 依赖父表 id | 子表插入失败必须回滚父表 |

### 强制规范

```java
// ✅ 正确：显式指定 rollbackFor，覆盖所有 RuntimeException
@Override
@Transactional(rollbackFor = RuntimeException.class)
public void addMenu(MenuEntity menuEntity, MenuEntity parentMenu) { ... }

// ❌ 错误：默认 rollbackFor 不覆盖 checked exception
@Override
@Transactional
public void editMenu(...) { ... }

// ❌ 错误：多步写但没加事务
@Override
public void addMenu(MenuEntity menuEntity, MenuEntity parentMenu) {
    menuMapper.insert(menuEntity);                    // 第 1 步
    menuMapper.updateTreePathByParentId(...);          // 第 2 步：失败时第 1 步不会回滚
}
```

### 注意事项

- 只读方法（如 `getMenu`、`selectPage`、`getMenuTree`）**不要**加事务
- 单条 SQL 写操作（仅 `insert`/`updateById`/`delete` 之一）**不必**加事务
- 跨服务调用（Feign、RPC）**不要**包含在事务中，事务应限定在单服务单数据源
- 修改 Service 方法签名（增删 SQL 步骤）时，必须同时检查并更新事务边界
- 单元测试可通过反射验证关键方法已标注 `@Transactional`，防止回归
