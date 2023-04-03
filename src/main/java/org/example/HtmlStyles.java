package org.example;

/**
 * Style elements supported by current html to rich text converter
 */
public enum HtmlStyles {
    BOLD("b"),
    EM("em"),
    STRONG("strong"),
    COLOR("color"),
    UNDERLINE("u"),
    SPAN("span"),
    ITALIC("i"),
    UNKNOWN("unknown"),
    PRE("pre"),
    BR("br");

    private String type;

    HtmlStyles(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static HtmlStyles fromValue(String type) {
        for (HtmlStyles style : values()) {
            if (style.type.equalsIgnoreCase(type)) {
                return style;
            }
        }
        return UNKNOWN;
    }
}
