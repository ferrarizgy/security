package com.cmcc.hy.pojo;

public class ResultVo {
    private Integer status;//状态码
    private String message;//状态信息
    private Object result;//数据体


    @Override
    public String toString() {
        return "ResultVo{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public ResultVo() {

    }

    public ResultVo(Integer status, String message, Object result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }
}
