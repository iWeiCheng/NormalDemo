package com.jiajun.demo.moudle.wechat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiajun.demo.R;
import com.jiajun.demo.base.BaseActivity;
import com.jiajun.demo.config.Const;
import com.jiajun.demo.model.entities.WechatItem;
import com.jiajun.demo.moudle.main.MainsActivity;
import com.jiajun.demo.network.Network;
import com.jiajun.demo.util.PixelUtil;
import com.jiajun.demo.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.jiajun.demo.moudle.wechat.WechatFragment.WECHAT_APPKEY;

/**
 * Created by dan on 2017/8/24/024.
 */

public class NewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.recycler_wechat)
    RecyclerView recyclerWechat;
    @BindView(R.id.swiper_wechat)
    SwipeRefreshLayout swiperWechat;
    
    Observer<WechatItem> observable = new Observer<WechatItem>(){

        @Override
        public void onCompleted() {
            
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(WechatItem wechatItem) {
            setNewDataAddList(wechatItem);
        }
    };
    private WechatItemAdapter mWechatItemAdapter;
    private boolean mRefreshMark;
    private int mPageMark = 1;
    private Subscription mSubscription;
    private int mPs =10;
    private ArrayList<WechatItem.ResultBean.ListBean> mListBeanList;


    private void initRecyclerWechat() {
        recyclerWechat.setLayoutManager(new GridLayoutManager(getContext().getApplicationContext(), 2));
        mListBeanList = new ArrayList<>();
        boolean isNotLoad = (boolean) SPUtils.get(getContext(), Const.SLLMS, false);
        int imgWidth = PixelUtil.getWindowWidth();
        int imgHeight = imgWidth * 3 / 4;
        mWechatItemAdapter = new WechatItemAdapter(mListBeanList, isNotLoad, imgWidth, imgHeight);
        mWechatItemAdapter.openLoadAnimation();
        mWechatItemAdapter.setOnLoadMoreListener(this);
        mWechatItemAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                if (position == 0) {
                    return 2;
                } else {
                    return mWechatItemAdapter.getData().get(position).getSpansize();
                }
            }
        });
        recyclerWechat.setAdapter(mWechatItemAdapter);
        recyclerWechat.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                WechatItem.ResultBean.ListBean listBean = (WechatItem.ResultBean.ListBean) adapter.getData().get(position);

                Intent intent = new Intent(getContext(), MainsActivity.class);
                intent.putExtra("wechat", listBean);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        NewsActivity.this,
                        view.findViewById(R.id.img_wechat_style),
                        getString(R.string.transition_wechat_img)
                );

                ActivityCompat.startActivity((Activity) getContext(), intent, optionsCompat.toBundle());
            }
        });

    }

    @Override
    protected void loadLayout() {
            setContentView(R.layout.activity_news);
    }

    @Override
    public void initPresenter() {
        initSwipeRefresh();
        initRecyclerWechat();
        requestData();
    }


    private void initSwipeRefresh() {
        swiperWechat.setOnRefreshListener(this);
        swiperWechat.setColorSchemeColors(Color.rgb(47, 223, 189));
        swiperWechat.setEnabled(false);
    }

    @Override
    public void onRefresh() {
        mWechatItemAdapter.setEnableLoadMore(false);
        mRefreshMark = true;
        mPageMark = 1;
        requestData();
    }

    private void requestData() {
        unsubscribe();
        mSubscription = Network.getWechatApi()
                .getWechat(WECHAT_APPKEY, mPageMark, mPs)//key,页码,每页条数
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observable);
    }

    private void unsubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onLoadMoreRequested() {
        swiperWechat.setEnabled(false);
        requestData();
    }


    private void setNewDataAddList(WechatItem wechatItem) {
        if (wechatItem != null && wechatItem.getError_code() == 0) {
            mPageMark++;
            List<WechatItem.ResultBean.ListBean> newData = wechatItem.getResult().getList();
            WechatItem.ResultBean.ListBean listBean = newData.get(0);
            listBean.setItemType(1);
            listBean.setSpansize(2);
            if (mRefreshMark) {
                mWechatItemAdapter.setNewData(newData);
                mRefreshMark = false;
            } else {
                mWechatItemAdapter.addData(newData);
            }

            if (swiperWechat.isRefreshing()) {
                swiperWechat.setRefreshing(false);
            }
            if (!swiperWechat.isEnabled()) {
                swiperWechat.setEnabled(true);
            }
            if (mWechatItemAdapter.isLoading()) {
                mWechatItemAdapter.loadMoreComplete();
            }
            if (!mWechatItemAdapter.isLoadMoreEnable()) {
                mWechatItemAdapter.setEnableLoadMore(true);
            }
        } else {
            if (mWechatItemAdapter.isLoading()) {
                Toast.makeText(getContext(), R.string.load_more_error, Toast.LENGTH_SHORT).show();
                mWechatItemAdapter.loadMoreFail();
            } else {
//                mWechatItemAdapter.setEmptyView(notDataView);
                if (swiperWechat.isRefreshing()) {
                    swiperWechat.setRefreshing(false);
                }
                if (swiperWechat.isEnabled()) {
                    swiperWechat.setEnabled(false);
                }
            }
        }

    }
}
