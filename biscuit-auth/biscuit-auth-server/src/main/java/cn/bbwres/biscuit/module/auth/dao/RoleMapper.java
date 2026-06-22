package cn.bbwres.biscuit.module.auth.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;


/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface RoleMapper extends BatchBaseMapper<RoleEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<RoleEntity, RolePageReqVO> selectPage(Page<RoleEntity, RolePageReqVO> reqVO) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = Wrappers.lambdaQuery(RoleEntity.class);
        RolePageReqVO query = reqVO.getQuery();
        if (!ObjectUtils.isEmpty(query)) {
            // 精确匹配
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getId()),
                    RoleEntity::getId, query.getId());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getStatus()),
                    RoleEntity::getStatus, query.getStatus());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getClientId()),
                    RoleEntity::getClientId, query.getClientId());

            // 模糊匹配：角色编码 / 角色名称
            queryWrapper.like(!ObjectUtils.isEmpty(query.getRoleCode()),
                    RoleEntity::getRoleCode, query.getRoleCode());
            queryWrapper.like(!ObjectUtils.isEmpty(query.getRoleName()),
                    RoleEntity::getRoleName, query.getRoleName());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(RoleEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RoleEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }

    /**
     * 根据角色编码和客户端id查询数据
     *
     * @param roleCode
     * @param clientId
     * @return
     */
    default RoleEntity findByRoleCodeAndClientId(String roleCode, String clientId) {
        return selectOne(Wrappers.lambdaQuery(RoleEntity.class)
                .eq(RoleEntity::getRoleCode, roleCode)
                .eq(RoleEntity::getClientId, clientId)
        );
    }
}

