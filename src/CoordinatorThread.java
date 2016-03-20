import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;


public class CoordinatorThread extends Thread {
	private ServerSocket sock;
	private Socket clientSock;
	private HashMap<Integer, Participant> multicastGroup;
	private HashMap<Integer, LinkedList<Message>> messageBuffer;
	private PrintWriter out;
	private BufferedReader in;
	private long threshold;
	
	public CoordinatorThread(
			ServerSocket sock, 
			Socket clientSock,
			HashMap<Integer, Participant> multicastGroup,
			HashMap<Integer, LinkedList<Message>> messageBuffer,
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
			String response = this.parse(cmd);
			//Send ACK
			//this.sendACK(true);
			//perform requested action
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
				this.in.close();
				this.out.close();
				this.clientSock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void parse(String message){
		String[] tokens = message.split(",");
		int code = Integer.valueOf(tokens[0]);
		if(code > 5 || code < 1){
			throw new IllegalArgumentException("Participant entered an invalid command");
		}
		String action = CommandCode.getMethodFromCode(Integer.valueOf(code));
		//move send ack here
		this.sendACK(true);
		try{
			switch(action){
			case "Register":
				this.register(Integer.valueOf(tokens[1]), tokens[2], Integer.valueOf(tokens[3]));
				break;
			case "Deregister":
				this.deregister(Integer.valueOf(tokens[1]));
				break;
			case "Disconnect":
				this.disconnect(Integer.valueOf(tokens[1]));
				break;
			case "Reconnect":
				break;
			case "MSend":
				break;
			default:
				System.out.println("Code meltdown failure");
				break;
			}
		}
		catch(NumberFormatException nfe){
			System.out.println("Participant send malformed input");
		}
		catch(ArrayIndexOutOfBoundsException aoobe){
			System.out.println("Participant send malformed input");
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
			p.isOnline = true;
			p.portCoordinator = port;
		}
		
	}
	
	private String receive() throws IOException{
		return this.in.readLine();
	}
	
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
		int port = p.portCoordinator;
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
	
	private void sendACK(boolean status){
		this.out.println((status) ? "1" : "0");
	}
	
	private void multicastSend(String message){
		//TODO
		Participant p = null;
		for(Integer id: this.multicastGroup.keySet()){
			p = this.multicastGroup.get(id);
			if(p.isOnline){
				try {
					this.sendMessage(message, p);
				}
				catch(UnknownHostException uhe){
					System.out.printf("Participant with id: %d at port: %d does not exist\n", id, p.portCoordinator);
					uhe.printStackTrace();
				}
				catch (IOException e) {
					System.out.printf("Participant with id: %d at port: %d IO Exception\n", id, p.portCoordinator);
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
