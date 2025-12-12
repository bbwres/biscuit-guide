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

package cn.bbwres.biscuit.module.basic.api;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.module.basic.api.vo.FileBindBusinessChangeParamsVO;
import cn.bbwres.biscuit.module.basic.api.vo.FileBindBusinessParamsVO;
import cn.bbwres.biscuit.module.basic.api.vo.FileInfoResultVO;
import cn.bbwres.biscuit.module.basic.constants.BasicErrorCodeConstants;
import cn.bbwres.biscuit.module.basic.convert.FileInfoConvert;
import cn.bbwres.biscuit.web.file.api.FileBusinessOperation;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessExpandParams;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessParams;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessRefOldParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;

/**
 * 文件业务操作服务
 *
 * @author zhanglinfeng
 */
@Slf4j
@RestController
public class FileBusinessApiServiceImpl implements FileBusinessApiService {

    private FileBusinessOperation fileBusinessOperation;

    @Autowired
    public void setFileBusinessOperation(FileBusinessOperation fileBusinessOperation) {
        this.fileBusinessOperation = fileBusinessOperation;
    }

    /**
     * 1.文件与业务id绑定
     *
     * @param fileBindBusinessParams 文件业务绑定参数
     * @return Result
     */
    @Override
    public Result<Void> fileBindBusiness(FileBindBusinessParamsVO fileBindBusinessParams) {
        FileBindBusinessExpandParams fileBindBusinessExpandParams = new FileBindBusinessExpandParams();
        fileBindBusinessExpandParams.setDeleteHistory(fileBindBusinessParams.getDeleteHistory());
        fileBindBusinessExpandParams.setFileIds(fileBindBusinessParams.getFileIds());
        fileBindBusinessExpandParams.setBusinessId(fileBindBusinessParams.getBusinessId());
        fileBindBusinessExpandParams.setBusinessType(fileBindBusinessParams.getBusinessType());
        fileBusinessOperation.fileBindBusiness(fileBindBusinessExpandParams);
        return Result.success(null);
    }

    /**
     * 2. 文件绑定的业务变更（用于将原文件绑定到新业务上）
     *
     * @param fileBindBusinessChangeParams 文件业务绑定变更参数
     * @return Result
     */
    @Override
    public Result<Void> fileBindBusinessChange(FileBindBusinessChangeParamsVO fileBindBusinessChangeParams) {
        FileBindBusinessRefOldParams fileBindBusinessRefOldParams = new FileBindBusinessRefOldParams();
        fileBindBusinessRefOldParams.setOldBusinessId(fileBindBusinessChangeParams.getOldBusinessId());
        fileBindBusinessRefOldParams.setOldBusinessType(fileBindBusinessChangeParams.getOldBusinessType());
        fileBindBusinessRefOldParams.setFileIds(fileBindBusinessChangeParams.getFileIds());
        fileBindBusinessRefOldParams.setBusinessId(fileBindBusinessChangeParams.getBusinessId());
        fileBindBusinessRefOldParams.setBusinessType(fileBindBusinessChangeParams.getBusinessType());
        fileBusinessOperation.fileBindBusinessChange(fileBindBusinessRefOldParams);
        return Result.success(null);
    }

    /**
     * 3. 文件绑定复制（用于将原业务关联的附件 复制到新的业务上）
     *
     * @param fileBindBusinessChangeParams 文件业务绑定参数
     * @return Result
     */
    @Override
    public Result<Void> fileBindBusinessCopy(FileBindBusinessChangeParamsVO fileBindBusinessChangeParams) {
        FileBindBusinessRefOldParams fileBindBusinessRefOldParams = new FileBindBusinessRefOldParams();
        fileBindBusinessRefOldParams.setOldBusinessId(fileBindBusinessChangeParams.getOldBusinessId());
        fileBindBusinessRefOldParams.setOldBusinessType(fileBindBusinessChangeParams.getOldBusinessType());
        fileBindBusinessRefOldParams.setFileIds(fileBindBusinessChangeParams.getFileIds());
        fileBindBusinessRefOldParams.setBusinessId(fileBindBusinessChangeParams.getBusinessId());
        fileBindBusinessRefOldParams.setBusinessType(fileBindBusinessChangeParams.getBusinessType());
        fileBusinessOperation.fileBindBusinessCopy(fileBindBusinessRefOldParams);
        return Result.success(null);
    }

    /**
     * 4. 根据业务id删除附件（支持删除单个或者多个）
     *
     * @param fileBindBusinessParams 文件业务绑定参数
     * @return Result
     */
    @Override
    public Result<Void> deleteFileByBusinessInfo(FileBindBusinessParamsVO fileBindBusinessParams) {
        FileBindBusinessParams fileBindBusiness = new FileBindBusinessExpandParams();
        fileBindBusiness.setFileIds(fileBindBusinessParams.getFileIds());
        fileBindBusiness.setBusinessId(fileBindBusinessParams.getBusinessId());
        fileBindBusiness.setBusinessType(fileBindBusinessParams.getBusinessType());
        fileBusinessOperation.deleteFileByBusinessInfo(fileBindBusiness);
        return Result.success(null);
    }

    /**
     * 6. 获取文件信息
     *
     * @param fileBindBusinessParams 业务信息
     * @return 文件列表
     */
    @Override
    public Result<List<FileInfoResultVO>> findFileInfoByBusiness(FileBindBusinessParamsVO fileBindBusinessParams) {
        FileBindBusinessParams fileBindBusiness = new FileBindBusinessParams();
        fileBindBusiness.setFileIds(fileBindBusinessParams.getFileIds());
        fileBindBusiness.setBusinessId(fileBindBusinessParams.getBusinessId());
        fileBindBusiness.setBusinessType(fileBindBusinessParams.getBusinessType());
        List<FileInfo> fileInfoByBusiness = fileBusinessOperation.findFileInfoByBusiness(fileBindBusiness);
        return Result.success(FileInfoConvert.INSTANCE.convertInfo2FileInfoResult(fileInfoByBusiness));
    }

    /**
     * 5. 删除过期没有关联的临时文件
     */
    @Override
    public void deleteTempFile() {
        fileBusinessOperation.deleteTempFile();
    }

    /**
     * 获取文件流 需要避免超过20m的文件
     *
     * @param businessType a {@link String} object
     * @param businessId   a {@link String} object
     * @param fileId       a {@link String} object
     * @return a {@link InputStream} object
     */
    @Override
    public Result<byte[]> getFileInputStream(String businessType, String businessId, String fileId) {
        try (InputStream inputStream = fileBusinessOperation.getFileInputStream(businessType, businessId, fileId)) {
            return Result.success(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            log.error("文件获取异常!{}", e.getMessage());
            return Result.error(BasicErrorCodeConstants.FILE_GET_ERROR);
        }
    }
}
