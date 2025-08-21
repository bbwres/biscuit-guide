package cn.bbwres.biscuit.module.auth.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import cn.bbwres.biscuit.module.auth.convert.OauthClientDetailsConvert;
import cn.bbwres.biscuit.module.auth.service.OauthClientDetailsService;

/**
 * <p>
 * 认证客户端信息表 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Tag(name = " 认证客户端信息表")
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RestController
@RequestMapping("/oauthClientDetails")
public class OauthClientDetailsController {
    private final  OauthClientDetailsService oauthClientDetailsService;


    /**
      * 分页参数信息
      * @param pageVO 分页参数
      * @return Result
      */
    @PostMapping("/page")
    @Operation(summary = "获得认证客户端信息表分页")
    public Result<Page<OauthClientDetailsRespVO,OauthClientDetailsPageReqVO>> getOauthClientDetailsPage(@Validated @RequestBody Page<OauthClientDetailsEntity,OauthClientDetailsPageReqVO> pageVO) {
        Page<OauthClientDetailsEntity,OauthClientDetailsPageReqVO> pageResult = oauthClientDetailsService.getOauthClientDetailsPage(pageVO);
        return new Result<>(GlobalErrorCodeConstants.SUCCESS.getCode(),GlobalErrorCodeConstants.SUCCESS.getMessage(),
                    OauthClientDetailsConvert.INSTANCE.convertPage(pageResult));
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
        return new Result<>(GlobalErrorCodeConstants.SUCCESS.getCode(), GlobalErrorCodeConstants.SUCCESS.getMessage(),
                    OauthClientDetailsConvert.INSTANCE.convert(oauthClientDetailsService.getOauthClientDetails(entityId)));
    }





}
