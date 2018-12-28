import java.io.File;
import java.util.Random;

public class Main {



    public Main() {
    }

    public static void main (String[] args){
        Main main = new Main();

        final File folder = new File("C:\\Users\\QWMQ5885\\Desktop\\New folder (2)");
        //final File folder = new File("C:\\Users\\QWMQ5885\\Desktop\\New folder (2)\\CDR_20181101.tsv");
        //main.listFilesForFolder(folder);


        //main.readFile("C:\\Users\\QWMQ5885\\Desktop\\New folder (2)\\aaa.tsv");
        //main.readFile(folder.getPath());
    }


    public void listFilesForFolder(File folder) {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //listFilesForFolder(fileEntry);
            } else {
                if(fileEntry.getName().contains(".tsv")) {
                    System.out.println("---START " + fileEntry.getName() + "---");
                    //readFile(fileEntry.getPath());
                    System.out.println("---FINISH " + fileEntry.getName() + "---");
                }
                //System.out.println(fileEntry.getName());
            }
        }
    }



    private String newNumber(){
        String NUMBERS ="0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        while (stringBuilder.length()<3){
            int index = (int) (random.nextFloat() * NUMBERS.length());
            stringBuilder.append(NUMBERS.charAt(index));
        }
        return stringBuilder.toString();
        //return "***";
    }

}
