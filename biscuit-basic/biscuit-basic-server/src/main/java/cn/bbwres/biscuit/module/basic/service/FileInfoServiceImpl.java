package cn.bbwres.biscuit.module.basic.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.FileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.dao.FileInfoMapper;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 业务文件信息表 服务实现类
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Service
public class FileInfoServiceImpl implements FileInfoService {

    private final FileInfoMapper fileInfoMapper;


    /**
     * 获得业务文件信息表
     *
     * @param id 编号
     * @return 业务文件信息表
     */
    @Override
    public FileInfoEntity getFileInfo(String id) {
        return fileInfoMapper.selectById(id);
    }

    /**
     * 获得业务文件信息表列表
     *
     * @param ids 编号
     * @return 业务文件信息表列表
     */
    @Override
    public List<FileInfoEntity> getFileInfoList(Collection<String> ids) {
        return fileInfoMapper.selectByIds(ids);
    }

    /**
     * 获得业务文件信息表分页
     *
     * @param pageReqVO 分页查询
     * @return 业务文件信息表分页
     */
    @Override
    public Page<FileInfoEntity, FileInfoPageReqVO> getFileInfoPage(Page<FileInfoEntity, FileInfoPageReqVO> pageReqVO) {
        return fileInfoMapper.selectPage(pageReqVO);
    }

    /**
     * 根据 文件业务类型和业务id 以及文件id查询文件列表
     *
     * @param businessType
     * @param businessId
     * @param fileId
     * @return
     */
    @Override
    public List<FileInfoEntity> findByBusinessAndId(String businessType, String businessId, String... fileId) {
        return fileInfoMapper.findByBusinessAndId(businessType, businessId, fileId);
    }

    /**
     * 根据文件hash查询一个文件出来
     *
     * @param fileHash
     * @return
     */
    @Override
    public FileInfoEntity findByFileHashOne(String fileHash) {
        return fileInfoMapper.findByFileHashOne(fileHash);
    }

    /**
     * 批量保存数据
     *
     * @param fileInfoEntities
     */
    @Override
    public void saveFileInfos(List<FileInfoEntity> fileInfoEntities) {
        fileInfoMapper.insert(fileInfoEntities);
    }



    /**
     * 根据文件id删除数据
     *
     * @param list
     */
    @Override
    public void deleteByFileIds(List<String> list) {
        fileInfoMapper.deleteByIds(list);
    }

    /**
     * 更新文件业务信息
     *
     * @param businessType
     * @param businessId
     * @param fileInfoIds
     */
    @Override
    public void updateFileInfoBusiness(String businessType, String businessId, List<String> fileInfoIds) {
        fileInfoMapper.updateFileInfoBusiness(businessType,businessId,fileInfoIds);
    }


}
