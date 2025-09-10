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
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.convert.RoleConvert;
import cn.bbwres.biscuit.module.auth.service.RoleService;

/**
 * <p>
 * 角色表 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Tag(name = " 角色表")
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RestController
@RequestMapping("/role")
public class RoleController {
    private final  RoleService roleService;


    /**
      * 分页参数信息
      * @param pageVO 分页参数
      * @return Result
      */
    @PostMapping("/page")
    @Operation(summary = "获得角色表分页")
    public Result<Page<RoleRespVO,RolePageReqVO>> getRolePage(@Validated @RequestBody Page<RoleEntity,RolePageReqVO> pageVO) {
        Page<RoleEntity,RolePageReqVO> pageResult = roleService.getRolePage(pageVO);
        return Result.success(RoleConvert.INSTANCE.convertPage(pageResult));
    }

    /**
    * 根据id获取数据
    *
    * @return Result
    */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取标签定义的数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<RoleRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(RoleConvert.INSTANCE.convert(roleService.getRole(entityId)));
    }





}
