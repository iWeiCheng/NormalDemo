package com.jiajun.demo.moudle.account;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jiajun.demo.R;
import com.jiajun.demo.base.BaseActivity;
import com.jiajun.demo.model.BaseBean;
import com.jiajun.demo.moudle.account.entities.LoginBean;
import com.jiajun.demo.network.BaseObserver;
import com.jiajun.demo.network.Network;
import com.jiajun.demo.util.MD5;
import com.jiajun.demo.util.RandomNum;
import com.jiajun.demo.util.SharedPreferenceUtil;
import com.jiajun.demo.views.ClearEditText;
import com.jiajun.demo.views.PassWordEditText;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by hgj on 2016/7/21 0021.
 * 登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layout;
    private Button loginBt;
    private TextView registerTv;
    private TextView forgetPwTv;
    private TextView remember_pwd;
    //    private ApiPresenter presenter;
    private String phone, password; // 电话 密码
    private ClearEditText nameEt;
    private PassWordEditText passWordEt;
    private SharedPreferences preferences;
    private boolean isRemember = true;
    private String type;
    private TextView app_name;

    private Subscription mSubscription;

    private BaseObserver<LoginBean> observer = new BaseObserver<LoginBean>() {

        @Override
        public void onSuccess(LoginBean bean) {
            dismissDialog();
            Logger.e("test:" + bean.getCompanyName());
            showToast(bean.getUserName());
        }

        @Override
        public void onError(int code, String message, BaseBean baseBean) {
            dismissDialog();
            Logger.e("error:" + message);
        }

        @Override
        public void networkError(Throwable e) {
            dismissDialog();
            Logger.e("service_error");
        }
    };


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void loadLayout() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // 加上这句设置为全屏 不加则只隐藏title
        setContentView(R.layout.account_login_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        presenter = ApiPresenter.getInstance();
    }


    private void unsubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void initPresenter() {
        unsubscribe();
    }

    @Override
    protected void getExtra(@NonNull Bundle bundle) {
        super.getExtra(bundle);
        type = bundle.getString("type");
    }

    @Override
    protected int getTranslucentColor() {
        return R.color.transparent;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        preferences = SharedPreferenceUtil.getSharedPreferences(this);
        isRemember = preferences.getBoolean("isRemember", false);

        layout = (LinearLayout) findViewById(R.id.layout);
        loginBt = (Button) findViewById(R.id.loginButton);
        registerTv = (TextView) findViewById(R.id.register_textView);
        app_name = (TextView) findViewById(R.id.tv_app_name);
        forgetPwTv = (TextView) findViewById(R.id.forget_password_textView);
        remember_pwd = (TextView) findViewById(R.id.remember_pwd);
        nameEt = (ClearEditText) findViewById(R.id.login_username);
        passWordEt = (PassWordEditText) findViewById(R.id.login_password);

        //特殊：龙粤行登录界面名字改为车险业务
        if (getString(R.string.app_name).equals("龙粤行")) {
            app_name.setText("车险业务");
        }
        if (isRemember) {
            Drawable drawable = getResources().getDrawable(R.drawable.choose_1);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            remember_pwd.setCompoundDrawables(drawable, null, null, null);
        }

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        String phone = preferences.getString("phone", "");
        if (!phone.equals("")) {
            nameEt.setText(phone);
        }
        if (preferences.getBoolean("isRemember", false)) {
            String pwd = preferences.getString("pwd", "");
            if (!pwd.equals("")) {
                passWordEt.setText(pwd);
            }
        }
    }

    @Override
    protected void setListener(Bundle savedInstanceState) {
        super.setListener(savedInstanceState);
        remember_pwd.setOnClickListener(this);
        loginBt.setOnClickListener(this);
        registerTv.setOnClickListener(this);
        forgetPwTv.setOnClickListener(this);
        layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == remember_pwd) {
            if (!isRemember) {
                Drawable drawable = getResources().getDrawable(R.drawable.login_select);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                remember_pwd.setCompoundDrawables(drawable, null, null, null);
                isRemember = true;
            } else {
                Drawable drawable = getResources().getDrawable(R.drawable.login_unselect);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                remember_pwd.setCompoundDrawables(drawable, null, null, null);
                isRemember = false;
            }
        }

        if (v == loginBt) {
            // 登录操作

            phone = nameEt.getText().toString();
            password = passWordEt.getText().toString();
            showProgressDialog();
            String str_random = RandomNum.getrandom();
            String str_signature = MD5.get32MD5(phone + password + "20160808" + str_random);
            Map<String, String> map = new HashMap<>();
            map.put("method", "login");
            map.put("user", phone);
            map.put("pwd", password);
            map.put("random", str_random);
            map.put("signature", str_signature);

            mSubscription = Network.login().login(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);

        } else if (v == registerTv) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (v == forgetPwTv) {
            Intent intent = new Intent(this, ForgetPasswordActivity.class);
            startActivity(intent);
        } else if (v == layout) {
            hideIM(nameEt, this);
        }
    }

//    @Override
//    public void onLoginResult(long requestId, LoginBean bean, BaseResponse baseResponse) {
//        dismissDialog();
//        YSFUserInfo userInfo = new YSFUserInfo();
//        userInfo.userId = bean.getUserId();
//        userInfo.data = userInfoData(bean.getUserName() + "-" + bean.getCompanyName(), bean.getMobile()).toJSONString();
//        Unicorn.setUserInfo(userInfo);
//        Editor editor = preferences.edit();
//        editor.putString("userId", bean.getUserId());
//        editor.putString("phone", nameEt.getText().toString());
//        editor.putString("pwd", password);
//        editor.putBoolean("isRemember", isRemember);
//        editor.apply();
//        Intent intent = new Intent(this, MainViewActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public void onLoginFaild(long requestId, String code, String message) {
//        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
//    }

    private JSONArray userInfoData(String name, String mobile) {
        JSONArray array = new JSONArray();
        array.add(userInfoDataItem("real_name", name, false, -1, null, null)); // name
        array.add(userInfoDataItem("mobile_phone", mobile, false, -1, null, null)); // mobile
        return array;
    }

    private JSONObject userInfoDataItem(String key, Object value, boolean hidden, int index, String label, String href) {
        JSONObject item = new JSONObject();
        try {
            item.put("key", key);
            item.put("value", value);
            if (hidden) {
                item.put("hidden", true);
            }
            if (index >= 0) {
                item.put("index", index);
            }
            if (!TextUtils.isEmpty(label)) {
                item.put("label", label);
            }
            if (!TextUtils.isEmpty(href)) {
                item.put("href", href);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }

//    @Override
//    public void onHttpFailure(long requestId, String code, String message) {
//        super.onHttpFailure(requestId, code, message);
//        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
//    }

}
