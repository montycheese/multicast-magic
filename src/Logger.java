
/**
 * Logs multicast messages from Coordinator to Participant to a specified file
 * @author montanawong
 *
 */
public class Logger implements Runnable {
	private String fileName;
	private String message;
	
	public Logger(String fileName, String message){
		this.fileName = fileName;
		this.message = message;
	}

	@Override
	public void run() {
		this.writeToFile();
		
	}
	
	
	private void writeToFile(){
		//take the message passed in constructor and append to the file listed above.
		// if the file doesn't exist yet, create it, other wise append to it.
	}
}
