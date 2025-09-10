package cn.bbwres.biscuit.module.auth.controller;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountRespVO;
import cn.bbwres.biscuit.module.auth.convert.LoginAccountConvert;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.service.LoginAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 登陆账户表 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Tag(name = " 登陆账户表")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/loginAccount")
public class LoginAccountController {
    private final LoginAccountService loginAccountService;


    /**
     * 分页参数信息
     *
     * @param pageVO 分页参数
     * @return Result
     */
    @PostMapping("/page")
    @Operation(summary = "获得登陆账户表分页")
    public Result<Page<LoginAccountRespVO, LoginAccountPageReqVO>> getLoginAccountPage(@Validated @RequestBody Page<LoginAccountEntity, LoginAccountPageReqVO> pageVO) {
        Page<LoginAccountEntity, LoginAccountPageReqVO> pageResult = loginAccountService.getLoginAccountPage(pageVO);
        return Result.success(LoginAccountConvert.INSTANCE.convertPage(pageResult));
    }

    /**
     * 根据id获取数据
     *
     * @return Result
     */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取标签定义的数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<LoginAccountRespVO> getById(@RequestParam("id") String entityId) {
        return Result.success(LoginAccountConvert.INSTANCE.convert(loginAccountService.getLoginAccount(entityId)));
    }


}
