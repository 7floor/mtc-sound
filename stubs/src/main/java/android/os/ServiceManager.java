package android.os;

@SuppressWarnings("UnusedParameters")
public class ServiceManager {
    public static IBinder getService(String name) { return new IBinder() {} ; }
    public static void addService(String paramString, IBinder paramIBinder) {}
}
