package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.tenant.TenantManager;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;

import static cn.bbwres.biscuit.module.auth.entity.table.LoginAccountEntityTableDef.LOGIN_ACCOUNT_ENTITY;


/**
 * <p>
 * 登陆账户表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface LoginAccountMapper extends BaseMapper<LoginAccountEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<LoginAccountEntity, LoginAccountPageReqVO> selectPage(Page<LoginAccountEntity, LoginAccountPageReqVO> reqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.where(LOGIN_ACCOUNT_ENTITY.ID.eq(reqVO.getQuery().getId()));
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderBy(LOGIN_ACCOUNT_ENTITY.ID, false);

        com.mybatisflex.core.paginate.Page<LoginAccountEntity> page = paginate(reqVO.getCurrent(), reqVO.getSize(), queryWrapper);
        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotalRow());
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
    default LoginAccountEntity findByLoginUsernameNoTenant(String tenantId, String username) {
        return TenantManager.withoutTenantCondition(() -> selectOneByQuery(QueryWrapper.create()
                .where(LOGIN_ACCOUNT_ENTITY.TENANT_ID.eq(tenantId)
                        .and(LOGIN_ACCOUNT_ENTITY.LOGIN_NAME.eq(username)))));
    }
}

