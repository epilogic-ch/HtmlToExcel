package org.example;

import java.util.Comparator;
import java.util.List;

/**
 * That class describes a portion of styled text. The object is instancied by
 * {@link HtmlNodeVisitor} during html parsing.
 */
public class HtmlTextRange {
    private int begIndex;
    private int endIndex;
    private HtmlStyles fontStyle;
    private String styleDetail;

    public HtmlTextRange(int begIndex, int endIndex, HtmlStyles fontStyle) {
        this.begIndex = begIndex;
        this.endIndex = endIndex;
        this.fontStyle = fontStyle;
    }

    public HtmlTextRange(int begIndex, int endIndex, HtmlStyles fontStyle, String styleDetail) {
        this.begIndex = begIndex;
        this.endIndex = endIndex;
        this.fontStyle = fontStyle;
        this.styleDetail = styleDetail;
    }

    public int getBegIndex() {
        return begIndex;
    }

    public void setBegIndex(int begIndex) {
        this.begIndex = begIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public HtmlStyles getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(HtmlStyles fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getStyleDetail() {
        return styleDetail;
    }

    public void setStyleDetail(String styleDetail) {
        this.styleDetail = styleDetail;
    }

    public boolean isValid() {
        return (begIndex != -1 && endIndex != -1 && endIndex >= begIndex &&
                (this.fontStyle != HtmlStyles.UNKNOWN || HtmlNodeVisitor.isDefined(this.styleDetail, false)));
    }

    @Override
    public String toString() {
        return String.format("HtmlTextRange : [startIndex=%d, endIndex=%d, fontStyle=%s, fontValue=%s]",
                this.begIndex, this.endIndex, this.fontStyle.toString(), this.styleDetail);
    }
}
