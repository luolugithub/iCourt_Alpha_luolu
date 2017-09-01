package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Description  图片查看器 区别于娱乐模式的查看
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/1
 * version 2.1.0
 */
public class ImageViewerActivity extends BaseActivity {

    private static final String KEY_BIG_URLS = "key_big_urls";//多个大图地址
    private static final String KEY_SMALL_URLS = "key_small_urls";//多个小图地址
    private static final String KEY_SELECT_POS = "key_select_pos";//多个图片地址 跳转到某一条

    public static void launch(@NonNull Context context,
                              ArrayList<String> smallUrls,
                              ArrayList<String> bigUrls,
                              int selectPos) {
        if (context == null) return;
        if (smallUrls == null || smallUrls.isEmpty()) return;
        if (selectPos < 0) {
            selectPos = 0;
        } else if (selectPos >= smallUrls.size()) {
            selectPos = bigUrls.size() - 1;
        }
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putStringArrayListExtra(KEY_SMALL_URLS, smallUrls);
        intent.putStringArrayListExtra(KEY_BIG_URLS, bigUrls);
        intent.putExtra(KEY_SELECT_POS, selectPos);
        context.startActivity(intent);
    }

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    ArrayList<String> bigUrls;
    ArrayList<String> smallUrls;
    int selectPos;
    ImagePagerAdapter imagePagerAdapter;
    Handler mHandler = new Handler();

    /**
     * 图片查看适配器
     */
    class ImagePagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = View.inflate(container.getContext(), R.layout.adapter_item_image_pager, null);
            container.addView(itemView);
            final ImageView touchImageView = itemView.findViewById(R.id.imageView);
            GlideUtils.loadSFilePic(getContext(), smallUrls.get(position), touchImageView);
            return itemView;
        }

        @Override
        public int getCount() {
            return smallUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        smallUrls = getIntent().getStringArrayListExtra(KEY_SMALL_URLS);
        bigUrls = getIntent().getStringArrayListExtra(KEY_BIG_URLS);
        selectPos = getIntent().getIntExtra(KEY_SELECT_POS, 0);
        viewPager.setAdapter(imagePagerAdapter = new ImagePagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setTitle(FileUtils.getFileName(smallUrls.get(position)));
            }
        });
        if (selectPos < imagePagerAdapter.getCount()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(selectPos, true);
                }
            }, 2_00);
        }
        setTitle(FileUtils.getFileName(smallUrls.get(0)));
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
