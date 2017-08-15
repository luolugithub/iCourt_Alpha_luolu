package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.DocumentRootEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  资料库库创建/更新标题/页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class DiskActionActivity extends BaseActivity {

    public static final String ACTION_CREATE = "action_create";//创建资料库
    public static final String ACTION_UPDATE_TITLE = "action_update_title";//更新资料库标题
    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.document_name_et)
    EditText documentNameEt;
    DocumentRootEntity paramDocumentRootEntity;

    /**
     * 创建资料库
     *
     * @param context
     */
    public static void launchCreate(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, DiskActionActivity.class);
        intent.setAction(ACTION_CREATE);
        context.startActivity(intent);
    }

    /**
     * 更新资料库标题
     *
     * @param context
     */
    public static void launchUpdateTitle(@NonNull Context context, DocumentRootEntity documentRootEntity) {
        if (context == null) return;
        if (documentRootEntity == null) return;
        Intent intent = new Intent(context, DiskActionActivity.class);
        intent.setAction(ACTION_UPDATE_TITLE);
        intent.putExtra("data", documentRootEntity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_create);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("创建资料库");
        documentNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            }
        });
        if (TextUtils.equals(getIntent().getAction(), ACTION_UPDATE_TITLE)) {
            setTitle("更新资料库");
            paramDocumentRootEntity = (DocumentRootEntity) getIntent().getSerializableExtra("data");
            documentNameEt.setText(paramDocumentRootEntity.repo_name);
            documentNameEt.setSelection(documentNameEt.getText().length());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction: {
                /**
                 * 创建资料库
                 * 资料库名称限制：
                 * 文件名长度不得超过256个字节
                 * 文件名末尾不得有空格
                 * 特殊字符不能作为资料库名称：'\\', '/', ':', '*', '?', '"', '<', '>', '|', '\b', '\t'
                 * 超过一行的，输入框变高，折行显示
                 */
                if (StringUtils.isEmpty(documentNameEt.getText())) {
                    showTopSnackBar("资料库名称不能为空");
                    return;
                }

                if (documentNameEt.getText().toString().endsWith(" ")) {
                    showTopSnackBar("资料库名称末尾不得有空格");
                    return;
                }
                if (documentNameEt.getText().toString().getBytes().length > 256) {
                    showTopSnackBar("资料库名称过长");
                    return;
                }

                /* Pattern pattern = Pattern.compile(targetText, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(originalText);
        SpannableUtils.setTextForegroundColorSpan();*/
                if (TextUtils.equals(getIntent().getAction(), ACTION_CREATE)) {
                    createRootDocument();
                } else if (TextUtils.equals(getIntent().getAction(), ACTION_UPDATE_TITLE)) {
                    updateRootDocumentTitle();
                }
            }
            break;
            default:
                super.onClick(v);
                break;
        }
    }


    /**
     * 创建资料库
     */
    private void createRootDocument() {
        showLoadingDialog("创建中...");
        getSFileApi().documentRootCreate(documentNameEt.getText().toString())
                .enqueue(new SFileCallBack<DocumentRootEntity>() {
                    @Override
                    public void onSuccess(Call<DocumentRootEntity> call, Response<DocumentRootEntity> response) {
                        dismissLoadingDialog();
                        showToast("创建资料库成功");
                        EventBus.getDefault().post(response.body());
                        finish();
                    }

                    @Override
                    public void onFailure(Call<DocumentRootEntity> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }

    /**
     * 更新资料库标题
     *
     */
    private void updateRootDocumentTitle() {
        if (paramDocumentRootEntity == null) return;
        showLoadingDialog("更改中...");
        paramDocumentRootEntity.repo_name = documentNameEt.getText().toString();
        paramDocumentRootEntity.last_modified = DateUtils.millis();
        getSFileApi().documentRootUpdateName(
                paramDocumentRootEntity.repo_id,
                "rename",
                documentNameEt.getText().toString())
                .enqueue(new SFileCallBack<String>() {
                    @Override
                    public void onSuccess(Call<String> call, Response<String> response) {
                        dismissLoadingDialog();
                        if (TextUtils.equals("success", response.body())) {
                            showToast("更改资料库标题成功");
                            EventBus.getDefault().post(paramDocumentRootEntity);
                            finish();
                        } else {
                            showToast("更改资料库标题失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }
                });
    }
}
