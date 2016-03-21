import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ParticipantThread extends Thread{
	
	public int ID;
	public int listenPort;
	public int portUserCmd;
	public boolean isOnline;
	public Socket coordinatorSocket = null;
	protected String myIPAddress = null;
	protected String command = null;
	protected String message = null;
	private PrintWriter out;
	private BufferedReader in;

	
	public ParticipantThread(Socket participantSocket, int ID, int listenPort, 
			boolean isOnline, String myIPAddress, String command, String message, 
			PrintWriter out, BufferedReader in){
		this.coordinatorSocket = participantSocket;
		this.ID = ID;
		this.listenPort = listenPort;
		this.isOnline = isOnline;
		this.myIPAddress = myIPAddress;
		this.command = command;
		this.message = message;
		this.out = out;
		this.in = in;
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
		this.receiveACK();

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
		this.receiveACK();
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
		this.receiveACK();
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
		this.receiveACK();
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
		this.receiveACK();
	}
	
	public void receiveACK(){
		try {
			String ACK = this.in.readLine();
			System.out.println("ACK received from coordinator: " + ACK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run(){
		//TODO becareful, you may cause an exception if you assume that the command sent by the user contains integers
		try{
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
		}
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
		catch(NullPointerException npe){
			npe.printStackTrace();
		}
		
	}

}
