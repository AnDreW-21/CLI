import java.io.File;
import java.nio.file.InvalidPathException;
import java.util.Arrays;
import java.util.Scanner;

public class Terminal {
    Parser parser;
    File currentPath;

    public void pwd(){
        System.out.println(currentPath.getAbsolutePath());
    }

    public void echo(){
        String[] args = parser.getArgs();
        for (String arg : args) {
            System.out.print(arg + " ");
        }
        System.out.print("\n");
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

    public void ls(String[] args){
        String[] paths = currentPath.list();
        if (args.length == 0) {
            for (String path : paths) {
                System.out.println(path);
            }
        }
        else if (args[0].equals("-r")){
            for (int i = paths.length - 1; i >= 0; i--){
                System.out.println(paths[i]);
            }
        }
        else{
            System.out.println("Invalid Arguments");
        }
    }


    Terminal(Parser parser){
        this.parser = parser;
    }


    public void chooseCommandAction(){
        switch (parser.getCommandName().toLowerCase()) {
            case "echo" -> echo();
            case "pwd" -> pwd();
            case "cd" -> {
                try {
                    cd(parser.getArgs());
                }
                catch (InvalidPathException e){
                    System.out.println("Invalid Path");
                }
            }
            case "ls" -> ls(parser.getArgs());
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
                System.out.print(terminal.currentPath.getAbsolutePath() + " > ");
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
