package com.pool.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.stereotype.Component;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

@Component
public class ExcelUtil {

    public String createExcelSheet(int partitionsSize){
        String fileName="";
        System.out.println(partitionsSize);
        try (Workbook wb = new HSSFWorkbook()){
            for (int i = 0; i < partitionsSize; i++) {
                wb.createSheet(String.valueOf(i));
            }
            fileName="workbook"+new Random().nextInt()+".xls";
            try (OutputStream fileOut = new FileOutputStream(fileName)) {
                wb.write(fileOut);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }
}
