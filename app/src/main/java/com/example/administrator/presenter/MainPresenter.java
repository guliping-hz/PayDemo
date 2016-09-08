package com.example.administrator.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.example.administrator.Alipay.OrderInfoUtil;
import com.example.administrator.Alipay.PayResult;
import com.example.administrator.model.SubscribeObj;
import com.example.administrator.model.WXPay;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
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

    //支付宝配置参数 start
    // 商户PID
    public static final String PARTNER = "2088102058580537";
    // 商户收款账号
    public static final String SELLER = "2088102058580537";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALnPN1AfO30CKsKJ" +
            "5lydrWVt8/e6+iTyYWRW4ARexFZsjiotKMjjLnez0Z7nPX4QjjvWFDp7zwC9Okr0" +
            "8skTfy83u5abrdYSscPUdOzglfm8KFwKC6bADUmpkVAg4JiausH4w6GrpaIsvqd0" +
            "/IWW2Wn2Vb43dg5u/oxGzwdNF/vnAgMBAAECgYEAuRSvBCAYIW0HKsAxtg9ZqK7d" +
            "VOXqROMqH8hpW/EJoQyAj4JsHa4tmw6CvsWXevsCspeLjrXEe/gBGKjlLWU+SRdJ" +
            "hiBcM55Jxmx+Esqnt4oo1SwPB8qAOAFy9tOT4sC2CC0sMmJRvq19NoSzwjfXz+na" +
            "SbvRWe65YtY2db9qUAECQQDkOzLUhi+7ewCWXv3ZNSFiC3bPR+XL+JGzuGwTJ/w/" +
            "+y/oXQF07C4Xs+8TSp0c8FprJV76dxEv+FJf5XePudlPAkEA0GqwqihTCWuTV+tF" +
            "FLOo+NtEROmzDKVGAjpNMFB957O9WHpUXFr0llAZwScWvJGwamVgqE83uW1PbNYw" +
            "aCjd6QJBAJoDPMTfnFxWn8nZZlHqIZHpDI7KBM9E+QWfYQb4R6fhWK3j/TSqoFwM" +
            "ZzvMcQNzSoDdYh+As898MhJWZf1OO88CQEsdaLaq+eJ3Rw9019zyM4AdZql/oOx5" +
            "1JWQ9ajoGbicay2sSSNQFL7n96BJukQULgTqrL98bZUC9JFBUQj5UaECQHT01hBy" +
            "4LvnH6QY+U5ep84rAN5i+6NAnUABowMvZDiuijPgQZs1+KOjMfomnilD9YASLIHc" +
            "pvxWHKMx2wyQa/w=";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";
    //支付宝配置参数 end

    /***
     *
     * @param activity
     * @param type 商品ID
     */
    public void doWxPay(final Activity activity,int type){
        final IWXAPI api = WXAPIFactory.createWXAPI(activity, WX_APPID);

        if(api.isWXAppInstalled())
        {
            Observable.just(type)//指定购买的商品ID
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
                    .map(new Func1<String, SubscribeObj>() {
                        @Override
                        public SubscribeObj call(String s) {
                            if (s != null)
                            {
                                SubscribeObj ret = new SubscribeObj(0,"");
                                final Gson gson = new Gson();
                                WXPay obj = gson.fromJson(s,WXPay.class);
                                obj.packag = "Sign=WXPay";
                                ret.obj = obj;
                                return ret;
                            }
                            else
                            {
                                SubscribeObj ret = new SubscribeObj(0,"网络异常");
                                return ret;
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<SubscribeObj>() {
                        @Override
                        public void call(SubscribeObj obj) {

                            if (obj.ret == 0) {
                                WXPay wxPay = (WXPay) obj.obj;
                                api.registerApp(WX_APPID);

                                PayReq req = new PayReq();
                                req.appId = WX_APPID;
                                req.partnerId = wxPay.partnerid;
                                req.prepayId = wxPay.prepayid;
                                req.nonceStr = wxPay.noncestr;
                                req.timeStamp = wxPay.timestamp;
                                req.packageValue = wxPay.packag;
                                req.sign = wxPay.sign;
                                req.extData = wxPay.extData; // optional
                                Toast.makeText(activity, "正常调起支付", Toast.LENGTH_SHORT).show();
                                api.sendReq(req);
                            } else {
                                Toast.makeText(activity, obj.msg, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Toast.makeText(activity, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Log.d(Tag,"Completed");
                        }
                    });
        }
        else
        {
            Toast.makeText(activity, "请先安装微信", Toast.LENGTH_SHORT).show();
        }
    }

    public void doAliPay(final Activity activity){

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        //for test 本地 start
                        String orderInfo = OrderInfoUtil.getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");

                        /**
                         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
                         */
                        String sign = OrderInfoUtil.sign(orderInfo);
                        try {
                            /**
                             * 仅需对sign 做URL编码
                             */
                            sign = URLEncoder.encode(sign, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        /**
                         * 完整的符合支付宝参数规范的订单信息
                         */
                        String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + OrderInfoUtil.getSignType();
                        //for test end

                        PayTask alipay = new PayTask(activity);
                        String result = alipay.pay(payInfo, true);
                        Log.i("msp", result.toString());

                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        PayResult payResult = new PayResult(s);

                        /**
                         * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                         * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                         * docType=1) 建议商户依赖异步通知
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
                        } else {
                            // 判断resultStatus 为非"9000"则代表可能支付失败
                            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                Toast.makeText(activity, "支付结果确认中", Toast.LENGTH_SHORT).show();
                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                Toast.makeText(activity, payResult.getMemo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}
