package com.asiainfo.util.poi;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;

/**
 * 文字样式
 * 
 * @author luohuawuyin
 *
 */
public class WordStyle {
    private boolean bold = false;// 是否加粗
    private boolean talic = false;// 斜体
    private boolean strikethrough = false;// 删除线
    private boolean imprinted = true;
    private boolean shadow = true;
    private String color = "000000";// 字体颜色
    private String fontFamily = "宋体";// 字体
    private int fontSize = 12;// 字体大小
    private UnderlinePatterns underline = UnderlinePatterns.NONE;// 下划线
    public static final WordStyle TITLE = new WordStyle("微软雅黑", 26);// 标题
    public static final WordStyle SUBTITLE = new WordStyle("宋体", 18);// 副标题
    public static final WordStyle LEVEL1 = new WordStyle(true, "宋体", 20);// 1级标题
    public static final WordStyle LEVEL2 = new WordStyle(true, "宋体", 14);// 2级标题
    public static final WordStyle LEVEL3 = new WordStyle(true, "宋体", 12);// 3级标题
    public static final WordStyle TEXT = new WordStyle("微软雅黑", 10);// 正文
    public static final WordStyle HIGHLINGHT = new WordStyle("微软雅黑", 10, "FF0000");// 红色高亮
    public static final WordStyle WHITE = new WordStyle("微软雅黑", 10, "FFFFFF");// 白色文字

    public WordStyle(String fontFamily, int fontSize) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
    }

    public WordStyle(boolean bold, String fontFamily, int fontSize) {
        this.bold = bold;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
    }

    public WordStyle(String fontFamily, int fontSize, String color) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.color = color;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isTalic() {
        return talic;
    }

    public void setTalic(boolean talic) {
        this.talic = talic;
    }

    public boolean isStrikethrough() {
        return strikethrough;
    }

    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public UnderlinePatterns getUnderline() {
        return underline;
    }

    public void setUnderline(UnderlinePatterns underline) {
        this.underline = underline;
    }

    public boolean isImprinted() {
        return imprinted;
    }

    public void setImprinted(boolean imprinted) {
        this.imprinted = imprinted;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

}
