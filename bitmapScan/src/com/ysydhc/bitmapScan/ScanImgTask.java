package com.ysydhc.bitmapScan;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanImgTask {

    private final List<File> analyseFiles = new ArrayList<>();
    private String rootPath = "";
    private double maxValueMB = 3.00;
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "png", "jpg", "jpeg", "webp", "gif", "bmp"
    ));
    private static final String BUILD_GENERATED = File.separator + "build" + File.separator + "generated";

    /**
     * 启动分析任务（异步）
     */
    public void start(String rootPath, double maxValueMB) {
        this.rootPath = rootPath;
        this.maxValueMB = maxValueMB;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this::runAnalysis);
        executor.shutdown();
    }

    /**
     * 启动分析任务（同步，便于测试）
     */
    public void startSync(String rootPath, double maxValueMB) {
        this.rootPath = rootPath;
        this.maxValueMB = maxValueMB;
        runAnalysis();
    }

    private void runAnalysis() {
        Utils.println("--------------------------------------------------------------------------------------------");
        File rootDir = new File(rootPath);
        if (!rootDir.isDirectory()) {
            Utils.println("Please input right path, rootPath is not Dir!!!");
            return;
        }
        analyseFiles.clear();
        try {
            traverseWithNio(rootDir);
        } catch (IOException e) {
            Utils.println("Error during file traversal: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        startAnalysis();
        analyseFiles.clear();
    }

    /**
     * 使用NIO递归遍历项目内所有目录
     */
    private void traverseWithNio(File rootDir) throws IOException {
        Path startPath = rootDir.toPath();
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                addAnalyse(file.toFile());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 将符合规则的File添加进分析列表
     */
    private void addAnalyse(File file) {
        String path = file.getAbsolutePath();
        String parentName = file.getParent();
        String name = file.getName();
        if (parentName == null || parentName.isEmpty()) {
            return;
        }
        if (path.contains(BUILD_GENERATED)) {
            return;
        }
        String lowerName = name.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".xml")) {
            return;
        }
        String parentLower = parentName.toLowerCase(Locale.ROOT);
        if ((parentLower.contains("drawable") || parentLower.contains("mipmap"))) {
            int dotIdx = lowerName.lastIndexOf('.');
            if (dotIdx > 0) {
                String ext = lowerName.substring(dotIdx + 1);
                if (IMAGE_EXTENSIONS.contains(ext)) {
                    analyseFiles.add(file);
                }
            }
        }
    }

    /**
     * 开始分析
     */
    private void startAnalysis() {
        for (File file : analyseFiles) {
            try {
                String lowerName = file.getName().toLowerCase(Locale.ROOT);
                if (lowerName.endsWith(".webp")) {
                    readWebp(file);
                    continue;
                }
                BufferedImage image = ImageIO.read(file);
                if (image == null) {
                    Utils.println("image is null - " + file.getAbsolutePath());
                    continue;
                }
                calMemory(file, image.getWidth(), image.getHeight());
            } catch (Throwable e) {
                Utils.println("Error analyzing file: " + file.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    /**
     * webP单独处理
     */
    private void readWebp(File file) throws Exception {
        byte[] bytes = new byte[30];
        try (FileInputStream fis = new FileInputStream(file)) {
            int read = fis.read(bytes, 0, bytes.length);
            if (read < 30) {
                Utils.println("webp header too short: " + file.getAbsolutePath());
                return;
            }
        }
        int width = ((int) bytes[27] & 0xff) << 8 | ((int) bytes[26] & 0xff);
        int height = ((int) bytes[29] & 0xff) << 8 | ((int) bytes[28] & 0xff);
        if (width == 0 || height == 0) {
            Utils.println("webp width/height is zero: " + file.getAbsolutePath());
            return;
        }
        calMemory(file, width, height);
    }

    /**
     * 计算占用内存
     */
    private void calMemory(File file, int width, int height) {
        long memory = (long) width * height * 4;
        double maxByte = maxValueMB * 1024 * 1024;
        if (memory >= maxByte) {
            String m = String.format(Locale.getDefault(), "%.02fM", memory / 1024f / 1024f);
            Utils.println("--------------------------------------------------------------------------------------------");
            Utils.println("这个Bitmap占用内存过大  -- " + m);
            Utils.println("路径 == " + file.getAbsolutePath());
        }
    }
}
