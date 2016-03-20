import java.net.Socket;
import java.util.Arrays;

public class ParticipantThread extends Thread{
	
	public int ID;
	public String IP_coordinator;
	public int coordinatorPort;
	public int listenPort;
	public int portUserCmd;
	public boolean isOnline;
	public Socket participantSocket = null;
	protected String myIPAddress = null;
	protected String command = null;
	protected String message = null;

	
	public ParticipantThread(int ID, String IP_coordinator, int coordinatorPort, int listenPort, 
			boolean isOnline, String myIPAddress, String command, String message){
		this.ID = ID;
		this.IP_coordinator = IP_coordinator;
		this.coordinatorPort = coordinatorPort;
		this.listenPort = listenPort;
		this.isOnline = isOnline;
		this.myIPAddress = myIPAddress;
		this.command = command;
		this.message = message;
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
		
		//TODO send the message to the coordinator
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
		
		//TODO send the message to the coordinator
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
		
		//TODO send the message to the coordinator
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
		
		//TODO send the message to the coordinator
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
		
		//TODO send the message to the coordinator	
		}
	
	@Override
	public void run(){
		switch (command){
			case "register":
				this.register(this.listenPort);
				break;
			case "deregister":
				this.deregister();
				break;
			case "disconnect":
				this.disconnect();
				break;
			case "reconnect":
				this.register(this.listenPort);
				break;
			case "msend":
				this.msend(this.message);
				break;
		
		}
		
	}

}
