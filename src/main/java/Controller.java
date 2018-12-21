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

    static Controller getInstance() {
        return ourInstance;
    }

    private Controller() {

    }

    public void readFile(String filePath) throws TooFewDigitsException {
        try {

            Path path = Paths.get(filePath);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> linesPartitioned = new ArrayList<>(dimension);

            int fileFragmentation = 1;
            int numLine=0;

            String partitionedFileName = path.getParent() + "\\" +  path.getFileName().toString().substring(0,path.getFileName().toString().indexOf("."))+"_" + fileFragmentation + path.getFileName().toString().substring(path.getFileName().toString().indexOf("."));


            if(lines.size() < dimension){
                for (String line : lines) {
                    String newLine;
                    newLine = lineAnalizator(line,numLine);
                    lines.set(numLine,newLine);
                    numLine++;
                }
            }else{
                int numLinePartitioned=0;
                for (String line : lines) {

                    String newLine;
                    newLine = lineAnalizator(line,numLine);
                    linesPartitioned.add(newLine);
                    numLine++;
                    numLinePartitioned++;

                    if(numLine%dimension==0){
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
            }

            if(fileFragmentation==1){
                Files.write(path,lines,StandardCharsets.UTF_8);
            }
            else{
                Path newFilePath =  Paths.get(partitionedFileName);
                File newFile = new File(newFilePath.toString());
                newFile.createNewFile();

                Files.write(newFilePath,linesPartitioned,StandardCharsets.UTF_8);

                System.gc();
            }

            System.gc();
            //Garbage collector

            /*
            List<String> newLines = new ArrayList<>(1000000);
            long numLineOld = 0;
            int numLineNew = 0;
            for (String line : lines) {
                String newLine;
                newLine = lineAnalizator(line,numLineOld);
                newLines.set(numLineNew, newLine);
                lines.set((int) numLineOld,newLine);
                numLineOld++;
                numLineNew++;

                if(numLineOld%1000000 == 0){
                    path.getParent();
                    Path newPath = Paths.get(path.getParent() + path.getFileName().toString().substring(0,path.getFileName().toString().indexOf("."))+"_1" + path.getFileName().toString().substring(path.getFileName().toString().indexOf(".")))
                    File newFile = new File(newPath.toString());
                    newFile.createNewFile();
                    Files.write(newPath,newLines);
                    //TODO Create new file with name filename + "_1"
                }
            }

            Files.write(path,lines,StandardCharsets.UTF_8);
            System.gc();
            //Garbage collector*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void selectColumns(String[] data){
        for(int i = 0; i< data.length; i++){
            if(data[i].equals("CALLING_NUMBER")) positions[0]=i;
            if(data[i].equals("CALLED_PUB_NUMBER")) positions[1]=i;
        }
    }

    private String lineAnalizator(String line, long numLine) throws TooFewDigitsException {
        String[] data = line.split("\t");

        if (numLine==0) {
            selectColumns(data);
            firstLine=line;
            return line;

        } else {
            for(int i =0; i<2; i++){
                try {
                    String oldNumber = data[positions[i]];
                    if(numberMap.containsKey(oldNumber))
                        data[positions[i]]=numberMap.get(oldNumber).getNumber();
                    else if (oldNumber.length() > 3) {

                        FakeNumber fknbr = new FakeNumber(TypeNumber.CALLED,oldNumber);

                        numberMap.put(oldNumber,fknbr);
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
    }

    String test(String filePath) {
        Path path = Paths.get(filePath);
        return path.getFileName().toString().substring(0,path.getFileName().toString().indexOf("."))+"_1" + path.getFileName().toString().substring(path.getFileName().toString().indexOf("."));
    }

    public void readAllFiles(String path) throws TooFewDigitsException {

        File folder = new File(path);

        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
            } else {
                if(fileEntry.getName().contains(".tsv")) {
                    System.out.println("---START " + fileEntry.getName() + "---");
                    readFile(fileEntry.getPath());
                    System.out.println("---FINISH " + fileEntry.getName() + "---");
                }
                //System.out.println(fileEntry.getName());
            }
        }


    }

    //TODO Create a file with all the real numbers and the corrisponding fake number with the type (called or calling)
    //TODO Load that file at the beginning of the app

    //TODO Use a hashMap with a string and an object (Fake number, containing both the fake number and the type)


}
