package com.sevenfloor.mtcsound;

import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class SoftwareChecker {

    private static List<String> fileNames = new ArrayList<>();

    public static List<String> getFileNames() {
        return fileNames;
    }

    public static String check() {
        String message;
        Log.i(Utils.logTag, "Checking software");

        try {
            CLib dummy = CLib.instance;
        } catch (Throwable e) {
            message = String.format("Exception %s", e.getMessage());
            Log.e(Utils.logTag, message);
            return message;
        }

        Log.i(Utils.logTag, "Looking for i2c device files");
        fileNames = getDeviceFileNames();
        if (fileNames.isEmpty()) {
            Log.i(Utils.logTag, "None found, trying loading device driver");
            message = tryLoadDriver();
            if (message != null) {
                message = "Error loading device driver: " + message;
                Log.e(Utils.logTag, message);
                return message;
            }
            Log.i(Utils.logTag, "Device driver loaded, looking for i2c device files");
            fileNames = getDeviceFileNames();
            if (fileNames.isEmpty()) {
                message = "Device driver seems non-functional: no i2c device files found";
                Log.e(Utils.logTag, message);
                return message;
            }
        }

        Log.i(Utils.logTag, String.format("Found %d i2c device files, setting permissions", fileNames.size()));
        java.util.Collections.sort(fileNames);
        int i = 0;
        while (i < fileNames.size())
        {
            String fileName = fileNames.get(i);
            message = setFilePermissions(fileName);
            if (message == null) {
                Log.i(Utils.logTag, fileName + " - OK");
                i++;
            } else {
                Log.e(Utils.logTag, fileName + " - " + message);
                fileNames.remove(i);
            }
        }

        if(fileNames.isEmpty()) {
            message = "Error setting permissions for i2c device files, possibly root access issue";
            Log.e(Utils.logTag, message);
            return message;
        }

        return null;
    }

    private static List<String> getDeviceFileNames()
    {
        List<String> result = new ArrayList<>();
        File dir = new File("/dev");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith("i2c-");
            }
        });

        for (File f : files) {
            result.add(f.getAbsolutePath());
        }

        return result;
    }

    private static String tryLoadDriver() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c","insmod /system/lib/modules/i2c-dev.ko"});
            process.waitFor();
            int ev = process.exitValue();
            if (ev == 0)
                return null;
            throw new Throwable("su exit code " + ev);
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    private static String setFilePermissions(String fileName) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", String.format("chmod 666 %s", fileName)});
            process.waitFor();
            int ev = process.exitValue();
            if (ev == 0)
                return null;
            throw new Throwable("su exit code " + ev);
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

}
