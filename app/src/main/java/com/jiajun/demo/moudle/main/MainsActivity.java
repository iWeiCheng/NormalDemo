package com.jiajun.demo.moudle.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jiajun.demo.R;
import com.jiajun.demo.app.BaseApplication;
import com.jiajun.demo.base.BaseActivity;
import com.jiajun.demo.base.BaseCustomActivity;
import com.jiajun.demo.config.Const;
import com.jiajun.demo.config.StatusBarCompat;
import com.jiajun.demo.model.entities.RefreshNewsFragmentEvent;
import com.jiajun.demo.moudle.find.FindFragment;
import com.jiajun.demo.moudle.me.MeFragment;
import com.jiajun.demo.moudle.news.NewsFragment;
import com.jiajun.demo.moudle.news_category.CategoryActivity;
import com.jiajun.demo.moudle.wechat.WechatFragment;
import com.jiajun.demo.update.AppUtils;
import com.jiajun.demo.update.UpdateChecker;
import com.jiajun.demo.util.SPUtils;
import com.jiajun.demo.util.StateBarTranslucentUtils;
import com.jiajun.demo.util.inputmethodmanager_leak.IMMLeaks;
import com.jiajun.demo.widget.TabBar_Mains;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dan on 2017/8/21.
 */

public class MainsActivity extends BaseCustomActivity {


    @BindView(R.id.framelayout_mains)
    FrameLayout framelayoutMains;
    @BindView(R.id.recommend_mains)
    TabBar_Mains recommendMains;
    @BindView(R.id.cityfinder_mains)
    TabBar_Mains cityfinderMains;
    @BindView(R.id.findtravel_mains)
    TabBar_Mains findtravelMains;
    @BindView(R.id.me_mains)
    TabBar_Mains meMains;

    private static final String NEWS_FRAGMENT = "NEWS_FRAGMENT";
    private static final String WECHAT_FRAGMENT = "WECHAT_FRAGMENT";
    private static final String FIND_FRAGMENT = "FIND_FRAGMENT";
    public static final String ME_FRAGMENT = "ME_FRAGMENT";
    public static final String FROM_FLAG = "FROM_FLAG";

    private FragmentManager sBaseFragmentManager;

    public MeFragment mMeFragment;
    public NewsFragment mNewsFragment;
    public WechatFragment mWechatFragment;
    public FindFragment mFindFragment;
    private String mCurrentIndex;

    public static List<String> logList = new CopyOnWriteArrayList<String>();

    @Override
    public void initContentView() {
        StateBarTranslucentUtils.setStateBarTranslucent(this);
        setContentView(R.layout.activity_mains);
        BaseApplication.setMainActivity(this);
        StatusBarCompat.compat(this);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        sBaseFragmentManager = getBaseFragmentManager();

        String startPage = WECHAT_FRAGMENT;
        String s = (String) SPUtils.get(this, Const.OPENNEWS, "nomagic");
        if (s.equals("magicopen")){
            recommendMains.setVisibility(View.VISIBLE);
            startPage = NEWS_FRAGMENT;
        }
        if (savedInstanceState != null) {
            initByRestart(savedInstanceState);
        } else {
            switchToFragment(startPage);
            mCurrentIndex = startPage;
        }

        int qbox_version = (int) SPUtils.get(this, Const.QBOX_NEW_VERSION, 0);
        if (qbox_version != 0 && qbox_version > AppUtils.getVersionCode(this)) {
            UpdateChecker.checkForNotification(this); //通知提示升级
        }

        //订阅事件
        EventBus.getDefault().register(this);
    }

    boolean isRestart = false;
    private void initByRestart(Bundle savedInstanceState) {

        mCurrentIndex = savedInstanceState.getString("mCurrentIndex");

        isRestart = true;
        Logger.e("恢复状态：" + mCurrentIndex);
        mMeFragment = (MeFragment) sBaseFragmentManager.findFragmentByTag(ME_FRAGMENT);
        if (recommendMains.getVisibility()==View.VISIBLE){
            mNewsFragment = (NewsFragment) sBaseFragmentManager.findFragmentByTag(NEWS_FRAGMENT);}
        mWechatFragment = (WechatFragment) sBaseFragmentManager.findFragmentByTag(WECHAT_FRAGMENT);
        mFindFragment = (FindFragment) sBaseFragmentManager.findFragmentByTag(FIND_FRAGMENT);

        switchToFragment(mCurrentIndex);
    }

    private void switchToFragment(String index) {
//        sFragmentTransaction = getFragmentTransaction();
        hideAllFragment();
        switch (index) {
            case NEWS_FRAGMENT:
                if (recommendMains.getVisibility() == View.VISIBLE) {
                    showNewsFragment();
                    Logger.e("newsopen:101");
                }
                break;
            case WECHAT_FRAGMENT:
                showWechatFragment();
                break;
            case FIND_FRAGMENT:
                showFindFragment();
                break;
            case ME_FRAGMENT:
                showMeFragment();
                break;
            default:

                break;
        }
        mCurrentIndex = index;
    }
    private void showMeFragment() {
        if (false == meMains.isSelected())
            meMains.setSelected(true);
        if (mMeFragment == null) {
            mMeFragment = MeFragment.newInstance();
            addFragment(R.id.framelayout_mains, mMeFragment, ME_FRAGMENT);
        }else if (isRestart = true){
            getFragmentTransaction().show(mMeFragment).commit();
            isRestart = false;
        }else {
            showFragment(mMeFragment);
        }
    }
    
    private void showFindFragment() {
        if (false == findtravelMains.isSelected()) {
            findtravelMains.setSelected(true);
        }
        if (mFindFragment == null) {
            mFindFragment = FindFragment.newInstance("", "");
            addFragment(R.id.framelayout_mains, mFindFragment, FIND_FRAGMENT);
        }else if (isRestart = true){
            isRestart =false;
            getFragmentTransaction().show(mFindFragment).commit();
        } else {
            showFragment(mFindFragment);
        }

    }

    private void showWechatFragment() {
        if (false == cityfinderMains.isSelected()) {
            cityfinderMains.setSelected(true);
        }
        if (mWechatFragment == null) {
            mWechatFragment = mWechatFragment.newInstance("", "");
            addFragment(R.id.framelayout_mains, mWechatFragment, WECHAT_FRAGMENT);
        }else if (isRestart = true){
            isRestart =false;
            getFragmentTransaction().show(mWechatFragment).commit();
        } else {
            showFragment(mWechatFragment);
        }

    }

    private void showNewsFragment() {
        if (recommendMains.getVisibility() != View.VISIBLE) {
            return;
        }
        if (false == recommendMains.isSelected()) {
            recommendMains.setSelected(true);
        }
        if (mNewsFragment == null) {
            Logger.e("恢复状态："+"null");
            mNewsFragment = NewsFragment.newInstance("a", "b");
            addFragment(R.id.framelayout_mains, mNewsFragment, NEWS_FRAGMENT);
        }else if (isRestart = true){
            isRestart =false;
            getFragmentTransaction().show(mNewsFragment).commit();
        } else {
            showFragment(mNewsFragment);
        }

    }

    private void hideAllFragment() {
        if (mNewsFragment != null) {
            hideFragment(mNewsFragment);
        }
        if (mWechatFragment != null) {
            hideFragment(mWechatFragment);
        }
        if (mFindFragment != null) {
            hideFragment(mFindFragment);
        }
        if (mMeFragment != null) {
            hideFragment(mMeFragment);
        }
        if (recommendMains.getVisibility() == View.VISIBLE) {
            recommendMains.setSelected(false);}
        findtravelMains.setSelected(false);
        cityfinderMains.setSelected(false);
        meMains.setSelected(false);
    }

    @Override
    public void initPresenter() {

    }

    @OnClick({R.id.recommend_mains, R.id.cityfinder_mains, R.id.findtravel_mains, R.id.me_mains})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recommend_mains:
                if (!mCurrentIndex.equals(NEWS_FRAGMENT))
                    switchToFragment(NEWS_FRAGMENT);
                break;
            case R.id.cityfinder_mains:
                if (!mCurrentIndex.equals(WECHAT_FRAGMENT))
                    switchToFragment(WECHAT_FRAGMENT);
                break;
            case R.id.findtravel_mains:
                if (!mCurrentIndex.equals(FIND_FRAGMENT))
                    switchToFragment(FIND_FRAGMENT);
                break;
            case R.id.me_mains:
                if (!mCurrentIndex.equals(ME_FRAGMENT))
                    switchToFragment(ME_FRAGMENT);
                break;
        }
    }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == Const.NEWSFRAGMENT_CATEGORYACTIVITY_REQUESTCODE
                    && resultCode == Const.NEWSFRAGMENT_CATEGORYACTIVITY_RESULTCODE) {
                mNewsFragment.initView();
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void OnRefreshNewsFragmentEvent(RefreshNewsFragmentEvent event) {
            startActivityForResult(new Intent(MainsActivity.this, CategoryActivity.class), event.getMark_code());
        }

        @Override
        protected void onDestroy() {
            EventBus.getDefault().unregister(this);
            for (Fragment fragment :
                    getBaseFragmentManager().getFragments()) {
                getFragmentTransaction().remove(fragment);
            }
            super.onDestroy();
            BaseApplication.setMainActivity(null);
            IMMLeaks.fixFocusedViewLeak(getApplication());//解决 Android 输入法造成的内存泄漏问题。
        }

        @Override
        protected void onResume() {
            super.onResume();
            refreshLogInfo();
        }

    public void refreshLogInfo() {
        String AllLog = "";
        for (String log : logList) {
            AllLog = AllLog + log + "\n\n";
        }
        Logger.e(AllLog);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mCurrentIndex", mCurrentIndex);
        Logger.e("保存状态");
    }

    /**
     * 监听用户按返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    private boolean mIsExit;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)   {

        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 用于优雅的退出程序(当从其他地方退出应用时会先返回到此页面再执行退出)
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra(Const.TAG_EXIT, false);
            if (isExit) {
                finish();
            }
        }
    }
}
