package cn.bbwres.biscuit.module.basic.api.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 业务文件信息表 Response VO
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class FileInfoResultVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识ID
     */
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 关联的业务ID
     */
    private String businessId;

    /**
     * 关联的业务类型
     */
    private String businessType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件存储目录
     */
    private String fileStorageMenu;

    /**
     * 文件存储类型
     */
    private String fileStorageType;

    /**
     * 文件hash值
     */
    private String fileHash;

    /**
     * 关联引入的文件ID
     */
    private String srcFileId;

    /**
     * 扩展字段1
     */
    private String ext1;

    /**
     * 扩展字段2
     */
    private String ext2;

    /**
     * 扩展字段3
     */
    private String ext3;

    /**
     * 扩展字段4
     */
    private String ext4;


}
