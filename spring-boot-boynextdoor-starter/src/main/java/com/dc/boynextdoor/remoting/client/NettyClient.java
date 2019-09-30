package com.dc.boynextdoor.remoting.client;

import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.codec.Codec;
import com.dc.boynextdoor.common.codec.protostuff.ProtostuffCodec;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.remoting.core.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * NettyClient todo dlc
 *
 * @title NettyClient
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-27
 **/
@Slf4j
public class NettyClient implements Client {

    private NettyClientHandler handler;

    private Channel channel;

    private URI uri;

    /**
     * 读写可重入锁，用来在进行写入操作的时候hang住其它线程
     */
    private ReadWriteLock stateLock = new ReentrantReadWriteLock();

    private InetSocketAddress remoteAddr;

    private Bootstrap bootstrap;

    private NettyClientInitializer clientPipeLineFactory;

    public NettyClient(URI uri) {
        this.uri = uri;
    }

    @Override
    public void connect(URI uri) throws IllegalStateException {
        if (uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        this.uri = uri;
        InetSocketAddress addr = new InetSocketAddress(uri.getHost(), uri.getPort());
        remoteAddr = addr;
        this.handler = new NettyClientHandler(uri);
        // handlers
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 默认创建CPU数量*2的eventLoop
        this.clientPipeLineFactory = new NettyClientInitializer(uri, handler);
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(clientPipeLineFactory)
                .option(ChannelOption.TCP_NODELAY, true);
        stateLock.readLock().lock();
        try {
            getChannel();
        } finally {
            stateLock.readLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void passiveClose() throws IOException {

    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public void transceive(Request request, Callback<Response> callback) throws IllegalStateException {
        // 注册callback回调，就是用来监控发送是否成功的
        handler.registerCallBack(request, callback);
        // 序列化request，发送byte数组
        writeDataPack(request);
    }

    /**
     * channel是否已经打开，并且已经绑定成功
     */
    private static boolean isChannelReady(Channel channel) {
        return (channel != null) && channel.isOpen() && channel.isActive();
    }

    private Channel getChannel() throws IllegalStateException {
        if (!isChannelReady(channel)) {
            // 如果channel没有开启，则需要重新建立连接，则升级为写锁（读请求也hang住）
            stateLock.readLock().unlock();
            stateLock.writeLock().lock();
            try {
                log.trace("Connecting to " + remoteAddr);
                ChannelFuture channelFuture = bootstrap.connect(remoteAddr);
                channelFuture.awaitUninterruptibly();
                channel = channelFuture.channel();
            } finally {
                stateLock.readLock().lock();
                stateLock.writeLock().unlock();
            }
        }
        return channel;
    }

    private void writeDataPack(Request request) throws IllegalStateException {
        Codec codec = TypeLocator.getInstance().getInstanceOfType(ProtostuffCodec.class);
        byte[] content;
        try {
            // 序列化
            content = codec.encode(RpcRequest.class, (RpcRequest) request);
        } catch (Throwable error) {
            throw new IllegalStateException("encode error", error);
        }
        // 读锁，除非channel重新连接，否则不会被hang住
        stateLock.readLock().lock();
        try {
            getChannel().writeAndFlush(Unpooled.wrappedBuffer(content));
        } finally {
            stateLock.readLock().unlock();
        }
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    public static void main(String[] args) {
        URI uri = new URI("van", "", "",
                "127.0.0.1", 8888, "/dlc/test", null);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodName("dlctest");
        rpcRequest.setId("666");
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setParameters(new Object[]{"param"});
        NettyClient nettyClient = new NettyClient(uri);
        nettyClient.connect(uri);
        nettyClient.transceive(rpcRequest, null);
    }
}
