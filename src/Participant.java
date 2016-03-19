import java.util.*;
import java.io.IOException;
import java.net.*;


public class Participant {
	
	protected String ID;
	private String IP_coordinator;
	private int portCoordinator;
	private int portUserCmd;
	protected boolean isOnline;
	private Socket participantSocket = null;
	private ParticipantListener listenerCoordinator = null;
	private String command = null;
	
	public Participant(String ID, String IP_coordinator, int portCoordinator, int portUserCmd, boolean isOnline){
		this.ID = ID;
		this.IP_coordinator = IP_coordinator;
		this.portCoordinator = portCoordinator;
		this.portUserCmd = portUserCmd;
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
			System.out.println("Enter command:" );
			Scanner in = new Scanner(System.in);
			command = in.nextLine();
			
			while (this.command != "quit"){
				//create the user thread
				ParticipantThread userCommandThread = new ParticipantThread(this.ID, this.IP_coordinator, 
						this.portCoordinator, this.portUserCmd, this.isOnline);
				
				
				
				
				
			};
			this.listenerCoordinator.participantSocket.close();
		
			
			
			
			in.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
