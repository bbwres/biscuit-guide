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

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.FileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.dao.FileInfoMapper;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link FileInfoServiceImpl} 单元测试。
 *
 * <p>纯 Mockito 测试，不依赖 Spring 容器。覆盖全部 8 个公共方法。</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileInfoServiceImpl 单元测试")
class FileInfoServiceImplUnitTest {

    @Mock
    private FileInfoMapper fileInfoMapper;

    @InjectMocks
    private FileInfoServiceImpl fileInfoService;

    @BeforeEach
    void setUp() {
        // @InjectMocks 自动注入
    }

    @Test
    @DisplayName("getFileInfo：委托 mapper.selectById")
    void getFileInfo_delegatesToMapper() {
        FileInfoEntity expected = new FileInfoEntity();
        expected.setId("f-1");
        when(fileInfoMapper.selectById("f-1")).thenReturn(expected);

        FileInfoEntity actual = fileInfoService.getFileInfo("f-1");

        assertSame(expected, actual);
        verify(fileInfoMapper, times(1)).selectById("f-1");
    }

    @Test
    @DisplayName("getFileInfo：mapper 返回 null 时透传 null")
    void getFileInfo_returnsNullWhenMapperReturnsNull() {
        when(fileInfoMapper.selectById("missing")).thenReturn(null);
        assertNull(fileInfoService.getFileInfo("missing"));
    }

    @Test
    @DisplayName("getFileInfoList：委托 mapper.selectByIds")
    void getFileInfoList_delegatesToMapper() {
        List<FileInfoEntity> expected = List.of(new FileInfoEntity(), new FileInfoEntity());
        Collection<String> ids = List.of("a", "b");
        when(fileInfoMapper.selectByIds(ids)).thenReturn(expected);

        List<FileInfoEntity> actual = fileInfoService.getFileInfoList(ids);

        assertSame(expected, actual);
        verify(fileInfoMapper).selectByIds(ids);
    }

    @Test
    @DisplayName("getFileInfoList：空集合时 mapper 返回空集合")
    void getFileInfoList_emptyCollection() {
        Collection<String> ids = Collections.emptyList();
        when(fileInfoMapper.selectByIds(ids)).thenReturn(Collections.emptyList());

        List<FileInfoEntity> actual = fileInfoService.getFileInfoList(ids);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("getFileInfoPage：委托 mapper.selectPage 并返回其结果")
    void getFileInfoPage_delegatesToMapper() {
        Page<FileInfoEntity, FileInfoPageReqVO> req = new Page<>();
        req.setCurrent(1);
        req.setSize(10);
        when(fileInfoMapper.selectPage(req)).thenReturn(req);

        Page<FileInfoEntity, FileInfoPageReqVO> result = fileInfoService.getFileInfoPage(req);

        assertSame(req, result);
        verify(fileInfoMapper).selectPage(req);
    }

    @Test
    @DisplayName("findByBusinessAndId：正常返回 mapper 结果")
    void findByBusinessAndId_returnsList() {
        FileInfoEntity e1 = new FileInfoEntity();
        e1.setId("1");
        FileInfoEntity e2 = new FileInfoEntity();
        e2.setId("2");
        List<FileInfoEntity> expected = List.of(e1, e2);
        when(fileInfoMapper.findByBusinessAndId("ORDER", "biz-1", "1", "2")).thenReturn(expected);

        List<FileInfoEntity> result = fileInfoService.findByBusinessAndId("ORDER", "biz-1", "1", "2");

        assertSame(expected, result);
        // Mockito 不允许混合具体值和 matcher：全部用具体值
        verify(fileInfoMapper).findByBusinessAndId("ORDER", "biz-1", "1", "2");
    }

    @Test
    @DisplayName("findByBusinessAndId：契约 - mapper 返回空集合时透传空集合（NOT null）")
    void findByBusinessAndId_emptyListContract() {
        when(fileInfoMapper.findByBusinessAndId(eq("ORDER"), eq("biz-1"), any()))
                .thenReturn(Collections.emptyList());

        List<FileInfoEntity> result = fileInfoService.findByBusinessAndId("ORDER", "biz-1", "x");

        assertNotNull(result, "契约：Service 必须透传 mapper 的空集合，不应转为 null");
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("findByBusinessAndId：可变参数 fileId 为空时仍能调用")
    void findByBusinessAndId_emptyFileId() {
        when(fileInfoMapper.findByBusinessAndId("ORDER", "biz-1")).thenReturn(Collections.emptyList());

        List<FileInfoEntity> result = fileInfoService.findByBusinessAndId("ORDER", "biz-1");

        assertNotNull(result);
        verify(fileInfoMapper).findByBusinessAndId("ORDER", "biz-1");
    }

    @Test
    @DisplayName("findByFileHashOne：委托 mapper")
    void findByFileHashOne_delegatesToMapper() {
        FileInfoEntity e = new FileInfoEntity();
        when(fileInfoMapper.findByFileHashOne("hash-1")).thenReturn(e);

        assertSame(e, fileInfoService.findByFileHashOne("hash-1"));
    }

    @Test
    @DisplayName("findByFileHashOne：未找到时透传 null")
    void findByFileHashOne_returnsNullWhenAbsent() {
        when(fileInfoMapper.findByFileHashOne("absent")).thenReturn(null);
        assertNull(fileInfoService.findByFileHashOne("absent"));
    }

    @Test
    @DisplayName("saveFileInfos：委托 mapper.insert")
    void saveFileInfos_delegatesToMapper() {
        List<FileInfoEntity> list = List.of(new FileInfoEntity(), new FileInfoEntity());
        fileInfoService.saveFileInfos(list);
        verify(fileInfoMapper, times(1)).insert(list);
    }

    @Test
    @DisplayName("saveFileInfos：空列表也透传给 mapper（不拦截）")
    void saveFileInfos_emptyListStillInvokesMapper() {
        fileInfoService.saveFileInfos(Collections.emptyList());
        verify(fileInfoMapper).insert(Collections.emptyList());
    }

    @Test
    @DisplayName("deleteByFileIds：委托 mapper.deleteByIds")
    void deleteByFileIds_delegatesToMapper() {
        List<String> ids = List.of("1", "2", "3");
        fileInfoService.deleteByFileIds(ids);
        verify(fileInfoMapper, times(1)).deleteByIds(ids);
    }

    @Test
    @DisplayName("updateFileInfoBusiness：委托 mapper.updateFileInfoBusiness")
    void updateFileInfoBusiness_delegatesToMapper() {
        List<String> ids = List.of("1", "2");
        fileInfoService.updateFileInfoBusiness("ORDER", "biz-1", ids);
        verify(fileInfoMapper).updateFileInfoBusiness("ORDER", "biz-1", ids);
    }

    @Test
    @DisplayName("getFileInfo 不传递调用其他 mapper 方法")
    void getFileInfo_doesNotTouchOtherMapperMethods() {
        when(fileInfoMapper.selectById("x")).thenReturn(null);
        fileInfoService.getFileInfo("x");
        verify(fileInfoMapper, never()).deleteByIds(any());
        verify(fileInfoMapper, never()).updateFileInfoBusiness(any(), any(), any());
    }
}
