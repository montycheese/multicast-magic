import java.net.Socket;

public class ParticipantListener extends Thread{
	
	public Socket participantSocket = null;
	
	public ParticipantListener(Socket participantSocket){
		this.participantSocket = participantSocket;
	}
	
	@Override
	public void run(){
		//try{
			System.out.println("Thread running");
			//loop that continuously listens for command from coordinator. 
		//}
		/*
		catch(SocketException e){
			//we close socket on participant class so this thread which is blocking on accept will get an error
			return;
		}
		*/
	}

}
