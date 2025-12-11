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
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import cn.bbwres.biscuit.module.basic.convert.FileInfoConvert;
import cn.bbwres.biscuit.module.basic.service.FileInfoService;

/**
 * <p>
 * 业务文件信息表 controller
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Tag(name = " 业务文件信息表")
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RestController
@RequestMapping("/fileInfo")
public class FileInfoController {
    private final  FileInfoService fileInfoService;


    /**
      * 分页参数信息
      * @param pageVO 分页参数
      * @return Result
      */
    @PostMapping("/page")
    @Operation(summary = "获得业务文件信息表分页")
    public Result<Page<FileInfoRespVO,FileInfoPageReqVO>> getFileInfoPage(@Validated @RequestBody Page<FileInfoEntity,FileInfoPageReqVO> pageVO) {
        Page<FileInfoEntity,FileInfoPageReqVO> pageResult = fileInfoService.getFileInfoPage(pageVO);
        return Result.success(FileInfoConvert.INSTANCE.convertPage(pageResult));
    }

    /**
    * 根据id获取数据
    *
    * @return Result
    */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取业务文件信息表详情数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<FileInfoRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(FileInfoConvert.INSTANCE.convert(fileInfoService.getFileInfo(entityId)));
    }





}
