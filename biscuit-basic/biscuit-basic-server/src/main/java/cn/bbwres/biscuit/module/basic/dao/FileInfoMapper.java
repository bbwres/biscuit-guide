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
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<FileInfoEntity, FileInfoPageReqVO> selectPage(Page<FileInfoEntity, FileInfoPageReqVO> reqVO) {
        LambdaQueryWrapper<FileInfoEntity> queryWrapper = Wrappers.lambdaQuery(FileInfoEntity.class);
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.eq(!ObjectUtils.isEmpty(reqVO.getQuery().getId()),
                    FileInfoEntity::getId, reqVO.getQuery().getId());
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

