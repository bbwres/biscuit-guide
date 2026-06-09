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

package cn.bbwres.biscuit.module.auth.config;

import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import cn.bbwres.biscuit.module.auth.service.cache.LoginAccountCacheService;
import cn.bbwres.biscuit.module.auth.service.cache.RoleCacheService;
import cn.bbwres.biscuit.security.oauth2.service.redis.RedisCheckUserLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * UserDetailsServiceImpl 单元测试
 *
 * <p>主要覆盖问题 #6：修复 clientId 比较的 NPE 风险。
 *
 * <p>修复前 {@code roleEntity.getClientId().equals(clientId)} 在 roleEntity.getClientId() 为 null
 * 时抛出 NullPointerException。修复后使用 {@code Objects.equals} 安全比较。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserDetailsServiceImplUnitTest {

    @Mock
    private LoginAccountCacheService loginAccountCacheService;

    @Mock
    private RoleCacheService roleCacheService;

    @Mock
    private RedisCheckUserLockService redisCheckUserLockService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        // Mockito @InjectMocks 默认会按 构造器 → setter → 字段 顺序注入，
        // 但 AbstractCustomUserDetailsService 父类构造器需要 RedisCheckUserLockService。
        // 这里手动通过反射注入，绕过 @Autowired setter 反射调用时机问题。
        when(redisCheckUserLockService.checkLoginFailLock(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString())).thenReturn(false);
        try {
            java.lang.reflect.Field f1 = UserDetailsServiceImpl.class.getDeclaredField("loginAccountCacheService");
            f1.setAccessible(true);
            f1.set(userDetailsService, loginAccountCacheService);
            java.lang.reflect.Field f2 = UserDetailsServiceImpl.class.getDeclaredField("roleCacheService");
            f2.setAccessible(true);
            f2.set(userDetailsService, roleCacheService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static LoginAccountEntity account(String id, String loginName, LoginAccountStatusEnum status) {
        LoginAccountEntity a = new LoginAccountEntity();
        a.setId(id);
        a.setLoginName(loginName);
        a.setLoginPassword("encoded-pwd");
        a.setStatus(status);
        a.setName("测试用户");
        a.setTenantId("1");
        return a;
    }

    private static RoleEntity role(String id, String clientId) {
        RoleEntity r = new RoleEntity();
        r.setId(id);
        r.setClientId(clientId);
        return r;
    }

    private static List<String> authRoles(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // ----------------------- #6 NPE 修复 -----------------------

    @Test
    @DisplayName("#6 回归：roleEntity.clientId=null 与 clientId 都不为 null 时不抛 NPE")
    void loadUser_roleClientIdNull_doesNotThrowNpe() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf"))
                .thenReturn(account("account-1", "zlf", LoginAccountStatusEnum.NORMAL));
        // 关键：所有 role 的 clientId 都是 null
        when(roleCacheService.findByAccountId("account-1"))
                .thenReturn(Arrays.asList(role("role-1", null), role("role-2", null)));

        // 修复前会抛 NPE
        assertDoesNotThrow(() -> userDetailsService.loadUserByUsername("zlf", "1", "client-A"));
    }

    @Test
    @DisplayName("#6 回归：roleEntity.clientId=null 时，角色被过滤掉（Objects.equals(null, x)=false）")
    void loadUser_roleClientIdNull_filtersOutRole() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf"))
                .thenReturn(account("account-1", "zlf", LoginAccountStatusEnum.NORMAL));
        // 角色 clientId 都为 null，登录 clientId=client-A，匹配失败
        when(roleCacheService.findByAccountId("account-1"))
                .thenReturn(Collections.singletonList(role("role-1", null)));

        UserDetails ud = userDetailsService.loadUserByUsername("zlf", "1", "client-A");

        // 角色被过滤，authorities 应为空
        assertTrue(authRoles(ud).isEmpty(),
                "clientId 为 null 的角色不应匹配任何登录 clientId");
    }

    @Test
    @DisplayName("#6 回归：login clientId=null 时也不抛 NPE")
    void loadUser_loginClientIdNull_doesNotThrowNpe() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf"))
                .thenReturn(account("account-1", "zlf", LoginAccountStatusEnum.NORMAL));
        when(roleCacheService.findByAccountId("account-1"))
                .thenReturn(Collections.singletonList(role("role-1", "client-A")));

        // 修复前：role-1.clientId="client-A".equals(null) 抛 NPE
        assertDoesNotThrow(() -> userDetailsService.loadUserByUsername("zlf", "1", null));
    }

    @Test
    @DisplayName("#6 回归：login clientId=null 时，所有角色都被过滤（Objects.equals(x, null)=false）")
    void loadUser_loginClientIdNull_filtersOutRoles() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf"))
                .thenReturn(account("account-1", "zlf", LoginAccountStatusEnum.NORMAL));
        when(roleCacheService.findByAccountId("account-1"))
                .thenReturn(Collections.singletonList(role("role-1", "client-A")));

        UserDetails ud = userDetailsService.loadUserByUsername("zlf", "1", null);
        assertTrue(authRoles(ud).isEmpty());
    }

    @Test
    @DisplayName("正常情况：role.clientId 与 login clientId 相等时角色被保留")
    void loadUser_matchingClientId_keepsRole() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf"))
                .thenReturn(account("account-1", "zlf", LoginAccountStatusEnum.NORMAL));
        when(roleCacheService.findByAccountId("account-1"))
                .thenReturn(Arrays.asList(
                        role("role-1", "client-A"),
                        role("role-2", "client-B"),
                        role("role-3", "client-A")
                ));

        UserDetails ud = userDetailsService.loadUserByUsername("zlf", "1", "client-A");
        List<String> roles = authRoles(ud);
        // Spring Security User.builder().roles() 会自动加 "ROLE_" 前缀
        // 只有 role-1 和 role-3（clientId=client-A）应该被保留
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_role-1"));
        assertTrue(roles.contains("ROLE_role-3"));
        assertTrue(!roles.contains("ROLE_role-2"));
    }

    // ----------------------- 状态校验 -----------------------

    @Test
    @DisplayName("账户 LOCKED 状态抛 LockedException")
    void loadUser_lockedAccount_throws() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf"))
                .thenReturn(account("account-1", "zlf", LoginAccountStatusEnum.LOCKED));
        assertThrows(LockedException.class,
                () -> userDetailsService.loadUserByUsername("zlf", "1", "client-A"));
    }

    @Test
    @DisplayName("账户 DISABLED 状态抛 DisabledException")
    void loadUser_disabledAccount_throws() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf"))
                .thenReturn(account("account-1", "zlf", LoginAccountStatusEnum.DISABLED));
        assertThrows(DisabledException.class,
                () -> userDetailsService.loadUserByUsername("zlf", "1", "client-A"));
    }

    @Test
    @DisplayName("账户不存在抛 UsernameNotFoundException")
    void loadUser_accountNotFound_throws() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("zlf", "1", "client-A"));
    }

    @Test
    @DisplayName("账户无角色时抛 SystemRuntimeException(ACCOUNT_NO_ROLE_ERROR)")
    void loadUser_noRole_throws() {
        when(loginAccountCacheService.findByLoginUsername("1", "zlf"))
                .thenReturn(account("account-1", "zlf", LoginAccountStatusEnum.NORMAL));
        when(roleCacheService.findByAccountId("account-1")).thenReturn(Collections.emptyList());

        SystemRuntimeException ex = assertThrows(SystemRuntimeException.class,
                () -> userDetailsService.loadUserByUsername("zlf", "1", "client-A"));
        assertEquals(AuthErrorCodeConstants.ACCOUNT_NO_ROLE_ERROR.getCode(), ex.getErrorCode());
    }
}
