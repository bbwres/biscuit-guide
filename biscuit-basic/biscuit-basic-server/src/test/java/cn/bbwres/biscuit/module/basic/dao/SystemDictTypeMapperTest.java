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

import cn.bbwres.biscuit.context.UserInfoContext;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.basic.controller.vo.SystemDictTypePageReqVO;
import cn.bbwres.biscuit.module.basic.entity.SystemDictTypeEntity;
import cn.bbwres.biscuit.module.basic.entity.table.SystemDictTypeEntityTableDef;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.cursor.Cursor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SystemDictTypeMapperTest {

    @Autowired
    private SystemDictTypeMapper systemDictTypeMapper;

    @Test
    void selectByPrimaryKey() {
        SystemDictTypeEntity systemDictTypeEntity = systemDictTypeMapper.selectOneById("111111");
        assertNotNull(systemDictTypeEntity);
    }

    @Test
    void selectByConfig() {
        List<SystemDictTypeEntity> systemDictTypeEntitys = systemDictTypeMapper.selectListByQuery(QueryWrapper.create()
                .where(SystemDictTypeEntityTableDef.SYSTEM_DICT_TYPE_ENTITY.ID.eq("1111")));
        assertNotNull(systemDictTypeEntitys);
    }

    /**
     * 测试分页查询
     */
    @Test
    void selectByPage() {
        Page<SystemDictTypeEntity> systemDictTypePage = systemDictTypeMapper.paginate(Page.of(1, 10),
                QueryWrapper.create());
                      //  .where(SystemDictTypeEntityTableDef.SYSTEM_DICT_TYPE_ENTITY.DICT_NAME.likeLeft("测试")));
        assertNotNull(systemDictTypePage);
    }

    @Test
    void selectPage(){
        cn.bbwres.biscuit.dto.Page<SystemDictTypeEntity, SystemDictTypePageReqVO> query = new cn.bbwres.biscuit.dto.Page<>();
        SystemDictTypePageReqVO  reqVO = new SystemDictTypePageReqVO();
        reqVO.setDictName("测试");
       // reqVO.setId("test");
        query.setQuery(reqVO);

        cn.bbwres.biscuit.dto.Page<SystemDictTypeEntity, SystemDictTypePageReqVO> systemDictTypeEntitySystemDictTypePageReqVOPage = systemDictTypeMapper.selectPage(query);
        assertNotNull(systemDictTypeEntitySystemDictTypePageReqVOPage);
    }


    @Test
    void insert() {
        UserBaseInfo user = new UserBaseInfo();
        user.setUserId("system");
        user.setUsername("system");
        user.setZhName("管理员");
        user.setTenantId("zhsmcjjt");
        UserInfoContext.setCurrentContext(user);

        SystemDictTypeEntity systemDictTypeEntity = new SystemDictTypeEntity();
        systemDictTypeEntity.setDictName("测试3");
        systemDictTypeEntity.setDictType("test_3");
        systemDictTypeEntity.setStatus(DataStatusEnum.NORMAL);
        systemDictTypeEntity.setRemark("测试的3");
        int insert = systemDictTypeMapper.insert(systemDictTypeEntity);
        assertEquals(1, insert);
    }

    @Test
    void  insertBatch() {
        UserBaseInfo user = new UserBaseInfo();
        user.setUserId("system");
        user.setUsername("system");
        user.setZhName("管理员");
        user.setTenantId("zhsmcjjt");
        UserInfoContext.setCurrentContext(user);
        List<SystemDictTypeEntity> systemDictTypes = new ArrayList<>(16);

        for (int i = 0; i < 100000; i++) {
            SystemDictTypeEntity systemDictTypeEntity = new SystemDictTypeEntity();
            systemDictTypeEntity.setDictName("测试"+i);
            systemDictTypeEntity.setDictType("test_"+i);
            systemDictTypeEntity.setStatus(DataStatusEnum.NORMAL);
            systemDictTypeEntity.setRemark("测试的"+i);
            systemDictTypes.add(systemDictTypeEntity);
        }

        int insert = systemDictTypeMapper.insertBatch(systemDictTypes,1000);
        assertEquals(100000, insert);
    }

    @Test
    void update(){
        UserBaseInfo user = new UserBaseInfo();
        user.setUserId("system");
        user.setUsername("system");
        user.setZhName("管理员");
        user.setTenantId("zhsmcjjt");
        UserInfoContext.setCurrentContext(user);
        SystemDictTypeEntity systemDictTypeEntity = new SystemDictTypeEntity();
        systemDictTypeEntity.setId("350842921027117056");
        systemDictTypeEntity.setDictName("我修改了");
        systemDictTypeMapper.update(systemDictTypeEntity);

    }

    @Test
    void delete(){
        systemDictTypeMapper.deleteById("350842921027117056");
    }


    @Test
    @Transactional
    void read(){
        Cursor<SystemDictTypeEntity> systemDictTypeEntities = systemDictTypeMapper.selectCursorByQuery(QueryWrapper.create());
        for (SystemDictTypeEntity systemDictTypeEntity : systemDictTypeEntities) {
            System.out.println(systemDictTypeEntity);
        }
    }


}