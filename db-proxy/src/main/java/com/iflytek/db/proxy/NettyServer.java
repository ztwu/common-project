package com.iflytek.db.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

public class NettyServer {
    public  static Map<String, ChannelHandlerContext> map = new HashMap<String, ChannelHandlerContext>();
    private static final int port = 43306;
    public void run() throws Exception{
        //NioEventLoopGroup是用来处理IO操作的多线程事件循环器
        EventLoopGroup bossGroup  = new NioEventLoopGroup();  // 用来接收进来的连接
        EventLoopGroup workerGroup  = new NioEventLoopGroup();// 用来处理已经被接收的连接
        try{
            ServerBootstrap server =new ServerBootstrap();//是一个启动NIO服务的辅助启动类
            server.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)  // 这里告诉Channel如何接收新的连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 自定义编解码器
                            ch.pipeline().addLast(new MessagePacketDecoder());
                            ch.pipeline().addLast(new MessagePacketEncoder());
                            // 自定义处理类
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            //标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
            server.option(ChannelOption.SO_BACKLOG, 1024);
            // 是否启用心跳保活机机制
            server.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = server.bind(port).sync();// 绑定端口，开始接收进来的连接
            System.out.println("服务端启动成功...");
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully(); //关闭EventLoopGroup，释放掉所有资源包括创建的线程
            workerGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        new NettyServer().run();
    }
}