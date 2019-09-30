package com.dc.boynextdoor.remoting.client;

import com.alibaba.fastjson.JSON;
import com.dc.boynextdoor.common.Callback;
import com.dc.boynextdoor.common.Request;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.URI;
import com.dc.boynextdoor.common.codec.Codec;
import com.dc.boynextdoor.common.codec.protostuff.ProtostuffCodec;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.remoting.core.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * NettyClientHandler
 *
 * @title NettyClientHandler
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-27
 **/
@ChannelHandler.Sharable
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final Codec codec = TypeLocator.getInstance().getInstanceOfType(ProtostuffCodec.class);

    private static final String ERROR_MSG = "client decode error";

    private final URI uri;

    public NettyClientHandler(URI uri) {
        this.uri = uri;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse response;
        ByteBuf buffer = (ByteBuf) msg;
        try {
            response = codec.decode(RpcResponse.class, buffer.array());
        } catch (Throwable error) {
            log.warn(ERROR_MSG, error);
            return;
        }

        if (response == null) {
            log.warn("rpc resposne corrupted.");
            return;
        }

        log.info(JSON.toJSONString(response));

        // todo callback
    }

    public void registerCallBack(Request request, Callback<Response> callback) {

    }
}
