# Used-Item-Market

校园闲置物品智慧流转系统，当前代码库已经整理为前后端分目录结构：

- `frontend/`：Vue 3 + Vite 单页前端
- `backend/`：Spring MVC + MyBatis + Maven 后端
- `useditemmarket`：MySQL 数据库名

这个仓库里同时保留了两套实现：

1. 新版主流程：`Vue SPA + /api REST 接口`
2. 旧版兼容流程：`Thymeleaf/传统 MVC 页面`

目前真正建议使用的是新版前后端分离流程，旧版代码主要用于兼容和历史保留。

## 目录

- [1. 项目目标](#1-项目目标)
- [2. 技术栈与目录结构](#2-技术栈与目录结构)
- [3. 快速启动](#3-快速启动)
- [4. 系统总体架构](#4-系统总体架构)
- [5. 数据库设计与自动迁移](#5-数据库设计与自动迁移)
- [6. 核心业务功能说明](#6-核心业务功能说明)
- [7. 功能到代码文件的定位表](#7-功能到代码文件的定位表)
- [8. 图片逻辑说明](#8-图片逻辑说明)
- [9. 旧版页面兼容层](#9-旧版页面兼容层)
- [10. 当前存在的问题与改进建议](#10-当前存在的问题与改进建议)

## 1. 项目目标

项目围绕“校园二手闲置流转”展开，当前已经覆盖这些核心场景：

- 用户注册、登录、鉴权
- 商品浏览、搜索、筛选、详情查看
- 商品发布、编辑、下架
- 管理员商品审核
- 购物车、直接下单、订单流转
- 收货地址管理
- 收藏商品
- 求购信息发布与匹配
- 用户私信沟通
- 个人资料维护
- 管理后台（用户、商品、分类、订单、仪表盘）

## 2. 技术栈与目录结构

### 2.1 前端

- 框架：Vue 3
- 构建工具：Vite
- 路由：Vue Router
- 状态管理：Pinia
- HTTP：Axios

关键目录：

- `frontend/src/router/index.js`：前端路由和权限守卫
- `frontend/src/stores/auth.js`：登录态、用户信息、启动时鉴权恢复
- `frontend/src/api/http.js`：Axios 实例、JWT 注入、统一错误处理
- `frontend/src/api/modules.js`：按业务拆分 API 调用
- `frontend/src/views/`：各页面
- `frontend/src/layouts/`：普通用户端与管理端布局
- `frontend/src/components/`：商品卡片、状态筛选等复用组件

### 2.2 后端

- 框架：Spring MVC
- 持久层：MyBatis + JdbcTemplate
- 构建：Maven
- 认证：JWT + `HandlerInterceptor`
- 数据库：MySQL

关键目录：

- `backend/src/main/java/com/TropicalFlavor/controller/api/`：新版 REST 接口控制器
- `backend/src/main/java/com/TropicalFlavor/service/api/`：新版业务服务接口
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/`：新版业务实现
- `backend/src/main/java/com/TropicalFlavor/repository/PlatformRepository.java`：基于 `JdbcTemplate` 的补充查询与聚合逻辑
- `backend/src/main/java/com/TropicalFlavor/dao/`：传统 MyBatis DAO
- `backend/src/main/resources/mapping/`：MyBatis XML
- `backend/src/main/resources/spring-mvc.xml`：MVC、CORS、拦截器、静态资源
- `backend/src/main/resources/spring-mybatis.xml`：数据源、事务、Mapper 扫描
- `backend/src/main/java/com/TropicalFlavor/config/DatabaseMigrationService.java`：启动时自动补表补字段

### 2.3 运行脚本

- `start-project.ps1`：项目根目录一键启动前后端
- `stop-project.ps1`：项目根目录一键停止
- `backend/start-tomcat9.ps1`：打包 WAR 后启动本地 Tomcat 9
- `backend/stop-tomcat9.ps1`：停止本地 Tomcat 9

## 3. 快速启动

### 3.1 数据库准备

需要本地 MySQL，并创建数据库：

```sql
create database useditemmarket;
```

本仓库不再提交真实数据库密码。请在本地准备：

- `backend/src/main/resources/jdbc.properties`

可以直接参考：

- `backend/src/main/resources/jdbc.properties.example`

### 3.2 一键启动

```powershell
powershell -ExecutionPolicy Bypass -File .\start-project.ps1
```

默认访问地址：

- 前端：`http://localhost:5173`
- 后端：`http://localhost:8080`

停止：

```powershell
powershell -ExecutionPolicy Bypass -File .\stop-project.ps1
```

### 3.3 分开启动

后端：

```powershell
cd backend
powershell -ExecutionPolicy Bypass -File .\start-tomcat9.ps1
```

前端：

```powershell
cd frontend
npm install
npm run dev
```

## 4. 系统总体架构

### 4.1 请求链路

新版业务主链路如下：

1. Vue 页面发起交互
2. `frontend/src/api/modules.js` 调用后端 `/api/**`
3. `frontend/src/api/http.js` 自动附带 `Authorization: Bearer <token>`
4. `backend/src/main/resources/spring-mvc.xml` 中的 `authInterceptor` 拦截 `/api/**`
5. `backend/src/main/java/com/TropicalFlavor/security/AuthInterceptor.java` 验证 JWT
6. 对应 `controller/api/*` 接收请求
7. 进入 `service/api/impl/*` 完成业务
8. DAO XML 或 `PlatformRepository` 访问 MySQL
9. 返回 `ApiResponse` 或 `PageResponse`

### 4.2 鉴权模型

- 登录成功后由 `JwtTokenService` 生成 JWT
- Token 中记录：
  - `uid`
  - `admin`
- 前端把 token 存到 `localStorage`
- `router/index.js` 在路由切换时检查：
  - 是否需要登录
  - 是否需要管理员权限
- 后端再次做服务端校验，避免只靠前端限制

### 4.3 新旧架构并存

当前后端同时存在两套入口：

- 新版：`controller/api/*`
- 旧版：`controller/*`

新版前端使用 REST 接口，旧版页面仍然可以通过 Tomcat 模板渲染访问。两套逻辑有部分复用 DAO，也有部分重复实现。

## 5. 数据库设计与自动迁移

### 5.1 自动迁移入口

`backend/src/main/java/com/TropicalFlavor/config/DatabaseMigrationService.java`

项目启动后会自动执行以下事情：

- 为旧表补字段
- 为新功能建表
- 初始化商品分类
- 把旧数据状态迁移到新状态字段

这让项目在已有旧库的基础上，也能自动扩展到当前版本。

### 5.2 核心数据表

#### `user`

用途：用户基础信息、登录信息、身份状态。

关键字段：

- `UID`：主键，普通用户类似 `NORM20269997`，管理员类似 `ADMI2021001`
- `Uname`
- `Email`
- `PhoneNum`
- `Password`
- `Status`
- `StudentNo`
- `RealName`
- `CampusVerified`
- `Avatar`
- `Bio`

#### `marketgoods`

用途：商品主表。

关键字段：

- `GID`
- `Name`
- `Kind`
- `Price`
- `Number`
- `Image`
- `Comment`
- `Status`
- `DeliveryMode`
- `PickupLocation`
- `CampusOnly`
- `ReviewNote`
- `PublishedAt`

#### `salegoods`

用途：卖家和商品的关系表。

作用：

- 一个商品属于哪个卖家
- 商品列表返回时通过它反查卖家 UID

#### `shoppingcart`

用途：购物车关系表。

关键字段：

- `UID`
- `GID`
- `Number`

#### `traderecord`

用途：订单 / 交易记录。

关键字段：

- `PID`
- `BuyerID`
- `SellerID`
- `GID`
- `Gname`
- `Gkind`
- `Gprice`
- `Gnumber`
- `IsSent`
- `IsGot`
- `Status`
- `DeliveryMode`
- `PickupLocation`
- `AppointmentTime`
- `Remark`
- `AddressSnapshot`

#### `user_address`

用途：收货地址。

#### `favorite_goods`

用途：收藏关系。

#### `chat_message`

用途：私信消息。

#### `goods_category`

用途：商品分类配置。

#### `wanted_post`

用途：求购信息。

### 5.3 表关系理解

- `user` 1 -> n `salegoods`
- `salegoods` n -> 1 `marketgoods`
- `user` 1 -> n `shoppingcart`
- `shoppingcart` n -> 1 `marketgoods`
- `user` 1 -> n `traderecord`（买家）
- `user` 1 -> n `traderecord`（卖家）
- `user` 1 -> n `user_address`
- `user` 1 -> n `favorite_goods`
- `favorite_goods` n -> 1 `marketgoods`
- `user` 1 -> n `wanted_post`
- `user` 1 -> n `chat_message`（发送者/接收者）

## 6. 核心业务功能说明

### 6.1 用户注册、登录与权限

前端入口：

- `frontend/src/views/LoginPage.vue`
- `frontend/src/views/RegisterPage.vue`
- `frontend/src/stores/auth.js`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/AuthController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/security/JwtTokenService.java`
- `backend/src/main/java/com/TropicalFlavor/security/AuthInterceptor.java`
- `backend/src/main/resources/mapping/UserDao.xml`

业务逻辑：

1. 登录页提交学号、密码、是否管理员
2. `AuthServiceImpl.login()` 把明文密码做 MD5
3. `UserDao.IsTrue` 根据学号后缀和密码校验账号
4. 登录成功后生成 JWT
5. 前端保存 token 和 user
6. 访问受保护页面时，路由守卫和后端拦截器双重鉴权

数据库涉及：

- `user`

### 6.2 商品浏览、搜索、筛选、详情

前端入口：

- `frontend/src/views/MarketPage.vue`
- `frontend/src/views/GoodsDetailPage.vue`
- `frontend/src/components/GoodsCard.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/CatalogController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/CatalogServiceImpl.java`
- `backend/src/main/resources/mapping/GoodsDao.xml`
- `backend/src/main/resources/mapping/SalesDao.xml`
- `backend/src/main/java/com/TropicalFlavor/repository/PlatformRepository.java`

业务逻辑：

1. 商品广场请求 `/api/catalog/goods`
2. `CatalogServiceImpl.queryGoods()` 先拿全部 `ACTIVE` 商品
3. 在 Java 内存中按关键字、分类过滤
4. 再按价格、库存、最新发布排序
5. 最后做分页切片
6. 详情页通过 `GID` 查询单个商品，并补充卖家 UID / 卖家名称

数据库涉及：

- `marketgoods`
- `salegoods`
- `goods_category`

### 6.3 商品发布、编辑、下架、审核

前端入口：

- `frontend/src/views/PublishGoodsPage.vue`
- `frontend/src/views/EditGoodsPage.vue`
- `frontend/src/views/SellerGoodsPage.vue`
- `frontend/src/views/AdminGoodsPage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/SellerGoodsController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/SellerGoodsServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/controller/api/AdminApiController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/AdminOpsServiceImpl.java`
- `backend/src/main/resources/mapping/GoodsDao.xml`
- `backend/src/main/resources/mapping/SalesDao.xml`

业务逻辑：

1. 用户发布商品时提交：
   - 名称
   - 分类
   - 价格
   - 库存
   - 图片地址
   - 描述
   - 交付方式
   - 自提/送货说明
2. 后端创建 `marketgoods` 记录
3. 同时写入 `salegoods`，建立“卖家-商品”关系
4. 新发布商品默认状态为 `PENDING_REVIEW`
5. 管理员在后台审核后：
   - `approve` -> `ACTIVE`
   - `reject` -> `REJECTED`
   - `ban` -> `BANNED` 并将库存清零
6. 卖家编辑商品后会重新回到待审核状态
7. 卖家下架商品时，将库存设为 `0`，状态设为 `OFF_SHELF`

数据库涉及：

- `marketgoods`
- `salegoods`

### 6.4 购物车

前端入口：

- `frontend/src/views/CartPage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/CartController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/CartServiceImpl.java`
- `backend/src/main/resources/mapping/CarDao.xml`

业务逻辑：

1. 加入购物车时检查：
   - 购买数量必须大于 0
   - 不能买自己的商品
   - 不得超过当前库存
2. 若购物车中已存在该商品，则累加数量
3. 购物车表只存：
   - 用户
   - 商品
   - 数量
4. 名称、价格、图片仍然从商品表关联查询

数据库涉及：

- `shoppingcart`
- `marketgoods`

### 6.5 订单与交易流程

前端入口：

- `frontend/src/views/GoodsDetailPage.vue`
- `frontend/src/views/PurchasesPage.vue`
- `frontend/src/views/SalesPage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/OrderController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/OrderServiceImpl.java`
- `backend/src/main/resources/mapping/RecordDao.xml`
- `backend/src/main/resources/mapping/SRecordDao.xml`

业务逻辑：

1. 用户可直接下单，或从购物车下单
2. 下单时后端会：
   - 校验不是买自己的商品
   - 校验商品状态必须为 `ACTIVE`
   - 校验库存充足
3. 通过 `nextPid()` 生成订单号
4. 扣减商品库存
5. 写入 `traderecord`
6. 如果来自购物车，则同步移除购物车项
7. 订单状态流转为：
   - `PENDING_CONTACT`
   - `PENDING_PICKUP`
   - `COMPLETED`
8. 卖家发货（或确认可取）后状态变为 `PENDING_PICKUP`
9. 买家确认收货后状态变为 `COMPLETED`

数据库涉及：

- `traderecord`
- `marketgoods`
- `shoppingcart`
- `user_address`

### 6.6 收货地址

前端入口：

- `frontend/src/views/ProfilePage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/AddressController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/AddressServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/repository/PlatformRepository.java`

业务逻辑：

1. 用户维护自己的多个地址
2. 可设置默认地址
3. 下单时如果没显式传地址 ID，则自动抓默认地址快照
4. 地址快照写入订单，避免后续用户改地址导致历史订单失真

数据库涉及：

- `user_address`
- `traderecord`

### 6.7 收藏

前端入口：

- `frontend/src/views/FavoritesPage.vue`
- `frontend/src/views/GoodsDetailPage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/FavoriteController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/FavoriteServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/repository/PlatformRepository.java`

业务逻辑：

1. 商品详情页可收藏
2. 收藏关系去重
3. 收藏列表只返回当前仍为 `ACTIVE` 的商品

数据库涉及：

- `favorite_goods`
- `marketgoods`
- `salegoods`

### 6.8 求购广场

前端入口：

- `frontend/src/views/WantedPage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/WantedController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/WantedServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/repository/PlatformRepository.java`

业务逻辑：

1. 用户可发布求购信息
2. 求购信息包含：
   - 标题
   - 分类
   - 预算
   - 关键词
   - 描述
3. 平台查询开放中的求购帖
4. 对每个求购帖，后台会自动匹配最多 6 条可能相关的在售商品
5. 匹配规则：
   - 商品分类相同
   - 或名称模糊匹配关键词
   - 或描述模糊匹配关键词
6. 用户也可以关闭自己的求购帖

数据库涉及：

- `wanted_post`
- `marketgoods`
- `salegoods`

### 6.9 私信消息

前端入口：

- `frontend/src/views/MessagesPage.vue`
- `frontend/src/views/GoodsDetailPage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/ChatController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/ChatServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/repository/PlatformRepository.java`

业务逻辑：

1. 商品详情页可以给卖家发起私信
2. 系统按“对话用户”聚合会话
3. 进入会话后读取双方消息时间线
4. 读取消息时自动把对方发来的消息标记为已读

数据库涉及：

- `chat_message`

### 6.10 个人中心

前端入口：

- `frontend/src/views/ProfilePage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/ProfileController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/ProfileServiceImpl.java`

业务逻辑：

1. 读取当前登录用户资料
2. 更新昵称、邮箱、手机号、实名、头像、简介
3. 修改密码时重新写入 MD5 后的密码
4. 修改密码后状态会恢复到正常状态

数据库涉及：

- `user`

### 6.11 管理后台

前端入口：

- `frontend/src/views/AdminDashboardPage.vue`
- `frontend/src/views/AdminUsersPage.vue`
- `frontend/src/views/AdminGoodsPage.vue`
- `frontend/src/views/AdminCategoriesPage.vue`
- `frontend/src/views/AdminOrdersPage.vue`

后端入口：

- `backend/src/main/java/com/TropicalFlavor/controller/api/AdminApiController.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/AdminOpsServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/AdminUserServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/repository/PlatformRepository.java`

业务逻辑：

管理员端目前包括：

- 仪表盘统计
- 用户列表
- 用户资料修改
- 用户停用
- 用户密码重置
- 商品待审核列表
- 商品全量列表
- 商品审核
- 商品分类管理
- 全量订单查看

仪表盘统计内容来自 `PlatformRepository.loadDashboard()`，主要通过聚合 SQL 统计：

- 商品总数
- 在售商品数
- 待审核商品数
- 订单总数
- 已完成订单数
- 停用用户数
- 开放中的求购数

数据库涉及：

- `user`
- `marketgoods`
- `traderecord`
- `goods_category`
- `wanted_post`

## 7. 功能到代码文件的定位表

| 功能 | 前端文件 | 后端入口 | 业务实现 | 数据表 |
| --- | --- | --- | --- | --- |
| 登录/注册 | `frontend/src/views/LoginPage.vue` `frontend/src/views/RegisterPage.vue` `frontend/src/stores/auth.js` | `controller/api/AuthController.java` | `service/api/impl/AuthServiceImpl.java` `security/JwtTokenService.java` | `user` |
| 路由鉴权 | `frontend/src/router/index.js` | `security/AuthInterceptor.java` | `spring-mvc.xml` | `user` |
| 商品广场 | `frontend/src/views/MarketPage.vue` | `controller/api/CatalogController.java` | `service/api/impl/CatalogServiceImpl.java` | `marketgoods` `salegoods` `goods_category` |
| 商品详情 | `frontend/src/views/GoodsDetailPage.vue` | `controller/api/CatalogController.java` | `service/api/impl/CatalogServiceImpl.java` | `marketgoods` `salegoods` |
| 商品发布/编辑 | `frontend/src/views/PublishGoodsPage.vue` `frontend/src/views/EditGoodsPage.vue` | `controller/api/SellerGoodsController.java` | `service/api/impl/SellerGoodsServiceImpl.java` | `marketgoods` `salegoods` |
| 卖家商品管理 | `frontend/src/views/SellerGoodsPage.vue` | `controller/api/SellerGoodsController.java` | `service/api/impl/SellerGoodsServiceImpl.java` | `marketgoods` `salegoods` |
| 管理员商品审核 | `frontend/src/views/AdminGoodsPage.vue` | `controller/api/AdminApiController.java` | `service/api/impl/AdminOpsServiceImpl.java` | `marketgoods` `salegoods` |
| 购物车 | `frontend/src/views/CartPage.vue` | `controller/api/CartController.java` | `service/api/impl/CartServiceImpl.java` | `shoppingcart` `marketgoods` |
| 订单 | `frontend/src/views/PurchasesPage.vue` `frontend/src/views/SalesPage.vue` | `controller/api/OrderController.java` | `service/api/impl/OrderServiceImpl.java` | `traderecord` `marketgoods` |
| 地址管理 | `frontend/src/views/ProfilePage.vue` | `controller/api/AddressController.java` | `service/api/impl/AddressServiceImpl.java` | `user_address` |
| 收藏 | `frontend/src/views/FavoritesPage.vue` | `controller/api/FavoriteController.java` | `service/api/impl/FavoriteServiceImpl.java` | `favorite_goods` |
| 私信 | `frontend/src/views/MessagesPage.vue` | `controller/api/ChatController.java` | `service/api/impl/ChatServiceImpl.java` | `chat_message` |
| 求购 | `frontend/src/views/WantedPage.vue` | `controller/api/WantedController.java` | `service/api/impl/WantedServiceImpl.java` | `wanted_post` |
| 个人中心 | `frontend/src/views/ProfilePage.vue` | `controller/api/ProfileController.java` | `service/api/impl/ProfileServiceImpl.java` | `user` |
| 管理员用户管理 | `frontend/src/views/AdminUsersPage.vue` | `controller/api/AdminApiController.java` | `service/api/impl/AdminUserServiceImpl.java` | `user` |
| 管理员分类管理 | `frontend/src/views/AdminCategoriesPage.vue` | `controller/api/AdminApiController.java` | `service/api/impl/AdminOpsServiceImpl.java` | `goods_category` |
| 管理员订单查看 | `frontend/src/views/AdminOrdersPage.vue` | `controller/api/AdminApiController.java` | `service/api/impl/AdminUserServiceImpl.java` | `traderecord` |

## 8. 图片逻辑说明

当前项目里商品图片存在“两套逻辑并存”。

### 8.1 新版前后端分离逻辑

主流程文件：

- `frontend/src/views/PublishGoodsPage.vue`
- `frontend/src/views/EditGoodsPage.vue`
- `frontend/src/components/GoodsCard.vue`
- `frontend/src/views/GoodsDetailPage.vue`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/SellerGoodsServiceImpl.java`

逻辑：

1. 前端发布商品时让用户填写“图片 URL”
2. 后端把这个字符串直接写入 `marketgoods.Image`
3. 前端展示商品时直接把 `image` 作为 `<img src>` 使用
4. 如果为空，则使用默认占位图

也就是说，新版主流程不是“上传文件”，而是“保存图片地址”。

### 8.2 旧版传统 MVC 上传逻辑

相关文件：

- `backend/src/main/java/com/TropicalFlavor/controller/userController.java`
- `backend/src/main/resources/spring-mvc.xml`
- `backend/src/main/webapp/image/`

逻辑：

1. 旧版页面通过 `MultipartFile image` 上传图片
2. 图片保存到 `src/main/webapp/image/`
3. 通过 Spring MVC 静态资源映射 `/image/**` 访问

### 8.3 当前实际影响

因为新版前端只代理了 `/api`，没有代理 `/image`，所以：

- 外链图片 URL 通常能正常显示
- 旧版相对路径图片在新版前端里不一定能正确显示

这是当前图片体系最明显的不一致点。

## 9. 旧版页面兼容层

虽然项目现在主要跑新版 SPA，但旧版服务端页面和控制器仍然保留：

- `backend/src/main/java/com/TropicalFlavor/controller/HomeController.java`
- `backend/src/main/java/com/TropicalFlavor/controller/GoodsController.java`
- `backend/src/main/java/com/TropicalFlavor/controller/userController.java`
- `backend/src/main/java/com/TropicalFlavor/controller/AdminController.java`
- `backend/src/main/java/com/TropicalFlavor/controller/RegisterController.java`

模板资源主要在：

- `backend/src/main/webapp/WEB-INF/templates/`

这部分代码的意义：

- 保留旧系统入口
- 保留旧文件上传逻辑
- 保留旧的管理和交易页面

但它也带来了较明显的维护复杂度，因为和新版 REST 逻辑存在重复。

## 10. 当前存在的问题与改进建议

### 10.1 新旧两套业务逻辑并存，存在重复维护成本

表现：

- 旧版 `controller/*` 和新版 `controller/api/*` 同时存在
- 商品发布、图片处理、订单流程有两种实现方式

影响：

- 容易一边改了，另一边漏改
- 新功能难以保证新旧页面都一致

建议：

- 明确以新版 SPA 为主线
- 逐步下线旧版页面，或把旧版页面彻底迁移到前端

### 10.2 商品图片体系不统一

表现：

- 新版保存图片 URL
- 旧版上传到本地 `/image/**`
- `vite.config.js` 只代理 `/api`，不代理 `/image`

影响：

- 有些商品图能显示，有些不能显示
- 历史数据中的相对路径图在新版前端兼容性差

建议：

- 统一改成文件上传 + 统一访问域名
- 或统一改成图床 URL
- 同时在前端做图片地址规范化处理

### 10.3 商品列表查询在 Java 内存中做过滤和分页

相关文件：

- `backend/src/main/java/com/TropicalFlavor/service/api/impl/CatalogServiceImpl.java`

表现：

- 先把所有 `ACTIVE` 商品查出来
- 再在 Java 中筛选、排序、分页

影响：

- 商品量大时效率差
- 不适合生产规模数据

建议：

- 迁移到 SQL 层分页与排序
- 使用明确的查询条件和索引

### 10.4 主键生成方式存在并发风险

相关文件：

- `backend/src/main/java/com/TropicalFlavor/service/api/impl/AbstractApiSupport.java`

表现：

- `nextGid()` 和 `nextPid()` 使用 `max + 1`

影响：

- 并发下单或并发发商品时可能生成重复主键

建议：

- 改为数据库自增
- 或使用雪花 ID / UUID / 号段方案

### 10.5 密码仍然使用 MD5，不适合正式环境

相关文件：

- `backend/src/main/java/com/TropicalFlavor/service/api/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/service/api/impl/ProfileServiceImpl.java`
- `backend/src/main/java/com/TropicalFlavor/util/Md5Util.java`

影响：

- 安全性不足

建议：

- 改为 BCrypt 或 Argon2

### 10.6 JWT 密钥硬编码在代码中

相关文件：

- `backend/src/main/java/com/TropicalFlavor/security/JwtTokenService.java`

影响：

- 不利于公开仓库和环境隔离

建议：

- 改为环境变量或外部配置

### 10.7 管理员重置密码流程目前不完整

相关文件：

- `backend/src/main/java/com/TropicalFlavor/service/api/impl/AdminUserServiceImpl.java`

表现：

- 重置时生成随机字符串并写入数据库
- 但 API 返回中没有把新密码告知管理员

影响：

- 被重置的用户实际上拿不到新密码

建议：

- 返回一次性初始密码
- 或触发短信/邮件通知
- 或改为“强制下次登录自助重置”

### 10.8 部分字段清空能力不足

表现：

- 例如头像、简介这类字段，服务层会把空字符串转成 `null`
- 但 MyBatis XML 又用 `<if test="字段!=null">` 控制更新

影响：

- 某些已有字段无法真正清空

建议：

- 在 DTO 层明确“未传值”和“传空值”的语义
- 或改为显式更新策略

### 10.9 代码中仍有中文乱码痕迹

表现：

- 一些 Java/Vue 文件中的中文文案已经出现乱码

影响：

- 维护阅读困难
- 页面文案可能异常

建议：

- 统一改成 UTF-8 编码
- 批量修正文案资源

### 10.10 缺少自动化测试

表现：

- 当前几乎没有针对业务流程的单元测试和集成测试

影响：

- 每次改动都更依赖人工回归

建议：

- 后端补服务层测试
- 前端补关键页面和接口流程测试
- 配上 CI

### 10.11 数据库迁移是“启动即修表”，但没有版本化

相关文件：

- `backend/src/main/java/com/TropicalFlavor/config/DatabaseMigrationService.java`

表现：

- 当前方式简单直接，但没有迁移版本记录

影响：

- 长期维护时难追踪每次结构变化

建议：

- 后续迁移到 Flyway 或 Liquibase

---

如果后续继续演进，我会优先建议按下面顺序推进：

1. 统一图片上传/展示链路
2. 移除旧版 MVC 重复逻辑
3. 把商品列表、订单列表查询下沉到 SQL 分页
4. 替换 MD5 和硬编码密钥
5. 为关键流程补测试
