package com.example.administrator.presenter;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.model.WXPay;
import com.example.administrator.pay.MainActivity;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/6/23.
 */

public class MainPresenter {

    static final String Tag = "MainPresenter";
    public static final String WX_APPID = "wxb4ba3c02aa476ea1";//测试微信APPID
    public static final String API_WXSign = "";

    private Activity mActivity;

    /***
     *
     * @param activity
     * @param type 商品ID
     */
    public void doWxPay(Activity activity,int type){

        mActivity = activity;
        final IWXAPI api = WXAPIFactory.createWXAPI(activity, WX_APPID);

        if(api.isWXAppInstalled())
        {
            rx.Observable.just(type)//指定购买的商品ID
                    .observeOn(Schedulers.newThread())
                    .map(new Func1<Integer, String>() {
                        @Override
                        public String call(Integer integer) {
                            //拼装请求URL
                            //return API_WXSign+"?type="+integer;

                            //测试
                            return "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String s) {
                            //同步 OKHttp Get
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(s)
                                    .build();

                            Response response = null;
                            String ret = null;
                            try {
                                response  = client.newCall(request).execute();

                                Headers responseHeaders = response.headers();
                                for (int i = 0; i < responseHeaders.size(); i++) {
                                    Log.d(Tag,"Get Headers : "+responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                }

                                ret = response.body().string();
                                Log.d(Tag,"Get Body : "+ret);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return ret;
                        }
                    })
                    .map(new Func1<String, WXPay>() {
                        @Override
                        public WXPay call(String s) {
                            if (s != null)
                            {
                                final Gson gson = new Gson();
                                WXPay ret = gson.fromJson(s,WXPay.class);
                                ret.packag = "Sign=WXPay";
                                return ret;
                            }
                            return null;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<WXPay>() {
                        @Override
                        public void call(WXPay wxPay) {

                            {
                                api.registerApp(WX_APPID);

                                PayReq req = new PayReq();
                                req.appId			= WX_APPID;
                                req.partnerId		= wxPay.partnerid;
                                req.prepayId		= wxPay.prepayid;
                                req.nonceStr		= wxPay.noncestr;
                                req.timeStamp		= wxPay.timestamp;
                                req.packageValue	= wxPay.packag;
                                req.sign			= wxPay.sign;
                                req.extData			= wxPay.extData; // optional
                                Toast.makeText(mActivity, "正常调起支付", Toast.LENGTH_SHORT).show();
                                api.sendReq(req);
                            }

                        }
                    });
        }
        else
        {
            Toast.makeText(mActivity, "请先安装微信", Toast.LENGTH_SHORT).show();
        }
    }

}
