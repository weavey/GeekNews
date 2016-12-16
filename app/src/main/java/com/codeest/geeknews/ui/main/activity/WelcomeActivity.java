package com.codeest.geeknews.ui.main.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codeest.geeknews.R;
import com.codeest.geeknews.base.BaseActivity;
import com.codeest.geeknews.component.ImageLoader;
import com.codeest.geeknews.model.bean.WelcomeBean;
import com.codeest.geeknews.presenter.WelcomePresenter;
import com.codeest.geeknews.presenter.contract.WelcomeContract;

import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static pub.devrel.easypermissions.AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE;

/**
 * Created by codeest on 16/8/15.
 */

public class WelcomeActivity extends BaseActivity<WelcomePresenter> implements WelcomeContract.View,  EasyPermissions.PermissionCallbacks {

    @BindView(R.id.iv_welcome_bg)
    ImageView ivWelcomeBg;
    @BindView(R.id.tv_welcome_author)
    TextView tvWelcomeAuthor;

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initEventAndData() {
        mPresenter.getWelcomeData();
    }

    @Override
    public void showContent(WelcomeBean welcomeBean) {
        ImageLoader.load(this, welcomeBean.getImg(), ivWelcomeBg);
        ivWelcomeBg.animate().scaleX(1.12f).scaleY(1.12f).setDuration(2000).setStartDelay(100).start();
        tvWelcomeAuthor.setText(welcomeBean.getText());
    }

    @Override
    public void jumpToMain() {
       requirePermissions();
    }

    @Override
    protected void onDestroy() {
        Glide.clear(ivWelcomeBg);
        super.onDestroy();
    }

    @Override
    public void showError(String msg) {

    }


    private void toMain() {
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private static final int RC_PERMISSIONS = 123;

    @SuppressLint("InlinedApi")
    private boolean hasPermissions() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);
    }


    @SuppressLint("InlinedApi")
    @AfterPermissionGranted(RC_PERMISSIONS)
    private void requirePermissions() {
        if (hasPermissions()) {
            toMain();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this,
                                               getString(R.string.rationale_permissions),
                                               RC_PERMISSIONS,
                                               Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                               Manifest.permission.READ_PHONE_STATE);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        toMain();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        permissionsDeniedOpenSetting();
    }


    private void permissionsDeniedOpenSetting() {
        if (!hasPermissions()) {
            new AppSettingsDialog.Builder(this, getString(R.string.app_settings_dialog_rationale_ask_again)).setTitle(getString(R.string.app_settings_dialog_title_settings_dialog))
                                                                                                            .setPositiveButton(getString(R.string.app_settings_dialog_setting))
                                                                                                            .setNegativeButton(getString(R.string.app_settings_dialog_cancel),
                                                                                                                               new DialogInterface.OnClickListener() {
                                                                                                                                   @Override
                                                                                                                                   public void onClick(DialogInterface dialogInterface, int i) {
                                                                                                                                       ActivityCompat.finishAffinity(WelcomeActivity.this);
                                                                                                                                   }
                                                                                                                               })
                                                                                                            .build()
                                                                                                            .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DEFAULT_SETTINGS_REQ_CODE:
                if (hasPermissions()) {
                    toMain();
                }
                permissionsDeniedOpenSetting();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
