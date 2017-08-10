package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderDocumentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.fragment.dialogfragment.DocumentDetailDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/10
 * version 2.1.0
 */
public class FolderListActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    FolderDocumentAdapter folderDocumentAdapter;

    public static void launch(@NonNull Context context,
                              String documentRootId,
                              String title,
                              String dirPath) {
        if (context == null) return;
        Intent intent = new Intent(context, FolderListActivity.class);
        intent.putExtra("documentRootId", documentRootId);
        intent.putExtra("title", title);
        intent.putExtra("dirPath", dirPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(getIntent().getStringExtra("title"));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(folderDocumentAdapter = new FolderDocumentAdapter());
        folderDocumentAdapter.setOnItemClickListener(this);
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }
        });
        refreshLayout.startRefresh();
    }

    private String getDocumentRootId() {
        return getIntent().getStringExtra("documentRootId");
    }

    private String getDocumentDirPath() {
        return getIntent().getStringExtra("dirPath");
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getSfileToken(new SimpleCallBack2<SFileTokenEntity<String>>() {
            @Override
            public void onSuccess(Call<SFileTokenEntity<String>> call, Response<SFileTokenEntity<String>> response) {
                if (TextUtils.isEmpty(response.body().authToken)) {
                    stopRefresh();
                    showTopSnackBar("sfile authToken返回为null");
                    return;
                }
                getFolder(isRefresh, response.body().authToken);
            }
        });

    }

    private void getFolder(final boolean isRefresh, String sfileToken) {
        getSFileApi().documentDirQuery(
                String.format("Token %s", sfileToken),
                getDocumentRootId(),
                getDocumentDirPath())
                .enqueue(new SimpleCallBack2<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        folderDocumentAdapter.bindData(isRefresh, response.body());
                        stopRefresh();
                    }

                    @Override
                    public void onFailure(Call<List<FolderDocumentEntity>> call, Throwable t) {
                        super.onFailure(call, t);
                        stopRefresh();
                    }
                });
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    /**
     * 获取sfile token
     *
     * @param callBack2
     */
    public void getSfileToken(@NonNull SimpleCallBack2<SFileTokenEntity<String>> callBack2) {
        getApi().documentTokenQuery()
                .enqueue(callBack2);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        FolderDocumentEntity item = folderDocumentAdapter.getItem(position);
        if (item == null) return;
        if (item.isDir()) {
            FolderListActivity.launch(getContext(),
                    getDocumentRootId(),
                    item.name,
                    String.format("%s/%s", getDocumentDirPath(), item.name));
        } else {
            DocumentDetailDialogFragment.show("",
                    getSupportFragmentManager());
        }
    }
}
