import java.util.Arrays;

public class Parser {

    String commandName;
    String[] args;

    public boolean parse(String input){
       String[] temp = input.split(" ");
       commandName = temp[0];
       args = Arrays.copyOfRange(temp, 1, temp.length);
       return true;
    }

    public String getCommandName(){
        return commandName;
    }

    public String[] getArgs(){
        return args;
    }
}
