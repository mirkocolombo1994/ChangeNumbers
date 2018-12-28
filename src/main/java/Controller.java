import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

class Controller {
    private static Controller ourInstance = new Controller();

    private static final int dimension = 1000000;
    //private static final int dimension = 9;

    private HashMap<String,FakeNumber> numberMap = new HashMap<>();

    private Integer[] positions = new Integer[2];

    private String firstLine;

    private Path oldNumbersPath = Paths.get("C:\\Users\\QWMQ5885\\Desktop\\New folder (2)\\corr");

    private boolean dbModified = false;

    private boolean dbFirstLoad = true;

    private File[] selectedFiles = null;

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

            BufferedReader br = new BufferedReader(new FileReader(oldNumbersPath.toString()));

            String line = br.readLine();

            System.out.println("Start loading db");

            while (line!=null){
                String[] data = line.split(";");
                FakeNumber fn;
                TypeNumber tn;
                if(data[2].equals("CD")) tn = TypeNumber.CALLED;
                else tn = TypeNumber.CALLING;

                fn = new FakeNumber(tn,data[1]);
                addToMap(data[0],fn);
                line = br.readLine();
            }

            System.out.println("Finish loading db");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile(String filePath) throws TooFewDigitsException {
        /*try {

            Path path = Paths.get(filePath);
            //List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            //Files.newBufferedReader(path,StandardCharsets.UTF_8);
            List<String> linesPartitioned = new ArrayList<>(dimension);

            int fileFragmentation = 1;
            int numLine=0;
            int numLinePartitioned=0;

            String partitionedFileName;
            String newLine;

            BufferedReader br = new BufferedReader(new FileReader(filePath));

            String nextLine = br.readLine();

            while (nextLine!=null){

                if(numLine==0){
                    newLine = firstLineAnalyzator(nextLine);
                }else{
                    newLine = lineAnalyzator(nextLine);
                    numLinePartitioned++;
                }
                linesPartitioned.add(newLine);
                numLine++;
                //System.out.println(nextLine);
                nextLine= br.readLine();


                if(numLinePartitioned%(dimension/2)==0 && numLinePartitioned!=0) System.out.println("____________HALF________");

                if(numLinePartitioned%10==0 && numLinePartitioned!=0) System.out.println(numLinePartitioned + " lines done!");

                if(numLinePartitioned%dimension==0 && numLinePartitioned!=0){
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
        }*/

        List<String> linesPartitioned = new ArrayList<>(dimension);
        Path path = Paths.get(filePath);

        int fileFragmentation = 1;
        int numLine=0;
        int numLinePartitioned=0;


        String newLine;

        FileInputStream inputStream = null;
        Scanner scanner = null;

        String nextLine;

        try {

            inputStream = new FileInputStream(filePath);
            scanner = new Scanner(inputStream,"UTF-8");

            while (scanner.hasNextLine()){
               /*PUT THE CODE HERE*/
                nextLine = scanner.nextLine();
                if(numLine==0){
                    newLine = firstLineAnalyzator(nextLine);
                }else{
                    newLine = lineAnalyzator(nextLine);
                    numLinePartitioned++;
                }
                linesPartitioned.add(newLine);
                numLine++;

                System.out.println(numLinePartitioned);

                if(numLinePartitioned%(dimension/2)==0 && numLinePartitioned!=0) System.out.println("____________HALF________");

                if(numLinePartitioned%dimension==0 && numLinePartitioned!=0){
                    Files.write(createNewFile(path,fileFragmentation),linesPartitioned,StandardCharsets.UTF_8);
                    linesPartitioned.clear();
                    //linesPartitioned = new ArrayList<>(dimension);
                    linesPartitioned.add(firstLine);

                    System.gc();

                    fileFragmentation++;
                    numLinePartitioned=0;
                }

                System.gc();
            }

            if(numLinePartitioned!=0) {
                Files.write(createNewFile(path,fileFragmentation), linesPartitioned, StandardCharsets.UTF_8);
            }

            System.gc();

            storeMap();

            if(scanner.ioException() !=null){
                throw scanner.ioException();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(scanner!=null) scanner.close();
        }


    }

    private Path createNewFile(Path path, int fileFragmentation) throws IOException {
        String partitionedFileName;
        partitionedFileName = path.getParent() + "\\" +  path.getFileName().toString().substring(0,path.getFileName().toString().indexOf("."))+"_" + fileFragmentation + path.getFileName().toString().substring(path.getFileName().toString().indexOf("."));
        Path newFilePath =  Paths.get(partitionedFileName);
        File newFile = new File(newFilePath.toString());
        newFile.createNewFile();
        return newFilePath;
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
                    //System.out.println("Add " + oldNumber);
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

    private void test(String filePath){
        FileInputStream inputStream = null;
        Scanner scanner = null;
        try {

            inputStream = new FileInputStream(filePath);
            scanner = new Scanner(inputStream,"UTF-8");

            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                System.out.println(line);
            }

            if(scanner.ioException() !=null){
                throw scanner.ioException();
            }


        } catch (IOException e) {
                e.printStackTrace();
        }finally {
            if(inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(scanner!=null) scanner.close();
        }

    }

    void readAllFiles(String path) throws TooFewDigitsException {

        if(selectedFiles==null) throw new NullPointerException();

        loadDb();

        for (File selectedFile : selectedFiles) {
            if(!selectedFile.getName().contains("corr")){
                if (selectedFile.getName().contains(".tsv")) {
                    System.out.println("---START " + selectedFile.getName() + "---");
                    readFile(selectedFile.getPath());
                    //test(fileEntry.getPath());
                    System.out.println("---FINISH " + selectedFile.getName() + "---");
                }
            }
        }

    }

    private void loadDb() {
        if(dbFirstLoad){
            loadNumbers();
            dbFirstLoad=false;
        }
    }


    private void addToMap(String oldNumber, FakeNumber fakeNumber){
        numberMap.put(oldNumber,fakeNumber);

        //System.out.println("PUT " + oldNumber);

    }

    public void setPath(File[] selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    //TODO Create a file with all the real numbers and the corrisponding fake number with the type (called or calling)
    //TODO Load that file at the beginning of the app

    //TODO Use a hashMap with a string and an object (Fake number, containing both the fake number and the type)


}
