package com.sevenfloor.mtcsound;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.io.IOException;

public class I2cBus {

    private interface CLib extends Library {
        CLib instance = (CLib) Native.loadLibrary(Platform.C_LIBRARY_NAME, CLib.class);

        int O_RDWR = 0x0002;
        int I2C_RETRIES = 0x0701;
        int I2C_SLAVE = 0x0703;

        int open(String path, int flags);
        int close(int fd);
        int ioctl(int fd, int request, Object... args);
        int write(int fd, byte[] buffer, int count);
    }

    public static int currentBusNumber = 4;
    public static String lastError = "";

    public static int getDevFileAccess(int busNumber) {
        lastError = "";
        currentBusNumber = busNumber;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", String.format("chmod 666 /dev/i2c-%d", currentBusNumber)});
            process.waitFor();
            int ev = process.exitValue();
            if (ev != 0)
                lastError = String.format("su exit code %s", ev);
            return ev;
        } catch (Throwable e) {
            lastError = String.format("Exception %s", e.getMessage());
            return -1;
        }
    }

    public static int write(int address, byte[][] data) {
        int errno;
        lastError = "";
        try {
            int file = CLib.instance.open(String.format("/dev/i2c-%d", currentBusNumber), CLib.O_RDWR);
            if (file < 0) {
                errno = Native.getLastError();
                lastError = String.format("Error in open() %d", errno);
                return errno;
            }

            if (CLib.instance.ioctl(file, CLib.I2C_RETRIES, (int) 0) < 0) {
                errno = Native.getLastError();
                lastError = String.format("Error in ioctl(retries) %d", errno);
                CLib.instance.close(file);
                return errno;
            }

            if (CLib.instance.ioctl(file, CLib.I2C_SLAVE, address) < 0) {
                errno = Native.getLastError();
                lastError = String.format("Error in ioctl(slave) %d", errno);
                CLib.instance.close(file);
                return errno;
            }

            for (byte[] chunk : data) {
                int len = chunk.length;
                if (CLib.instance.write(file, chunk, len) != len) {
                    errno = Native.getLastError();
                    lastError = String.format("Error in write() %d", errno);
                    CLib.instance.close(file);
                    return errno;
                }
            }

            CLib.instance.close(file);

            return 0;

        } catch (Throwable e) {
            lastError = String.format("Exception %s", e.getMessage());
            return -1;
        }
    }
}
