/**File: CoordinatorThread.java
 * Authors: Montana Wong, Justin Tumale, Matthew Haneburger
 * Handles requests from participants. Never fails. Handles methods for the
 * five fundamental operations of the participants. (Register, Deregister, Disconnect, Reconnect, Msend)
 * Also handles the messages received by participants accordingly.
*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.LinkedList;

//description above
public class CoordinatorThread extends Thread {
	private ServerSocket sock;
	private Socket clientSock;
	private Hashtable<Integer, Participant> multicastGroup;
	private Hashtable<Integer, LinkedList<Message>> messageBuffer;
	private PrintWriter out;
	private BufferedReader in;
	private long threshold;
	
	public CoordinatorThread(
			ServerSocket sock, 
			Socket clientSock,
			Hashtable<Integer, Participant> multicastGroup,
			Hashtable<Integer, LinkedList<Message>> messageBuffer,
			long threshold
	){
		this.sock = sock;
		this.clientSock = clientSock;
		this.multicastGroup = multicastGroup;
		this.messageBuffer = messageBuffer;
		this.threshold = threshold;
		
		try {
			this.out = new PrintWriter(this.clientSock.getOutputStream(), true);
			//in is the incoming message buffer from the participant to be read by the coordinator
			this.in = new BufferedReader(new InputStreamReader(this.clientSock.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	
	public void run(){
		String cmd = "";
		try {
			//Read command from participant
			cmd = this.receive();
			this.parse(cmd);
		}  
		catch(IllegalArgumentException iae){
			this.sendACK(false);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				//shutdown sockets, writer and reader
				this.in.close();
				this.out.close();
				this.clientSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	/**
	* @param String message that is received by a participant within the grouo
	* Parses message and tokenizes requests
	* Uses switch case to determine which command to apply 
	*/
	private void parse(String message){       
		
		message = message.substring(1, message.length()-1); //Remove the brackets
		String[] tokens = message.split(",");
		int code = Integer.valueOf(tokens[0]);

		if(code > 5 || code < 1){
			throw new IllegalArgumentException("Participant entered an invalid command");
		}
		String action = CommandCode.getMethodFromCode(Integer.valueOf(code));
		//move send ack here
		this.sendACK(true);
		System.out.println("ACK SENT");
		try{
			switch(action){
			case "Register":
				this.register(Integer.valueOf(tokens[1].trim()), tokens[2].trim(), 
						Integer.valueOf(tokens[3].trim()));
				System.out.println("Registeration complete");
				break;
			case "Deregister":
				this.deregister(Integer.valueOf(tokens[1].trim()));
				break;
			case "Disconnect":
				this.disconnect(Integer.valueOf(tokens[1].trim()));
				break;
			case "Reconnect":
				this.reconnect(Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2].trim()));
				break;
			case "MSend":
				this.multicastSend(tokens[1].trim());
				break;
			default:
				System.out.println("Code meltdown failure");
				break;
			}
		}
		catch(NumberFormatException nfe){
			System.out.println("Participant sent malformed input");
		}
		catch(ArrayIndexOutOfBoundsException aioobe){
			System.out.println("Participant sent malformed input");
		}
		
	}
	
	private void register(int id, String ip, int port){
		Participant p = new Participant(id, ip, port, true);
		//add participant to group
		this.multicastGroup.put(id, p);
		//create a message buffer for this new participant
		this.messageBuffer.put(id, new LinkedList<Message>());
	}
	
	private void deregister(int id){
		Participant p = this.multicastGroup.remove(new Integer(id));
		if (p == null){
			System.out.println("Error, participant not registered");
		}
		else{
			this.messageBuffer.remove(new Integer(id));
		}
	}
	
	private void disconnect(int id){
		Participant p = this.multicastGroup.get(new Integer(id));
		if (p == null){
			System.out.println("Error, participant not registered");
		}
		else{
			p.isOnline = false;
		}
		
	}
	
	private void reconnect(int id, int port){
		Participant p = this.multicastGroup.get(new Integer(id));
		if (p == null){
			System.out.println("Error, participant not registered");
		}
		else{
			//update boolean and port num
			p.isOnline = true;
			p.listenPort = port;
			this.sendQueuedMessages(id, port);
		}
		
	}
	
	private String receive() throws IOException{
		return this.in.readLine();
	}
	
	@SuppressWarnings("unused")
	private void sendMessage(String message){
		this.out.println(message);
	}
	
	/**
	 * Send message to a particular participant by id
	 * @param message
	 * @param id
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void sendMessage(String message, Participant p) throws UnknownHostException, IOException{
		//create socket
		int port = p.listenPort;
		String host = "localhost";
		Socket sock = new Socket(host, port);
		
		//send message
	    PrintWriter out = new PrintWriter(sock.getOutputStream());
	    out.println(message);
	    out.flush();
	    
	    //close socket
	    out.close();
	    if(sock != null){
	    	sock.close();
	    }
	}
	
	/**
	 * Sends all queued messages to a participant who has reconnected to the mutlicast group.
	 * The queued messages were stored for the duration the participant was disconnected.
	 * Messages that exceed the persistence time threshold are discarded and not sent
	 * @param id int The unique ID of the participant
	 * @param port int The port at which the participant listens for messages from the coordinator
	 */
	private void sendQueuedMessages(int id, int port){
		String host = "localhost";
		PrintWriter out = null;
		Socket sock = null;
		LinkedList<Message> buffer = this.messageBuffer.get(new Integer(id));
		if(buffer.isEmpty()){
			//if no queued message exist, exit method.
			return;
		}
		try {	
			sock = new Socket(host, port);
			out = new PrintWriter(sock.getOutputStream());
			
			//casting linkedlist to iterable array
			for(Message m: (Message[]) buffer.toArray()){
				//only allow a message to be sent if now-createTime <= T_d 
				long diff = System.nanoTime() - m.getCreateTime();
				if(diff <= this.threshold){
					out.println(m.getMessage());
				}
			}
			//clear the message buffer
			buffer.clear();
			
			//clean up
			out.close();
			sock.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	private void sendACK(boolean status){
		this.out.println((status) ? "1" : "0");
	}
	
	/**
	 * 
	 * @param message
	 */
	private void multicastSend(String message){
		Participant p = null;
		for(Integer id: this.multicastGroup.keySet()){
			p = this.multicastGroup.get(id);
			if(p.isOnline){
				try {
					this.sendMessage(message, p);
				}
				catch(UnknownHostException uhe){
					System.out.printf("Participant with id: %d at port: %d does not exist\n", id, p.listenPort);
					uhe.printStackTrace();
				}
				catch (IOException e) {
					System.out.printf("Participant with id: %d at port: %d IO Exception\n", id, p.listenPort);
					e.printStackTrace();
				}
			}
			else{
				//store message in buffer if participant is not online
				LinkedList<Message> messageQueue = this.messageBuffer.get(id);
				messageQueue.add(new Message(message));
			}
		}
	}
	
	
}
