package com.dup.tdup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloadActivity extends Activity {
    private static String TAG = "Photo";
    TextView textView;
    ImageView imageView;
    Context context;

    ProgressBar progressBar;
    private File file, dir;
    private String savePath= "ImageTemp";
    private String FileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_download);
        context = this.getBaseContext();

        MakePhtoDir();

        imageView = (ImageView) findViewById(R.id.DNImageView);

        Button photo_download = (Button) findViewById(R.id.btn_photodownload);
        photo_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imgUrl = "https://i0.codibook.net/files/thumb/big/197711113230/c41e599580609462/1403096374.png";
                FileName = imgUrl.substring( imgUrl.lastIndexOf('/')+1, imgUrl.length() );
                DownloadPhotoFromURL downloadPhotoFromURL = new DownloadPhotoFromURL();
                // 동일한 파일이 있는지 검사
                if(new File(dir.getPath() + File.separator + FileName).exists() == false){
                    downloadPhotoFromURL.execute(imgUrl,FileName);
                } else {
                    Toast.makeText(context, "파일이 이미 존재합니다", Toast.LENGTH_SHORT).show();

                    File file = new File(dir + "/" + FileName);
                    Bitmap photoBitmap = BitmapFactory.decodeFile(file.getAbsolutePath() );
                    imageView.setImageBitmap(photoBitmap);
                }
            }
        });
    }

    private void MakePhtoDir(){
        //savePath = "/Android/data/" + getPackageName();
        //dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), savePath);
        dir = new File(Environment.getExternalStorageDirectory(), savePath );
        if (!dir.exists())
            dir.mkdirs(); // make dir
    }

    public String getRealPathFromURI(Uri contentUri) {
        // 갤러리 이미지 파일의 실제 경로 구하기
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    class DownloadPhotoFromURL extends AsyncTask<String, Integer, String> {
        int count;
        int lenghtOfFile = 0;
        InputStream input = null;
        OutputStream output = null;
        String tempFileName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setProgress(0);
        }

        @Override
        protected String doInBackground(String... params) {
            tempFileName = params[1];
            file = new File(dir, params[1]); // 다운로드할 파일명
            try {
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                lenghtOfFile = connection.getContentLength(); // 파일 크기를 가져옴

//                if (file.exists()) {
//                    file.delete();
//                    Log.d(TAG, "file deleted...");
//                }

                input = new BufferedInputStream(url.openStream());
                output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return String.valueOf(-1);
                    }
                    total = total + count;
                    if (lenghtOfFile > 0) { // 파일 총 크기가 0 보다 크면
                        publishProgress((int) (total * 100 / lenghtOfFile));
                    }
                    output.write(data, 0, count); // 파일에 데이터를 기록
                }

                output.flush();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    }
                    catch(IOException ioex) {
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    }
                    catch(IOException ioex) {
                    }
                }
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // 백그라운드 작업의 진행상태를 표시하기 위해서 호출하는 메소드
            //progressBar.setProgress(progress[0]);
            //textView.setText("다운로드 : " + progress[0] + "%");
        }

        protected void onPostExecute(String result) {
            // pdLoading.dismiss();
            if (result == null) {
                Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다.", Toast.LENGTH_LONG).show();

                File file = new File(dir + "/" + tempFileName);
                //이미지 스캔해서 갤러리 업데이트
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Bitmap photoBitmap = BitmapFactory.decodeFile(file.getAbsolutePath() );
                imageView.setImageBitmap(photoBitmap);
            } else {
                Toast.makeText(getApplicationContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
            }
        }
    }
}

