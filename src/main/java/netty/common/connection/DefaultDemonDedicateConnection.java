package netty.common.connection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import netty.common.handler.inboud.DemonConnectionInboundEventHandler;
import netty.common.handler.inboud.DemonHexTraceInboundEventHandler;
import netty.common.message.DemonMessage;
import netty.common.message.DemonRequest;
import netty.common.message.DemonResponse;
import netty.common.stack.DemonStackConfiguration;
import netty.common.stack.DemonStackModel;
import netty.common.tracer.DemonTracer;
import netty.common.transaction.DemonTransaction;
import netty.common.transaction.DemonTransactionCreateEvent;
import netty.common.transaction.DemonTransactionManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DefaultDemonDedicateConnection extends
		ChannelInitializer<SocketChannel> implements DemonDedicateConnection,
		DemonDedicateConnectionProcessor {

	private static DemonTracer _tracer = DemonTracer
			.getInstance(DefaultDemonDedicateConnection.class);

	private String key;

	private DemonStackConfiguration config;

	private DemonTransactionManager transMgr;

	private DemonDedicateConnectionEvent connEvent;

	private DemonTransactionCreateEvent createEvent;

	private NioEventLoopGroup group;

	private Channel channel;

	public DefaultDemonDedicateConnection(String key,
			DemonStackConfiguration config, NioEventLoopGroup group) {
		this.key = key;
		this.config = config;
		this.group = group;
		this.transMgr = new DemonTransactionManager(this);
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return this.key;
	}

	@Override
	public void registerDemonConnectionEvent(DemonDedicateConnectionEvent event) {
		if (this.config.getModel() != DemonStackModel.Dedicate)
			throw new UnsupportedOperationException(
					"The stack's mode MUST be Dedicate.");
		this.connEvent = event;
	}

	@Override
	public void registerDemonTransactionCreated(
			DemonTransactionCreateEvent event) {
		if (config.getModel() != DemonStackModel.Dedicate)
			throw new UnsupportedOperationException(
					"The stack's mode MUST be Dedicate.");
		this.createEvent = event;
	}

	@Override
	public void connect(String ip, int port) {
		// TODO Auto-generated method stub
		this.connect(new InetSocketAddress(ip, port));
	}

	@Override
	public void connect(SocketAddress address) {
		// TODO Auto-generated method stub
		this.connect(address, null);
	}

	@Override
	public void connect(SocketAddress address, final Object attachment) {
		// TODO Auto-generated method stub
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class).handler(this)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60 * 1000);
		ChannelFuture f = b.connect(address);
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess())
					processConnectionConnected(future.channel(), attachment);
				else
					processDisconnectionConnected(attachment);

			}
		});

	}

	// 处理连接
	protected void processConnectionConnected(Channel channel, Object attachment) {
		channel = channel;
		if (connEvent != null)
			connEvent.onConnected(this, attachment);
	}

	protected void processDisconnectionConnected(Object attachment) {
		if (connEvent != null)
			connEvent.onDisconnected(this, attachment);
		transMgr.reset();
		channel = null;
	}

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline line = sc.pipeline();
		line.addLast(new DemonConnectionInboundEventHandler());
		if(this.config.getEnableHexTracer())
			line.addLast(new DemonHexTraceInboundEventHandler());
		
	}

	@Override
	public void disconnect() {
		disconnect(null);
	}

	@Override
	public void disconnect(final Object attachment) {
		if (channel == null)
			return;
		ChannelFuture f = channel.disconnect();
		f.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture arg0) throws Exception {
				// TODO Auto-generated method stub
				processDisconnectionConnected(attachment);
			}
		});

	}

	@Override
	public DemonTransaction createTransaction(DemonRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DemonTransaction createTransaction(DemonRequest req, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processSendMessage(final DemonMessage msg,
			final DemonTransaction trans) {
		// TODO Auto-generated method stub
		if (channel == null) {
			trans.doSendRequestFailed();
			return;
		}
		if (msg.isRequest())
			transMgr.addTransaction(trans);
		ChannelFuture f = channel.writeAndFlush(msg);
		f.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					if (msg.isRequest())
						transMgr.removeTransaction(trans.getKey());
					trans.doSendRequestFailed();
				}
			}
		});
	}

	@Override
	public void processReceiveRequest(DemonRequest req) {
		DemonTransaction trans = transMgr.createTransaction(req);
		trans.setDemonConnection(this);
	}

	@Override
	public void processReceiveResponse(DemonResponse resp) {
		// TODO Auto-generated method stub

	}

}
