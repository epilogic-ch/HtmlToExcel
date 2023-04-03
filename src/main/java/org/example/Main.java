package org.example;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String html = "some <b>bold text</b>, some <i>italic text</i> and <b><i>some bold italic text</i></b><br/>a second line";
        File destination = new File("C:/Temp/Sample.xlsx");

        try {
            // create an empty workbook
            Workbook workbook = WorkbookFactory.create(true);
            Sheet sheet = workbook.createSheet("Sheet1");

            Row r = sheet.createRow(0);
            Cell c = r.createCell(0);
            RichTextString contentRich = HtmlToExcel.fromHtmlToCellValue(html, sheet.getWorkbook());
            c.setCellValue(contentRich);

            try (FileOutputStream fileOut = new FileOutputStream(destination)) {
                workbook.write(fileOut);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}