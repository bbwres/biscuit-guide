package cn.bbwres.biscuit.module.basic.service;

import java.time.LocalDateTime;
import java.util.*;
import cn.bbwres.biscuit.module.basic.controller.vo.*;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;


/**
 * <p>
 * 临时文件表 服务类
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
public interface TempFileInfoService {

    /**
     * 获得临时文件表
     *
     * @param id 编号
     * @return 临时文件表
     */
    TempFileInfoEntity getTempFileInfo(String id);

    /**
     * 获得临时文件表列表
     *
     * @param ids 编号
     * @return 临时文件表列表
     */
    List<TempFileInfoEntity> getTempFileInfoList(Collection<String> ids);

    /**
     * 获得临时文件表分页
     *
     * @param pageReqVO 分页查询
     * @return 临时文件表分页
     */
    Page<TempFileInfoEntity,TempFileInfoPageReqVO> getTempFileInfoPage(Page<TempFileInfoEntity,TempFileInfoPageReqVO> pageReqVO);


    /**
     * 保存临时文件
     * @param tempFileInfoEntity
     * @return
     */
    TempFileInfoEntity saveTempFileInfo(TempFileInfoEntity tempFileInfoEntity);

    /**
     * 根据文件id查询文件信息
     * @param fileIds
     * @return
     */
    List<TempFileInfoEntity> findTempFileInfoByIds(List<String> fileIds);

    /**
     * 根据文件id删除数据
     * @param fileIds
     */
    void deleteByFileIds(List<String> fileIds);

    /**
     * 查询在指定时间也没有业务关联的临时文件信息
     * @param gtData
     * @return
     */
    List<TempFileInfoEntity> findByNoBusiness(LocalDateTime gtData);

    /**
     * 删除临时文件
     * @param noBusinessList
     */
    void deleteTempFileInfo(List<String> noBusinessList);

}
