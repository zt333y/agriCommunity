package com.example.agricommunity.common;

/**
 * 统一的 API 响应结果封装类
 * 以后所有的 Controller 接口，返回值都必须套上这个 Result
 */
public class Result<T> {
    private Integer code; // 状态码：200代表成功，500代表失败
    private String msg;   // 给前端的提示信息，比如"登录成功"、"密码错误"
    private T data;       // 真正要返回的数据（比如用户信息、商品列表）

    // --- 快捷方法：成功时调用 (默认提示语) ---
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // --- 快捷方法：成功时调用 (自定义提示语，非常实用！) ---
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    // --- 快捷方法：失败时调用 ---
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    // --- 下面是 Getter 和 Setter 方法 ---
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}