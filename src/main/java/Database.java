import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Mirko Colombo on 09/01/2019.
 */
class Database {

    //Dimension in bytes. So 100000000 = 100 MB
    private static final long dimensionDatabase = 100000000;

    private static Database ourInstance = null;
    private boolean dbModified = false;

    static Database getInstance(){
        if(ourInstance==null) ourInstance = new Database();
        return ourInstance;
    }

    private Path databasePath =null;
    private HashMap<String, String> numberMap = new HashMap<>();
    private HashMap<String, String> newNumberMap = new HashMap<>();

    private static int databaseTrunk = 1;
    private static final String databaseRoot = "D:\\aewwc\\Desktop\\ORange\\CDR";

     void loadDataFile(){
        boolean present = false;

        if(databasePath ==null){
            databasePath = Paths.get(databaseRoot);
        }
        for (File fileEntry : Objects.requireNonNull(databasePath.toFile().listFiles())) {
            if (!fileEntry.isDirectory()) {
                if (fileEntry.getName().contains("data")) {
                    present = true;
                    System.out.println("---START " + fileEntry.getName() + "---");
                    loadDataFile(fileEntry.toPath());
                    databaseTrunk++;
                    System.out.println("---FINISH " + fileEntry.getName() + "---");
                }
            }
        }
        if(!present){
            newDatabaseFile();
        }
        System.out.println("There are " + numberMap.size() + " elements in memory!");
    }

    /**
     * load database file in memory
     * @param path the database file to be loaded in memory
     */
    private void loadDataFile(Path path) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path.toString()));

            String line = br.readLine();

            System.out.println("Start loading db");

            while (line!=null){
                String[] data = line.split(";");
                if(numberMap.containsKey(data[0])) System.out.println("H");
                addToMap(data[0],data[1]);
                line = br.readLine();
            }

            System.out.println("Finish loading db");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                assert br != null;
                br.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void newDatabaseFile() {
        File newFile = new File(databaseRoot + "\\data_" + databaseTrunk);
        databasePath = newFile.toPath();
        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        databaseTrunk++;
    }

    private void addToMap(String oldNumber, String fakeNumber){
        numberMap.put(oldNumber,fakeNumber);
    }

    private Path getDatabasePath() {
        return databasePath;
    }

    private void createNewDatabaseFile(){
        newDatabaseFile();
    }

    private void addToMap(Map<String, String> map){
        numberMap.putAll(map);
    }

    private boolean containsKey(String key){
        return numberMap.containsKey(key);
    }

    private String get(String key){
        return numberMap.get(key);
    }

    String getNewNumber(String oldNumber, int finalDigitsToChange, boolean hideFinaldigits) {
        String newData = oldNumber;

        if(containsKey(oldNumber))
            //newData = numberMap.get(oldNumber).getNumber();
            newData = get(oldNumber);
        else if (newNumberMap.containsKey(oldNumber))
            //newData = newNumberMap.get(oldNumber).getNumber();
            newData = newNumberMap.get(oldNumber);
        else if (oldNumber.length() > 3) {
            dbModified = true;
            FakeNumber fknbr;
            do {
                //TODO remove TypeNumber 'cause a number can both be a called and a calling number
                fknbr = new FakeNumber(oldNumber, finalDigitsToChange);
            }while (oldNumber.equals(fknbr.getNumber()));
            newNumberMap.put(oldNumber,fknbr.getNumber());
            newData = fknbr.getNumber();
            fknbr.clear();
        }

        if(hideFinaldigits){
            StringBuilder finale = new StringBuilder();
            for (int i = 0; i < finalDigitsToChange; i++) {
                finale.append("*");
            }
            newData = newData.substring(0,newData.length()-finalDigitsToChange) + finale.toString();
        }

        return newData;
    }

    /**
     * Stores the map in database files and transfer the newNumberMap (containing all the numbers contained in the last file) into the numberMap ( that contains all the numbers loaded in memory)
     */
     void storeMap() {
        if(dbModified) {

            File file = new File(getDatabasePath().toString());

            Writer output = null;

            for (String key : newNumberMap.keySet()) {
                String line = key + ";" + newNumberMap.get(key);

                //controlling if the file reaches the maximum size
                if(file.length()>dimensionDatabase){
                    //if yes, we close it
                    try {
                        assert output != null;
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //and then create a new one
                    createNewDatabaseFile();
                    //and store it in the method for the next control
                    file = new File(getDatabasePath().toString());
                }

                //preparing for writing in the file
                try {
                    output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getDatabasePath().toString(),true),"UTF-8"));
                } catch (UnsupportedEncodingException | FileNotFoundException e) {
                    e.printStackTrace();
                }

                //write the line
                try {
                    assert output != null;
                    output.append(line).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //at the end we close it
            try {
                assert output != null;
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Added " + newNumberMap.size() + " new numbers!");

            //and add all the new numbers in the numberMap
            addToMap(newNumberMap);

            //clearing the newNumberMap
            newNumberMap.clear();

            System.gc();

        }

        dbModified=false;
    }
}
