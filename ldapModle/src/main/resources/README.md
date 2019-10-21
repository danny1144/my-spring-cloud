## ldap模块

1、同步华盾的ldap数据到西门子数据库
2、测试阶段引入h2内存数据库，方便调试同步。同步成功可到localhost:8999/h2  查看同步成功数据。
3、同步的属性和过滤器都可配置
4、开发阶段把pom.xml中pgsql注释放开
```$xslt
 <!--       <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>-->

```

测试的用户和用户组结构
![用户组](https://raw.githubusercontent.com/danny1144/picgo/master/20191021143816.png)

![用户](https://raw.githubusercontent.com/danny1144/picgo/master/20191021143905.png)