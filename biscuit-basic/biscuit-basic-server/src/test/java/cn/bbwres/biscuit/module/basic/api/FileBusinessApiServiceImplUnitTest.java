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
import cn.bbwres.biscuit.web.file.api.FileBusinessOperation;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessExpandParams;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessParams;
import cn.bbwres.biscuit.web.file.api.vo.FileBindBusinessRefOldParams;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link FileBusinessApiServiceImpl} 单元测试。
 *
 * <p>重点覆盖：
 * <ul>
 *   <li>{@code getFileInputStream} 注释里要求"避免超过 20MB 的文件"，但当前实现没有做任何大小校验；
 *       测试用 20MB+ 的 InputStream 调用，结果能正常返回 ——
 *       <b>证明：实现没有 20MB 限制，需要在源代码里补齐</b>。</li>
 *   <li>{@code deleteFileByBusinessInfo} 没有对入参做空值校验，测试用空 VO 也能调到下游；
 *       <b>标记：建议加 {@code @NotNull} 或前置校验</b>。</li>
 * </ul>
 * </p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileBusinessApiServiceImpl 单元测试")
class FileBusinessApiServiceImplUnitTest {

    @Mock
    private FileBusinessOperation fileBusinessOperation;

    private FileBusinessApiServiceImpl serviceUnderTest;

    @BeforeEach
    void setUp() {
        serviceUnderTest = new FileBusinessApiServiceImpl();
        serviceUnderTest.setFileBusinessOperation(fileBusinessOperation);
    }

    // -----------------------------------------------------------------------------------------
    // fileBindBusiness
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("fileBindBusiness：构造 ExpandParams 并委托下游，返回 Result.success")
    void fileBindBusiness_buildsParamsAndDelegates() {
        FileBindBusinessParamsVO vo = new FileBindBusinessParamsVO()
                .setBusinessId("biz-1")
                .setBusinessType("ORDER")
                .setFileIds(List.of("f-1", "f-2"))
                .setDeleteHistory(Boolean.TRUE);

        Result<Void> result = serviceUnderTest.fileBindBusiness(vo);

        ArgumentCaptor<FileBindBusinessExpandParams> captor =
                ArgumentCaptor.forClass(FileBindBusinessExpandParams.class);
        verify(fileBusinessOperation, times(1)).fileBindBusiness(captor.capture());

        FileBindBusinessExpandParams sent = captor.getValue();
        assertAll(
                () -> assertEquals("biz-1", sent.getBusinessId()),
                () -> assertEquals("ORDER", sent.getBusinessType()),
                () -> assertEquals(List.of("f-1", "f-2"), sent.getFileIds()),
                () -> assertEquals(Boolean.TRUE, sent.getDeleteHistory())
        );

        assertNotNull(result);
        assertTrue(result.checkSuccess());
    }

    // -----------------------------------------------------------------------------------------
    // fileBindBusinessChange
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("fileBindBusinessChange：构造 RefOldParams 并委托下游")
    void fileBindBusinessChange_buildsParamsAndDelegates() {
        FileBindBusinessChangeParamsVO vo = new FileBindBusinessChangeParamsVO()
                .setBusinessId("new-biz")
                .setBusinessType("NEW_TYPE")
                .setFileIds(List.of("f-1"))
                .setOldBusinessId("old-biz")
                .setOldBusinessType("OLD_TYPE");

        Result<Void> result = serviceUnderTest.fileBindBusinessChange(vo);

        ArgumentCaptor<FileBindBusinessRefOldParams> captor =
                ArgumentCaptor.forClass(FileBindBusinessRefOldParams.class);
        verify(fileBusinessOperation, times(1)).fileBindBusinessChange(captor.capture());

        FileBindBusinessRefOldParams sent = captor.getValue();
        assertAll(
                () -> assertEquals("new-biz", sent.getBusinessId()),
                () -> assertEquals("NEW_TYPE", sent.getBusinessType()),
                () -> assertEquals(List.of("f-1"), sent.getFileIds()),
                () -> assertEquals("old-biz", sent.getOldBusinessId()),
                () -> assertEquals("OLD_TYPE", sent.getOldBusinessType())
        );
        assertNotNull(result);
        assertTrue(result.checkSuccess());
    }

    // -----------------------------------------------------------------------------------------
    // fileBindBusinessCopy
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("fileBindBusinessCopy：构造 RefOldParams 并委托下游")
    void fileBindBusinessCopy_buildsParamsAndDelegates() {
        FileBindBusinessChangeParamsVO vo = new FileBindBusinessChangeParamsVO()
                .setBusinessId("dst-biz")
                .setBusinessType("DST")
                .setFileIds(List.of("f-1", "f-2"))
                .setOldBusinessId("src-biz")
                .setOldBusinessType("SRC");

        Result<Void> result = serviceUnderTest.fileBindBusinessCopy(vo);

        ArgumentCaptor<FileBindBusinessRefOldParams> captor =
                ArgumentCaptor.forClass(FileBindBusinessRefOldParams.class);
        verify(fileBusinessOperation, times(1)).fileBindBusinessCopy(captor.capture());

        FileBindBusinessRefOldParams sent = captor.getValue();
        assertEquals("dst-biz", sent.getBusinessId());
        assertEquals("src-biz", sent.getOldBusinessId());
        assertNotNull(result);
        assertTrue(result.checkSuccess());
    }

    // -----------------------------------------------------------------------------------------
    // deleteFileByBusinessInfo  — 入参空值校验是缺失的
    // -----------------------------------------------------------------------------------------

    @Nested
    @DisplayName("deleteFileByBusinessInfo 入参校验")
    class DeleteFileByBusinessInfo {

        @Test
        @DisplayName("正常入参：构造 Params 并委托")
        void normalParams() {
            FileBindBusinessParamsVO vo = new FileBindBusinessParamsVO()
                    .setBusinessId("biz-1")
                    .setBusinessType("ORDER")
                    .setFileIds(List.of("f-1", "f-2"));

            Result<Void> result = serviceUnderTest.deleteFileByBusinessInfo(vo);

            ArgumentCaptor<FileBindBusinessParams> captor =
                    ArgumentCaptor.forClass(FileBindBusinessParams.class);
            verify(fileBusinessOperation, times(1)).deleteFileByBusinessInfo(captor.capture());

            FileBindBusinessParams sent = captor.getValue();
            assertEquals("biz-1", sent.getBusinessId());
            assertEquals("ORDER", sent.getBusinessType());
            assertEquals(List.of("f-1", "f-2"), sent.getFileIds());
            assertNotNull(result);
            assertTrue(result.checkSuccess());
        }

        @Test
        @DisplayName("[缺陷] 入参为 null 时不抛异常，错误地直接调用下游 — 缺少前置校验")
        void nullParamStillInvokesDownstream() {
            // 期望：抛 IllegalArgumentException 或者返回错误 Result
            // 实际：当前实现未做空值校验，会以 NPE 失败，或下游收到一个全空 params
            org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                    () -> serviceUnderTest.deleteFileByBusinessInfo(null));
        }

        @Test
        @DisplayName("[缺陷] 全空字段的 VO 也能调到下游 — 缺少业务字段非空校验")
        void emptyVoStillInvokesDownstream() {
            FileBindBusinessParamsVO vo = new FileBindBusinessParamsVO();

            Result<Void> result = serviceUnderTest.deleteFileByBusinessInfo(vo);

            // 当前实现：三个字段均为 null 仍会创建一个 Params 对象并调用下游
            verify(fileBusinessOperation, times(1)).deleteFileByBusinessInfo(any());
            assertTrue(result.checkSuccess(), "实现不校验空值；建议增加 @NotNull 校验");
        }
    }

    // -----------------------------------------------------------------------------------------
    // findFileInfoByBusiness
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("findFileInfoByBusiness：委托并转换为 FileInfoResultVO 列表")
    void findFileInfoByBusiness_convertsResult() {
        FileInfo src = new FileInfo()
                .setId("f-1")
                .setFileName("a.png")
                .setFileHash("hash-1");
        when(fileBusinessOperation.findFileInfoByBusiness(any(FileBindBusinessParams.class)))
                .thenReturn(List.of(src));

        FileBindBusinessParamsVO vo = new FileBindBusinessParamsVO()
                .setBusinessId("biz-1")
                .setBusinessType("ORDER");

        Result<List<FileInfoResultVO>> result = serviceUnderTest.findFileInfoByBusiness(vo);

        assertNotNull(result);
        assertTrue(result.checkSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("f-1", result.getData().get(0).getId());
        assertEquals("a.png", result.getData().get(0).getFileName());
        assertEquals("hash-1", result.getData().get(0).getFileHash());
    }

    @Test
    @DisplayName("findFileInfoByBusiness：下游返回空集合时 Result.data 是空列表")
    void findFileInfoByBusiness_empty() {
        when(fileBusinessOperation.findFileInfoByBusiness(any(FileBindBusinessParams.class)))
                .thenReturn(Collections.emptyList());

        FileBindBusinessParamsVO vo = new FileBindBusinessParamsVO()
                .setBusinessId("biz-1")
                .setBusinessType("ORDER");

        Result<List<FileInfoResultVO>> result = serviceUnderTest.findFileInfoByBusiness(vo);

        assertNotNull(result);
        assertTrue(result.checkSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    // -----------------------------------------------------------------------------------------
    // deleteTempFile
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("deleteTempFile：直接委托下游")
    void deleteTempFile_delegates() {
        serviceUnderTest.deleteTempFile();
        verify(fileBusinessOperation, times(1)).deleteTempFile();
    }

    // -----------------------------------------------------------------------------------------
    // getFileInputStream — 缺少 20MB 限制（缺陷）
    // -----------------------------------------------------------------------------------------

    @Nested
    @DisplayName("getFileInputStream 20MB 限制 — 缺陷标记")
    class GetFileInputStream20MbLimit {

        @Test
        @DisplayName("正常：InputStream 转 byte[] 并返回 success")
        void normalBytesReturn() throws IOException {
            byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
            when(fileBusinessOperation.getFileInputStream("ORDER", "biz-1", "f-1"))
                    .thenReturn(new ByteArrayInputStream(data));

            Result<byte[]> result = serviceUnderTest.getFileInputStream("ORDER", "biz-1", "f-1");

            assertNotNull(result);
            assertTrue(result.checkSuccess());
            assertArrayEquals(data, result.getData());
            verify(fileBusinessOperation, times(1)).getFileInputStream("ORDER", "biz-1", "f-1");
        }

        @Test
        @DisplayName("异常：下游抛异常时返回 Result.error(FILE_GET_ERROR)")
        void returnsErrorOnException() {
            when(fileBusinessOperation.getFileInputStream("ORDER", "biz-1", "f-1"))
                    .thenThrow(new RuntimeException("io error"));

            Result<byte[]> result = serviceUnderTest.getFileInputStream("ORDER", "biz-1", "f-1");

            assertNotNull(result);
            assertEquals(BasicErrorCodeConstants.FILE_GET_ERROR.getCode(), result.getResultCode());
            assertNull(result.getData());
        }

        @Test
        @DisplayName("空流：返回成功，data 为空数组（不抛异常）")
        void emptyStream() {
            when(fileBusinessOperation.getFileInputStream("ORDER", "biz-1", "f-1"))
                    .thenReturn(new ByteArrayInputStream(new byte[0]));

            Result<byte[]> result = serviceUnderTest.getFileInputStream("ORDER", "biz-1", "f-1");

            assertNotNull(result);
            assertTrue(result.checkSuccess());
            assertNotNull(result.getData());
            assertEquals(0, result.getData().length);
        }

        /**
         * 关键缺陷测试：当前实现没有 20MB 限制。
         *
         * <p>如果未来修复（增加 {@code if (data.length > 20 * 1024 * 1024) return Result.error(...);}），
         * 这条用例需要相应更新为断言返回错误码。</p>
         */
        @Test
        @DisplayName("[缺陷] 21MB 输入流能直接返回 — 实现没有 20MB 限制，应当前置拦截")
        void noLimitOver20Mb_knownIssue() {
            // 21MB 数据
            int size = 21 * 1024 * 1024;
            byte[] big = new byte[size];
            for (int i = 0; i < size; i++) {
                big[i] = (byte) (i & 0xFF);
            }
            when(fileBusinessOperation.getFileInputStream("ORDER", "biz-1", "big"))
                    .thenReturn(new ByteArrayInputStream(big));

            Result<byte[]> result = serviceUnderTest.getFileInputStream("ORDER", "biz-1", "big");

            // 当前实现不限制大小，能正常返回 21MB
            assertNotNull(result);
            assertTrue(result.checkSuccess(),
                    "实现目前能成功返回超过 20MB 的文件 —— 这就是缺陷：方法注释要求 '需要避免超过 20m 的文件'，但代码没有强制");
            assertEquals(size, result.getData().length);

            // 期望（修复后）：
            //   assertEquals(BasicErrorCodeConstants.FILE_GET_ERROR.getCode(), result.getResultCode());
            //   assertNull(result.getData());
        }

        /**
         * 边界测试：正好 20MB 不应当被拦截（仅 > 20MB 才算超标）。
         *
         * <p>同样，这条用例只在修复后才有意义。当前实现无论如何都通过，所以保持其作为对照。</p>
         */
        @Test
        @DisplayName("边界：正好 20MB 时通过（实现当前对所有大小都通过）")
        void exactly20MbAllowed() {
            int size = 20 * 1024 * 1024;
            byte[] data = new byte[size];
            when(fileBusinessOperation.getFileInputStream(any(), any(), any()))
                    .thenReturn(new ByteArrayInputStream(data));

            Result<byte[]> result = serviceUnderTest.getFileInputStream("ORDER", "biz-1", "f");

            assertNotNull(result);
            assertTrue(result.checkSuccess());
            assertEquals(size, result.getData().length);
        }
    }

    // -----------------------------------------------------------------------------------------
    // 集成性：IOUtils.toByteArray 不会关闭非 ByteArrayInputStream 之外的资源
    // 验证 try-with-resources 不会泄漏 stream
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("getFileInputStream：try-with-resources 应关闭 InputStream")
    void getFileInputStream_closesStream() throws IOException {
        // 使用一个会标记关闭的 mock stream
        InputStream mockStream = mock(InputStream.class);
        // IOUtils.copyLarge 调用 read(byte[], int, int)，匹配三参重载
        when(mockStream.read(any(byte[].class), anyInt(), anyInt())).thenReturn(-1); // 立即 EOF
        when(fileBusinessOperation.getFileInputStream("ORDER", "biz-1", "f"))
                .thenReturn(mockStream);

        Result<byte[]> result = serviceUnderTest.getFileInputStream("ORDER", "biz-1", "f");

        assertNotNull(result);
        assertTrue(result.checkSuccess());
        // try-with-resources 必须调用 close()
        verify(mockStream, atLeastOnce()).close();
    }

    // -----------------------------------------------------------------------------------------
    // 兜底：fileBusinessOperation 为 null 时的行为（不期望出现，仅作现状记录）
    // -----------------------------------------------------------------------------------------

    @Test
    @DisplayName("[缺陷] 忘记 setFileBusinessOperation 时调用方法抛 NPE，没有友好的错误提示")
    void missingDependencyThrowsNpe() {
        FileBusinessApiServiceImpl naked = new FileBusinessApiServiceImpl();
        FileBindBusinessParamsVO vo = new FileBindBusinessParamsVO().setBusinessId("b").setBusinessType("t");

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> naked.fileBindBusiness(vo));
    }
}
