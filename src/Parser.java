import java.util.Arrays;

public class Parser {

    String commandName;
    String[] args;
    boolean toFile;
    boolean appendToFile;
    String outputFilePath;


    public boolean parse(String input){
       String[] temp = input.split(" ");
       commandName = temp[0];
       args = Arrays.copyOfRange(temp, 1, temp.length);
       toFile = input.contains(">");
       appendToFile = input.contains(">>");

       if (appendToFile){
           outputFilePath = input.split(">>")[1];
           outputFilePath = outputFilePath.strip();
       }
       else if (toFile){
           outputFilePath = input.split(">")[1];
           outputFilePath = outputFilePath.strip();
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
