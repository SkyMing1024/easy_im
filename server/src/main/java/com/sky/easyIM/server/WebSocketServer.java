package com.sky.easyIM.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
public class WebSocketServer {

    private ServerBootstrap bootstrap;

    // 接收连接的线程
    private EventLoopGroup boss;
    // 处理线程
    private EventLoopGroup worker;

    @Setter
    private String contextPath;

    public WebSocketServer(String contextPath) {
        this.contextPath = contextPath;
    }

    public void start(final short port){
        this.init();
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("websocket server 启动成功：listen on:{}",port);

            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        bootstrap = new ServerBootstrap();
        // 设置TCP 连接参数
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_BACKLOG,1024);

        // 初始化线程池
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup(5);

        // bootstrap配置
        bootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // Http编解码
                        pipeline.addLast(new HttpServerCodec());
                        // 分片写入
                        pipeline.addLast(new ChunkedWriteHandler());
                        // 每一帧 64字节
                        pipeline.addLast(new HttpObjectAggregator(64*1024));
                        // netty自带的处理器， 处理websocket握手协议，ping/pong等
                        pipeline.addLast(new WebSocketServerProtocolHandler(contextPath));
                        // 自定义处理器
                        pipeline.addLast(new WebSocketHandler());

                    }
                });

    }

    private class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
            log.info("收到消息：{}, 来自：{}",o,ctx.channel().remoteAddress());
            if ( !(o instanceof TextWebSocketFrame)){
                log.warn("收到错误类型信息：{}",o);
                return;
            }
            TextWebSocketFrame text = (TextWebSocketFrame) o;
            log.info("收到消息内容：{}, 来自：{}",text.text(),ctx.channel().remoteAddress());
            ctx.writeAndFlush(new TextWebSocketFrame("服务端返回："+text.text()));

        }

        // 建立连接
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            log.info("连接建立，地址：{}",ctx.channel().remoteAddress());
        }

        // 断开连接
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            log.info("连接断开，地址：{}",ctx.channel().remoteAddress());
        }


    }


}
