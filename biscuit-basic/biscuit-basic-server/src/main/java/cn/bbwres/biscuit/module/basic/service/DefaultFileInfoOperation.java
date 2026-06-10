/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.module.basic.service;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.enums.YesOrNoEnum;
import cn.bbwres.biscuit.module.basic.convert.FileInfoConvert;
import cn.bbwres.biscuit.module.basic.convert.TempFileInfoConvert;
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessExpandParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import cn.bbwres.biscuit.web.file.service.FileInfoOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件信息操作类
 *
 * @author zhanglinfeng
 */
@Slf4j
@Component
public class DefaultFileInfoOperation implements FileInfoOperation {

    private final ParameterizedTypeReference<Result<Boolean>> BOOLEAN_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };


    private final TempFileInfoService tempFileInfoService;
    private final FileInfoService fileInfoService;
    private final FileBusinessInfoService fileBusinessInfoService;

    private final WebClient webClient;

    @Autowired
    public DefaultFileInfoOperation(TempFileInfoService tempFileInfoService, FileInfoService fileInfoService,
                                    FileBusinessInfoService fileBusinessInfoService, WebClient webClient) {
        this.tempFileInfoService = tempFileInfoService;
        this.fileInfoService = fileInfoService;
        this.fileBusinessInfoService = fileBusinessInfoService;
        this.webClient = webClient;
    }

    /**
     * 保存临时文件和记录
     *
     * @param fileInfo 文件信息
     * @return 文件信息
     */
    @Override
    public TempFileInfo saveTempFileInfo(TempFileInfo fileInfo) {
        TempFileInfoEntity tempFileInfoEntity = TempFileInfoConvert.INSTANCE.convertByInfo(fileInfo);
        tempFileInfoEntity = tempFileInfoService.saveTempFileInfo(tempFileInfoEntity);
        fileInfo.setId(tempFileInfoEntity.getId());
        return fileInfo;
    }

    /**
     * 根据 文件业务类型和业务id 以及文件id查询文件列表
     *
     * @param businessType a {@link String} object
     * @param businessId   a {@link String} object
     * @param fileId       文件id
     * @return a {@link List} object
     */
    @Override
    public List<FileInfo> findByBusinessAndId(String businessType, String businessId, String... fileId) {
        List<FileInfoEntity> fileInfoEntities = fileInfoService.findByBusinessAndId(businessType, businessId, fileId);
        if (CollectionUtils.isEmpty(fileInfoEntities)) {
            return null;
        }
        return FileInfoConvert.INSTANCE.convertList2Info(fileInfoEntities);
    }

    /**
     * 检查当前用户对当前业务文件的权限
     *
     * @param businessType 业务类型
     * @param businessId   业务id
     * @param requestUser  请求用户
     * @param fileId       文件id 为空时不校验文件
     * @return true  有权限，false 无权限
     */
    @Override
    public boolean checkFilePermission(String businessType, String businessId, UserBaseInfo requestUser, String... fileId) {
        FileBusinessInfoEntity businessInfo = fileBusinessInfoService.findByBusinessType(businessType);
        if (businessInfo == null) {
            log.warn("当前业务:[{}]没有配置文件业务信息!", businessType);
            return false;
        }
        if (YesOrNoEnum.NO.equals(businessInfo.getNeedAuth())) {
            log.info("当前业务:[{}]配置无需文件鉴权!", businessType);
            return true;
        }
        if (requestUser == null) {
            log.warn("当前业务:[{}]配置需要文件鉴权!但是获取到的用户信息为空", businessType);
            return false;
        }
        Mono<Boolean> result = webClient.post()
                .uri(businessInfo.getModuleName() + businessInfo.getAuthPath())
                .bodyValue(new CheckFilePermissionRequest(businessType, businessId, requestUser.getUserId(),
                        requestUser.getUsername(), requestUser.getTenantId(),
                        requestUser.getClientId(), fileId))
                .retrieve()
                .bodyToMono(BOOLEAN_TYPE_REFERENCE)
                .map(Result::checkAndGetData)
                // 防御：远程鉴权超时时快速失败，避免阻塞业务线程
                .timeout(Duration.ofSeconds(3))
                // 防御：网络异常 / 4xx-5xx / 解析异常时降级为"无权限"，避免业务侧误判为有权限
                .onErrorReturn(false);
        return result.blockOptional().orElse(false);
    }


    /**
     * 根据文件hash查询临时文件中是否存在
     *
     * @param fileHash a {@link String} object
     * @return a {@link FileInfo} object
     */
    @Override
    public FileInfo findByFileHashOne(String fileHash) {
        return FileInfoConvert.INSTANCE.convert2Info(fileInfoService.findByFileHashOne(fileHash));
    }

    /**
     * 根据ID查询出临时文件
     *
     * @param fileIds a {@link List} object
     * @return a {@link List} object
     */
    @Override
    public List<TempFileInfo> findTempFileInfoByIds(List<String> fileIds) {
        return TempFileInfoConvert.INSTANCE.convert2Info(tempFileInfoService.findTempFileInfoByIds(fileIds));
    }

    /**
     * 文件绑定业务信息
     * 1.先删除临时文件信息
     * 2.再保存文件信息
     *
     * @param fileBindBusinessExpandParams 请求参数信息
     * @param fileInfos                    文件信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fileBindBusiness(FileBindBusinessExpandParams fileBindBusinessExpandParams, List<FileInfo> fileInfos) {
        tempFileInfoService.deleteByFileIds(fileBindBusinessExpandParams.getFileIds());
        fileInfoService.saveFileInfos(FileInfoConvert.INSTANCE.convertInfoList2Entity(fileInfos));
    }

    /**
     * 更新文件信息
     *
     * @param fileInfoIds a {@link List} object
     */
    @Override
    public void updateFileInfo(String businessType, String businessId, List<String> fileInfoIds) {
        fileInfoService.updateFileInfoBusiness(businessType, businessId, fileInfoIds);
    }

    /**
     * 保存文件信息
     *
     * @param fileInfos a {@link List} object
     */
    @Override
    public void saveFileInfo(List<FileInfo> fileInfos) {
        fileInfoService.saveFileInfos(FileInfoConvert.INSTANCE.convertInfoList2Entity(fileInfos));
    }

    /**
     * 根据文件id删除文件信息
     *
     * @param list a {@link List} object
     */
    @Override
    public void deleteByFileIds(List<String> list) {
        fileInfoService.deleteByFileIds(list);
    }

    /**
     * 查询在指定时间也没有业务关联的临时文件信息
     *
     * @param gtData a {@link LocalDateTime} object
     * @return a {@link List} object
     */
    @Override
    public List<TempFileInfo> findByNoBusiness(LocalDateTime gtData) {
        return TempFileInfoConvert.INSTANCE.convert2Info(tempFileInfoService.findByNoBusiness(gtData));
    }

    /**
     * 根据临时文件信息，删除临时文件
     *
     * @param noBusinessList a {@link List} object
     */
    @Override
    public void deleteTempFileInfo(List<TempFileInfo> noBusinessList) {
        tempFileInfoService.deleteTempFileInfo(noBusinessList.stream().map(TempFileInfo::getId).toList());
    }

    /**
     * 检查用户是否有当前业务权限
     *
     * @param businessType
     * @param businessId
     * @param userId
     * @param userName
     * @param tenantId
     * @param clientId
     * @param fileId
     */
    public record CheckFilePermissionRequest(String businessType, String businessId, String userId,
                                             String userName, String tenantId, String clientId,
                                             String[] fileId) {
    }
}
