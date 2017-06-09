package myPage.model;

public class CommandFactory {

	
	private CommandFactory(){}
	
	
	private static CommandFactory instance = new CommandFactory();
		
	
	public static CommandFactory newInstance(){
		return instance;
	}
	
	public CommandInterface createCommand(String cmd){
		
		System.out.println(cmd);
		
		if(cmd.equals("myinfo")){
			
			return new MyinfoCommand();
			
		}
		else{
			return null;
		}
		
	}
}
