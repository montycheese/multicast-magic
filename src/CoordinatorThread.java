import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;


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
		
	}
	
	public String parse(String message){
		String[] tokens = message.split(",");
		int code = Integer.valueOf(tokens[0]);
		if(code > 5 || code < 1){
			throw new IllegalArgumentException("Participant entered an invalid command");
		}
		String action = CommandCode.getMethodFromCode(Integer.valueOf(code));
		//move send ack here
		this.sendACK(true);
		switch(action){
		case "Register":
			break;
		}
		
		return null;
	}
	
	public void register(int id, String ip, int port){
		
	}
	
	public String receive() throws IOException{
		return this.in.readLine();
	}
	
	public void sendACK(boolean status){
		this.out.println((status) ? "1" : "0");
	}
	
	private void sendAll(String message){
		
	}
	
	
}
