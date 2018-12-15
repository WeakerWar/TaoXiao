package com.example.taoxiao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;

public class PublishDetailActivity extends AppCompatActivity implements View.OnClickListener {
    /*标题初始化*/
    private TextView title_text = (TextView)findViewById(R.id.title_text);
    private Button bt_back = (Button)findViewById(R.id.bt_back);
    private Button bt_edit = (Button)findViewById(R.id.bt_edit);

    private String title,description;
    private String price, userid;
    private TextView tv_title, tv_description, tv_userid;
    private Button bt_call;
    @BindView(R.id.pu_fragment)
    FrameLayout pu_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_detail);

        /*初始化标题*/
        getActionBar().hide();
        title_text.setText("我的发布");
        bt_edit.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        price = intent.getStringExtra("price");
        userid = intent.getStringExtra("userid");

        tv_title = (TextView)findViewById(R.id.title);
        tv_description = (TextView)findViewById(R.id.description);
        tv_userid = (TextView)findViewById(R.id.publishId);
        bt_call = (Button)findViewById(R.id.call);

        tv_title.setText(title);
        tv_description.setText(description);
        tv_userid.setText(userid);

        bt_call.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
