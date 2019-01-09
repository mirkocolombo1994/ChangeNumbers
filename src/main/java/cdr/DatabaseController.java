package cdr;

import cdr.model.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mirko Colombo on 09/01/2019.
 */
class DatabaseController {

    private Database database = Database.getInstance();

    private static DatabaseController instance = null;

    static DatabaseController getInstance() {
        if(instance==null) instance = new DatabaseController();
        return instance;
    }

    private DatabaseController(){

    }

    String[] changeNumbers(String[] numbers, boolean hideFinaldigits, int finalDigitsToChange) {
        List<String> possibleDuplicates = new ArrayList<>();
        for (int i = 0; i < numbers.length; i++) {
            if(!numbers[i].equals("null")){
                int duplicatePosition = numbers.length;
                boolean foundDuplicate = false;
                int indexNumberToChange = -1;
                if (!possibleDuplicates.isEmpty()) {
                    for (int duplicateIndex = 0; duplicateIndex < possibleDuplicates.size(); duplicateIndex++) {
                        try {
                            indexNumberToChange = numbers[i].indexOf(possibleDuplicates.get(duplicateIndex));
                            if (indexNumberToChange > -1) {
                                if (duplicateIndex < duplicatePosition) {
                                    duplicatePosition = duplicateIndex;
                                    foundDuplicate = true;
                                }
                                break;                          }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //System.out.println(line);
                            //no problem, in the file it doesn't exist the numbers
                        }
                    }
                }
                if (foundDuplicate) {
                    try {
                        numbers[i] = numbers[i].substring(0, indexNumberToChange) + numbers[duplicatePosition];
                    } catch (StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException e) {
                        System.out.println(numbers[i]);
                    }
                    //data[positions[i]].replace(possibleDuplicates.get(duplicatePosition),data[duplicatePosition]);
                } else {
                    try {
                        if (numbers[i].length() > 3) possibleDuplicates.add(numbers[i]);
                        numbers[i] = changeNumber(numbers[i],hideFinaldigits,finalDigitsToChange);

                    } catch (ArrayIndexOutOfBoundsException e) {
                        //System.out.println(line);
                    }
                }
            }
        }

        //TODO check if it returns the changed numbers!!!!
        return numbers;
    }

    private String changeNumber(String oldNumber, boolean hideFinaldigits, int finalDigitsToChange) {
        return database.getNewNumber(oldNumber, finalDigitsToChange,hideFinaldigits);
    }

    void loadDataFile() {
        database.loadDataFile();
    }

    void storeMap() {
        database.storeMap();
    }
}
