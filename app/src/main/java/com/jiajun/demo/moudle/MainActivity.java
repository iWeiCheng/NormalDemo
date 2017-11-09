package com.jiajun.demo.moudle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiajun.demo.R;
import com.jiajun.demo.model.BaseBean;
import com.jiajun.demo.model.entities.VersionBean;
import com.jiajun.demo.model.entities.WechatItem;
import com.jiajun.demo.network.BaseObserver;
import com.jiajun.demo.network.Network;
import com.orhanobut.logger.Logger;


import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String WECHAT_APPKEY = "26ce25ffcfc907a26263e2b0e3e23676";
    //每页请求的 item 数量
    public final int mPs = 21;
    public int mPageMark = 1;
    private Subscription mSubscription;

    private BaseObserver<VersionBean.DataBean> observer = new BaseObserver<VersionBean.DataBean>() {

        @Override
        public void onSuccess(VersionBean.DataBean bean) {
            Logger.e("test:"+bean.getVersionUrl());
        }

        @Override
        public void onError(int code, String message, BaseBean baseBean) {
            Logger.e("error:"+message);
        }

        @Override
        public void networkError(Throwable e) {
            Logger.e("service_error");
        }
    };

    Observer<WechatItem> mObserver = new Observer<WechatItem>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logger.e("onError:" + e.getMessage());
//            onErrorView();
//            if (mSwiperWechat.isRefreshing()) {
//                mSwiperWechat.setRefreshing(false);
//            }
//            if (mSwiperWechat.isEnabled()) {
//                mSwiperWechat.setEnabled(false);
//            }

        }

        @Override
        public void onNext(WechatItem wechatItem) {
//            setNewDataAddList(wechatItem);
            WechatItem.ResultBean result =  wechatItem.getResult();
            result.getList();
        }
    };

    Observer<BaseBean<VersionBean.DataBean>> testObserver = new Observer<BaseBean<VersionBean.DataBean>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logger.e("onError:" + e.getMessage());
//            onErrorView();
        }

        @Override
        public void onNext(BaseBean<VersionBean.DataBean> bean) {
//            setNewDataAddList(wechatItem);
            VersionBean.DataBean data = bean.getData();
            data.getVersionNumber();
            if(bean.getResultCode()!=1){

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        unsubscribe();
        mSubscription = Network.getWechatApi()
                .getWechat(WECHAT_APPKEY, mPageMark, mPs)//key,页码,每页条数
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mObserver);
        mSubscription = Network.getVersionApi().getVersion("getVersion","2","B2617158AB4221EBA92C4BA589BA0998","891642730","460075891207698","1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void unsubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
