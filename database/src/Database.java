import sun.font.TrueTypeFont;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Database {
    private static final String CATALOG = "dbfiles";
    private static final String CATALOG_PATH = "dbfiles/";

    public static void main(String[] args) throws IOException {
        //attempt to create the dbFiles if it doesnt already exist

        File catalog = new File(CATALOG);
        HashMap<String, String> db = new HashMap<String, String>();
        try {
            if (!catalog.exists()) {
                catalog.mkdir();
            }
            else {
                System.out.println("Reading db files...");
                //read from the db files and store in some structure
                db = readDbFiles(catalog);
                System.out.println("Reading complete!");
            }
        } catch(Exception e) {
            System.err.println("Error creatinng or reading from dbFiles: " + catalog.toString());
            System.err.println(e);
        }
        //run the program now that the dbFiles loaded
        run(catalog, db);
    } // end main


    public static void run(File catalog, HashMap<String, String> db) throws IOException {
        //scanner object to prompt for user input and to pass to functions
        Scanner scanner = new Scanner(System.in);
        int option;
        //continue prompting user until they hit 5 to exit
        boolean done = false;
        while(!done) {
            printMenu();
            option = scanner.nextInt();
            scanner.nextLine();
            System.out.println();
            switch (option) {
                case 1:
                    createTable(scanner);
                    break;
                case 2:
                    insert(scanner);
                    break;
                case 3:
                    remove(scanner);
                    break;
                case 4:
                    printFile(scanner);
                    break;
                case 5:
                    done = true;
                    // add shutdown hook so this purge happens always?
                    purge(catalog, db);
                    break;
                default:
                    System.out.println("Error! Enter a valid option");
            }//end switch
            System.out.println("\n");
        }//end infinite while loop
        return;
    }

    public static void printMenu(){
        System.out.println("---------DATABASE-------");
        String[] options = {
                "1 - Create Table",
                "2 - Insert",
                "3 - Remove",
                "4 - Print File",
                "5 - Exit"
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
    public static void createTable(Scanner in) throws IOException {
        //store the column names and lengths in arraylists
        ArrayList<String> colNames = new ArrayList<String>();
        ArrayList<Integer> colLengths = new ArrayList<Integer>();
        boolean keepReading = true;

        //if table already exists, kick user out
        System.out.println("Enter table name");
        String tableName = in.nextLine();
        File table = new File(CATALOG_PATH + tableName + ".db");
        if (table.exists()) {
            System.err.println("Error! cannot create table " + tableName + ".db as it already exists!");
            return;
        }

        //take in and store column info
        while(keepReading) {
            System.out.println("Enter column name and it's length:");
            String line = in.nextLine();
            if(line.equals(""))
                keepReading = false;
            else {
                colNames.add(line.split(" ")[0]);
                colLengths.add(Integer.parseInt(line.split(" ")[1]));
            }

        }
        System.out.println("Done collecting!");
        //save column and table info to disk
        try {
            dump(table, colNames, colLengths);
            System.out.println("Wrote to disk!");
        } catch(Exception e) {
            System.err.println("Error writing to disk. Table not saved.");
        }
        return;
    }


    private static void dump(File table, ArrayList<String> colNames, ArrayList<Integer> colLengths) throws IOException {
        //dump data to disk
        table.createNewFile();
        FileWriter fw = new FileWriter(table);
        for(int i = 0; i < colNames.size(); i++) {
            fw.append(colNames.get(i) + " ");
            fw.append(colLengths.get(i).toString() + "\t");
        }
        fw.close();
        return;
    }


    //insert into an existing table
    public static void insert(Scanner in) {

    }


    //mark db entry for removal
    public static void remove(Scanner in) {

    }


    //print contents of dbfile
    public static void printFile(Scanner in) {

    }


    //delete entries from table marked by the remove function
    public static void purge(File catalog, HashMap<String, String> db) {

    }



}//end Main class
