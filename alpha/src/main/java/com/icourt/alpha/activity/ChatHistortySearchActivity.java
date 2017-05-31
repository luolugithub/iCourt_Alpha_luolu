package com.icourt.alpha.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.SearchItemAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.ISearchItemEntity;
import com.icourt.alpha.entity.bean.SearchItemEntity;
import com.icourt.alpha.widget.nim.GlobalMessageObserver;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;
import static com.icourt.alpha.constants.Const.SEARCH_TYPE_MSG;

/**
 * Description 聊天记录搜索
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/11
 * version 1.0.0
 */
public class ChatHistortySearchActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener {
    public static final String KEY_KEYWORD = "keyWord";
    int foregroundColor = 0xFFed6c00;
    SearchItemAdapter searchItemAdapter;
    @BindView(R.id.et_contact_name)
    EditText etContactName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;
    ContactDbService contactDbService;

    public static void launch(@NonNull Context context,
                              @Nullable View searchLayout,
                              @Nullable String keyWord) {
        if (context == null) return;
        Intent intent = new Intent(context, ChatHistortySearchActivity.class);
        intent.putExtra(KEY_KEYWORD, keyWord);
        if (context instanceof Activity
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && searchLayout != null) {
            ViewCompat.setTransitionName(searchLayout, "searchLayout");
            context.startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation((Activity) context, searchLayout, "searchLayout").toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_search);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        AlphaUserInfo loginUserInfo = getLoginUserInfo();
        contactDbService = new ContactDbService(loginUserInfo == null ? "" : loginUserInfo.getUserId());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchItemAdapter = new SearchItemAdapter(Integer.MAX_VALUE));
        searchItemAdapter.setOnItemClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        if (softKeyboardSizeWatchLayout != null
                                && softKeyboardSizeWatchLayout.isSoftKeyboardPop()) {
                            SystemUtils.hideSoftKeyBoard(getActivity(), etContactName, true);
                        }
                    }
                    break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        etContactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    searchItemAdapter.clearData();
                } else {
                    getData(true);
                }
            }
        });
        etContactName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), etContactName);
                        if (!TextUtils.isEmpty(etContactName.getText())) {
                            getData(true);
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        etContactName.setText(getIntent().getStringExtra(KEY_KEYWORD));
        etContactName.setSelection(etContactName.getText().length());
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        final String keyWord = etContactName.getText().toString();
        Observable.create(new ObservableOnSubscribe<List<SearchItemEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchItemEntity>> e) throws Exception {
                if (e.isDisposed()) return;

                //查询聊天记录
                List<MsgIndexRecord> msgindexs = NIMClient.getService(MsgService.class).searchAllSessionBlock(keyWord, 4);
                List<SearchItemEntity> searchMsgItems = convertMsg2SearchItem(msgindexs, keyWord);

                e.onNext(searchMsgItems);
                e.onComplete();
            }


        }).compose(this.<List<SearchItemEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchItemEntity>>() {
                    @Override
                    public void accept(List<SearchItemEntity> searchPolymerizationEntities) throws Exception {
                        searchItemAdapter.bindData(true, searchPolymerizationEntities);
                    }
                });
    }

    private List<SearchItemEntity> convertMsg2SearchItem(List<MsgIndexRecord> msgindexs, String keyWord) {
        List<SearchItemEntity> data = new ArrayList<>();
        if (msgindexs != null && !msgindexs.isEmpty()) {
            ContactDbService contactDbService = new ContactDbService(getLoginUserId());
            for (MsgIndexRecord item : msgindexs) {
                IMUtils.logIMMessage("------------>MsgIndexRecord Message", item.getMessage());
                log("------------>MsgIndexRecord:" + item);
                if (item != null) {
                    IMMessageCustomBody imBody = GlobalMessageObserver.getIMBody(item.getRecord().content);
                    if (imBody != null
                            && !TextUtils.isEmpty(imBody.content)
                            && imBody.content.contains(keyWord)) {
                        String title = null;
                        String icon = null;
                        switch (imBody.ope) {
                            case CHAT_TYPE_P2P:
                                ContactDbModel contactDbModel = contactDbService.queryFirst("accid", imBody.to);
                                if (contactDbModel != null) {
                                    GroupContactBean groupContactBean = contactDbModel.convert2Model();
                                    if (groupContactBean != null) {
                                        title = groupContactBean.name;
                                        icon = groupContactBean.pic;
                                    }
                                } else {
                                    continue;
                                }
                                break;
                            case CHAT_TYPE_TEAM:
                                Team team = NIMClient.getService(TeamService.class).queryTeamBlock(imBody.to);
                                if (team != null) {
                                    title = team.getName();
                                    icon = team.getIcon();
                                }
                                break;
                        }
                        CharSequence content;
                        if (item.getRecord().count > 1) {
                            content = String.format("%s条相关聊天记录", item.getRecord().count);
                        } else {
                            //瞄色
                            CharSequence originalText = imBody.content;
                            content = SpannableUtils.getTextForegroundColorSpan(originalText, keyWord, foregroundColor);
                        }
                        SearchItemEntity searchItemEntity = new SearchItemEntity(title, content, icon, keyWord);
                        searchItemEntity.id = imBody.to;
                        searchItemEntity.type = imBody.ope;
                        searchItemEntity.classfyType = SEARCH_TYPE_MSG;
                        searchItemEntity.recordTime = item.getTime();
                        data.add(searchItemEntity);
                    }
                }
            }
            contactDbService.releaseService();
        }
        return data;
    }

    @OnClick({R.id.tv_search_cancel})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                SystemUtils.hideSoftKeyBoard(getActivity(), etContactName, true);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDbService != null) {
            contactDbService.releaseService();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof SearchItemAdapter) {
            Object obj = adapter.getItem(position);
            if (obj instanceof ISearchItemEntity) {
                ISearchItemEntity item = (ISearchItemEntity) obj;
                switch (item.classfyType()) {
                    case SEARCH_TYPE_MSG:
                        switch (item.type()) {
                            case CHAT_TYPE_P2P:
                                ChatActivity.launchP2P(getContext(),
                                        StringUtils.toLowerCase(item.getId()),
                                        TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle().toString(),
                                        item.getRecordTime(),
                                        0);
                                break;
                            case CHAT_TYPE_TEAM:
                                ChatActivity.launchTEAM(getContext(),
                                        item.getId(),
                                        TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle().toString(),
                                        item.getRecordTime(),
                                        0);
                                break;
                        }
                        break;
                }
            }
        }
    }
}
