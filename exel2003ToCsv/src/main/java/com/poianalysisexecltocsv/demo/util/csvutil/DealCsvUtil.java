package com.poianalysisexecltocsv.demo.util.csvutil;

import com.poianalysisexecltocsv.demo.util.constantsutil.ConfigUtil;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lizehua 2018-07-24 13:49 pm
 * @desc (1):该类时处理所有的csv文件的工具类
 * (2):处理csv文件删除前几行，或者解析csv文件
 * (3):按行读取csv文件的内容，并处理某些指定的数据
 */
public class DealCsvUtil {

    private static Logger logger = LoggerFactory.getLogger(DealCsvUtil.class);

    private static String qarFilePath = ConfigUtil.getConfigToKey("csv.file.path");

    private static String backPath = ConfigUtil.getConfigToKey("qar.backup.path");

    private static String xmlPath = ConfigUtil.getConfigToKey("qar.analysis.protocol.path");

    /**
     * 删除指定的行号的数据
     *
     * @param index    要删除行所在的行号
     * @param fileName 要删除的文件名
     */
    public static String delteline(int index, String fileName) {
        int couunt = 1;
        FileWriter fileWriter = null;
        BufferedReader bufferedReader = null;
        StringBuffer temp = new StringBuffer();
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                if (couunt != index) {
                    temp.append(bufferedReader.readLine() + "\n");
                } else {
                    bufferedReader.readLine();
                }
                couunt++;
            }
            bufferedReader.close();
            String writerFileName = ConfigUtil.getConfigToKey("qar.computer.analysisCsv.path") + "/" + getCsvFromAnalysisHistory(fileName);
            //写入文件的名称
            fileWriter = new FileWriter(writerFileName);
            fileWriter.write(temp.toString());
            fileWriter.close();
            return writerFileName;
        } catch (FileNotFoundException fi) {
            fi.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
        return null;
    }

    /**
     * 获得飞机ID的方法，qarFilePath的形式为B-2898_3858_20170118.csv，没有路径
     *
     * @param qarFilePath csv文件名称
     * @return
     */
    public static String getPlaneid(String qarFilePath) {
        String[] planeSplit = qarFilePath.split("/");
        String csvName = null;
        String planeId = null;
        for (int i = 0; i < planeSplit.length; i++) {
            csvName = planeSplit[planeSplit.length - 1];
        }
        String[] planes = csvName.split("_");
        //获得飞机ID
        planeId = planes[0];
        return planeId;
    }


    /**
     * 获得飞机的ID
     *
     * @param qarFileName
     * @return
     */
    public static String getPlaneID(String qarFileName) {
        //先按照"_"
        String[] tables = qarFileName.split("_");
        String planeId = null;
        for (int i = 0; i < tables.length; i++) {
            if (i == 0) {
                //再按照"\"分割
                String[] planeType = tables[0].split("/");
                //得到飞机ID
                String[] planeSplit = planeType[2].split("-");
                for (int j = 0; j < planeSplit.length; j++) {
                    planeId = planeSplit[0] + "-" + planeSplit[1];
                }
                // table = planeType[2].replace("-","_");
                logger.info("飞机ID是>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>:" + planeId);
            }
        }
        return planeId;
    }


    public static String batchAnalysisPlaneID(String csvFileName) {
        String[] tables = csvFileName.split("_");

        return tables[0];
    }


    /**
     * 获得csv文件的飞机ID
     *
     * @param qarFileName
     * @return
     */
    public static String getCsvFilePlaneID(String qarFileName) {
        String[] tables = qarFileName.split("_");
        String table = null;
        for (int i = 0; i < tables.length; i++) {
            if (i == 0) {
                //再按照"\"分割
                String[] planeType = tables[0].split("/");
                //得到飞机ID
                String[] planeSplit = planeType[0].split("-");
                if (planeSplit.length > 1) {
                    for (int j = 0; j < planeSplit.length; j++) {
                        table = planeSplit[0] + "-" + planeSplit[1];
                        logger.info("飞机ID是>>>>>>>>>>>>>>：" + table);
                    }
                } else {
                    table = planeSplit[0];
                    logger.info("飞机ID是>>>>>>>>>>>>>>：" + table);
                }

            }
        }
        return table;
    }

    /**
     * 获得该文件夹下所有的文件名
     *
     * @param path 路径
     * @return 返回字符串数组
     */
    public static String[] getFileName(String path) {
        File file = new File(path);
        String[] fileName = file.list();
        return fileName;
    }

    /**
     * 获得上传飞机ID
     * windows
     *
     * @param qarFileName
     * @return
     */
    public static String getUploadPlaneID(String qarFileName) {
        String csvName = getCsvFileName(qarFileName);
        return getCsvFilePlaneID(csvName);
    }

    /**
     * linux
     *
     * @param qarFileName
     * @return
     */
    public static String getUploadPlaneId(String qarFileName) {
        String csvFileName = getCsvFromAnalysisHistory(qarFileName);
        return getCsvFilePlaneID(csvFileName);
    }

    /**
     * 获得csv文件的名称
     *
     * @param path 从解析历史表中查询得到的存放qar的路径
     * @return 返回csv文件名
     */
    public static String getCsvFromAnalysisHistory(String path) {
        String csvName = null;
        String[] csvNames = path.split("/");
        //遍历数组获取csv文件名
        // /home/java/data/qar/normal/xxx.csv格式，所以以 “/”分割，然后csv文件刚好就是数组的最后一个元素
        for (int i = 0; i < csvNames.length; i++) {
            csvName = csvNames[csvNames.length - 1];
        }
        return csvName;
    }

    /**
     * 处理如7004161026A(1)类型的csv原始文件上传到hadoop作备份出现()不能被识别的问题
     *
     * @param csvFilePath 原始csv文件的路径
     * @return
     */
    public static String dealContinuousCsvFileName(String csvFilePath) {
        //定义处理后的原始csv文件名
        String originalDealCsvName = null;
        //未被处理的csv文件名
        String csvOriginalName = null;
        //把csv文件的路径按照/分隔获得csv文件名
        String[] originalCsvName = csvFilePath.split("/");
        //获得路径下的csv文件名
        for (int i = 0; i < originalCsvName.length; i++) {
            csvOriginalName = originalCsvName[originalCsvName.length - 1];
        }
        //取出()里面的值
        String str = csvOriginalName.substring(csvOriginalName.lastIndexOf("("), csvOriginalName.lastIndexOf(")"));
        String s = str.substring(1, str.length());
        //去点()的正则表达式
        String pattern = "([-+*/^()\\]\\[])";
        //使用正则表示式获得处理后的csv文件名，以便能够上传到hadoop上指定的文件夹下
        originalDealCsvName = csvOriginalName.replaceAll(pattern, "");
        return originalDealCsvName;

    }

    /**
     * 获得上传的csv文件名
     *
     * @param csvPath 上传路径
     * @return csv文件名
     * @author lizehua 2017/08/22 14:52
     */
    public static String getCsvFileName(String csvPath) {
        String csvName = null;
        //String[] csvNames = csvPath.split("\\\\");windows
        String[] csvNames = csvPath.split("/");
        //遍历数组获取csv文件名
        // /home/java/data/qar/normal/xxx.csv格式，所以以 “/”分割，然后csv文件刚好就是数组的最后一个元素
        for (int i = 0; i < csvNames.length; i++) {
            csvName = csvNames[csvNames.length - 1];
        }
        return csvName;
    }

    /**
     * 读取配置好的解析xml规则，获取rule节点，
     * 然后获取rule节点的paramName的值，并把值添加到集合中,并拼接成sql语句的形式
     *
     * @param xmlFilePath xml解析规则的路径
     * @return 存放解析规则paramName属性值得集合
     * @author lizehua 2017/08/22 16:41
     */
    public static String traversalXMLRuleGetColumns(String xmlFilePath) {
        List<String> columnLists = new ArrayList<String>();
        SAXReader addNodeAttribute = new SAXReader();
        String paramNamesValues = "";
        try {
            //获得文件路径
            File xmlPath = new File(xmlFilePath);
            //获得Documents对象
            Document document = addNodeAttribute.read(xmlPath);
            //获得根节点下的某子节点
            Element root = document.getRootElement();
            //获得根节点下的某个子节点
            Element analysisRules = root.element("AnalysisRules");
            //获得所有的rule节点
            List<Element> rules = analysisRules.elements("rule");
            //获得每个rule节点的paramName属性
            for (Element e : rules) {
                //获得paramName属性的值
                String paramNameAttributeValue = e.attributeValue("paramName");
                //把获得paramName的值添加到集合中去
                columnLists.add(paramNameAttributeValue);
            }
            // columnLists = DealQarFile.traversalXMLRuleGetColumns(xmlFilePath);
            for (String s : columnLists) {
                logger.info("paramName的值分别是================》:" + s);
                s += " String,";
                logger.info("s>>>>>>>>>>>>>>>>>>>>>>>>>:  " + s);
                paramNamesValues += s;

            }
            logger.info("paramNamesValues>>>>>>>>>>>>>>>>>>>>:  " + paramNamesValues);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return paramNamesValues;
    }

    /**
     * 获取表的别名
     *
     * @param xmlRulePath xml文件的路径
     * @param tableAlias  表的别名
     * @return
     */
    public static String jiontTableAlias(String xmlRulePath, String tableAlias) {
        List<String> columnLists = new ArrayList<String>();
        SAXReader addNodeAttribute = new SAXReader();
        String paramNamesValues = "";
        try {
            //获得文件路径
            File xmlPath = new File(xmlRulePath);
            //获得Documents对象
            Document document = addNodeAttribute.read(xmlPath);
            //获得根节点下的某子节点
            Element root = document.getRootElement();
            //获得根节点下的某个子节点
            Element analysisRules = root.element("AnalysisRules");
            //获得所有的rule节点
            List<Element> rules = analysisRules.elements("rule");
            //获得每个rule节点的paramName属性
            for (Element e : rules) {
                //获得paramName属性的值
                String paramNameAttributeValue = e.attributeValue("paramName");
                logger.info("获得各个字段的名称是============》" + paramNameAttributeValue);
                //把获得paramName的值添加到集合中去
                columnLists.add(paramNameAttributeValue);
            }
            String s = "";
            for (int i = 0; i < columnLists.size(); i++) {
                s += tableAlias + "." + columnLists.get(i) + ",";
                paramNamesValues += s;
            }
            logger.info("paramNamesValues>>>>>>>>>>>>>>>>>>>>:  " + paramNamesValues);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return paramNamesValues;
    }

    /**
     * 获得所有的报警字段名称
     *
     * @param xmlPath
     * @return
     */
    public static List<String> getAlterColumns(String xmlPath) {
        List<String> alterColumns = new ArrayList<String>();
        return alterColumns;
    }

    /**
     * 获得新csv文件的飞机ID
     *
     * @param csvName 新的csv文件的名称
     * @return 飞机ID
     * 例：7014160409A.csv的飞机ID是：B7014
     */
    public static String getNewCsvPlaneId(String csvName) {
        String planeID = null;
        planeID = "B" + csvName.substring(0, 4);
        logger.info("新的csv文件下的飞机ID是==========>:" + planeID);
        return planeID;

    }


    /**
     * 得到小时时间，然后把第一次出现的时间的减去4秒
     *
     * @param time  小时时间
     * @param index 小时为空的前几行
     * @return 返回处理后的时间字符串
     * @throws ParseException
     */
    public static String timeAddSecond(String time, int index) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long time1 = sdf.parse(time).getTime();
        String timeNow = sdf.format(time1 - index * 1000);
        return timeNow;
    }

    /**
     * @param @param  time
     * @param @return
     * @param @throws ParseException    设定文件
     * @return String    返回类型
     * @throws
     * @Description: 格式化圆通航空中的time_r字段，time_r字段中每行代表一秒，目前情况是文件中
     * 数据错误，四秒数据是一样的，需要我们取第一行中的时间，然后逐行加一秒形成新的时间
     */
    public static String timeSecond(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long time1 = sdf.parse(time).getTime();
        String timeNow = sdf.format(time1 + 1000);
        return timeNow;
    }

    /**
     * 从xml文件的头部得到要删除的行数
     *
     * @return
     */
    public static int getDeleteLine(String xmlPath) {
        int lineNum = 0;
        try {
            //创建SAXReader对象
            SAXReader updateSAXReader = new SAXReader();
            Document updateDocument = updateSAXReader.read(new File(xmlPath));
            //获得根节点
            Element root = updateDocument.getRootElement();
            //获得fileAttribute节点
            Element fileAttribute = root.element("fileAttribute");
            Element DeleteLine = fileAttribute.element("DeleteLine");
            String line = DeleteLine.getText();
            lineNum = Integer.parseInt(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lineNum;

    }

    /**
     * 通过xml文件获得列的位置
     *
     * @param xmlFilePath xml文件路径
     * @return
     */
    public static List getPositionByParseXml(String xmlFilePath) {
        List<String> columnLists = new ArrayList<String>();
        SAXReader addNodeAttribute = new SAXReader();
        try {
            //获得文件路径
            File xmlPath = new File(xmlFilePath);
            //获得Documents对象
            Document document = addNodeAttribute.read(xmlPath);
            //获得根节点下的某子节点
            Element root = document.getRootElement();
            //获得根节点下的某个子节点
            Element analysisRules = root.element("AnalysisRules");
            //获得所有的rule节点
            List<Element> rules = analysisRules.elements("rule");
            //获得每个rule节点的paramName属性
            for (Element e : rules) {
                //获得position属性的值
                String positionAttributeValue = e.attributeValue("position");
                //把获得paramName的值添加到集合中去
                columnLists.add(positionAttributeValue);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return columnLists;
    }

    /**
     * 通过发动机位置来获取列名
     *
     * @param compnayName 航空公司名
     * @param planeType   飞机类型
     * @param position    发动机位置
     * @return
     */
    public static List<String> getParNameByPOsition(String compnayName, String planeType, String position) {
        List<String> parNames = new ArrayList<String>();
        String xmlFilePath = xmlPath + File.separator + compnayName + "-" + planeType + ".xml";
        SAXReader saxReader = new SAXReader(); // 用来读取xml文档
        if (GetFilePathNames.isExsistFile(compnayName + "-" + planeType + ".xml", xmlPath)) {
            Document document = null;
            try {
                //url是文件的地址'
                document = saxReader.read(new File(xmlFilePath));
                List list = document.selectNodes("/root/AnalysisRules");//查找指定的节点
                Iterator iterator = list.iterator();//迭代chanpin下所有的节点
                while (iterator.hasNext()) {
                    Element ele = (Element) iterator.next();
                    Iterator it = ele.elementIterator("rule");//指定到CP
                    while (it.hasNext()) {
                        Element es = (Element) it.next();//迭代的所有CP节点
                        //先确定CP属性不为空，然后再根据name属性来查找到需要的节点 if(es.attributeValue("name")!=null&&es.attributeValue("name").equals(name))
                        if (es.attributeValue("engines") != null && es.attributeValue("engines").equals(position)) {
                            String parName = es.attributeValue("paramName");
                            parNames.add(parName);
                        }
                    }
                }
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }
            return parNames;
        }
        return null;
    }

    public static String getAnalysisCompanyName(String xmlFilePath) {
        String companyName = null;
        String[] data = xmlFilePath.split("/");
        if (data.length > 1 && data != null) {
            String[] xmldata = data[data.length - 1].split("-");
            companyName = xmldata[0];
            return companyName;
        }
        return null;
    }

}
