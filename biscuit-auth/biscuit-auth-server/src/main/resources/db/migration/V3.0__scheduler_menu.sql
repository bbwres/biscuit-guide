-- 定时任务管理：菜单与按钮权限 + 菜单接口种子数据（增量）
--
-- 依赖：基础菜单种子已建立「系统管理」目录（id = m2000，见 logs/auth.sql）。
-- 本脚本把「定时任务」菜单挂到 系统管理 目录下，与 登录账号 / 角色管理 / 菜单管理 /
-- OAuth 客户端 同级。
--
-- 菜单层级（挂在 m2000 系统管理 下）：
--   m2000 系统管理 (dir)              ← 基础种子已有，本脚本不重复创建
--     └ m2500 定时任务 (menu)         component = @/views/scheduler/job/index.vue
--         ├ m2501 暂停       (button) 权限码 schedulerJob:pause
--         ├ m2502 恢复       (button) 权限码 schedulerJob:resume
--         ├ m2503 立即执行   (button) 权限码 schedulerJob:trigger
--         └ m2504 修改Cron   (button) 权限码 schedulerJob:updateCron
--
-- 约定（与 logs/auth.sql 一致，对齐 V2.0 之后的表结构）：
--   * t_menu 已无 api_url / api_url_method 列（V2.0 已迁出到 t_menu_api 并删除）
--   * t_menu / t_menu_api 均含 creator_name 列
--   * menu_type 入库小写：dir / menu / button（MenuTypeEnum.value）
--   * status smallint：1=正常（DataStatusEnum.NORMAL）
--   * BUTTON 的 component 字段复用为前端权限码，与页面 v-permission 比对
--   * MENU 的 componentName 经 kebab-case 转换即路由 path（SchedulerJob -> /scheduler-job）
--   * tree_path：子=parentTreePath/id
--   * t_menu_api.api_url 填前端实际请求路径（含 /scheduler 网关前缀）
--   * tenant_id 默认 '0'（菜单读取走 findByRoleIdNoTenant 忽略租户）
--   * ON CONFLICT (id) DO NOTHING：主键冲突跳过，可重复执行
--
-- 接口清单（对应前端 src/api/scheduler/job.ts，后端 JobController /scheduler/job/**）：
--   GET  /scheduler/job/list          全部触发器列表        -> m2500
--   GET  /scheduler/job/getJobDetail  任务明细              -> m2500
--   POST /scheduler/job/pause         暂停                  -> m2501
--   POST /scheduler/job/pauseBatch    批量暂停              -> m2501
--   POST /scheduler/job/resume        恢复                  -> m2502
--   POST /scheduler/job/resumeBatch   批量恢复              -> m2502
--   POST /scheduler/job/triggerJob    按 job 立即执行       -> m2503
--   POST /scheduler/job/trigger       按 trigger 立即执行   -> m2503
--   POST /scheduler/job/updateCron    修改 cron             -> m2504


-- ============================== t_menu ==============================
insert into t_menu (id, name, menu_type, menu_sort, parent_id, icon, component, component_name,
                    status, visible, keep_alive, always_show, create_time, creator, creator_name,
                    tenant_id, tree_path) values
  -- 定时任务菜单（挂在 系统管理 m2000 下）
  ('m2500', '定时任务', 'menu', 5, 'm2000', 'Operation', '@/views/scheduler/job/index.vue', 'SchedulerJob',
   1, true, true, false, now(), 'system', '系统', '0', 'm2000/m2500'),
  -- 按钮：暂停
  ('m2501', '暂停', 'button', 1, 'm2500', null, 'schedulerJob:pause', null,
   1, false, false, false, now(), 'system', '系统', '0', 'm2000/m2500/m2501'),
  -- 按钮：恢复
  ('m2502', '恢复', 'button', 2, 'm2500', null, 'schedulerJob:resume', null,
   1, false, false, false, now(), 'system', '系统', '0', 'm2000/m2500/m2502'),
  -- 按钮：立即执行
  ('m2503', '立即执行', 'button', 3, 'm2500', null, 'schedulerJob:trigger', null,
   1, false, false, false, now(), 'system', '系统', '0', 'm2000/m2500/m2503'),
  -- 按钮：修改 Cron
  ('m2504', '修改Cron', 'button', 4, 'm2500', null, 'schedulerJob:updateCron', null,
   1, false, false, false, now(), 'system', '系统', '0', 'm2000/m2500/m2504')
on conflict (id) do nothing;

-- ============================== t_menu_api ==============================
insert into t_menu_api (id, menu_id, api_url, api_url_method, create_time, creator,
                        creator_name, tenant_id) values
  -- m2500 定时任务：列表 + 明细
  ('ma0036', 'm2500', '/scheduler/job/list', 'GET', now(), 'system', '系统', '0'),
  ('ma0037', 'm2500', '/scheduler/job/getJobDetail', 'GET', now(), 'system', '系统', '0'),
  -- m2501 暂停：单个 + 批量
  ('ma0038', 'm2501', '/scheduler/job/pause', 'POST', now(), 'system', '系统', '0'),
  ('ma0039', 'm2501', '/scheduler/job/pauseBatch', 'POST', now(), 'system', '系统', '0'),
  -- m2502 恢复：单个 + 批量
  ('ma0040', 'm2502', '/scheduler/job/resume', 'POST', now(), 'system', '系统', '0'),
  ('ma0041', 'm2502', '/scheduler/job/resumeBatch', 'POST', now(), 'system', '系统', '0'),
  -- m2503 立即执行：按 job + 按 trigger
  ('ma0042', 'm2503', '/scheduler/job/triggerJob', 'POST', now(), 'system', '系统', '0'),
  ('ma0043', 'm2503', '/scheduler/job/trigger', 'POST', now(), 'system', '系统', '0'),
  -- m2504 修改 Cron
  ('ma0044', 'm2504', '/scheduler/job/updateCron', 'POST', now(), 'system', '系统', '0')
on conflict (id) do nothing;

-- 菜单合计: 5 条 (MENU=1, BUTTON=4)
-- 接口合计: 9 条
