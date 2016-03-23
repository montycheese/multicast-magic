import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Coordinator {
	
	private Hashtable<Integer, Participant> multicastGroup;
	private Hashtable<Integer, LinkedList<Message>> messageBuffer;
	private ExecutorService threadPool;
	private long threshold;
	private int portNum;
	private int backlog;
	private ServerSocket sock;
	
	public Coordinator(int portNum, long persistenceTimeThreshold){
		this.multicastGroup = new Hashtable<>();
		this.messageBuffer = new Hashtable<>();
		this.portNum = portNum;
		this.backlog = 20;
		this.threshold = persistenceTimeThreshold;
		this.threadPool = Executors.newCachedThreadPool();
	}
	
	public void run() throws IOException{
		System.out.println("Coordinator running.");
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
					this.portNum,
					this.multicastGroup,
					this.messageBuffer,
					this.threshold
			);
			this.threadPool.execute(worker);
		}while(true);
				
	}
	
	@Override
	public String toString(){
		return String.format("port: %d, threshold: %d seconds", this.portNum, this.threshold);
	}
	
	public static Coordinator createFromFile(String filename) throws IllegalArgumentException, FileNotFoundException{
		File file = new File(filename);
		int port = -1;
		int persistenceTimeThreshold = -1;
		//try with resources
		try(Scanner scanner = new Scanner(file);){
			while(scanner.hasNext()){
				port = scanner.nextInt();
				persistenceTimeThreshold = scanner.nextInt();
			}
		}
		
		if(port > 65535 || port < 0 || persistenceTimeThreshold < 0){
			throw new IllegalArgumentException();
		}
		return new Coordinator(port, persistenceTimeThreshold);
	}
	

	public static void main(String[] args) {
		boolean DEVELOPMENT = false;
		
		if(args.length != 1 && DEVELOPMENT == false){
			System.out.println("The program should be run as so: java Coordinator [config.txt]");
			System.exit(0);
		}
		
		String configFilePath = (DEVELOPMENT) ? "config/PP3-coordinator-conf.txt" : args[0];
		Coordinator c = null;
		
		//create coordinator from config file
		try {
			c = Coordinator.createFromFile(configFilePath);
		} 
		catch (FileNotFoundException e) {
			System.out.println("Cannot find file: " + configFilePath);
			System.exit(0);
		}
		catch(IllegalArgumentException iae){
			System.out.println("Please check the parameters of the configuration file.");
			System.exit(0);
		}
		
		try {
			System.out.println(c.toString());
			c.run();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error binding to port, port may be in use. Possible socket error");
		}
		System.out.println("Coordinator shutdown.");
		
	}

}
