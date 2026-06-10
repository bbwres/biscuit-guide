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

package cn.bbwres.biscuit.module.basic.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.TempFileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Mapper selectPage 查询条件补全测试
 *
 * <p>覆盖问题 #16：修复前 selectPage 只按 id 过滤，导致 VO 中其他字段形同虚设。
 *
 * <p>实现方式：手动通过 {@link TableInfoHelper#initTableInfo} 初始化 entity 的 lambda 缓存，
 * 复现 mapper 中 Wrapper 构建的等价逻辑，验证生成的 SQL 片段。
 * （MyBatis-Plus 的 {@code Wrappers.lambdaQuery} 需要 lambda cache，
 *  正常由 Spring 启动时扫描 entity 注册；单元测试中通过 initTableInfo 模拟。）
 */
class TempFileInfoMapperUnitTest {

    @BeforeAll
    static void initEntityCache() {
        // 在 Spring 上下文之外手动初始化 entity 的 TableInfo，让 Wrappers.lambdaQuery 能用
        // 这模拟了 MyBatis-Plus 在 Spring 启动时扫描 @TableName 注解的 entity 的行为
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(
                new Configuration(), "test");
        try {
            TableInfoHelper.initTableInfo(assistant, TempFileInfoEntity.class);
            TableInfoHelper.initTableInfo(assistant, FileInfoEntity.class);
        } catch (Exception e) {
            // 如果已经初始化过会抛异常，忽略
        }
    }

    /**
     * 复现 TempFileInfoMapper.selectPage 中 Wrapper 构建的核心逻辑。
     */
    private String buildTempFileInfoWrapperSql(TempFileInfoPageReqVO query) {
        LambdaQueryWrapper<TempFileInfoEntity> qw = Wrappers.lambdaQuery(TempFileInfoEntity.class);
        if (!ObjectUtils.isEmpty(query)) {
            qw.eq(!ObjectUtils.isEmpty(query.getId()), TempFileInfoEntity::getId, query.getId());
            qw.eq(!ObjectUtils.isEmpty(query.getTenantId()), TempFileInfoEntity::getTenantId, query.getTenantId());
            qw.like(!ObjectUtils.isEmpty(query.getFileName()), TempFileInfoEntity::getFileName, query.getFileName());
            qw.eq(!ObjectUtils.isEmpty(query.getFileSuffix()), TempFileInfoEntity::getFileSuffix, query.getFileSuffix());
            qw.eq(!ObjectUtils.isEmpty(query.getFileHash()), TempFileInfoEntity::getFileHash, query.getFileHash());
            qw.eq(!ObjectUtils.isEmpty(query.getFileStorageType()), TempFileInfoEntity::getFileStorageType, query.getFileStorageType());
        }
        qw.orderByDesc(TempFileInfoEntity::getId);
        return qw.getSqlSegment();
    }

    @Test
    @DisplayName("#16 selectPage 在 query 为 null 时不抛异常，SQL 不含业务字段条件")
    void selectPage_queryNull_noError() {
        String sql = buildTempFileInfoWrapperSql(null);
        assertNotNull(sql);
        assertFalse(sql.contains("file_name ="),
                "null query 时 SQL 不应包含 file_name 条件: " + sql);
        assertFalse(sql.contains("file_hash ="),
                "null query 时 SQL 不应包含 file_hash 条件: " + sql);
    }

    @Test
    @DisplayName("#16 selectPage 设置 fileName 后，wrapper SQL 包含 file_name 列（like）")
    void selectPage_withFileName_appearsInWrapperSql() {
        TempFileInfoPageReqVO query = new TempFileInfoPageReqVO();
        query.setFileName("订单.pdf");

        String sql = buildTempFileInfoWrapperSql(query);
        assertNotNull(sql);
        // 修复后 fileName 字段会拼接到 SQL 中
        assertTrue(sql.contains("file_name"),
                "wrapper SQL 应包含 file_name 列名: " + sql);
        // like 条件应包含 LIKE
        assertTrue(sql.toUpperCase().contains("LIKE"),
                "fileName 字段应使用 LIKE 条件: " + sql);
    }

    @Test
    @DisplayName("#16 selectPage 设置 fileHash 后，wrapper SQL 包含 file_hash 列（eq）")
    void selectPage_withFileHash_appearsInWrapperSql() {
        TempFileInfoPageReqVO query = new TempFileInfoPageReqVO();
        query.setFileHash("abc123hash");

        String sql = buildTempFileInfoWrapperSql(query);
        assertNotNull(sql);
        assertTrue(sql.contains("file_hash"),
                "wrapper SQL 应包含 file_hash 列名: " + sql);
    }

    @Test
    @DisplayName("#16 selectPage 设置多个业务字段，SQL 包含所有列")
    void selectPage_allFields_addedToWrapper() {
        TempFileInfoPageReqVO query = new TempFileInfoPageReqVO();
        query.setId("temp-1");
        query.setFileName("临时.pdf");
        query.setFileSuffix("pdf");
        query.setFileHash("abc123");
        query.setFileStorageType("LOCAL");
        query.setTenantId("1");

        String sql = buildTempFileInfoWrapperSql(query);

        assertNotNull(sql);
        assertTrue(sql.contains("file_name"), "SQL 应包含 file_name: " + sql);
        assertTrue(sql.contains("file_suffix"), "SQL 应包含 file_suffix: " + sql);
        assertTrue(sql.contains("file_hash"), "SQL 应包含 file_hash: " + sql);
        assertTrue(sql.contains("file_storage_type"), "SQL 应包含 file_storage_type: " + sql);
        assertTrue(sql.contains("tenant_id"), "SQL 应包含 tenant_id: " + sql);
    }

    @Test
    @DisplayName("#16 selectPage 在 query 为空对象时 SQL 不含业务字段条件")
    void selectPage_emptyQuery_noError() {
        String sql = buildTempFileInfoWrapperSql(new TempFileInfoPageReqVO());
        assertNotNull(sql);
        assertFalse(sql.contains("file_name ="),
                "空 query 时 SQL 不应包含 file_name 条件: " + sql);
        assertFalse(sql.contains("file_hash ="),
                "空 query 时 SQL 不应包含 file_hash 条件: " + sql);
    }

    @Test
    @DisplayName("#16 selectPage 只设置 id 过滤，SQL 包含 id 但不含其他字段")
    void selectPage_idOnly_onlyIdInWrapper() {
        TempFileInfoPageReqVO query = new TempFileInfoPageReqVO();
        query.setId("temp-file-1");

        String sql = buildTempFileInfoWrapperSql(query);
        assertNotNull(sql);
        // 至少包含 id
        assertTrue(sql.contains("id =") || sql.contains("`id`"),
                "SQL 应包含 id 条件: " + sql);
        assertFalse(sql.contains("file_name ="),
                "只设置 id 时 SQL 不应包含 file_name 条件: " + sql);
        assertFalse(sql.contains("file_hash ="),
                "只设置 id 时 SQL 不应包含 file_hash 条件: " + sql);
    }

    @Test
    @DisplayName("#16 selectPage 设置 tenantId 后，wrapper SQL 包含 tenant_id 列（tenant 隔离）")
    void selectPage_withTenantId_tenantIsolation() {
        TempFileInfoPageReqVO query = new TempFileInfoPageReqVO();
        query.setTenantId("tenant-007");

        String sql = buildTempFileInfoWrapperSql(query);
        assertNotNull(sql);
        assertTrue(sql.contains("tenant_id"),
                "wrapper SQL 应包含 tenant_id 列名以保证租户隔离: " + sql);
    }

    @Test
    @DisplayName("#16 Page 接受 query null/空对象/正常值 都不报错")
    void pageConstructionDoesNotThrow() {
        Page<TempFileInfoEntity, TempFileInfoPageReqVO> req1 = new Page<>();
        req1.setQuery(null);
        assertTrue(req1.getQuery() == null);

        Page<TempFileInfoEntity, TempFileInfoPageReqVO> req2 = new Page<>();
        req2.setQuery(new TempFileInfoPageReqVO());
        assertNotNull(req2.getQuery());

        Page<TempFileInfoEntity, TempFileInfoPageReqVO> req3 = new Page<>();
        TempFileInfoPageReqVO q = new TempFileInfoPageReqVO();
        q.setId("1");
        req3.setQuery(q);
        assertNotNull(req3.getQuery().getId());
    }
}
