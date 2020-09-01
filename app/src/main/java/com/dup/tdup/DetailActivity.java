package com.dup.tdup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity { //제품 상세정보 페이지

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final ImageView itemImg = (ImageView)findViewById(R.id.item_img);
        TextView itemName = (TextView)findViewById(R.id.item_name);
        TextView itemPrice = (TextView)findViewById(R.id.item_price);
        final Button siteButton = (Button) findViewById(R.id.item_site);
        final Button fitButton = (Button) findViewById(R.id.ar_fit_btn);

        final int PERMISSION_ALL = 1;
        final String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};


        final Intent intent = getIntent();

        //앞 페이지에서 선택한 옷의 상세정보 불러와서 띄워주기
        Glide.with(this)
                .asBitmap()
                .load(intent.getStringExtra("img"))
                .into(itemImg);
        itemName.setText(intent.getStringExtra("name"));
        itemPrice.setText(intent.getStringExtra("price"));

        //사이트 이동 버튼 눌렀을 때
        siteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent siteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ko.codibook.net"+intent.getStringExtra("itemID")));
                startActivity(siteIntent);
            }
        });

        //가상 피팅 버튼 눌렀을 때
        fitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap outfit_bmp = ((BitmapDrawable)itemImg.getDrawable()).getBitmap();
                DrawView.outfitImg = outfit_bmp;
                DrawView.outfitCategory = intent.getStringExtra("category");

                // 권한이 허용되어있지않다면 권한요청
                if(!PermissionManager.checkPermissions()){
                    Toast.makeText(getApplicationContext(), "설정에서 카메라 권한을 허용해 주세요", Toast.LENGTH_SHORT).show();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PermissionManager.checkpermissions();
                    }

                }
                // 권한이 허용되어있다면 다음 화면 진행
                else {
                    Intent fitIntent = new Intent(getApplicationContext(), FitPreviewActivity.class);
                    startActivity(fitIntent);
                }


            }
        });

    }
}