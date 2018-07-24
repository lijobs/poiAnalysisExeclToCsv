package com.poianalysisexecltocsv.demo.util.csvutil;

import com.poianalysisexecltocsv.demo.util.constantsutil.ConfigUtil;

import java.io.File;

/**
 * @author lizehua 2018-07-24 17:03 pm
 * @desc 判断文件是否存在某个指定的文件夹下
 */
public class GetFilePathNames {

    private static String protocolPath = ConfigUtil.getConfigToKey("bin.analysis.protocol.path");

    /**
     * 获得路径下所有文件名，存放到数组中，遍历该数组获得相应文件名
     *
     * @param path 文件路径
     * @return 返回字符串数组
     */
    public static String[] getFileName(String path) {
        //通过文件路径读取该路径下的所有文件
        File file = new File(path);
        //获得所有文件的文件名，并且存放到字符串数组中
        String[] fileName = file.list();
        return fileName;
    }

    public static boolean isExsistFile(String fileName, String filePath) {

        //定义一个标识变量，如果为true则表示本地有xml文件，反之没有
        boolean flag = false;
        //获得该路径下的所有文件的文件名
        String[] xmlFilenames = getFileName(filePath);
        //把文件名截取获得符合条件的文件名
        //String[] fname =fileName.split("\\\\");
        String[] fname = fileName.split("/");
        if (xmlFilenames == null || xmlFilenames.length <= 0) {
            return true;
        }
        for (int i = 0; i < xmlFilenames.length; i++) {
            for (int j = 0; j < fname.length; j++) {
                //表示在该文件夹下存在该文件
                if (xmlFilenames[i].equals(fname[j])) {
                    flag = true;
                }
            }
        }
        return flag;
    }
}
