package com.example.a51167.mywork.Navigation_Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.a51167.mywork.Adapter.MyPagerAdapter;
import com.example.a51167.mywork.R;
import com.example.a51167.mywork.rent;
import com.example.a51167.mywork.search;
import com.example.a51167.mywork.want;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnTouchListener{
    public static final int VIEW_PAGER_DELAY = 2000;
    private MyPagerAdapter mAdapter;
    private List<ImageView> mItems;
    private ImageView[] mBottomImages;
    private LinearLayout mBottomLiner;
    private ViewPager mViewPager;

    private int currentViewPagerItem;
    //是否自动播放
    private boolean isAutoPlay;

    private MyHandler mHandler;
    private Thread mThread;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_fragment,null);
        return view;
    }
    public void onActivityCreated(Bundle savedInstanceState) {

        mHandler = new MyHandler(this);
        //配置轮播图ViewPager
        mViewPager = ((ViewPager) getActivity().findViewById(R.id.live_view_pager));
        mItems = new ArrayList<>();
        mAdapter = new MyPagerAdapter(mItems, getActivity());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnTouchListener(this);
        mViewPager.addOnPageChangeListener(this);
        isAutoPlay = true;

        //TODO: 添加ImageView
        addImageView();
        mAdapter.notifyDataSetChanged();
        //设置底部4个小点
        setBottomIndicator();

        super.onActivityCreated(savedInstanceState);
        Button b2 = (Button) getActivity().findViewById(R.id.B2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),search.class);
                startActivity(intent);
            }
        });

        Button b4 = (Button) getActivity().findViewById(R.id.B4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),rent.class);
                startActivity(intent);
            }
        });

        Button b3 = (Button) getActivity().findViewById(R.id.B3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),want.class);
                startActivity(intent);
            }
        });
    }
    private void addImageView(){
        ImageView view0 = new ImageView(getActivity());
        view0.setImageResource(R.mipmap.pic1);
        ImageView view1 = new ImageView(getActivity());
        view1.setImageResource(R.mipmap.pic2);
        ImageView view2 = new ImageView(getActivity());
        view2.setImageResource(R.mipmap.pic3);

        view0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view2.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mItems.add(view0);
        mItems.add(view1);
        mItems.add(view2);

    }
    private void setBottomIndicator() {
        //获取指示器(下面三个小点)
        mBottomLiner = (LinearLayout) getActivity().findViewById(R.id.live_indicator);
        //右下方小圆点
        mBottomImages = new ImageView[mItems.size()];
        for (int i = 0; i < mBottomImages.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(params);
            //如果当前是第一个 设置为选中状态
            if (i == 0) {
                imageView.setImageResource(R.drawable.indicator_select);
            } else {
                imageView.setImageResource(R.drawable.indicator_no_select);
            }
            mBottomImages[i] = imageView;
            //添加到父容器
            mBottomLiner.addView(imageView);
        }

        //让其在最大值的中间开始滑动, 一定要在 mBottomImages初始化之前完成
        int mid = MyPagerAdapter.MAX_SCROLL_VALUE / 2;
        mViewPager.setCurrentItem(mid);
        currentViewPagerItem = mid;

        //定时发送消息
        mThread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    mHandler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(HomeFragment.VIEW_PAGER_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        mThread.start();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

        currentViewPagerItem = position;
        if (mItems != null) {
            position %= mBottomImages.length;
            int total = mBottomImages.length;

            for (int i = 0; i < total; i++) {
                if (i == position) {
                    mBottomImages[i].setImageResource(R.drawable.indicator_select);
                } else {
                    mBottomImages[i].setImageResource(R.drawable.indicator_no_select);
                }
            }
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isAutoPlay = false;
                break;
            case MotionEvent.ACTION_UP:
                isAutoPlay = true;
                break;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    // 为防止内存泄漏, 声明自己的Handler并弱引用Activity
    ///////////////////////////////////////////////////////////////////////////
    private static class MyHandler extends Handler {
        private WeakReference<HomeFragment> mWeakReference;

        public MyHandler(HomeFragment fragment) {
            mWeakReference = new WeakReference<HomeFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    HomeFragment activity = mWeakReference.get();
                    if (activity.isAutoPlay) {

                        activity.mViewPager.setCurrentItem(++activity.currentViewPagerItem);
                    }

                    break;
            }

        }
    }
}
