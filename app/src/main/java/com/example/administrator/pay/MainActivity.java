package com.example.administrator.pay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.SubscriptSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.model.WXPay;
import com.example.administrator.presenter.MainPresenter;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static java.util.concurrent.TimeUnit.MICROSECONDS;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.wx) Button mBtnWx;
    @Bind(R.id.ali) Button mBtnAli;

    static final String Tag = "MainActivity";

    Subscription mBtnWxSub;
    Subscription mBtnAliSub;
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new MainPresenter();

        mBtnWxSub = RxView.clicks(mBtnWx)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.d(Tag,"Click WX");

                        //测试 商品ID为1
                        mPresenter.doWxPay(MainActivity.this,1);

                    }
                });

        mBtnAliSub = RxView.clicks(mBtnAli)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.d(Tag,"Click WX");

                        //测试 商品ID为1
                        mPresenter.doAliPay(MainActivity.this);

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBtnWxSub.unsubscribe();
        mBtnAliSub.unsubscribe();
    }
}
