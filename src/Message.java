/* File: Message.java
 * Authors: Montana Wong, Justin Tumale, Matthew Haneburger
 * Constructs message to be sent with start time
 * */
import java.util.Date; //use for create time
public class Message {
	private String message;
	private long createTime;
	
	//constructor
	public Message(String message){
		createTime = System.nanoTime();
		this.message = message;
	}
	//get message
	public String getMessage(){
		return message;
	}
	//returns creation time on message
	public long getCreateTime(){
		return createTime;
	}
	//get date timestamp
	public static String getCurrentTimestamp(){
		Date date = new Date();
		String timestamp = String.format("%tT\n", date);
		return timestamp;
	}
	//prints message 
	public String toString(){
		return this.message;
	}
}
