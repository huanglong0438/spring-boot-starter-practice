package com.dc.boynextdoor.remoting.server;

import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.URI;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * NettyServer
 *
 * @title NettyServer
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-01
 **/
@Slf4j
public class NettyServer implements Server {

//    protected final ChannelGroup allChannels = new DefaultChannelGroup("netty-rpc-server");

    private String host;

    private int port;

    private NettyServerInitializer serverChannelInitializer;

    private NettyServerHander handler = new NettyServerHander(this);

    private ChannelFuture future;

    private CountDownLatch closed = new CountDownLatch(1);

    public NettyServer(URI uri) {
        // 从uri中取出
        this.serverChannelInitializer = new NettyServerInitializer(uri, handler);
        this.port = uri.getPort();
        this.host = uri.getHost();
    }

    @Override
    public int getPort() {
        return 0;
    }

    /**
     * 核心：启动netty服务
     */
    @Override
    public void start() {
        log.info("Netty Server starting... at " + host + ":" + port);
        // 【重点】netty服务的启动
        InetSocketAddress addr = new InetSocketAddress(host, port);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 用NIO
                    .childHandler(serverChannelInitializer) // boosGroup处理接收，worker负责处理
                    .option(ChannelOption.SO_BACKLOG, 128) // TCP的参数
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 维持长连接
            future = b.bind(addr).syncUninterruptibly(); // 等待bind结束，不可中断
            log.info("Rpc Server started... at " + port);
            future.channel().closeFuture().syncUninterruptibly();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void close() {
        log.info("Rpc Server closing... at " + host + ":" + port);
        future.awaitUninterruptibly();
//        serverChannelInitializer.close();
        closed.countDown();
    }

    @Override
    public void join() throws InterruptedException {
        closed.await();
    }

    @Override
    public void registerService(Requestor<?> requestor) {

    }

    @Override
    public void unregisterService(Requestor<?> requestor) {

    }

    public void addChannel(Channel channel) {

    }

    public static void main(String[] args) {
        URI uri = new URI("van", null, null,
                "127.0.0.1", 8888, "/fuck/you", null);
        NettyServer server = new NettyServer(uri);
        server.start();
    }
}

