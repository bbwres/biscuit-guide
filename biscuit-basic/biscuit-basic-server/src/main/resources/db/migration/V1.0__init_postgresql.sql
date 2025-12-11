CREATE TABLE t_file_info
(
    -- 核心字段定义（无内联注释）
    id                VARCHAR(32)  NOT NULL
        primary key,
    create_time       timestamp    not null,
    creator           varchar(50)  not null,
    creator_name      varchar(50),
    updater           varchar(50) default NULL::character varying,
    updater_name      varchar(50) default NULL::character varying,
    update_time       timestamp,
    tenant_id         varchar(36)  not null,
    file_name         VARCHAR(255) NOT NULL,
    file_suffix       VARCHAR(32),
    file_size         BIGINT,
    business_id       VARCHAR(64),
    business_type     VARCHAR(64),
    file_path         VARCHAR(4096),
    file_storage_menu VARCHAR(512),
    file_storage_type VARCHAR(32),
    file_hash         VARCHAR(128),
    src_file_id       VARCHAR(64),
    ext1              VARCHAR(255),
    ext2              VARCHAR(255),
    ext3              VARCHAR(255),
    ext4              VARCHAR(255)
);


COMMENT
ON TABLE t_file_info IS '业务文件信息表';

COMMENT
ON COLUMN t_file_info.id IS '文件唯一标识ID';
COMMENT
ON COLUMN t_file_info.file_name IS '文件名称';
COMMENT
ON COLUMN t_file_info.file_suffix IS '文件后缀';
COMMENT
ON COLUMN t_file_info.file_size IS '文件大小（字节）';
COMMENT
ON COLUMN t_file_info.business_id IS '关联的业务ID';
COMMENT
ON COLUMN t_file_info.business_type IS '关联的业务类型';
COMMENT
ON COLUMN t_file_info.file_path IS '文件路径';
COMMENT
ON COLUMN t_file_info.file_storage_menu IS '文件存储目录';
COMMENT
ON COLUMN t_file_info.file_storage_type IS '文件存储类型';
COMMENT
ON COLUMN t_file_info.file_hash IS '文件hash值';
COMMENT
ON COLUMN t_file_info.src_file_id IS '关联引入的文件ID';
COMMENT
ON COLUMN t_file_info.ext1 IS '扩展字段1';
COMMENT
ON COLUMN t_file_info.ext2 IS '扩展字段2';
COMMENT
ON COLUMN t_file_info.ext3 IS '扩展字段3';
COMMENT
ON COLUMN t_file_info.ext4 IS '扩展字段4';


comment on column t_file_info.create_time is '创建时间';
comment on column t_file_info.creator is '创建人';
comment on column t_file_info.creator_name is '创建人名称';
comment on column t_file_info.updater is '更新人';
comment on column t_file_info.updater_name is '更新人名称';
comment on column t_file_info.update_time is '更新时间';
comment on column t_file_info.tenant_id is '租户编码';


-- 1. 业务维度查询索引（适配findFileInfoByBusiness接口）
CREATE INDEX idx_t_file_info_business ON t_file_info (business_type, business_id, tenant_id);
-- 2. 文件Hash索引（适配秒传checkFileHashExist接口）
CREATE INDEX idx_t_file_info_hash ON t_file_info (file_hash, tenant_id);
-- 3. 存储类型索引（适配按存储类型筛选文件）
CREATE INDEX idx_t_file_info_storage_type ON t_file_info (file_storage_type, tenant_id);





CREATE TABLE t_temp_file_info
(
    -- 核心字段定义（无内联注释）
    id                VARCHAR(32)  NOT NULL
        primary key,
    create_time       timestamp    not null,
    creator           varchar(50)  not null,
    creator_name      varchar(50),
    updater           varchar(50) default NULL::character varying,
    updater_name      varchar(50) default NULL::character varying,
    update_time       timestamp,
    tenant_id         varchar(36)  not null,
    file_name         VARCHAR(255) NOT NULL,
    file_suffix       VARCHAR(32),
    file_size         BIGINT,
    file_path         VARCHAR(4096),
    file_storage_menu VARCHAR(512),
    file_storage_type VARCHAR(32),
    file_hash         VARCHAR(128),
    ext1              VARCHAR(255),
    ext2              VARCHAR(255),
    ext3              VARCHAR(255),
    ext4              VARCHAR(255)
);


COMMENT
ON TABLE t_temp_file_info IS '临时文件表';

COMMENT
ON COLUMN t_temp_file_info.id IS '文件唯一标识ID';
COMMENT
ON COLUMN t_temp_file_info.file_name IS '文件名称';
COMMENT
ON COLUMN t_temp_file_info.file_suffix IS '文件后缀';
COMMENT
ON COLUMN t_temp_file_info.file_size IS '文件大小（字节）';

COMMENT
ON COLUMN t_temp_file_info.file_path IS '文件路径';
COMMENT
ON COLUMN t_temp_file_info.file_storage_menu IS '文件存储目录';
COMMENT
ON COLUMN t_temp_file_info.file_storage_type IS '文件存储类型';
COMMENT
ON COLUMN t_temp_file_info.file_hash IS '文件hash值';

COMMENT
ON COLUMN t_temp_file_info.ext1 IS '扩展字段1';
COMMENT
ON COLUMN t_temp_file_info.ext2 IS '扩展字段2';
COMMENT
ON COLUMN t_temp_file_info.ext3 IS '扩展字段3';
COMMENT
ON COLUMN t_temp_file_info.ext4 IS '扩展字段4';

comment on column t_temp_file_info.create_time is '创建时间';
comment on column t_temp_file_info.creator is '创建人';
comment on column t_temp_file_info.creator_name is '创建人名称';
comment on column t_temp_file_info.updater is '更新人';
comment on column t_temp_file_info.updater_name is '更新人名称';
comment on column t_temp_file_info.update_time is '更新时间';
comment on column t_temp_file_info.tenant_id is '租户编码';




CREATE TABLE t_file_business_info
(
    -- 核心字段定义（无内联注释）
    id                VARCHAR(32)  NOT NULL
        primary key,
    create_time       timestamp    not null,
    creator           varchar(50)  not null,
    creator_name      varchar(50),
    updater           varchar(50) default NULL::character varying,
    updater_name      varchar(50) default NULL::character varying,
    update_time       timestamp,
    tenant_id         varchar(36)  not null,
    business_type         VARCHAR(64) NOT NULL,
    business_name VARCHAR(512),
    need_auth          varchar(36),
    module_name       VARCHAR(32),
    auth_path         VARCHAR(4096)
);

COMMENT ON TABLE t_file_business_info IS '业务配置表';
comment on column t_file_business_info.create_time is '创建时间';
comment on column t_file_business_info.creator is '创建人';
comment on column t_file_business_info.creator_name is '创建人名称';
comment on column t_file_business_info.updater is '更新人';
comment on column t_file_business_info.updater_name is '更新人名称';
comment on column t_file_business_info.update_time is '更新时间';
comment on column t_file_business_info.tenant_id is '租户编码';
comment on column t_file_business_info.business_type is '业务类型';
comment on column t_file_business_info.business_name is '业务名称';
comment on column t_file_business_info.need_auth is '是否需要鉴权';
comment on column t_file_business_info.module_name is '业务所属模块';
comment on column t_file_business_info.auth_path is '鉴权请求路径';








