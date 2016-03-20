import java.util.*;
import java.io.IOException;
import java.net.*;
import java.io.File;


public class Participant {
	
	protected int ID;
	private String IP_coordinator;
	protected int portCoordinator;
	protected boolean isOnline;
	private Socket participantSocket = null;
	private ParticipantListener listenerCoordinator = null;
	private String command = null;
	private String logfileName = null;
	
	public Participant(int ID, String IP_coordinator, int portCoordinator, boolean isOnline){
		this.ID = ID;
		this.IP_coordinator = IP_coordinator;
		this.portCoordinator = portCoordinator;
		this.isOnline = isOnline;
	}
	
	public void run(){
		try {
			//this.participantSocket = new Socket(this.IP_coordinator, this.portCoordinator);
			//run on local host for now
			this.participantSocket = new 
					Socket(InetAddress.getLocalHost().getHostName(), this.portCoordinator);
			
			//create the multicast listener thread
			this.listenerCoordinator = new ParticipantListener(this.participantSocket);
			//start threads
			this.listenerCoordinator.start();
			//retrieve a command from the user

			Scanner in;
			while (this.command != "quit"){
				System.out.println("Enter command:" );
				in = new Scanner(System.in);
				command = in.nextLine();
				
				//create the user thread
				ParticipantThread userCommandThread = new ParticipantThread(this.ID, this.IP_coordinator, 
						this.portCoordinator, this.isOnline);
				userCommandThread.start();		
				try {
					userCommandThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
			this.listenerCoordinator.participantSocket.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public static Participant configurationParser(String[] args){

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
		String _logFileName = null;
		String _IP_Coordinator = null;
		String _portCoordinator = null;
		String _ipAndPortString = null;
		try{
			
			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()){
				_ID = scanner.nextInt();
				_logFileName = scanner.next();
				_ipAndPortString = scanner.next();
			}
			scanner.close();
			
			String[] ipAndPortArray =  _ipAndPortString.split(":");
			_IP_Coordinator = ipAndPortArray[0];
			_portCoordinator = ipAndPortArray[1];
			

		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		/*
		
		
		String address = "192.123.324.123:4567";
		String[] portArray = address.trim().split(":");
		String portNumber = portArray[1];
		
		*/

		//checking
		System.out.println(_ID);
		System.out.println(_logFileName);
		System.out.println(_IP_Coordinator);
		System.out.println(_portCoordinator);
		
		return new Participant(_ID, _IP_Coordinator, port, true);
	}
		
	public static void main(String[] args) {
		Participant P1 = Participant.configurationParser(args);
		P1.run();

	
	}

}
