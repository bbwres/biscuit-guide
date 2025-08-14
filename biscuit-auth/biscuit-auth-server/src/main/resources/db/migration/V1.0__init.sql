DROP TABLE IF EXISTS `t_login_account`;

CREATE TABLE `t_login_account`
(
    `id`                        varchar(36) COLLATE utf8mb4_bin  NOT NULL COMMENT '主键',
    `login_name`                varchar(36) COLLATE utf8mb4_bin  NOT NULL COMMENT '登陆账号',
    `login_password`            varchar(256) COLLATE utf8mb4_bin NOT NULL COMMENT '登陆密码',
    `phone`                     varchar(256) COLLATE utf8mb4_bin DEFAULT '' COMMENT '手机号',
    `create_time`               datetime                         NOT NULL COMMENT '创建时间',
    `creator`                   varchar(50) COLLATE utf8mb4_bin  NOT NULL COMMENT '创建人',
    `updater`                   varchar(50) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '更新人',
    `update_time`               datetime                         DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `tenant_id`                 varchar(36) COLLATE utf8mb4_bin  NOT NULL COMMENT '租户编码',
    `user_id`                   varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '关联用户表id',
    `status`                    tinyint(2) NOT NULL DEFAULT '0' COMMENT '登陆用户状态',
    `locked_time`               datetime                         DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '锁定到期时间',
    `name`                      varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '姓名',
    `last_update_password_time` datetime                         NOT NULL COMMENT '最后一次修改密码时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_un_account_loginname` (`login_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='登陆账户表';


/*Table structure for table `t_oauth_client_details` */

DROP TABLE IF EXISTS `t_oauth_client_details`;

CREATE TABLE `t_oauth_client_details`
(
    `id`                      varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '客户端id',
    `create_time`             datetime                        NOT NULL COMMENT '创建时间',
    `creator`                 varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '创建人',
    `updater`                 varchar(50) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '更新人',
    `update_time`             datetime                         DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `resource_ids`            varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客户端所能访问的资源id集合,多个资源时用逗号(,)分隔',
    `client_secret`           varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用于指定客户端(client)的访问密匙',
    `scope`                   varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '指定客户端申请的权限范围,可选值包括read,write,trust;',
    `authorized_grant_types`  varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '指定客户端支持的grant_type',
    `web_server_redirect_uri` varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客户端的重定向URI,可为空',
    `authorities`             varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '指定客户端所拥有的权限值',
    `access_token_validity`   int(8) DEFAULT NULL COMMENT '设定客户端的access_token的有效时间值(单位:秒)',
    `refresh_token_validity`  int(8) DEFAULT NULL COMMENT '设定客户端的refresh_token的有效时间值(单位:秒)',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='认证客户端信息表';



DROP TABLE IF EXISTS `system_menu`;
CREATE TABLE `system_menu`
(
    `id`          varchar(36) COLLATE utf8mb4_bin  NOT NULL COMMENT '资源id',
    `name`           varchar(50) NOT NULL COMMENT '菜单名称',
    `permission`     varchar(100)         DEFAULT '' COMMENT '权限标识',
    `type`           smallint    NOT NULL COMMENT '菜单类型',
    `sort`           int         NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `parent_id`      bigint      NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    `path`           varchar(200)         DEFAULT '' COMMENT '路由地址',
    `icon`           varchar(100)         DEFAULT '#' COMMENT '菜单图标',
    `component`      varchar(255)         DEFAULT NULL COMMENT '组件路径',
    `component_name` varchar(255)         DEFAULT NULL COMMENT '组件名',
    `status`         smallint    NOT NULL DEFAULT 0 COMMENT '菜单状态',
    `visible`        tinyint     NOT NULL DEFAULT 1 COMMENT '是否可见（1:是，0:否）',
    `keep_alive`     tinyint     NOT NULL DEFAULT 1 COMMENT '是否缓存（1:是，0:否）',
    `always_show`    tinyint     NOT NULL DEFAULT 1 COMMENT '是否总是显示（1:是，0:否）',
    `creator`        varchar(64)          DEFAULT '' COMMENT '创建者',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(64)          DEFAULT '' COMMENT '更新者',
    `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        smallint    NOT NULL DEFAULT 0 COMMENT '是否删除（0:未删除，1:已删除）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

/*Table structure for table `t_role` */

DROP TABLE IF EXISTS `t_role`;

CREATE TABLE `t_role`
(
    `id`          varchar(36) COLLATE utf8mb4_bin  NOT NULL COMMENT '角色id',
    `create_time` datetime                         NOT NULL COMMENT '创建时间',
    `creator`     varchar(50) COLLATE utf8mb4_bin  NOT NULL COMMENT '创建人',
    `updater`     varchar(50) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '更新人',
    `update_time` datetime                         DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `role_code`   varchar(50) COLLATE utf8mb4_bin  NOT NULL COMMENT '角色编码',
    `role_name`   varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
    `status`      int(2) NOT NULL COMMENT '角色状态',
    `remark`      varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
    `tenant_id`   varchar(36) COLLATE utf8mb4_bin  NOT NULL COMMENT '租户编码',
    `client_id`   varchar(36) COLLATE utf8mb4_bin  NOT NULL COMMENT '角色所属客户端应用',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色表';

/*Table structure for table `t_role_account` */

DROP TABLE IF EXISTS `t_role_account`;

CREATE TABLE `t_role_account`
(
    `id`               varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '主键',
    `create_time`      datetime                        NOT NULL COMMENT '创建时间',
    `creator`          varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '创建人',
    `updater`          varchar(50) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '更新人',
    `update_time`      datetime                         DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `login_account_id` varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '登陆账号id',
    `role_id`          varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '角色id',
    `remark`           varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
    `tenant_id`        varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '租户编码',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_role_id_account_id_01` (`role_id`,`login_account_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户角色表';

/*Table structure for table `t_role_resource` */

DROP TABLE IF EXISTS `t_role_resource`;

CREATE TABLE `t_role_resource`
(
    `id`          varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '主键',
    `create_time` datetime                        NOT NULL COMMENT '创建时间',
    `creator`     varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '创建人',
    `updater`     varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人',
    `update_time` datetime                        DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `role_id`     varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '角色id',
    `resource_id` varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '资源id',
    `tenant_id`   varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '租户编码',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色所属的资源';

