import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Coordinator {
	
	private HashMap<Integer, Participant> multicastGroup;
	private HashMap<Integer, LinkedList<Message>> messageBuffer;
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
	
	public void listen() throws IOException{
		do{
			Socket clientSocket = this.sock.accept();
			CoordinatorThread worker = new CoordinatorThread(this.sock,
					clientSocket,
					this.multicastGroup,
					this.messageBuffer,
					this.threshold
			);
			this.threadPool.execute(worker);
		}while(true);
				
	}
	
	
	

	public static void main(String[] args) {
		if(args.length != 1){
			System.out.println("The program should be run as so: java Coordinator [config.txt]");
			System.exit(0);
		}
		String configFilePath = args[1];
		
	}

}
