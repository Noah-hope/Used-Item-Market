# Used Item Market

校园二手交易平台项目，采用前后端分离实现：

- 前端：`Vue 3 + Vite + Vue Router + Pinia + Axios`
- 后端：`Spring MVC + Spring Service + MyBatis + JdbcTemplate`
- 数据库：`MySQL`
- 鉴权：`JWT`

当前项目已经覆盖一个完整的校园二手交易闭环，包括商品浏览、发布、审核、购物车、下单、聊天、收藏、求购、地址管理和管理员后台。

---

## 项目简介

这是一个面向校园场景的二手交易平台，目标是支持校内用户进行闲置商品发布与交易，同时提供后台审核与运营管理能力。

系统分为三个主要使用端：

1. 普通用户端
2. 卖家端
3. 管理员端

普通用户可以浏览商品、收藏、加购、下单、聊天和发布求购；卖家可以发布和管理商品；管理员可以审核商品、管理用户、管理分类并查看平台数据。

---

## 主要功能

### 普通用户功能

- 注册、登录、获取当前用户信息
- 商品分页浏览、搜索、分类筛选、商品详情查看
- 收藏商品
- 加入购物车、移除购物车、购物车下单
- 立即下单
- 查看购买记录
- 与卖家私信沟通
- 发布求购信息
- 维护个人资料
- 新增、编辑、删除收货地址

### 卖家功能

- 发布商品
- 上传商品图片
- 编辑商品
- 下架商品
- 永久删除商品
- 查看自己的商品列表
- 查看销售记录
- 与买家沟通订单信息

### 管理员功能

- 管理员登录
- 用户列表查看、资料修改、停用、启用、重置密码
- 商品审核：通过、驳回、违规下架
- 全站商品查看
- 订单总览
- 分类管理
- 平台数据看板

---

## 技术栈

### 前端

- Vue 3
- Vite
- Vue Router
- Pinia
- Axios

### 后端

- Spring MVC
- Spring JDBC / Service
- MyBatis
- JdbcTemplate
- JWT
- Tomcat 9

### 数据库

- MySQL

---

## 项目结构

```text
Used-Item-Market/
├─ backend/                     后端工程（Maven WAR）
│  ├─ src/main/java/com/useditemmarket
│  │  ├─ controller            接口控制层
│  │  ├─ service               业务层
│  │  ├─ dao                   MyBatis DAO
│  │  ├─ repository            JdbcTemplate Repository
│  │  ├─ dto                   请求对象
│  │  ├─ vo                    返回对象
│  │  ├─ po                    持久化对象
│  │  ├─ model                 枚举与上下文模型
│  │  ├─ security              JWT 与鉴权拦截
│  │  ├─ config                配置与数据库迁移
│  │  └─ exception             全局异常处理
│  ├─ src/main/resources       Spring/MyBatis/数据库配置
│  ├─ start-tomcat9.ps1        后端启动脚本
│  └─ stop-tomcat9.ps1         后端停止脚本
├─ frontend/                    前端工程（Vite）
│  ├─ src/api                  接口封装
│  ├─ src/router               路由配置
│  ├─ src/stores               Pinia 状态管理
│  ├─ src/layouts              页面布局
│  ├─ src/views                页面组件
│  ├─ src/components           通用组件
│  ├─ src/utils                工具函数
│  └─ src/styles.css           全局样式
├─ docs/                        项目说明文档
├─ start-project.ps1            一键启动前后端
├─ stop-project.ps1             一键停止前后端
└─ README.md                    项目总说明
```

---

## 环境要求

建议本地具备以下环境：

- JDK 8
- Maven 3.9+
- Node.js
- npm
- MySQL 5.7+ 或 8.x
- Tomcat 9

如果是首次在新机器上运行，请先确认下面几项：

- `backend/start-tomcat9.ps1` 中的 Maven 路径是否存在
- `backend/start-tomcat9.ps1` 中的 Tomcat 路径是否存在
- `start-project.ps1` 中优先使用的 npm 路径是否存在

当前脚本里的默认本机路径如下：

- Maven：`D:\software\Maven\apache-maven-3.9.16\bin\mvn.cmd`
- Tomcat：`D:\software\Tomcat\apache-tomcat-9.0.119`
- npm：`D:\software\nodejs\npm.cmd`

如果你的机器路径不同，请先修改对应脚本中的变量再启动项目。

---

## 数据库配置

后端数据库配置文件位于：

- `backend/src/main/resources/jdbc.properties`

仓库中提供了示例配置：

- `backend/src/main/resources/jdbc.properties.example`

示例内容如下：

```properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/useditemmarket
username=root
password=your_mysql_password
initiaSize=0
maxActive=20
maxIdle=20
minIdle=1
maxWait=60000
```

如果本地没有 `jdbc.properties`，可根据示例自行创建并修改为自己的数据库连接信息。

说明：

- 后端启动时会执行 `DatabaseMigrationService`
- 会自动补字段、建新表、初始化默认分类

因此只要基础数据库可连接，新增功能相关表通常可以自动初始化。

但需要注意：

1. 需要先在 MySQL 中手动创建数据库，例如：`useditemmarket`
2. 自动迁移负责建表和补字段，不负责创建数据库实例本身

---

## 启动方式

第一次启动项目时，建议按下面顺序操作。

### 启动前准备

1. 安装并确认以下环境可用：
   - JDK 8
   - Maven 3.9+
   - Node.js 与 npm
   - MySQL 5.7+ 或 8.x
   - Tomcat 9
2. 在 MySQL 中创建数据库：

```sql
CREATE DATABASE useditemmarket DEFAULT CHARACTER SET utf8mb4;
```

3. 将 `backend/src/main/resources/jdbc.properties.example` 复制为 `backend/src/main/resources/jdbc.properties`
4. 修改 `jdbc.properties` 中的数据库连接信息
5. 按照你的本机环境，检查并修改以下脚本中的路径变量：
   - `start-project.ps1`
   - `backend/start-tomcat9.ps1`
6. 首次启动前端前，先进入 `frontend` 目录安装依赖：

```powershell
npm install
```

完成以上准备后，再选择下面任一种启动方式。

### 方式一：一键启动前后端

根目录提供了统一启动脚本：

- `start-project.ps1`

在项目根目录执行：

```powershell
.\start-project.ps1
```

如果当前 PowerShell 禁止脚本执行，也可以使用：

```powershell
powershell -ExecutionPolicy Bypass -File .\start-project.ps1
```

执行后会：

1. 先停止已有前后端进程
2. 启动后端 Tomcat 服务
3. 启动前端 Vite 开发服务器

对应停止脚本：

- `stop-project.ps1`

### 方式二：分别启动

### 启动后端

后端脚本：

- `backend/start-tomcat9.ps1`

在项目根目录执行：

```powershell
.\backend\start-tomcat9.ps1
```

该脚本会：

1. 执行 Maven 打包生成 WAR
2. 复制到 Tomcat Base 的 `webapps/ROOT.war`
3. 启动 Tomcat 运行后端服务

### 启动前端

进入前端目录后执行：

```powershell
npm install
npm run dev
```

如果你已经安装过依赖，只执行下面这条也可以：

```powershell
npm run dev
```

或使用根目录启动脚本统一拉起。

---

## 默认访问方式

前端使用 Vite 开发服务器启动后，可通过浏览器访问：

- `http://localhost:5173`

后端默认由 Tomcat 运行，脚本中检查的端口是：

- `8080`

前端开发服务器已经配置了代理：

- `/api` -> `http://localhost:8080`
- `/img` -> `http://localhost:8080`

也就是说，前端页面访问 `5173` 端口，接口请求会自动转发到后端 `8080` 端口。

---

## 后端接口风格

后端统一使用：

- `/api/**` 作为接口前缀
- `ApiResponse` 作为统一返回格式
- `Authorization: Bearer <token>` 作为登录鉴权头

公开接口主要包括：

- 登录
- 注册
- 商品列表
- 商品详情
- 分类列表
- 公开求购列表

管理员接口统一在：

- `/api/admin/**`

---

## 文档目录

当前 `docs` 目录已经整理了项目主要说明文档：

- [功能模块说明](./docs/feature-modules.md)
- [后端实现说明](./docs/backend-feature-implementation.md)
- [前端实现说明](./docs/frontend-feature-implementation.md)
- [数据库设计说明](./docs/database-design.md)
- [后端 API 文档](./docs/backend-api.md)

建议阅读顺序：

1. 先看本 README 了解整体项目
2. 再看功能模块说明了解平台做了什么
3. 然后根据需要分别查看前端、后端、数据库和 API 文档

---

## 当前实现特点

这个项目当前有几个比较鲜明的实现特点：

1. 前后端已经完成分离，前端负责页面与交互，后端负责业务和数据。
2. 用户端、卖家端、管理员端已经有明确边界。
3. 商品采用“发布后待审核”的平台型流程。
4. 交易链路完整，包含购物车、订单、聊天、地址与库存联动。
5. 数据访问层同时使用 MyBatis DAO 和 JdbcTemplate Repository：
   - DAO 主要负责核心旧表
   - Repository 主要负责新功能和聚合查询
6. 数据库迁移逻辑内置在项目中，适合课程项目快速运行和演示。

---

## 开发说明

### 前端开发重点目录

- `frontend/src/views`
- `frontend/src/api/modules.js`
- `frontend/src/router/index.js`
- `frontend/src/stores/auth.js`

### 后端开发重点目录

- `backend/src/main/java/com/useditemmarket/controller/api`
- `backend/src/main/java/com/useditemmarket/service/api/impl`
- `backend/src/main/java/com/useditemmarket/dao`
- `backend/src/main/java/com/useditemmarket/repository`

### 配置重点文件

- `backend/src/main/resources/spring-mvc.xml`
- `backend/src/main/resources/spring-mybatis.xml`
- `backend/src/main/resources/jdbc.properties`

---

## 后续可扩展方向

如果继续完善这个项目，比较自然的方向包括：

- 增加自动化测试
- 增强数据库约束与索引设计
- 增加评价、退款、取消订单等完整交易流程
- 优化商品列表查询为数据库分页
- 增加消息会话表、操作日志表等辅助模型
- 优化部署脚本，使项目更容易迁移到新机器

---

## 总结

`Used Item Market` 当前已经具备一个校园二手交易平台应有的核心能力：

- 有用户体系
- 有商品体系
- 有交易体系
- 有平台审核体系
- 有后台管理体系

如果你是第一次接手这个项目，建议从以下路径开始：

1. 看本 README 了解整体结构
2. 看 `docs/feature-modules.md` 理解平台功能
3. 看 `docs/backend-api.md` 和 `docs/database-design.md` 对接口与数据
4. 再进入前端或后端代码做针对性阅读
