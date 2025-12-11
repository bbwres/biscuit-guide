package cn.bbwres.biscuit.module.basic.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import cn.bbwres.biscuit.exception.constants.GlobalErrorCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import cn.bbwres.biscuit.module.basic.controller.vo.*;
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
import cn.bbwres.biscuit.module.basic.convert.FileBusinessInfoConvert;
import cn.bbwres.biscuit.module.basic.service.FileBusinessInfoService;

/**
 * <p>
 * 业务配置表 controller
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Tag(name = " 文件业务配置表")
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RestController
@RequestMapping("/fileBusinessInfo")
public class FileBusinessInfoController {
    private final  FileBusinessInfoService fileBusinessInfoService;


    /**
      * 分页参数信息
      * @param pageVO 分页参数
      * @return Result
      */
    @PostMapping("/page")
    @Operation(summary = "获得业务配置表分页")
    public Result<Page<FileBusinessInfoRespVO,FileBusinessInfoPageReqVO>> getFileBusinessInfoPage(@Validated @RequestBody Page<FileBusinessInfoEntity,FileBusinessInfoPageReqVO> pageVO) {
        Page<FileBusinessInfoEntity,FileBusinessInfoPageReqVO> pageResult = fileBusinessInfoService.getFileBusinessInfoPage(pageVO);
        return Result.success(FileBusinessInfoConvert.INSTANCE.convertPage(pageResult));
    }

    /**
    * 根据id获取数据
    *
    * @return Result
    */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取业务配置表详情数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<FileBusinessInfoRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(FileBusinessInfoConvert.INSTANCE.convert(fileBusinessInfoService.getFileBusinessInfo(entityId)));
    }





}
