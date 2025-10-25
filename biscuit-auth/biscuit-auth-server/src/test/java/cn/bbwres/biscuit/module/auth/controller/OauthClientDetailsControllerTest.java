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

package cn.bbwres.biscuit.module.auth.controller;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.OauthClientDetailsRespVO;
import cn.bbwres.biscuit.module.auth.entity.OauthClientDetailsEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OauthClientDetailsControllerTest {

    @Autowired
    private OauthClientDetailsController oauthClientDetailsController;

    @Test
    void getOauthClientDetailsPage() {
        Page<OauthClientDetailsEntity, OauthClientDetailsPageReqVO> page = new Page<>();
        OauthClientDetailsPageReqVO query = new OauthClientDetailsPageReqVO();
        query.setId("1-mp");
        page.setQuery(query);
        Result<Page<OauthClientDetailsRespVO, OauthClientDetailsPageReqVO>> oauthClientDetailsPage = oauthClientDetailsController.getOauthClientDetailsPage(page);
        Page<OauthClientDetailsRespVO, OauthClientDetailsPageReqVO> oauthClientDetailsRespVOOauthClientDetailsPageReqVOPage = oauthClientDetailsPage.checkAndGetData();
    }

    @Test
    void getById() {
        Result<OauthClientDetailsRespVO> result = oauthClientDetailsController.getById("1-mp");
        result.checkAndGetData();
    }

    @Test
    void add() {
    }

    @Test
    void edit() {
    }
}