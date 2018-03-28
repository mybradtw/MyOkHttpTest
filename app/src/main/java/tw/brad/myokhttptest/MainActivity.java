package tw.brad.myokhttptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// OkHttp
public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private TextView mesg;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
        }else{
            finish();
        }
    }

    private void init(){
        mesg = findViewById(R.id.mesg);
        img = findViewById(R.id.img);
    }

    // OkHttp 基本 Get 實作
    public void test1(View view) {

        new Thread(){
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("https://www.bradchao.com")
                        .build();

                try {
                    Response response =  client.newCall(request).execute();
                    Log.v("brad", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    // OkHttp Callback 機制實作
    public void test2(View view) {
        Request request = new Request.Builder()
                .url("https://www.bradchao.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final  String ret = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mesg.setText(ret);
                    }
                });

            }
        });

    }

    public void test3(View view) {
        new GetImageTask().execute();
    }


    // OkHttp 取得影像資料
    private class GetImageTask extends AsyncTask<Void,Void,Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            Request request = new Request.Builder()
                    .url("https://i2.wp.com/www.bradchao.com/wp-content/uploads/2018/01/IMG_20180108_113633-3.jpg?w=1024&ssl=1")
                    .build();

            try {
                Response response =  client.newCall(request).execute();

                Bitmap ret = BitmapFactory.decodeStream(response.body().byteStream());
                return ret;
            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            img.setImageBitmap(bitmap);
        }
    }


    public void test4(View view) {
        new Thread(){
            @Override
            public void run() {
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("account", "brad");
                builder.add("passwd", "7654321");
                FormBody formBody = builder.build();

                Request request = new Request.Builder()
                        .url("https://www.bradchao.com/iii/brad02.php")
                        .post(formBody)
                        .build();

                try {
                    Response response =  client.newCall(request).execute();
                    Log.v("brad", response.body().string());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void test5(View view) {
        new Thread(){
            @Override
            public void run() {
                final MediaType MEDIA_TYPE_PDF = MediaType.parse("application/pdf");
                File pdfFile = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS),
                        "www.bradchao.com.pdf");

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("upload", "brad7.pdf",
                                RequestBody.create(MEDIA_TYPE_PDF,pdfFile))
                        .build();

                Request request = new Request.Builder()
                        .url("https://www.bradchao.com/iii/brad03.php")
                        .post(requestBody)
                        .build();

                try {
                    Response response =  client.newCall(request).execute();
                    if (response.isSuccessful()){
                        Log.v("brad", "Upload OK");
                    }else{
                        Log.v("brad", "Upload XX");

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }
}










