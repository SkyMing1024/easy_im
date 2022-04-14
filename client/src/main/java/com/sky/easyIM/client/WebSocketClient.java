package com.sky.easyIM.client;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.net.URI;

@Slf4j
public class WebSocketClient {
    private URI uri;

    private Bootstrap bootstrap;
    private EventLoopGroup group;

    private ChannelPromise channelPromise;

    private Channel channel;

    public WebSocketClient(URI uri) {
        this.uri = uri;
        this.init();
    }

    private void init(){
        bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.option(ChannelOption.TCP_NODELAY,true);

        group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(64*1024));
                        pipeline.addLast(new WebSocketHandler(getHandshaker(uri)));
                    }
                    private WebSocketClientHandshaker getHandshaker(final URI uri) {
                        return WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, null);
                    }
                });

    }

    public void connect(){
        try {
            channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
            channelPromise.sync();
            log.info("连接成功！ 完成hand shake");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class WebSocketHandler extends SimpleChannelInboundHandler<Object>{
        private WebSocketClientHandshaker handshaker;

        public WebSocketHandler(WebSocketClientHandshaker handshaker) {
            this.handshaker = handshaker;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            channelPromise = ctx.newPromise();
        }


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            this.handshaker.handshake(ctx.channel());
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
            log.info("收到消息：{}, 来自：{}",o,ctx.channel().remoteAddress());
            if (!handshaker.isHandshakeComplete()){
                try {
                    handshaker.finishHandshake(ctx.channel(), ((FullHttpResponse) o));
                    channelPromise.setSuccess();
                    log.info("handshake success");
                }catch (WebSocketHandshakeException e){
                    e.printStackTrace();
                    channelPromise.setFailure(e);
                }
                return;
            }

            if ( !(o instanceof TextWebSocketFrame) ) {
                log.warn("收到了错误类型的信息：{}",o);
                return ;
            }


            TextWebSocketFrame request = (TextWebSocketFrame) o;
            log.info("收到信息：{}",request.text());
            Action action;
            try {
                action = JSONObject.parseObject(request.text(), Action.class);
            }catch (Exception e){
                e.printStackTrace();
                log.error("JSON 对象转换失败");
                return;
            }

            // 处理时间

        }
    }

}
