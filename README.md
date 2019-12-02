

本项目将 Springboot 运行在 Netty4 上, 完全将 Netty 作为一个接受 http 请求的 web　容器，特点：

1). 无入侵 SpringBoot 项目原代码；

2). 支持 静态页面，thymeleaf 模板引擎, forward 和 Redirect；

3). Netty IO 线程和 业务线程分离, 可以支持后台 数据库操作；


Spring boot 项目已 web(WebApplicationType.NONE) 方式运行， 通过 spring-servlet.xml 文件产生 DispatcherServlet，
将其 注入 Netty hander pipeline 中，处理后续的 controller。 ( 这也是唯一的遗憾的地方，目前尚 没有通过 Mock 或者 
application.yml 获得 DispatcherServlet)

springboot 后续的 controller 无需任何改动， 支持 get, post， 普通参数， 表单等多种方式的提交。
 




