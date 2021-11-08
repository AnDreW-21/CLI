package CLD;

import java.util.Scanner;

public class Terminal {
    Parser parser;
    //Implement each command in a method, for example:
    public String pwd(){return null;}
    public String echo(){
        return null;
    }

    public void cd(String[] args){}
    // ...
//This method will choose the suitable command method to be called
    Terminal(Parser parser){
        this.parser=parser;
    }
    public void chooseCommandAction(){


    }
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        String input=sc.nextLine();
        System.out.println(input);
        Parser oc=new Parser();
        Terminal cl=new Terminal(oc);
        cl.parser.print();
    }
}
