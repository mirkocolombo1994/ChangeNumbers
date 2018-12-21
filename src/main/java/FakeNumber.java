import java.util.Random;

public class FakeNumber {

    private static final String defaultChar = "*";
    private static final int defaultFinalDigits = 3;
    private String number;
    private TypeNumber type;

    public FakeNumber(TypeNumber type, String number) throws TooFewDigitsException {
        this.number = changeFinals(number,defaultFinalDigits,false, null);
        this.type = type;
    }

    public FakeNumber(TypeNumber type, String number, boolean hide) throws TooFewDigitsException {
        this.number = changeFinals(number,defaultFinalDigits,hide, defaultChar);
        this.type = type;
    }

    public FakeNumber(TypeNumber type, String number, boolean hide, String hideChar) throws TooFewDigitsException {
        this.number = changeFinals(number,defaultFinalDigits,hide, hideChar);
        this.type = type;
    }

    public FakeNumber(TypeNumber type, String number, int finalDigits) throws TooFewDigitsException {
        this.number = changeFinals(number,finalDigits,true, defaultChar);
        this.type=type;
    }

    public FakeNumber(TypeNumber type, String number, int finalDigits, boolean hide) throws TooFewDigitsException {
        this.number = changeFinals(number,finalDigits,hide, null);
        this.type=type;
    }

    public FakeNumber(TypeNumber type, String number, int finalDigits, boolean hide, String hideChar) throws TooFewDigitsException {
        this.number = changeFinals(number,finalDigits,hide, hideChar);
        this.type=type;
    }


    /**
     * Get a random final for the number
     * @param finalDigits the number of digits to randomize
     * @return
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
     * Get the hidden final for the number
     * @param finalDigits the number of final digits
     * @param hideChar the hide char
     * @return the hidden final to add to the number
     */
    private String hideFinal(int finalDigits, String hideChar){
        StringBuilder hiding = new StringBuilder();
        for (int i = 0; i < finalDigits; i++) {
            hiding.append(hideChar);
        }
        return hiding.toString();
    }

    /**
     * Given a number, this method change or hide with a char, the final digits.
     * @param number the number to change
     * @param finalDigits then number of final digits to change
     * @param hide if the final digits have to be hide
     * @param hideChar the char to hide the final digits (if hide is enabled). Can be setted to null
     * @return the number with the final digits changed
     * @throws TooFewDigitsException if the final digits is minor or equal to 1
     */
    private String changeFinals(String number, int finalDigits, boolean hide, String hideChar) throws TooFewDigitsException {
        if(finalDigits<2) throw new TooFewDigitsException();
        if(hide)
            if(hideChar==null) hideChar = defaultChar;
        StringBuilder newNumber = new StringBuilder();
        newNumber.append(number, 0, number.length() - finalDigits);
        if (hide) {
            newNumber.append(hideFinal(finalDigits, hideChar));
        } else {
            newNumber.append(randomFinal(finalDigits));
        }
        return newNumber.toString();
    }

    public String getNumber() {
        return number;
    }

    public TypeNumber getType() {
        return type;
    }


}
