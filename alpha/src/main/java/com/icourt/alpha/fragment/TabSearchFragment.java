package com.icourt.alpha.fragment;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchTabActivity;
import com.icourt.alpha.adapter.SearchEngineAdapter;
import com.icourt.alpha.adapter.SearchHistoryAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.SearchEngineModel;
import com.icourt.alpha.db.dbservice.SearchEngineDbService;
import com.icourt.alpha.entity.bean.SearchEngineEntity;
import com.icourt.alpha.entity.bean.SearchHistoryEntity;
import com.icourt.alpha.fragment.dialogfragment.AudioWaveDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.JsonUtils;
import com.icourt.alpha.utils.SpUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.UMMobClickAgent;
import com.icourt.alpha.view.recyclerviewDivider.DividerItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class TabSearchFragment extends BaseFragment implements OnFragmentCallBackListener {
    private static final String KEY_SEARCH_HISTORY = "search_history_%s";
    Unbinder unbinder;
    SearchEngineAdapter searchEngineAdapter;
    SearchHistoryAdapter searchHistoryAdapter;
    @BindView(R.id.search_edit)
    EditText searchEdit;
    @BindView(R.id.search_input_clear_btn)
    ImageView searchInputClearBtn;
    @BindView(R.id.search_audio_btn)
    ImageView searchAudioBtn;
    @BindView(R.id.search_engines_divider)
    TextView searchEnginesDivider;
    @BindView(R.id.engineRecyclerView)
    RecyclerView engineRecyclerView;
    @BindView(R.id.search_history_clear_btn)
    ImageView searchHistoryClearBtn;
    @BindView(R.id.history_divider)
    LinearLayout historyDivider;
    @BindView(R.id.historyRecyclerView)
    RecyclerView historyRecyclerView;
    @BindView(R.id.history_rl)
    RelativeLayout historyRl;
    SearchEngineDbService searchEngineDbService;
    final List<SearchHistoryEntity> recordSearchHistories = new ArrayList<>();
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;

    public static TabSearchFragment newInstance() {
        return new TabSearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_find_search, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        searchEngineDbService = new SearchEngineDbService(getLoginUserId());
        engineRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager engineLayoutManager = new GridLayoutManager(getContext(), 4);
        engineLayoutManager.setAutoMeasureEnabled(true);
        engineRecyclerView.setLayoutManager(engineLayoutManager);
        DividerItemDecoration engineItemDecoration = new DividerItemDecoration();
        engineItemDecoration.setDividerLookup(new DividerItemDecoration.DividerLookup() {
            @Override
            public DividerItemDecoration.Divider getVerticalDivider(int position) {
                return new DividerItemDecoration.Divider.Builder()
                        .size(getResources().getDimensionPixelSize(R.dimen.dp8))
                        .color(Color.TRANSPARENT)
                        .build();
            }

            @Override
            public DividerItemDecoration.Divider getHorizontalDivider(int position) {
                return new DividerItemDecoration.Divider.Builder()
                        .size(getResources().getDimensionPixelSize(R.dimen.dp8))
                        .color(Color.TRANSPARENT)
                        .build();
            }
        });
        engineRecyclerView.addItemDecoration(engineItemDecoration);
        engineRecyclerView.setAdapter(searchEngineAdapter = new SearchEngineAdapter());
        contentEmptyText.setText(R.string.str_click_refresh);
        searchEngineAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                contentEmptyText.setVisibility(searchEngineAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });


        historyRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager historyLayoutManager = new LinearLayoutManager(getContext());
        historyLayoutManager.setAutoMeasureEnabled(true);
        historyRecyclerView.setLayoutManager(historyLayoutManager);
        historyRecyclerView.setAdapter(searchHistoryAdapter = new SearchHistoryAdapter());
        searchHistoryAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                SearchHistoryEntity item = searchHistoryAdapter.getItem(position);
                if (item != null) {
                    List<SearchEngineEntity> searchEngines = item.searchEngines;
                    ArrayList<SearchEngineEntity> searchEngineEntities = new ArrayList<SearchEngineEntity>(searchEngines);
                    launchH5Search(item.keyWord, searchEngineEntities);
                }
            }
        });


        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchInputClearBtn.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }
        });
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), v);
                        if (!TextUtils.isEmpty(v.getText())) {

                            ArrayList<SearchEngineEntity> selectedData = searchEngineAdapter.getSelectedData();
                            launchH5Search(v.getText().toString(), selectedData);

                            SearchHistoryEntity searchHistoryEntity = new SearchHistoryEntity();
                            searchHistoryEntity.keyWord = v.getText().toString();
                            List<SearchEngineEntity> searchEngines = new ArrayList<SearchEngineEntity>();
                            for (int i = 0; i < selectedData.size(); i++) {
                                SearchEngineEntity item = selectedData.get(i);
                                if (item != null) {
                                    searchEngines.add(item);
                                }
                            }
                            searchHistoryEntity.searchEngines = searchEngines;
                            recordSearchHistories.add(0, searchHistoryEntity);

                            //最大10条
                            if (recordSearchHistories.size() > 10) {
                                List<SearchHistoryEntity> searchHistoryEntities = recordSearchHistories.subList(0, 10);
                                searchHistoryAdapter.bindData(true, searchHistoryEntities);
                            } else {
                                searchHistoryAdapter.bindData(true, recordSearchHistories);
                            }

                            saveSearchHistory();
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });

        getData(true);
        getSearchHistory();
    }

    /**
     * h5搜索
     *
     * @param keyWord
     * @param searchEngineEntities
     */
    private void launchH5Search(@NonNull String keyWord,
                                @Nullable ArrayList<SearchEngineEntity> searchEngineEntities) {
        if (TextUtils.isEmpty(keyWord)) return;
        if (searchEngineEntities == null) {
            searchEngineEntities = new ArrayList<SearchEngineEntity>();
        }
        if (searchEngineEntities.isEmpty() && searchEngineAdapter.getItemCount() > 0) {
            searchEngineEntities.add(searchEngineAdapter.getItem(0));
        }
        MobclickAgent.onEvent(getContext(), UMMobClickAgent.search_content_click_id);
        SearchTabActivity.launch(getContext(),
                keyWord,
                searchEngineEntities);
    }


    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getFromLocal();
        callEnqueue(
                getApi().getSearchEngines(),
                new SimpleCallBack<List<SearchEngineEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<SearchEngineEntity>>> call, Response<ResEntity<List<SearchEngineEntity>>> response) {
                        if (response.body().result != null) {
                            searchEngineDbService.deleteAll();
                            searchEngineDbService.insertOrUpdate(new ArrayList<IConvertModel<SearchEngineModel>>(response.body().result));
                        }
                        searchEngineAdapter.bindData(true, response.body().result);
                    }
                });
    }

    /**
     * 从本地获取
     */
    private void getFromLocal() {
        RealmResults<SearchEngineModel> searchEngineModels = searchEngineDbService.queryAll();
        if (searchEngineModels != null) {
            List<SearchEngineEntity> searchEnginges = ListConvertor.convertList(new ArrayList<IConvertModel<SearchEngineEntity>>(searchEngineModels));
            searchEngineAdapter.bindData(true, searchEnginges);
        }
    }


    /**
     * 获取 搜索历史记录
     */
    private void getSearchHistory() {
        try {
            String stringData = SpUtils.getInstance().getStringData(KEY_SEARCH_HISTORY, "");
            Type type = new TypeToken<ArrayList<SearchHistoryEntity>>() {
            }.getType();
            ArrayList<SearchHistoryEntity> searchHistoryEntities = JsonUtils.Gson2Type(stringData, type);
            recordSearchHistories.clear();
            recordSearchHistories.addAll(searchHistoryEntities);
            searchHistoryAdapter.bindData(true, recordSearchHistories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSearchHistory() {
        recordSearchHistories.clear();
        SpUtils.getInstance().putData(KEY_SEARCH_HISTORY, "");
        searchHistoryAdapter.clearData();
    }


    /**
     * 保存搜索历史消息
     */
    private void saveSearchHistory() {
        if (recordSearchHistories.size() > 10) {
            List<SearchHistoryEntity> searchHistoryEntities = recordSearchHistories.subList(0, 10);
            try {
                SpUtils.getInstance().putData(KEY_SEARCH_HISTORY, JsonUtils.Gson2String(searchHistoryEntities));
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                SpUtils.getInstance().putData(KEY_SEARCH_HISTORY, JsonUtils.Gson2String(recordSearchHistories));
            } catch (JsonParseException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick({R.id.search_input_clear_btn,
            R.id.search_history_clear_btn,
            R.id.search_audio_btn,
            R.id.contentEmptyText})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_input_clear_btn:
                searchEdit.setText("");
                break;
            case R.id.search_history_clear_btn:
                clearSearchHistory();
                break;
            case R.id.search_audio_btn:
                if (!checkPermission(Manifest.permission.RECORD_AUDIO)) {
                    reqPermission(Manifest.permission.RECORD_AUDIO, "我们需要录音权限!", 1001);
                    return;
                }
                showAudioWaveDialogFragment();
                break;
            case R.id.contentEmptyText:
                getData(true);
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    /**
     * 展示录音对话框
     */
    protected final void showAudioWaveDialogFragment() {
        String tag = AudioWaveDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        AudioWaveDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchEngineDbService != null) {
            searchEngineDbService.releaseService();
        }
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof AudioWaveDialogFragment) {
            if (params != null) {
                String searchText = params.getString(KEY_FRAGMENT_RESULT);
                if (!TextUtils.isEmpty(searchText))
                    searchEdit.setText(searchText.replace("。", ""));
            }
        }
    }
}
