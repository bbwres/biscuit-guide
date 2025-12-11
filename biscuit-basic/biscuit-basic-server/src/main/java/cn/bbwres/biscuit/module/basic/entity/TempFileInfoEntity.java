package cn.bbwres.biscuit.module.basic.entity;

import cn.bbwres.biscuit.entity.BaseNameTenantEntity;
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
 * 临时文件表
 *
 * @author zlf
 * @since 2025-12-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_temp_file_info")
public class TempFileInfoEntity extends BaseNameTenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 文件唯一标识ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;


    /**
     * 文件后缀
     */
    @TableField("file_suffix")
    private String fileSuffix;


    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;


    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;


    /**
     * 文件存储目录
     */
    @TableField("file_storage_menu")
    private String fileStorageMenu;


    /**
     * 文件存储类型
     */
    @TableField("file_storage_type")
    private String fileStorageType;


    /**
     * 文件hash值
     */
    @TableField("file_hash")
    private String fileHash;


    /**
     * 扩展字段1
     */
    @TableField("ext1")
    private String ext1;


    /**
     * 扩展字段2
     */
    @TableField("ext2")
    private String ext2;


    /**
     * 扩展字段3
     */
    @TableField("ext3")
    private String ext3;


    /**
     * 扩展字段4
     */
    @TableField("ext4")
    private String ext4;


}