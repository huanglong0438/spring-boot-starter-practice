package com.dc.boynextdoor.remoting.server;

import com.dc.boynextdoor.common.Requestor;
import com.dc.boynextdoor.common.Response;
import com.dc.boynextdoor.common.codec.Codec;
import com.dc.boynextdoor.common.codec.protostuff.ProtostuffCodec;
import com.dc.boynextdoor.common.ext.TypeLocator;
import com.dc.boynextdoor.remoting.core.CallFuture;
import com.dc.boynextdoor.remoting.core.RpcRequest;
import com.dc.boynextdoor.remoting.core.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * NettyServerHander
 *
 * @title NettyServerHander
 * @Description
 * @Author donglongcheng01
 * @Date 2019-09-26
 **/
@ChannelHandler.Sharable
@Slf4j
public class NettyServerHander extends ChannelInboundHandlerAdapter {

    private ConcurrentHashMap<String, Requestor<?>> requestorMap = new ConcurrentHashMap<>();

    private final NettyServer server;

    private final Codec codec = TypeLocator.getInstance().getInstanceOfType(ProtostuffCodec.class);

    private static final String ERROR_MSG = "server decode error, api.jar not compatible " +
            "or contains eg.HashMap.keySet()";

    public NettyServerHander(NettyServer server) {
        this.server = server;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf buffer = (ByteBuf) msg;
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.getBytes(0, bytes);
            RpcRequest request;
            try {
                request = codec.decode(RpcRequest.class, bytes);
            } catch (Throwable error) {
                log.warn(ERROR_MSG, error); // api不兼容是在这里反序列化抛异常的
                return;
            }

            String serviceKey = request.getUri().getServiceKey();
            Assert.hasText(serviceKey, "Service key is empty");
            Assert.notNull(serviceKey, "Service key is null");
            Requestor<?> requestor = requestorMap.get(serviceKey);
            CallFuture<Response> callFuture = new CallFuture<>();
            try {
                // 1. Server在这里会调用本地的impl执行
                requestor.request(request, callFuture);
            } catch (IllegalStateException e) {
                callFuture.handleResult(new RpcResponse(request.getId(), null, e));
            }
            // 2. 然后会在这里等待本地执行的结果
            Response response = callFuture.get();
            try {
                // 3. 在这里把响应的结果序列化成Response，然后写入channel
                byte[] content = codec.encode(RpcResponse.class, (RpcResponse) response);
                ctx.channel().writeAndFlush(Unpooled.wrappedBuffer(content));
            } catch (Throwable error) {
                log.warn("encode error", error);
                return;
            }
        } catch (Throwable ex) {
            log.warn("unexpect error: " + ex.getMessage(), ex);
            ctx.channel().close();
        }
    }

}
