package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.fragment.FileImportContactFragment;
import com.icourt.alpha.fragment.FileImportNavFragment;
import com.icourt.alpha.fragment.FileImportTeamFragment;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.alpha.view.NoScrollViewPager;

import java.net.URLDecoder;
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
    private static final String CLOSE_ACTION = "close_action";//关闭当前页面
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

    public static void lauchClose(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, ImportFile2AlphaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(CLOSE_ACTION);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String action = intent.getAction();
            if (TextUtils.equals(action, CLOSE_ACTION)) {
                finish();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            if (TextUtils.equals(getIntent().getAction(), CLOSE_ACTION)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_import_file_to_alpha);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        String action = getIntent().getAction();
        String type = getIntent().getType();
        Uri fileUir = null;
        if (TextUtils.equals(action, Intent.ACTION_SEND)) {//分享
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (getIntent().getClipData() != null) {
                    if (getIntent().getClipData().getItemCount() > 0) {
                        fileUir = getIntent().getClipData().getItemAt(0).getUri();
                    }
                }
            } else {
                fileUir = getIntent().getData();
            }
        } else {//打开方式
            fileUir = getIntent().getData();
        }
        String extraSubject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
        String extraText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        String extraStream = getIntent().getStringExtra(Intent.EXTRA_STREAM);
        if (getIntent().getExtras() != null) {
            Object extraStreamp = getIntent().getExtras().get(Intent.EXTRA_STREAM);
            //可能file uri 为null 比如魅族手机 从extraStreamp获取
            if (fileUir == null
                    && extraStreamp != null) {
                try {
                    fileUir = Uri.parse(extraStreamp.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        log("-------->share action:" + action);
        log("-------->share type:" + type);
        log("-------->share uri:" + fileUir);
        log("-------->share extraSubject:" + extraSubject);
        log("-------->share extraText:" + extraText);
        log("-------->share extraStream:" + extraStream);

        LogUtils.logBundle(getIntent().getExtras());
        if (!isUserLogin()) {
            LoginSelectActivity.launch(getContext());
            finish();
        }
        titleAction.setVisibility(View.GONE);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    titleContent.setText("分享到Alpha");
                    titleBack.setVisibility(View.GONE);
                    titleCancel.setVisibility(View.VISIBLE);
                    titleAction.setVisibility(View.GONE);
                } else {
                    titleContent.setText("享聊");
                    titleBack.setVisibility(View.VISIBLE);
                    titleCancel.setVisibility(View.GONE);
                    titleAction.setVisibility(View.VISIBLE);
                }
            }
        });

        String path = null;
        String desc = null;
        if (TextUtils.equals(type, "text/plain"))//网页
        {
            //可能是.txt 带中文路径的
            if (fileUir != null) {
                try {
                    String decodePath = URLDecoder.decode(fileUir.toString(), "utf-8");
                    Uri parse = Uri.parse(decodePath);
                    path = UriUtils.getPath(getContext(), parse);
                } catch (Exception e) {
                    e.printStackTrace();
                    path = UriUtils.getPath(getContext(), fileUir);
                }
            } else {
                path = extraText;
            }
            desc = extraSubject;
        } else {//文件
            path = UriUtils.getPath(getContext(), fileUir);
        }
        //fileProvider
        if (TextUtils.isEmpty(path) && fileUir != null) {
            path = fileUir.toString();
            try {
                path = URLDecoder.decode(fileUir.toString(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                path = fileUir.toString();
            }
        }
        if (TextUtils.isEmpty(path)) {
            showToast("文件地址为空");
            bugSync("分享到Alpha失败", "fileUir:" + fileUir);
        }
        log("----------->path:" + path);
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager()));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(
                        FileImportNavFragment.newInstance(path, desc),
                        FileImportContactFragment.newInstance(path, desc, true),
                        FileImportTeamFragment.newInstance(path, desc)));
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
