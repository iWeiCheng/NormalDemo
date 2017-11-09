package com.jiajun.demo.moudle.personal;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jiajun.demo.R;
import com.jiajun.demo.base.BaseCameraActivity;
import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人信息-测试选择相册 拍照
 * Created by dan on 2017/8/18.
 */

public class PersonalActivity extends BaseCameraActivity {

    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.tv_set_head)
    TextView tvSetHead;

    @Override
    protected void loadLayout() {
        setContentView(R.layout.activity_personal);
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void picSelectSuccess(String path) {
        Logger.e("pic:" + path);
        Glide.with(this).load(new File(path)).into(ivHead);
    }


    @OnClick(R.id.tv_set_head)
    public void onViewClicked() {
        openCamera("test",ivHead.getWidth(),ivHead.getHeight());
//        openAlbum(ivHead.getWidth(), ivHead.getHeight());
    }
}
