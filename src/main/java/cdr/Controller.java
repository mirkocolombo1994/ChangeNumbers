package cdr;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller implements Runnable{
    private static Controller ourInstance = null;

    private static final int dimension = 1000000;

    private Integer[] positions = new Integer[3];

    private boolean hideFinaldigits = false;

    private int finalDigitsToChange = 3;

    private DatabaseController database = DatabaseController.getInstance();

    private File[] selectedFiles = null;

    public static Controller getInstance() {
        if(ourInstance==null) ourInstance = new Controller();
        return ourInstance;
    }

    public void setFinalDigitsToChange(int finalDigitsToChange) {
        this.finalDigitsToChange = finalDigitsToChange;
    }

    private Controller() {

    }

    public void setHideFinaldigits(boolean hideFinaldigits) {
        this.hideFinaldigits = hideFinaldigits;
    }

    /**
     * Read the file and analise each line for data.
     * For each line it writes it in a file.
     * When the number of lines reaches <i>dimension</i> the new lines will be written in a new file
     * @param file the file to analise
     */
    private void readFile(Path file) {
        try {
            //the reader for the file
            BufferedReader br = new BufferedReader(new FileReader(file.toString()));

            //the fragmentation index for new files
            int fileFragmentation = 1;

            //Where we will store the new path of the fragmented files
            Path fileToWrite;

            //create the first fragmented file
            fileToWrite = createNewFile(file,fileFragmentation);
            fileFragmentation++;

            //preparing for writing in the fragmented file
            Writer output = null;
            try {
                output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite.toString(),true),"UTF-8"));
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            }

            //Reading a new line in the file
            String nextLine = br.readLine();

            //Keeping track of:
            int numLine = 0; //the line of the file for cutting it in fragment
            int numLineProcessed = 0; //the numbers of line processed in total
            String firstLine = null;
            //While we not finish the file
            while (nextLine!=null){
                //if it's the first line of the fragmented file
                if(numLineProcessed==0){
                    //if if the first line of the file
                    if(firstLine == null)
                        //analise the line to search for the index
                        firstLineAnalyzator(nextLine);
                        firstLine = nextLine;
                    assert output != null;
                    //write in the fragmented file the first line
                    output.append(firstLine).append("\n");
                }else{
                    //analise the line of the file and change numbers
                    String newLine = lineAnalyzator(nextLine);
                    //then writing in the fragmented file
                    output.append(newLine).append("\n");
                }

                //if we have processed 'dimension' lines, it's time to create a new fragment file
                if(numLine==dimension){
                    //closing the old one
                    output.close();
                    //create the new file
                    fileToWrite = createNewFile(file,fileFragmentation);
                    fileFragmentation++;
                    //opening the writing tool
                    try {
                        output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite.toString(),true),"UTF-8"));
                    } catch (UnsupportedEncodingException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //writing the first line
                    output.append(firstLine).append("\n");
                    numLine=0;
                }

                numLine++;
                numLineProcessed++;
                nextLine = br.readLine();

                if(numLineProcessed%1000000==0 && numLineProcessed!=0) System.out.println(numLineProcessed + " lines done!");
                if(numLineProcessed%100000==0 && numLineProcessed!=0 && numLineProcessed%1000000!=0) System.out.print(".");

            }

            assert output != null;
            output.close();

            System.gc();
            //Garbage collector

        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * Select and set the columns that we are interested, the calling and the called number columns
     * @param data The data contained in the file that has to be analyzed
     */
    private void selectColumns(String[] data){
        for(int i = 0; i< data.length; i++){
            if(data[i].equals("CALLING_NUMBER")) positions[0]=i;
            if(data[i].equals("CALLED_PUB_NUMBER")) positions[1]=i;
            if(data[i].equals("DECO")) positions[2]=i;
        }
    }

    /**
     * Gives the new line to put in the file, with the number changed
     * @param line the line of the file to be analyzed
     * @return the new line with the modified data
     */
    private String lineAnalyzator(String line) {
        String[] data = line.split("\t");

        //List<String> possibleDuplicates = new ArrayList<>();

        /*if(data[positions[0]].length()>3) possibleDuplicates.add(data[positions[0]]);
        data[positions[0]]=changeNumber(data[positions[0]]);*/

        String[] numbers = new String[positions.length];

        for (int i = 0; i < positions.length; i++) {
            try{
                numbers[i] = data[positions[i]];
            }catch (ArrayIndexOutOfBoundsException aobe){
                //Simply there's not a number
                numbers[i] = "null";
            }
        }

        String[] newnumbers = database.changeNumbers(numbers,hideFinaldigits, finalDigitsToChange);

        for (int i = 0; i < positions.length; i++) {
            try{
                if (!data[positions[i]].equals("null"))
                    data[positions[i]] = newnumbers[i];
                //numbers[i] = data[positions[i]];
            }catch (ArrayIndexOutOfBoundsException aobe){
                //Same as above!
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

    /**
     * It analise the first line of the file, selecting the index columns that contains data
     * @param firstLine the first line of the file
     */
    private void firstLineAnalyzator(String firstLine){
        String[] data = firstLine.split("\t");
        selectColumns(data);
    }

    public void setPath(File[] selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        readAllFiles();
        //test();
    }

    /**
     * Then it takes every file in the memorized file and change the numbers for each of them.
     * When a file is done, the system memorize all the new numbers both in the file and in memory.
     * @throws NullPointerException if there are no files in the selectedFiles array.
     */
    private void readAllFiles()  {
        if(selectedFiles==null) throw new NullPointerException();

        //loadDb();

        for (File selectedFile : selectedFiles) {
            if(!selectedFile.getName().contains("corr")){
                if (selectedFile.getName().contains(".tsv")) {
                    System.out.println("---START " + selectedFile.getName() + "---");
                    readFile(selectedFile.toPath());
                    System.out.println("---FINISH " + selectedFile.getName() + "---");
                    System.out.println("------------STORING NUMBERS-----------");
                    database.storeMap();
                    System.out.println("--------NUMBER STORED----------------");
                }
            }
        }
        System.out.println("File Finished!");

    }

}
