/*File: CommandCode.java
 *Authors: Montana Wong, Justin Tumale, Matthew Haneburger
 *Helper file that contains the commands that each participant will utilize and handles them accordingly.
*/
public enum CommandCode {
	uno (1, "Register"),
	dos (2, "Deregister"),
	tres (3, "Disconnect"),
	cuatro (4, "Reconnect"),
	cinco (5, "MSend");
	
	public int code;
	public String command;
	
	private CommandCode(int code, String command){
		this.code = code;
		this.command = command;
	}
	
	public static String getMethodFromCode(int code){
		switch(code){
		case 1:
			return "Register";
		case 2:
			return "Deregister";
		case 3:
			return "Disconnect";
		case 4:
			return "Reconnect";
		case 5:
			return "MSend";
		default:
			return "ERROR";
		}
	}
	
	public static String getCodeFromMethod(String method){
		switch(method){
		case "Register":
			return "1";
		case "Deregister":
			return "2";
		case "Disconnect":
			return "3";
		case "Reconnect":
			return "4";
		case "MSend":
			return "5";
		}
		return "0";
	}
}
