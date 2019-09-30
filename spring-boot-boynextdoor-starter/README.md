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
 - `com.dc.boynextdoor.autoconfigure.exporting.ServiceExporterRegisterBean.register`注册中心生效+单测
 

## 2019年09月29日
1. inbound和outbound的顺序，inbound一般用来读，outbound一般用来写
2. 写操作要用writeAndFlush，不然写不出去，还在本地缓冲区（经验）
3. `UnsupportedOperationException: direct buffer`，在`NettyServerHander`这里直接用buffer.array()不行，因为这个buffer是直接内存  
[一次ByteBuf的故障排查经历](http://www.sohu.com/a/132214404_684743)  
>为了提升性能，Netty默认的I/O Buffer使用直接内存DirectByteBuf，可以减少Socket读写的内存拷贝，即著名的 ”零拷贝”。  
 由于是直接内存，因此无法直接转换成堆内存，因此它并不支持array()方法。用户需要自己做内存拷贝。