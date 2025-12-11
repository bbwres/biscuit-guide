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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.InputStream;
import java.util.List;

/**
 * 文件业务操作接口
 *
 * @author zhanglinfeng
 */
@FeignClient(name = "basic")
public interface FileBusinessApiService {

    /**
     * 1.文件与业务id绑定
     *
     * @param fileBindBusinessParams 文件业务绑定参数
     * @return Result
     */
    @PostMapping("/fileBindBusiness")
    Result<Void> fileBindBusiness(@RequestBody FileBindBusinessParamsVO fileBindBusinessParams);


    /**
     * 2. 文件绑定的业务变更（用于将原文件绑定到新业务上）
     *
     * @param fileBindBusinessChangeParams 文件业务绑定变更参数
     * @return Result
     */
    @PostMapping("/fileBindBusinessChange")
    Result<Void> fileBindBusinessChange(@RequestBody FileBindBusinessChangeParamsVO fileBindBusinessChangeParams);


    /**
     * 3. 文件绑定复制（用于将原业务关联的附件 复制到新的业务上）
     *
     * @param fileBindBusinessChangeParams 文件业务绑定参数
     * @return Result
     */
    @PostMapping("/fileBindBusinessCopy")
    Result<Void> fileBindBusinessCopy(@RequestBody FileBindBusinessChangeParamsVO fileBindBusinessChangeParams);

    /**
     * 4. 根据业务id删除附件（支持删除单个或者多个）
     *
     * @param fileBindBusinessParams 文件业务绑定参数
     * @return Result
     */
    @PostMapping("/deleteFileByBusinessInfo")
    Result<Void> deleteFileByBusinessInfo(@RequestBody FileBindBusinessParamsVO fileBindBusinessParams);


    /**
     * 6. 获取文件信息
     *
     * @param fileBindBusinessParams 业务信息
     * @return 文件列表
     */
    @PostMapping("/findFileInfoByBusiness")
    Result<List<FileInfoResultVO>> findFileInfoByBusiness(@RequestBody FileBindBusinessParamsVO fileBindBusinessParams);


    /**
     * 5. 删除过期没有关联的临时文件
     */
    @PostMapping("/deleteTempFile")
    void deleteTempFile();

    /**
     * 获取文件流
     *
     * @param businessType a {@link java.lang.String} object
     * @param businessId   a {@link java.lang.String} object
     * @param fileId       a {@link java.lang.String} object
     * @return a {@link java.io.InputStream} object
     */
    InputStream getFileInputStream(String businessType, String businessId, String fileId);


}
