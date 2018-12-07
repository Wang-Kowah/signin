package club.szuai.signin.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtils {

    /**
     * 读取文件内容
     */
    public static String read(String filepath) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStreamReader reader;
            reader = new InputStreamReader(new FileInputStream(filepath));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            reader.close();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 按编码读取文件内容
     */
    public static String read(String filepath, String charset) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStreamReader reader;
            reader = new InputStreamReader(new FileInputStream(filepath), charset);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            reader.close();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 将内容写入文件
     */
    public static void write(String filepath, String content) {
        try {
            File file = new File(filepath);
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件大小
     */
    public static long getLength(String filepath) {
        long length = 0;
        try {
            File file = new File(filepath);
            length = file.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }

    /**
     * 获取文件夹下所有特定类型的文件
     *
     * @param suffix 文件后缀名
     * @return 文件路径数组
     */
    public static String[] listBySuffix(String dirPath, String suffix) {
        File targetDir = new File(dirPath);
        ArrayList<String> csvFiles = new ArrayList<>();
        if (!targetDir.isDirectory()) {
            System.err.println("目标路径不是文件夹");
        } else {
            File[] files = targetDir.listFiles();
            if (files == null || files.length == 0) {
                System.err.println("目标文件夹为空");
            } else {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(suffix)) {
                        csvFiles.add(file.getPath());
                    }
                    if (file.isDirectory()) {
                        String[] subDirFiles = listBySuffix(file.getPath(), suffix);
                        csvFiles.addAll(Arrays.asList(subDirFiles));
                    }
                }
                return csvFiles.toArray(new String[0]);
            }
        }
        return new String[0];
    }

}
