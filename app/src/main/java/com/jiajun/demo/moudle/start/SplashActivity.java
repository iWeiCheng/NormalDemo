package com.jiajun.demo.moudle.start;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.jiajun.demo.moudle.MainActivity;
import com.jiajun.demo.R;
import com.jiajun.demo.config.Const;
import com.jiajun.demo.database.TestDao;
import com.jiajun.demo.model.entities.TestEntity;
import com.jiajun.demo.moudle.main.MainsActivity;
import com.jiajun.demo.moudle.start.welcome.WelcomeActivity;
import com.jiajun.demo.util.SPUtils;
import com.jiajun.demo.util.StateBarTranslucentUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends AppCompatActivity  {

    private KenBurnsView mKenBurns;
    private ImageView mLogo;
    private TextView welcomeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // 判断是否是第一次开启应用
        boolean isFirstOpen = (boolean) SPUtils.get(this, Const.FIRST_OPEN, false);
        Logger.e("isFirstOpen:" + isFirstOpen);
//        isFirstOpen = true;
        if(isFirstOpen){
            SplashActivityPermissionsDispatcher.startWelcomeGuideActivityWithCheck(this);
//            Intent intent = new Intent(this, WelcomeActivity.class);
//            startActivity(intent);
//            finish();
        }else{
            initContentView();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                   startHomeActivity();
                }
            }, 2000);
        }
        SPUtils.put(this, Const.FIRST_OPEN, false);
    }

    //=======================动态权限的申请===========================================================<
    @NeedsPermission({Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            /*Manifest.permission.WRITE_CONTACTS,*/
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startWelcomeGuideActivity() {
        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 为什么要获取这个权限给用户的说明
     *
     * @param request
     */
    @OnShowRationale({Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            /*Manifest.permission.WRITE_CONTACTS,*/
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("有部分权限需要你的授权")
//                .setPositiveButton(R.string.imtrue, (dialog, button) -> request.proceed())
//                .setNegativeButton(R.string.cancel, (dialog, button) -> request.cancel())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show()
        ;
    }

    /**
     * 如果用户不授予权限调用的方法
     */
    @OnPermissionDenied({Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            /*Manifest.permission.WRITE_CONTACTS,*/
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showDeniedForCamera() {
        showPermissionDenied();
    }

    public void showPermissionDenied() {
        new AlertDialog.Builder(this)
                .setTitle("权限说明")
                .setCancelable(false)
                .setMessage("本应用需要部分必要的权限，如果不授予可能会影响正常使用！")
                .setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("赋予权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SplashActivityPermissionsDispatcher.startWelcomeGuideActivityWithCheck(SplashActivity.this);
                    }
                })
                .create().show();
    }

    /**
     * 如果用户选择了让设备“不再询问”，而调用的方法
     */
    @OnNeverAskAgain({Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            /*Manifest.permission.WRITE_CONTACTS,*/
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showNeverAskForCamera() {
        Toast.makeText(this, "不再询问授权权限！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //=======================动态权限的申请===========================================================>


    public void initContentView() {
        setContentView(R.layout.activity_splash);
        //设置状态栏透明
        StateBarTranslucentUtils.setStateBarTranslucent(this);
        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mLogo = ((ImageView) findViewById(R.id.logo_splash));
        welcomeText = ((TextView) findViewById(R.id.welcome_text));

        Glide.with(this)
                .load(R.drawable.welcometoqbox)
                .into(mKenBurns);

        animation2();
        animation3();
        saveTestData();
    }

    private void saveTestData() {
        TestDao testDao = new TestDao(getApplicationContext());
        testDao.deleteAllTestEntity();
        String[] strs = getResources().getStringArray(R.array.arrays_address);
        for (int i = 0; i < 10; i++) {
            testDao.insertTestEntities(new TestEntity(i, strs[i]));
        }
        List<TestEntity> testEntities = testDao.queryTestEntityList();
        Logger.e(testEntities.toString());
    }


    Animation anim;

    private void animation2() {
        mLogo.setAlpha(1.0F);
        anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

    ObjectAnimator alphaAnimation;

    private void animation3() {
        alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(1700);
        alphaAnimation.setDuration(500);
        alphaAnimation.start();
    }

    public void startHomeActivity() {
        startActivity(new Intent(this, MainsActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mKenBurns != null) {
            mKenBurns.pause();
        }
        if (alphaAnimation != null) {
            alphaAnimation.cancel();
        }
        if (anim != null) {
            anim.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mKenBurns != null) {
            mKenBurns.resume();
        }
    }
}
