package com.tz.web.dto;

import javafx.scene.chart.PieChartBuilder;
import javafx.scene.input.KeyCodeCombinationBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseDTO<T> {
    private  int code;
    private String msg;
    private  boolean success;
    private T t;
    public ResponseDTO(String msg){
        this.msg=msg;
        this.code=-1;
        this.success=false;
    }
    public static <T> ResponseDTO<T> ok(T data) {
        return new ResponseDTO<T>().success(true).code(0).data(data);
    }
    private ResponseDTO<T> code(int i) {
        this.code=i;
        return  this;
    }
    private ResponseDTO<T> data(T t) {
        this.t=t;
        return  this;
    }
    private   ResponseDTO<T> success(boolean b) {
        this.success=b;
        return this;
    }
}
