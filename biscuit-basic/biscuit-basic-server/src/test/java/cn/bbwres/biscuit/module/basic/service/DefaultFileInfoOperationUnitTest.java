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
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessExpandParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link DefaultFileInfoOperation} 单元测试。
 *
 * <p>WebClient 链路使用 {@code RETURNS_DEEP_STUBS} 形式创建 mock，
 * 以支持 {@code webClient.post().uri().bodyValue().retrieve().bodyToMono().map()} 的链式调用。</p>
 *
 * <p>重点覆盖：
 * <ul>
 *   <li>{@code checkFilePermission}：businessInfo 为 null、needAuth=NO、requestUser 为 null、
 *       WebClient 远程调用返回 true / false / null（超时兜底）、异常兜底</li>
 *   <li>{@code findByBusinessAndId} 空集合时返回 null 的契约</li>
 *   <li>MapStruct 转换调用次数</li>
 * </ul>
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultFileInfoOperation 单元测试")
class DefaultFileInfoOperationUnitTest {

    @Mock
    private TempFileInfoService tempFileInfoService;
    @Mock
    private FileInfoService fileInfoService;
    @Mock
    private FileBusinessInfoService fileBusinessInfoService;

    /**
     * 使用 RETURNS_DEEP_STUBS 让 webClient.post().uri().bodyValue().retrieve().bodyToMono() 一路可链式 mock。
     */
    private WebClient buildDeepStubWebClient() {
        return mock(WebClient.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
    }

    private DefaultFileInfoOperation newOperation(WebClient webClient) {
        return new DefaultFileInfoOperation(
                tempFileInfoService, fileInfoService, fileBusinessInfoService, webClient);
    }

    // -----------------------------------------------------------------------------------------
    // saveTempFileInfo
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("saveTempFileInfo：保存并回填 id")
    void saveTempFileInfo_savesAndBackfillsId() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        TempFileInfo in = new TempFileInfo().setFileName("a.png");
        TempFileInfoEntity savedEntity = new TempFileInfoEntity();
        savedEntity.setId("t-1");
        when(tempFileInfoService.saveTempFileInfo(any(TempFileInfoEntity.class))).thenReturn(savedEntity);

        TempFileInfo out = op.saveTempFileInfo(in);

        assertSame(in, out, "约定：saveTempFileInfo 返回入参本身");
        assertEquals("t-1", out.getId());
        verify(tempFileInfoService, times(1)).saveTempFileInfo(any(TempFileInfoEntity.class));
    }

    // -----------------------------------------------------------------------------------------
    // findByBusinessAndId
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("findByBusinessAndId：mapper 返回非空时转换为 List<FileInfo>")
    void findByBusinessAndId_convertsToFileInfoList() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        FileInfoEntity e1 = new FileInfoEntity();
        e1.setId("1");
        when(fileInfoService.findByBusinessAndId("ORDER", "biz-1", "1")).thenReturn(List.of(e1));

        List<FileInfo> result = op.findByBusinessAndId("ORDER", "biz-1", "1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    @DisplayName("findByBusinessAndId：契约 - 空结果必须返回 null（用于上游判空）")
    void findByBusinessAndId_emptyReturnsNull() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        when(fileInfoService.findByBusinessAndId("ORDER", "biz-1"))
                .thenReturn(Collections.emptyList());

        List<FileInfo> result = op.findByBusinessAndId("ORDER", "biz-1");

        assertNull(result, "契约：空集合必须返回 null（不是空 List），调用方通常使用 == null 判断");
        verify(fileInfoService).findByBusinessAndId("ORDER", "biz-1");
    }

    // -----------------------------------------------------------------------------------------
    // checkFilePermission
    // -----------------------------------------------------------------------------------------

    @Nested
    @DisplayName("checkFilePermission 鉴权流程")
    class CheckFilePermission {

        @Test
        @DisplayName("businessInfo 为 null 时返回 false（不发起远程调用）")
        void returnsFalseWhenBusinessInfoNull() {
            WebClient wc = buildDeepStubWebClient();
            DefaultFileInfoOperation op = newOperation(wc);

            when(fileBusinessInfoService.findByBusinessType("ORDER")).thenReturn(null);

            UserBaseInfo user = new UserBaseInfo();
            user.setUserId("u-1");

            boolean result = op.checkFilePermission("ORDER", "biz-1", user, "f-1");

            assertFalse(result);
            // 业务配置缺失时不能发远程请求
            verify(wc, never()).post();
        }

        @Test
        @DisplayName("needAuth=NO 时直接返回 true，不发远程请求")
        void returnsTrueWhenAuthNotRequired() {
            WebClient wc = buildDeepStubWebClient();
            DefaultFileInfoOperation op = newOperation(wc);

            FileBusinessInfoEntity info = new FileBusinessInfoEntity();
            info.setBusinessType("ORDER");
            info.setNeedAuth(YesOrNoEnum.NO);
            when(fileBusinessInfoService.findByBusinessType("ORDER")).thenReturn(info);

            UserBaseInfo user = new UserBaseInfo();
            user.setUserId("u-1");

            boolean result = op.checkFilePermission("ORDER", "biz-1", user);

            assertTrue(result);
            verify(wc, never()).post();
        }

        @Test
        @DisplayName("needAuth=YES 但 requestUser 为 null 时返回 false，不发远程请求")
        void returnsFalseWhenUserNull() {
            WebClient wc = buildDeepStubWebClient();
            DefaultFileInfoOperation op = newOperation(wc);

            FileBusinessInfoEntity info = new FileBusinessInfoEntity();
            info.setBusinessType("ORDER");
            info.setNeedAuth(YesOrNoEnum.YES);
            info.setModuleName("order");
            info.setAuthPath("/api/auth");
            when(fileBusinessInfoService.findByBusinessType("ORDER")).thenReturn(info);

            boolean result = op.checkFilePermission("ORDER", "biz-1", null);

            assertFalse(result);
            verify(wc, never()).post();
        }

        @Test
        @DisplayName("WebClient 返回 true 时透传 true")
        void returnsTrueWhenRemoteReturnsTrue() {
            WebClient wc = buildDeepStubWebClient();
            DefaultFileInfoOperation op = newOperation(wc);

            FileBusinessInfoEntity info = new FileBusinessInfoEntity();
            info.setBusinessType("ORDER");
            info.setNeedAuth(YesOrNoEnum.YES);
            info.setModuleName("order");
            info.setAuthPath("/api/auth");
            when(fileBusinessInfoService.findByBusinessType("ORDER")).thenReturn(info);

            // 构造 Result<Boolean> true 的 Mono
            Result<Boolean> okResult = Result.success(Boolean.TRUE);
            // 链式 mock 末端 bodyToMono 返回 Mono
            when(wc.post().uri(any(String.class))
                    .bodyValue(any(DefaultFileInfoOperation.CheckFilePermissionRequest.class))
                    .retrieve()
                    .bodyToMono(any(ParameterizedTypeReference.class)))
                    .thenReturn(Mono.just(okResult));

            UserBaseInfo user = new UserBaseInfo();
            user.setUserId("u-1");
            user.setUsername("u1");
            user.setTenantId("t-1");
            user.setClientId("c-1");

            boolean result = op.checkFilePermission("ORDER", "biz-1", user, "f-1");

            assertTrue(result);
            verify(wc, atLeastOnce()).post();
        }

        @Test
        @DisplayName("WebClient 返回 false 时透传 false")
        void returnsFalseWhenRemoteReturnsFalse() {
            WebClient wc = buildDeepStubWebClient();
            DefaultFileInfoOperation op = newOperation(wc);

            FileBusinessInfoEntity info = new FileBusinessInfoEntity();
            info.setBusinessType("ORDER");
            info.setNeedAuth(YesOrNoEnum.YES);
            info.setModuleName("order");
            info.setAuthPath("/api/auth");
            when(fileBusinessInfoService.findByBusinessType("ORDER")).thenReturn(info);

            Result<Boolean> noResult = Result.success(Boolean.FALSE);
            when(wc.post().uri(any(String.class))
                    .bodyValue(any(DefaultFileInfoOperation.CheckFilePermissionRequest.class))
                    .retrieve()
                    .bodyToMono(any(ParameterizedTypeReference.class)))
                    .thenReturn(Mono.just(noResult));

            UserBaseInfo user = new UserBaseInfo();
            user.setUserId("u-1");
            boolean result = op.checkFilePermission("ORDER", "biz-1", user, "f-1");

            assertFalse(result);
        }

        @Test
        @DisplayName("WebClient 远程调用超时（blockOptional 为空）时 3s 内兜底返回 false")
        void returnsFalseOnTimeout() {
            WebClient wc = buildDeepStubWebClient();
            DefaultFileInfoOperation op = newOperation(wc);

            FileBusinessInfoEntity info = new FileBusinessInfoEntity();
            info.setBusinessType("ORDER");
            info.setNeedAuth(YesOrNoEnum.YES);
            info.setModuleName("order");
            info.setAuthPath("/api/auth");
            when(fileBusinessInfoService.findByBusinessType("ORDER")).thenReturn(info);

            // Mono.never() 模拟永不到达的远程响应
            when(wc.post().uri(any(String.class))
                    .bodyValue(any(DefaultFileInfoOperation.CheckFilePermissionRequest.class))
                    .retrieve()
                    .bodyToMono(any(ParameterizedTypeReference.class)))
                    .thenReturn(Mono.never());

            // 修复后实现加了 .timeout(3s) + .onErrorReturn(false)：
            // - 超过 3 秒触发 TimeoutException
            // - onErrorReturn(false) 兜底返回 false
            // - 业务线程不会被无限阻塞
            UserBaseInfo user = new UserBaseInfo();
            user.setUserId("u-1");
            Boolean[] result = new Boolean[1];
            Thread t = new Thread(() -> result[0] = op.checkFilePermission(
                    "ORDER", "biz-1", user, "f-1"));
            t.setDaemon(true);
            t.start();
            try {
                t.join(Duration.ofSeconds(5).toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 关键点：实现已加 3s 超时，调用应在 5s 内完成（不会无限挂起）
            assertFalse(t.isAlive(), "调用应在超时（3s）内完成，不应无限阻塞");
            assertNotNull(result[0]);
            assertFalse(result[0], "超时应兜底返回 false");
        }

        @Test
        @DisplayName("WebClient 远程调用抛异常时被 onErrorReturn 兜底返回 false（不抛给调用方）")
        void rethrowsOnRemoteException() {
            WebClient wc = buildDeepStubWebClient();
            DefaultFileInfoOperation op = newOperation(wc);

            FileBusinessInfoEntity info = new FileBusinessInfoEntity();
            info.setBusinessType("ORDER");
            info.setNeedAuth(YesOrNoEnum.YES);
            info.setModuleName("order");
            info.setAuthPath("/api/auth");
            when(fileBusinessInfoService.findByBusinessType("ORDER")).thenReturn(info);

            when(wc.post().uri(any(String.class))
                    .bodyValue(any(DefaultFileInfoOperation.CheckFilePermissionRequest.class))
                    .retrieve()
                    .bodyToMono(any(ParameterizedTypeReference.class)))
                    .thenReturn(Mono.error(new RuntimeException("upstream down")));

            UserBaseInfo user = new UserBaseInfo();
            user.setUserId("u-1");
            // 修复后：异常被 onErrorReturn(false) 兜底，不再向上抛
            boolean result = op.checkFilePermission("ORDER", "biz-1", user, "f-1");
            assertFalse(result, "远程异常应被 onErrorReturn(false) 兜底");
        }
    }

    // -----------------------------------------------------------------------------------------
    // findByFileHashOne
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("findByFileHashOne：存在时返回 FileInfo")
    void findByFileHashOne_returnsFileInfo() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        FileInfoEntity e = new FileInfoEntity();
        e.setId("f-1");
        e.setFileName("a.png");
        when(fileInfoService.findByFileHashOne("hash-1")).thenReturn(e);

        FileInfo info = op.findByFileHashOne("hash-1");

        assertNotNull(info);
        assertEquals("f-1", info.getId());
        assertEquals("a.png", info.getFileName());
    }

    @Test
    @DisplayName("findByFileHashOne：mapper 返回 null 时透传 null")
    void findByFileHashOne_null() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);
        when(fileInfoService.findByFileHashOne("x")).thenReturn(null);
        assertNull(op.findByFileHashOne("x"));
    }

    // -----------------------------------------------------------------------------------------
    // findTempFileInfoByIds
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("findTempFileInfoByIds：委托并转换")
    void findTempFileInfoByIds_converts() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        TempFileInfoEntity e = new TempFileInfoEntity();
        e.setId("t-1");
        when(tempFileInfoService.findTempFileInfoByIds(List.of("t-1"))).thenReturn(List.of(e));

        List<TempFileInfo> result = op.findTempFileInfoByIds(List.of("t-1"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("t-1", result.get(0).getId());
    }

    // -----------------------------------------------------------------------------------------
    // fileBindBusiness
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("fileBindBusiness：先删临时文件再保存文件")
    void fileBindBusiness_deletesTempThenSaves() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        FileBindBusinessExpandParams params = new FileBindBusinessExpandParams();
        params.setFileIds(List.of("t-1", "t-2"));
        FileInfo info = new FileInfo().setFileName("f.png");
        List<FileInfo> infos = List.of(info);

        op.fileBindBusiness(params, infos);

        verify(tempFileInfoService, times(1)).deleteByFileIds(List.of("t-1", "t-2"));
        verify(fileInfoService, times(1)).saveFileInfos(any());
    }

    // -----------------------------------------------------------------------------------------
    // updateFileInfo
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("updateFileInfo：直接委托 fileInfoService.updateFileInfoBusiness")
    void updateFileInfo_delegates() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        List<String> ids = List.of("f-1", "f-2");
        op.updateFileInfo("ORDER", "biz-1", ids);

        verify(fileInfoService, times(1)).updateFileInfoBusiness("ORDER", "biz-1", ids);
    }

    // -----------------------------------------------------------------------------------------
    // saveFileInfo
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("saveFileInfo：委托并转换")
    void saveFileInfo_delegatesWithConvert() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        FileInfo info = new FileInfo().setFileName("a.txt");
        op.saveFileInfo(List.of(info));

        verify(fileInfoService, times(1)).saveFileInfos(any());
    }

    // -----------------------------------------------------------------------------------------
    // deleteByFileIds
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("deleteByFileIds：直接委托")
    void deleteByFileIds_delegates() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        List<String> ids = List.of("f-1");
        op.deleteByFileIds(ids);

        verify(fileInfoService, times(1)).deleteByFileIds(ids);
    }

    // -----------------------------------------------------------------------------------------
    // findByNoBusiness
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("findByNoBusiness：委托并转换")
    void findByNoBusiness_converts() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        LocalDateTime t = LocalDateTime.now();
        TempFileInfoEntity e = new TempFileInfoEntity();
        e.setId("t-1");
        when(tempFileInfoService.findByNoBusiness(t)).thenReturn(List.of(e));

        List<TempFileInfo> result = op.findByNoBusiness(t);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("t-1", result.get(0).getId());
    }

    // -----------------------------------------------------------------------------------------
    // deleteTempFileInfo
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("deleteTempFileInfo：提取每个 TempFileInfo 的 id 并删除")
    void deleteTempFileInfo_extractsIdsAndDeletes() {
        WebClient wc = buildDeepStubWebClient();
        DefaultFileInfoOperation op = newOperation(wc);

        TempFileInfo t1 = new TempFileInfo().setId("t-1");
        TempFileInfo t2 = new TempFileInfo().setId("t-2");
        op.deleteTempFileInfo(List.of(t1, t2));

        verify(tempFileInfoService, times(1)).deleteTempFileInfo(List.of("t-1", "t-2"));
    }
}
