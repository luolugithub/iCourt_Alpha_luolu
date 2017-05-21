package com.icourt.alpha.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.FileImportContactFragment;
import com.icourt.alpha.fragment.FileImportNavFragment;
import com.icourt.alpha.fragment.FileImportProjectFragment;
import com.icourt.alpha.fragment.FileImportTeamFragment;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.alpha.view.NoScrollViewPager;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Description  文件分享到alpha
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/20
 * version 1.0.0
 */
public class ImportFile2AlphaActivity extends BaseActivity
        implements OnPageFragmentCallBack {

    BaseFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.title_cancel)
    TextView titleCancel;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;
    @BindView(R.id.titleAction)
    TextView titleAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_file_to_alpha);
        ButterKnife.bind(this);
        initView();
    }

    private String getFilePath() {
        return UriUtils.getPath(getContext(),getIntent().getData());
    }

    @Override
    protected void initView() {
        super.initView();
        Uri fileUir = getIntent().getData();
        log("-------->share file uri:" + fileUir);
        if (!isUserLogin()) {
            LoginSelectActivity.launch(getContext());
            finish();
        }
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    titleContent.setText("分享到Alpha");
                    titleBack.setVisibility(View.GONE);
                    titleCancel.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    titleContent.setText("享聊");
                    titleBack.setVisibility(View.VISIBLE);
                    titleCancel.setVisibility(View.GONE);
                } else {
                    titleContent.setText("项目");
                    titleBack.setVisibility(View.VISIBLE);
                    titleCancel.setVisibility(View.GONE);
                }
            }
        });
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager()));
        baseFragmentAdapter.bindData(true, Arrays.asList(FileImportNavFragment.newInstance(getFilePath()),
                FileImportContactFragment.newInstance(getFilePath(), true),
                FileImportTeamFragment.newInstance(getFilePath()),
                FileImportProjectFragment.newInstance(getFilePath())));
    }

    @OnClick({R.id.title_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                viewPager.setCurrentItem(0);
                break;
            case R.id.title_cancel:
                finish();
                break;
            case R.id.titleAction:
                switch (viewPager.getCurrentItem()) {
                    case 1:
                        FileImportContactFragment item = (FileImportContactFragment) baseFragmentAdapter.getItem(viewPager.getCurrentItem());
                        item.notifyFragmentUpdate(item, FileImportContactFragment.TYPE_UPLOAD, null);
                        break;
                    case 2:
                        FileImportTeamFragment item2 = (FileImportTeamFragment) baseFragmentAdapter.getItem(viewPager.getCurrentItem());
                        item2.notifyFragmentUpdate(item2, FileImportContactFragment.TYPE_UPLOAD, null);
                        break;
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onRequest2NextPage(Fragment fragment, int type, Bundle bundle) {
        if (fragment instanceof FileImportContactFragment) {
            viewPager.setCurrentItem(2);
        } else if (fragment instanceof FileImportNavFragment) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onRequest2LastPage(Fragment fragment, int type, Bundle bundle) {

    }

    @Override
    public void onRequest2Page(Fragment fragment, int type, int pagePos, Bundle bundle) {
        if (pagePos >= 0 && pagePos < baseFragmentAdapter.getCount()) {
            viewPager.setCurrentItem(pagePos);
        }
    }

    @Override
    public boolean canGoNextFragment(Fragment fragment) {
        return baseFragmentAdapter.getFragmentsList()
                .indexOf(fragment)
                < baseFragmentAdapter.getCount();
    }

    @Override
    public boolean canGoLastFragment(Fragment fragment) {
        return baseFragmentAdapter.getFragmentsList()
                .indexOf(fragment)
                > 0;
    }

}
