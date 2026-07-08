# 二手交易平台前端功能实现说明

## 1. 文档目的

本文档基于当前项目前端代码，说明这个二手交易平台前端部分的实现方式，重点回答以下问题：

1. 前端整体采用了什么技术结构。
2. 前端每一层分别承担什么职责。
3. 各个功能页面是如何实现的。
4. 前端是如何通过路由、状态管理、接口封装与后端协同完成业务功能的。

本文档对应的前端代码主目录为：

- `frontend/src/main.js`
- `frontend/src/App.vue`
- `frontend/src/router`
- `frontend/src/stores`
- `frontend/src/api`
- `frontend/src/layouts`
- `frontend/src/views`
- `frontend/src/components`
- `frontend/src/utils`
- `frontend/src/styles.css`

---

## 2. 前端总体架构

当前项目前端基于 `Vue 3 + Vite + Vue Router + Pinia + Axios` 构建，整体调用链如下：

1. 用户进入某个页面路由。
2. Vue Router 将页面加载到布局中。
3. 页面组件在 `onMounted`、`watch` 或点击事件中发起接口请求。
4. 请求通过 `api/modules.js` 调用封装好的业务 API。
5. API 最终通过 `api/http.js` 中的 Axios 实例访问后端 `/api/**` 接口。
6. 后端返回数据后，页面更新本地状态并重新渲染。

可以把前端理解成下面这条链路：

`路由 -> 页面组件 -> 状态/表单 -> API 模块 -> Axios -> 后端 -> 页面渲染`

---

## 3. 前端分层说明

## 3.1 入口层

相关文件：

- `frontend/src/main.js`
- `frontend/src/App.vue`

### 实现方式

- `main.js` 创建 Vue 应用实例。
- 注册 Pinia。
- 注册 Router。
- 注册全局枚举显示函数 `enumLabel`。
- 挂载根组件 `App.vue`。

`App.vue` 本身非常轻，只保留：

- `<router-view />`

这说明整个应用是典型的路由驱动型单页应用，页面切换主要依赖路由系统完成。

---

## 3.2 路由层

相关文件：

- `frontend/src/router/index.js`

### 职责

- 定义页面路径。
- 区分公开页面、登录后页面、管理员页面。
- 在页面切换前做登录态校验与权限控制。

### 路由结构

项目当前主要分为三类页面：

1. 公开页面
   - `/`
   - `/market`
   - `/goods/:id`
   - `/wanted`
   - `/login`
   - `/register`
2. 普通登录用户页面
   - `/cart`
   - `/profile`
   - `/favorites`
   - `/messages`
   - `/orders/purchases`
   - `/orders/sales`
   - `/seller/goods`
   - `/publish`
   - `/seller/goods/:id/edit`
3. 管理员页面
   - `/admin/dashboard`
   - `/admin/users`
   - `/admin/goods`
   - `/admin/categories`
   - `/admin/orders`

### 路由守卫逻辑

`beforeEach` 中实现了三类前端权限控制：

1. 如果目标路由要求登录，而当前未登录，则跳转到 `/login`。
2. 如果目标路由要求管理员权限，而当前用户不是管理员，则跳转首页。
3. 如果当前用户是管理员，则禁止进入购物车、收藏、消息、购买记录、售卖记录、发布商品等普通用户页面。

### 功能意义

- 前端先做一层体验级权限拦截。
- 避免未登录用户直接进入需要登录的业务页。
- 避免管理员误进入普通用户交易流。
- 与后端权限控制形成前后双重保护。

---

## 3.3 状态管理层

相关文件：

- `frontend/src/stores/auth.js`

### 职责

- 维护当前登录 Token。
- 维护当前登录用户信息。
- 提供登录、注册、退出、初始化登录态的方法。

### 状态字段

- `token`
- `user`
- `initialized`

### 关键逻辑

#### 1. bootstrap

页面首次加载时，如果本地存在 Token，则调用 `authApi.me()` 向后端确认当前登录状态是否仍有效：

- 有效：恢复用户信息。
- 无效：自动登出并清空本地缓存。

#### 2. login

登录成功后：

- 保存 `accessToken`
- 保存 `user`
- 写入 `localStorage`

#### 3. logout

退出时：

- 清空状态中的 Token 和用户信息
- 清除本地缓存

### 功能意义

- 把登录状态从单个页面中抽离出来，形成全局共享状态。
- 路由守卫、布局导航、交易页面都依赖这个 Store 判断当前用户身份。

---

## 3.4 接口封装层

相关文件：

- `frontend/src/api/http.js`
- `frontend/src/api/modules.js`

### 1. http.js 的职责

`http.js` 创建统一 Axios 实例，负责：

- 统一 `baseURL = /api`
- 统一请求超时
- 请求时自动追加 `Authorization` 请求头
- 响应失败时统一转换错误信息
- 401 时自动清除登录态并跳转登录页

### 2. 错误处理机制

`extractErrorMessage()` 会根据不同情况生成用户可读错误信息，例如：

- 404：接口不存在或后端未启动
- 500：服务器内部错误
- 403：没有权限
- 400：请求参数不正确
- 超时：请求超时
- 无响应：无法连接后端

### 3. modules.js 的职责

`modules.js` 按业务模块封装 API，包含：

- `authApi`
- `catalogApi`
- `addressApi`
- `favoriteApi`
- `chatApi`
- `wantedApi`
- `sellerApi`
- `cartApi`
- `orderApi`
- `profileApi`
- `adminApi`

### 功能意义

- 页面不直接拼接接口地址。
- 把接口调用按业务域归类，降低页面复杂度。
- 当后端路径变化时，只需集中修改 API 模块。

---

## 3.5 布局层

相关文件：

- `frontend/src/layouts/MainLayout.vue`

### 职责

- 提供整站统一的页面框架。
- 统一顶部导航、搜索框、左侧菜单、用户入口。
- 根据管理员和普通用户身份切换导航菜单。

### 主要实现逻辑

1. 顶部提供：
   - 品牌入口
   - 搜索框
   - 登录/注册或当前用户/退出按钮
2. 左侧菜单根据 `authStore.user?.admin` 动态计算：
   - 普通用户菜单
   - 管理员菜单
3. 页面主体通过内部 `<router-view />` 承载具体页面。
4. 搜索框会把关键字写入当前路由 query，驱动首页商品检索。

### 功能意义

- 让不同页面共享统一外壳。
- 让“搜索”这种跨页面能力直接挂到布局层，而不是只存在于某个页面里。
- 使管理员端和普通端复用同一套基础布局，但菜单内容不同。

---

## 3.6 组件层

相关文件：

- `frontend/src/components/GoodsCard.vue`
- `frontend/src/components/StatusTabs.vue`

### GoodsCard 组件

职责：

- 统一渲染商品卡片。
- 展示图片、分类、价格、库存、卖家等信息。
- 处理商品已下架或不可点击状态。
- 支持在收藏页中作为可移除卡片使用。

实现特点：

- 根据 `item.status !== 'ACTIVE'` 判断卡片是否锁定。
- 点击卡片时跳转到详情页。
- 在 `removable` 模式下，通过事件把“移除收藏”交给父页面处理。

### StatusTabs 组件

职责：

- 为订单页等需要状态切换的页面提供统一 tab 切换 UI。

实现特点：

- 使用 `v-model` 双向绑定当前状态。
- 父组件传入可选状态数组，子组件只负责 UI 呈现和事件抛出。

### 功能意义

- 把重复 UI 抽离出来，减少页面间重复代码。
- 保持商品展示和状态切换在不同页面中的一致性。

---

## 3.7 工具层

相关文件：

- `frontend/src/utils/assets.js`
- `frontend/src/utils/enums.js`

### assets.js

职责：

- 将后端返回的图片路径转换为浏览器可访问的完整地址。

实现方式：

- 如果是绝对地址、`data:`、`blob:`，直接返回。
- 如果是相对路径，则基于 `import.meta.env.BASE_URL` 和当前站点地址拼出完整 URL。

### enums.js

职责：

- 把后端返回的状态枚举值转换成适合中文页面展示的文案。

包括：

- 用户状态
- 商品状态
- 订单状态
- 交付方式
- 求购状态

### 功能意义

- 后端维持稳定的英文枚举值。
- 前端负责用户可读化显示。
- 页面模板中无需到处写状态翻译判断。

---

## 3.8 样式层

相关文件：

- `frontend/src/styles.css`

### 职责

- 提供全局色板、布局变量、按钮、表单、卡片、表格、消息区、后台面板等样式规范。

### 特点

- 使用 `:root` 定义全局主题变量。
- 前台和后台共用一套基础设计体系。
- 大量页面都复用：
  - `panel-card`
  - `primary-btn`
  - `ghost-btn`
  - `text-input`
  - `goods-grid`
  - `data-table`
  - `metric-card`

### 功能意义

- 通过全局样式减少局部页面重复 CSS。
- 统一整站视觉语言。
- 提供响应式适配逻辑，保证移动端和中等宽度屏幕可用。

---

## 4. 各功能页面的前端实现与协作关系

下面按功能模块说明前端页面是如何与状态、组件、接口和后端协同工作的。

## 4.1 登录与注册

### 相关文件

- `frontend/src/views/LoginPage.vue`
- `frontend/src/views/RegisterPage.vue`
- `frontend/src/stores/auth.js`
- `frontend/src/api/modules.js`

### 登录页面实现

1. 页面维护本地表单：
   - `studentNo`
   - `password`
   - `admin`
2. 用户点击登录后调用 `authStore.login(form)`。
3. Store 内部再调用 `authApi.login(payload)` 请求后端。
4. 登录成功后：
   - 保存 Token
   - 保存用户信息
   - 跳转到 `redirect` 或首页
5. 失败时通过 `extractErrorMessage` 或 `error.userMessage` 显示可读错误提示。

### 注册页面实现

- 注册页负责收集注册表单，调用 `authStore.register(payload)`。
- 注册成功后通常返回登录页或提示用户继续登录。

### 协作特点

- 页面层负责表单交互。
- Store 层负责全局登录态写入。
- API 层负责接口访问。
- Router 负责登录后的跳转控制。

---

## 4.2 商品首页与分类筛选

### 相关文件

- `frontend/src/views/MarketPage.vue`
- `frontend/src/components/GoodsCard.vue`
- `frontend/src/layouts/MainLayout.vue`
- `frontend/src/api/modules.js`

### 实现流程

1. 首页维护查询对象：
   - `keyword`
   - `category`
   - `sort`
   - `page`
   - `pageSize`
2. 页面启动时读取当前路由 query，同步到本地查询对象。
3. 调用 `catalogApi.list(query)` 获取商品分页数据。
4. 调用 `catalogApi.categories()` 获取分类列表。
5. 分类栏点击后更新 query，并通过 `router.replace()` 回写到 URL。
6. 页面监听 `route.fullPath`，只要查询参数变化，就自动重新请求商品列表。
7. 商品列表使用 `GoodsCard` 渲染。

### 协作特点

- 搜索框在 `MainLayout` 中，真正的数据请求在 `MarketPage` 中。
- URL query 充当了“页面状态与可分享链接”的中间层。
- 页面层依赖 API 获取数据，组件层只负责渲染。

---

## 4.3 商品详情页

### 相关文件

- `frontend/src/views/GoodsDetailPage.vue`
- `frontend/src/api/modules.js`
- `frontend/src/stores/auth.js`

### 实现流程

1. 页面根据路由参数 `id` 调用 `catalogApi.detail(id)` 获取商品详情。
2. 如果当前已登录且不是管理员，则额外并行请求：
   - `cartApi.list()`
   - `favoriteApi.list()`
3. 页面根据返回结果计算：
   - 当前商品是否已经在购物车中
   - 当前商品是否已被收藏
4. 页面提供四类核心操作：
   - 加入购物车
   - 立即下单
   - 收藏
   - 私信卖家
5. 未登录时执行交易行为会跳转登录页。
6. 管理员进入商品详情时，仅允许查看，不允许执行交易操作。

### 协作特点

- 一个页面同时调用商品、购物车、收藏、订单、聊天多个 API。
- 页面根据 `authStore` 决定当前可用操作。
- 前端通过状态判断把后端返回的商品状态转成“按钮是否可用”的界面逻辑。

---

## 4.4 购物车

### 相关文件

- `frontend/src/views/CartPage.vue`
- `frontend/src/api/modules.js`

### 实现流程

1. 页面启动时调用 `cartApi.list()` 获取购物车列表。
2. 为了补足卖家名称显示，页面会对每个购物车商品再调用 `catalogApi.detail(gid)` 查询详情。
3. 页面基于商品状态和库存判断：
   - 是否允许下单
   - 是否只允许移除或联系
4. 页面支持三类操作：
   - `cartApi.remove(gid)` 移除商品
   - `orderApi.create({ fromCart: true })` 从购物车下单
   - `chatApi.send(...)` 联系卖家
5. 下单完成后重新拉取购物车数据。

### 协作特点

- 购物车页不是单独展示本地缓存，而是完全依赖后端购物车数据。
- 页面会主动补充商品详情数据，以获得更完整的展示内容。
- 聊天和订单能力直接嵌入购物车流程中。

---

## 4.5 我的购买 / 我的售卖

### 相关文件

- `frontend/src/views/PurchasesPage.vue`
- `frontend/src/views/SalesPage.vue`
- `frontend/src/components/StatusTabs.vue`
- `frontend/src/api/modules.js`

### 实现流程

1. 页面使用 `StatusTabs` 切换订单状态。
2. 状态变化后调用：
   - 买家页：`orderApi.purchases(status)`
   - 卖家页：`orderApi.sales(status)`
3. 页面为了显示商品图，还会补查商品详情中的图片路径。
4. 买家页支持：
   - 联系卖家
   - 确认取货 / 确认收货
   - 删除订单
5. 卖家页支持：
   - 联系买家
   - 确认发货

### 协作特点

- 订单页面依赖订单 API 为主，商品 API 为辅。
- `StatusTabs` 负责状态切换 UI，页面负责真正的数据刷新逻辑。
- 聊天功能再次作为订单页中的辅助操作被复用。

---

## 4.6 我的商品 / 发布商品 / 编辑商品

### 相关文件

- `frontend/src/views/SellerGoodsPage.vue`
- `frontend/src/views/PublishGoodsPage.vue`
- `frontend/src/views/EditGoodsPage.vue`
- `frontend/src/api/modules.js`

### 我的商品页

实现流程：

1. 页面调用 `sellerApi.list()` 拉取自己发布的商品。
2. 根据商品状态判断：
   - 是否允许下架
   - 是否允许重新上架
   - 是否允许永久删除
3. 按钮行为包括：
   - 跳转编辑页
   - `sellerApi.remove(gid)` 下架
   - 跳转带 `mode=relist` 的编辑页
   - `sellerApi.permanentDelete(gid)` 永久删除

### 发布商品页

实现流程：

1. 页面先通过 `catalogApi.categories()` 获取分类选项。
2. 维护本地商品表单。
3. 如果选择了图片，先调用 `sellerApi.uploadImage(file)`。
4. 上传成功后将返回的图片路径写入 `form.image`。
5. 再调用 `sellerApi.create(form)` 提交商品信息。
6. 成功后跳回我的商品页。

### 编辑商品页

逻辑与发布商品页相似，但会：

- 先加载商品原始信息
- 允许针对已有商品更新信息
- 在重新上架场景中复用编辑页逻辑

### 协作特点

- 发布和编辑是典型的“前端两步提交”流程：
  - 先上传图片
  - 再提交商品业务数据
- 页面层负责组织表单与上传顺序。
- 后端负责真正的审核状态变化。

---

## 4.7 收藏页

### 相关文件

- `frontend/src/views/FavoritesPage.vue`
- `frontend/src/components/GoodsCard.vue`
- `frontend/src/api/modules.js`

### 实现流程

1. 页面调用 `favoriteApi.list()` 获取收藏列表。
2. 使用 `GoodsCard removable` 模式展示商品。
3. 点击移除时触发组件事件，父页面调用 `favoriteApi.remove(gid)`。
4. 更新收藏列表数据。

### 协作特点

- 收藏页复用了商品卡片组件。
- “移除收藏”不是组件自己直接发请求，而是通过事件把操作交还给页面，保持组件通用性。

---

## 4.8 私信聊天

### 相关文件

- `frontend/src/views/MessagesPage.vue`
- `frontend/src/api/modules.js`

### 实现流程

1. 页面启动时调用 `chatApi.conversations()` 获取会话列表。
2. 会话根据是否绑定订单分成：
   - 售前咨询
   - 售后订单对话
3. 页面根据当前路由 query 或已有活动会话决定默认选中会话。
4. 选择某个会话后调用 `chatApi.messages(conversationKey)` 获取消息列表。
5. 输入消息后调用 `chatApi.send(payload)` 发送。
6. 支持删除当前会话或列表中的某个会话：
   - `chatApi.removeConversation(conversationKey)`

### 协作特点

- 会话列表和消息列表是前端典型的主从结构。
- 页面通过路由 query 允许从订单页跳转到指定订单会话。
- 商品详情页、购物车页、订单页都可以发起聊天，最终汇聚到消息页中继续沟通。

---

## 4.9 求购广场

### 相关文件

- `frontend/src/views/WantedPage.vue`
- `frontend/src/api/modules.js`
- `frontend/src/stores/auth.js`

### 实现流程

1. 页面先获取商品分类列表。
2. 再调用 `wantedApi.list()` 获取公开求购信息。
3. 如果用户已登录，再调用 `wantedApi.mine()` 获取自己的求购记录。
4. 页面支持发布求购：
   - 填写标题、分类、关键词
   - 调用 `wantedApi.create(payload)`
5. 页面支持删除自己的求购：
   - 调用 `wantedApi.remove(id)`

### 协作特点

- 一个页面同时承载公开信息流和个人管理区。
- 登录态决定是否显示“我的求购”和发布按钮。
- 后端返回的匹配商品结果直接由前端做轻量展示。

---

## 4.10 个人中心

### 相关文件

- `frontend/src/views/ProfilePage.vue`
- `frontend/src/api/modules.js`

### 实现流程

1. 页面加载时调用：
   - `profileApi.get()` 获取个人资料
   - `addressApi.list()` 获取地址列表
2. 页面维护三个区域：
   - 基本资料编辑
   - 密码修改
   - 地址新增与管理
3. 保存资料时调用 `profileApi.update(profile)`。
4. 修改密码时调用 `profileApi.updatePassword(passwordForm)`。
5. 新增地址时调用 `addressApi.create(addressForm)`，然后重新拉取列表。
6. 删除地址时调用 `addressApi.remove(id)`，然后重新拉取列表。

### 协作特点

- 个人中心是一个复合型页面。
- 同一页面中整合了用户资料模块和地址模块。
- 页面通过局部刷新方式保证交互简单直接。

---

## 4.11 管理员后台

### 相关文件

- `frontend/src/views/AdminDashboardPage.vue`
- `frontend/src/views/AdminUsersPage.vue`
- `frontend/src/views/AdminOrdersPage.vue`
- `frontend/src/views/AdminGoodsPage.vue`
- `frontend/src/views/AdminCategoriesPage.vue`
- `frontend/src/api/modules.js`

### 数据看板

实现方式：

- 页面加载时调用 `adminApi.dashboard()`
- 使用指标卡片展示：
  - 累计发布
  - 当前上架
  - 待审核商品
  - 总订单数
  - 已完成订单
  - 黑名单用户
  - 开放求购数

### 商品审核页

实现方式：

1. 页面并行调用：
   - `adminApi.pendingGoods()`
   - `adminApi.goods()`
2. 上半部分展示待审核商品卡片。
3. 每个商品支持三种审核动作：
   - 通过
   - 驳回
   - 违规下架
4. 输入审核备注后调用：
   - `adminApi.reviewGoods(gid, { action, note })`
5. 成功后重新拉取数据。
6. 下半部分表格展示全站商品信息。

### 用户管理、订单管理、分类管理

这些页面的共同特点是：

- 页面加载时请求对应后台数据。
- 表单提交或按钮操作后调用后台接口。
- 成功后重新加载列表或局部刷新数据。

### 协作特点

- 管理员页在前端层面完全和普通用户页分流。
- 菜单、路由、可访问页面、操作按钮都围绕 `user.admin` 做控制。
- 后台页面更偏表格、列表、操作面板式交互。

---

## 5. 前端与后端的协作方式

从当前项目看，前后端协作有几个非常明确的模式。

## 5.1 基于 REST 接口协作

前端所有业务能力都通过 `modules.js` 中的 API 与后端 `/api/**` 接口通信，例如：

- 商品列表：`catalogApi.list`
- 下单：`orderApi.create`
- 发布商品：`sellerApi.create`
- 聊天：`chatApi.send`
- 管理员审核：`adminApi.reviewGoods`

这使前端页面不需要知道后端细节实现，只依赖稳定接口契约。

## 5.2 基于 JWT 的登录态协作

- 登录成功后前端保存 Token。
- 请求时 Axios 自动带上 Token。
- 后端根据 Token 识别用户。
- Token 失效时前端自动清理登录态并跳转登录页。

## 5.3 基于枚举值的展示协作

- 后端返回稳定的英文状态值，例如 `ACTIVE`、`PENDING_SHIPMENT`。
- 前端通过 `enumLabel()` 显示为中文文案。

这种做法让后端保持结构化表达，同时让前端页面更易读。

## 5.4 基于页面状态的交互协作

后端返回的是业务状态，前端负责把它转为界面行为，例如：

- 商品 `ACTIVE` 才允许购买
- 订单不同状态决定按钮文案与可操作性
- 管理员账号隐藏交易按钮
- 已收藏、已加入购物车状态直接改变按钮显示

---

## 6. 前端实现特点总结

从当前代码实现来看，这个项目前端有以下几个明显特点：

1. 采用了典型的 Vue 单页应用结构，路由驱动清晰。
2. 登录态管理集中在 Pinia 中，避免多个页面重复处理认证逻辑。
3. API 调用做了统一封装，页面层不直接依赖底层 Axios 细节。
4. 页面大多采用“加载数据 -> 本地状态渲染 -> 调用接口操作 -> 重新拉取数据”的稳定模式。
5. 商品卡片、状态切换、全局按钮和面板样式做了较高复用。
6. 普通用户流、卖家流、管理员流已经在路由、布局和页面结构上明显分层。
7. 前端不仅做展示，还承担了较完整的交互控制，例如搜索同步 URL、按钮状态控制、会话切换、订单动作引导等。

---

## 7. 结论

当前项目的前端已经不是简单的页面拼接，而是形成了较完整的分层结构：

1. 入口层负责启动应用。
2. 路由层负责页面组织与权限守卫。
3. Store 层负责登录态等全局状态。
4. API 层负责与后端通信。
5. 布局层负责统一页面框架。
6. 组件层负责复用展示单元。
7. 页面层负责具体业务交互。
8. 工具层和样式层负责展示辅助与视觉统一。

按这个结构，每个功能页面都不是孤立完成的，而是通过“路由进入 + 状态判断 + API 请求 + 页面渲染 + 操作回写”的方式形成完整闭环。  
这就是当前二手交易平台前端实现的核心思路。
