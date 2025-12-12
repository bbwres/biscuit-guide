package cn.bbwres.biscuit.module.basic.controller.vo;

import cn.bbwres.biscuit.enums.YesOrNoEnum;
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
 * 业务配置表 Response VO
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Schema(description = " 业务配置表 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class FileBusinessInfoRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String businessType;

    /**
     * 业务名称
     */
    @Schema(description = "业务名称")
    private String businessName;

    /**
     * 是否需要鉴权
     */
    @Schema(description = "是否需要鉴权")
    private YesOrNoEnum needAuth;

    /**
     * 业务所属模块
     */
    @Schema(description = "业务所属模块")
    private String moduleName;

    /**
     * 鉴权请求路径
     */
    @Schema(description = "鉴权请求路径")
    private String authPath;


}
