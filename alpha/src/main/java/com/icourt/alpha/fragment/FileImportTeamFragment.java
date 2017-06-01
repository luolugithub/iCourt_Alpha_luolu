package com.icourt.alpha.fragment;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.JsonParseException;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.GroupAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.api.RequestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/20
 * version 1.0.0
 */
public class FileImportTeamFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String KEY_PATH = "path";
    public static final int TYPE_UPLOAD = 1001;
    private static final String KEY_DESC = "desc";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    GroupAdapter groupAdapter;
    HeaderFooterAdapter<GroupAdapter> headerFooterAdapter;
    EditText header_input_et;
    private final List<GroupEntity> groupEntities = new ArrayList<>();

    /**
     * @param path
     * @return
     */
    public static FileImportTeamFragment newInstance(String path, String desc) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PATH, path);
        bundle.putString(KEY_DESC, desc);
        FileImportTeamFragment importFilePathFragment = new FileImportTeamFragment();
        importFilePathFragment.setArguments(bundle);
        return importFilePathFragment;
    }

    OnPageFragmentCallBack onPageFragmentCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onPageFragmentCallBack = (OnPageFragmentCallBack) context;
        } catch (ClassCastException e) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_file_import_team, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        headerFooterAdapter = new HeaderFooterAdapter<>(groupAdapter = new GroupAdapter());
        groupAdapter.setSelectable(true);
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_input_text, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        header_input_et = (EditText) headerView.findViewById(R.id.header_input_et);
        header_input_et.clearFocus();
        header_input_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    groupAdapter.clearSelected();
                    groupAdapter.bindData(true, groupEntities);
                } else {
                    queryByName(s.toString());
                }
            }
        });
        recyclerView.setAdapter(headerFooterAdapter);
        groupAdapter.setOnItemClickListener(this);
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getChatApi().groupsQuery(0, true)
                .enqueue(new SimpleCallBack<List<GroupEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupEntity>>> call, Response<ResEntity<List<GroupEntity>>> response) {
                        groupEntities.clear();
                        groupEntities.addAll(response.body().result);
                        groupAdapter.bindData(true, groupEntities);
                    }
                });
    }

    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
        super.notifyFragmentUpdate(targetFrgament, type, bundle);
        if (targetFrgament == this) {
            if (type == TYPE_UPLOAD) {
                if (type == TYPE_UPLOAD) {
                    String path = getPath();
                    if (!TextUtils.isEmpty(path) && path.startsWith("http")) {
                        shareUrl2Contact(path, getArguments().getString(KEY_DESC, ""));
                    } else {
                        shareFile2Contact(path);
                    }
                }
            }
        }
    }

    protected void queryByName(String name) {
        List<GroupEntity> filterGroupEntities = new ArrayList<>();
        for (GroupEntity groupEntity : groupEntities) {
            if (groupEntity != null && !TextUtils.isEmpty(groupEntity.name)) {
                if (groupEntity.name.contains(name)) {
                    filterGroupEntities.add(groupEntity);
                }
            }
        }
        groupAdapter.clearSelected();
        groupAdapter.bindData(true, filterGroupEntities);
    }

    private String getPath() {
        return getArguments().getString(KEY_PATH);
    }

    private void shareUrl2Contact(String path, String desc) {
        if (TextUtils.isEmpty(path)) return;
        ArrayList<GroupEntity> selectedData = groupAdapter.getSelectedData();
        if (selectedData.isEmpty()) return;
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        String uid = StringUtils.toLowerCase(loginUserInfo != null ? loginUserInfo.getUserId() : "");
        final IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createLinkMsg(Const.CHAT_TYPE_TEAM,
                loginUserInfo == null ? "" : loginUserInfo.getName(),
                uid,
                selectedData.get(0).tid,
                path,
                desc,
                null,
                null);
        String jsonBody = null;
        try {
            jsonBody = JsonUtils.Gson2String(msgPostEntity);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        showLoadingDialog(null);
        getChatApi().msgAdd(RequestUtils.createJsonBody(jsonBody))
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        dismissLoadingDialog();
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<IMMessageCustomBody>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 分享文件到享聊
     */
    private void shareFile2Contact(String path) {
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件读写权限!", 1001);
            return;
        }
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
        ArrayList<GroupEntity> selectedData = groupAdapter.getSelectedData();
        if (selectedData.isEmpty()) return;
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        String uid = null;
        if (loginUserInfo != null) {
            uid = loginUserInfo.getUserId();
            if (!TextUtils.isEmpty(uid)) {
                uid = uid.toLowerCase();
            }
        }
        final IMMessageCustomBody msgPostEntity = IMMessageCustomBody.createFileMsg(
                Const.CHAT_TYPE_TEAM,
                loginUserInfo == null ? "" : loginUserInfo.getName(),
                uid,
                selectedData.get(0).tid,
                path);
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
        showLoadingDialog(null);
        getChatApi().msgImageAdd(params)
                .enqueue(new SimpleCallBack<IMMessageCustomBody>() {
                    @Override
                    public void onSuccess(Call<ResEntity<IMMessageCustomBody>> call, Response<ResEntity<IMMessageCustomBody>> response) {
                        dismissLoadingDialog();
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
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
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        groupAdapter.clearSelected();
        groupAdapter.toggleSelected(position);
    }
}
