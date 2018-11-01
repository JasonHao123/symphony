package jason.app.symphony.commons.http.response;

import jason.app.symphony.commons.http.model.ResponseHeader;

public class ServiceResponse<T> {
	private ResponseHeader header;
	private T body;
	public ResponseHeader getHeader() {
		return header;
	}
	public void setHeader(ResponseHeader header) {
		this.header = header;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
	
	
}
