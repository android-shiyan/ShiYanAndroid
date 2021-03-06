package com.shiyan.android.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.shiyan.android.R;
import com.shiyan.android.basemodule.enumeration.DownLoadEnum;
import com.shiyan.android.basemodule.permission.PermissionListener;
import com.shiyan.android.basemodule.permission.Permissions;
import com.shiyan.android.basemodule.util.DownLoadUtil;
import com.shiyan.android.basemodule.util.PhotoUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * 权限申请下载测试
 * shiyan
 * update 2019.01.17
 */
public class RxPermissionActivity extends AppCompatActivity {

    private Button btn_click;

    private String apkUrl = "https://airspace-test.oss-cn-beijing.aliyuncs.com/201700003/1543924658539%E7%A9%BA%E7%BD%91%E6%B5%8B%E8%AF%95.apk";

    private ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_permission);


        btn_click = findViewById(R.id.btn_click);

        btn_click.setOnClickListener(v -> {

            new Permissions(RxPermissionActivity.this).request(new PermissionListener() {
                @Override
                public void granted() {

                    PhotoUtil.getInstance().takeAlbumn(RxPermissionActivity.this);

                }
            },Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        });

        iv_image = findViewById(R.id.iv_image);

    }

    private void downloadApk() {
        new Permissions(RxPermissionActivity.this).request(new PermissionListener() {
            @Override
            public void granted() {

                String apkPath = Environment.getExternalStorageDirectory() + "/test.apk";

                DownLoadUtil.getInstance().downLoad(RxPermissionActivity.this, apkUrl, "中国空网", "空域申请APP", apkPath, file -> Log.e("onDownLoadFinish","完成"));

                Observable.interval(1000,TimeUnit.MILLISECONDS)
                        .subscribe(aLong -> {

                            String progress = DownLoadUtil.getInstance().query(RxPermissionActivity.this, DownLoadEnum.PERCENT);

                            String downloadSize = DownLoadUtil.getInstance().query(RxPermissionActivity.this, DownLoadEnum.DOWNLOADSIZE);

                            String totalSize = DownLoadUtil.getInstance().query(RxPermissionActivity.this, DownLoadEnum.TOTALSIZE);

                            Log.e("progress",progress);

                            Log.e("downloadSize",downloadSize);

                            Log.e("totalSize",totalSize);
                        });
            }
        },Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PhotoUtil.getInstance().onActivityResult(this, requestCode, resultCode, data, file -> {

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            iv_image.setImageBitmap(bitmap);

        });
    }
}
