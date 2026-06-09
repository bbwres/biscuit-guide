package cn.bbwres.biscuit.module.basic.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.FileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 业务文件信息表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Mapper
public interface FileInfoMapper extends BatchBaseMapper<FileInfoEntity> {

    /**
     * 分页查询数据
     * <p>
     * 修复说明：原实现只按 id 过滤，导致 {@link FileInfoPageReqVO} 中的其他字段
     * （fileName、businessId、businessType、fileHash、fileStorageType、createTime、tenantId 等）
     * 形同虚设。现补全常见业务字段的查询条件。
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<FileInfoEntity, FileInfoPageReqVO> selectPage(Page<FileInfoEntity, FileInfoPageReqVO> reqVO) {
        LambdaQueryWrapper<FileInfoEntity> queryWrapper = Wrappers.lambdaQuery(FileInfoEntity.class);
        FileInfoPageReqVO query = reqVO.getQuery();
        if (!ObjectUtils.isEmpty(query)) {
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getId()), FileInfoEntity::getId, query.getId());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getTenantId()), FileInfoEntity::getTenantId, query.getTenantId());
            queryWrapper.like(!ObjectUtils.isEmpty(query.getFileName()), FileInfoEntity::getFileName, query.getFileName());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getFileSuffix()), FileInfoEntity::getFileSuffix, query.getFileSuffix());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getBusinessId()), FileInfoEntity::getBusinessId, query.getBusinessId());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getBusinessType()), FileInfoEntity::getBusinessType, query.getBusinessType());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getFileHash()), FileInfoEntity::getFileHash, query.getFileHash());
            queryWrapper.eq(!ObjectUtils.isEmpty(query.getFileStorageType()), FileInfoEntity::getFileStorageType, query.getFileStorageType());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(FileInfoEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<FileInfoEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }

    /**
     * 根据 文件业务类型和业务id 以及文件id查询文件列表
     *
     * @param businessType
     * @param businessId
     * @param fileId
     * @return
     */
    default List<FileInfoEntity> findByBusinessAndId(String businessType, String businessId, String... fileId) {
        return selectList(Wrappers.lambdaQuery(FileInfoEntity.class)
                .eq(FileInfoEntity::getBusinessType, businessType)
                .eq(FileInfoEntity::getBusinessId, businessId)
                .in(!ArrayUtils.isEmpty(fileId), FileInfoEntity::getId, fileId)
        );
    }

    /**
     * 根据文件hash查询一个文件出来
     *
     * @param fileHash
     * @return
     */
    default FileInfoEntity findByFileHashOne(String fileHash) {
        return selectOne(Wrappers.lambdaQuery(FileInfoEntity.class)
                .eq(FileInfoEntity::getFileHash, fileHash), false);
    }

    /**
     * 根据文件id更新文件的业务类型和业务id
     *
     * @param businessType
     * @param businessId
     * @param fileInfoIds
     */
    default void updateFileInfoBusiness(String businessType, String businessId, List<String> fileInfoIds) {
        update(Wrappers.lambdaUpdate(FileInfoEntity.class)
                .set(FileInfoEntity::getBusinessType, businessType)
                .set(FileInfoEntity::getBusinessId, businessId)
                .in(FileInfoEntity::getId, fileInfoIds)
        );
    }

}

