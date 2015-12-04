Solar 项目基础支架
=====


## 技术选型 ##

 - Spring Boot
 - Mybatis & PageHelper分页插件
 - Apache Shiro
 - Alibaba Druid
 - Redis 共享Session与ORM二级缓存
 - Mybatis Generator
 - Embed Tomcat
 - MariaDB or MySQL
 - Logback
 
----------
## 模块说明 ##
 - **common** 属于通用配置
    - Mybatis Mapper抽象
    - 基础的CRUD操作的Service实现`ElementaryService`
    - Controller层的RESTful工具`Shift`
 - **config** 配置模块
    - 所有配置选项有且仅有存在这个模块
    - 包括Druid数据源，Mybatis，Shiro，Session DAO，Logback与Spring Boot配置
 - **system** 权限管理模块 **(未完成待定)**
    - RBAC权限模块
> 正在对JWT(JSON Web Token)与传统Session-Cookie认证的选择当中
    


----------

## Page Helper 使用说明 ##
在`config`模块下`resources`配置文件夹中`application.properties`配置选项

> mybatis.pagination-enable=true

----------

## Mybatis 配置说明 ##
集成 **mybatis-spring-boot** 项目，除原生
> 
mybatis.config= # mybatis config file
mybatis.mapperLocations= # mappers file
mybatis.typeAliasesPackage= # domain object's package 
mybatis.typeHandlersPackage= # handler's package
mybatis.check-config-location= # check the mybatis configuration exists
mybatis.executorType= # mode of execution. Default is SIMPLE

**额外添加mybatis.pagination-enable=true配置**


----------

## Reference ##
Json Web Token
> http://jwt.io/

Spring Boot Properties
> http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html

Mybatis Spring Boot
> https://github.com/mybatis/mybatis-spring-boot

Page Helper
> https://github.com/pagehelper/Mybatis-PageHelper

