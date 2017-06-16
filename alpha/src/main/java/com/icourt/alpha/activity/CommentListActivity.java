package com.icourt.alpha.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.CommentListAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.CommentEntity;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.entity.event.TaskActionEvent;
import com.icourt.alpha.fragment.dialogfragment.ContactDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ActionConstants;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.view.xrefreshlayout.RefreshLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  评论列表页
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class CommentListActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemChildClickListener, BaseRecyclerAdapter.OnItemLongClickListener {

    private static final String KEY_TASK_ID = "key_task_id";
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.comment_edit)
    EditText commentEdit;
    @BindView(R.id.comment_tv)
    TextView commentTv;
    @BindView(R.id.send_tv)
    TextView sendTv;

    TaskEntity.TaskItemEntity taskItemEntity;
    int pageIndex, commentCount;
    CommentListAdapter commentListAdapter;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    HeaderFooterAdapter<CommentListAdapter> headerFooterAdapter;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;

    public static void launch(@NonNull Context context, @NonNull TaskEntity.TaskItemEntity taskItemEntity) {
        if (context == null) return;
        if (taskItemEntity == null) return;
        Intent intent = new Intent(context, CommentListActivity.class);
        intent.putExtra(KEY_TASK_ID, taskItemEntity);
        context.startActivity(intent);
    }

    public static void forResultLaunch(@NonNull Activity context, @NonNull TaskEntity.TaskItemEntity taskItemEntity, int requestCode) {
        if (context == null) return;
        if (taskItemEntity == null) return;
        Intent intent = new Intent(context, CommentListActivity.class);
        intent.putExtra(KEY_TASK_ID, taskItemEntity);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("查看评论");
        taskItemEntity = (TaskEntity.TaskItemEntity) getIntent().getSerializableExtra(KEY_TASK_ID);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        headerFooterAdapter = new HeaderFooterAdapter<>(commentListAdapter = new CommentListAdapter());
        View footerview = HeaderFooterAdapter.inflaterView(this, R.layout.footer_comment_list_layout, recyclerview);
        TextView contentTv = (TextView) footerview.findViewById(R.id.content_tv);
        if (taskItemEntity != null) {
            if (taskItemEntity.createUser != null)
                contentTv.setText(taskItemEntity.createUser.userName + " 创建了任务 " + DateUtils.getTimeDateFormatMm(taskItemEntity.createTime));
        }
        headerFooterAdapter.addFooter(footerview);

        refreshLayout.setNoticeEmpty(R.mipmap.icon_placeholder_task, R.string.task_no_comment_text);
        refreshLayout.setMoveForHorizontal(true);

        recyclerview.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(this, true));
        recyclerview.setHasFixedSize(true);
        commentListAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (getIntent() != null) {
                    Intent intent = getIntent();
                    int count = commentListAdapter.getItemCount();
                    intent.putExtra(KEY_ACTIVITY_RESULT, count);
                    setResult(RESULT_OK, intent);
                }
            }
        });
//        commentListAdapter.registerAdapterDataObserver(new RefreshViewEmptyObserver(refreshLayout, commentListAdapter));
        commentListAdapter.setOnItemChildClickListener(this);
        commentListAdapter.setOnItemLongClickListener(this);
        recyclerview.setAdapter(headerFooterAdapter);
        refreshLayout.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getData(true);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
            }
        });
        commentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    commentTv.setVisibility(View.GONE);
                    sendTv.setVisibility(View.VISIBLE);
                } else {
                    commentTv.setVisibility(View.VISIBLE);
                    sendTv.setVisibility(View.GONE);
                }
            }
        });
        refreshLayout.startRefresh();
        commentEdit.setMaxEms(1500);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        if (isRefresh) {
            pageIndex = 1;
        }
        if (taskItemEntity == null) return;
        getApi().commentListQuery(100, taskItemEntity.id, pageIndex, ActionConstants.DEFAULT_PAGE_SIZE).enqueue(new SimpleCallBack<CommentEntity>() {
            @Override
            public void onSuccess(Call<ResEntity<CommentEntity>> call, Response<ResEntity<CommentEntity>> response) {
                stopRefresh();
                commentListAdapter.bindData(isRefresh, response.body().result.items);
                pageIndex += 1;
                enableLoadMore(response.body().result.items);
                commentCount = commentListAdapter.getItemCount();
                commentTv.setText(commentCount + "条动态");
            }
        });
    }

    @OnClick({R.id.send_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_tv:
                sendComment();
                break;
        }
        super.onClick(v);
    }

    private void enableLoadMore(List result) {
        if (refreshLayout != null) {
            refreshLayout.setPullLoadEnable(result != null
                    && result.size() >= ActionConstants.DEFAULT_PAGE_SIZE);
        }
    }

    private void stopRefresh() {
        if (refreshLayout != null) {
            refreshLayout.stopRefresh();
            refreshLayout.stopLoadMore();
        }
    }

    /**
     * 添加评论
     */
    private void sendComment() {
        if (taskItemEntity == null) return;
        String content = commentEdit.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            content = content.replace(" ", "");
            content = content.replace("\n", "");
        }
        if (TextUtils.isEmpty(content)) {
            showTopSnackBar("请输入评论内容");
            commentEdit.setText("");
            return;
        } else if (commentEdit.getText().length() > 1500) {
            showTopSnackBar("评论内容不能超过1500字");
            return;
        }
        showLoadingDialog(null);
        getApi().commentCreate(100, taskItemEntity.id, content).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                CommentEntity.CommentItemEntity commentItemEntity = getNewComment();
                commentItemEntity.id = response.body().result.getAsString();
                commentListAdapter.addItem(0, commentItemEntity);
                commentEdit.setText("");
                commentEdit.clearFocus();
                recyclerview.scrollToPosition(0);
                commentTv.setVisibility(View.VISIBLE);
                sendTv.setVisibility(View.GONE);
                commentTv.setText(commentListAdapter.getItemCount() + "条动态");
                EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
                showTopSnackBar("添加评论失败");
            }
        });
    }

    /**
     * 获取新的评论实体
     *
     * @return
     */
    private CommentEntity.CommentItemEntity getNewComment() {
        CommentEntity.CommentItemEntity commentItemEntity = new CommentEntity.CommentItemEntity();
        commentItemEntity.createTime = DateUtils.millis();
        commentItemEntity.content = commentEdit.getText().toString();
        CommentEntity.CommentItemEntity.CreateUser createUser = new CommentEntity.CommentItemEntity.CreateUser();
        createUser.userId = getLoginUserId();
        createUser.userName = getLoginUserInfo().getName();
        createUser.pic = getLoginUserInfo().getPic();
        commentItemEntity.createUser = createUser;
        return commentItemEntity;
    }

    /**
     * 展示联系人对话框
     *
     * @param accid
     * @param hiddenChatBtn
     */
    public void showContactDialogFragment(String accid, boolean hiddenChatBtn) {
        String tag = "ContactDialogFragment";
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactDialogFragment.newInstance(accid, "成员资料", hiddenChatBtn)
                .show(mFragTransaction, tag);
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        CommentEntity.CommentItemEntity commentItemEntity = (CommentEntity.CommentItemEntity) adapter.getItem(position);
        switch (view.getId()) {
            case R.id.user_photo_image:
                if (commentItemEntity.createUser != null) {
                    if (!TextUtils.isEmpty(commentItemEntity.createUser.userId))
                        showContactDialogFragment(commentItemEntity.createUser.userId.toLowerCase(), true);
                }
                break;
        }
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        CommentEntity.CommentItemEntity entity = (CommentEntity.CommentItemEntity) adapter.getItem(adapter.getRealPos(position));
        if (entity != null) {
            if (entity.createUser == null) {
                return false;
            }
            if (TextUtils.equals(entity.createUser.userId.toLowerCase(), getLoginUserId().toLowerCase())) {
                showSelfBottomMeau(entity);
            } else {
                showOthersBottomMeau(entity);
            }
        }
        return true;
    }

    /**
     * 显示我的底部菜单
     */
    private void showSelfBottomMeau(final CommentEntity.CommentItemEntity commentItemEntity) {
        if (commentItemEntity == null) return;
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("复制", "删除"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                commentActionCopy(commentItemEntity.content);
                                break;
                            case 1:
                                deleteComment(commentItemEntity);
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 显示他人底部菜单
     */
    private void showOthersBottomMeau(final CommentEntity.CommentItemEntity commentItemEntity) {
        if (commentItemEntity == null) return;
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("复制"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                commentActionCopy(commentItemEntity.content);
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 评论复制
     *
     * @param charSequence
     */
    protected final void commentActionCopy(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) return;
        SystemUtils.copyToClipboard(getContext(), "comment", charSequence);
        showTopSnackBar("复制成功");
    }

    /**
     * 删除评论
     *
     * @param commentItemEntity
     */
    private void deleteComment(final CommentEntity.CommentItemEntity commentItemEntity) {
        if (commentItemEntity == null) return;
        showLoadingDialog(null);
        getApi().taskDeleteComment(commentItemEntity.id).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                if (commentListAdapter != null) {
                    commentListAdapter.removeItem(commentItemEntity);
                    commentTv.setText(commentListAdapter.getItemCount() + "条动态");
                    EventBus.getDefault().post(new TaskActionEvent(TaskActionEvent.TASK_REFRESG_ACTION));
                }
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }
}
