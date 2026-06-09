package cn.bbwres.biscuit.module.basic.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.FileBusinessInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;


/**
 * <p>
 * 业务配置表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Mapper
public interface FileBusinessInfoMapper extends BatchBaseMapper<FileBusinessInfoEntity> {

    /**
     * 分页查询数据
     * <p>
     * 修复说明：原实现只按 id 过滤，导致 {@link FileBusinessInfoPageReqVO} 中其他字段无效。
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<FileBusinessInfoEntity, FileBusinessInfoPageReqVO> selectPage(Page<FileBusinessInfoEntity, FileBusinessInfoPageReqVO> reqVO) {
        LambdaQueryWrapper<FileBusinessInfoEntity> queryWrapper = Wrappers.lambdaQuery(FileBusinessInfoEntity.class);
        FileBusinessInfoPageReqVO query = reqVO.getQuery();
        if (!ObjectUtils.isEmpty(query)) {
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getId()), FileBusinessInfoEntity::getId, query.getId());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getTenantId()), FileBusinessInfoEntity::getTenantId, query.getTenantId());
            queryWrapper.like(!ObjectUtils.isEmpty(query.getBusinessName()), FileBusinessInfoEntity::getBusinessName, query.getBusinessName());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getBusinessType()), FileBusinessInfoEntity::getBusinessType, query.getBusinessType());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getModuleName()), FileBusinessInfoEntity::getModuleName, query.getModuleName());
            queryWrapper.like(!ObjectUtils.isEmpty(query.getAuthPath()), FileBusinessInfoEntity::getAuthPath, query.getAuthPath());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(FileBusinessInfoEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<FileBusinessInfoEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }

    /**
     * 根据业务类型获取配置信息
     *
     * @param businessType
     * @return
     */
    default FileBusinessInfoEntity findByBusinessType(String businessType) {
        return selectOne(Wrappers.lambdaQuery(FileBusinessInfoEntity.class)
                .eq(FileBusinessInfoEntity::getBusinessType, businessType), false);
    }
}

