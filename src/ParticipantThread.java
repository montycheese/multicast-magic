import java.net.Socket;

public class ParticipantThread extends Thread{
	
	public int ID;
	public String IP_coordinator;
	public int portCoordinator;
	public int portUserCmd;
	public boolean isOnline;
	public Socket participantSocket = null;
	
	public ParticipantThread(int ID, String IP_coordinator, int portCoordinator, boolean isOnline){
		this.ID = ID;
		this.IP_coordinator = IP_coordinator;
		this.portCoordinator = portCoordinator;
		this.isOnline = isOnline;
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
		//TODO
	}
	
	/* Deregister
	 * Participant indicates to the coordinator that it is no longer belongs to the multicast 
	 * group. Please note that this is different than being disconnected. A participant that 
	 * deregisters, may register again. But it will not get any messages that were sent since 
	 * its deregistration (i.e., it will be treated as a new entrant). Thread-B will relinquish 
	 * the port and may become dormant or die. 
	 */
	public void deregister(){
		//TODO
	}
	
	/* Disconnect
	 * Participant indicates to the coordinator that it is temporarily going offline. 
	 * The 	coordinator will have to send it messages sent during disconnection (subject 
	 * to temporal constraint). Thread-B will relinquish the port and may become dormant or die. 
	 */
	
	public void disconnect(){
		//TODO
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
		//TODO
	}
	
	/* Send
	 * Multicast [message] to all current members. Note that [message] is 	
	 * an alpha-numeric string (e.g., UGACSRocks). The participant sends the 
	 * message to the coordinator and unblocks after an acknowledgement is received. 
	 * 
	 * @param message: the message to be multicasted
	 */
	public void msend(String message){
		//TODO
	}
	
	@Override
	public void run(){
		
	}

}
