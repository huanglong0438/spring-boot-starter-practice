# boy next door
一个基于SpringBoot的starter开发的RPC框架，  
序列化计划采用Google的ProtoBuffer，  
服务采用**基于NIO多路复用技术的**netty框架，支持高并发情况下快速响应

## 结构
入口：EndPointStarter，响应容器启动事件，注册服务到zk（或者Eruka），启动netty server

## 技术点
 - `com.dc.boynextdoor.common.ext.TypeLocator` - 线程安全的懒汉单例模式
 - `com.dc.boynextdoor.remoting.server.NettyServer` - netty服务端的最佳实践
 
## todo
 - `com.dc.boynextdoor.remoting.server.NettyServer`补全并单测
 - `@ServiceExported @ServiceReference`在`ServiceExporterRegisterBean`中注册生效的逻辑，并单测