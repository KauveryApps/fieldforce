package com.kauveryhospital.fieldforce.Globaldeclare;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    @SerializedName("result")
    @Expose
    private Result_ result;

    @SerializedName("message")
    @Expose
    private List<Message> message = null;


    private final static long serialVersionUID = -7691001539827547897L;

    public Result_ getResult() {
        return result;
    }

    public void setResult(Result_ result) {
        this.result = result;
    }



    public List<Message> getMessage() {
        return message;
    }

    public void setMessage(List<Message> message) {
        this.message = message;
    }
}
