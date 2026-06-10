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
import cn.bbwres.biscuit.enums.YesOrNoEnum;
import cn.bbwres.biscuit.module.basic.controller.vo.FileBusinessInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.controller.vo.FileBusinessInfoRespVO;
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
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
 * MapStruct {@link FileBusinessInfoConvert} 单元测试。
 */
@DisplayName("FileBusinessInfoConvert MapStruct 转换测试")
class FileBusinessInfoConvertTest {

    private static FileBusinessInfoEntity buildEntity() {
        FileBusinessInfoEntity e = new FileBusinessInfoEntity();
        e.setId("biz-1");
        e.setBusinessType("ORDER");
        e.setBusinessName("订单业务");
        e.setNeedAuth(YesOrNoEnum.YES);
        e.setModuleName("order");
        e.setAuthPath("/api/order/auth");
        return e;
    }

    @Test
    @DisplayName("convert(Entity) -> RespVO：所有业务字段映射")
    void convert_entityToRespVO_allFieldsMapped() {
        FileBusinessInfoEntity e = buildEntity();
        FileBusinessInfoRespVO vo = FileBusinessInfoConvert.INSTANCE.convert(e);

        assertAll(
                () -> assertNotNull(vo),
                () -> assertEquals("biz-1", vo.getId()),
                () -> assertEquals("ORDER", vo.getBusinessType()),
                () -> assertEquals("订单业务", vo.getBusinessName()),
                () -> assertEquals(YesOrNoEnum.YES, vo.getNeedAuth()),
                () -> assertEquals("order", vo.getModuleName()),
                () -> assertEquals("/api/order/auth", vo.getAuthPath())
        );
    }

    @Test
    @DisplayName("convert(null) -> null")
    void convert_null_returnsNull() {
        assertNull(FileBusinessInfoConvert.INSTANCE.convert((FileBusinessInfoEntity) null));
    }

    @Test
    @DisplayName("convertList：保持顺序与数量")
    void convertList_preservesOrderAndSize() {
        FileBusinessInfoEntity a = buildEntity();
        a.setId("a");
        FileBusinessInfoEntity b = buildEntity();
        b.setId("b");
        List<FileBusinessInfoRespVO> vos = FileBusinessInfoConvert.INSTANCE.convertList(List.of(a, b));
        assertEquals(2, vos.size());
        assertEquals("a", vos.get(0).getId());
        assertEquals("b", vos.get(1).getId());
    }

    @Test
    @DisplayName("convertList(null) / convertList(空集合)")
    void convertList_nullAndEmpty() {
        assertNull(FileBusinessInfoConvert.INSTANCE.convertList(null));
        List<FileBusinessInfoRespVO> vos = FileBusinessInfoConvert.INSTANCE.convertList(Collections.emptyList());
        assertNotNull(vos);
        assertTrue(vos.isEmpty());
    }

    @Test
    @DisplayName("convertPage：records/query/total/size/current/pages 全部透传")
    void convertPage_passesThrough() {
        FileBusinessInfoPageReqVO q = new FileBusinessInfoPageReqVO();
        q.setBusinessType("ORDER");
        Page<FileBusinessInfoEntity, FileBusinessInfoPageReqVO> src = new Page<>();
        src.setQuery(q);
        src.setRecords(List.of(buildEntity()));
        src.setTotal(50);
        src.setSize(5);
        src.setCurrent(3);
        src.setPages(10);

        Page<FileBusinessInfoRespVO, FileBusinessInfoPageReqVO> dest =
                FileBusinessInfoConvert.INSTANCE.convertPage(src);

        assertAll(
                () -> assertNotNull(dest),
                () -> assertEquals(1, dest.getRecords().size()),
                () -> assertEquals(50L, dest.getTotal()),
                () -> assertEquals(5L, dest.getSize()),
                () -> assertEquals(3L, dest.getCurrent()),
                () -> assertEquals(10L, dest.getPages()),
                () -> assertSame(q, dest.getQuery()),
                () -> assertEquals("biz-1", dest.getRecords().get(0).getId())
        );
    }

    @Test
    @DisplayName("convertPage(null) -> null")
    void convertPage_null_returnsNull() {
        assertNull(FileBusinessInfoConvert.INSTANCE.convertPage(null));
    }
}
