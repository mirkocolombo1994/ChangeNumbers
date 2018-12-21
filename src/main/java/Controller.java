import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Controller {
    private static Controller ourInstance = new Controller();

    private static final int dimension = 1000000;

    private HashMap<String,FakeNumber> numberMap = new HashMap<>();

    private Integer[] positions = new Integer[2];

    private String firstLine;

    private Path oldNumbersPath = Paths.get("C:\\Users\\QWMQ5885\\Desktop\\New folder (2)\\corr");

    private boolean dbModified = false;

    static Controller getInstance() {
        return ourInstance;
    }

    private Controller() {

    }

    private void loadNumbers() {
        try {

            if(Files.notExists(oldNumbersPath)){
                Files.createFile(oldNumbersPath);
            }

            List<String> lines = Files.readAllLines(oldNumbersPath,StandardCharsets.UTF_8);

            for (String line : lines) {
                String[] data = line.split(";");
                FakeNumber fn;
                TypeNumber tn;
                if(data[2].equals("CD")) tn = TypeNumber.CALLED;
                else tn = TypeNumber.CALLING;

                fn = new FakeNumber(tn,data[1]);
                addToMap(data[0],fn);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile(String filePath) throws TooFewDigitsException {
        try {

            Path path = Paths.get(filePath);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> linesPartitioned = new ArrayList<>(dimension);

            int fileFragmentation = 1;
            int numLine=0;
            int numLinePartitioned=0;

            String partitionedFileName;

            for (String line : lines) {
                String newLine;
                if(numLine==0){
                    newLine = firstLineAnalyzator(line);
                }else{
                    newLine = lineAnalyzator(line);
                    numLinePartitioned++;
                }
                linesPartitioned.add(newLine);
                numLine++;

                if(numLinePartitioned%dimension==0){
                    partitionedFileName = path.getParent() + "\\" +  path.getFileName().toString().substring(0,path.getFileName().toString().indexOf("."))+"_" + fileFragmentation + path.getFileName().toString().substring(path.getFileName().toString().indexOf("."));
                    Path newFilePath =  Paths.get(partitionedFileName);
                    File newFile = new File(newFilePath.toString());
                    newFile.createNewFile();

                    Files.write(newFilePath,linesPartitioned,StandardCharsets.UTF_8);
                    linesPartitioned = new ArrayList<>(dimension);
                    linesPartitioned.add(firstLine);

                    System.gc();

                    fileFragmentation++;
                    numLinePartitioned=0;
                }
            }

            partitionedFileName = path.getParent() + "\\" +  path.getFileName().toString().substring(0,path.getFileName().toString().indexOf("."))+"_" + fileFragmentation + path.getFileName().toString().substring(path.getFileName().toString().indexOf("."));
            Path newFilePath =  Paths.get(partitionedFileName);
            File newFile = new File(newFilePath.toString());
            newFile.createNewFile();

            Files.write(newFilePath,linesPartitioned,StandardCharsets.UTF_8);

            System.gc();

            storeMap();

            //Garbage collector
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeMap() {
        if(dbModified) {
            List<String> map = new ArrayList<>();

            for (String key : numberMap.keySet()) {
                map.add(key + ";" + numberMap.get(key).toString());
            }

            try {
                Files.write(oldNumbersPath, map, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dbModified=false;
    }

    /**
     * Select and set the columns that we are interested, the calling and the called number columns
     * @param data The data contained in the file that has to be analyzed
     */
    private void selectColumns(String[] data){
        for(int i = 0; i< data.length; i++){
            if(data[i].equals("CALLING_NUMBER")) positions[0]=i;
            if(data[i].equals("CALLED_PUB_NUMBER")) positions[1]=i;
        }
    }

    /**
     * Gives the new line to put in the file, with the number changed
     * @param line the line of the file to be analyzed
     * @return
     * @throws TooFewDigitsException
     */
    private String lineAnalyzator(String line) throws TooFewDigitsException {
        String[] data = line.split("\t");
        for(int i =0; i<2; i++){
            try {
                String oldNumber = data[positions[i]];
                if(numberMap.containsKey(oldNumber))
                    data[positions[i]]=numberMap.get(oldNumber).getNumber();
                else if (oldNumber.length() > 3) {
                    dbModified = true;
                    FakeNumber fknbr = new FakeNumber(TypeNumber.CALLED,oldNumber,false);

                    addToMap(oldNumber,fknbr);
                    data[positions[i]] = fknbr.getNumber();



                }
            }catch (ArrayIndexOutOfBoundsException ex){
                //Simply the program have not found neither calling nor called number.
            }
        }

        StringBuilder newDataString = new StringBuilder();
        for(int i=0;i<data.length-1;i++){
            newDataString.append(data[i]);
            newDataString.append("\t");
        }
        newDataString.append(data[data.length-1]);

        return newDataString.toString();
    }

    private String firstLineAnalyzator(String firstLine){
        String[] data = firstLine.split("\t");
        selectColumns(data);
        this.firstLine=firstLine;
        return firstLine;
    }

    private String test(String filePath) {
        Path path = Paths.get(filePath);
        return path.getFileName().toString().substring(0,path.getFileName().toString().indexOf("."))+"_1" + path.getFileName().toString().substring(path.getFileName().toString().indexOf("."));
    }

    public void readAllFiles(String path) throws TooFewDigitsException {

        loadNumbers();

        File folder = new File(path);

        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
            } else {
                if(!fileEntry.getName().contains("corr")) {
                    if (fileEntry.getName().contains(".tsv")) {
                        System.out.println("---START " + fileEntry.getName() + "---");
                        readFile(fileEntry.getPath());
                        System.out.println("---FINISH " + fileEntry.getName() + "---");
                    }
                }
                //System.out.println(fileEntry.getName());
            }
        }


    }


    private void addToMap(String oldNumber, FakeNumber fakeNumber){
        numberMap.put(oldNumber,fakeNumber);

        System.out.println("PUT " + oldNumber);

    }

    //TODO Create a file with all the real numbers and the corrisponding fake number with the type (called or calling)
    //TODO Load that file at the beginning of the app

    //TODO Use a hashMap with a string and an object (Fake number, containing both the fake number and the type)


}
