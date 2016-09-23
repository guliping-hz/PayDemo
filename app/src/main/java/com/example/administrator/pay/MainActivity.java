package com.example.administrator.pay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.administrator.pay.presenter.MainPresenter;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

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
