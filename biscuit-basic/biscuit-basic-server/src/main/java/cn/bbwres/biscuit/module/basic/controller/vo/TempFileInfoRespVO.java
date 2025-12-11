package cn.bbwres.biscuit.module.basic.controller.vo;

import lombok.*;
import lombok.experimental.Accessors;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.io.Serial;

/**
* <p>
* 临时文件表 Response VO
* </p>
*
* @author zlf
* @since 2025-12-11
*/
@Schema(description = " 临时文件表 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class TempFileInfoRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 文件唯一标识ID
    */
    @Schema(description = "文件唯一标识ID")
    private String id;

    /**
    * 创建时间
    */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
    * 创建人
    */
    @Schema(description = "创建人")
    private String creator;

    /**
    * 创建人名称
    */
    @Schema(description = "创建人名称")
    private String creatorName;

    /**
    * 更新人
    */
    @Schema(description = "更新人")
    private String updater;

    /**
    * 更新人名称
    */
    @Schema(description = "更新人名称")
    private String updaterName;

    /**
    * 更新时间
    */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
    * 租户编码
    */
    @Schema(description = "租户编码")
    private String tenantId;

    /**
    * 文件名称
    */
    @Schema(description = "文件名称")
    private String fileName;

    /**
    * 文件后缀
    */
    @Schema(description = "文件后缀")
    private String fileSuffix;

    /**
    * 文件大小（字节）
    */
    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    /**
    * 文件路径
    */
    @Schema(description = "文件路径")
    private String filePath;

    /**
    * 文件存储目录
    */
    @Schema(description = "文件存储目录")
    private String fileStorageMenu;

    /**
    * 文件存储类型
    */
    @Schema(description = "文件存储类型")
    private String fileStorageType;

    /**
    * 文件hash值
    */
    @Schema(description = "文件hash值")
    private String fileHash;

    /**
    * 扩展字段1
    */
    @Schema(description = "扩展字段1")
    private String ext1;

    /**
    * 扩展字段2
    */
    @Schema(description = "扩展字段2")
    private String ext2;

    /**
    * 扩展字段3
    */
    @Schema(description = "扩展字段3")
    private String ext3;

    /**
    * 扩展字段4
    */
    @Schema(description = "扩展字段4")
    private String ext4;




}
