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

			this.participantSocket = new Socket(this.IP_coordinator, this.portCoordinator);
			
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
		
	public void configuration_parser(String[] args){

		//usage checking
		if(args.length != 1){
			System.out.println("Please enter a valid file name as the first parameter.");
			System.out.println("Usage: [filename.txt]");
		}
		//set equal to first (and only) argument
		String inputFileName = args[0];
		//creates new file
		File file = new File(inputFileName);
		try{
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext()){
			this.ID = scanner.nextInt();
			logfileName = scanner.next();
			this.IP_coordinator = scanner.next();
		}
		scanner.close();

		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		//checking
		System.out.println(this.ID);
		System.out.println(this.logfileName);
		System.out.println(this.IP_coordinator);
		
	}
		
	public static void main(String[] args) {
		Participant P1 = new Participant(0, "", 0, false);
		P1.configuration_parser(args);
		P1.run();

	
	}

}
