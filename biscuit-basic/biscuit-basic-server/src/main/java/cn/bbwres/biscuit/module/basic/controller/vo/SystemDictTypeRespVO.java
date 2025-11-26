package cn.bbwres.biscuit.module.basic.controller.vo;

import cn.bbwres.biscuit.enums.DataStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 字典类型表 Response VO
 * </p>
 *
 * @author zlf
 * @since 2025-11-26
 */
@Schema(description = " 字典类型表 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class SystemDictTypeRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    @Schema(description = "字典主键")
    private String id;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    private String dictName;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型")
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）")
    private DataStatusEnum status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建者id
     */
    @Schema(description = "创建者id")
    private String creator;

    /**
     * 创建者名称
     */
    @Schema(description = "创建者名称")
    private String creatorName;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新者id
     */
    @Schema(description = "更新者id")
    private String updater;

    /**
     * 更新者名称
     */
    @Schema(description = "更新者名称")
    private String updaterName;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;


}
