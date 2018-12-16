package com.example.taoxiao;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taoxiao.bin.user;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_back, bt_finish, bt_edit;
    private EditText ed_tel;
    private String status, tel;

    private ImageView imageView;
    private static final int CHOOSE_PHOTO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        bt_back = (Button)findViewById(R.id.bt_back);
        bt_finish = (Button)findViewById(R.id.finish);
        bt_edit = (Button)findViewById(R.id.bt_edit);
        ed_tel = (EditText)findViewById(R.id.tel);

        imageView = (ImageView)findViewById(R.id.icon);
        imageView.setOnClickListener(this);

        bt_finish.setOnClickListener(this);
        bt_back.setOnClickListener(this);
        bt_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        tel = ed_tel.getText().toString();
        if(v.getId() == R.id.bt_back){
            finish();
        }else if(v.getId() == R.id.bt_edit){
            bt_edit.setVisibility(View.INVISIBLE);      //编辑按钮 不可见
            bt_finish.setVisibility(View.VISIBLE);          //完成按钮 可见
        }else if(v.getId() == R.id.icon) {
            if(ContextCompat.checkSelfPermission(EditActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }else{
                openAlbum();
            }
        }else {
            sendRequestWithHttpURLConnection();

                /*等待*/
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if("true".equals(status)){
                Toast.makeText(EditActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(EditActivity.this, "保存失败，请重试！", Toast.LENGTH_SHORT).show();
            }
        }
    }


            /*发送更改信息请求*/
    public void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable() {
            @Override

            public void run(){
                try{

                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()    /*发送账号密码*/
                            .add("userid",user.username)
                            .add("tel",tel)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://172.24.7.31:8080/user/updateuser")  /*登录url  http://localhost:8080/user/updateuser   */
                            .post(requestBody)
                            .build();
                    Response response = null;
                    String responseData = null;

                    response = client.newCall(request).execute();
                    responseData = response.body().string();
                    Log.d("LoginActivity","responseData is " + responseData);

                    parseJSONWithJSONObject(responseData);              /*解析Json数据*/

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            status = jsonObject.getString("status");

            Log.d("EditActivity","status is " + status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO); //打开相册
        Toast.makeText(this, "You open the album",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this, "You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Toast.makeText(this, "You get it",Toast.LENGTH_SHORT).show();
        switch (requestCode){
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                    }
                    else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
             default:
                 break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.download.documents".equals(uri.getAuthority())) {
                Uri contenUri = ContentUris.withAppendedId(Uri.parse("content://download/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contenUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"faile to get image",Toast.LENGTH_SHORT).show();
        }
    }
}
