public class Launcher {

    /*
     * Yes, this is weird. Yes, it is necessary. If we use SEP as our main class
     * then java will shit itself thinking javafx is not present in java 11.
     * https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing
     * */

    public static void main(String[] args) {
        SEP.main(args);
    }
}
