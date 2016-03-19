/* File: Message.java
 * Authors: Montana Wong, Justin Tumale, Matthew Haneburger
 * Constructs message to be sent with start time
 * */
import java.util.Date; //use for create time
public class Message {
	
	//private SimpleDateFormat date;
	private String message;
	private long createTime;
	
	//constructor
	Message(String message){
		createTime = System.nanoTime();
		this.message = message;
	}
	//get message
	String getMessage(){
		return message;
	}
	//get createTime; probably won't be needed
	long getCreateTime(){
		return createTime;
	}
	//get date timestamp
	String getTimestamp(){
		Date date = new Date();
		String timestamp = String.format("%tT\n", date);
		return timestamp;
	}
	
	public String toString(){
		return this.message;
	}
	
	/*
	 * Whiteboard Reference:
	 * if(System.nanoTime() - m1.getCreateTime() <= persistenceTimeThreshold)
	 * {
	 * send(m1, p1); -> means we have not yet passed the treshold, therefore 
	 * 				 -> we can receive out messages
	 * }
	 * */
	
}
