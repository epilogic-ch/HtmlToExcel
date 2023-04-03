package org.example;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Html to Excel Rich Text String converter
 */
public class HtmlToExcel {
    public static RichTextString fromHtmlToCellValue(String html, Workbook workbook){
    HtmlNodeVisitor visitor = new HtmlNodeVisitor();
    Document source = Jsoup.parseBodyFragment(html);
    source.traverse(visitor);

    return visitor.buildRichText(workbook);
    }
}
