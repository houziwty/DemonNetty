package netty.common.message;

import java.io.Serializable;
import java.util.ArrayList;

import netty.common.util.DemonLinkedList;
import netty.common.util.DemonLinkedNode;

public class DemonMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5471528125749414349L;

	private DemonLinkedList<DemonHeader> _headers;
	private DemonLinkedList<DemonBody> _bodys;
	private DemonMessageType _messageType;
	private byte _method;
	private transient DemonTransaction _parentTrans;

	public DemonHeader From;
	public DemonHeader To;
	public DemonHeader CallId;
	public DemonHeader Csequence;
	public DemonHeader Fpid;
	public DemonHeader Tpid;
	public DemonHeader Event;

	public DemonMessage(byte method) {
		this._method = method;
		this._messageType = ((this._method | 0x7F) == 0x7F) ? DemonMessageType.Request
				: DemonMessageType.Response;
		this._headers = new DemonLinkedList<DemonHeader>();
		this._bodys = new DemonLinkedList<DemonBody>();
	}

	public void addBody(DemonBody body) {
		if (body != null)
			body.Node = this._bodys.put(body);
	}

	public void addBody(byte[] bytes) {
		if (bytes != null) {
			DemonBody body = new DemonBody(bytes);
			body.Node = this._bodys.put(body);
		}
	}

	public void addBodys(ArrayList<DemonBody> bodys) {
		for (DemonBody body : bodys) {
			body.Node = this._bodys.put(body);
		}
	}

	public void addHeader(DemonHeader header) {
		if (header != null) {
			if (header.Node != null)
				header = new DemonHeader(header.getType(), header.getValue());
			switch (header.getType()) {
			case DemonHeaderType.From: {
				header.Node = this._headers.put(header);
				this.From = header;
			}
				break;
			case DemonHeaderType.To: {
				header.Node = this._headers.put(header);
				this.To = header;
			}
				break;
			case DemonHeaderType.CallId: {
				header.Node = this._headers.put(header);
				this.CallId = header;
			}
				break;
			case DemonHeaderType.Csequence: {
				header.Node = this._headers.put(header);
				this.Casequence = header;
			}
				break;
			case DemonHeaderType.Fpid: {
				header.Node = this._headers.put(header);
				this.Fpid = header;
			}
				break;
			case DemonHeaderType.Tpid: {
				header.Node = this._headers.put(header);
				this.Tpid = header;
			}
				break;
			case DemonHeaderType.Event: {
				header.Node = this._headers.put(header);
				this.Event = header;
			}
				break;
			default: {
				header.Node = this._headers.put(header);
			}
				break;
			}
		}
	}

	public DemonTransaction get_parentTrans() {
		return _parentTrans;
	}

	public void set_parentTrans(DemonTransaction _parentTrans) {
		this._parentTrans = _parentTrans;
	}

	public synchronized DemonBody getBody() {
		try {
			_bodys.moveToHead();
			return _bodys.get().obj();
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized ArrayList<DemonBody> getBodys() {
		ArrayList<DemonBody> ret = new ArrayList<DemonBody>();
		_bodys.moveToHead();
		DemonLinkedNode<DemonBody> bodyNode = null;
		while ((bodyNode = _bodys.get()) != null) {
			ret.add(bodyNode.obj());
		}
		return ret;
	}

	public synchronized DemonHeader getHeader(byte headerType) {
		_headers.moveToHead();
		DemonLinkedNode<DemonHeader> _headerNode = null;
		while ((_headerNode = _headers.get()) != null) {
			if (_headerNode.obj().isTypeOf(headerType))
				return _headerNode.obj();
		}
		return null;
	}

	public synchronized ArrayList<DemonHeader> getHeaders() {
		ArrayList<DemonHeader> ret = new ArrayList<DemonHeader>();
		_headers.moveToHead();
		DemonLinkedNode<DemonHeader> _headerNode = null;
		while ((_headerNode = _headers.get()) != null) {
			ret.add(_headerNode.obj());
		}
		return ret;
	}

	public synchronized ArrayList<DemonHeader> getHeaders(byte headerType) {
		ArrayList<DemonHeader> ret = new ArrayList<DemonHeader>();
		_headers.moveToHead();
		DemonLinkedNode<DemonHeader> _headerNode = null;
		while ((_headerNode = _headers.get()) != null) {
			if (_headerNode.obj().isTypeOf(headerType))
				ret.add(_headerNode.obj());
		}
		return ret;
	}
	public String getKey(boolean diretion){
		StringBuilder sb=new StringBuilder();
		for(DemonHeader header:new DemonHeader[]{this.From, this.To, this.CallId, this.Csequence, this.Fpid, this.Tpid}){
			if(header!=null&&header.isNotNullValue()){
				if(header.getType()!=DemonHeaderType.Fpid&&header.getType()!=DemonHeaderType.Tpid)
					sb.append(header.getInt64());
				else
					sb.append(header.getHexString());
			}
			sb.append("-");
		}
		sb.append(diretion);
		return sb.toString();
	}
}
