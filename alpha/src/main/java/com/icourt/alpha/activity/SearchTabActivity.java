package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.SearchEngineEntity;
import com.icourt.alpha.fragment.SearchWebViewFragment;
import com.icourt.alpha.interfaces.INotifyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/20
 * version 1.0.0
 */
public class SearchTabActivity extends BaseActivity {

    private static final String KEY_ENGINES = "searchEngineEntities";
    private static final String KEY_WORD = "key_word";
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    private BaseFragmentAdapter baseFragmentAdapter;

    public static void launch(@NonNull Context context,
                              @NonNull String keyWord,
                              @NonNull ArrayList<SearchEngineEntity> searchEngineEntities) {
        if (context == null) return;
        if (TextUtils.isEmpty(keyWord)) return;
        if (searchEngineEntities == null) return;
        if (searchEngineEntities.isEmpty()) return;
        Intent intent = new Intent(context, SearchTabActivity.class);
        intent.putExtra(KEY_ENGINES, searchEngineEntities);
        intent.putExtra(KEY_WORD, keyWord);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tab);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("智能搜索");
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.browser_open);
        }
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorHeight(0);

        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager()));
        ArrayList<SearchEngineEntity> searchEngineEntities =
                (ArrayList<SearchEngineEntity>) getIntent().getSerializableExtra(KEY_ENGINES);
        String keyWord = getIntent().getStringExtra(KEY_WORD);
        List<String> titles = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();
        for (SearchEngineEntity searchEngineEntity : searchEngineEntities) {
            if (searchEngineEntity != null) {
                titles.add(searchEngineEntity.name);
                fragments.add(
                        SearchWebViewFragment.newInstance(
                                TextUtils.isEmpty(searchEngineEntity.site)
                                        ? "" : searchEngineEntity.site.replace("iCourt", keyWord)));
            }
        }

        baseFragmentAdapter.bindTitle(true, titles);
        baseFragmentAdapter.bindData(true, fragments);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < baseFragmentAdapter.getCount(); i++) {
            TabLayout.Tab itemTab = tabLayout.getTabAt(i);
            if (itemTab != null) {
                itemTab.setCustomView(R.layout.item_tab_layout_custom);
                TextView itemTv = (TextView) itemTab.getCustomView().findViewById(R.id.item_text);
                itemTv.setText(baseFragmentAdapter.getPageTitle(i));
                if (i == 0) {
                    itemTab.select();
                }
            }
        }
        //要求提前加载三页
       /* if (baseFragmentAdapter.getCount() >= 3) {
            viewPager.setOffscreenPageLimit(3);
        }*/
    }

    @OnClick({R.id.bottom_back_iv,
            R.id.bottom_forward_iv,
            R.id.bottom_refresh_iv,
            R.id.bottom_share_iv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_back_iv:
                updateCurrentFragment(1);
                break;
            case R.id.bottom_forward_iv:
                updateCurrentFragment(2);
                break;
            case R.id.bottom_refresh_iv:
                updateCurrentFragment(3);
                break;
            case R.id.bottom_share_iv:
                updateCurrentFragment(4);
                break;
            case R.id.titleAction:
                openWithOtherApp();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 用其它app打开网页
     */
    private void openWithOtherApp() {
        Fragment item = baseFragmentAdapter.getItem(viewPager.getCurrentItem());
        if (item != null && item.getArguments() != null) {
            String url = item.getArguments().getString("url", "");
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
        }
    }

    /**
     * @param action 参考{@link SearchWebViewFragment }
     */
    private void updateCurrentFragment(int action) {
        Fragment item = baseFragmentAdapter.getItem(viewPager.getCurrentItem());
        if (item instanceof INotifyFragment) {
            ((INotifyFragment) item).notifyFragmentUpdate(item, action, null);
        }
    }
}
