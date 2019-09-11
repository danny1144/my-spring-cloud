# my-spring-cloud

2019-09-11 借助本地锁实现http请求防重复提交

``

使用了 ConcurrentHashMap 并发容器 putIfAbsent 方法,和 ScheduledThreadPoolExecutor 定时任务,也可以使用guava cache的机制, gauva中有配有缓存的有效时间也是可以的key的生成 
Content-MD5 
Content-MD5 是指 Body 的 MD5 值，只有当 Body 非Form表单时才计算MD5，计算方式直接将参数和参数名称统一加密MD5
MD5在一定范围类认为是唯一的 近似唯一 当然在低并发的情况下足够了
本地锁只适用于单机部署的应用.

``
