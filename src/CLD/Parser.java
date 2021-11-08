package CLD;
public class Parser {
    String line;
    String commandName;
    String[] args;
    String []words;
    //This method will divide the input into commandName and args
    // where "input" is the string command entered by the user
    public boolean parse(String input){
        return !input.equals("");
    }
    public void divide_line(){
         words=line.split(" ");
    }
    public void setCommandName(){
        commandName=words[0];
    }
    public void setLine(String line){
        this.line=line;
    }
    public void setArgs(String line){
        int j=0;
        for (int i=1;i<words.length;i++){
            args[j]=words[i];
            j++;
        }
    }
    public String getCommandName(){return commandName;}
    public String[] getArgs(){return args; }
}
