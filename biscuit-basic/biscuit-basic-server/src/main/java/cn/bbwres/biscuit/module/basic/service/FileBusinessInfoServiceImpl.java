package cn.bbwres.biscuit.module.basic.service;


import java.util.*;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.*;
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
import cn.bbwres.biscuit.module.basic.dao.FileBusinessInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 业务配置表 服务实现类
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Slf4j
@Service
public class FileBusinessInfoServiceImpl implements FileBusinessInfoService {

    private final FileBusinessInfoMapper fileBusinessInfoMapper;


    /**
     * 获得业务配置表
     *
     * @param id 编号
     * @return 业务配置表
     */
    @Override
    public FileBusinessInfoEntity getFileBusinessInfo(String id) {
        return fileBusinessInfoMapper.selectById(id);
    }

    /**
     * 获得业务配置表列表
     *
     * @param ids 编号
     * @return 业务配置表列表
     */
    @Override
    public List<FileBusinessInfoEntity> getFileBusinessInfoList(Collection<String> ids) {
        return fileBusinessInfoMapper.selectByIds(ids);
    }

    /**
     * 获得业务配置表分页
     *
     * @param pageReqVO 分页查询
     * @return 业务配置表分页
     */
    @Override
    public  Page<FileBusinessInfoEntity,FileBusinessInfoPageReqVO> getFileBusinessInfoPage(Page<FileBusinessInfoEntity,FileBusinessInfoPageReqVO> pageReqVO) {
        return fileBusinessInfoMapper.selectPage(pageReqVO);
    }


}
