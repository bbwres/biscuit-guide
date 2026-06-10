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
import cn.bbwres.biscuit.module.basic.api.vo.FileInfoResultVO;
import cn.bbwres.biscuit.module.basic.controller.vo.FileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.controller.vo.FileInfoRespVO;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
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
 * MapStruct {@link FileInfoConvert} 单元测试。
 *
 * <p>覆盖每个转换方法的字段映射完整性，包括单对象、列表、分页、Info 互转、null 入参。</p>
 */
@DisplayName("FileInfoConvert MapStruct 转换测试")
class FileInfoConvertTest {

    private static FileInfoEntity buildEntity() {
        FileInfoEntity e = new FileInfoEntity();
        e.setId("file-1");
        e.setFileName("订单.pdf");
        e.setFileSuffix("pdf");
        e.setFileSize(1024L);
        e.setBusinessId("biz-1");
        e.setBusinessType("ORDER");
        e.setFilePath("/tmp/order.pdf");
        e.setFileStorageMenu("default");
        e.setFileStorageType("LOCAL");
        e.setFileHash("abc123");
        e.setSrcFileId("src-1");
        e.setExt1("e1");
        e.setExt2("e2");
        e.setExt3("e3");
        e.setExt4("e4");
        return e;
    }

    @Test
    @DisplayName("convert(Entity) -> RespVO：所有字段一一映射")
    void convert_entityToRespVO_allFieldsMapped() {
        FileInfoEntity e = buildEntity();
        FileInfoRespVO vo = FileInfoConvert.INSTANCE.convert(e);

        assertAll(
                () -> assertNotNull(vo),
                () -> assertEquals("file-1", vo.getId()),
                () -> assertEquals("订单.pdf", vo.getFileName()),
                () -> assertEquals("pdf", vo.getFileSuffix()),
                () -> assertEquals(1024L, vo.getFileSize()),
                () -> assertEquals("biz-1", vo.getBusinessId()),
                () -> assertEquals("ORDER", vo.getBusinessType()),
                () -> assertEquals("/tmp/order.pdf", vo.getFilePath()),
                () -> assertEquals("default", vo.getFileStorageMenu()),
                () -> assertEquals("LOCAL", vo.getFileStorageType()),
                () -> assertEquals("abc123", vo.getFileHash()),
                () -> assertEquals("src-1", vo.getSrcFileId()),
                () -> assertEquals("e1", vo.getExt1()),
                () -> assertEquals("e2", vo.getExt2()),
                () -> assertEquals("e3", vo.getExt3()),
                () -> assertEquals("e4", vo.getExt4())
        );
    }

    @Test
    @DisplayName("convert(null) -> null：空入参安全")
    void convert_null_returnsNull() {
        assertNull(FileInfoConvert.INSTANCE.convert((FileInfoEntity) null));
    }

    @Test
    @DisplayName("convertList(List<Entity>)：保持顺序与数量")
    void convertList_preservesOrderAndSize() {
        FileInfoEntity e1 = buildEntity();
        e1.setId("1");
        FileInfoEntity e2 = buildEntity();
        e2.setId("2");
        List<FileInfoRespVO> vos = FileInfoConvert.INSTANCE.convertList(List.of(e1, e2));
        assertNotNull(vos);
        assertEquals(2, vos.size());
        assertEquals("1", vos.get(0).getId());
        assertEquals("2", vos.get(1).getId());
    }

    @Test
    @DisplayName("convertList(null) -> null")
    void convertList_null_returnsNull() {
        assertNull(FileInfoConvert.INSTANCE.convertList(null));
    }

    @Test
    @DisplayName("convertList(空集合) -> 空集合")
    void convertList_empty_returnsEmpty() {
        List<FileInfoRespVO> vos = FileInfoConvert.INSTANCE.convertList(Collections.emptyList());
        assertNotNull(vos);
        assertTrue(vos.isEmpty());
    }

    @Test
    @DisplayName("convertPage：records / query / total / size / current / pages 全部透传")
    void convertPage_passesThrough() {
        FileInfoPageReqVO q = new FileInfoPageReqVO();
        q.setBusinessType("ORDER");
        Page<FileInfoEntity, FileInfoPageReqVO> src = new Page<>();
        src.setQuery(q);
        src.setRecords(List.of(buildEntity()));
        src.setTotal(100);
        src.setSize(10);
        src.setCurrent(2);
        src.setPages(10);

        Page<FileInfoRespVO, FileInfoPageReqVO> dest = FileInfoConvert.INSTANCE.convertPage(src);
        assertNotNull(dest);
        assertEquals(1, dest.getRecords().size());
        assertEquals(100L, dest.getTotal());
        assertEquals(10L, dest.getSize());
        assertEquals(2L, dest.getCurrent());
        assertEquals(10L, dest.getPages());
        assertSame(q, dest.getQuery());
    }

    @Test
    @DisplayName("convertPage(null) -> null")
    void convertPage_null_returnsNull() {
        assertNull(FileInfoConvert.INSTANCE.convertPage(null));
    }

    @Test
    @DisplayName("convertList2Info + convert2Info：Entity -> FileInfo 字段映射")
    void convertList2Info_andConvert2Info() {
        FileInfoEntity e = buildEntity();
        FileInfo info = FileInfoConvert.INSTANCE.convert2Info(e);
        assertNotNull(info);
        assertEquals("file-1", info.getId());
        assertEquals("订单.pdf", info.getFileName());
        assertEquals("abc123", info.getFileHash());
        assertEquals("src-1", info.getSrcFileId());
        assertEquals("e4", info.getExt4());

        List<FileInfo> infos = FileInfoConvert.INSTANCE.convertList2Info(List.of(e));
        assertNotNull(infos);
        assertEquals(1, infos.size());
    }

    @Test
    @DisplayName("convertList2Info(null) / convert2Info(null) -> null")
    void convertList2Info_nullSafe() {
        assertNull(FileInfoConvert.INSTANCE.convertList2Info(null));
        assertNull(FileInfoConvert.INSTANCE.convert2Info(null));
    }

    @Test
    @DisplayName("convertInfoList2Entity：FileInfo -> Entity 字段映射")
    void convertInfoList2Entity_mapsAllFields() {
        FileInfo src = new FileInfo();
        src.setId("f-x");
        src.setFileName("n.txt");
        src.setFileSuffix("txt");
        src.setFileSize(8L);
        src.setBusinessId("b");
        src.setBusinessType("T");
        src.setFilePath("/p");
        src.setFileStorageMenu("m");
        src.setFileStorageType("S3");
        src.setFileHash("hh");
        src.setSrcFileId("s");
        src.setExt1("1");
        src.setExt2("2");
        src.setExt3("3");
        src.setExt4("4");

        List<FileInfoEntity> entities = FileInfoConvert.INSTANCE.convertInfoList2Entity(List.of(src));
        assertNotNull(entities);
        assertEquals(1, entities.size());
        FileInfoEntity e = entities.get(0);
        assertAll(
                () -> assertEquals("f-x", e.getId()),
                () -> assertEquals("n.txt", e.getFileName()),
                () -> assertEquals("txt", e.getFileSuffix()),
                () -> assertEquals(8L, e.getFileSize()),
                () -> assertEquals("b", e.getBusinessId()),
                () -> assertEquals("T", e.getBusinessType()),
                () -> assertEquals("/p", e.getFilePath()),
                () -> assertEquals("m", e.getFileStorageMenu()),
                () -> assertEquals("S3", e.getFileStorageType()),
                () -> assertEquals("hh", e.getFileHash()),
                () -> assertEquals("s", e.getSrcFileId()),
                () -> assertEquals("1", e.getExt1()),
                () -> assertEquals("2", e.getExt2()),
                () -> assertEquals("3", e.getExt3()),
                () -> assertEquals("4", e.getExt4())
        );
    }

    @Test
    @DisplayName("convertInfoList2Entity(null) -> null")
    void convertInfoList2Entity_nullSafe() {
        assertNull(FileInfoConvert.INSTANCE.convertInfoList2Entity(null));
    }

    @Test
    @DisplayName("convertInfo2FileInfoResult：FileInfo -> FileInfoResultVO 字段映射")
    void convertInfo2FileInfoResult_mapsAllFields() {
        FileInfo src = new FileInfo();
        src.setId("f-1");
        src.setFileName("n.bin");
        src.setFileSuffix("bin");
        src.setFileSize(99L);
        src.setBusinessId("b");
        src.setBusinessType("T");
        src.setFilePath("/p");
        src.setFileStorageMenu("m");
        src.setFileStorageType("S");
        src.setFileHash("h");
        src.setSrcFileId("s");
        src.setExt1("e1");
        src.setExt2("e2");
        src.setExt3("e3");
        src.setExt4("e4");

        List<FileInfoResultVO> vos = FileInfoConvert.INSTANCE.convertInfo2FileInfoResult(List.of(src));
        assertNotNull(vos);
        assertEquals(1, vos.size());
        FileInfoResultVO vo = vos.get(0);
        assertEquals("f-1", vo.getId());
        assertEquals("n.bin", vo.getFileName());
        assertEquals("h", vo.getFileHash());
        assertEquals("e4", vo.getExt4());
    }

    @Test
    @DisplayName("convertInfo2FileInfoResult(null) -> null")
    void convertInfo2FileInfoResult_nullSafe() {
        assertNull(FileInfoConvert.INSTANCE.convertInfo2FileInfoResult(null));
    }
}
