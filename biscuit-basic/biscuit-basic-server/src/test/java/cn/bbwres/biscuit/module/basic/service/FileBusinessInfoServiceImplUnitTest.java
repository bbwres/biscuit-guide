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
import cn.bbwres.biscuit.module.basic.controller.vo.FileBusinessInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.dao.FileBusinessInfoMapper;
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link FileBusinessInfoServiceImpl} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileBusinessInfoServiceImpl 单元测试")
class FileBusinessInfoServiceImplUnitTest {

    @Mock
    private FileBusinessInfoMapper fileBusinessInfoMapper;

    @InjectMocks
    private FileBusinessInfoServiceImpl service;

    @Test
    @DisplayName("getFileBusinessInfo：委托 mapper.selectById")
    void getFileBusinessInfo_delegatesToMapper() {
        FileBusinessInfoEntity e = new FileBusinessInfoEntity();
        e.setId("biz-1");
        when(fileBusinessInfoMapper.selectById("biz-1")).thenReturn(e);

        assertSame(e, service.getFileBusinessInfo("biz-1"));
        verify(fileBusinessInfoMapper).selectById("biz-1");
    }

    @Test
    @DisplayName("getFileBusinessInfo：mapper 返回 null 时透传 null")
    void getFileBusinessInfo_returnsNullWhenAbsent() {
        when(fileBusinessInfoMapper.selectById("missing")).thenReturn(null);
        assertNull(service.getFileBusinessInfo("missing"));
    }

    @Test
    @DisplayName("getFileBusinessInfoList：委托 mapper.selectByIds")
    void getFileBusinessInfoList_delegatesToMapper() {
        Collection<String> ids = List.of("1", "2");
        List<FileBusinessInfoEntity> expected = List.of(new FileBusinessInfoEntity(), new FileBusinessInfoEntity());
        when(fileBusinessInfoMapper.selectByIds(ids)).thenReturn(expected);

        assertSame(expected, service.getFileBusinessInfoList(ids));
    }

    @Test
    @DisplayName("getFileBusinessInfoList：空集合时 mapper 返回空集合")
    void getFileBusinessInfoList_emptyCollection() {
        Collection<String> ids = Collections.emptyList();
        when(fileBusinessInfoMapper.selectByIds(ids)).thenReturn(Collections.emptyList());

        List<FileBusinessInfoEntity> actual = service.getFileBusinessInfoList(ids);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("getFileBusinessInfoPage：委托 mapper.selectPage")
    void getFileBusinessInfoPage_delegatesToMapper() {
        Page<FileBusinessInfoEntity, FileBusinessInfoPageReqVO> req = new Page<>();
        req.setCurrent(1);
        req.setSize(10);
        when(fileBusinessInfoMapper.selectPage(req)).thenReturn(req);

        Page<FileBusinessInfoEntity, FileBusinessInfoPageReqVO> result = service.getFileBusinessInfoPage(req);

        assertSame(req, result);
        verify(fileBusinessInfoMapper).selectPage(req);
    }

    @Test
    @DisplayName("findByBusinessType：正常返回 mapper 结果")
    void findByBusinessType_returnsEntity() {
        FileBusinessInfoEntity e = new FileBusinessInfoEntity();
        e.setBusinessType("ORDER");
        when(fileBusinessInfoMapper.findByBusinessType("ORDER")).thenReturn(e);

        assertSame(e, service.findByBusinessType("ORDER"));
    }

    @Test
    @DisplayName("findByBusinessType：未配置时透传 null（约定）")
    void findByBusinessType_returnsNullWhenAbsent() {
        when(fileBusinessInfoMapper.findByBusinessType("UNKNOWN")).thenReturn(null);
        assertNull(service.findByBusinessType("UNKNOWN"));
    }
}
