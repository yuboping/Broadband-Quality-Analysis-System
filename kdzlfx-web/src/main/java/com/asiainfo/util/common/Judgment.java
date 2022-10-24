package com.asiainfo.util.common;

import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 判断工具
 * 
 * @author luohuawuyin
 *
 */
public class Judgment {
    private static final ScriptEngine JSE = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
     * 判断List是否为空
     * 
     * @param list
     * @return
     */
    public static boolean listIsNull(List<?> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean mapIsNull(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 判断string是否为空
     * 
     * @param target
     * @return
     */
    public static boolean stringIsNull(String target) {
        if (target == null || "".equals(target.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断string表达式
     * 
     * @param expression
     * @return
     */
    public static boolean judgeExpression(String expression) {
        try {
            return (boolean) JSE.eval(expression);
        } catch (ScriptException e) {
            return false;
        }
    }

}
