package cn.bbwres.biscuit.module.basic.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.TempFileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.dao.TempFileInfoMapper;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 临时文件表 服务实现类
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Service
public class TempFileInfoServiceImpl implements TempFileInfoService {

    private final TempFileInfoMapper tempFileInfoMapper;


    /**
     * 获得临时文件表
     *
     * @param id 编号
     * @return 临时文件表
     */
    @Override
    public TempFileInfoEntity getTempFileInfo(String id) {
        return tempFileInfoMapper.selectById(id);
    }

    /**
     * 获得临时文件表列表
     *
     * @param ids 编号
     * @return 临时文件表列表
     */
    @Override
    public List<TempFileInfoEntity> getTempFileInfoList(Collection<String> ids) {
        return tempFileInfoMapper.selectByIds(ids);
    }

    /**
     * 获得临时文件表分页
     *
     * @param pageReqVO 分页查询
     * @return 临时文件表分页
     */
    @Override
    public Page<TempFileInfoEntity, TempFileInfoPageReqVO> getTempFileInfoPage(Page<TempFileInfoEntity, TempFileInfoPageReqVO> pageReqVO) {
        return tempFileInfoMapper.selectPage(pageReqVO);
    }

    /**
     * 保存临时文件
     *
     * @param tempFileInfoEntity
     * @return
     */
    @Override
    public TempFileInfoEntity saveTempFileInfo(TempFileInfoEntity tempFileInfoEntity) {
        tempFileInfoMapper.insert(tempFileInfoEntity);
        return tempFileInfoEntity;
    }

    /**
     * 根据文件id查询文件信息
     *
     * @param fileIds
     * @return
     */
    @Override
    public List<TempFileInfoEntity> findTempFileInfoByIds(List<String> fileIds) {
        return tempFileInfoMapper.selectByIds(fileIds);
    }

    /**
     * 根据文件id删除数据
     *
     * @param fileIds
     */
    @Override
    public void deleteByFileIds(List<String> fileIds) {
        tempFileInfoMapper.deleteByIds(fileIds);
    }

    /**
     * 查询在指定时间也没有业务关联的临时文件信息
     *
     * @param gtData
     * @return
     */
    @Override
    public List<TempFileInfoEntity> findByNoBusiness(LocalDateTime gtData) {
        return tempFileInfoMapper.findByNoBusiness(gtData);
    }

    /**
     * 删除临时文件
     *
     * @param noBusinessList
     */
    @Override
    public void deleteTempFileInfo(List<String> noBusinessList) {
        tempFileInfoMapper.deleteByIds(noBusinessList);
    }


}
