import sun.font.TrueTypeFont;

import java.io.*;
import java.util.*;

public class Database {
    private static final String CATALOG = "dbfiles.db";
    private static final String CATALOG_PATH = "dbfiles/";
    private static final boolean DEBUG = true;

    public static void main(String[] args) throws IOException {
        // going to be pulled from dbFiles, mapping Tablesnames to columns and their size
        HashMap<String, String[]> db = new HashMap<String, String[]>();
        File catalog = load(db);
        //run the program now that the dbFiles loaded
        run(catalog, db);
    } // end main

    private static File load(HashMap<String, String[]> db ) {
        //attempt to create the dbFiles if it doesnt already exist
        File catalog_dir = new File(CATALOG_PATH);
        File catalog = null;
        try {
            if (!catalog_dir.exists()) {
                catalog_dir.mkdir();
                catalog = new File(CATALOG_PATH + CATALOG);
                catalog.createNewFile();
            }
            else {
                System.out.println("Reading db files...");
                //read from the db files and store in some structure
                catalog = new File(CATALOG_PATH + CATALOG);
                db = readDbFiles(catalog);
                System.out.println("Reading complete!");
            }
        } catch(Exception e) {
            System.err.println("Error creating or reading from dbFiles: " + catalog_dir.toString());
            System.err.println(e);
            System.exit(1);
        }
        return catalog;
    }


    public static void run(File catalog, HashMap<String, String[]> db) throws IOException {
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
            db = readDbFiles(catalog);
            switch (option) {
                case 1:
                    createTable(scanner, db);
                    break;
                case 2:
                    insert(scanner, db);
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
    public static HashMap<String, String[]> readDbFiles(File catalog) {
        HashMap<String, String[]> db = new HashMap<String, String[]>();
        try {
            Scanner file = new Scanner(catalog);
            while(file.hasNextLine()) {
                String[] line = file.nextLine().split("\t");
                if(DEBUG) System.out.println(Arrays.toString(line));
                // get the table name from your dbFile
                String table = line[0];
                String[] cols = new String[line.length - 1];
                int count = 0;
                // add your columns and their maximum size to the cols array
                for (int i = 1; i < line.length; i++) {
                    cols[count] = line[i];
                    count++;
                }
                // add your column info to your table -> col map
                db.put(table, cols);
            }
        } catch(FileNotFoundException e) {
            System.err.println("Could not find file " + catalog.getAbsolutePath());
        }
        // db will now hold (for example)
        // BOOKS -> ["author 20", "title 25", "ISBN 15"]
        return db;
    }


    //create a table and store it in the dbfiles
    public static void createTable(Scanner in, HashMap<String, String[]> db) throws IOException {
        //store the column names and lengths in arraylists
        ArrayList<String> colNames = new ArrayList<String>();
        ArrayList<Integer> colLengths = new ArrayList<Integer>();
        boolean keepReading = true;

        //if table already exists, kick user out
        System.out.print("Enter table name: ");
        String tableName = in.nextLine();
        System.out.println();
        File table = new File(CATALOG_PATH + tableName + ".db");
        if (table.exists()) {
            System.err.println("Error! cannot create table " + tableName + ".db as it already exists!");
            return;
        }

        //take in and store column info
        while(keepReading) {
            System.out.print("Enter column name and it's length: ");
            String line = in.nextLine();
            System.out.println();
            if(line.equals(""))
                keepReading = false;
            else {
                String[] lineSplit = line.split(" ");
                String name = lineSplit[0];
                int size = Integer.parseInt(line.split(" ")[1]);
                // ensure single word colNames
                if( name.contains(" ")) {
                    System.err.println("Error! Db columns must be single words");
                    table.delete();
                    System.err.println("Table deleted.");
                    return;
                }
                // add to lists
                colNames.add(name);
                colLengths.add(size);
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
        File catalog = new File(CATALOG_PATH + CATALOG);
        // create writes for both the table file and catalog file
        FileWriter fwTable = new FileWriter(table);
        FileWriter fwCatalog = new FileWriter(catalog);
        fwCatalog.append(table.getName() + "\t");
        for(int i = 0; i < colNames.size(); i++) {
            // add column info to catalog for later size checking
            fwCatalog.append(colNames.get(i) + " ");
            fwCatalog.append(colLengths.get(i).toString() + "\t");
            // add column names to the tables
            //  NEED TO PAD THE SPACES HERE
            fwTable.append(colNames.get(i) + "\t");
        }
        fwCatalog.append("\n");
        fwTable.append("\n");
        fwTable.close();
        fwCatalog.close();
        return;
    }


    //insert into an existing table
    public static void insert(Scanner in, HashMap<String, String[]> db) throws IOException {
        System.out.print("Enter file name: ");
        String table = in.nextLine();
        if(!table.contains(".db")) {
            table+=".db";
        }
        FileWriter fw = null;
        if(DEBUG) {
            System.out.println(db.keySet());
            for (String x : db.keySet()) {
                System.out.println(Arrays.toString(db.get(x)));
            }
        }
        try {
            if ((db.get(table) == null)) {
                System.err.println("Error - could not find table in db hashmap");
                throw new IOException();
            }
            fw = new FileWriter(CATALOG_PATH + table, true);
        } catch (IOException e) {
            System.err.println("Error - table does not exist.");
            return;
        }

        String[] tableData = db.get(table);

        //loop while the user wants to input more data
        boolean moreEntries = true;
        while(moreEntries ) {
            //reset the entries in our list
            ArrayList<String> newEntries = new ArrayList<String>();
            //go through every col and prompt for input
            if(DEBUG) System.out.println("tableData = " + Arrays.toString(tableData));
            for (String i : tableData) {
                if(DEBUG)  System.out.println(i);
                String[] splitCol = i.split(" ");
                String colName = splitCol[0];
                int colSize = Integer.parseInt(splitCol[1]);
                System.out.print(colName + " (" + colSize + "): ");
                String col = in.nextLine();
                //ensure that user input is of proper size
                if (col.length() > colSize) {
                    System.err.println("Error - input data exceeds size for column");
                    return;
                }
                while(col.length() - 1 != colSize)
                    col += " ";
                newEntries.add(col);
                System.out.println();
            }
            //append new entries to file

            for ( String x : newEntries) {
                if(DEBUG) {
                    System.out.println("APPENDING: "+ x);
                }
                fw.append(x + "\t");
            }
            fw.append("\n");
            //ask user if they'd like to add more entries
            System.out.print("More entries (Y / n): ");
            String more = in.nextLine().toLowerCase();
            System.out.println();
            if(!more.equals("y")) {
                moreEntries = false;
            }
        }//end moreEntries loop
        fw.close();
        return;
    }


    //mark db entry for removal
    public static void remove(Scanner in) {

    }


    //print contents of dbfile
    public static void printFile(Scanner in) {
        System.out.print("Enter file name: ");
        String table = in.nextLine();
        BufferedReader br = null;
        if(!table.contains(".db")) {
            table+=".db";
        }
        try {
            br = new BufferedReader(new FileReader(CATALOG_PATH + table));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error - table does not exist.");
            return;
        }
    }


    //delete entries from table marked by the remove function
    public static void purge(File catalog, HashMap<String, String[]> db) {

    }



}//end Main class
