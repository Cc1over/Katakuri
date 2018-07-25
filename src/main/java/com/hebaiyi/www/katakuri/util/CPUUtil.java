package com.hebaiyi.www.katakuri.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class CPUUtil{

    /**
     *  获取CPU
     * @return
     */
    public static int obtainCPUCoreNum(){
        File dir = new File("/sys/devices/system/cpu/");
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return Pattern.matches("cpu[0-9]", file.getName());
            }
        });
        return files.length;
    }


}
