import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ParticipantThread extends Thread{
	
	public int ID;
	public int listenPort;
	public int portUserCmd;
	private int coordinatorPort;
	public boolean isOnline;
	public Socket coordinatorSocket = null;
	protected String myIPAddress = null;
	protected String command = null;
	protected String message = null;
	private PrintWriter out;
	private BufferedReader in;

	
	public ParticipantThread(int ID, int listenPort, int coordinatorPort,
			boolean isOnline, String myIPAddress, String command, String message){
		this.ID = ID;
		this.listenPort = listenPort;
		this.coordinatorPort = coordinatorPort;
		this.isOnline = isOnline;
		this.myIPAddress = myIPAddress;
		this.command = command;
		this.message = message;

		try {
			//Connect to the Coordinator
			//this.participantSocket = new Socket(this.IP_coordinator, this.portCoordinator);
			//run on local host for now

			//this.coordinatorSocket = new 
			//		Socket(InetAddress.getLocalHost().getHostName(), this.coordinatorPort);

			//this.coordinatorSocket = new Socket("localhost", 5600);
			this.coordinatorSocket = new Socket("localhost", this.coordinatorPort);
			this.out = new PrintWriter(this.coordinatorSocket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(this.coordinatorSocket.getInputStream()));
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/* Register
	 * Participant has to register with the coordinator specifying its ID, 	
	 * IP address and port number where its thread-B will receive multicast 
	 * messages (thread-B has to be operational before sending the message 
	 * to the coordinator). Upon successful registration, the participant is 
	 * a member of the multicast group and will begin receiving messages. 
	 * 
	 * @param myPort: the port number where this participant will receive messages
	 */
	public void register(int myPort){
		//Format: Code | ID | IP | Port
		String[]registerMessageArray = {
				CommandCode.getCodeFromMethod("Register"), 
				String.valueOf(this.ID), 
				String.valueOf(this.myIPAddress), 
				String.valueOf(myPort)};
		
		String registerMessage = Arrays.toString(registerMessageArray);
		
		//send the message to the coordinator
		this.out.println(registerMessage);
		this.out.flush();
		
		if (this.receiveACK() == 1){
			System.out.println("You are now registered.");
		}
		else{
			System.out.println("Error in registration.");
		}

	}
	
	/* Deregister
	 * Participant indicates to the coordinator that it is no longer belongs to the multicast 
	 * group. Please note that this is different than being disconnected. A participant that 
	 * deregisters, may register again. But it will not get any messages that were sent since 
	 * its deregistration (i.e., it will be treated as a new entrant). Thread-B will relinquish 
	 * the port and may become dormant or die. 
	 */
	public void deregister(){
		//Format: Code | ID 
		String[] deregisterMessageArray = {
				CommandCode.getCodeFromMethod("Deregister"), 
				String.valueOf(this.ID)
			};
		String deregisterMessage = Arrays.toString(deregisterMessageArray);
		
		//send the message to the coordinator
		this.out.println(deregisterMessage);
		this.out.flush();
		if (this.receiveACK() == 1){
			System.out.println("You are now deregistered.");
		}
		else{
			System.out.println("Error in deregistration.");
		}
	}
	
	/* Disconnect
	 * Participant indicates to the coordinator that it is temporarily going offline. 
	 * The 	coordinator will have to send it messages sent during disconnection (subject 
	 * to temporal constraint). Thread-B will relinquish the port and may become dormant or die. 
	 */
	
	public void disconnect(){
		//Format: Code | ID 
		String[] disconnectMessageArray = {CommandCode.getCodeFromMethod("Disconnect"), String.valueOf(this.ID)};
		String disconnectMessage = Arrays.toString(disconnectMessageArray);	
		
		//send the message to the coordinator
		this.out.println(disconnectMessage);
		this.out.flush();
		
		if (this.receiveACK() == 1){
			System.out.println("You are now disconnected.");
		}
		else{
			System.out.println("Error in disconnecting.");
		}
		
	}
	
	/* Reconnect
	 * Participant indicates to the coordinator that it is online and it 
	 * will specify the IP address and port number where its thread-B will receive 
	 * multicast messages (thread-B has to be operational before sending the message to 
	 * the coordinator). 
	 * 
	 * @param myPort: the port number where this participant will receive messages
	 */
	
	public void reconnect(int myPort){
		//Format: Code | ID | Port
		String[]reconnectMessageArray = {
				CommandCode.getCodeFromMethod("Reconnect"), 
				String.valueOf(this.ID), 
				String.valueOf(myPort)};
		String reconnectMessage = Arrays.toString(reconnectMessageArray);	
		
		//send the message to the coordinator
		this.out.println(reconnectMessage);
		this.out.flush();
		
		if (this.receiveACK() == 1){
			System.out.println("You are now reconnected.");
		}
		else{
			System.out.println("Error in reconnecting.");
		}
	}
	
	/* Send
	 * Multicast [message] to all current members. Note that [message] is 	
	 * an alpha-numeric string (e.g., UGACSRocks). The participant sends the 
	 * message to the coordinator and unblocks after an acknowledgement is received. 
	 * 
	 * @param message: the message to be multicasted
	 */
	public void msend(String message){
		//Format: Code | ID | IP | Port
		String[] msendMessageArray = { CommandCode.getCodeFromMethod("MSend"), message };
		String msendMessage = Arrays.toString(msendMessageArray);	
		
		//send the message to the coordinator	
		this.out.println(msendMessage);
		this.out.flush();

		if (this.receiveACK() == 1){
			System.out.println("Message sent");
		}
		else{
			System.out.println("Error in sending your message");
		}
	}
	
	public int receiveACK(){
		String ACK = null;
		try {
			ACK = this.in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Integer.valueOf(ACK);
		
	}
	
	@Override
	public void run(){

		switch (command){
			case "Register":
				this.register(Integer.valueOf(this.message.trim()));
				break;
			case "Deregister":
				this.deregister();
				break;
			case "Disconnect":
				this.disconnect();
				break;
			case "Reconnect":
				this.reconnect(Integer.valueOf(this.message.trim()));
				break;
			case "MSend":
				this.msend(this.message);
				break;
		}
		//Close socket after methods are finished
		try {
			this.coordinatorSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
