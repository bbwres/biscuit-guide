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
import cn.bbwres.biscuit.module.auth.entity.RoleAccountEntity;
import cn.bbwres.biscuit.module.auth.convert.RoleAccountConvert;
import cn.bbwres.biscuit.module.auth.service.RoleAccountService;

/**
 * <p>
 * 用户角色表 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Tag(name = " 用户角色表")
@RequiredArgsConstructor(onConstructor_={@Autowired})
@RestController
@RequestMapping("/roleAccount")
public class RoleAccountController {
    private final  RoleAccountService roleAccountService;


    /**
      * 分页参数信息
      * @param pageVO 分页参数
      * @return Result
      */
    @PostMapping("/page")
    @Operation(summary = "获得用户角色表分页")
    public Result<Page<RoleAccountRespVO,RoleAccountPageReqVO>> getRoleAccountPage(@Validated @RequestBody Page<RoleAccountEntity,RoleAccountPageReqVO> pageVO) {
        Page<RoleAccountEntity,RoleAccountPageReqVO> pageResult = roleAccountService.getRoleAccountPage(pageVO);
        return Result.success(RoleAccountConvert.INSTANCE.convertPage(pageResult));
    }

    /**
    * 根据id获取数据
    *
    * @return Result
    */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取标签定义的数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<RoleAccountRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(RoleAccountConvert.INSTANCE.convert(roleAccountService.getRoleAccount(entityId)));
    }





}
