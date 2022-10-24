package com.asiainfo.model.system;

public class WebResult {
    public static final WebResult SUCCESS = new WebResult(true, "success");
    private boolean result = true;
    private String message;
    private Object data;

    public WebResult() {

    }

    public WebResult(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public WebResult(String message) {
        this.result = false;
        this.message = message;
    }

    public WebResult(boolean result, String message, Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }
    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
