# HtmlToExcel
Html to Excel conversion based on Apache POI

This project is an alternative to https://github.com/michaelcgood/HTML-to-Apache-POI-RichTextString / https://dzone.com/articles/converting-html-to-richtextstring-for-apache-poi

The implementation mainly brings two improvements:
- Parsing is performed with JSoup HTML parser (instead of Jericho) and is done using a visitor class that is called back by JSoup traversal parsing. This allows much simple code (imho)
- A more efficient algorithm is implemented in order to apply font to final richtext. Original project used following code to apply font to destination rich text:
    ````
    for (int i = 0; i < textBuffer.length(); i++) {
        Font currentFont = mergedMap.get(i);
        if (currentFont != null) {
            richText.applyFont(i, i + 1, currentFont);
        }
    }
    ````

This applies one font per character, which is not recommended imho for two reasons:
1. it brings complexity in final Excel file
2. it breaks the ligature opportunity of font

An optimisation algorithm based on https://softwareengineering.stackexchange.com/a/363096 has been implemented in order to group range of characters that use same font properties.
