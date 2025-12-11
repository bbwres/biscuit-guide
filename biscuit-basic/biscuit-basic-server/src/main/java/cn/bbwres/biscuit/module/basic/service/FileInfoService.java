package cn.bbwres.biscuit.module.basic.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.FileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;

import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 业务文件信息表 服务类
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
public interface FileInfoService {

    /**
     * 获得业务文件信息表
     *
     * @param id 编号
     * @return 业务文件信息表
     */
    FileInfoEntity getFileInfo(String id);

    /**
     * 获得业务文件信息表列表
     *
     * @param ids 编号
     * @return 业务文件信息表列表
     */
    List<FileInfoEntity> getFileInfoList(Collection<String> ids);

    /**
     * 获得业务文件信息表分页
     *
     * @param pageReqVO 分页查询
     * @return 业务文件信息表分页
     */
    Page<FileInfoEntity, FileInfoPageReqVO> getFileInfoPage(Page<FileInfoEntity, FileInfoPageReqVO> pageReqVO);


    /**
     * 根据 文件业务类型和业务id 以及文件id查询文件列表
     *
     * @param businessType
     * @param businessId
     * @param fileId
     * @return
     */
    List<FileInfoEntity> findByBusinessAndId(String businessType, String businessId, String... fileId);

    /**
     * 根据文件hash查询一个文件出来
     * @param fileHash
     * @return
     */
    FileInfoEntity findByFileHashOne(String fileHash);

    /**
     * 批量保存数据
     * @param fileInfoEntities
     */
    void saveFileInfos(List<FileInfoEntity> fileInfoEntities);


    /**
     * 根据文件id删除数据
     * @param list
     */
    void deleteByFileIds(List<String> list);

    /**
     * 更新文件业务信息
     * @param businessType
     * @param businessId
     * @param fileInfoIds
     */
    void updateFileInfoBusiness(String businessType, String businessId, List<String> fileInfoIds);

}
