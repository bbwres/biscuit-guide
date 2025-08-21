create table t_login_account
(
    id                        varchar(36)  not null
        primary key,
    login_name                varchar(36)  not null,
    login_password            varchar(256) not null,
    phone                     varchar(256) default '':: character varying,
    create_time               timestamp    not null,
    creator                   varchar(50)  not null,
    updater                   varchar(50)  default NULL:: character varying,
    update_time               timestamp,
    tenant_id                 varchar(36)  not null,
    user_id                   varchar(36)  default NULL:: character varying,
    status                    varchar(20)  default '0':: character varying not null,
    locked_time               timestamp,
    name                      varchar(100) default '':: character varying,
    last_update_password_time timestamp    not null
);

comment
on table  t_login_account is '登陆账户表';

comment
on column  t_login_account.id is '主键';

comment
on column  t_login_account.login_name is '登陆账号';

comment
on column  t_login_account.login_password is '登陆密码';

comment
on column  t_login_account.phone is '手机号';

comment
on column  t_login_account.create_time is '创建时间';

comment
on column  t_login_account.creator is '创建人';

comment
on column  t_login_account.updater is '更新人';

comment
on column  t_login_account.update_time is '更新时间';

comment
on column  t_login_account.tenant_id is '租户编码';

comment
on column  t_login_account.user_id is '关联用户表id';

comment
on column  t_login_account.status is '登陆用户状态';

comment
on column  t_login_account.locked_time is '锁定到期时间';

comment
on column  t_login_account.name is '姓名';

comment
on column  t_login_account.last_update_password_time is '最后一次修改密码时间';

alter table t_login_account
    owner to postgresql;

create unique index idx_un_account_loginname
    on t_login_account (login_name);

create index idx_account_user_id
    on t_login_account (user_id);

create table t_oauth_client_details
(
    id                      varchar(36) not null
        primary key,
    create_time             timestamp   not null,
    creator                 varchar(50) not null,
    updater                 varchar(50)  default NULL:: character varying,
    update_time             timestamp,
    resource_ids            varchar(256) default NULL:: character varying,
    client_secret           varchar(256) default NULL:: character varying,
    scope                   varchar(256) default NULL:: character varying,
    authorized_grant_types  varchar(256) default NULL:: character varying,
    web_server_redirect_uri varchar(256) default NULL:: character varying,
    authorities             varchar(256) default NULL:: character varying,
    access_token_validity   integer,
    refresh_token_validity  integer,
    tenant_id               varchar(36) not null
);

comment
on table  t_oauth_client_details is '认证客户端信息表';

comment
on column  t_oauth_client_details.id is '客户端id';

comment
on column  t_oauth_client_details.create_time is '创建时间';

comment
on column  t_oauth_client_details.creator is '创建人';

comment
on column  t_oauth_client_details.updater is '更新人';

comment
on column  t_oauth_client_details.update_time is '更新时间';

comment
on column  t_oauth_client_details.resource_ids is '客户端所能访问的资源id集合,多个资源时用逗号(,)分隔';

comment
on column  t_oauth_client_details.client_secret is '用于指定客户端(client)的访问密匙';

comment
on column  t_oauth_client_details.scope is '指定客户端申请的权限范围,可选值包括read,write,trust;';

comment
on column  t_oauth_client_details.authorized_grant_types is '指定客户端支持的grant_type';

comment
on column  t_oauth_client_details.web_server_redirect_uri is '客户端的重定向URI,可为空';

comment
on column  t_oauth_client_details.authorities is '指定客户端所拥有的权限值';

comment
on column  t_oauth_client_details.access_token_validity is '设定客户端的access_token的有效时间值(单位:秒)';

comment
on column  t_oauth_client_details.refresh_token_validity is '设定客户端的refresh_token的有效时间值(单位:秒)';

comment
on column  t_oauth_client_details.tenant_id is '租户编码';

alter table t_oauth_client_details
    owner to postgresql;

create table t_menu
(
    id             varchar(36)            not null
        primary key,
    name           varchar(50)            not null,
    menu_type      varchar(20)            not null,
    menu_sort      integer      default 0 not null,
    parent_id      varchar(36)  default '0':: character varying not null,
    path           varchar(200) default '':: character varying,
    icon           varchar(100) default '#':: character varying,
    component      varchar(255) default NULL:: character varying,
    component_name varchar(255) default NULL:: character varying,
    status         varchar(20)  default '0':: character varying not null,
    visible        smallint     default 1 not null,
    keep_alive     smallint     default 1 not null,
    always_show    smallint     default 1 not null,
    create_time    timestamp              not null,
    creator        varchar(50)            not null,
    updater        varchar(50)  default NULL:: character varying,
    update_time    timestamp,
    tenant_id      varchar(36)            not null
);

comment
on table  t_menu is '菜单权限表';

comment
on column  t_menu.id is '菜单id';

comment
on column  t_menu.name is '菜单名称';

comment
on column  t_menu.menu_type is '菜单类型，目录、菜单、按钮';

comment
on column  t_menu.menu_sort is '显示顺序';

comment
on column  t_menu.parent_id is '父菜单ID';

comment
on column  t_menu.path is '路由地址';

comment
on column  t_menu.icon is '菜单图标';

comment
on column  t_menu.component is '组件路径';

comment
on column  t_menu.component_name is '组件名';

comment
on column  t_menu.status is '菜单状态';

comment
on column  t_menu.visible is '是否可见（1:是，0:否）';

comment
on column  t_menu.keep_alive is '是否缓存（1:是，0:否）';

comment
on column  t_menu.always_show is '是否总是显示（1:是，0:否）';

comment
on column  t_menu.create_time is '创建时间';

comment
on column  t_menu.creator is '创建人';

comment
on column  t_menu.updater is '更新人';

comment
on column  t_menu.update_time is '更新时间';

comment
on column  t_menu.tenant_id is '租户编码';

alter table t_menu
    owner to postgresql;

create index idx_menu_parent_id_001
    on t_menu (parent_id);

create table t_menu_resource
(
    id          varchar(36) not null
        primary key,
    menu_id     varchar(36),
    create_time timestamp   not null,
    creator     varchar(50) not null,
    updater     varchar(50) default NULL:: character varying,
    update_time timestamp,
    tenant_id   varchar(36) not null,
    url_method  varchar(36),
    url_path    varchar(256),
    match_type  varchar(20),
    auth_type   varchar(20) not null,
    description varchar(512)
);

comment
on table  t_menu_resource is '菜单资源表';

comment
on column  t_menu_resource.id is 'id';

comment
on column  t_menu_resource.menu_id is '菜单id';

comment
on column  t_menu_resource.create_time is '创建时间';

comment
on column  t_menu_resource.creator is '创建人';

comment
on column  t_menu_resource.updater is '更新人';

comment
on column  t_menu_resource.update_time is '更新时间';

comment
on column  t_menu_resource.tenant_id is '租户编码';

comment
on column  t_menu_resource.url_method is '请求方法';

comment
on column  t_menu_resource.url_path is '请求地址';

comment
on column  t_menu_resource.match_type is '匹配类型,ANT ,REGX';

comment
on column  t_menu_resource.auth_type is '资源鉴权类型,无需鉴权、登录鉴权、完整鉴权';

comment
on column  t_menu_resource.description is '资源描述';

alter table t_menu_resource
    owner to postgresql;

create index idx_menu_resource_menu_id_001
    on t_menu_resource (menu_id, tenant_id);

create table t_role
(
    id          varchar(36)  not null
        primary key,
    create_time timestamp    not null,
    creator     varchar(50)  not null,
    updater     varchar(50)  default NULL:: character varying,
    update_time timestamp,
    role_code   varchar(50)  not null,
    role_name   varchar(100) not null,
    status      smallint     not null,
    remark      varchar(256) default NULL:: character varying,
    tenant_id   varchar(36)  not null,
    client_id   varchar(36)  not null
);

comment
on table  t_role is '角色表';

comment
on column  t_role.id is '角色id';

comment
on column  t_role.create_time is '创建时间';

comment
on column  t_role.creator is '创建人';

comment
on column  t_role.updater is '更新人';

comment
on column  t_role.update_time is '更新时间';

comment
on column  t_role.role_code is '角色编码';

comment
on column  t_role.role_name is '角色名称';

comment
on column  t_role.status is '角色状态';

comment
on column  t_role.remark is '备注';

comment
on column  t_role.tenant_id is '租户编码';

comment
on column  t_role.client_id is '角色所属客户端应用';

alter table t_role
    owner to postgresql;

create index idx_role_code_001
    on t_role (role_code);

create table t_role_account
(
    id               varchar(36) not null
        primary key,
    create_time      timestamp   not null,
    creator          varchar(50) not null,
    updater          varchar(50)  default NULL:: character varying,
    update_time      timestamp,
    login_account_id varchar(36) not null,
    role_id          varchar(36) not null,
    remark           varchar(256) default NULL:: character varying,
    tenant_id        varchar(36) not null
);

comment
on table  t_role_account is '用户角色表';

comment
on column  t_role_account.id is '主键';

comment
on column  t_role_account.create_time is '创建时间';

comment
on column  t_role_account.creator is '创建人';

comment
on column  t_role_account.updater is '更新人';

comment
on column  t_role_account.update_time is '更新时间';

comment
on column  t_role_account.login_account_id is '登陆账号id';

comment
on column  t_role_account.role_id is '角色id';

comment
on column  t_role_account.remark is '备注';

comment
on column  t_role_account.tenant_id is '租户编码';

alter table t_role_account
    owner to postgresql;

create unique index idx_role_id_account_id_01
    on t_role_account (role_id, login_account_id);

create table t_role_menu
(
    id          varchar(36) not null
        primary key,
    create_time timestamp   not null,
    creator     varchar(50) not null,
    updater     varchar(50) default NULL:: character varying,
    update_time timestamp,
    role_id     varchar(36) not null,
    menu_id     varchar(36) not null,
    tenant_id   varchar(36) not null
);

comment
on table  t_role_menu is '角色所属的资源';

comment
on column  t_role_menu.id is '主键';

comment
on column  t_role_menu.create_time is '创建时间';

comment
on column  t_role_menu.creator is '创建人';

comment
on column  t_role_menu.updater is '更新人';

comment
on column  t_role_menu.update_time is '更新时间';

comment
on column  t_role_menu.role_id is '角色id';

comment
on column  t_role_menu.menu_id is '菜单id';

comment
on column  t_role_menu.tenant_id is '租户编码';

alter table t_role_menu
    owner to postgresql;

create index idx_rile_menu_menu_id_role_id_001
    on t_role_menu (role_id, menu_id);

