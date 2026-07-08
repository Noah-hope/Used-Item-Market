# 二手交易平台后端 API 文档

## 1. 文档说明

本文档基于当前项目后端代码整理，说明系统现有 REST API 的接口路径、请求方式、鉴权要求、请求参数和主要返回结构。

后端接口统一前缀为：

- `/api`

当前后端主要采用：

- `Spring MVC`
- `JSON` 请求/响应
- `JWT Bearer Token` 鉴权

---

## 2. 通用规则

## 2.1 统一响应格式

除文件上传仍返回 JSON 外，所有接口都使用统一结构：

```json
{
  "code": 0,
  "message": "OK",
  "data": {}
}
```

字段说明：

| 字段 | 含义 |
| --- | --- |
| `code` | 业务状态码，`0` 表示成功 |
| `message` | 提示信息 |
| `data` | 返回数据，可能是对象、数组、分页对象或 `null` |

失败时示例：

```json
{
  "code": 401,
  "message": "未登录或登录已过期",
  "data": null
}
```

---

## 2.2 鉴权方式

需要登录的接口，请在请求头中携带：

```http
Authorization: Bearer <token>
```

Token 来源：

- `POST /api/auth/login`

---

## 2.3 当前公开接口

根据 `spring-mvc.xml` 当前不需要登录即可访问的接口有：

- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/catalog/goods`
- `GET /api/catalog/goods/{gid}`
- `GET /api/catalog/categories`
- `GET /api/wanted/open`

其余 `/api/**` 默认都需要登录。

---

## 2.4 管理员接口

以下接口除了需要登录，还要求当前用户是管理员：

- `/api/admin/**`

如果当前用户不是管理员，会返回：

- HTTP `403`
- `message: 无管理员权限`

---

## 2.5 分页规则

当前商品列表接口使用分页参数：

| 参数 | 默认值 | 说明 |
| --- | --- | --- |
| `page` | `1` | 页码，从 1 开始 |
| `pageSize` | `12` | 每页大小，最大 100 |

分页返回格式：

```json
{
  "list": [],
  "page": 1,
  "pageSize": 12,
  "total": 100
}
```

---

## 2.6 常见状态值

### 用户状态

- `ACTIVE`
- `PASSWORD_RESET_REQUIRED`
- `DISABLED`

### 商品状态

- `PENDING_REVIEW`
- `ACTIVE`
- `REJECTED`
- `OFF_SHELF`
- `BANNED`

### 订单状态

- `PENDING_PICKUP`
- `PENDING_SHIPMENT`
- `PENDING_RECEIPT`
- `COMPLETED`

### 交付方式

- `SELF_PICKUP`
- `CAMPUS_DELIVERY`

---

## 3. 认证模块

接口前缀：

- `/api/auth`

## 3.1 登录

### 请求

- 方法：`POST`
- 路径：`/api/auth/login`
- 鉴权：否

请求体：

```json
{
  "studentNo": "20240001",
  "password": "123456",
  "admin": false
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `studentNo` | string | 是 | 学号 |
| `password` | string | 是 | 密码 |
| `admin` | boolean | 否 | 是否按管理员身份登录 |

成功返回 `data`：

```json
{
  "accessToken": "jwt-token",
  "user": {
    "uid": "NORM20240001",
    "username": "张三",
    "email": "a@xx.com",
    "phoneNumber": "13800000000",
    "studentNo": "20240001",
    "realName": "张三",
    "campusVerified": true,
    "avatar": null,
    "bio": null,
    "admin": false,
    "status": "ACTIVE"
  }
}
```

## 3.2 注册

### 请求

- 方法：`POST`
- 路径：`/api/auth/register`
- 鉴权：否

请求体：

```json
{
  "studentNo": "20240001",
  "username": "张三",
  "password": "123456",
  "confirmPassword": "123456",
  "email": "a@xx.com",
  "phoneNumber": "13800000000",
  "realName": "张三"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `studentNo` | string | 是 | 学号 |
| `username` | string | 是 | 用户名 |
| `password` | string | 是 | 密码 |
| `confirmPassword` | string | 是 | 确认密码 |
| `email` | string | 是 | 邮箱 |
| `phoneNumber` | string | 是 | 手机号 |
| `realName` | string | 否 | 真实姓名 |

成功返回：

- `data` 为 `UserVo`

## 3.3 获取当前登录用户

### 请求

- 方法：`GET`
- 路径：`/api/auth/me`
- 鉴权：是

成功返回：

- `data` 为 `UserVo`

---

## 4. 商品浏览模块

接口前缀：

- `/api/catalog`

## 4.1 商品分页列表

### 请求

- 方法：`GET`
- 路径：`/api/catalog/goods`
- 鉴权：否

查询参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `keyword` | string | 否 | 关键字搜索 |
| `category` | string | 否 | 分类名称 |
| `sort` | string | 否 | 排序方式 |
| `page` | int | 否 | 页码 |
| `pageSize` | int | 否 | 每页数量 |

`sort` 当前支持：

- `LATEST`
- `PRICE_ASC`
- `PRICE_DESC`
- `STOCK_ASC`
- `STOCK_DESC`

成功返回：

- `data` 为 `PageResponse<GoodsVo>`

## 4.2 商品详情

### 请求

- 方法：`GET`
- 路径：`/api/catalog/goods/{gid}`
- 鉴权：否

路径参数：

| 参数 | 说明 |
| --- | --- |
| `gid` | 商品编号 |

成功返回：

- `data` 为 `GoodsVo`

## 4.3 分类列表

### 请求

- 方法：`GET`
- 路径：`/api/catalog/categories`
- 鉴权：否

成功返回：

- `data` 为 `CategoryVo[]`

`CategoryVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `code` | 分类编码 |
| `label` | 分类名称 |
| `sortOrder` | 排序值 |
| `enabled` | 是否启用 |

---

## 5. 卖家商品模块

接口前缀：

- `/api/seller/goods`

所有接口都要求：

- 已登录
- 普通用户身份

## 5.1 我的商品列表

### 请求

- 方法：`GET`
- 路径：`/api/seller/goods`

成功返回：

- `data` 为 `GoodsVo[]`

## 5.2 我的商品详情

### 请求

- 方法：`GET`
- 路径：`/api/seller/goods/{gid}`

成功返回：

- `data` 为 `GoodsVo`

## 5.3 发布商品

### 请求

- 方法：`POST`
- 路径：`/api/seller/goods`

请求体：

```json
{
  "name": "高数教材",
  "category": "教材",
  "price": 20,
  "stock": 1,
  "image": "/img/NORM20240001/a.png",
  "comment": "九成新",
  "deliveryMode": "SELF_PICKUP",
  "pickupLocation": "一食堂门口"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 是 | 商品名称 |
| `category` | string | 是 | 商品分类 |
| `price` | number | 是 | 商品价格 |
| `stock` | number | 是 | 商品库存 |
| `image` | string | 否 | 图片路径 |
| `comment` | string | 否 | 商品描述 |
| `deliveryMode` | string | 是 | 交付方式 |
| `pickupLocation` | string | 否 | 自提点或送货说明 |

成功返回：

- `data` 为 `GoodsVo`

## 5.4 编辑商品

### 请求

- 方法：`PUT`
- 路径：`/api/seller/goods/{gid}`

请求体字段与“发布商品”一致，均为可更新字段。

成功返回：

- `data` 为 `GoodsVo`

## 5.5 下架商品

### 请求

- 方法：`DELETE`
- 路径：`/api/seller/goods/{gid}`

成功返回：

- `data: null`

## 5.6 永久删除商品

### 请求

- 方法：`DELETE`
- 路径：`/api/seller/goods/{gid}/permanent`

成功返回：

- `data: null`

## 5.7 上传商品图片

### 请求

- 方法：`POST`
- 路径：`/api/seller/goods/upload-image`
- Content-Type：`multipart/form-data`

表单字段：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `imageFile` | file | 是 | 上传图片文件 |

成功返回：

```json
{
  "code": 0,
  "message": "上传成功",
  "data": "/img/NORM20240001/xxxx.png"
}
```

---

## 6. 购物车模块

接口前缀：

- `/api/cart`

## 6.1 购物车列表

### 请求

- 方法：`GET`
- 路径：`/api/cart`

成功返回：

- `data` 为 `CartItemVo[]`

`CartItemVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `gid` | 商品 ID |
| `name` | 商品名称 |
| `category` | 商品分类 |
| `price` | 商品价格 |
| `quantity` | 购物车数量 |
| `stock` | 当前库存 |
| `image` | 商品图片 |
| `sellerUid` | 卖家 UID |
| `sellerName` | 卖家名称 |
| `status` | 商品状态 |

## 6.2 加入购物车

### 请求

- 方法：`POST`
- 路径：`/api/cart/items`

请求体：

```json
{
  "gid": "1000000001",
  "quantity": 1
}
```

成功返回：

- `data` 为最新 `CartItemVo[]`

## 6.3 修改购物车数量

### 请求

- 方法：`PUT`
- 路径：`/api/cart/items/{gid}`

请求体：

```json
{
  "gid": "1000000001",
  "quantity": 2
}
```

说明：

- 实际更新依赖路径参数中的 `gid`

成功返回：

- `data` 为最新 `CartItemVo[]`

## 6.4 移除购物车商品

### 请求

- 方法：`DELETE`
- 路径：`/api/cart/items/{gid}`

成功返回：

- `data` 为最新 `CartItemVo[]`

---

## 7. 订单模块

接口前缀：

- `/api/orders`

## 7.1 创建订单

### 请求

- 方法：`POST`
- 路径：`/api/orders`

请求体：

```json
{
  "gid": "1000000001",
  "quantity": 1,
  "fromCart": false,
  "addressId": 1
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `gid` | string | 是 | 商品 ID |
| `quantity` | number | 是 | 购买数量 |
| `fromCart` | boolean | 否 | 是否来自购物车 |
| `addressId` | long | 否 | 收货地址 ID |

成功返回：

- `data` 为 `OrderVo`

## 7.2 我的购买记录

### 请求

- 方法：`GET`
- 路径：`/api/orders/purchases`

查询参数：

| 参数 | 默认值 | 说明 |
| --- | --- | --- |
| `status` | `PENDING_PICKUP` | 订单状态 |

成功返回：

- `data` 为 `OrderVo[]`

## 7.3 我的销售记录

### 请求

- 方法：`GET`
- 路径：`/api/orders/sales`

查询参数：

| 参数 | 默认值 | 说明 |
| --- | --- | --- |
| `status` | `PENDING_SHIPMENT` | 订单状态 |

成功返回：

- `data` 为 `OrderVo[]`

## 7.4 卖家发货

### 请求

- 方法：`POST`
- 路径：`/api/orders/{pid}/ship`

成功返回：

- `data` 为更新后的 `OrderVo`

## 7.5 买家确认收货

### 请求

- 方法：`POST`
- 路径：`/api/orders/{pid}/receive`

成功返回：

- `data` 为更新后的 `OrderVo`

## 7.6 删除订单

### 请求

- 方法：`DELETE`
- 路径：`/api/orders/{pid}`

成功返回：

- `data: null`

`OrderVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `pid` | 订单 ID |
| `buyerUid` | 买家 UID |
| `sellerUid` | 卖家 UID |
| `date` | 日期 |
| `time` | 时间 |
| `gid` | 商品 ID |
| `goodsName` | 商品名称 |
| `category` | 商品分类 |
| `price` | 单价 |
| `quantity` | 购买数量 |
| `status` | 订单状态 |
| `deliveryMode` | 交付方式 |
| `pickupLocation` | 自提/送货说明 |
| `appointmentTime` | 预约时间 |
| `remark` | 订单备注 |
| `addressSnapshot` | 地址快照 |

---

## 8. 地址模块

接口前缀：

- `/api/addresses`

## 8.1 地址列表

### 请求

- 方法：`GET`
- 路径：`/api/addresses`

成功返回：

- `data` 为 `AddressVo[]`

## 8.2 新增地址

### 请求

- 方法：`POST`
- 路径：`/api/addresses`

请求体：

```json
{
  "receiverName": "张三",
  "phoneNumber": "13800000000",
  "campusArea": "东校区",
  "detailAddress": "1号楼101",
  "defaultAddress": true
}
```

成功返回：

- `data` 为 `AddressVo`

## 8.3 修改地址

### 请求

- 方法：`PUT`
- 路径：`/api/addresses/{id}`

请求体字段与新增地址一致。

成功返回：

- `data` 为 `AddressVo`

## 8.4 删除地址

### 请求

- 方法：`DELETE`
- 路径：`/api/addresses/{id}`

成功返回：

- `data: null`

`AddressVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 地址 ID |
| `receiverName` | 收货人 |
| `phoneNumber` | 电话 |
| `campusArea` | 校区/宿舍区 |
| `detailAddress` | 详细地址 |
| `defaultAddress` | 是否默认地址 |

---

## 9. 收藏模块

接口前缀：

- `/api/favorites`

## 9.1 收藏列表

### 请求

- 方法：`GET`
- 路径：`/api/favorites`

成功返回：

- `data` 为 `GoodsVo[]`

## 9.2 收藏商品

### 请求

- 方法：`POST`
- 路径：`/api/favorites/{gid}`

成功返回：

- `data: null`

## 9.3 取消收藏

### 请求

- 方法：`DELETE`
- 路径：`/api/favorites/{gid}`

成功返回：

- `data: null`

---

## 10. 聊天模块

接口前缀：

- `/api/chat`

## 10.1 会话列表

### 请求

- 方法：`GET`
- 路径：`/api/chat/conversations`

成功返回：

- `data` 为 `ChatConversationVo[]`

`ChatConversationVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `conversationKey` | 会话键 |
| `peerUid` | 对方 UID |
| `peerName` | 对方名称 |
| `goodsId` | 关联商品 ID |
| `orderPid` | 关联订单 ID |
| `goodsName` | 商品名称 |
| `goodsImage` | 商品图片 |
| `lastMessage` | 最后一条消息 |
| `lastSenderUid` | 最后一条消息发送者 |
| `lastTime` | 最后消息时间 |
| `unreadCount` | 未读数 |

## 10.2 获取会话消息

### 请求

- 方法：`GET`
- 路径：`/api/chat/messages/{conversationKey}`

成功返回：

- `data` 为 `ChatMessageVo[]`

## 10.3 发送消息

### 请求

- 方法：`POST`
- 路径：`/api/chat/messages`

请求体：

```json
{
  "receiverUid": "NORM20240002",
  "goodsId": "1000000001",
  "orderPid": null,
  "content": "你好，我想咨询一下商品情况"
}
```

字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `receiverUid` | string | 是 | 接收方 UID |
| `goodsId` | string | 否 | 关联商品 ID |
| `orderPid` | string | 否 | 关联订单 ID |
| `content` | string | 是 | 消息内容 |

成功返回：

- `data` 为 `ChatMessageVo`

## 10.4 删除会话

### 请求

- 方法：`DELETE`
- 路径：`/api/chat/conversations/{conversationKey}`

成功返回：

- `data: null`

`ChatMessageVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 消息 ID |
| `senderUid` | 发送者 UID |
| `receiverUid` | 接收者 UID |
| `goodsId` | 商品 ID |
| `orderPid` | 订单 ID |
| `content` | 内容 |
| `createdAt` | 创建时间 |
| `read` | 是否已读 |

---

## 11. 求购模块

接口前缀：

- `/api/wanted`

## 11.1 公开求购列表

### 请求

- 方法：`GET`
- 路径：`/api/wanted/open`
- 鉴权：否

成功返回：

- `data` 为 `WantedVo[]`

## 11.2 我的求购列表

### 请求

- 方法：`GET`
- 路径：`/api/wanted/mine`

成功返回：

- `data` 为 `WantedVo[]`

## 11.3 发布求购

### 请求

- 方法：`POST`
- 路径：`/api/wanted`

请求体：

```json
{
  "title": "求购高数教材",
  "category": "教材",
  "budget": 30,
  "keyword": "高数",
  "description": "最好有笔记"
}
```

成功返回：

- `data` 为 `WantedVo`

## 11.4 删除求购

### 请求

- 方法：`DELETE`
- 路径：`/api/wanted/{id}`

成功返回：

- `data` 为被删除的 `WantedVo`

`WantedVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 求购 ID |
| `uid` | 发布者 UID |
| `publisherName` | 发布者名称 |
| `title` | 标题 |
| `category` | 分类 |
| `budget` | 预算 |
| `keyword` | 关键词 |
| `description` | 描述 |
| `status` | 状态 |
| `createdAt` | 发布时间 |
| `matches` | 匹配商品列表 |

---

## 12. 个人中心模块

接口前缀：

- `/api/profile`

## 12.1 获取个人资料

### 请求

- 方法：`GET`
- 路径：`/api/profile`

成功返回：

- `data` 为 `UserVo`

## 12.2 修改个人资料

### 请求

- 方法：`PUT`
- 路径：`/api/profile`

请求体：

```json
{
  "username": "张三",
  "email": "a@xx.com",
  "phoneNumber": "13800000000",
  "realName": "张三",
  "avatar": "https://...",
  "bio": "爱买爱卖"
}
```

成功返回：

- `data` 为 `UserVo`

## 12.3 修改密码

### 请求

- 方法：`PUT`
- 路径：`/api/profile/password`

请求体：

```json
{
  "password": "123456",
  "confirmPassword": "123456"
}
```

成功返回：

- `data: null`

`UserVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `uid` | 用户 ID |
| `username` | 用户名 |
| `email` | 邮箱 |
| `phoneNumber` | 手机号 |
| `studentNo` | 学号 |
| `realName` | 真实姓名 |
| `campusVerified` | 是否通过校园认证 |
| `avatar` | 头像地址 |
| `bio` | 个人简介 |
| `admin` | 是否管理员 |
| `status` | 用户状态 |

---

## 13. 管理员模块

接口前缀：

- `/api/admin`

所有接口都要求：

- 已登录
- 管理员身份

## 13.1 用户管理

### 1. 用户列表

- 方法：`GET`
- 路径：`/api/admin/users`
- 返回：`UserVo[]`

### 2. 更新用户资料

- 方法：`PUT`
- 路径：`/api/admin/users/{uid}`

请求体字段与 `/api/profile` 更新资料一致。

返回：

- `UserVo`

### 3. 重置用户密码

- 方法：`POST`
- 路径：`/api/admin/users/{uid}/reset-password`
- 返回：`UserVo`

### 4. 停用用户

- 方法：`POST`
- 路径：`/api/admin/users/{uid}/disable`
- 返回：`UserVo`

### 5. 启用用户

- 方法：`POST`
- 路径：`/api/admin/users/{uid}/enable`
- 返回：`UserVo`

## 13.2 订单总览

- 方法：`GET`
- 路径：`/api/admin/orders`
- 返回：`OrderVo[]`

## 13.3 商品审核与管理

### 1. 待审核商品列表

- 方法：`GET`
- 路径：`/api/admin/goods/pending`
- 返回：`GoodsVo[]`

### 2. 全部商品列表

- 方法：`GET`
- 路径：`/api/admin/goods`
- 返回：`GoodsVo[]`

### 3. 审核商品

- 方法：`POST`
- 路径：`/api/admin/goods/{gid}/review`

请求体：

```json
{
  "action": "approve",
  "note": "审核通过"
}
```

`action` 当前支持：

- `approve`
- `reject`
- `ban`

返回：

- `GoodsVo`

## 13.4 分类管理

### 1. 分类列表

- 方法：`GET`
- 路径：`/api/admin/categories`
- 返回：`CategoryVo[]`

### 2. 保存分类

- 方法：`POST`
- 路径：`/api/admin/categories`

请求体：

```json
{
  "code": "TEXTBOOK",
  "label": "教材",
  "sortOrder": 10,
  "enabled": true
}
```

返回：

- `CategoryVo`

## 13.5 数据看板

- 方法：`GET`
- 路径：`/api/admin/dashboard`
- 返回：`DashboardVo`

`DashboardVo` 字段：

| 字段 | 说明 |
| --- | --- |
| `goodsPublished` | 累计发布商品数 |
| `goodsActive` | 当前上架商品数 |
| `goodsPending` | 待审核商品数 |
| `totalOrders` | 总订单数 |
| `completedOrders` | 已完成订单数 |
| `disabledUsers` | 已停用用户数 |
| `wantedOpen` | 开放求购数 |

---

## 14. 主要返回对象补充

## 14.1 `GoodsVo`

| 字段 | 说明 |
| --- | --- |
| `gid` | 商品 ID |
| `name` | 商品名称 |
| `category` | 商品分类 |
| `price` | 价格 |
| `stock` | 库存 |
| `image` | 图片地址 |
| `comment` | 商品描述 |
| `sellerUid` | 卖家 UID |
| `sellerName` | 卖家名称 |
| `status` | 商品状态 |
| `deliveryMode` | 交付方式 |
| `pickupLocation` | 自提/送货说明 |
| `reviewNote` | 审核备注 |
| `publishedAt` | 发布时间 |

## 14.2 `LoginResponse`

| 字段 | 说明 |
| --- | --- |
| `accessToken` | JWT Token |
| `user` | 当前登录用户信息 |

---

## 15. 非 `/api` 图片访问接口

虽然不属于 REST 业务接口，但当前项目中图片最终通过以下路径访问：

- `GET /img/{uid}/{filename}`

用途：

- 返回商品图片文件

通常前端不会手工调用这个接口，而是直接使用上传接口返回的图片路径进行展示。

---

## 16. 常见错误码

| HTTP/业务码 | 含义 |
| --- | --- |
| `0` | 成功 |
| `400` | 参数错误、业务校验失败 |
| `401` | 未登录或登录过期 |
| `403` | 无权限 |
| `404` | 资源不存在 |
| `500` | 服务器内部错误 |

常见错误消息示例：

- `未登录或登录已过期`
- `登录凭证无效`
- `无管理员权限`
- `商品不存在`
- `订单不存在`
- `图片大小不能超过5MB`

---

## 17. 结论

当前后端 API 已经覆盖了这个二手交易平台的完整业务主线：

- 认证登录
- 商品浏览
- 卖家发布商品
- 购物车
- 下单交易
- 地址维护
- 收藏
- 私信聊天
- 求购广场
- 个人中心
- 管理员后台

整体接口风格比较统一：

1. 统一 `/api` 前缀
2. 统一 `ApiResponse` 返回结构
3. 统一 JWT 鉴权
4. 按业务模块拆分 Controller

如果后续需要，我可以继续把这份文档再补成“带完整 JSON 示例响应”的版本，或者进一步生成一份适合前端直接对接的简化接口表。 
