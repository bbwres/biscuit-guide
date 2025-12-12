package cn.bbwres.biscuit.module.basic.service;

import java.util.*;
import cn.bbwres.biscuit.module.basic.controller.vo.*;
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
import cn.bbwres.biscuit.dto.Page;





/**
 * <p>
 * 业务配置表 服务类
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
public interface FileBusinessInfoService {

    /**
     * 获得业务配置表
     *
     * @param id 编号
     * @return 业务配置表
     */
    FileBusinessInfoEntity getFileBusinessInfo(String id);

    /**
     * 获得业务配置表列表
     *
     * @param ids 编号
     * @return 业务配置表列表
     */
    List<FileBusinessInfoEntity> getFileBusinessInfoList(Collection<String> ids);

    /**
     * 获得业务配置表分页
     *
     * @param pageReqVO 分页查询
     * @return 业务配置表分页
     */
    Page<FileBusinessInfoEntity,FileBusinessInfoPageReqVO> getFileBusinessInfoPage(Page<FileBusinessInfoEntity,FileBusinessInfoPageReqVO> pageReqVO);


    /**
     * 根据业务类型获取到配置信息
     * @param businessType
     * @return
     */
    FileBusinessInfoEntity findByBusinessType(String businessType);

}
