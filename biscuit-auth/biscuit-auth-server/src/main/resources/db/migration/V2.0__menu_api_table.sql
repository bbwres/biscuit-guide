-- 菜单接口关联表：将 t_menu 中的 api_url / api_url_method 拆出为一对多

create table public.t_menu_api
(
    id             varchar(36)  not null
        primary key,
    menu_id        varchar(36)  not null,
    api_url        varchar(255) not null,
    api_url_method varchar(50),
    create_time    timestamp    not null,
    creator        varchar(50)  not null,
    updater        varchar(50) default NULL::character varying,
    update_time    timestamp,
    tenant_id      varchar(36)  not null
);

comment on table public.t_menu_api is '菜单接口关联表';

comment on column public.t_menu_api.id is '主键';

comment on column public.t_menu_api.menu_id is '菜单id';

comment on column public.t_menu_api.api_url is '请求接口地址';

comment on column public.t_menu_api.api_url_method is '请求接口方法';

comment on column public.t_menu_api.create_time is '创建时间';

comment on column public.t_menu_api.creator is '创建人';

comment on column public.t_menu_api.updater is '更新人';

comment on column public.t_menu_api.update_time is '更新时间';

comment on column public.t_menu_api.tenant_id is '租户编码';


create index idx_menu_api_menu_id_001
    on public.t_menu_api (menu_id);


-- 数据迁移：将 t_menu 中已有的 api_url 数据迁入新表
insert into t_menu_api (id, menu_id, api_url, api_url_method, create_time, creator, tenant_id)
select md5(random()::text || clock_timestamp()::text || id),
       id,
       api_url,
       api_url_method,
       coalesce(create_time, now()),
       coalesce(creator, 'system'),
       tenant_id
from t_menu
where api_url is not null
  and api_url <> '';

alter table public.t_menu_api
    add creator_name varchar(100);

comment on column public.t_menu_api.creator_name is '创建人姓名';

alter table public.t_menu_api
    add updater_name varchar(100);

comment on column public.t_menu_api.updater_name is '更新人姓名';


-- 数据迁移完成后删除 t_menu 旧字段，不再兼容
alter table public.t_menu drop column api_url;
alter table public.t_menu drop column api_url_method;

