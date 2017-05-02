package com.icourt.alpha.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.icourt.alpha.R;
import com.icourt.alpha.activity.SearchTabActivity;
import com.icourt.alpha.adapter.SearchEngineAdapter;
import com.icourt.alpha.adapter.SearchHistoryAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.SearchEngineModel;
import com.icourt.alpha.db.dbmodel.SearhHistoryModel;
import com.icourt.alpha.db.dbservice.SearchHistroyDbService;
import com.icourt.alpha.entity.bean.SearchEngineEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.recyclerviewDivider.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.RealmList;
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
public class TabFindSearchFragment extends BaseFragment {

    Unbinder unbinder;
    SearchEngineAdapter searchEngineAdapter;
    SearchHistroyDbService searchHistroyDbService;
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

    public static TabFindSearchFragment newInstance() {
        return new TabFindSearchFragment();
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
        String loginUserId = getLoginUserId();
        searchHistroyDbService = new SearchHistroyDbService(TextUtils.isEmpty(loginUserId) ? "" : loginUserId);
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


        historyRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager historyLayoutManager = new LinearLayoutManager(getContext());
        historyLayoutManager.setAutoMeasureEnabled(true);
        historyRecyclerView.setLayoutManager(historyLayoutManager);
        historyRecyclerView.addItemDecoration(ItemDecorationUtils.getCommTrans10Divider(getContext(), true));
        historyRecyclerView.setAdapter(searchHistoryAdapter = new SearchHistoryAdapter());
        searchHistoryAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                SearhHistoryModel item = searchHistoryAdapter.getItem(position);
                if (item != null) {
                    RealmList<SearchEngineModel> searchEngines = item.searchEngines;
                    ArrayList<SearchEngineEntity> searchEngineEntities = new ArrayList<SearchEngineEntity>();
                    if (searchEngines != null) {
                        searchEngineEntities.addAll(ListConvertor.convertList(new ArrayList<IConvertModel<SearchEngineEntity>>(searchEngines)));
                    }
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
                        if (!TextUtils.isEmpty(v.getText()) && searchHistroyDbService != null) {

                            ArrayList<SearchEngineEntity> selectedData = searchEngineAdapter.getSelectedData();
                            launchH5Search(v.getText().toString(), selectedData);

                            SearhHistoryModel searhHistoryModel = new SearhHistoryModel();
                            searhHistoryModel.keyWord = v.getText().toString();
                            RealmList<SearchEngineModel> searchEngines = new RealmList<SearchEngineModel>();
                            for (int i = 0; i < selectedData.size(); i++) {
                                SearchEngineEntity item = searchEngineAdapter.getItem(i);
                                if (item != null) {
                                    searchEngines.add(item.convert2Model());
                                }
                            }
                            searhHistoryModel.searchEngines = searchEngines;
                            //清理本地
                            if (searchHistoryAdapter.getItemCount() >= 100) {
                                searchHistroyDbService.deleteAll();
                            }
                            searchHistroyDbService.insert(searhHistoryModel);
                            getSearhHistory();
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });

        getData(true);
        getSearhHistory();
    }

    /**
     * h5搜索
     *
     * @param keyWord
     * @param searchEngineEntities
     */
    private void launchH5Search(@NonNull String keyWord, @Nullable ArrayList<SearchEngineEntity> searchEngineEntities) {
        if (TextUtils.isEmpty(keyWord)) return;
        if (searchEngineEntities == null) {
            searchEngineEntities = new ArrayList<SearchEngineEntity>();
        }
        if (searchEngineEntities.isEmpty() && searchEngineAdapter.getItemCount() > 0) {
            searchEngineEntities.add(searchEngineAdapter.getItem(0));
        }
        SearchTabActivity.launch(getContext(),
                keyWord,
                searchEngineEntities);
    }


    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getApi().getSearchEngines()
                .enqueue(new SimpleCallBack<List<SearchEngineEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<SearchEngineEntity>>> call, Response<ResEntity<List<SearchEngineEntity>>> response) {
                        searchEngineAdapter.bindData(true, response.body().result);
                    }
                });

    }


    /**
     * 获取 搜索历史记录
     */
    private void getSearhHistory() {
        if (searchHistroyDbService != null) {
            RealmResults<SearhHistoryModel> searhHistoryModels = searchHistroyDbService.queryAll();
            searchHistoryAdapter.bindData(true, searhHistoryModels);
        }
    }


    @OnClick({R.id.search_input_clear_btn, R.id.search_history_clear_btn, R.id.search_audio_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_input_clear_btn:
                searchEdit.setText("");
                break;
            case R.id.search_history_clear_btn:
                searchHistoryAdapter.clearData();
                if (searchHistroyDbService != null) {
                    searchHistroyDbService.deleteAll();
                }
                break;
            case R.id.search_audio_btn:
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchHistroyDbService != null) {
            searchHistroyDbService.releaseService();
        }
    }
}
