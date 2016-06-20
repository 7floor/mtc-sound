package com.sevenfloor.mtcsound;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class I2cBus {



    public static String write(String fileName, int address, byte[][] data) {
        int errno;
        try {
            int file = CLib.instance.open(fileName, CLib.O_RDWR);
            if (file < 0) {
                errno = Native.getLastError();
                return String.format("Error in open() %d", errno);
            }

            if (CLib.instance.ioctl(file, CLib.I2C_RETRIES, (int) 2) < 0) {
                errno = Native.getLastError();
                CLib.instance.close(file);
                return String.format("Error in ioctl(retries) %d", errno);
            }

            if (CLib.instance.ioctl(file, CLib.I2C_SLAVE, address) < 0) {
                errno = Native.getLastError();
                CLib.instance.close(file);
                return String.format("Error in ioctl(slave) %d", errno);
            }

            for (byte[] chunk : data) {
                int len = chunk.length;
                if (CLib.instance.write(file, chunk, len) != len) {
                    errno = Native.getLastError();
                    CLib.instance.close(file);
                    String errorMessage = String.format("Error in write() %d", errno);
                    if (errno == 11 || errno == 110) // EAGAIN (3188) || ETIMEOUT (3066)
                        errorMessage = errorMessage + " (no response from i2c slave)";
                    return errorMessage;
                }
            }

            CLib.instance.close(file);

            return null;

        } catch (Throwable e) {
            return String.format("Exception %s", e.getMessage());
        }
    }
}

