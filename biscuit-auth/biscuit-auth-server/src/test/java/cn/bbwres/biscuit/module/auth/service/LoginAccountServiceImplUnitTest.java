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

package cn.bbwres.biscuit.module.auth.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountAddOrUpdateReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountAddRoleReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountEditPasswordReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.dao.LoginAccountMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleAccountMapper;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleAccountEntity;
import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * LoginAccountServiceImpl 单元测试（Mockito 隔离环境，不依赖 Spring 容器）。
 *
 * <p>覆盖 LoginAccountService 接口的全部公共方法，并对以下潜在 bug 写回归测试：
 * <ul>
 *     <li>editAccountPassword 旧密码校验方向（修复后必须 {@code !passwordEncoder.matches} 才抛错）</li>
 *     <li>accountRoleConfig 行为（先删除旧关联；roleIds 为空时仅删除不插入；非空时按 roleId 拼装 RoleAccountEntity）</li>
 *     <li>editAccountPassword 缺 oldPassword 时跳过校验直接改密（reset 流程）</li>
 *     <li>save 方法强制设置状态为 UNACTIVATED 并写入 lastUpdatePasswordTime</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginAccountServiceImplUnitTest {

    @Mock
    private LoginAccountMapper loginAccountMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleAccountMapper roleAccountMapper;

    @InjectMocks
    private LoginAccountServiceImpl loginAccountService;

    // ----------------------- 基础查询方法 -----------------------

    @Test
    @DisplayName("getLoginAccount 透传 id 调用 mapper")
    void getLoginAccount_delegatesToMapper() {
        LoginAccountEntity expected = new LoginAccountEntity();
        expected.setId("acc-1");
        when(loginAccountMapper.selectById("acc-1")).thenReturn(expected);

        LoginAccountEntity actual = loginAccountService.getLoginAccount("acc-1");

        assertSame(expected, actual);
        verify(loginAccountMapper).selectById("acc-1");
    }

    @Test
    @DisplayName("getLoginAccount 传入 null 时仍调用 mapper，由 mapper 决定返回 null")
    void getLoginAccount_nullId() {
        when(loginAccountMapper.selectById(null)).thenReturn(null);

        LoginAccountEntity actual = loginAccountService.getLoginAccount(null);

        assertNull(actual);
        verify(loginAccountMapper).selectById(null);
    }

    @Test
    @DisplayName("findByLoginUsernameNoTenant 透传 tenantId/username")
    void findByLoginUsernameNoTenant_delegatesToMapper() {
        LoginAccountEntity expected = new LoginAccountEntity();
        expected.setLoginName("zlf");
        when(loginAccountMapper.findByLoginUsernameNoTenant("tenant-1", "zlf"))
                .thenReturn(expected);

        LoginAccountEntity actual = loginAccountService.findByLoginUsernameNoTenant("tenant-1", "zlf");

        assertSame(expected, actual);
        verify(loginAccountMapper).findByLoginUsernameNoTenant("tenant-1", "zlf");
    }

    @Test
    @DisplayName("getLoginAccountList 透传 ids 集合给 mapper")
    void getLoginAccountList_delegatesToMapper() {
        Collection<String> ids = Arrays.asList("a", "b", "c");
        List<LoginAccountEntity> expected = List.of(new LoginAccountEntity(), new LoginAccountEntity());
        when(loginAccountMapper.selectByIds(ids)).thenReturn(expected);

        List<LoginAccountEntity> actual = loginAccountService.getLoginAccountList(ids);

        assertSame(expected, actual);
        verify(loginAccountMapper).selectByIds(ids);
    }

    @Test
    @DisplayName("getLoginAccountPage 透传 page 对象给 mapper")
    void getLoginAccountPage_delegatesToMapper() {
        Page<LoginAccountEntity, LoginAccountPageReqVO> page = new Page<>();
        page.setCurrent(1L);
        page.setSize(10L);
        page.setQuery(new LoginAccountPageReqVO());
        when(loginAccountMapper.selectPage(page)).thenReturn(page);

        Page<LoginAccountEntity, LoginAccountPageReqVO> actual =
                loginAccountService.getLoginAccountPage(page);

        assertSame(page, actual);
        verify(loginAccountMapper).selectPage(page);
    }

    // ----------------------- updateById -----------------------

    @Test
    @DisplayName("updateById 当 mapper 返回 >0 时返回 true")
    void updateById_returnsTrueWhenMapperSucceeds() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId("acc-1");
        when(loginAccountMapper.updateById(entity)).thenReturn(1);

        assertTrue(loginAccountService.updateById(entity));
    }

    @Test
    @DisplayName("updateById 当 mapper 返回 0 时返回 false")
    void updateById_returnsFalseWhenMapperFails() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId("missing");
        when(loginAccountMapper.updateById(entity)).thenReturn(0);

        assertFalse(loginAccountService.updateById(entity));
    }

    // ----------------------- save -----------------------

    @Test
    @DisplayName("save 加密密码、强制设置 UNACTIVATED、写入 lastUpdatePasswordTime 并 insert")
    void save_encryptsPasswordAndSetsDefaults() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setLoginPassword("plain-pwd");
        entity.setStatus(LoginAccountStatusEnum.NORMAL);

        when(passwordEncoder.encode("plain-pwd")).thenReturn("encoded-pwd");

        loginAccountService.save(entity);

        // 密码被加密
        assertEquals("encoded-pwd", entity.getLoginPassword());
        // 状态被强制改为未激活
        assertEquals(LoginAccountStatusEnum.UNACTIVATED, entity.getStatus());
        // 最后修改密码时间已设置
        assertNotNull(entity.getLastUpdatePasswordTime());

        ArgumentCaptor<LoginAccountEntity> captor = ArgumentCaptor.forClass(LoginAccountEntity.class);
        verify(loginAccountMapper).insert(captor.capture());
        LoginAccountEntity inserted = captor.getValue();
        assertEquals("encoded-pwd", inserted.getLoginPassword());
        assertEquals(LoginAccountStatusEnum.UNACTIVATED, inserted.getStatus());
        assertNotNull(inserted.getLastUpdatePasswordTime());
    }

    // ----------------------- accountRoleConfig -----------------------

    @Test
    @DisplayName("accountRoleConfig 正常路径：先删除旧关联，再按 roleIds 批量插入")
    void accountRoleConfig_normalPath() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("acc-1");
        req.setRoleIds(Arrays.asList("role-1", "role-2", "role-3"));

        loginAccountService.accountRoleConfig(req);

        // 先删后插
        verify(roleAccountMapper).deleteByAccountId("acc-1");
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RoleAccountEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(roleAccountMapper).insert(captor.capture());
        List<RoleAccountEntity> inserted = captor.getValue();

        assertEquals(3, inserted.size());
        for (int i = 0; i < inserted.size(); i++) {
            assertEquals("acc-1", inserted.get(i).getLoginAccountId());
            assertEquals(req.getRoleIds().get(i), inserted.get(i).getRoleId());
        }
    }

    @Test
    @DisplayName("accountRoleConfig roleIds 为空集合时：仅删除，不插入")
    void accountRoleConfig_emptyRoleIds() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("acc-1");
        req.setRoleIds(Collections.emptyList());

        loginAccountService.accountRoleConfig(req);

        verify(roleAccountMapper).deleteByAccountId("acc-1");
        verify(roleAccountMapper, never()).insert(anyCollection());
    }

    @Test
    @DisplayName("accountRoleConfig roleIds 为 null 时：仅删除，不抛 NPE")
    void accountRoleConfig_nullRoleIds() {
        LoginAccountAddRoleReqVO req = new LoginAccountAddRoleReqVO();
        req.setId("acc-1");
        // roleIds = null
        req.setRoleIds(null);

        loginAccountService.accountRoleConfig(req);

        verify(roleAccountMapper).deleteByAccountId("acc-1");
        verify(roleAccountMapper, never()).insert(anyCollection());
    }

    // ----------------------- checkUserRole -----------------------

    @Test
    @DisplayName("checkUserRole 当 mapper 返回 >0 时返回 true")
    void checkUserRole_true() {
        when(roleAccountMapper.countByAccountId("acc-1")).thenReturn(2L);

        assertTrue(loginAccountService.checkUserRole("acc-1"));
    }

    @Test
    @DisplayName("checkUserRole 当 mapper 返回 0 时返回 false")
    void checkUserRole_false() {
        when(roleAccountMapper.countByAccountId("acc-1")).thenReturn(0L);

        assertFalse(loginAccountService.checkUserRole("acc-1"));
    }

    // ----------------------- editAccountStatus -----------------------

    @Test
    @DisplayName("editAccountStatus 只更新 id + status 字段")
    void editAccountStatus_updatesOnlyStatus() {
        LoginAccountAddOrUpdateReqVO req = new LoginAccountAddOrUpdateReqVO();
        req.setId("acc-1");
        req.setStatus(LoginAccountStatusEnum.DISABLED);
        req.setLoginName("不要把 name 写进去");
        req.setLoginPassword("不要把 password 写进去");

        loginAccountService.editAccountStatus(req);

        ArgumentCaptor<LoginAccountEntity> captor = ArgumentCaptor.forClass(LoginAccountEntity.class);
        verify(loginAccountMapper).updateById(captor.capture());
        LoginAccountEntity updated = captor.getValue();

        assertEquals("acc-1", updated.getId());
        assertEquals(LoginAccountStatusEnum.DISABLED, updated.getStatus());
        // 其它字段未携带
        assertNull(updated.getLoginName());
        assertNull(updated.getLoginPassword());
    }

    // ----------------------- editAccountPassword 重点回归 -----------------------

    @Test
    @DisplayName("editAccountPassword 旧密码匹配时不抛错，并加密新密码、更新 lastUpdatePasswordTime")
    void editAccountPassword_oldPasswordMatches_success() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId("acc-1");
        entity.setName("zlf");
        entity.setLoginPassword("encoded-old");

        LoginAccountEditPasswordReqVO req = new LoginAccountEditPasswordReqVO();
        req.setId("acc-1");
        req.setOldPassword("plain-old");
        req.setNewPassword("plain-new");

        when(passwordEncoder.matches("plain-old", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("plain-new")).thenReturn("encoded-new");

        loginAccountService.editAccountPassword(entity, req);

        // 旧密码校验被调用
        verify(passwordEncoder).matches("plain-old", "encoded-old");
        // 新密码被编码
        verify(passwordEncoder).encode("plain-new");

        ArgumentCaptor<LoginAccountEntity> captor = ArgumentCaptor.forClass(LoginAccountEntity.class);
        verify(loginAccountMapper).updateById(captor.capture());
        LoginAccountEntity updated = captor.getValue();
        assertEquals("acc-1", updated.getId());
        assertEquals("encoded-new", updated.getLoginPassword());
        assertNotNull(updated.getLastUpdatePasswordTime());
    }

    @Test
    @DisplayName("editAccountPassword 旧密码不匹配时抛 ACCOUNT_PASSWORD_ERROR 且不写库")
    void editAccountPassword_oldPasswordMismatch_throws() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId("acc-1");
        entity.setName("zlf");
        entity.setLoginPassword("encoded-old");

        LoginAccountEditPasswordReqVO req = new LoginAccountEditPasswordReqVO();
        req.setId("acc-1");
        req.setOldPassword("wrong");
        req.setNewPassword("plain-new");

        // 关键回归断言：旧密码校验方向必须为 !matches 才抛错
        when(passwordEncoder.matches("wrong", "encoded-old")).thenReturn(false);

        SystemRuntimeException ex = assertThrows(SystemRuntimeException.class,
                () -> loginAccountService.editAccountPassword(entity, req));

        assertEquals(AuthErrorCodeConstants.ACCOUNT_PASSWORD_ERROR.getCode(), ex.getErrorCode());

        // 抛错后不应写库
        verify(loginAccountMapper, never()).updateById(any(LoginAccountEntity.class));
        // 不应编码新密码
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("editAccountPassword oldPassword 为空/null 时跳过校验，直接改密（reset 流程）")
    void editAccountPassword_skipCheckWhenOldPasswordBlank() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId("acc-1");
        entity.setLoginPassword("encoded-old");

        LoginAccountEditPasswordReqVO req = new LoginAccountEditPasswordReqVO();
        req.setId("acc-1");
        req.setOldPassword(null); // 重置场景
        req.setNewPassword("plain-new");

        when(passwordEncoder.encode("plain-new")).thenReturn("encoded-new");

        loginAccountService.editAccountPassword(entity, req);

        // 旧密码校验跳过
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder).encode("plain-new");
        verify(loginAccountMapper).updateById(any(LoginAccountEntity.class));
    }

    @Test
    @DisplayName("editAccountPassword oldPassword 为空字符串时同样跳过校验")
    void editAccountPassword_skipCheckWhenOldPasswordEmptyString() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId("acc-1");
        entity.setLoginPassword("encoded-old");

        LoginAccountEditPasswordReqVO req = new LoginAccountEditPasswordReqVO();
        req.setId("acc-1");
        req.setOldPassword("");  // Spring ObjectUtils.isEmpty("") == true
        req.setNewPassword("plain-new");

        when(passwordEncoder.encode("plain-new")).thenReturn("encoded-new");

        loginAccountService.editAccountPassword(entity, req);

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(loginAccountMapper).updateById(any(LoginAccountEntity.class));
    }

    // ----------------------- 事务注解 -----------------------

    @Test
    @DisplayName("accountRoleConfig 标注 @Transactional(rollbackFor = RuntimeException.class)")
    void accountRoleConfig_transactionalAnnotationPresent() throws NoSuchMethodException {
        Method method = LoginAccountServiceImpl.class.getDeclaredMethod(
                "accountRoleConfig", LoginAccountAddRoleReqVO.class);
        assertNotNull(method.getAnnotation(Transactional.class),
                "accountRoleConfig 必须标注 @Transactional（先删后插，需要原子性）");
    }

    // ----------------------- 安全性/隔离性 -----------------------

    @Test
    @DisplayName("save 即便 entity 预先设置状态，最终状态仍被强制覆盖为 UNACTIVATED")
    void save_overwritesPresetStatus() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setLoginPassword("plain");
        entity.setStatus(LoginAccountStatusEnum.LOCKED);

        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        loginAccountService.save(entity);

        // 不论传入什么状态，最终都应为未激活（业务流程约束）
        assertEquals(LoginAccountStatusEnum.UNACTIVATED, entity.getStatus());
    }

    @Test
    @DisplayName("save 不会使用 entity 中预设的明文 password 入库")
    void save_doesNotStorePlainPassword() {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setLoginPassword("plain-pwd");

        when(passwordEncoder.encode("plain-pwd")).thenReturn("encoded-pwd");

        loginAccountService.save(entity);

        // 校验：入参 entity 的明文被替换
        assertNotEquals("plain-pwd", entity.getLoginPassword());
    }

    @Test
    @DisplayName("editAccountStatus 当状态非法(null)时也能正常调用 mapper（由 mapper 决定）")
    void editAccountStatus_nullStatusPropagates() {
        LoginAccountAddOrUpdateReqVO req = new LoginAccountAddOrUpdateReqVO();
        req.setId("acc-1");
        // status = null

        loginAccountService.editAccountStatus(req);

        ArgumentCaptor<LoginAccountEntity> captor = ArgumentCaptor.forClass(LoginAccountEntity.class);
        verify(loginAccountMapper, times(1)).updateById(captor.capture());
        assertNull(captor.getValue().getStatus());
    }
}
