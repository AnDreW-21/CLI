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

    public boolean parse(String input) {
        String[] temp = input.split(" ");
        commandName = temp[0];
        args = Arrays.copyOfRange(temp, 1, temp.length);
        toFile = input.contains(">");
        appendToFile = input.contains(">>");
        if (appendToFile) {
            outputFilePath = input.split(">>")[1];
            outputFilePath = outputFilePath.strip();
        } else if (toFile) {
            outputFilePath = input.split(">")[1];
            outputFilePath = outputFilePath.strip();
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}


public class Terminal {
    Parser parser;
    File currentPath;

    public String pwd() {
        return currentPath.getAbsolutePath();
    }

    public String echo() {
        StringBuilder output = new StringBuilder();
        String[] args = parser.getArgs();
        for (String arg : args) {
            if (arg.equals(">") || arg.equals(">>")) {
                break;
            }
            output.append(arg).append(" ");
        }
        return output.toString();
    }

    public void cd(String[] args) {
        File temp = new File(currentPath.getAbsolutePath());
        if (args.length == 0) {
            currentPath = new File(System.getProperty("user.dir"));
        } else {
            String[] filesInDirectory = currentPath.list();
            if (args[0].equals("..")) {
                currentPath = currentPath.getParentFile();
            } else if (filesInDirectory != null) {
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

    public String mkdir(String[] args) {
        if (args.length == 0) {
            return "Invalid Arguments";
        }
        for (String arg : args) {
            if (arg.equals(">") || arg.equals(">>")) break;
            File creator = new File(arg);
            if (creator.isAbsolute() && !creator.exists()) {
                creator.mkdir();
            } else {
                creator = new File(currentPath.getAbsolutePath() + File.separator + arg);
                creator.mkdir();
            }
        }
        return "";
    }

    public String ls(String[] args) {
        StringBuilder output = new StringBuilder();
        String[] paths = currentPath.list();
        boolean flag = false;
        for (String arg : args){
            if (arg.equals("-r")) flag = true;
        }
        if (!flag) {
            if(args.length==0 || (args[0].equals(">") || args[0].equals(">>") & (args.length>1))){
                for (String path : paths) {
                    output.append(path).append("\n");
                }
                return output.toString();
            }
            return "Invalid Arguments";
        }
        else {
            for (int i = paths.length - 1; i >= 0; i--) {
                output.append(paths[i]).append("\n");
            }
            return output.toString();
        }
    }

    public void cp(String[] args) throws IOException {
        if ((args.length == 0)) {
            throw new IOException();
        }
        if (!args[0].equals("-r")) {
            InputStream inputStream;
            OutputStream outputStream;


            File temp = new File(args[0]);
            if (temp.isAbsolute()) inputStream = new FileInputStream(args[0]);
            else inputStream = new FileInputStream(currentPath + File.separator + args[0]);

            temp = new File(args[1]);
            if (temp.isAbsolute()) outputStream = new FileOutputStream(args[1]);
            else outputStream = new FileOutputStream(currentPath + File.separator + args[1]);
            if (!temp.exists()) temp.createNewFile();


            byte[] buffer = new byte[1024];
            int size;
            while ((size = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, size);
            }
            inputStream.close();
            outputStream.close();
        }

    }

    public String rmdir(String[] args) {
        String output = "";
        String[] paths = currentPath.list();
        if(args.length==0){
            return "Invalid Arguments";
        }
        if (args[0].equals("*")) {
            for (String path : paths) {
                File file = new File(path);
                if (!file.isAbsolute()) file = new File(currentPath.getAbsolutePath() + File.separator + path);
                if (file.isDirectory()) {
                    if (file.list().length == 0)
                        file.delete();
                }
            }
            return output;
        } else {
            File file = new File(args[0]);
            if (file.exists()) {
                if (!file.isAbsolute()) file = new File(currentPath.getAbsolutePath() + File.separator + args[0]);
                if (file.isDirectory()) {
                    if (file.list().length == 0)
                        file.delete();
                }
                return output;
            }
        }
        return "Invalid Arguments";

    }

    public boolean touch(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IOException();
        }

        File file = new File(args[0]);
        if (file.isAbsolute()) {
            return file.createNewFile();
        }
        file = new File(currentPath.getAbsolutePath() + File.separator + args[0]);
        return file.createNewFile();
    }

    public boolean rm(String[] args) {
        if (args.length == 0) {
            return false;
        }
        File file = new File(currentPath + File.separator + args[0]);
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    public String cat(String[] args) throws IOException{
        if(args.length==0){
            return "Invalid Arguments";
        }
        File temp1, temp2 = new File("");
        boolean foundSecond = false;
        temp1 = new File(args[0]);
        if (!temp1.isAbsolute()) temp1 = new File(currentPath + File.separator + args[0]);

        try{
            temp2 = new File(args[1]);
            if (!temp2.isAbsolute()) temp2 = new File(currentPath + File.separator + args[1]);
            foundSecond = true;
        }catch (ArrayIndexOutOfBoundsException ignored){}

        StringBuilder rtn = new StringBuilder();
        byte[] temp = Files.readAllBytes(temp1.toPath());
        for (byte c : temp){
            rtn.append((char) c);
        }
        if (foundSecond){
            rtn.append("\n");
            temp = Files.readAllBytes(temp2.toPath());
            for (byte c : temp){
                rtn.append((char) c);
            }
        }
        return rtn.toString();
    }

    Terminal(Parser parser) {
        this.parser = parser;
    }

    public void chooseCommandAction() {
        String output = "";
        switch (parser.getCommandName().toLowerCase()) {
            case "exit" -> {
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
                } catch (InvalidPathException e) {
                    System.out.println("Invalid Path");
                }
            }
            case "rm" -> {
                if (!rm(parser.getArgs())) {
                    System.out.println("Invalid Arguments");
                }
            }
            case "cat" -> {
                try{
                    output = cat(parser.getArgs());
                    System.out.println(output);
                }catch (IOException ignored){
                    output = "Invalid Arguments";
                }
            }
            case "touch" -> {
                try {
                    touch(parser.getArgs());
                } catch (IOException ignored) {
                    output = "Invalid Arguments";
                    System.out.println(output);
                }
            }
            case "rmdir" -> {
                output = rmdir(parser.getArgs());
                System.out.println(output);
            }
            case "cp" -> {
                try {
                    cp(parser.getArgs());
                } catch (IOException ignored) {
                    output = "Invalid Arguements";
                    System.out.println(output);
                }
            }
            case "ls" -> {
                output = ls(parser.getArgs());
                System.out.println(output);
            }
            case "mkdir" -> {
                output = mkdir(parser.getArgs());
                System.out.println(output);
            }
            default -> {
                output = "Invalid Command";
                System.out.println(output);
            }
        }
        if (parser.toFile) {
            File out = new File(parser.outputFilePath);
            if (!out.isAbsolute()) out = new File(currentPath + File.separator + parser.outputFilePath);
            try {
                if (!out.exists()) out.createNewFile();
                if (parser.appendToFile) output = Files.readString(out.toPath()) + output;
                output += "\n";
                Files.write(out.toPath(), output.getBytes());
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String command = "non-empty";
        Terminal terminal = new Terminal(new Parser());
        terminal.currentPath = new File(System.getProperty("user.dir"));
        File temp = new File("");
        while (!command.equals("exit")) {
            try {
                System.out.print(terminal.currentPath.getAbsolutePath() + " : ");
                command = scan.nextLine();
                terminal.parser.parse(command);
                temp = new File(terminal.currentPath.getAbsolutePath());
                terminal.chooseCommandAction();
            } catch (NullPointerException e) {
                System.out.println("Invalid Path");
                terminal.currentPath = new File(temp.getAbsolutePath());
            }
        }
    }
}
