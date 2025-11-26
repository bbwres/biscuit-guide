create table public.t_login_account
(
    id                        varchar(36)                                 not null
        primary key,
    login_name                varchar(36)                                 not null,
    login_password            varchar(256)                                not null,
    phone                     varchar(256) default ''::character varying,
    create_time               timestamp                                   not null,
    creator                   varchar(50)                                 not null,
    updater                   varchar(50)  default NULL::character varying,
    update_time               timestamp,
    tenant_id                 varchar(36)                                 not null,
    user_id                   varchar(36)  default NULL::character varying,
    status                    varchar(20)  default '0'::character varying not null,
    locked_time               timestamp,
    name                      varchar(100) default ''::character varying,
    last_update_password_time timestamp                                   not null
);

comment on table public.t_login_account is '登陆账户表';

comment on column public.t_login_account.id is '主键';

comment on column public.t_login_account.login_name is '登陆账号';

comment on column public.t_login_account.login_password is '登陆密码';

comment on column public.t_login_account.phone is '手机号';

comment on column public.t_login_account.create_time is '创建时间';

comment on column public.t_login_account.creator is '创建人';

comment on column public.t_login_account.updater is '更新人';

comment on column public.t_login_account.update_time is '更新时间';

comment on column public.t_login_account.tenant_id is '租户编码';

comment on column public.t_login_account.user_id is '关联用户表id';

comment on column public.t_login_account.status is '登陆用户状态';

comment on column public.t_login_account.locked_time is '锁定到期时间';

comment on column public.t_login_account.name is '姓名';

comment on column public.t_login_account.last_update_password_time is '最后一次修改密码时间';



create unique index idx_un_account_loginname
    on public.t_login_account (login_name);

create index idx_account_user_id
    on public.t_login_account (user_id);

create table public.t_oauth_client_details
(
    id                            varchar(36)               not null
        primary key,
    create_time                   timestamp                 not null,
    creator                       varchar(50)               not null,
    updater                       varchar(50)  default NULL::character varying,
    update_time                   timestamp,
    client_secret                 varchar(256)              not null,
    client_authentication_methods varchar(256)              not null,
    scopes                        varchar(256)              not null,
    authorized_grant_types        varchar(256)              not null,
    web_server_redirect_uri       varchar(256) default NULL::character varying,
    post_logout_redirect_uri      varchar(256) default NULL::character varying,
    access_token_validity         integer                   not null,
    refresh_token_validity        integer                   not null,
    access_token_format           varchar(50)  default NULL::character varying,
    reuse_refresh_token           boolean      default true not null,
    single_user_login             boolean      default true not null
);

comment on table public.t_oauth_client_details is '认证客户端信息表';

comment on column public.t_oauth_client_details.id is '客户端id';

comment on column public.t_oauth_client_details.create_time is '创建时间';

comment on column public.t_oauth_client_details.creator is '创建人';

comment on column public.t_oauth_client_details.updater is '更新人';

comment on column public.t_oauth_client_details.update_time is '更新时间';

comment on column public.t_oauth_client_details.client_secret is '用于指定客户端(client)的访问密匙';

comment on column public.t_oauth_client_details.client_authentication_methods is '客户端支持的认证方式';

comment on column public.t_oauth_client_details.scopes is '指定客户端申请的权限范围,可选值包括read,write,trust;';

comment on column public.t_oauth_client_details.authorized_grant_types is '指定客户端支持的grant_type';

comment on column public.t_oauth_client_details.web_server_redirect_uri is '客户端的重定向URI,可为空';

comment on column public.t_oauth_client_details.post_logout_redirect_uri is 'postLogoutRedirectUri,可为空';

comment on column public.t_oauth_client_details.access_token_validity is '设定客户端的access_token的有效时间值(单位:秒)';

comment on column public.t_oauth_client_details.refresh_token_validity is '设定客户端的refresh_token的有效时间值(单位:秒)';

comment on column public.t_oauth_client_details.access_token_format is 'accessToken的类型，reference-不透明的token，self-contained-jwt类型的token';

comment on column public.t_oauth_client_details.reuse_refresh_token is '是否复用刷新令牌 为true则复用刷新令牌（refresh token），为false则签发新的刷新令牌。';

comment on column public.t_oauth_client_details.single_user_login is '  用户是否单一登录true则用户每次登录失效其他token，为false则允许用户同时登录多次';



create table public.t_role
(
    id          varchar(36)  not null
        primary key,
    create_time timestamp    not null,
    creator     varchar(50)  not null,
    updater     varchar(50)  default NULL::character varying,
    update_time timestamp,
    role_code   varchar(50)  not null,
    role_name   varchar(100) not null,
    status      smallint     not null,
    remark      varchar(256) default NULL::character varying,
    tenant_id   varchar(36)  not null,
    client_id   varchar(36)  not null
);

comment on table public.t_role is '角色表';

comment on column public.t_role.id is '角色id';

comment on column public.t_role.create_time is '创建时间';

comment on column public.t_role.creator is '创建人';

comment on column public.t_role.updater is '更新人';

comment on column public.t_role.update_time is '更新时间';

comment on column public.t_role.role_code is '角色编码';

comment on column public.t_role.role_name is '角色名称';

comment on column public.t_role.status is '角色状态';

comment on column public.t_role.remark is '备注';

comment on column public.t_role.tenant_id is '租户编码';

comment on column public.t_role.client_id is '角色所属客户端应用';


create unique index idx_role_code_001
    on public.t_role (role_code, client_id, tenant_id);

create table public.t_role_account
(
    id               varchar(36) not null
        primary key,
    create_time      timestamp   not null,
    creator          varchar(50) not null,
    updater          varchar(50)  default NULL::character varying,
    update_time      timestamp,
    login_account_id varchar(36) not null,
    role_id          varchar(36) not null,
    remark           varchar(256) default NULL::character varying,
    tenant_id        varchar(36) not null
);

comment on table public.t_role_account is '用户角色表';

comment on column public.t_role_account.id is '主键';

comment on column public.t_role_account.create_time is '创建时间';

comment on column public.t_role_account.creator is '创建人';

comment on column public.t_role_account.updater is '更新人';

comment on column public.t_role_account.update_time is '更新时间';

comment on column public.t_role_account.login_account_id is '登陆账号id';

comment on column public.t_role_account.role_id is '角色id';

comment on column public.t_role_account.remark is '备注';

comment on column public.t_role_account.tenant_id is '租户编码';



create unique index idx_role_id_account_id_01
    on public.t_role_account (login_account_id, role_id);

create table public.t_role_menu
(
    id          varchar(36) not null
        primary key,
    create_time timestamp   not null,
    creator     varchar(50) not null,
    updater     varchar(50) default NULL::character varying,
    update_time timestamp,
    role_id     varchar(36) not null,
    menu_id     varchar(36) not null,
    tenant_id   varchar(36) not null,
    client_id   varchar(36),
    role_code   varchar(50)
);

comment on table public.t_role_menu is '角色所属的资源';

comment on column public.t_role_menu.id is '主键';

comment on column public.t_role_menu.create_time is '创建时间';

comment on column public.t_role_menu.creator is '创建人';

comment on column public.t_role_menu.updater is '更新人';

comment on column public.t_role_menu.update_time is '更新时间';

comment on column public.t_role_menu.role_id is '角色id';

comment on column public.t_role_menu.menu_id is '菜单id';

comment on column public.t_role_menu.tenant_id is '租户编码';

comment on column public.t_role_menu.client_id is '角色所属客户端应用id';

comment on column public.t_role_menu.role_code is '角色编码';



create index idx_rile_menu_menu_id_role_id_001
    on public.t_role_menu (role_id, menu_id);

create index idx_role_menu_role_code_client_001
    on public.t_role_menu (role_code, client_id, tenant_id);

create table public.t_menu
(
    id             varchar(36)              not null
        primary key,
    name           varchar(50)              not null,
    menu_type      varchar(20)              not null,
    menu_sort      integer     default 0    not null,
    parent_id      varchar(36),
    icon           varchar(100),
    component      varchar(255),
    component_name varchar(255),
    status         smallint                 not null,
    visible        boolean     default true not null,
    keep_alive     boolean     default true not null,
    always_show    boolean     default true not null,
    api_url_method varchar(50),
    api_url        varchar(255),
    create_time    timestamp                not null,
    creator        varchar(50)              not null,
    updater        varchar(50) default NULL::character varying,
    update_time    timestamp,
    tenant_id      varchar(36)              not null,
    tree_path      varchar(1024)
);

comment on table public.t_menu is '菜单权限表';

comment on column public.t_menu.id is '菜单id';

comment on column public.t_menu.name is '菜单名称';

comment on column public.t_menu.menu_type is '菜单类型，目录、菜单、按钮';

comment on column public.t_menu.menu_sort is '显示顺序';

comment on column public.t_menu.parent_id is '父菜单ID';

comment on column public.t_menu.icon is '菜单图标';

comment on column public.t_menu.component is '组件路径';

comment on column public.t_menu.component_name is '组件名';

comment on column public.t_menu.status is '菜单状态';

comment on column public.t_menu.visible is '是否可见（true:是，false:否）';

comment on column public.t_menu.keep_alive is '是否缓存（true:是，false:否）';

comment on column public.t_menu.always_show is '是否总是显示（true:是，false:否）';

comment on column public.t_menu.api_url_method is '请求接口方法';

comment on column public.t_menu.api_url is '请求接口地址';

comment on column public.t_menu.create_time is '创建时间';

comment on column public.t_menu.creator is '创建人';

comment on column public.t_menu.updater is '更新人';

comment on column public.t_menu.update_time is '更新时间';

comment on column public.t_menu.tenant_id is '租户编码';

comment on column public.t_menu.tree_path is '树形路径';



create index idx_menu_parent_id_001
    on public.t_menu (parent_id);

create index idx_t_menu_tree_path_01
    on public.t_menu (tree_path);



alter table public.t_login_account add creator_name varchar(100);
comment on column public.t_login_account.creator_name is '创建者姓名';

alter table public.t_login_account add updater_name varchar(100);
comment on column public.t_login_account.creator_name is '更新者姓名';



alter table public.t_menu add creator_name varchar(100);
comment on column public.t_menu.creator_name is '创建者姓名';

alter table public.t_menu add updater_name varchar(100);
comment on column public.t_menu.creator_name is '更新者姓名';



alter table public.t_oauth_client_details add creator_name varchar(100);
comment on column public.t_oauth_client_details.creator_name is '创建者姓名';

alter table public.t_oauth_client_details add updater_name varchar(100);
comment on column public.t_oauth_client_details.creator_name is '更新者姓名';



alter table public.t_role add creator_name varchar(100);
comment on column public.t_role.creator_name is '创建者姓名';

alter table public.t_role add updater_name varchar(100);
comment on column public.t_role.creator_name is '更新者姓名';



alter table public.t_role_account add creator_name varchar(100);
comment on column public.t_role_account.creator_name is '创建者姓名';

alter table public.t_role_account add updater_name varchar(100);
comment on column public.t_role_account.creator_name is '更新者姓名';




alter table public.t_role_menu add creator_name varchar(100);
comment on column public.t_role_menu.creator_name is '创建者姓名';

alter table public.t_role_menu add updater_name varchar(100);
comment on column public.t_role_menu.creator_name is '更新者姓名';