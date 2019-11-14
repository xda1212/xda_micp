package com.micp.im;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.micp.im.ui.MainActivity;
import com.micp.im.utils.TLog;
import com.micp.im.utils.UIHelper;
import com.tuanmai.tools.Utils.PermissionUtil;
import com.tuanmai.tools.dialog.PermissionDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.jiguang.jmlinksdk.api.JMLinkAPI;
import cn.jpush.android.api.JPushInterface;

/**
 * 应用启动界面
 */
public class AppStart extends Activity {
    private PermissionDialog mPermissionDialog;//权限申请对话框
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAPP(this);

        JMLinkAPI.getInstance().registerWithAnnotation();
        Uri uri = getIntent().getData();
        if (uri != null) { //uri不为null，表示应用是从scheme拉起
            JMLinkAPI.getInstance().router(uri);
            finish();
        }

        JMLinkAPI.getInstance().registerWithAnnotation();
        JMLinkAPI.getInstance().deferredRouter();
        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }

        // SystemTool.gc(this); //针对性能好的手机使用，加快应用相应速度

        mRootView = View.inflate(this, R.layout.app_start, null);
        setContentView(mRootView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);

    }

    private void permissoionCheck() {
        List<String> pmList = new ArrayList<>();
        pmList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        pmList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!PermissionUtil.checkPermission(this, pmList)) {
            PermissionUtil.requestPermission(this, PermissionUtil.REQUEST_NECESSARY_PERMISSION, pmList);
        } else {
            redirectTo();
        }
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginSelectActivity(this);
            finish();
            return;
        }

        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(800);
        mRootView.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                permissoionCheck();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtil.REQUEST_NECESSARY_PERMISSION) {
            if (null != grantResults && grantResults.length > 0) {
                boolean isGranted = true;
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                        break;
                    }
                }
                if (!isGranted) {
                    if (null == mPermissionDialog) {
                        mPermissionDialog = new PermissionDialog(this, R.style.dialog, null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                    }
                    mPermissionDialog.show();
                } else {
                    redirectTo();
                }
            }
        }
    }

    private int checkAPP(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];

            int hashcode = sign.hashCode();
            Log.i("myhashcode", "hashCode : " + hashcode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
