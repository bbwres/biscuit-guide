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
import cn.bbwres.biscuit.module.basic.entity.SystemDictTypeEntity;
import cn.bbwres.biscuit.module.basic.convert.SystemDictTypeConvert;
import cn.bbwres.biscuit.module.basic.service.SystemDictTypeService;

/**
 * <p>
 * 字典类型表 controller
 * </p>
 *
 * @author zlf
 * @since 2025-11-26
 */
@Tag(name = " 字典类型表")
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RestController
@RequestMapping("/systemDictType")
public class SystemDictTypeController {
    private final  SystemDictTypeService systemDictTypeService;


    /**
      * 分页参数信息
      * @param pageVO 分页参数
      * @return Result
      */
    @PostMapping("/page")
    @Operation(summary = "获得字典类型表分页")
    public Result<Page<SystemDictTypeRespVO,SystemDictTypePageReqVO>> getSystemDictTypePage(@Validated @RequestBody Page<SystemDictTypeEntity,SystemDictTypePageReqVO> pageVO) {
        Page<SystemDictTypeEntity,SystemDictTypePageReqVO> pageResult = systemDictTypeService.getSystemDictTypePage(pageVO);
        return Result.success(SystemDictTypeConvert.INSTANCE.convertPage(pageResult));
    }

    /**
    * 根据id获取数据
    *
    * @return Result
    */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取字典类型表详情数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<SystemDictTypeRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(SystemDictTypeConvert.INSTANCE.convert(systemDictTypeService.getSystemDictType(entityId)));
    }





}
