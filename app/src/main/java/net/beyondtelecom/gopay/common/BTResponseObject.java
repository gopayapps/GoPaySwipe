package net.beyondtelecom.gopay.common;

/**
 * User: tkaviya
 * Date: 3/27/2015
 * Time: 7:47 PM
 */
public class BTResponseObject<T>
{
	BTResponseCode responseCode;
	T responseObject = null;

	public BTResponseObject(BTResponseCode responseCode, T responseObject) {
		this.responseCode = responseCode;
		this.responseObject = responseObject;
	}

	public BTResponseObject(BTResponseCode responseCode)
	{
		this.responseCode = responseCode;
	}

    public int getCode() {  return responseCode.code; }

	//Override default message with custom message. Return this to allow method chaining
    public BTResponseObject<T> setMessage(String responseMessage) { responseCode.message = responseMessage; return this; }

	public String getMessage() {   return responseCode.message;  }

	public BTResponseCode getResponseCode() { return responseCode; }

	//Change response code for this.btResponseObject. Return this to allow method chaining
	public BTResponseObject<T> setResponseCode(BTResponseCode responseCode) { this.responseCode = responseCode; return this; }

	public T getResponseObject()
	{
		return responseObject;
	}

	//Change object for this.btResponseObject. Return this to allow method chaining
	public BTResponseObject<T> setResponseObject(T responseObject) { this.responseObject = responseObject; return this; }
}
