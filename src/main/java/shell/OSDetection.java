package shell;

public class OSDetection {

    private OSDetection() {}

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("windows");
    }
}
