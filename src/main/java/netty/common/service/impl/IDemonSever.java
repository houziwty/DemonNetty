package netty.common.service.impl;

import io.netty.channel.EventLoopGroup;

public interface IDemonSever {
     void run(EventLoopGroup bossGroup,EventLoopGroup workerGroup)throws Exception;
     void close()throws Exception;
}
