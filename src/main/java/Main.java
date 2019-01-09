public class Main {

    public Main() {
    }

    public static void main (String[] args){
        UserInterface userInterface = new UserInterface();
        Thread gui = new Thread(userInterface);
        gui.setPriority(Thread.MIN_PRIORITY);
        gui.start();
    }


}
