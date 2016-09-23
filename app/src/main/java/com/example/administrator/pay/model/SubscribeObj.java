package com.example.administrator.pay.model;

/**
 * Created by Administrator on 2016/6/24.
 */

public class SubscribeObj {
    public int ret;//0 正常
    public String msg;
    public Object obj;

    public SubscribeObj(int ret, String msg){
        this.ret = ret;
        this.msg = msg;
    }
}
