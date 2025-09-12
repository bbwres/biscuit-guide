package cn.bbwres.biscuit.module.auth.controller;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsAddOrUpdateReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsRespVO;
import cn.bbwres.biscuit.module.auth.convert.OauthClientDetailsConvert;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.module.auth.service.OauthClientDetailsService;
import cn.bbwres.biscuit.module.auth.service.cache.OauthClientDetailsCacheService;
import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidateEditGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 认证客户端信息表 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Tag(name = " 认证客户端信息表")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/oauthClientDetails")
public class OauthClientDetailsController {
    private final OauthClientDetailsService oauthClientDetailsService;

    private final OauthClientDetailsCacheService oauthClientDetailsCacheService;


    /**
     * 分页参数信息
     *
     * @param pageVO 分页参数
     * @return Result
     */
    @PostMapping("/page")
    @Operation(summary = "获得认证客户端信息表分页")
    public Result<Page<OauthClientDetailsRespVO, OauthClientDetailsPageReqVO>> getOauthClientDetailsPage(@Validated @RequestBody Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> pageVO) {
        Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> pageResult = oauthClientDetailsService.getOauthClientDetailsPage(pageVO);
        return Result.success(OauthClientDetailsConvert.INSTANCE.convertPage(pageResult));
    }

    /**
     * 根据id获取数据
     *
     * @return Result
     */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取标签定义的数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<OauthClientDetailsRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(OauthClientDetailsConvert.INSTANCE.convert(oauthClientDetailsService.getOauthClientDetails(entityId)));
    }


    /**
     * 新增客户端配置
     *
     * @return Result
     */
    @PostMapping("/add")
    @Operation(summary = "新增客户端配置")
    public Result<Void> add(@Validated(ValidateAddGroup.class) @RequestBody OauthClientDetailsAddOrUpdateReqVO req) {
        oauthClientDetailsService.save(OauthClientDetailsConvert.INSTANCE.covertAddOrUpdateReq(req));
        return Result.success(null);
    }

    /**
     * 修改客户端配置
     *
     * @return Result
     */
    @PostMapping("/edit")
    @Operation(summary = "修改客户端配置")
    public Result<Void> edit(@Validated(ValidateEditGroup.class) @RequestBody OauthClientDetailsAddOrUpdateReqVO req) {
        oauthClientDetailsCacheService.updateById(OauthClientDetailsConvert.INSTANCE.covertAddOrUpdateReq(req));
        return Result.success(null);
    }


}
