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
import cn.bbwres.biscuit.module.basic.controller.vo.TempFileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.dao.TempFileInfoMapper;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TempFileInfoServiceImpl} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TempFileInfoServiceImpl 单元测试")
class TempFileInfoServiceImplUnitTest {

    @Mock
    private TempFileInfoMapper tempFileInfoMapper;

    @InjectMocks
    private TempFileInfoServiceImpl service;

    @Test
    @DisplayName("getTempFileInfo：委托 mapper.selectById")
    void getTempFileInfo_delegatesToMapper() {
        TempFileInfoEntity e = new TempFileInfoEntity();
        e.setId("t-1");
        when(tempFileInfoMapper.selectById("t-1")).thenReturn(e);

        assertSame(e, service.getTempFileInfo("t-1"));
    }

    @Test
    @DisplayName("getTempFileInfo：不存在时透传 null")
    void getTempFileInfo_returnsNullWhenAbsent() {
        when(tempFileInfoMapper.selectById("nope")).thenReturn(null);
        assertNull(service.getTempFileInfo("nope"));
    }

    @Test
    @DisplayName("getTempFileInfoList：委托 mapper.selectByIds")
    void getTempFileInfoList_delegatesToMapper() {
        Collection<String> ids = List.of("1", "2");
        List<TempFileInfoEntity> expected = List.of(new TempFileInfoEntity(), new TempFileInfoEntity());
        when(tempFileInfoMapper.selectByIds(ids)).thenReturn(expected);

        assertSame(expected, service.getTempFileInfoList(ids));
    }

    @Test
    @DisplayName("getTempFileInfoList：空集合场景")
    void getTempFileInfoList_emptyCollection() {
        Collection<String> ids = Collections.emptyList();
        when(tempFileInfoMapper.selectByIds(ids)).thenReturn(Collections.emptyList());

        List<TempFileInfoEntity> actual = service.getTempFileInfoList(ids);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("getTempFileInfoPage：委托 mapper.selectPage")
    void getTempFileInfoPage_delegatesToMapper() {
        Page<TempFileInfoEntity, TempFileInfoPageReqVO> req = new Page<>();
        req.setCurrent(1);
        req.setSize(10);
        when(tempFileInfoMapper.selectPage(req)).thenReturn(req);

        Page<TempFileInfoEntity, TempFileInfoPageReqVO> result = service.getTempFileInfoPage(req);

        assertSame(req, result);
        verify(tempFileInfoMapper).selectPage(req);
    }

    @Test
    @DisplayName("saveTempFileInfo：委托 mapper.insert 并返回 entity")
    void saveTempFileInfo_delegatesAndReturnsEntity() {
        TempFileInfoEntity e = new TempFileInfoEntity();
        e.setFileName("a.txt");

        TempFileInfoEntity result = service.saveTempFileInfo(e);

        assertSame(e, result, "约定：saveTempFileInfo 应返回入参 entity 本身");
        verify(tempFileInfoMapper, times(1)).insert(e);
    }

    @Test
    @DisplayName("findTempFileInfoByIds：委托 mapper.selectByIds")
    void findTempFileInfoByIds_delegatesToMapper() {
        List<String> ids = List.of("1", "2");
        List<TempFileInfoEntity> expected = List.of(new TempFileInfoEntity());
        when(tempFileInfoMapper.selectByIds(ids)).thenReturn(expected);

        assertSame(expected, service.findTempFileInfoByIds(ids));
    }

    @Test
    @DisplayName("deleteByFileIds：委托 mapper.deleteByIds")
    void deleteByFileIds_delegatesToMapper() {
        List<String> ids = List.of("1", "2", "3");
        service.deleteByFileIds(ids);
        verify(tempFileInfoMapper, times(1)).deleteByIds(ids);
    }

    @Test
    @DisplayName("findByNoBusiness：委托 mapper.findByNoBusiness 并透传时间")
    void findByNoBusiness_delegatesWithTime() {
        LocalDateTime time = LocalDateTime.of(2025, 1, 1, 0, 0);
        List<TempFileInfoEntity> expected = List.of(new TempFileInfoEntity());
        when(tempFileInfoMapper.findByNoBusiness(time)).thenReturn(expected);

        assertSame(expected, service.findByNoBusiness(time));
    }

    @Test
    @DisplayName("findByNoBusiness：无数据时透传空集合")
    void findByNoBusiness_emptyResult() {
        LocalDateTime time = LocalDateTime.now();
        when(tempFileInfoMapper.findByNoBusiness(time)).thenReturn(Collections.emptyList());

        List<TempFileInfoEntity> result = service.findByNoBusiness(time);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("deleteTempFileInfo：委托 mapper.deleteByIds（与 deleteByFileIds 等价）")
    void deleteTempFileInfo_delegatesToMapper() {
        List<String> ids = List.of("1", "2");
        service.deleteTempFileInfo(ids);
        verify(tempFileInfoMapper, times(1)).deleteByIds(ids);
    }

    @Test
    @DisplayName("saveTempFileInfo：不应对 mapper 做任何额外调用")
    void saveTempFileInfo_noExtraCalls() {
        TempFileInfoEntity e = new TempFileInfoEntity();
        service.saveTempFileInfo(e);
        verify(tempFileInfoMapper, never()).findByNoBusiness(any());
        verify(tempFileInfoMapper, never()).deleteByIds(any());
    }
}
