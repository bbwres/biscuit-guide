package cn.bbwres.biscuit.module.auth.controller;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleMenuPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleMenuRespVO;
import cn.bbwres.biscuit.module.auth.convert.RoleMenuConvert;
import cn.bbwres.biscuit.module.auth.entity.RoleMenuEntity;
import cn.bbwres.biscuit.module.auth.service.RoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 角色所属的资源 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Tag(name = " 角色所属的资源")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/roleMenu")
public class RoleMenuController {
    private final RoleMenuService roleMenuService;


    /**
     * 分页参数信息
     *
     * @param pageVO 分页参数
     * @return Result
     */
    @PostMapping("/page")
    @Operation(summary = "获得角色所属的资源分页")
    public Result<Page<RoleMenuRespVO, RoleMenuPageReqVO>> getRoleMenuPage(@Validated @RequestBody Page<RoleMenuEntity, RoleMenuPageReqVO> pageVO) {
        Page<RoleMenuEntity, RoleMenuPageReqVO> pageResult = roleMenuService.getRoleMenuPage(pageVO);
        return Result.success(RoleMenuConvert.INSTANCE.convertPage(pageResult));
    }

    /**
     * 根据id获取数据
     *
     * @return Result
     */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取标签定义的数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<RoleMenuRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(RoleMenuConvert.INSTANCE.convert(roleMenuService.getRoleMenu(entityId)));
    }


}
