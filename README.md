# my-spring-cloud

2019-09-11 借助本地锁实现http请求防重复提交

## 原理
``
使用了 ConcurrentHashMap 并发容器 putIfAbsent 方法,和 ScheduledThreadPoolExecutor 定时任务,也可以使用guava cache的机制, gauva中有配有缓存的有效时间也是可以的key的生成 
Content-MD5 
Content-MD5 是指 Body 的 MD5 值，只有当 Body 非Form表单时才计算MD5，计算方式直接将参数和参数名称统一加密MD5
MD5在一定范围类认为是唯一的 近似唯一 当然在低并发的情况下足够了
本地锁只适用于单机部署的应用.
``
2019-09-12 使用Redis锁实现http防止重复提交
## 原理

``
使用Redis 的setIfAbsent判断属性对应的值是否存在(redis层是通过setNx命令实现)，如果存在则加锁然后设置超时时间。
如果设置超时时间成功则加锁成功,如果设置失败则表示锁过期了。
如果加锁失败则获取旧址与当前时间对比，如果小于当前时间则加锁成功
反之则属于重复提交
``