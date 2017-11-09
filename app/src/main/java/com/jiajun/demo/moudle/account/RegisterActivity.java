package com.jiajun.demo.moudle.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiajun.demo.R;
import com.jiajun.demo.base.BaseActivity;
import com.jiajun.demo.moudle.account.entities.OrganizationInfoBean;
import com.jiajun.demo.util.SharedPreferenceUtil;
import com.jiajun.demo.views.ClearEditText;
import com.jiajun.demo.views.PassWordEditText;

import java.util.ArrayList;

import butterknife.BindView;


//import butterknife.BindView;

/**
 * Created by djj on 2016/10/26 0021.
 * 注册
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, SelectFragment.OnClickSubmitListener {


    @BindView(R.id.titleLeft_textView)
    TextView back;
    @BindView(R.id.titleCeneter_textView)
    TextView title;
    @BindView(R.id.name)
    ClearEditText name;
    @BindView(R.id.idCard)
    ClearEditText idCard;
    @BindView(R.id.phoneNum)
    ClearEditText phoneNum;
    @BindView(R.id.verification_code)
    ClearEditText verificationCode;
    @BindView(R.id.get_forget_password_Code)
    TextView getVerificationCode;
    @BindView(R.id.pwd)
    PassWordEditText pwd;
    @BindView(R.id.confirmPwd)
    PassWordEditText confirmPwd;
    @BindView(R.id.email)
    ClearEditText email;
    @BindView(R.id.text_yes)
    TextView textYes;
    @BindView(R.id.image_yes)
    ImageView imageYes;
    @BindView(R.id.text_no)
    TextView textNo;
    @BindView(R.id.image_no)
    ImageView imageNo;
    @BindView(R.id.recommendNum)
    ClearEditText recommendNum;
    @BindView(R.id.hasRecommend_layout)
    LinearLayout hasRecommendLayout;
    @BindView(R.id.group)
    TextView group;
    @BindView(R.id.branch)
    TextView branch;
    @BindView(R.id.noRecommend_layout)
    LinearLayout noRecommendLayout;
    @BindView(R.id.register)
    Button register;

    private SharedPreferences preferences;
    private TimeCount time;
//    private ApiPresenter presenter;
    private String phone;
    private String password1, password2;
    private String isRecommend = "";
    private String groudId="", companyId="";
    private SelectFragment selectFragment_group;
    private SelectFragment selectFragment_company;
    private ArrayList<OrganizationInfoBean.OrgInfoBean> grouds;
    private int index = -2;//选择集团的位置


    @Override
    protected void loadLayout() {
//        setTheme(R.style.greyTheme);
        setContentView(R.layout.account_register_activity2);
//        presenter = ApiPresenter.getInstance();
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected int getTranslucentColor() {
        return R.color.textGreen;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        preferences = SharedPreferenceUtil.getSharedPreferences(this);
        title.setText(getString(R.string.register));
        back.setVisibility(View.VISIBLE);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
//        PresenterStateMessage message = presenter.getOrgInfo(2, this);
    }

    @Override
    protected void setListener(Bundle savedInstanceState) {
        super.setListener(savedInstanceState);
        back.setOnClickListener(this);
        imageYes.setOnClickListener(this);
        imageNo.setOnClickListener(this);
        group.setOnClickListener(this);
        branch.setOnClickListener(this);
        register.setOnClickListener(this);
        getVerificationCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        phone = phoneNum.getText().toString();
        if (v == register) {
            // 注册操作
            password1 = pwd.getText().toString();
            password2 = confirmPwd.getText().toString();
//            showProgressDialog();
//            PresenterStateMessage message = presenter.register(1, name.getText().toString(), phone, idCard.getText().toString(), verificationCode.getText().toString(), email.getText().toString(),
//                    password1, password2, isRecommend, recommendNum.getText().toString(), groudId, companyId, this);
//            if (message.state() != PresenterState.SUCCESS) {
//                dismissDialog();
//                Toast.makeText(RegisterActivity.this, message.message(), Toast.LENGTH_SHORT).show();
//            }
        } else if (v == getVerificationCode) {
            getCode();
        } else if (v == back) {
            finish();
        } else if (v == imageYes) {
            isRecommend = "1";
            hasRecommendLayout.setVisibility(View.VISIBLE);
            noRecommendLayout.setVisibility(View.GONE);
            imageYes.setImageResource(R.drawable.radio_blue);
            imageNo.setImageResource(R.drawable.radio_white);
        } else if (v == imageNo) {
            isRecommend = "0";
            hasRecommendLayout.setVisibility(View.GONE);
            noRecommendLayout.setVisibility(View.VISIBLE);
            imageYes.setImageResource(R.drawable.radio_white);
            imageNo.setImageResource(R.drawable.radio_blue);
        } else if (v == group) {//选择集团
            if (selectFragment_group == null) {
                selectFragment_group = SelectFragment.getInstance(grouds, -1);
                selectFragment_group.setClickItemListener(this);
            }
            selectFragment_group.show(getSupportFragmentManager(), "");
        } else if (v == branch) {//选择分公司
            if (index == -1) {
                Toast.makeText(this, "请先选择集团", Toast.LENGTH_LONG).show();
                return;
            }
            selectFragment_company = SelectFragment.getInstance(grouds, index);
            selectFragment_company.setClickItemListener(this);
            selectFragment_company.show(getSupportFragmentManager(), "");
        }
    }

    // 获取验证码
    private void getCode() {
//        PresenterStateMessage message = ApiPresenter.getInstance().registerSendMobileVcode(
//                RequestState.SEND_CODE, phone, this);
//        if (message.state() != PresenterState.SUCCESS) {
//            Toast.makeText(RegisterActivity.this, message.message(), Toast.LENGTH_SHORT).show();
//        }
    }

//    @Override
//    public void onHttpFailure(long requestId, String code, String message) {
//        super.onHttpFailure(requestId, code, message);
//        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onRegisterResult(long requestId, BaseResponse baseResponse) {
//        showPopwindow("注册成功");
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public void onRegisterSendMobileVerifyResult(long requestId, BaseResponse baseResponse) {
//        //已经获取验证码开启计时器
//        time.start();
//    }

//    @Override
//    public void onGetOrgInfoResult(long requestId, OrganizationInfoBean bean, BaseResponse baseResponse) {
//        grouds = (ArrayList<OrganizationInfoBean.OrgInfoBean>) bean.getOrgInfo();
//    }

    @Override
    public void onClickItem(int index, int position) {
        if (index == -1) {
            if (this.index == position) {
                return;
            }
            this.index = position;
            groudId = grouds.get(position).getGroudId();
            group.setText(grouds.get(position).getName());
            selectFragment_group.dismiss();
            companyId = "";
            branch.setText("");
        } else {
            companyId = grouds.get(index).getSubOrg().get(position).getCompanyId();
            branch.setText(grouds.get(index).getSubOrg().get(position).getName());
            selectFragment_company.dismiss();
        }
    }


    /**
     * 倒计时类~
     *
     * @author Administrator
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            getVerificationCode.setText("获取验证码");
            getVerificationCode.setTextColor(getResources().getColor(R.color.white));
            getVerificationCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            getVerificationCode.setClickable(false);
            getVerificationCode.setTextColor(getResources().getColor(R.color.text_login_gray));
            getVerificationCode.setText(String.format("(%d)重新获取", millisUntilFinished / 1000));
        }
    }
}
