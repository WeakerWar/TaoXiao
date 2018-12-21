package com.example.a51167.mywork.Navigation_Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a51167.mywork.R;
import com.example.a51167.mywork.bin.user;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;

public class PublishFragment extends Fragment {
    public static  final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;
    private String status;
    private String partment,divide;
    private String description="";
    private String title="";
    private String price="";
    private EditText ed_title;
    private EditText ed_description;
    private EditText ed_price;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.publish_fragment,null);
        return view;
    }
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        Button edit=(Button)getActivity().findViewById(R.id.edit);
        ed_title = getActivity().findViewById(R.id.ptitle);
        ed_description= getActivity().findViewById(R.id.pdescription);
        ed_price = getActivity().findViewById(R.id.pprice);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = ed_title.getText().toString();
                description = ed_description.getText().toString();
                price = ed_price.getText().toString();
                sendRequestWithHttpURLConnection();
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                if("true".equals(status))
                Toast.makeText(getActivity(),"发布成功",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(),"发布失败",Toast.LENGTH_LONG).show();
            }
        });
        Spinner spinner=(Spinner) getActivity().findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                partment=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner spinner1=(Spinner) getActivity().findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                divide=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button takePhoto = (Button) getActivity().findViewById(R.id.Button1);
        picture = (ImageView) getActivity().findViewById(R.id.picture);
        takePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                File outputImage = new File(getActivity().getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(getActivity(),"com.example.a51167.fileprovider",outputImage);
                }else{
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
    private void sendRequestWithHttpURLConnection(){

        new Thread(new Runnable() {
            @Override

            public void run(){
                try{

                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()    /*发送账号密码*/
                            .add("userid",user.username)
                            .add("title",title)
                            .add("description",description)
                            .add("price", price)
                            .add("partment",partment)
                            .add("divide",divide)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://172.24.8.43:8080/user/entry")  /*登录url  http://172.24.7.31:8080/user/login   */
                            .post(requestBody)
                            .build();
                    Response response = null;
                    String responseData = null;

                    response = client.newCall(request).execute();
                    responseData = response.body().string();

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

