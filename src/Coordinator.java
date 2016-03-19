import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Coordinator {
	
	private HashMap<Integer, Participant> multicastGroup;
	private HashMap<Integer, Message[]> messageBuffer;
	private ExecutorService threadPool;
	private long threshold;
	private int portNum;
	private int backlog;
	private ServerSocket sock;
	
	public Coordinator(int portNum, long persistenceTimeThreshold){
		this.multicastGroup = new HashMap<>();
		this.messageBuffer = new HashMap<>();
		this.portNum = portNum;
		this.backlog = 20;
		this.threshold = persistenceTimeThreshold;
		this.threadPool = Executors.newCachedThreadPool();
	}
	
	public void run() throws IOException{
		this.sock = new ServerSocket(
				this.portNum,
				this.backlog
		);
		try{
			this.listen();
		}
		finally{
			this.sock.close();
		}
		
	}
	
	public void listen(){
		do{
			Socket clientSocket = this.sock.accept();
			this.threadPool.execute(coordinatorThread);
		}while(true);
				
	}
	
	
	private void sendAll(String message){
		
	}
	

	public static void main(String[] args) {
		String configFilePath = "TODO.txt";
		
		
		

	}

}
