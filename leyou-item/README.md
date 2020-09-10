## 商品服务-8081
## 学习笔记

编写一个controller一般需要知道四个内容：

请求方式：决定我们用GetMapping还是PostMapping
请求路径：决定映射路径
请求参数：决定方法的参数
返回值结果：决定方法的返回值

### 商品管理

既然是一个全品类的电商购物平台，那么核心自然就是商品。因此我们要搭建的第一个服务，就是商品微服务。其中会包含对于商品相关的一系列内容的管理，包括：

- 商品分类管理
- 品牌管理
- 商品规格参数管理
- 商品管理
- 库存管理


因为与商品的品类相关，我们的工程命名为`ly-item`.

需要注意的是，我们的ly-item是一个微服务，那么将来肯定会有其它系统需要来调用服务中提供的接口，因此肯定也会使用到接口中关联的实体类。

因此这里我们需要使用聚合工程，将要提供的接口及相关实体类放到独立子工程中，以后别人引用的时候，只需要知道坐标即可。

我们会在ly-item中创建两个子工程：

- ly-item-interface：主要是对外暴露的接口及相关实体类
- ly-item-service：所有业务逻辑及内部使用接口