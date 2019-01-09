package cdr.model;

import java.util.Random;

public class FakeNumber {

    private static final int defaultFinalDigits = 3;
    private String number;
    private boolean called;

    public FakeNumber(boolean called, String fakeNumber){
        this.number = fakeNumber;
        this.called = called;
    }

    public FakeNumber(TypeNumber type, String number) {
        this.number = changeFinals(number,defaultFinalDigits);
    }

    FakeNumber(String number, int finalDigits){
        this.number = changeFinals(number,finalDigits);
        called=true;
    }

    public FakeNumber(TypeNumber type, String number, int finalDigits) {
        this.number = changeFinals(number,finalDigits);
    }


    /**
     * Get a random final for the number
     * @param finalDigits the number of digits to randomize
     * @return the random final to append at the number
     */
    private String randomFinal(int finalDigits){
        String NUMBERS ="0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        while (stringBuilder.length()<finalDigits){
            int index = (int) (random.nextFloat() * NUMBERS.length());
            stringBuilder.append(NUMBERS.charAt(index));
        }
        return stringBuilder.toString();
    }

    /**
     * Given a number, this method change or hide with a char, the final digits.
     * @param number the number to change
     * @param finalDigits then number of final digits to change
     * @return the number with the final digits changed
     */
    private String changeFinals(String number, int finalDigits) {
        //if(finalDigits<2) throw new TooFewDigitsException();
        StringBuilder newNumber = new StringBuilder();
        newNumber.append(number, 0, number.length() - finalDigits);
        newNumber.append(randomFinal(finalDigits));
        return newNumber.toString();
    }

    String getNumber() {
        return number;
    }

    @Override
    /*public String toString() {
        return number + ";" + getStringType();
    }*/

    public String toString() {
        return number + ";" + called;
    }

    void clear() {
        this.number=null;
    }
}
