# 二手交易平台后端功能实现说明

## 1. 文档目的

本文档面向当前项目后端代码，重点说明以下内容：

1. 后端整体采用了什么分层结构。
2. 后端每一层分别承担什么职责。
3. 每个功能在后端是如何由各层协同完成的。
4. 系统中的鉴权、异常处理、事务、数据库访问等横向机制是如何支撑业务功能的。

本文档对应的后端代码主目录为：

- `backend/src/main/java/com/useditemmarket/controller`
- `backend/src/main/java/com/useditemmarket/controller/api`
- `backend/src/main/java/com/useditemmarket/service/api`
- `backend/src/main/java/com/useditemmarket/service/api/impl`
- `backend/src/main/java/com/useditemmarket/dao`
- `backend/src/main/java/com/useditemmarket/repository`
- `backend/src/main/java/com/useditemmarket/dto`
- `backend/src/main/java/com/useditemmarket/po`
- `backend/src/main/java/com/useditemmarket/vo`
- `backend/src/main/java/com/useditemmarket/security`
- `backend/src/main/java/com/useditemmarket/config`
- `backend/src/main/java/com/useditemmarket/exception`

---

## 2. 后端总体架构

当前项目后端基于 `Spring MVC + Spring Service + MyBatis + JdbcTemplate` 实现，整体调用链如下：

1. 前端向 `/api/**` 发起 HTTP 请求。
2. Spring MVC 将请求分发到对应 `Controller`。
3. `Controller` 负责接收参数、读取登录用户、调用 `Service`。
4. `Service` 负责业务规则校验、权限判断、状态流转、事务控制。
5. `DAO` 或 `Repository` 负责与数据库交互。
6. `Service` 将数据库对象转换为 `VO` 或业务返回结果。
7. `Controller` 使用 `ApiResponse` 统一返回给前端。

可以把后端理解成下面这条业务链：

`请求 -> 鉴权 -> Controller -> Service -> DAO/Repository -> 数据库 -> VO/Response -> 前端`

---

## 3. 后端分层说明

## 3.1 Controller 层

### 职责

- 定义 REST 接口路径。
- 接收前端传入的 `DTO` 参数。
- 从 `AuthContext` 读取当前登录用户。
- 调用对应 `Service`。
- 将返回值包装成 `ApiResponse`。

### 特点

- `Controller` 层本身逻辑较轻。
- 不直接编写复杂业务规则。
- 不直接访问数据库。
- 主要扮演“HTTP 请求入口”和“业务调用分发器”的角色。

### 典型实现

例如登录接口：

- `AuthController.login(@RequestBody LoginRequest request)`
- Controller 只负责接收登录参数，并调用 `authService.login(request)`。

例如卖家商品接口：

- `SellerGoodsController.create(...)`
- 先从 `AuthContext.get().getUid()` 取出当前用户，再调用 `sellerGoodsService.create(uid, request)`。

这说明 Controller 的核心职责是“把 Web 请求变成 Service 方法调用”。

---

## 3.2 Service 层

### 职责

- 实现业务功能主流程。
- 校验参数合法性。
- 校验当前用户身份和权限。
- 查询多个数据源并组合逻辑。
- 控制商品、订单、求购等业务状态的流转。
- 在一个事务中完成多表更新。

### 特点

- 绝大部分业务逻辑都集中在 `service/api/impl`。
- Service 是系统真正的业务核心层。
- 大多数 Service 类都标注了 `@Transactional`，说明它们不仅做逻辑编排，也承担事务边界管理。

### 公共业务基类

项目中有一个非常关键的基类：

- `backend/src/main/java/com/useditemmarket/service/api/impl/AbstractApiSupport.java`

它为多个 Service 提供了统一的基础能力，例如：

- `requireUser(uid)`：校验用户是否存在。
- `requireNormalUser(uid)`：校验当前用户是普通用户而不是管理员。
- `requireGoods(gid)`：校验商品是否存在。
- `requireOwner(uid, gid)`：校验当前用户是否是商品所有者。
- `requireCampusVerified(user)`：校验是否完成校园认证。
- `requireGoodsActive(goods, message)`：校验商品是否处于可购买/可展示状态。
- `nextGid()`、`nextPid()`：生成商品 ID 和订单 ID。

这个基类的作用是把重复的基础业务校验抽出来，使不同功能模块在实现上保持一致。

---

## 3.3 DAO 层

### 职责

- 使用 MyBatis Mapper 操作数据库。
- 主要承接原有核心表的增删改查。
- 负责面向 `PO` 对象进行数据读写。

### 特点

- `dao` 接口与 `resources/mapping/*.xml` 配合使用。
- 这部分更偏传统的 MyBatis 写法。
- 主要服务于用户、商品、卖家关系、订单、购物车等核心业务表。

### 典型表

- `UserDao`
- `GoodsDao`
- `SalesDao`
- `CarDao`
- `RecordDao`
- `SRecordDao`
- `UtilsDao`

### 典型实现方式

例如 `GoodsDao.xml` 中：

- `InsertGoods` 插入商品
- `ChangeInfo` 动态更新商品字段
- `SelectGoods` 查询单个商品
- `SelectAllActiveGoods` 查询可上架商品

这里的特点是：

- DAO 不解释“为什么改”，只负责“怎么改”。
- 业务含义由 Service 决定，SQL 读写由 DAO 执行。

---

## 3.4 Repository 层

### 职责

- 使用 `JdbcTemplate` 直接写 SQL。
- 主要承接新增功能、聚合查询和返回 `VO` 的场景。
- 用于实现更灵活的查询与后台统计。

### 特点

- 这是当前项目相对更“面向结果”的数据访问层。
- 许多 Repository 直接返回 `VO`，减少中间转换步骤。
- 适合收藏、地址、聊天、分类、后台统计、求购等后加功能。

### 典型类

- `AddressRepository`
- `FavoriteRepository`
- `ChatRepository`
- `CategoryRepository`
- `WantedRepository`
- `AdminRepository`

### 公共映射支持

- `JdbcVoMapperSupport.java`

这个类封装了多个 `RowMapper`，负责把 SQL 查询结果映射成：

- `GoodsVo`
- `AddressVo`
- `CategoryVo`
- `ChatMessageVo`
- `WantedVo`

因此 Repository 层不仅是数据访问层，也兼顾了一部分“结果对象装配”的职责。

---

## 3.5 DTO、PO、VO、Model 层

### DTO

职责：接收前端提交的数据。

典型类：

- `LoginRequest`
- `RegisterRequest`
- `GoodsCreateRequest`
- `OrderCreateRequest`
- `ChatSendRequest`

特点：

- DTO 代表“前端请求长什么样”。
- Controller 通过 `@RequestBody` 接收 DTO。

### PO

职责：映射数据库表字段。

典型类：

- `MarketUser`
- `MarketGoods`
- `TradeRecord`
- `ShoppingCart`

特点：

- PO 更接近数据库存储结构。
- DAO 层主要围绕 PO 操作。

### VO

职责：返回给前端展示的数据结构。

典型类：

- `UserVo`
- `GoodsVo`
- `OrderVo`
- `WantedVo`
- `DashboardVo`

特点：

- VO 会隐藏部分数据库细节。
- VO 会补充页面真正需要的展示信息，例如卖家名称、审核备注、地址快照等。

### Model

职责：保存枚举、上下文和基础模型。

典型类：

- `GoodsStatus`
- `OrderStatus`
- `DeliveryMode`
- `UserStatus`
- `AuthUser`
- `AuthContext`

特点：

- 这些类帮助业务层表达状态机、身份上下文和统一枚举语义。

---

## 4. 后端横切机制

## 4.1 鉴权机制

相关文件：

- `JwtTokenService.java`
- `AuthInterceptor.java`
- `AuthContext.java`
- `AuthUser.java`
- `spring-mvc.xml`

### 实现方式

1. 登录成功后，`AuthServiceImpl` 调用 `JwtTokenService.generateToken(uid, admin)` 生成 JWT。
2. 前端后续请求在请求头中携带 `Authorization: Bearer <token>`。
3. `spring-mvc.xml` 为 `/api/**` 注册了 `AuthInterceptor`。
4. `AuthInterceptor.preHandle()` 解析 Token：
   - 成功则把用户信息写入 `AuthContext`；
   - 失败则直接返回 `401`。
5. Controller 和 Service 通过 `AuthContext.get()` 获取当前登录用户。
6. 请求完成后 `afterCompletion()` 清理线程上下文。

### 功能意义

- 统一保护需要登录的接口。
- 把“当前登录用户是谁”从每个接口中抽离成公共能力。
- 为普通用户/管理员的权限隔离提供基础。

---

## 4.2 异常处理机制

相关文件：

- `BaseException.java`
- `GlobalExceptionHandler.java`
- `ApiResponse.java`

### 实现方式

- 业务层发现问题时，直接抛出 `BaseException`，并附带状态码和错误信息。
- `GlobalExceptionHandler` 统一捕获异常：
  - `BaseException` 返回对应业务错误码；
  - 上传异常返回 400；
  - 其他未知异常返回 500。
- 所有异常最终都转换成统一的 `ApiResponse.fail(code, message)` 返回给前端。

### 功能意义

- 保证所有接口错误格式统一。
- 让 Service 可以专注于“抛出什么业务错误”，而不是处理 HTTP 细节。
- 让前端能稳定地按 `code/message` 解析失败结果。

---

## 4.3 事务机制

相关文件：

- `spring-mybatis.xml`
- 各 `ServiceImpl` 上的 `@Transactional`

### 实现方式

- Spring 配置了 `DataSourceTransactionManager`。
- 大多数 Service 都标注了 `@Transactional`。

### 功能意义

当一个功能涉及多步数据库操作时，事务保证这些操作要么全部成功，要么一起回滚。  
例如：

- 发布商品时同时写 `marketgoods` 和 `salegoods`
- 下单时既要扣库存，又要写订单，还可能删购物车
- 永久删除商品时要清理多张关联表

这些都依赖 Service 层事务来维持数据一致性。

---

## 4.4 数据库初始化与迁移机制

相关文件：

- `DatabaseMigrationService.java`

### 实现方式

- Spring 容器启动后通过 `@PostConstruct` 执行数据库迁移逻辑。
- 自动补充旧表缺失字段。
- 自动创建新功能依赖的新表。
- 自动初始化默认商品分类。

### 功能意义

- 降低本地演示和课程项目部署门槛。
- 让地址、收藏、聊天、分类、求购等后加功能可以直接运行。

---

## 5. 各功能的后端实现与层间协作

下面按功能模块说明每个功能是如何在后端各层之间协同完成的。

## 5.1 用户注册、登录与当前用户

### 相关层

- Controller：`AuthController`
- DTO：`LoginRequest`、`RegisterRequest`
- Service：`AuthServiceImpl`
- DAO：`UserDao`
- Security：`JwtTokenService`
- VO：`LoginResponse`、`UserVo`

### 注册流程

1. 前端将注册表单提交给 `POST /api/auth/register`。
2. `AuthController.register()` 接收 `RegisterRequest`。
3. Controller 调用 `authService.register(request)`。
4. `AuthServiceImpl` 执行业务校验：
   - 学号、用户名、密码、邮箱、手机号不能为空；
   - 两次密码必须一致；
   - 邮箱和手机号格式要合法；
   - 学号是否已注册。
5. Service 使用 `Md5Util` 处理密码。
6. Service 构造 `MarketUser`，调用 `userDao.InsertUser(user)` 写入数据库。
7. Service 再调用 `requireUser(uid)` 读取最新用户数据，并转换成 `UserVo`。
8. Controller 将结果包装为 `ApiResponse.success("注册成功", userVo)` 返回。

### 登录流程

1. `AuthController.login()` 接收 `LoginRequest`。
2. `AuthServiceImpl.login()` 校验账号和密码。
3. Service 调用 `userDao.IsTrue(studentNo, md5Password)` 验证账号密码。
4. Service 根据 UID 判断是否是管理员，并和前端传入角色比对。
5. Service 读取完整用户，校验账号是否被停用。
6. Service 调用 `jwtTokenService.generateToken(uid, admin)` 生成令牌。
7. Service 将 `token + UserVo` 封装为 `LoginResponse` 返回。

### 层间协作特点

- Controller 只负责接收请求和返回响应。
- Service 负责所有注册/登录业务规则。
- DAO 只负责用户数据查询与写入。
- JWT 生成与解析从业务逻辑中拆开，形成独立安全层。

---

## 5.2 商品浏览、详情与分类

### 相关层

- Controller：`CatalogController`
- DTO：`GoodsQuery`
- Service：`CatalogServiceImpl`
- DAO：`GoodsDao`、`SalesDao`、`UserDao`
- Repository：`CategoryRepository`
- VO：`GoodsVo`、`CategoryVo`、`PageResponse`

### 商品列表流程

1. 前端请求 `GET /api/catalog/goods`。
2. `CatalogController` 接收查询参数并构造 `GoodsQuery`。
3. `CatalogServiceImpl.queryGoods()` 调用 `goodsDao.SelectAllActiveGoods()` 先获取基础商品集。
4. Service 再做二次业务过滤：
   - 商品状态必须是 `ACTIVE`
   - 库存必须大于 0
   - 卖家不能是管理员
5. Service 根据关键字、分类、排序方式继续处理。
6. Service 通过 `salesDao.WhoseGoods(gid)` 和 `userDao.SelectUser(uid)` 补充卖家信息。
7. Service 将 `MarketGoods` 转为 `GoodsVo`。
8. 最终封装为 `PageResponse<GoodsVo>` 返回。

### 商品详情流程

1. `CatalogController` 接收商品 ID。
2. `CatalogServiceImpl.getGoodsDetail(gid)` 调用 `requireGoods(gid)`。
3. Service 通过 `salesDao.WhoseGoods(gid)` 找卖家。
4. 如果卖家为空或是管理员，则视为商品不可公开访问。
5. Service 组装 `GoodsVo` 返回。

### 分类列表流程

1. `CatalogController.categories()` 调用 `catalogService.listCategories()`。
2. `CatalogServiceImpl` 调用 `CategoryRepository.listEnabledCategories()`。
3. Repository 通过 `JdbcTemplate` 查询分类表，并映射成 `CategoryVo`。

### 层间协作特点

- DAO 提供商品原始数据。
- Service 负责“公开市场可见性”的业务定义。
- Repository 负责分类这类更适合 VO 查询的功能。
- VO 在这一模块中承担了“数据库结果到页面展示结构”的转换角色。

---

## 5.3 卖家商品管理

### 相关层

- Controller：`SellerGoodsController`
- DTO：`GoodsCreateRequest`、`GoodsUpdateRequest`
- Service：`SellerGoodsServiceImpl`
- DAO：`GoodsDao`、`SalesDao`、`CarDao`、`RecordDao`
- Repository：`FavoriteRepository`、`ChatRepository`
- Support：`FileStorageService`
- VO：`GoodsVo`

### 发布商品流程

1. 前端请求 `POST /api/seller/goods`。
2. `SellerGoodsController.create()` 从 `AuthContext` 读取当前用户 UID。
3. Controller 调用 `sellerGoodsService.create(uid, request)`。
4. `SellerGoodsServiceImpl` 执行以下校验：
   - 当前用户必须是普通用户；
   - 必须完成校园认证；
   - 商品名称、分类、价格、库存、交付方式不能为空或非法。
5. Service 构造 `MarketGoods`，并通过 `nextGid()` 生成商品编号。
6. Service 将商品状态设置为 `PENDING_REVIEW`。
7. Service 调用：
   - `goodsDao.InsertGoods(goods)` 写商品主体；
   - `salesDao.InsertGoods(uid, goods)` 写卖家关联。
8. Service 返回 `GoodsVo` 给 Controller。

### 编辑商品流程

1. Controller 接收商品 ID 和更新参数。
2. Service 先执行 `requireOwner(uid, gid)`，确认是商品本人操作。
3. Service 按字段逐项更新 `MarketGoods`。
4. Service 根据库存和当前状态重新决定商品状态：
   - 有库存一般改回 `PENDING_REVIEW`
   - 库存为 0 时改为 `OFF_SHELF`
   - `REJECTED` 和 `BANNED` 特殊保留
5. Service 调用 `goodsDao.ChangeInfo(goods)` 更新数据库。

### 下架流程

1. Service 校验归属权。
2. 将库存设为 `0`，状态设为 `OFF_SHELF`。
3. 调用 `goodsDao.ChangeInfo(goods)`。

### 永久删除流程

1. Service 校验当前用户和商品归属。
2. 校验当前商品状态是否允许删除。
3. Service 触发级联清理：
   - `fileStorageService.deleteImage(goods.getImage())`
   - `carDao.DeleteByGid(gid)`
   - `favoriteRepository.deleteByGid(gid)`
   - `recordDao.DeleteByGid(gid)`
   - `chatRepository.clearGoodsId(gid)`
   - `salesDao.DeleteGoods(uid, goods)`
   - `goodsDao.DeleteGoods(goods)`

### 层间协作特点

- 这个模块最能体现 Service 的“业务编排中心”角色。
- Service 同时调度 MyBatis DAO、JdbcTemplate Repository 和文件存储服务。
- Controller 不参与任何状态机判断。
- 数据一致性依赖事务控制与 Service 的统一编排。

---

## 5.4 图片上传

### 相关层

- Controller：`SellerGoodsController`
- Service：`SellerGoodsServiceImpl`
- Support：`FileStorageService`
- Config：`spring-mvc.xml`、`ImageResourceConfig`

### 处理流程

1. 前端通过 `multipart/form-data` 提交 `imageFile`。
2. `SellerGoodsController.uploadImage()` 接收 `MultipartFile`。
3. Controller 调用 `sellerGoodsService.uploadImage(file, uid)`。
4. Service 校验当前用户身份后，将文件交给 `FileStorageService.saveImage()`。
5. 文件服务检查文件大小、扩展名并保存文件。
6. 返回图片访问路径。
7. 前端再把这个路径作为商品字段写入数据库。

### 层间协作特点

- 文件上传没有直接进入 DAO 层，因为它不是数据库写入问题。
- Service 负责权限校验，Support 层负责文件系统处理。
- 上传异常最终由全局异常处理器统一返回。

---

## 5.5 购物车

### 相关层

- Controller：`CartController`
- DTO：`CartAddRequest`
- Service：`CartServiceImpl`
- DAO：`CarDao`、`GoodsDao`、`SalesDao`
- VO：`CartItemVo`

### 加入购物车流程

1. `CartController` 接收商品 ID 和数量。
2. `CartServiceImpl.addItem(uid, gid, quantity)` 先校验数量。
3. Service 检查：
   - 当前用户必须是普通用户；
   - 商品存在；
   - 卖家不能是管理员；
   - 不能购买自己的商品；
   - 商品必须可购买；
   - 加购后数量不能超过库存。
4. Service 查询当前购物车中是否已存在该商品。
5. 如果不存在则调用 `carDao.InsertGoods(uid, cartGoods)`。
6. 如果存在则调用 `carDao.ChangeCart(uid, cartGoods)`。
7. 返回最新购物车列表。

### 修改与删除流程

- 修改：重新校验库存后调用 `carDao.ChangeCart(...)`
- 删除：调用 `carDao.DeleteGoods(uid, goods)`

### 层间协作特点

- DAO 负责购物车表自身的维护。
- 商品是否能加购、能加多少，由 Service 基于商品表和卖家关系来判断。
- 购物车模块并不是独立系统，它依赖商品模块和卖家关系模块共同完成校验。

---

## 5.6 订单交易

### 相关层

- Controller：`OrderController`
- DTO：`OrderCreateRequest`
- Service：`OrderServiceImpl`
- DAO：`RecordDao`、`SRecordDao`、`CarDao`、`GoodsDao`、`SalesDao`
- Repository：`AddressRepository`
- VO：`OrderVo`
- Model：`OrderStatus`、`DeliveryMode`

### 创建订单流程

1. `OrderController.create()` 接收下单参数。
2. `OrderServiceImpl.createOrder(uid, request)` 校验：
   - 请求不能为空；
   - 数量必须大于 0；
   - 当前用户必须是普通用户；
   - 商品存在；
   - 卖家存在；
   - 不能买自己的商品；
   - 不能买管理员商品；
   - 商品必须处于可下单状态；
   - 库存必须足够。
3. Service 计算扣减后的库存。
4. Service 调用 `goodsDao.ChangeInfo(updatedGoods)` 更新商品库存和状态。
5. Service 构造 `TradeRecord`：
   - 生成 `PID`
   - 记录买家、卖家、商品、数量、价格
   - 根据交付方式设置初始订单状态
6. Service 调用 `AddressRepository` 生成地址快照。
7. Service 调用 `recordDao.InsertTradeRecord(tradeRecord)` 写订单。
8. 如果来自购物车，再调用 `carDao.DeleteGoods(...)` 移除购物车项。
9. 返回 `OrderVo`。

### 发货流程

1. Service 检查当前用户是否是卖家。
2. 校验交付方式是否为校园配送。
3. 校验当前订单状态是否允许发货。
4. 调用 `recordDao.UpdateStatus(pid, PENDING_RECEIPT, true, false)`。

### 确认收货流程

1. Service 检查当前用户是否是买家。
2. 校验订单当前状态是否可完成。
3. 调用 `recordDao.UpdateStatus(pid, COMPLETED, true, true)`。

### 删除订单流程

1. Service 确认当前用户属于该订单。
2. 校验该状态是否允许删除。
3. 如果订单还处于可回滚阶段，则调用 `restoreStockIfPossible(tradeRecord)`。
4. 最后调用 `recordDao.DeleteByPid(pid)` 删除订单记录。

### 层间协作特点

- 订单模块是最典型的“多表联动”功能。
- 商品库存、订单记录、地址快照、购物车数据都在一个 Service 中协同。
- Service 层负责订单状态机。
- DAO 负责订单和库存的底层写入。
- Repository 负责地址快照这类更灵活的查询逻辑。

---

## 5.7 收货地址

### 相关层

- Controller：`AddressController`
- DTO：`AddressSaveRequest`
- Service：`AddressServiceImpl`
- Repository：`AddressRepository`
- VO：`AddressVo`

### 处理流程

1. Controller 接收地址请求并读取当前 UID。
2. Service 校验当前用户身份与参数。
3. Service 调用 `AddressRepository` 执行新增、修改、删除、查询。
4. Repository 使用 `JdbcTemplate` 对 `user_address` 表进行读写。
5. 如果设置默认地址，Repository 会先清空用户其他地址的默认标记。
6. 下单时，订单模块再通过 `AddressRepository` 读取地址快照。

### 层间协作特点

- 地址模块整体较轻，Repository 直接承担了主要数据库实现。
- Service 保持统一的用户身份边界。
- 地址模块既服务个人中心，也服务订单模块，是一个复用型基础模块。

---

## 5.8 收藏功能

### 相关层

- Controller：`FavoriteController`
- Service：`FavoriteServiceImpl`
- Repository：`FavoriteRepository`
- DAO：`GoodsDao`、`SalesDao`
- VO：`GoodsVo`

### 处理流程

1. Controller 接收商品 ID。
2. `FavoriteServiceImpl.add(uid, gid)` 校验：
   - 当前用户必须是普通用户；
   - 商品存在；
   - 卖家不能是管理员；
   - 商品必须仍可展示/收藏。
3. Service 调用 `favoriteRepository.addFavorite(uid, gid)`。
4. 收藏列表查询时，由 Repository 关联商品表、卖家表等数据后返回 `GoodsVo` 列表。

### 层间协作特点

- Service 决定能否收藏。
- Repository 决定如何高效查出收藏结果并组装成前端可直接展示的 VO。
- 收藏模块依赖商品状态逻辑，不能脱离商品业务独立存在。

---

## 5.9 私信聊天

### 相关层

- Controller：`ChatController`
- DTO：`ChatSendRequest`
- Service：`ChatServiceImpl`
- Repository：`ChatRepository`
- DAO：`RecordDao`
- VO：`ChatConversationVo`、`ChatMessageVo`

### 发送消息流程

1. Controller 接收消息请求。
2. `ChatServiceImpl.send(uid, request)` 校验：
   - 当前用户必须是普通用户；
   - 接收方必须是普通用户；
   - 消息内容不能为空；
   - 不能给自己发消息。
3. 如果关联订单：
   - Service 通过 `recordDao.SelectTradeRecord(pid)` 查询订单；
   - 校验当前用户是否属于订单双方；
   - 自动绑定正确的 `OrderPID` 和 `GoodsID`。
4. 如果关联商品：
   - Service 通过 `requireGoods(gid)` 校验商品存在；
   - 非订单咨询场景下要求商品仍可咨询。
5. 最终调用 `chatRepository.sendMessage(...)` 写入消息表。

### 查看消息流程

1. Controller 接收会话标识。
2. Service 校验当前用户和会话标识。
3. Repository 查询双方消息并返回 `ChatMessageVo` 列表。

### 删除会话流程

1. Service 检查会话是否存在。
2. Repository 删除该用户对应会话数据。

### 层间协作特点

- 聊天模块与商品、订单模块发生联动。
- Order DAO 提供订单边界校验。
- Goods 校验提供商品咨询边界。
- Repository 负责真正的消息读写和会话聚合查询。

---

## 5.10 求购功能

### 相关层

- Controller：`WantedController`
- DTO：`WantedCreateRequest`
- Service：`WantedServiceImpl`
- Repository：`WantedRepository`
- VO：`WantedVo`

### 处理流程

1. Controller 接收求购请求。
2. Service 校验当前用户和输入内容。
3. Service 构造 `WantedVo` 或相关数据对象。
4. 调用 `wantedRepository.createWanted(uid, wanted)` 写入 `wanted_post`。
5. 查询公开求购时，Repository 不仅查询求购信息，还会按分类和关键字匹配商品，补充相关推荐。

### 层间协作特点

- Controller 很轻。
- Service 主要承担身份和参数校验。
- Repository 不仅做表查询，还承担了“求购与商品匹配”的业务查询能力。

---

## 5.11 个人资料与密码修改

### 相关层

- Controller：`ProfileController`
- DTO：`UserProfileUpdateRequest`、`PasswordUpdateRequest`
- Service：`ProfileServiceImpl`
- DAO：`UserDao`
- VO：`UserVo`

### 处理流程

1. Controller 接收更新请求。
2. Service 读取当前用户并校验字段合法性。
3. 修改资料时，Service 调用 `UserDao` 更新用户信息。
4. 修改密码时，Service 会校验旧密码和新密码规则，再将新密码加密后保存。
5. 最终返回 `UserVo`。

### 层间协作特点

- 资料模块主要围绕用户表展开。
- Service 承担了字段规范和密码安全处理。
- DAO 仅负责用户数据更新。

---

## 5.12 管理员后台

### 相关层

- Controller：`AdminApiController`
- DTO：`AdminGoodsReviewRequest`、`CategorySaveRequest`、`UserProfileUpdateRequest`
- Service：`AdminUserServiceImpl`、`AdminOpsServiceImpl`
- DAO：`UserDao`、`GoodsDao`、`RecordDao`、`SalesDao`
- Repository：`AdminRepository`、`CategoryRepository`
- VO：`UserVo`、`GoodsVo`、`OrderVo`、`CategoryVo`、`DashboardVo`

### 权限控制

`AdminApiController` 中通过 `requireAdmin()` 统一校验管理员身份。  
这说明后台模块在 Controller 层已经建立了第一层权限边界。

### 用户管理流程

1. Controller 接收后台用户操作请求。
2. `AdminUserServiceImpl` 执行：
   - 查询用户列表
   - 修改资料
   - 重置密码
   - 停用/启用用户
3. 停用用户时，Service 不只修改用户状态，还会联动处理该用户的商品状态。

### 商品审核流程

1. Controller 接收商品审核请求。
2. `AdminOpsServiceImpl.reviewGoods(gid, request)` 读取商品。
3. 根据 `action` 执行状态机：
   - `approve` -> `ACTIVE`
   - `reject` -> `REJECTED`
   - `ban` -> `BANNED`
4. 必要时同步修改库存和审核备注。
5. 调用 `goodsDao.ChangeInfo(goods)` 落库。
6. 返回新的 `GoodsVo`。

### 分类管理流程

1. Controller 接收分类保存请求。
2. Service 校验分类编码和名称。
3. 调用 `CategoryRepository.saveCategory(...)` 写库并返回 `CategoryVo`。

### 仪表盘流程

1. Controller 请求后台统计数据。
2. `AdminOpsServiceImpl.dashboard()` 直接调用 `AdminRepository.loadDashboard()`。
3. Repository 聚合多张表统计结果并返回 `DashboardVo`。

### 层间协作特点

- 后台模块分成“用户管理”和“运营管理”两个 Service 方向。
- Controller 负责管理员入口边界。
- Service 负责具体审核、封禁、状态迁移规则。
- Repository 更适合做后台聚合视图和统计报表。

---

## 6. 为什么项目同时存在 DAO 和 Repository

当前项目后端数据访问层采用了两种方式：

1. MyBatis DAO
2. JdbcTemplate Repository

这不是简单重复，而是功能演化的结果。

### DAO 更适合的场景

- 已有核心表
- 基于 PO 的标准增删改查
- SQL 映射相对固定

例如：

- 用户
- 商品
- 卖家关系
- 购物车
- 订单

### Repository 更适合的场景

- 新增功能模块
- 聚合查询
- 直接返回 VO
- 统计报表
- 查询逻辑灵活、跨表展示较多

例如：

- 地址
- 收藏
- 聊天
- 分类
- 求购
- 后台统计

### 实际意义

这使得项目在保留原有 MyBatis 结构的同时，可以较快扩展新功能，而不必把所有新需求都强行塞回旧 Mapper 体系中。

---

## 7. 层与层之间的协作规律总结

从当前后端代码可以总结出几个稳定的协作规律。

### 7.1 Controller 只负责入口，不负责业务决策

Controller 的边界很明确：

- 接收参数
- 取当前用户
- 调 Service
- 包装响应

这让业务逻辑集中在 Service，避免控制器膨胀。

### 7.2 Service 是真正的业务中枢

Service 统一承担：

- 权限校验
- 状态机
- 多表联动
- 事务控制
- DTO/PO/VO 之间的编排

所以要理解某个功能“真正怎么实现”，核心应先看对应 `ServiceImpl`。

### 7.3 DAO/Repository 只负责数据访问，不定义业务规则

虽然 Repository 有时会带一点结果组装能力，但整体上：

- 数据层负责“查什么、写什么”
- 业务层负责“为什么查、为什么写、什么时候写”

### 7.4 Model/VO/DTO 让层间边界更清晰

- DTO：面向请求
- PO：面向存储
- VO：面向响应
- Model：面向业务状态与上下文

这种拆分有助于减少各层之间的直接耦合。

### 7.5 横切机制统一支撑所有业务

所有业务模块都共享：

- JWT 鉴权
- 全局异常处理
- 事务管理
- 数据库迁移初始化
- 通用业务校验基类

这保证了平台虽然功能模块较多，但整体实现方式保持一致。

---

## 8. 结论

从当前项目后端实现来看，这个二手交易平台并不是简单的“接口 + 数据库”堆叠，而是已经形成了比较清晰的分层结构：

1. Controller 层统一承接 HTTP 请求。
2. Service 层集中实现业务规则与功能流程。
3. DAO/Repository 层分别负责核心表访问和新增功能/聚合查询。
4. DTO、PO、VO、Model 共同维持了各层之间的数据边界。
5. JWT、异常处理、事务、数据库迁移等横切机制为所有功能提供统一支撑。

按这个结构，每个功能都不是单层独立完成的，而是通过“Controller 接口入口 + Service 业务编排 + DAO/Repository 数据落地 + VO 结果返回”的方式形成完整闭环。  
这也是当前项目后端实现的核心设计思路。
