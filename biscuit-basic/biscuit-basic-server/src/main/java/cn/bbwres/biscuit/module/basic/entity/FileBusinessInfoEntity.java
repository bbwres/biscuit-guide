package cn.bbwres.biscuit.module.basic.entity;

import cn.bbwres.biscuit.entity.BaseNameTenantEntity;
import cn.bbwres.biscuit.enums.YesOrNoEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 *
 * 业务配置表
 *
 * @author zlf
 * @since 2025-12-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_file_business_info")
public class FileBusinessInfoEntity extends BaseNameTenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;


    /**
     * 业务名称
     */
    @TableField("business_name")
    private String businessName;


    /**
     * 是否需要鉴权
     */
    @TableField("need_auth")
    private YesOrNoEnum needAuth;


    /**
     * 业务所属模块
     */
    @TableField("module_name")
    private String moduleName;


    /**
     * 鉴权请求路径
     */
    @TableField("auth_path")
    private String authPath;


}