/*File: Participant.java
 *Authors: Montana Wong, Justin Tumale, Matthew Haneburger
 *Description: Participant of the coordinator pool. Sends and receives messages
 *within specified time from coordinator. Runs with only one argument [filename.txt]
 *and parses it to obtain the IP address, Port number, ID, and the file name that 
 *will log all of the messages.
 * */
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.BufferedReader;
import java.io.File;

//description above
public class Participant {
	
	protected int ID;
	private String IP_coordinator;
	private int coordinatorPort;
	protected int listenPort;
	protected boolean isOnline;
	private Socket coordinatorSocket = null;
	private ParticipantListener listenerCoordinator = null;
	private String input = "";
	private String logfileName = null;
	private String myIPAddress= null;
	private PrintWriter out;
	private BufferedReader in;
	private ExecutorService threadPool = Executors.newCachedThreadPool();;
	
	public Participant(int ID, String logfileName, String IP_coordinator, int coordinatorPort, boolean isOnline){
		this.ID = ID;
		this.logfileName = logfileName;
		this.IP_coordinator = IP_coordinator;
		this.coordinatorPort = coordinatorPort;
		this.isOnline = isOnline;
		try {
			InetAddress ipAddress = InetAddress.getLocalHost();
			this.myIPAddress = ipAddress.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	

	//Overloaded constructor for a registered Participant with an elected listenPort
	public Participant(int ID, String IP_coordinator, int coordinatorPort, int listenPort, boolean isOnline){
		this.ID = ID;
		this.IP_coordinator = IP_coordinator;
		this.coordinatorPort = coordinatorPort;
		this.isOnline = isOnline;
		this.listenPort = listenPort;
		try {
			InetAddress ipAddress = InetAddress.getLocalHost();
			this.myIPAddress = ipAddress.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
		
	public void run(){

			//refactor--montana need to set listenPort after first register command
			//this.listenerCoordinator = new ParticipantListener(this.listenPort, this.logfileName, this.threadPool);
			//this.threadPool.execute(this.listenerCoordinator);
			
			//retrieve a command from the user
			Scanner in;

			while (true){
				System.out.println(">>Enter command:" );
				in = new Scanner(System.in);
				input = in.nextLine();
				if(input.equalsIgnoreCase("quit")){
					break;
				}
				
				//parse the input for a command and a message
				String command = null;
				String message = null;
				String [] commandAndMessage = this.input.split(" ", 2);
				if (commandAndMessage.length == 1){
					command = commandAndMessage[0];
					
					//check for incorrect uses
					if(!(command.equalsIgnorecase('disconnect') ||
						command.equalsIgnoreCase('deregister'))){
							System.out.println("Please enter a valid command");
							continue;
					}
					
					//if disconnect shutdown the socket on listener and kill the thread
					if(command.equalsIgnoreCase("disconnect")){
						try {
							this.listenerCoordinator.shutdown();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					

				}
				else if (commandAndMessage.length == 2){
					command = commandAndMessage[0];
					message = commandAndMessage[1];
					
					//check for incorrect uses
					if((command.equalsIgnorecase('disconnect') ||
						command.equalsIgnoreCase('deregister'))){
							System.out.println("Please enter a valid command");
							continue;
						}
					
					//If the participant is registering for the first time
					if(command.equalsIgnoreCase("Register") && Integer.valueOf(message) != this.listenPort){
						this.listenPort = Integer.valueOf(message);
						this.listenerCoordinator = new ParticipantListener(this.listenPort, this.logfileName, this.threadPool);
						this.threadPool.execute(this.listenerCoordinator);
					}
					//if participant is reconnecting - create new listener on new port
					else if (command.equalsIgnoreCase("reconnect")){
						this.listenPort = Integer.valueOf(message);
						this.listenerCoordinator = new ParticipantListener(this.listenPort, this.logfileName, this.threadPool);
						this.threadPool.execute(this.listenerCoordinator);
					}
				}
				else{
					System.out.println("Please enter a valid command");
					continue;
				}
				
				//create the user thread
				ParticipantThread userCommandThread = new ParticipantThread(
						this.ID, 
						this.listenPort,
						this.coordinatorPort,
						this.isOnline,
						this.myIPAddress,
						command,
						message);		
				//execute thread
				this.threadPool.execute(userCommandThread);
				try {
					userCommandThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
			};
			in.close();

			System.out.println("Participant shutting down.");
			//this.listenerCoordinator.participantSocket.close();
		} 
	
	@Override
	public String toString(){
		return String.format(
				"Participant {id : %d, ip : %s, listenPort : %d}",
				this.ID, 
				this.myIPAddress, 
				this.listenPort
			);
	}
		
	public static Participant configurationParser(String[] args) throws FileNotFoundException{

		//usage checking
		if(args.length != 1){
			System.out.println("Please enter a valid file name as the first parameter.");
			System.out.println("Usage: [filename.txt]");
		}
		//set equal to first (and only) argument
		String inputFileName = args[0];
		//creates new file
		File file = new File(inputFileName);
		
		
		//TODO GET PORT
		int port = -1;
		int _ID = -1;
		String _logfileName = null;
		String _IP_Coordinator = null;
		String _portCoordinator = null;
		String _ipAndPortString = null;

		try(Scanner scanner = new Scanner(file);){
			while(scanner.hasNext()){
				_ID = scanner.nextInt();
				_logfileName = scanner.next();
				_ipAndPortString = scanner.next();
				break;
			}

			String[] ipAndPortArray =  _ipAndPortString.trim().split(":");
			_IP_Coordinator = ipAndPortArray[0];
			_portCoordinator = ipAndPortArray[1];
			port = Integer.valueOf(_portCoordinator);
		}

		return new Participant(_ID, _logfileName, _IP_Coordinator, port, true);
	}
		
	public static void main(String[] args) {
		boolean DEVELOPMENT = true;
		if(DEVELOPMENT == true){
			Participant P1;
			try {
				P1 = Participant.configurationParser(new String[]{"config/1001-message-log.txt"});
				P1.run();
			} catch (FileNotFoundException e) {
				System.out.println("File not found.");
			}
		
		}
		else{
			
			Participant P1;
			try {
				P1 = Participant.configurationParser(args);
				P1.run();
			} catch (FileNotFoundException e) {
				System.out.println("File not found.");
			}
			
		}
	}

}
