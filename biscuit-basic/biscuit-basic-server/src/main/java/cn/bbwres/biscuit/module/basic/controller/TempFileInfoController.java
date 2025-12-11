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
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import cn.bbwres.biscuit.module.basic.convert.TempFileInfoConvert;
import cn.bbwres.biscuit.module.basic.service.TempFileInfoService;

/**
 * <p>
 * 临时文件表 controller
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Tag(name = " 临时文件表")
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RestController
@RequestMapping("/tempFileInfo")
public class TempFileInfoController {
    private final  TempFileInfoService tempFileInfoService;


    /**
      * 分页参数信息
      * @param pageVO 分页参数
      * @return Result
      */
    @PostMapping("/page")
    @Operation(summary = "获得临时文件表分页")
    public Result<Page<TempFileInfoRespVO,TempFileInfoPageReqVO>> getTempFileInfoPage(@Validated @RequestBody Page<TempFileInfoEntity,TempFileInfoPageReqVO> pageVO) {
        Page<TempFileInfoEntity,TempFileInfoPageReqVO> pageResult = tempFileInfoService.getTempFileInfoPage(pageVO);
        return Result.success(TempFileInfoConvert.INSTANCE.convertPage(pageResult));
    }

    /**
    * 根据id获取数据
    *
    * @return Result
    */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取临时文件表详情数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<TempFileInfoRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(TempFileInfoConvert.INSTANCE.convert(tempFileInfoService.getTempFileInfo(entityId)));
    }





}
