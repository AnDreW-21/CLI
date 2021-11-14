import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.Scanner;
import java.util.Arrays;



class Parser {

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


public class Terminal {
    Parser parser;
    File currentPath;

    public String pwd(){
        return currentPath.getAbsolutePath();
    }

    public String echo(){
        StringBuilder output = new StringBuilder();
        String[] args = parser.getArgs();
        for (String arg : args) {
            if(arg.equals(">") || arg.equals(">>")){
                break;
            }
            output.append(arg).append(" ");
        }
        return output.toString();
    }

    public void cd(String[] args){
        File temp = new File(currentPath.getAbsolutePath());
        if (args.length == 0){
            currentPath = new File(System.getProperty("user.dir"));
        }
        else{
            String[] filesInDirectory = currentPath.list();
            if (args[0].equals("..")) {
                currentPath = currentPath.getParentFile();
            }
            else if (filesInDirectory != null) {
                for (String path : filesInDirectory) {
                    if (path.equals(args[0])) {
                        currentPath = new File(currentPath.getAbsolutePath() + File.separator + args[0]);
                        return;
                    }
                }
                currentPath = new File(args[0]);
                if (!currentPath.exists()) {
                    currentPath = temp;
                    throw new InvalidPathException(currentPath.getAbsolutePath(), "Such path doesn't exist");
                }
            }
        }
    }

    public String mkdir(String[] args){
        if (args.length == 0){
            return "Invalid Arguments";
        }
        for (String arg: args){
            if (arg.equals(">") || arg.equals(">>")) break;
            File creator = new File(arg);
            if (creator.isAbsolute() && !creator.exists()){
                creator.mkdir();
            }else{
                creator = new File(currentPath.getAbsolutePath() + File.separator + arg);
                creator.mkdir();
            }
        }
        return "" ;
    }

    public String ls(String[] args){
        StringBuilder output = new StringBuilder();
        String[] paths = currentPath.list();
        if (args.length == 0) {
            for (String path : paths) {
               output.append(path).append("\n");
            }
        }
        else if (args[0].equals("-r")){
            for (int i = paths.length - 1; i >= 0; i--){
                output.append(paths[i]).append("\n");
            }
        }
        else{
            return "Invalid Arguments";
        }
        return output.toString();
    }

    public void cr(String[] args) throws IOException {
        if(!args[0].equals("-r")){
            InputStream inputStream ;
            OutputStream outputStream ;
            inputStream = new FileInputStream(currentPath+"\\"+args[1]);
            outputStream = new FileOutputStream(currentPath+"\\"+args[2]);
            byte[] buffer = new byte[1024];
            int size;
            while ((size = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, size);
            }
            inputStream.close();
            outputStream.close();
        }
    }
    public boolean rm(String[] args){
        File file= new File(currentPath+"\\"+args[0]);
        return file.delete();
    }
    Terminal(Parser parser){
        this.parser = parser;
    }


    public void chooseCommandAction() {
        String output = "";
        switch (parser.getCommandName().toLowerCase()) {
            case "exit" ->{
                return;
            }
            case "echo" -> {
                output = echo();
                System.out.println(output);
            }
            case "pwd" -> {
                output = pwd();
                System.out.println(output);
            }
            case "cd" -> {
                try {
                    cd(parser.getArgs());
                }
                catch (InvalidPathException e){
                    System.out.println("Invalid Path");
                }
            }
            case "ls" -> {
                output = ls(parser.getArgs());
                System.out.println(output);
            }
            default ->{
                output = "Invalid Command";
                System.out.println(output);
            }
        }
        if (parser.toFile){
            File out = new File(parser.outputFilePath);
            try{
                if (!out.exists()) out.createNewFile();
                if (parser.appendToFile) output = Files.readString(out.toPath()) + output;
                output += "\n";
                Files.write(out.toPath(), output.getBytes());
            }
            catch (IOException e){
                System.out.println(e.toString());
            }
        }
    }


    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        String command = "non-empty";
        Terminal terminal = new Terminal(new Parser());
        terminal.currentPath = new File(System.getProperty("user.dir"));
        File temp = new File("");
        while (!command.equals("exit")){
            try{
                System.out.print(terminal.currentPath.getAbsolutePath() + " : ");
                command = scan.nextLine();
                terminal.parser.parse(command);
                temp = new File(terminal.currentPath.getAbsolutePath());
                terminal.chooseCommandAction();
            }
            catch (NullPointerException e){
                System.out.println("Invalid Path");
                terminal.currentPath = new File(temp.getAbsolutePath());
            }
        }
    }
}
