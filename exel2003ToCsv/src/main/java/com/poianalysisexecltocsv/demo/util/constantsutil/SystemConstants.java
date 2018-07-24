package com.poianalysisexecltocsv.demo.util.constantsutil;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
 * @ClassName: SystemConstants
 * @Description: TODO(系统常量定义 ， 初始化时加载配置文件)
 * @date 2018年6月22日
 */
public class SystemConstants {


    /**
     * @Fields field:field:{todo}(试车数据上传路径)
     */
    public static final String FES_DATA_UPLOAD_PATH = ConfigUtil.getConfigToKey("fes.data.upload.path");

    /**
     * @Fields field:field:{todo}(试车数据处理后的csv路径)
     */
    public static final String FES_DATA_DEAL_PATH = ConfigUtil.getConfigToKey("fes.data.dealAfterCsv.path");

    /**
     * @Fields field:field:{todo}(正确解析之后数据备份路径)
     */
    public static final String FES_DATA_SUCCESS_BACKUP_PATH = ConfigUtil.getConfigToKey("fes.data.success.backup.path");

    /**
     * @Fields field:field:{todo}(解析失败之后数据备份路径)
     */
    public static final String FES_DATA_FAILED_BACKUP_PATH = ConfigUtil.getConfigToKey("fes.data.failed.backup.path");

    /**
     * @Fields field:field:{todo}(解析录入临时外部表目录)
     */
    public static final String FES_DATA_EXTEND_TABLE_PATH = ConfigUtil.getConfigToKey("fes.data.extend.table.path");

    /**
     * @Fields field:field:{todo}(eec格式时间格式化 yyyy年MM月dd日HH点mm分ss秒)
     */
    public static SimpleDateFormat eecsdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");

    /**
     * @Fields field:field:{todo}(tie格式文件解析开始时间，yyyyMMddHHmmss)
     */

    public static SimpleDateFormat tiesdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static SimpleDateFormat tiesdf1 = new SimpleDateFormat("yyyyMMdd");

    /**
     * @Fields field:field:{todo}(标准时间格式化：yyyy-MM-dd HH:mm:ss)
     */
    public static SimpleDateFormat stdsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * @Fields field:field:{todo}(格式化毫秒数 yyyy-MM-dd HH:mm:ss.SSS)
     */

    public static SimpleDateFormat stdMSsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * @Fields field:field:{todo}(EMU格式时间,yyyy/MM/dd HH:mm:ss)
     */
    public static SimpleDateFormat emuMSsdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * @Fields field:field:{todo}(EMU格式时间,yyyy-MM-dd HH:mm:ss)
     */
    public static SimpleDateFormat emuMSsdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * @Fields field:field:{todo}(解析文件MAP)
     */

//	public static Map<String, FileInputStatus> inputFileInfo = new HashMap<String, FileInputStatus>();

    static {
        // 传入初始化时需要创建的目录，如果有新加配置，在数组中添加新的参数。
        String[] initPathArr = {FES_DATA_UPLOAD_PATH, FES_DATA_SUCCESS_BACKUP_PATH, FES_DATA_FAILED_BACKUP_PATH, FES_DATA_EXTEND_TABLE_PATH};
        for (String initPath : initPathArr) {
            File file = new File(initPath);
            if (file.exists()) {
                continue;
            } else {
                file.mkdirs();
            }
        }
    }

    public static String getCreateExtendTableSql(String tableName, String[] columnArr, String fileName) {
        return null;
    }

    public static void main(String args[]) throws ParseException {
        String inputStr1 = "2018年04月25日11点33分44秒-第四次起动状态的假开车A";
        long time = eecsdf.parse(inputStr1.split("\\-")[0]).getTime() + 200;
        System.out.println(time);
        System.out.println(stdsdf.format(new Date(time)));
        System.out.println(stdMSsdf.format(new Date(time)));
        double res = 3980d / 950d;
        System.out.println(res);
        System.out.println(Math.ceil(res));

    }

}
