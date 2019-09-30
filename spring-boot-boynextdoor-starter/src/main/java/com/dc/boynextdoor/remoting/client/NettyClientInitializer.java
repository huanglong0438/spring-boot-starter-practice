package com.dc.boynextdoor.remoting.client;

import com.dc.boynextdoor.common.URI;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * NettyClientInitializer
 *
 * @title NettyClientInitializer
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-29
 **/
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandlerAdapter handler;

    public NettyClientInitializer(URI uri, ChannelHandlerAdapter handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // inbound，在收到服务端的结果后【黏包】
        socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                Integer.MAX_VALUE, 0, 4, 0, 4));
        // outbound，会在write的时候在头部加上长度(用于服务端黏包)
        socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
        // inbound，收到服务端的响应后处理结果
        socketChannel.pipeline().addLast("handler", handler);
    }
}
