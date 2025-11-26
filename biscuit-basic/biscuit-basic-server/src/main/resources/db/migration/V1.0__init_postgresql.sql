create table public.t_system_dict_type
(
    id           varchar(50)  not null
        constraint pk_t_system_dict_type
            primary key,
    dict_name    varchar(100) not null,
    dict_type    varchar(100) not null,
    status       smallint     not null,
    remark       varchar(500),
    creator      varchar(50)  not null,
    creator_name varchar(100),
    create_time  timestamp    not null,
    updater      varchar(64),
    updater_name varchar(100),
    update_time  timestamp
);

comment on table public.t_system_dict_type is '字典类型表';

comment on column public.t_system_dict_type.id is '字典主键';

comment on column public.t_system_dict_type.dict_name is '字典名称';

comment on column public.t_system_dict_type.dict_type is '字典类型';

comment on column public.t_system_dict_type.status is '状态（0正常 1停用）';

comment on column public.t_system_dict_type.remark is '备注';

comment on column public.t_system_dict_type.creator is '创建者id';

comment on column public.t_system_dict_type.creator_name is '创建者名称';

comment on column public.t_system_dict_type.create_time is '创建时间';

comment on column public.t_system_dict_type.updater is '更新者id';

comment on column public.t_system_dict_type.updater_name is '更新者名称';

comment on column public.t_system_dict_type.update_time is '更新时间';


