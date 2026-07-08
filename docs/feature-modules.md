# 二手交易平台功能模块说明

## 1. 文档说明

本文档基于当前项目代码整理，说明这个二手交易平台已经具备的功能模块，以及各模块在前端、后端和数据层的实现方式。

项目当前采用前后端分离结构：

- 前端：`Vue 3 + Vite + Vue Router + Pinia + Axios`
- 后端：`Spring MVC + Spring Service + MyBatis/JdbcTemplate`
- 数据库：`MySQL`
- 鉴权：`JWT + AuthInterceptor`
- 图片存储：本地文件存储 + `/img/**` 静态资源映射

## 2. 总体架构

当前平台的核心调用链如下：

1. 前端页面发起操作。
2. `frontend/src/api/modules.js` 按业务模块封装接口请求。
3. 后端 `controller/api` 接收请求。
4. `service/api/impl` 处理业务规则、权限校验和状态流转。
5. `dao` 或 `repository` 访问数据库。
6. 后端返回 `ApiResponse` 或对应 `VO` 给前端渲染。

## 3. 功能模块总览

当前项目可以分成以下几个主要功能模块：

1. 用户认证与权限控制
2. 商品浏览与分类检索
3. 商品发布、编辑、下架与永久删除
4. 商品图片上传
5. 购物车
6. 订单交易与状态流转
7. 收货地址管理
8. 收藏夹
9. 私信聊天
10. 求购信息
11. 个人中心
12. 管理员后台

---

## 4. 用户认证与权限控制

### 4.1 模块功能

- 用户注册
- 用户登录
- 获取当前登录用户信息
- 区分普通用户和管理员
- 对需要登录/管理员权限的页面和接口做访问控制

### 4.2 前端实现

相关文件：

- `frontend/src/views/LoginPage.vue`
- `frontend/src/views/RegisterPage.vue`
- `frontend/src/stores/auth.js`
- `frontend/src/router/index.js`
- `frontend/src/api/modules.js`

实现方式：

- 登录、注册页面提交表单后调用 `authApi.login()` 和 `authApi.register()`。
- 登录成功后，前端将 `token` 和用户信息写入 Pinia 与本地缓存。
- 路由守卫在进入页面前检查：
  - 是否需要登录；
  - 是否需要管理员权限；
  - 管理员是否访问了被限制的普通用户页面。

### 4.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/AuthController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/AuthServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/security/JwtTokenService.java`
- `backend/src/main/java/com/useditemmarket/security/AuthInterceptor.java`

实现方式：

- 注册接口校验学号、用户名、密码、邮箱、手机号等信息。
- 密码通过 `Md5Util` 做 MD5 处理后保存。
- 登录时根据账号密码查询用户并生成 JWT。
- `AuthInterceptor` 解析请求头中的 `Authorization: Bearer <token>`，将当前用户放入 `AuthContext`。
- 业务接口通过 `AuthContext` 判断当前用户身份，并在管理员接口中进一步校验 `isAdmin()`。

### 4.4 相关数据

- `user`

---

## 5. 商品浏览与分类检索

### 5.1 模块功能

- 查看市场商品列表
- 根据关键字检索商品
- 根据分类筛选商品
- 查看商品详情
- 获取平台分类列表

### 5.2 前端实现

相关文件：

- `frontend/src/views/MarketPage.vue`
- `frontend/src/views/GoodsDetailPage.vue`
- `frontend/src/components/GoodsCard.vue`
- `frontend/src/api/modules.js`

实现方式：

- 市场页通过 `catalogApi.list(params)` 获取商品列表。
- 详情页通过 `catalogApi.detail(id)` 获取指定商品详情。
- 分类选项通过 `catalogApi.categories()` 获取。
- 商品卡片组件负责统一展示商品图片、价格、状态和操作入口。

### 5.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/CatalogController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/CatalogServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/dao/GoodsDao.java`
- `backend/src/main/java/com/useditemmarket/dao/SalesDao.java`
- `backend/src/main/java/com/useditemmarket/repository/CategoryRepository.java`

实现方式：

- 商品列表接口查询当前可展示商品，再按条件做搜索、分类过滤、排序与分页处理。
- 商品详情接口按商品 ID 组装 `GoodsVo`，补充卖家信息、分类和商品状态。
- 分类接口从 `goods_category` 表读取可用分类，供用户端筛选和发布商品时选择。
- 只有处于上架状态、库存大于 0 的商品才会出现在公开市场列表中。

### 5.4 相关数据

- `marketgoods`
- `salegoods`
- `goods_category`

---

## 6. 商品发布、编辑、下架与永久删除

### 6.1 模块功能

- 卖家查看自己发布的商品
- 发布新商品
- 编辑商品信息
- 下架商品
- 永久删除非在售商品

### 6.2 前端实现

相关文件：

- `frontend/src/views/SellerGoodsPage.vue`
- `frontend/src/views/PublishGoodsPage.vue`
- `frontend/src/views/EditGoodsPage.vue`
- `frontend/src/api/modules.js`

实现方式：

- 我的商品页通过 `sellerApi.list()` 获取当前用户发布的商品。
- 发布商品通过 `sellerApi.create(payload)` 提交商品表单。
- 编辑商品通过 `sellerApi.update(id, payload)` 更新商品。
- 下架商品通过 `sellerApi.remove(id)` 实现。
- 永久删除通过 `sellerApi.permanentDelete(id)` 实现。

### 6.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/SellerGoodsController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/SellerGoodsServiceImpl.java`

实现方式：

- 发布商品前会校验当前用户是否为普通用户、是否完成校园认证、商品信息是否完整。
- 新商品写入 `marketgoods` 时，状态默认设置为 `PENDING_REVIEW`，表示需要管理员审核后才能展示。
- 同时写入 `salegoods` 建立卖家和商品的关联关系。
- 编辑商品时会重新校验归属权，并根据库存和原状态重新设置商品状态：
  - 有库存时通常回到 `PENDING_REVIEW`；
  - 库存为 0 时进入 `OFF_SHELF`；
  - 已驳回或已封禁商品保持对应状态。
- 下架商品本质上不是删除，而是把库存改为 `0`，状态改为 `OFF_SHELF`。
- 永久删除会额外清理关联数据，包括购物车、收藏、订单关联、聊天中引用的商品信息以及图片文件。

### 6.4 相关数据

- `marketgoods`
- `salegoods`
- `shoppingcart`
- `favorite_goods`
- `traderecord`
- `chat_message`

---

## 7. 商品图片上传

### 7.1 模块功能

- 发布商品时上传图片
- 编辑商品时更换图片
- 返回图片访问路径供前端展示

### 7.2 前端实现

相关文件：

- `frontend/src/views/PublishGoodsPage.vue`
- `frontend/src/views/EditGoodsPage.vue`
- `frontend/src/api/modules.js`

实现方式：

- 前端将图片文件封装成 `FormData`，字段名为 `imageFile`。
- 调用 `sellerApi.uploadImage(file)` 上传。
- 接口返回图片路径后，前端再将该路径随商品信息一起提交。

### 7.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/SellerGoodsController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/SellerGoodsServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/service/support/FileStorageService.java`
- `backend/src/main/resources/spring-mvc.xml`

实现方式：

- 后端先校验当前用户身份，再调用 `FileStorageService` 保存图片。
- 文件服务会检查：
  - 文件不能为空；
  - 文件大小是否超限；
  - 文件扩展名是否在允许范围内。
- 图片按用户目录进行保存，并生成访问路径。
- `spring-mvc.xml` 中对 `/img/**` 做静态资源映射，使前端可以直接访问图片。

### 7.4 相关数据

- 图片文件保存在本地目录
- 商品表中保存图片路径字段

---

## 8. 购物车

### 8.1 模块功能

- 查看购物车商品
- 加入购物车
- 修改购物车数量
- 删除购物车商品

### 8.2 前端实现

相关文件：

- `frontend/src/views/CartPage.vue`
- `frontend/src/api/modules.js`

实现方式：

- 购物车页通过 `cartApi.list()` 查询当前用户购物车。
- 加购使用 `cartApi.add(payload)`。
- 修改数量使用 `cartApi.update(id, payload)`。
- 删除条目使用 `cartApi.remove(id)`。

### 8.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/CartController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/CartServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/dao/CarDao.java`

实现方式：

- 加入购物车时会校验用户身份、商品是否存在、商品是否可购买、是否为本人商品以及数量是否超过库存。
- 购物车列表查询时会关联商品信息，返回前端可直接展示的数据结构。
- 在购物车中修改数量时，会再次检查库存约束。
- 从购物车下单成功后，系统会自动删除对应购物车条目。

### 8.4 相关数据

- `shoppingcart`
- `marketgoods`

---

## 9. 订单交易与状态流转

### 9.1 模块功能

- 立即购买
- 从购物车结算下单
- 查看购买记录
- 查看销售记录
- 卖家发货
- 买家确认收货
- 删除订单

### 9.2 前端实现

相关文件：

- `frontend/src/views/GoodsDetailPage.vue`
- `frontend/src/views/CartPage.vue`
- `frontend/src/views/PurchasesPage.vue`
- `frontend/src/views/SalesPage.vue`
- `frontend/src/api/modules.js`

实现方式：

- 下单通过 `orderApi.create(payload)` 发起。
- 买家订单列表通过 `orderApi.purchases(status)` 查询。
- 卖家订单列表通过 `orderApi.sales(status)` 查询。
- 卖家发货调用 `orderApi.ship(pid)`。
- 买家确认收货调用 `orderApi.receive(pid)`。
- 删除订单调用 `orderApi.remove(pid)`。

### 9.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/OrderController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/OrderServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/dao/RecordDao.java`
- `backend/src/main/java/com/useditemmarket/dao/SRecordDao.java`
- `backend/src/main/java/com/useditemmarket/repository/AddressRepository.java`

实现方式：

- 下单时会校验：
  - 当前用户是否为普通用户；
  - 商品是否存在且可下单；
  - 不能购买自己的商品；
  - 不能购买管理员商品；
  - 购买数量不能超过库存。
- 下单成功后会扣减商品库存；如果库存减到 0，商品状态自动变为 `OFF_SHELF`。
- 系统会生成订单记录，并根据交付方式设置订单初始状态：
  - 校园配送：`PENDING_SHIPMENT`
  - 买家自提：`PENDING_PICKUP`
- 如果传入地址 ID，会保存地址快照到订单中；这样后续用户修改地址不会影响历史订单。
- 卖家发货后，配送订单状态会进入 `PENDING_RECEIPT`。
- 买家确认收货后，订单状态变为 `COMPLETED`。
- 删除订单时，如果订单仍处于未完成但可撤销阶段，系统会尝试回补商品库存。

### 9.4 状态流转

常见订单状态流转如下：

1. `PENDING_SHIPMENT` -> `PENDING_RECEIPT` -> `COMPLETED`
2. `PENDING_PICKUP` -> `COMPLETED`

### 9.5 相关数据

- `traderecord`
- `marketgoods`
- `shoppingcart`
- `user_address`

---

## 10. 收货地址管理

### 10.1 模块功能

- 地址列表查询
- 新增地址
- 编辑地址
- 删除地址
- 设置默认地址

### 10.2 前端实现

相关文件：

- `frontend/src/views/ProfilePage.vue`
- `frontend/src/api/modules.js`

实现方式：

- 地址管理通过 `addressApi.list/create/update/remove` 完成。
- 用户在个人中心维护自己的地址信息。
- 下单时可选择地址，后端再生成地址快照写入订单。

### 10.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/AddressController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/AddressServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/repository/AddressRepository.java`

实现方式：

- 地址增删改查全部绑定当前登录用户。
- 设置默认地址时，会先清空当前用户其他地址的默认标记，再写入新的默认地址。
- 下单阶段通过地址仓储读取地址快照，而不是直接依赖地址主表实时数据。

### 10.4 相关数据

- `user_address`

---

## 11. 收藏夹

### 11.1 模块功能

- 收藏商品
- 取消收藏
- 查看我的收藏列表

### 11.2 前端实现

相关文件：

- `frontend/src/views/FavoritesPage.vue`
- `frontend/src/views/GoodsDetailPage.vue`
- `frontend/src/components/GoodsCard.vue`
- `frontend/src/api/modules.js`

实现方式：

- 收藏列表通过 `favoriteApi.list()` 获取。
- 收藏商品通过 `favoriteApi.add(gid)`。
- 取消收藏通过 `favoriteApi.remove(gid)`。
- 商品详情页和商品卡片可作为收藏操作入口。

### 11.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/FavoriteController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/FavoriteServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/repository/FavoriteRepository.java`

实现方式：

- 仅普通用户可收藏商品。
- 收藏前会校验商品存在且可见，并避免重复收藏。
- 收藏列表查询时会过滤掉当前不可展示或无效的商品。

### 11.4 相关数据

- `favorite_goods`
- `marketgoods`

---

## 12. 私信聊天

### 12.1 模块功能

- 查看会话列表
- 查看与某个对象的消息记录
- 发送消息
- 删除会话
- 支持围绕商品或订单进行聊天

### 12.2 前端实现

相关文件：

- `frontend/src/views/MessagesPage.vue`
- `frontend/src/api/modules.js`

实现方式：

- 会话列表通过 `chatApi.conversations()` 获取。
- 某个会话消息通过 `chatApi.messages(conversationKey)` 获取。
- 发送消息通过 `chatApi.send(payload)`。
- 删除会话通过 `chatApi.removeConversation(conversationKey)`。

### 12.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/ChatController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/ChatServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/repository/ChatRepository.java`

实现方式：

- 发送消息前会校验发送者、接收者是否为普通用户，消息内容是否为空，以及是否给自己发送消息。
- 如果消息关联订单，系统会校验当前用户是否属于该订单的买卖双方，并自动绑定订单和商品信息。
- 如果消息关联商品，系统会校验商品是否存在；非订单会话下通常要求商品仍可咨询。
- 查看消息记录时，会按会话读取双方消息。
- 删除会话时，系统会先校验会话是否存在，再清理该用户可见的会话数据。

### 12.4 相关数据

- `chat_message`
- `traderecord`
- `marketgoods`

---

## 13. 求购信息

### 13.1 模块功能

- 查看开放求购信息
- 发布求购信息
- 查看我的求购
- 删除或关闭自己的求购
- 为求购匹配平台中的相关商品

### 13.2 前端实现

相关文件：

- `frontend/src/views/WantedPage.vue`
- `frontend/src/api/modules.js`

实现方式：

- 求购广场通过 `wantedApi.list()` 获取公开求购信息。
- 当前用户的求购通过 `wantedApi.mine()` 获取。
- 发布求购通过 `wantedApi.create(payload)`。
- 删除或关闭求购通过 `wantedApi.remove(id)`。

### 13.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/WantedController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/WantedServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/repository/WantedRepository.java`

实现方式：

- 公开求购列表允许匿名访问。
- 发布求购时会校验当前用户身份和内容完整性。
- 我的求购只返回当前登录用户自己发布的数据。
- 仓储层在查询求购信息时，会根据分类和关键字去商品表中匹配可用商品，返回给前端做关联展示。

### 13.4 相关数据

- `wanted_post`
- `marketgoods`

---

## 14. 个人中心

### 14.1 模块功能

- 查看个人资料
- 修改个人资料
- 修改密码
- 维护地址信息

### 14.2 前端实现

相关文件：

- `frontend/src/views/ProfilePage.vue`
- `frontend/src/api/modules.js`

实现方式：

- 资料查询通过 `profileApi.get()`。
- 更新资料通过 `profileApi.update(payload)`。
- 修改密码通过 `profileApi.updatePassword(payload)`。
- 地址相关能力也主要挂在个人中心页面中完成。

### 14.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/ProfileController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/ProfileServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/dao/UserDao.java`

实现方式：

- 资料更新会校验当前用户身份以及字段合法性。
- 密码修改通常要求旧密码校验和新密码格式校验，再更新数据库中的加密密码。
- 用户头像、昵称、邮箱、手机号、个人简介等资料都由该模块统一维护。

### 14.4 相关数据

- `user`
- `user_address`

---

## 15. 管理员后台

### 15.1 模块功能

- 管理员仪表盘
- 用户管理
- 订单总览
- 商品审核与商品管理
- 分类管理

### 15.2 前端实现

相关文件：

- `frontend/src/views/AdminDashboardPage.vue`
- `frontend/src/views/AdminUsersPage.vue`
- `frontend/src/views/AdminOrdersPage.vue`
- `frontend/src/views/AdminGoodsPage.vue`
- `frontend/src/views/AdminCategoriesPage.vue`
- `frontend/src/api/modules.js`
- `frontend/src/router/index.js`

实现方式：

- 所有后台页面都要求登录且要求管理员权限。
- 普通用户无法进入后台路由。
- 后台页面按模块分别调用 `adminApi` 下的接口完成数据展示和操作。

### 15.3 后端实现

相关文件：

- `backend/src/main/java/com/useditemmarket/controller/api/AdminApiController.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/AdminUserServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/service/api/impl/AdminOpsServiceImpl.java`
- `backend/src/main/java/com/useditemmarket/repository/AdminRepository.java`
- `backend/src/main/java/com/useditemmarket/repository/CategoryRepository.java`

实现方式：

- 后台接口统一先做 `requireAdmin()` 权限校验。
- 用户管理支持：
  - 查看普通用户列表；
  - 修改用户资料；
  - 重置密码；
  - 停用与启用账号。
- 用户停用后，系统会同步处理其商品状态，避免违规账号继续售卖。
- 商品审核支持：
  - 查看待审核商品；
  - 查看全部商品；
  - 审核通过；
  - 驳回；
  - 封禁商品。
- 分类管理支持新增和更新分类。
- 仪表盘负责聚合统计，例如商品量、订单量、用户情况、求购情况等。

### 15.4 相关数据

- `user`
- `marketgoods`
- `salegoods`
- `traderecord`
- `goods_category`
- `wanted_post`

---

## 16. 关键实现特点总结

从当前代码实现来看，这个平台有几个比较明确的业务特点：

1. 普通用户端、卖家端、管理员端已经分层清楚，前端路由和后端权限控制是对应的。
2. 商品发布采用“先提交、后审核”的流程，平台具备基本审核机制。
3. 订单、聊天、收藏、求购、地址等围绕二手交易的辅助模块已经较完整。
4. 订单处理会联动库存变化，删除未完成订单时还会尝试恢复库存。
5. 聊天模块不仅支持普通商品咨询，还支持和订单上下文绑定。
6. 永久删除商品时会同步清理关联购物车、收藏、订单与聊天引用，说明当前项目已经考虑到一定的数据一致性。

## 17. 涉及的主要代码目录

为方便后续查阅，下面给出当前功能实现最核心的几个目录：

- 前端页面：`frontend/src/views`
- 前端接口封装：`frontend/src/api/modules.js`
- 前端路由守卫：`frontend/src/router/index.js`
- 后端控制器：`backend/src/main/java/com/useditemmarket/controller/api`
- 后端业务实现：`backend/src/main/java/com/useditemmarket/service/api/impl`
- MyBatis DAO：`backend/src/main/java/com/useditemmarket/dao`
- JdbcTemplate Repository：`backend/src/main/java/com/useditemmarket/repository`
- 请求对象 DTO：`backend/src/main/java/com/useditemmarket/dto`
- 返回对象 VO：`backend/src/main/java/com/useditemmarket/vo`

## 18. 结论

根据当前项目代码，这个二手交易平台已经实现了一个较完整的校园二手交易业务闭环：

- 用户可以注册、登录、浏览商品、收藏商品、加入购物车、下单购买。
- 卖家可以发布商品、编辑商品、上传图片、管理自己的出售记录。
- 买卖双方可以通过私信沟通，也可以围绕订单继续交流。
- 用户可以维护地址、查看订单、发布求购信息。
- 管理员可以管理用户、审核商品、管理分类、查看订单和平台统计数据。

整体上，当前项目已经不只是一个简单的商品展示系统，而是具备基本交易流程、审核流程和后台管理能力的二手交易平台。
