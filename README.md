# springboot-dubbo
  simple example 练手dubbo
  主要为了测试dubbo协议下性能，与serlvet的http协议性能。
  100000次调用，测试下来貌似dubbo【17s】http【27s】快了不少。后续将继续测试dubbo自带协议之间的性能。

##说明
  springboot-dubbo-server  服务器工程
  springboot-dubbo-service 服务器接口实现类
  springboot-dubbo-api     服务器对外接口
  springboot-dubbo-client  dubbo调用的客户端
  springboot-http-client   http调用的客户端

##使用
  启动服务： ApplicationDubboServer.java
  dubbo测试：ApplicationDubboClient.java
  http测试： ApplicationDubboClient.java

##工程构建
  1 打开springboot-dubbo-server ,输入gradle eclipse
  2 打开springboot-dubbo-client ,输入gradle eclipse
  3 springboot-http-client      ,输入gradle eclipse
  4 配置zookeeper

##问题
  下次直接测试RMI方式效果
@2016
