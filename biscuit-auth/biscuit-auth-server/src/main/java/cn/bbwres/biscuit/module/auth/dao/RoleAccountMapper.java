package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.entity.RoleAccountEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.entity.table.RoleEntityTableDef;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static cn.bbwres.biscuit.module.auth.entity.table.RoleAccountEntityTableDef.ROLE_ACCOUNT_ENTITY;


/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface RoleAccountMapper extends BaseMapper<RoleAccountEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<RoleAccountEntity, RoleAccountEntity> selectPage(Page<RoleAccountEntity, RoleAccountEntity> reqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.where(ROLE_ACCOUNT_ENTITY.ID.eq(reqVO.getQuery().getId()));
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderBy(ROLE_ACCOUNT_ENTITY.ID, false);

        com.mybatisflex.core.paginate.Page<RoleAccountEntity> page = paginate(reqVO.getCurrent(), reqVO.getSize(), queryWrapper);
        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotalRow());
        reqVO.calculationPages();
        return reqVO;
    }

    /**
     * 根据登录id删除配置的角色信息
     *
     * @param accountId
     */
    default void deleteByAccountId(String accountId) {
        deleteByCondition(ROLE_ACCOUNT_ENTITY.LOGIN_ACCOUNT_ID.eq(accountId));
    }

    /**
     * 根据accountId
     *
     * @param accountId
     * @return
     */
    default Long countByAccountId(String accountId) {
        return selectCountByCondition(ROLE_ACCOUNT_ENTITY.LOGIN_ACCOUNT_ID.eq(accountId));
    }

    /**
     * 根据账号id查询出角色信息
     *
     * @param accountId 账户id
     * @param status
     * @return
     */
    default List<RoleEntity> findByAccountIdNoTenant(String accountId, DataStatusEnum status) {
        QueryWrapper queryWrapper = QueryWrapper.create().leftJoin(RoleEntityTableDef.ROLE_ENTITY)
                .on(RoleEntityTableDef.ROLE_ENTITY.ID.eq(ROLE_ACCOUNT_ENTITY.ID))
                .where(ROLE_ACCOUNT_ENTITY.LOGIN_ACCOUNT_ID.eq(accountId))
                .and(RoleEntityTableDef.ROLE_ENTITY.STATUS.eq(status));
        return selectObjectListByQueryAs(queryWrapper, RoleEntity.class);
    }


    /**
     * 根据角色id查询出关联的账户信息
     *
     * @param roleId
     * @return
     */
    default List<String> findAccountsByRoleId(String roleId) {
        return selectObjectListByQueryAs(QueryWrapper.create()
                .where(ROLE_ACCOUNT_ENTITY.ROLE_ID.eq(roleId))
                .select(ROLE_ACCOUNT_ENTITY.LOGIN_ACCOUNT_ID), String.class);

    }
}

