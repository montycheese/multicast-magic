/* File: Logger.java
 * Authors: Montana Wong, Justin Tumale, Matthew Haneburger
 * Logs multicast messages from Coordinator to Participant to a specified file
 * Uses queue to temporarily store messages that the users will be able to see
 * then adds/writes them to the file
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.io.File;

public class Logger implements Runnable {
	private String fileName;
	private String message;
	private Queue <String> messageQueue;
	/*constructor for Logger; fileName and message to be written to file*/
	public Logger(String fileName, Queue<String> messageQueue){
		this.fileName = fileName;
		this.messageQueue = messageQueue;
	}
	
	/*calls writeToFile method and performs the necessary operations*/
	@Override
	public void run() {
		this.writeToFile();
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	/*This method was written solely using Java API*/
	
	/*
	 * @param none
	 * This method constructs a stream to write to a file using a buffer.
	 * If the file exists, it appends to it. If is not in the current working
	 * directory, it creates it and adds text to it.
	 * */
	private void writeToFile(){
		//take the message passed in constructor and append to the file listed above.
		// if the file doesn't exist yet, create it, other wise append to it.
		//using bufferedWriter in case the messages get very large
		File newFile = new File(fileName);
		if(!newFile.exists()){
			try{
			newFile.createNewFile();
			}
			catch (IOException ioe){
				ioe.printStackTrace();
			}
		}
		
		try(      
				/* FileWriter(File file)
				 * This constructs a FileWriter object given a File object.
				 * */
				  FileWriter file = new FileWriter(fileName, true);
				  /*Writes text to a character-output stream, buffering characters 
				   *so as to provide for the efficient writing of single characters,
				   *arrays, and strings.*/
		          	  BufferedWriter bufferedWriter = new BufferedWriter(file);
				  /*Prints formatted representations of objects to a text-output stream*/
				  PrintWriter printer = new PrintWriter(bufferedWriter))
				{
			//prints to file
			//System.out.println("message to log from logger : " + this.messageQueue.peek());
			while(!this.messageQueue.isEmpty()){
				this.message = this.messageQueue.remove();
				printer.println(this.message);
			}
		  }  //throws exception if error should occur
		  catch( IOException ioe ){
		      ioe.printStackTrace();
		  }
		/*Method Unit Tested*/
	}
	
}
