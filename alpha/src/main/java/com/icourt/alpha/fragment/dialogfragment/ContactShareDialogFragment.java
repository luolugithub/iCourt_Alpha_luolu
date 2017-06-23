package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.fragment.ContactActionFragment;
import com.icourt.alpha.fragment.GroupActionFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.INotifyFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.api.RequestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/10
 * version 1.0.0
 */
public class ContactShareDialogFragment extends BaseDialogFragment
        implements OnFragmentCallBackListener {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.titleContent)
    TextView titleContent;


    public static ContactShareDialogFragment newInstance(@NonNull long msgId, boolean isFilterMySelef) {
        ContactShareDialogFragment contactDialogFragment = new ContactShareDialogFragment();
        Bundle args = new Bundle();
        args.putLong("msgId", msgId);
        args.putBoolean("isFilterMySelef", isFilterMySelef);
        contactDialogFragment.setArguments(args);
        return contactDialogFragment;
    }

    /**
     * 文档转发到享聊
     *
     * @param filePath
     * @return
     */
    public static ContactShareDialogFragment newInstanceFile(@NonNull String filePath, boolean isFilterMySelef) {
        ContactShareDialogFragment contactDialogFragment = new ContactShareDialogFragment();
        Bundle args = new Bundle();
        args.putString("filePath", filePath);
        args.putBoolean("isFilterMySelef", isFilterMySelef);
        contactDialogFragment.setArguments(args);
        return contactDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_share, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private boolean isFilterMySelef() {
        return getArguments().getBoolean("isFilterMySelef", false);
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                titleContent.setText(position == 0 ? "享聊" : "讨论组");
            }
        });
        baseFragmentAdapter.bindData(true, Arrays.asList(ContactActionFragment.newInstance(isFilterMySelef())
                , GroupActionFragment.newInstance()));
    }

    @OnClick({R.id.titleBack, R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(0);
                    return;
                }
                dismiss();
                break;
            case R.id.titleAction:
                if (getArguments().getLong("msgId") > 0) {
                    shareMsg();
                } else {
                    shareFile(getArguments().getString("filePath"));
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 分享消息
     */
    private void shareMsg() {
        Set<GroupEntity> sendGroupEntities = new HashSet<>();
        Set<GroupContactBean> sendGroupContactBeans = new HashSet<>();
        Fragment item = baseFragmentAdapter.getItem(0);
        if (item instanceof INotifyFragment) {
            Bundle fragmentData = ((INotifyFragment) item).getFragmentData(0, null);
            if (fragmentData != null) {
                try {
                    sendGroupContactBeans.addAll((Collection<? extends GroupContactBean>) fragmentData.getSerializable(KEY_FRAGMENT_RESULT));
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }

        Fragment item1 = baseFragmentAdapter.getItem(1);
        if (item1 instanceof INotifyFragment) {
            Bundle fragmentData = ((INotifyFragment) item1).getFragmentData(0, null);
            if (fragmentData != null) {
                try {
                    sendGroupEntities.addAll((Collection<? extends GroupEntity>) fragmentData.getSerializable(KEY_FRAGMENT_RESULT));
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!sendGroupContactBeans.isEmpty() || !sendGroupEntities.isEmpty()) {
            showLoadingDialog(null);
            JsonArray param = new JsonArray();
            long msgId = getArguments().getLong("msgId");
            for (GroupEntity groupEntity : sendGroupEntities) {
                if (groupEntity != null) {
                    JsonObject groupJsonObject = new JsonObject();
                    groupJsonObject.addProperty("ope", CHAT_TYPE_TEAM);
                    groupJsonObject.addProperty("to", groupEntity.tid);
                    groupJsonObject.addProperty("msg_id", msgId);
                    param.add(groupJsonObject);
                }
            }

            for (GroupContactBean groupContactBean : sendGroupContactBeans) {
                if (groupContactBean != null) {
                    JsonObject groupJsonObject = new JsonObject();
                    groupJsonObject.addProperty("ope", CHAT_TYPE_P2P);
                    groupJsonObject.addProperty("to", groupContactBean.accid);
                    groupJsonObject.addProperty("msg_id", msgId);
                    param.add(groupJsonObject);
                }
            }

            getChatApi().msgTrans(RequestUtils.createJsonBody(param.toString()))
                    .enqueue(new SimpleCallBack<JsonElement>() {

                        @Override
                        public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                            dismissLoadingDialog();
                            dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                            super.onFailure(call, t);
                            dismissLoadingDialog();
                        }
                    });
        }
    }

    /**
     * 分享文件到享聊
     */
    private void shareFile(String filePath) {
        List<GroupEntity> sendGroupEntities = new ArrayList<>();
        List<GroupContactBean> sendGroupContactBeans = new ArrayList<>();
        Fragment item = baseFragmentAdapter.getItem(0);
        if (item instanceof INotifyFragment) {
            Bundle fragmentData = ((INotifyFragment) item).getFragmentData(0, null);
            if (fragmentData != null) {
                try {
                    sendGroupContactBeans.addAll((Collection<? extends GroupContactBean>) fragmentData.getSerializable(KEY_FRAGMENT_RESULT));
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }

        Fragment item1 = baseFragmentAdapter.getItem(1);
        if (item1 instanceof INotifyFragment) {
            Bundle fragmentData = ((INotifyFragment) item1).getFragmentData(0, null);
            if (fragmentData != null) {
                try {
                    sendGroupEntities.addAll((Collection<? extends GroupEntity>) fragmentData.getSerializable(KEY_FRAGMENT_RESULT));
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        }
        shareFile2Contact(filePath, sendGroupEntities, sendGroupContactBeans);
    }

    /**
     * 分享文件到享聊
     */
    private void shareFile2Contact(String path, List<GroupEntity> sendGroupEntities, List<GroupContactBean> sendGroupContactBeans) {
//        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件读写权限!", 1001);
//            return;
//        }
        if (TextUtils.isEmpty(path)) return;
        //content://com.icourt.alpha.provider/external_storage_root/alpha_download/dshgghsdhg.pdf
        ParcelFileDescriptor n_fileDescriptor = null;
        File file = null;
        Uri fileUri = null;
        if (path.startsWith("content://")) {
            try {
                fileUri = Uri.parse(path);
                n_fileDescriptor = UriUtils.get_N_FileDescriptor(getContext(), fileUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (n_fileDescriptor == null) {
                showTopSnackBar("共享文件不存在啦");
                return;
            }
        } else {
            file = new File(path);
            if (!file.exists()) {
                showTopSnackBar("文件不存在啦");
                return;
            }
        }
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        String uid = StringUtils.toLowerCase(loginUserInfo.getUserId());
        IMMessageCustomBody msgPostEntity = null;

        if (!sendGroupEntities.isEmpty()) {
            if (sendGroupEntities.size() > 0) {
                showLoadingDialog(null);
                for (int i = 0; i < sendGroupEntities.size(); i++) {
                    msgPostEntity = IMMessageCustomBody.createFileMsg(
                            Const.CHAT_TYPE_TEAM,
                            loginUserInfo == null ? "" : loginUserInfo.getName(),
                            uid,
                            sendGroupEntities.get(i).tid,
                            path);
                    sendFileToServer(msgPostEntity, file, fileUri, n_fileDescriptor);
                }
            }
        } else if (!sendGroupContactBeans.isEmpty()) {
            if (sendGroupContactBeans.size() > 0) {
                showLoadingDialog(null);
                for (int i = 0; i < sendGroupContactBeans.size(); i++) {
                    msgPostEntity = IMMessageCustomBody.createFileMsg(
                            Const.CHAT_TYPE_P2P,
                            loginUserInfo == null ? "" : loginUserInfo.getName(),
                            uid,
                            sendGroupContactBeans.get(i).accid,
                            path);
                    sendFileToServer(msgPostEntity, file, fileUri, n_fileDescriptor);
                }
            }
        }


    }

    private void sendFileToServer(IMMessageCustomBody msgPostEntity, File file, Uri fileUri, ParcelFileDescriptor n_fileDescriptor) {
        Map<String, RequestBody> params = new HashMap<>();
        params.put("platform", RequestUtils.createTextBody(msgPostEntity.platform));
        params.put("to", RequestUtils.createTextBody(msgPostEntity.to));
        params.put("from", RequestUtils.createTextBody(msgPostEntity.from));
        params.put("ope", RequestUtils.createTextBody(String.valueOf(msgPostEntity.ope)));
        params.put("name", RequestUtils.createTextBody(msgPostEntity.name));
        params.put("magic_id", RequestUtils.createTextBody(msgPostEntity.magic_id));
        if (file != null) {
            params.put(RequestUtils.createStreamKey(file), RequestUtils.createStreamBody(file));
        } else {
            String lastPathSegment = fileUri.getLastPathSegment();
            if (TextUtils.isEmpty(lastPathSegment)) {
                lastPathSegment = UUID.randomUUID().toString();
            }
            byte[] bytes = FileUtils.fileDescriptor2Byte(n_fileDescriptor);
            if (n_fileDescriptor != null) {
                try {
                    n_fileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            params.put(RequestUtils.createStreamKey(lastPathSegment), RequestUtils.createStreamBody(bytes));
        }


        getChatApi().msgImageAdd(params)
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        dismissLoadingDialog();
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment == baseFragmentAdapter.getItem(0)) {
            viewPager.setCurrentItem(1);
        }
    }
}
