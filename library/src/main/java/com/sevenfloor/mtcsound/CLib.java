package com.sevenfloor.mtcsound;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CLib extends Library {
    CLib instance = (CLib) Native.loadLibrary(Platform.C_LIBRARY_NAME, CLib.class);

    int O_RDWR = 0x0002;
    int I2C_RETRIES = 0x0701;
    int I2C_SLAVE = 0x0703;

    int open(String path, int flags);
    int close(int fd);
    int ioctl(int fd, int request, Object... args);
    int write(int fd, byte[] buffer, int count);
}
