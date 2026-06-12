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

package cn.bbwres.biscuit.module.auth.enums;

import cn.bbwres.biscuit.enums.BaseEnum;

/**
 * 菜单类型
 *
 * @author zhanglinfeng
 */

public enum MenuTypeEnum implements BaseEnum<String> {
    /**
     * 目录
     */
    DIR("dir", "目录"),
    /**
     * 菜单
     */
    MENU("menu", "菜单"),
    /**
     * 禁用
     */
    BUTTON("button", "按钮"),

    ;

    MenuTypeEnum(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    private final String value;

    private final String displayName;

    /**
     * 枚举value
     *
     * @return 枚举value
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * 枚举的显示名字
     *
     * @return 枚举的显示名字
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }
}
