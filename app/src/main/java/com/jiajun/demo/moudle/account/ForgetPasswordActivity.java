package com.jiajun.demo.moudle.account;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jiajun.demo.R;
import com.jiajun.demo.base.BaseActivity;
import com.jiajun.demo.views.ClearEditText;
import com.jiajun.demo.views.PassWordEditText;


/**
 * Created by hgj on 2016/7/22 0022.
 * 忘记密码
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener{


    private TextView titleCenterTv;
    private TextView titleLeftIv;
    private ClearEditText phoneEt;
    private EditText codeEt;
    private TextView codeBtn;
    private Button submitBtn;
    private PassWordEditText password1Edt;
    private PassWordEditText password2Edt;

    private String phone;
    private String type;
    private String password1;
    private String password2;
    private String mobielVerify; // 验证码
    private TimeCount time;

    @Override
    protected void loadLayout() {
        setContentView(R.layout.account_fogetpassword_activity);
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        titleCenterTv = (TextView) findViewById(R.id.titleCeneter_textView);
        titleLeftIv = (TextView) findViewById(R.id.titleLeft_textView);
        phoneEt = (ClearEditText) findViewById(R.id.forget_password_phoneNum);
        codeEt = (EditText) findViewById(R.id.verification_code);
        codeBtn = (TextView) findViewById(R.id.get_forget_password_Code);
        submitBtn = (Button) findViewById(R.id.forgetPwd_submit);
        password1Edt = (PassWordEditText) findViewById(R.id.password1);
        password2Edt = (PassWordEditText) findViewById(R.id.password2);
    }

    @Override
    protected void setListener(Bundle savedInstanceState) {
        super.setListener(savedInstanceState);
        titleLeftIv.setOnClickListener(this);
        codeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        super.processLogic(savedInstanceState);
        titleLeftIv.setVisibility(View.VISIBLE);
        titleCenterTv.setText(R.string.findPassword);
        time = new TimeCount(60000, 1000);
    }

    @Override
    public void onClick(View v) {
        if (v == titleLeftIv) {
            finish();
        } else if (v == submitBtn) {
            phone = phoneEt.getText().toString();
            mobielVerify = codeEt.getText().toString();
            password1 = password1Edt.getText().toString();
            password2 = password2Edt.getText().toString();
            submit();
        } else if (v == codeBtn) {
            phone = phoneEt.getText().toString();
            type = "get";
            getCode();
        }
    }

    private void submit() {
//        PresenterStateMessage message = ApiPresenter.getInstance().setPassword(
//                RequestState.Account_SetPassword, mobielVerify, phone, password1, password2, this);
//
//        if (message.state() != PresenterState.SUCCESS) {
//            Toast.makeText(ForgetPasswordActivity.this, message.message(), Toast.LENGTH_SHORT).show();
//        }
    }


    // 获取验证码
    private void getCode() {
//        PresenterStateMessage message = ApiPresenter.getInstance().registerSendMobileVcode(
//                RequestState.SEND_CODE, phone, this);
//        if (message.state() != PresenterState.SUCCESS) {
//            Toast.makeText(ForgetPasswordActivity.this, message.message(), Toast.LENGTH_SHORT).show();
//            return;
//        }
        time.start();
    }

//    @Override
//    public void onRegisterResult(long requestId, BaseResponse baseResponse) {
//
//    }
//
//    @Override
//    public void onRegisterSendMobileVerifyResult(long requestId, BaseResponse baseResponse) {
//
//    }
//
//    @Override
//    public void onGetOrgInfoResult(long requestId, OrganizationInfoBean bean, BaseResponse baseResponse) {
//
//    }
//
//
//    @Override
//    public void onHttpFailure(long requestId, String code, String message) {
//        super.onHttpFailure(requestId, code, message);
//        Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onSetPasswordResult(long requestId, BaseResponse baseResponse) {
//        ToastUtil.showToast(this, baseResponse.message());
//        finish();
//    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            codeBtn.setText("获取验证码");
            codeBtn.setTextColor(getResources().getColor(R.color.white));
            codeBtn.setBackgroundResource(R.drawable.shape_login);
            codeBtn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            codeBtn.setClickable(false);
            codeBtn.setTextColor(getResources().getColor(R.color.text_login_gray));
            codeBtn.setText("(" + millisUntilFinished / 1000 + ")重新获取");
        }
    }
}
