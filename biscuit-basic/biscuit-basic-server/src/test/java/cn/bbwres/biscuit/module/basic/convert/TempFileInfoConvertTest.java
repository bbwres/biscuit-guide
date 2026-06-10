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

package cn.bbwres.biscuit.module.basic.convert;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.TempFileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.controller.vo.TempFileInfoRespVO;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MapStruct {@link TempFileInfoConvert} 单元测试。
 */
@DisplayName("TempFileInfoConvert MapStruct 转换测试")
class TempFileInfoConvertTest {

    private static TempFileInfoEntity buildEntity() {
        TempFileInfoEntity e = new TempFileInfoEntity();
        e.setId("temp-1");
        e.setFileName("tmp.png");
        e.setFileSuffix("png");
        e.setFileSize(2048L);
        e.setFilePath("/tmp/tmp.png");
        e.setFileStorageMenu("tmp");
        e.setFileStorageType("LOCAL");
        e.setFileHash("hash-1");
        e.setExt1("x1");
        e.setExt2("x2");
        e.setExt3("x3");
        e.setExt4("x4");
        return e;
    }

    @Test
    @DisplayName("convert(Entity) -> RespVO：所有字段映射")
    void convert_entityToRespVO_allFieldsMapped() {
        TempFileInfoEntity e = buildEntity();
        TempFileInfoRespVO vo = TempFileInfoConvert.INSTANCE.convert(e);

        assertAll(
                () -> assertNotNull(vo),
                () -> assertEquals("temp-1", vo.getId()),
                () -> assertEquals("tmp.png", vo.getFileName()),
                () -> assertEquals("png", vo.getFileSuffix()),
                () -> assertEquals(2048L, vo.getFileSize()),
                () -> assertEquals("/tmp/tmp.png", vo.getFilePath()),
                () -> assertEquals("tmp", vo.getFileStorageMenu()),
                () -> assertEquals("LOCAL", vo.getFileStorageType()),
                () -> assertEquals("hash-1", vo.getFileHash()),
                () -> assertEquals("x1", vo.getExt1()),
                () -> assertEquals("x2", vo.getExt2()),
                () -> assertEquals("x3", vo.getExt3()),
                () -> assertEquals("x4", vo.getExt4())
        );
    }

    @Test
    @DisplayName("convert(null) -> null")
    void convert_null_returnsNull() {
        assertNull(TempFileInfoConvert.INSTANCE.convert((TempFileInfoEntity) null));
    }

    @Test
    @DisplayName("convertList：保持顺序与数量")
    void convertList_preservesOrderAndSize() {
        TempFileInfoEntity a = buildEntity();
        a.setId("a");
        TempFileInfoEntity b = buildEntity();
        b.setId("b");
        List<TempFileInfoRespVO> vos = TempFileInfoConvert.INSTANCE.convertList(List.of(a, b));
        assertEquals(2, vos.size());
        assertEquals("a", vos.get(0).getId());
        assertEquals("b", vos.get(1).getId());
    }

    @Test
    @DisplayName("convertList(null) / convertList(空集合)")
    void convertList_nullAndEmpty() {
        assertNull(TempFileInfoConvert.INSTANCE.convertList(null));
        List<TempFileInfoRespVO> vos = TempFileInfoConvert.INSTANCE.convertList(Collections.emptyList());
        assertNotNull(vos);
        assertTrue(vos.isEmpty());
    }

    @Test
    @DisplayName("convertPage：records/query/total/size/current/pages 全部透传")
    void convertPage_passesThrough() {
        TempFileInfoPageReqVO q = new TempFileInfoPageReqVO();
        q.setFileSuffix("png");
        Page<TempFileInfoEntity, TempFileInfoPageReqVO> src = new Page<>();
        src.setQuery(q);
        src.setRecords(List.of(buildEntity()));
        src.setTotal(7);
        src.setSize(1);
        src.setCurrent(1);
        src.setPages(7);

        Page<TempFileInfoRespVO, TempFileInfoPageReqVO> dest =
                TempFileInfoConvert.INSTANCE.convertPage(src);

        assertAll(
                () -> assertNotNull(dest),
                () -> assertEquals(1, dest.getRecords().size()),
                () -> assertEquals(7L, dest.getTotal()),
                () -> assertEquals(1L, dest.getSize()),
                () -> assertEquals(1L, dest.getCurrent()),
                () -> assertEquals(7L, dest.getPages()),
                () -> assertSame(q, dest.getQuery())
        );
    }

    @Test
    @DisplayName("convertPage(null) -> null")
    void convertPage_null_returnsNull() {
        assertNull(TempFileInfoConvert.INSTANCE.convertPage(null));
    }

    @Test
    @DisplayName("convertByInfo：TempFileInfo -> Entity 字段映射")
    void convertByInfo_mapsAllFields() {
        TempFileInfo info = new TempFileInfo();
        info.setId("t-1");
        info.setFileName("a.log");
        info.setFileSuffix("log");
        info.setFileSize(64L);
        info.setFilePath("/a.log");
        info.setFileStorageMenu("logs");
        info.setFileStorageType("LOCAL");
        info.setFileHash("hhh");
        info.setExt1("e1");
        info.setExt2("e2");
        info.setExt3("e3");
        info.setExt4("e4");

        TempFileInfoEntity e = TempFileInfoConvert.INSTANCE.convertByInfo(info);
        assertAll(
                () -> assertNotNull(e),
                () -> assertEquals("t-1", e.getId()),
                () -> assertEquals("a.log", e.getFileName()),
                () -> assertEquals("log", e.getFileSuffix()),
                () -> assertEquals(64L, e.getFileSize()),
                () -> assertEquals("/a.log", e.getFilePath()),
                () -> assertEquals("logs", e.getFileStorageMenu()),
                () -> assertEquals("LOCAL", e.getFileStorageType()),
                () -> assertEquals("hhh", e.getFileHash()),
                () -> assertEquals("e1", e.getExt1()),
                () -> assertEquals("e2", e.getExt2()),
                () -> assertEquals("e3", e.getExt3()),
                () -> assertEquals("e4", e.getExt4())
        );
    }

    @Test
    @DisplayName("convertByInfo(null) -> null")
    void convertByInfo_null_returnsNull() {
        assertNull(TempFileInfoConvert.INSTANCE.convertByInfo(null));
    }

    @Test
    @DisplayName("convert2Info：List<Entity> -> List<TempFileInfo>")
    void convert2Info_mapsAllFields() {
        List<TempFileInfo> infos = TempFileInfoConvert.INSTANCE.convert2Info(List.of(buildEntity()));
        assertNotNull(infos);
        assertEquals(1, infos.size());
        TempFileInfo info = infos.get(0);
        assertEquals("temp-1", info.getId());
        assertEquals("tmp.png", info.getFileName());
        assertEquals("hash-1", info.getFileHash());
    }

    @Test
    @DisplayName("convert2Info(null) -> null")
    void convert2Info_null_returnsNull() {
        assertNull(TempFileInfoConvert.INSTANCE.convert2Info(null));
    }
}
