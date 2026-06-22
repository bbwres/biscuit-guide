package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;


/**
 * <p>
 * 登陆账户表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface LoginAccountMapper extends BatchBaseMapper<LoginAccountEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<LoginAccountEntity, LoginAccountPageReqVO> selectPage(Page<LoginAccountEntity, LoginAccountPageReqVO> reqVO) {
        LambdaQueryWrapper<LoginAccountEntity> queryWrapper = Wrappers.lambdaQuery(LoginAccountEntity.class);
        LoginAccountPageReqVO query = reqVO.getQuery();
        if (!ObjectUtils.isEmpty(query)) {
            // 精确匹配
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getId()),
                    LoginAccountEntity::getId, query.getId());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getStatus()),
                    LoginAccountEntity::getStatus, query.getStatus());

            // 模糊匹配：登录名 / 姓名 / 手机号
            queryWrapper.like(!ObjectUtils.isEmpty(query.getLoginName()),
                    LoginAccountEntity::getLoginName, query.getLoginName());
            queryWrapper.like(!ObjectUtils.isEmpty(query.getName()),
                    LoginAccountEntity::getName, query.getName());
            queryWrapper.like(!ObjectUtils.isEmpty(query.getPhone()),
                    LoginAccountEntity::getPhone, query.getPhone());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(LoginAccountEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<LoginAccountEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }

    /**
     * 根据用户名称查询数据
     *
     * @param tenantId
     * @param username
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    default LoginAccountEntity findByLoginUsernameNoTenant(String tenantId, String username) {
        return selectOne(Wrappers.lambdaQuery(LoginAccountEntity.class)
                .eq(LoginAccountEntity::getTenantId, tenantId)
                .eq(LoginAccountEntity::getLoginName, username));
    }
}

