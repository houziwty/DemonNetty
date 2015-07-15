package netty.common.transaction;

import java.util.concurrent.atomic.AtomicLong;

import netty.common.message.DemonRequest;
import netty.common.message.DemonResponse;
import netty.common.tracer.DemonTracer;

public class DemonTransaction {

	private DemonRequest _request;
	private DemonResponse _response;
	private String _key;
	private boolean _isFake;
	private long _updatetime;
	private DemonTransaction _stateTransaction;
	private Object _stateObject;
	private DemonTransactionManager _maager;
	public DemonTransactionEvent  TransactionEvent;
	private static AtomicLong cseq=new AtomicLong();
	private static DemonTracer tracer=DemonTracer.getInstance(DemonTransaction.class,true);
	
	private DemonTransaction(DemonRequest request){
		_request=request;
		_request.setParentTrans(this);
		_updatetime=System.currentTimeMillis();
		
	}
	public static DemonTransaction createFakeTransaction(DemonRequest request){
		DemonTransaction trans=new DemonTransaction(request);
		trans._isFake=true;
		return trans;
	}
	
	DemonTransaction(DemonRequest request,boolean direction,DemonTransactionManager manager){
		this(request);
		
	}
}
