package cn.bbwres.biscuit.module.auth.controller;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.convert.LoginAccountConvert;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import cn.bbwres.biscuit.module.auth.service.LoginAccountService;
import cn.bbwres.biscuit.module.auth.service.cache.LoginAccountCacheService;
import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidateEditStatusGroup;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 登陆账户表 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Slf4j
@Tag(name = "登陆账户表")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/loginAccount")
public class LoginAccountController {
    private final LoginAccountService loginAccountService;

    private final LoginAccountCacheService loginAccountCacheService;


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
    @Operation(summary = "根据id获取登陆账户表的数据",
            parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<LoginAccountRespVO> getById(@RequestParam("id") String entityId) {
        UserBaseInfo requestUser = WebFrameworkUtils.getRequestUser();
        return Result.success(LoginAccountConvert.INSTANCE.convert(loginAccountService.getLoginAccount(entityId)));
    }


    /**
     * 新增登录账户
     *
     * @return Result
     */
    @PostMapping("/addAccount")
    @Operation(summary = "新增登录账户")
    public Result<Void> addAccount(@RequestBody @Validated(ValidateAddGroup.class) LoginAccountAddOrUpdateReqVO loginAccountAddOrUpdateReq) {
        log.info("当前用户:[{}]新增新增登录账户信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), loginAccountAddOrUpdateReq);
        LoginAccountEntity loginAccountEntity = loginAccountService.findByLoginUsernameNoTenant(WebFrameworkUtils.getUserInfo(UserBaseInfo::getTenantId),
                loginAccountAddOrUpdateReq.getLoginName());
        if (Objects.nonNull(loginAccountEntity)) {
            return Result.error(AuthErrorCodeConstants.DATA_ALREADY_EXISTS_ERROR);
        }
        //查询当前的角色编码是否已经存在
        loginAccountEntity = LoginAccountConvert.INSTANCE.convertByAddReq(loginAccountAddOrUpdateReq);
        loginAccountService.save(loginAccountEntity);
        return Result.success(null);
    }


    /**
     * 账户分配角色
     *
     * @return Result
     */
    @PostMapping("/accountRoleConfig")
    @Operation(summary = "账户分配角色")
    public Result<Void> accountRoleConfig(@RequestBody @Validated LoginAccountAddRoleReqVO loginAccountAddRoleReq) {
        log.info("当前用户:[{}]新增新增登录账户信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), loginAccountAddRoleReq);
        LoginAccountEntity entity = loginAccountService.getLoginAccount(loginAccountAddRoleReq.getId());
        if (Objects.isNull(entity)) {
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        //查询当前的角色编码是否已经存在
        loginAccountService.accountRoleConfig(loginAccountAddRoleReq);
        loginAccountCacheService.deleteCache(entity.getTenantId(), entity.getLoginName());
        return Result.success(null);
    }



    /**
     * 修改账户状态
     *
     * @return Result
     */
    @PostMapping("/editAccountStatus")
    @Operation(summary = "修改账户状态")
    public Result<Void> editAccountStatus(@RequestBody @Validated(ValidateEditStatusGroup.class) LoginAccountAddOrUpdateReqVO loginAccountAddOrUpdateReq) {
        log.info("当前用户:[{}]修改账户状态信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), loginAccountAddOrUpdateReq);
        LoginAccountEntity entity = loginAccountService.getLoginAccount(loginAccountAddOrUpdateReq.getId());
        if (Objects.isNull(entity)) {
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        if (loginAccountAddOrUpdateReq.getStatus().equals(entity.getStatus())) {
            return Result.success(null);
        }
        if (LoginAccountStatusEnum.NORMAL.equals(loginAccountAddOrUpdateReq.getStatus())) {
            //修改状态为启用，则检查是否配置角色信息
            if (loginAccountService.checkUserRole(loginAccountAddOrUpdateReq.getId())) {
                return Result.error(AuthErrorCodeConstants.ACCOUNT_NO_ROLE_ERROR);
            }
        }
        loginAccountService.editAccountStatus(loginAccountAddOrUpdateReq);
        loginAccountCacheService.deleteCache(entity.getTenantId(), entity.getLoginName());
        return Result.success(null);
    }

    /**
     * 修改账户密码
     *
     * @return Result
     */
    @PostMapping("/editAccountPassword")
    @Operation(summary = "修改账户密码")
    public Result<Void> editAccountPassword(@RequestBody @Validated(LoginAccountEditPasswordReqVO.ValidateEditPasswordGroup.class) LoginAccountEditPasswordReqVO loginAccountEditPasswordReq) {
        return passwordHandler(loginAccountEditPasswordReq);
    }

    /**
     * 重置账户密码
     *
     * @return Result
     */
    @PostMapping("/resetAccountPassword")
    @Operation(summary = "重置账户密码")
    public Result<Void> resetAccountPassword(@RequestBody @Validated(LoginAccountEditPasswordReqVO.ValidateResetPasswordGroup.class) LoginAccountEditPasswordReqVO loginAccountEditPasswordReq) {
        return passwordHandler(loginAccountEditPasswordReq);
    }

    /**
     * 用户密码处理
     *
     * @param loginAccountEditPasswordReq
     * @return
     */
    private Result<Void> passwordHandler(LoginAccountEditPasswordReqVO loginAccountEditPasswordReq) {
        LoginAccountEntity entity = loginAccountService.getLoginAccount(loginAccountEditPasswordReq.getId());
        if (Objects.isNull(entity)) {
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        //校验原始密码
        loginAccountService.editAccountPassword(entity, loginAccountEditPasswordReq);
        loginAccountCacheService.deleteCache(entity.getTenantId(), entity.getLoginName());
        return Result.success(null);
    }


}
