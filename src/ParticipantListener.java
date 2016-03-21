/*File: ParticipantListener.java
 * Authors: Montana Wong, Justin Tumale, Matthew Haneburger
 * Listens to commands coming from Coordinator. Runs on an infinite loop.
 * 
 * */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticipantListener extends Thread{
	
	private int listenPort;
	private ServerSocket sock;
	private Socket coordinatorSocket;
	private ExecutorService threadPool;
	private String logFileName;
	
	public ParticipantListener(int listenPort, String logFileName, ExecutorService threadPool){
		this.listenPort = listenPort;
		this.threadPool = threadPool;
		this.logFileName = logFileName;
	}
	
	@Override
	public void run(){
		try {
		this.sock = new ServerSocket(this.listenPort);
		System.out.println("Listener running on port: " + this.listenPort);
			while(true){
					this.coordinatorSocket = this.sock.accept();
					//TODO HANDLE request with a new custom thread. I called it Logger
					//Receive message from coordinator here
					//create logger thread
					Logger logger = new Logger(this.logFileName);
					this.threadPool.execute(logger);
					
					//may reconsider this design chice for queue message send
					if (this.coordinatorSocket != null){
						this.coordinatorSocket.close();
					}	
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
