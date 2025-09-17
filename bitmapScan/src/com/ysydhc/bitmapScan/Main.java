package com.ysydhc.bitmapScan;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        try {
            // 构建参数
            String rootPath;
            double maxValue = 1.00;
            if (args.length == 0) {
                rootPath = new File("").getAbsolutePath();
            } else if (args.length == 1) {
                rootPath = args[0];
            } else {
                rootPath = args[0];
                maxValue = Double.parseDouble(args[1]);
            }
            Utils.println("path:" + rootPath);
            Utils.println("divide img size: " + maxValue + "M");
            // 构建任务
            new ScanImgTask().start(rootPath, maxValue);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
