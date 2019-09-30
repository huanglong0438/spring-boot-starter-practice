package com.dc.boynextdoor.remoting.server;

import com.dc.boynextdoor.common.URI;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * NettyServerInitializer
 *
 * @title NettyServerInitializer
 * @Description
 * @Author donglongcheng01
 * @Date 2019-08-29
 **/
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelInboundHandlerAdapter handler;


    public NettyServerInitializer(URI uri, ChannelInboundHandlerAdapter handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 看源码可以发现，addLast实际上是插到倒数第二了
        socketChannel.pipeline()
                // inbound, 收到client的请求后黏包
                .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4))
                // outbound，加头部
                .addLast("frameEncoder", new LengthFieldPrepender(4))
                // inbound，收到client请求后反序列化，并且进行处理
                .addLast("handler", handler);
    }
}
