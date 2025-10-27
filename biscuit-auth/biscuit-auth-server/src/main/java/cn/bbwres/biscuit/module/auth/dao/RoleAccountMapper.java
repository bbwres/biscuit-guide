package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.entity.RoleAccountEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface RoleAccountMapper extends BatchBaseMapper<RoleAccountEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<RoleAccountEntity, RoleAccountEntity> selectPage(Page<RoleAccountEntity, RoleAccountEntity> reqVO) {
        LambdaQueryWrapper<RoleAccountEntity> queryWrapper = Wrappers.lambdaQuery(RoleAccountEntity.class);
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.eq(!ObjectUtils.isEmpty(reqVO.getQuery().getId()),
                    RoleAccountEntity::getId, reqVO.getQuery().getId());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(RoleAccountEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RoleAccountEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }

    /**
     * 根据登录id删除配置的角色信息
     *
     * @param accountId
     */
    default void deleteByAccountId(String accountId) {
        delete(Wrappers.lambdaQuery(RoleAccountEntity.class)
                .eq(RoleAccountEntity::getLoginAccountId, accountId));
    }

    /**
     * 根据accountId
     *
     * @param accountId
     * @return
     */
    default Long countByAccountId(String accountId) {
        return selectCount(Wrappers.lambdaQuery(RoleAccountEntity.class)
                .eq(RoleAccountEntity::getLoginAccountId, accountId));
    }

    /**
     * 根据账号id查询出角色信息
     *
     * @param accountId 账户id
     * @param status
     * @return
     */
    @Select("""
            select r.*  from t_role_account ra left join  t_role r on r.id = ra.role_id
            where ra.login_account_id = #{accountId,jdbcType=VARCHAR}
            and r.status = #{status}
            """)
    @InterceptorIgnore(tenantLine = "true")
    List<RoleEntity> findByAccountId(@Param("accountId") String accountId, @Param("status") DataStatusEnum status);


    /**
     * 根据角色id查询出关联的账户信息
     *
     * @param roleId
     * @return
     */
    default List<String> findAccountsByRoleId(String roleId) {
        return selectObjs(Wrappers.lambdaQuery(RoleAccountEntity.class)
                .eq(RoleAccountEntity::getRoleId, roleId)
                .select(RoleAccountEntity::getLoginAccountId));
    }
}

