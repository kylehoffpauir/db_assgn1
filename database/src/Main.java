import sun.font.TrueTypeFont;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        //attempt to create the dbFiles if it doesnt already exist
        File catalog = new File("dbFiles.db");
        try {
            if (!catalog.exists()) {
                catalog.createNewFile();
            }
        } catch(Exception e) {
            System.out.println("Error creating dbFiles.db: " + catalog.toString());
        }
        //read from the db files and store in some structure
        HashMap<String, String> db = readDbFiles(catalog);


        Scanner scanner = new Scanner(System.in);
        int option;
        boolean done = false;
        while(!done) {
            printMenu();
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    createTable();
                    break;
                case 2:
                    insert();
                    break;
                case 3:
                    remove();
                    break;
                case 4:
                    printFile();
                    break;
                case 5:
                    done = true;
                    purge();
                    break;
                default:
                    System.out.println("Error! Enter a valid option");
            }

        }
    } // end main


    public static void printMenu(){
        String[] options = {"1- Create Table",
                "2- Insert",
                "3- Remove",
                "4- Print File",
                "5- Exit"
        };
        for (String option : options){
            System.out.println(option);
        }
        System.out.print("Choose your option : ");
    }


    // read from the db files and store in some structure
    public static HashMap<String, String> readDbFiles(File catalog) {
        return null;
    }


    //create a table and store it in the dbfiles
    public static void createTable() {

    }


    //insert into an existing table
    public static void insert() {

    }


    //mark db entry for removal
    public static void remove() {

    }


    //print contents of dbfile
    public static void printFile() {

    }


    //delete entries from table marked by the remove function
    public static void purge() {

    }



}//end Main class
