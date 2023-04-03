package org.example;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.util.*;

/**
 * That class implements visitor used for html traversal parsing done by JSoup.
 *
 * Voir https://jsoup.org/apidocs/org/jsoup/select/NodeVisitor.html
 */
public class HtmlNodeVisitor implements NodeVisitor {
    StringBuilder fullText = new StringBuilder();
    Map<Element, HtmlTextRange> styleElements = new LinkedHashMap<>();

    /**
     * Builds final {@link RichTextString} object
     * @param workBook Destination workbook
     * @return The RichTextString object build upon parsed html
     */
    public RichTextString buildRichText(Workbook workBook) {
        RichTextString richText = new XSSFRichTextString(this.fullText.toString());

        // get list of valid styles
        List<HtmlTextRange> elements = new LinkedList<>();
        for (HtmlTextRange htmlTextRange : this.styleElements.values()) {
            if (htmlTextRange.isValid()) {
                elements.add(htmlTextRange);
            }
        }

        // font fusion algorithm start
        // source: https://softwareengineering.stackexchange.com/a/363096
        List<MutableTriple<Integer, Integer, Boolean>> array = new LinkedList<>();
        int currentIndex = 0;
        for (HtmlTextRange htmlTextRange : this.styleElements.values()) {
            if (htmlTextRange.isValid()) {
                array.add(new MutableTriple<>(htmlTextRange.getBegIndex(), currentIndex, false));
                array.add(new MutableTriple<>(htmlTextRange.getEndIndex(), currentIndex, true));
                ++currentIndex;
            }
        }

        Collections.sort(array, new Comparator<MutableTriple<Integer, Integer, Boolean>>() {
            public int compare(MutableTriple<Integer, Integer, Boolean> o1, MutableTriple<Integer, Integer, Boolean> o2) {
                // compare two instance of `Score` and return `int` as result.
                int x = o1.getLeft() - o2.getLeft();
                if (x == 0) {
                    if (o2.getRight()) return 1;
                    else return -1;
                }
                return x;
            }
        });

        Set<Integer> s = new HashSet<>();
        for (int i = 0; i < array.size(); ++i) {
            MutableTriple<Integer, Integer, Boolean> o = array.get(i);

            if (o.getRight()) {
                s.remove(o.getMiddle());
            }
            else {
                s.add(o.getMiddle());
            }

            if (i < array.size() - 1) {
                int np, mp;
                if (o.getRight()) {
                    np = array.get(i + 1).getLeft();
                }
                else {
                    np = o.getLeft();
                }

                MutableTriple<Integer, Integer, Boolean> oo = array.get(i + 1);
                if (oo.getRight()) {
                    mp = oo.getLeft();
                }
                else {
                    mp = o.getLeft();
                }

                if (np < mp) {
                    // Below StringBuilder is only used for contol
                    // StringBuilder sb = new StringBuilder(String.format("%d ... %d : ", np, mp));
                    Font font = null;
                    for (Integer j : s) {
                        HtmlTextRange htmlTextRange = elements.get(j);
                        font = HtmlNodeVisitor.mergeFont(font, htmlTextRange.getFontStyle(), htmlTextRange.getStyleDetail(), workBook);
                        // sb.append(String.format("%d ", j));
                    }
                    // System.out.println(sb.toString());
                    richText.applyFont(np, mp, font);
                }
            }
        }

        return richText;
    }

    private static Font mergeFont(Font font, HtmlStyles fontStyle, String styleDetail, Workbook workBook) {
        if (font == null) {
            font = workBook.createFont();
        }

        switch (fontStyle) {
            case BOLD:
            case EM:
            case STRONG:
                font.setBold(true);
                break;
            case UNDERLINE:
                font.setUnderline(Font.U_SINGLE);
                break;
            case ITALIC:
                font.setItalic(true);
                break;
            case PRE:
                font.setFontName("Courier New");
                break;
            case COLOR:
                if (HtmlNodeVisitor.isDefined(styleDetail, false)) {
                    font.setColor(IndexedColors.BLACK.getIndex());
                }
                break;
            default:
                break;
        }

        return font;
    }

    public static boolean isDefined(String val, boolean allowEmpty) {
        if (val == null) return false;
        return (allowEmpty || val.length() > 0);
    }

    @Override
    public void head(Node node, int depth) {
        if (node instanceof TextNode) {
            this.fullText.append(((TextNode) node).text());
        }
        else if (node instanceof Element) {
            // we store the element beg position (which corresponds to current fulltext
            // length) and it's type (are we reading a <b>, a <i>, etc.)

            int startIndex = this.fullText.length();
            HtmlTextRange info = null;
            switch (HtmlStyles.fromValue(((Element) node).tagName())) {
                case BR:
                    this.fullText.append(System.getProperty("line.separator"));
                    break;
                case SPAN:
                    String styleAttr = ((Element) node).attr("style");
                    if (HtmlNodeVisitor.isDefined(styleAttr, false)) {
                        for (String style : styleAttr.split(";")) {
                            String[] styleDetails = style.split(":");
                            if (styleDetails != null && styleDetails.length > 1) {
                                if ("COLOR".equalsIgnoreCase(styleDetails[0].trim())) {
                                    info = new HtmlTextRange(startIndex, -1, HtmlStyles.COLOR, styleDetails[1]);
                                }
                            }
                        }
                    }
                    break;
                default:
                    info = new HtmlTextRange(startIndex, -1, HtmlStyles.fromValue(((Element) node).tagName()));
                    break;
            }

            if (info != null) {
                this.styleElements.put((Element) node, info);
            }
        }
    }

    @Override
    public void tail(Node node, int depth) {
        if (node instanceof Element) {
            // we store the element end position (which corresponds to current fulltext
            // length) in object the object that describes the element style
            HtmlTextRange info = this.styleElements.get(node);
            if (info != null) {
                info.setEndIndex(this.fullText.length());
            }
        }
    }
}
