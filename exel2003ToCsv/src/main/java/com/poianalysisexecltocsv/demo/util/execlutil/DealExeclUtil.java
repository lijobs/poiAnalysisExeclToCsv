package com.poianalysisexecltocsv.demo.util.execlutil;

import com.poianalysisexecltocsv.demo.util.constantsutil.SystemConstants;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author lizehua 2018-07-24 13:52 pm
 * @deac (1)处理execl文件的工具类
 * (2)使用apache开源的java解析execl表格的框架poi读取execl表格
 * (3)读取指定列的数据，并写如新的csv文件中
 * (4)有效解决读取execl文件由于数据量过大导致的内存溢出问题
 */
public class DealExeclUtil implements HSSFListener {

    private int minColumns;
    private POIFSFileSystem fs;
    private PrintStream output;

    /*记录最后一行*/
    private int lastRowNumber;
    /*记录最后一列*/
    private int lastColumnNumber;

    /*时间偏移量，单位：毫秒*/
    public long time_cha;

    public long getTime_cha() {
        return time_cha;
    }

    public void setTime_cha(long time_cha) {
        this.time_cha = time_cha;
    }

    /**
     * Should we output the formula, or the value it has?
     */
    private boolean outputFormulaValues = true;

    /**
     * For parsing Formulas
     */
    private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;
    private HSSFWorkbook stubWorkbook;

    // Records we pick up as we process
    private SSTRecord sstRecord;
    private FormatTrackingHSSFListener formatListener;

    /**
     * So we known which sheet we're on
     */
    private int sheetIndex = -1;
    private BoundSheetRecord[] orderedBSRs;
    private ArrayList boundSheetRecords = new ArrayList();

    // For handling formulas with string results
    private int nextRow;
    private int nextColumn;
    private boolean outputNextStringRecord;
    private String d = "";

    private final String OUTPUT_CHARSET = "UTF-8";
    //    private final String OUTPUT_CHARSET = "GBK";
    private int count = 0;

    public static String deal_time = "";

    /**
     * Creates a new XLS -> CSV converter
     *
     * @param fs         The POIFSFileSystem to process
     * @param output     The PrintStream to output the CSV to
     * @param minColumns The minimum number of columns to output, or -1 for no minimum
     */
    public DealExeclUtil(POIFSFileSystem fs, PrintStream output, int minColumns) {
        this.fs = fs;
        this.output = output;
        this.minColumns = minColumns;
    }

    public DealExeclUtil(String inputFilePath, String outputFilePath) throws Exception {
        fs = new POIFSFileSystem(new FileInputStream(inputFilePath));
        output = new PrintStream(outputFilePath, OUTPUT_CHARSET);
        minColumns = -1;
    }

    /**
     * Creates a new XLS -> CSV converter
     *
     * @param filename   The file to process
     * @param minColumns The minimum number of columns to output, or -1 for no minimum
     * @throws IOException
     * @throws FileNotFoundException
     */
    public DealExeclUtil(String filename, int minColumns) throws IOException, FileNotFoundException {
        this(new POIFSFileSystem(new FileInputStream(filename)), System.out, minColumns);
    }


    /**
     * Initiates the processing of the XLS file to CSV
     */
    public void process() throws IOException {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatListener);
        } else {
            workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
            request.addListenerForAllRecords(workbookBuildingListener);
        }
        factory.processWorkbookEvents(request, fs);
    }


    /**
     * Main HSSFListener method, processes events, and outputs the CSV as the
     * file is processed.
     */
    public void processRecord(Record record) {
        int thisRow = -1;
        int thisColumn = -1;
        String thisStr = null;
        String sign = "";
        switch (record.getSid()) {
            case BoundSheetRecord.sid:
                //boundSheetRecords.add(record);
                break;
            case BOFRecord.sid:
                BOFRecord br = (BOFRecord) record;
                if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
                    // Create sub workbook if required
                    if (workbookBuildingListener != null && stubWorkbook == null) {
                        stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
                    }

                    // Output the worksheet name
                    // Works by ordering the BSRs by the location of
                    // their BOFRecords, and then knowing that we
                    // process BOFRecords in byte offset order
                    sheetIndex++;
                    if (orderedBSRs == null) {
                        orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                    }
                }
                break;

            case SSTRecord.sid:
                sstRecord = (SSTRecord) record;
                break;

            case BlankRecord.sid:
                BlankRecord brec = (BlankRecord) record;
                thisRow = brec.getRow();
                thisColumn = brec.getColumn();
//                thisStr = "";
                break;
            case BoolErrRecord.sid:
                BoolErrRecord berec = (BoolErrRecord) record;
                thisRow = berec.getRow();
                thisColumn = berec.getColumn();
//                thisStr = "";
                break;

            case FormulaRecord.sid:
                FormulaRecord frec = (FormulaRecord) record;
                thisRow = frec.getRow();
                thisColumn = frec.getColumn();
                if (outputFormulaValues) {
                    if (Double.isNaN(frec.getValue())) {
                        // Formula result is a string
                        // This is stored in the next record
                        outputNextStringRecord = true;
                        nextRow = frec.getRow();
                        nextColumn = frec.getColumn();
                    } else {
                        thisStr = formatListener.formatNumberDateCell(frec);
                    }
                } else {
//                    thisStr = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression()) + '"';
                    thisStr = HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression());
                }
                break;
            case StringRecord.sid:
                if (outputNextStringRecord) {
                    // String for formula
                    StringRecord srec = (StringRecord) record;
                    thisStr = srec.getString();
                    thisRow = nextRow;
                    thisColumn = nextColumn;
                    outputNextStringRecord = false;
                }
                break;

            case LabelRecord.sid:
                LabelRecord lrec = (LabelRecord) record;

                thisRow = lrec.getRow();
                thisColumn = lrec.getColumn();
//                thisStr = '"' + lrec.getValue() + '"';
                thisStr = lrec.getValue();
                break;
            case LabelSSTRecord.sid:
                LabelSSTRecord lsrec = (LabelSSTRecord) record;
                thisRow = lsrec.getRow();
                thisColumn = lsrec.getColumn();
                if (sstRecord == null) {
                    thisStr = '"' + "(No SST Record, can't identify string)" + '"';
                } else if (sstRecord.getString(lsrec.getSSTIndex()).toString().contains("30/03/2018 - 16h34:18.440")) {
//                    thisStr = '"' + sstRecord.getString(lsrec.getSSTIndex()).toString() + '"';
                    thisStr = sstRecord.getString(lsrec.getSSTIndex()).toString();
                    //用-分割得到时间数据的年月日和小时，分钟秒的数据
                    String[] splitData = thisStr.split("-");
                    //格式化年月日的数据
                    String[] date = splitData[0].split("/");
                    //格式化小时分钟秒的数据
                    String[] time = splitData[1].split(":");
                    String[] hourAndMount = time[0].split("h");
                    String hours = hourAndMount[0] + ":" + hourAndMount[1] + ":" + time[1].split("\\.")[0] + "." + time[1].split("\\.")[1];
                    d = date[date.length - 1].trim() + "-" + date[1] + "-" + date[0];
                    deal_time = d + " " + hours;
                    thisStr = "";
                } else {
                    //处理时间格式
                    thisStr = sstRecord.getString(lsrec.getSSTIndex()).toString();
//                    System.out.println("------->" + thisStr);
                    if (thisStr.contains("h")) {
                        String deal_get_time_split[] = thisStr.split(":");
                        String deal_hour_minute[] = deal_get_time_split[0].split("h");
                        String deal_hour_minute_ = deal_hour_minute[0] + ":" + deal_hour_minute[1];
                        String deal_seconds_millis[] = deal_get_time_split[1].split(",");
                        String deal_seonds_millis_ = deal_seconds_millis[0] + "." + deal_seconds_millis[1];
                        String deal_date = d + " " + deal_hour_minute_ + ":" + deal_seonds_millis_;
                        try {
                            //加上时间便宜量
                            long t = SystemConstants.stdMSsdf.parse(deal_date).getTime() + this.getTime_cha();
//                            System.out.println("时间差粗例=========> " + t);
                            //把毫秒转成日期格式
                            String s = SystemConstants.stdMSsdf.format(new Date(t));
                            System.out.println("s===" + s);
                            thisStr = s;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else if (thisStr.contains("Sign")) {
                        //处理信号的信号名。包括信号产生时间和对应的值
                        sign += thisStr + "_time," + thisStr + "_value";
                        thisStr = sign;
                    } else {
                        thisStr = "";
                    }

                }
                break;
            case NoteRecord.sid:
                NoteRecord nrec = (NoteRecord) record;

                thisRow = nrec.getRow();
                thisColumn = nrec.getColumn();
                thisStr = '"' + "(TODO)" + '"';
                break;
            case NumberRecord.sid:
                NumberRecord numrec = (NumberRecord) record;

                thisRow = numrec.getRow();
                thisColumn = numrec.getColumn();

                // Format
                thisStr = formatListener.formatNumberDateCell(numrec);
                break;
            case RKRecord.sid:
                RKRecord rkrec = (RKRecord) record;
                thisRow = rkrec.getRow();
                thisColumn = rkrec.getColumn();
                thisStr = '"' + "(TODO)" + '"';
                break;
            default:
                break;
        }

        // Handle new row
        if (thisRow != -1 && thisRow != lastRowNumber) {
            lastColumnNumber = -1;
        }

        // Handle missing column
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            thisStr = "";
        }

        // If we got something to print out, do so
        if (thisStr != null) {
            if (thisStr == "") {
            } else {
//                System.out.println("thisColumn :" + thisColumn + " thisStr: " + thisStr);
                if (thisColumn > 0) {
                    //控制那一列不被写入到csv文件中，具体以thisStr来决定
                    if ((thisColumn - 1) % 4 == 0) {
//                        System.out.println("us=====>" + thisStr );
                        thisStr = "";

                    } else {
                        output.print(',');
                    }
                }
                output.print(thisStr);
            }

        }

        // Update column and row count
        if (thisRow > -1)
            lastRowNumber = thisRow;
        if (thisColumn > -1)
            lastColumnNumber = thisColumn;

        // Handle end of row
        if (record instanceof LastCellOfRowDummyRecord) {
            if (((LastCellOfRowDummyRecord) record).getRow() > 7) {
                // Print out any missing commas if needed
                if (minColumns > 0) {
                    // Columns are 0 based
                    if (lastColumnNumber == -1) {
                        lastColumnNumber = 0;
                    }
                    for (int i = lastColumnNumber; i < (minColumns); i++) {
                        output.print(',');
                    }
                }
                // We're onto a new row
                lastColumnNumber = -1;
                // End the row
                output.println();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        long star = System.currentTimeMillis();
        System.out.println("开始读取的时间是======》::   " + star);
        String inputPath2 = "/home/java/data/fault_database/20180330-第一次试验-试验阶段.xls";
        String outputPath2 = "/home/java/data/fault_database/data.csv";
        DealExeclUtil execlUtil = new DealExeclUtil(inputPath2, outputPath2);
        execlUtil.setTime_cha(2000);
        execlUtil.process();
        long end = System.currentTimeMillis();
        System.out.println("总过用时==========>: " + (end - star));
    }

}
