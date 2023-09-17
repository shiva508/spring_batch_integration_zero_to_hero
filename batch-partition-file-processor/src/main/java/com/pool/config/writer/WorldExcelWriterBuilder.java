package com.pool.config.writer;

import com.pool.domine.CityEntity;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

@Component
public class WorldExcelWriterBuilder implements ItemStreamWriter<CityEntity> , StepExecutionListener {
    private HSSFWorkbook wb;
    private WritableResource resource;

    POIFSFileSystem fs = null;
    private int row;
    int sheetNum=0;
    @Override
    public void write(Chunk<? extends CityEntity> citiesChunk) throws Exception {
        HSSFSheet s = wb.getSheet(String.valueOf(this.sheetNum));

        for (CityEntity cityEntity : citiesChunk) {
            Row sheetRow = s.createRow(row++);
            Cell c = sheetRow.createCell(0);
            c.setCellValue(cityEntity.getName());

            c = sheetRow.createCell(1);
            c.setCellValue(cityEntity.getCountryCode());

            c = sheetRow.createCell(2);
            c.setCellValue(cityEntity.getDistrict());

            c = sheetRow.createCell(3);
            c.setCellValue(cityEntity.getPopulation());
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        /*wb = new HSSFWorkbook();
        HSSFPalette palette = wb.getCustomPalette();
        HSSFSheet s = wb.createSheet();
        wb.createSheet("WORLD_"+this.sheetNum);
        resource = new FileSystemResource("output.xlsx");
        row = 0;
        createTitleRow(s, palette);
        createHeaderRow(s);*/

        try {
            fs = new POIFSFileSystem(new File("workbook-812030534.xls"),false);
            //resource = new FileSystemResource("workbook-211267534.xls");
            wb = new HSSFWorkbook(fs.getRoot(), true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        ItemStreamWriter.super.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        if (wb == null) {
            return;
        }
        //createFooterRow();
        /*try (BufferedOutputStream bos = new BufferedOutputStream(resource.getOutputStream())) {
            wb.write(bos);
            bos.flush();
            wb.close();
        } catch (IOException ex) {
            throw new ItemStreamException("Error writing to output file", ex);
        }*/
        try {
            fs.writeFilesystem();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        row = 0;
    }

    private void createFooterRow() {
        HSSFSheet s = wb.getSheetAt(0);
        HSSFRow r = s.createRow(row);
        Cell c = r.createCell(3);
        c.setCellType(CellType.FORMULA);
        c.setCellFormula(String.format("SUM(D3:D%d)", row));
        row++;

    }
    private void createTitleRow(HSSFSheet s, HSSFPalette palette) {
        HSSFColor redish = palette.findSimilarColor((byte) 0xE6, (byte) 0x50, (byte) 0x32);
        palette.setColorAtIndex(redish.getIndex(), (byte) 0xE6, (byte) 0x50, (byte) 0x32);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setWrapText(true);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(redish.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        HSSFRow r = s.createRow(row);

        Cell c = r.createCell(0);
        c.setCellValue("Internal Use Only");
        r.createCell(1).setCellStyle(headerStyle);
        r.createCell(2).setCellStyle(headerStyle);
        s.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        c.setCellStyle(headerStyle);

        CellUtil.setAlignment(c, HorizontalAlignment.CENTER);

        row++;
    }

    private void createHeaderRow(HSSFSheet s) {
        CellStyle cs = wb.createCellStyle();
        cs.setWrapText(true);
        cs.setAlignment(HorizontalAlignment.LEFT);

        HSSFRow r = s.createRow(row);
        r.setRowStyle(cs);

        Cell c = r.createCell(0);
        c.setCellValue("Author");
        s.setColumnWidth(0, poiWidth(18.0));
        c = r.createCell(1);
        c.setCellValue("Book Name");
        s.setColumnWidth(1, poiWidth(24.0));
        c = r.createCell(2);
        c.setCellValue("ISBN");
        s.setColumnWidth(2, poiWidth(18.0));
        c = r.createCell(3);
        c.setCellValue("Price");
        s.setColumnWidth(3, poiWidth(18.0));

        row++;
    }
    private int poiWidth(double width) {
        return (int) Math.round(width * 256 + 200);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String maxrow = (String) stepExecution.getExecutionContext().get("maxrow");
        sheetNum=Integer.valueOf(maxrow);
        System.out.println("========================================>=:"+sheetNum);

        boolean fileName = stepExecution.getExecutionContext().containsKey("fileName");;

        System.out.println("========================================>=:"+fileName);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
