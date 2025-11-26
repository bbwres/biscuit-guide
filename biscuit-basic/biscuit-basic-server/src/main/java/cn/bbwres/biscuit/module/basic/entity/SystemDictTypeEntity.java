package cn.bbwres.biscuit.module.basic.entity;

import cn.bbwres.biscuit.entity.BaseEntity;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


import java.io.Serial;

/**
*
* 字典类型表
*
* @author zlf
* @since 2025-11-26
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table("t_system_dict_type")
public class SystemDictTypeEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 字典主键
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;


    /**
     * 字典名称
     */
    @Column(value = "dict_name")
    private String dictName;


    /**
     * 字典类型
     */
    @Column(value = "dict_type")
    private String dictType;


    /**
     * 状态（0正常 1停用）
     */
    @Column(value = "status")
    private DataStatusEnum status;


    /**
     * 备注
     */
    @Column(value = "remark")
    private String remark;










}