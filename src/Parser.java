import java.util.Arrays;

public class Parser {

    String commandName;
    String[] args;
    boolean toFile;
    String outputFilePath;

    public boolean parse(String input){
       String[] temp = input.split(" ");
       commandName = temp[0];
       args = Arrays.copyOfRange(temp, 1, temp.length);
       if (input.contains(">")) {
           toFile = true;
           outputFilePath = input.split(">")[1];
           outputFilePath = outputFilePath.strip();
       }else{
           toFile = false;
       }
       return true;
    }

    public String getCommandName(){
        return commandName;
    }

    public String[] getArgs(){
        return args;
    }
}
