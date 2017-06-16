package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.SearchItemAdapter;
import com.icourt.alpha.adapter.SearchPolymerizationAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.db.convertor.IConvertModel;
import com.icourt.alpha.db.convertor.ListConvertor;
import com.icourt.alpha.db.dbmodel.ContactDbModel;
import com.icourt.alpha.db.dbservice.ContactDbService;
import com.icourt.alpha.entity.bean.GroupContactBean;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.entity.bean.IMMessageCustomBody;
import com.icourt.alpha.entity.bean.ISearchItemEntity;
import com.icourt.alpha.entity.bean.SearchItemEntity;
import com.icourt.alpha.entity.bean.SearchPolymerizationEntity;
import com.icourt.alpha.fragment.dialogfragment.ContactDialogFragment;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SpannableUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.SoftKeyboardSizeWatchLayout;
import com.icourt.alpha.widget.filter.ListFilter;
import com.icourt.alpha.widget.nim.GlobalMessageObserver;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.search.model.MsgIndexRecord;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
import io.realm.RealmResults;
import retrofit2.Response;

import static com.icourt.alpha.R.id.search_customer_tv;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_P2P;
import static com.icourt.alpha.constants.Const.CHAT_TYPE_TEAM;
import static com.icourt.alpha.constants.Const.SEARCH_TYPE_CONTACT;
import static com.icourt.alpha.constants.Const.SEARCH_TYPE_MSG;
import static com.icourt.alpha.constants.Const.SEARCH_TYPE_TEAM;

/**
 * Description  聚合搜索
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/24
 * version 1.0.0
 */
public class SearchPolymerizationActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemChildClickListener, BaseRecyclerAdapter.OnItemClickListener {

    private static final String KEY_SEARCH_PRIORITY = "search_priority";
    int foregroundColor = 0xFFed6c00;
    @BindView(R.id.et_search_name)
    EditText etSearchName;
    @BindView(R.id.tv_search_cancel)
    TextView tvSearchCancel;
    @BindView(R.id.search_msg_tv)
    TextView searchMsgTv;
    @BindView(R.id.search_group_tv)
    TextView searchGroupTv;
    @BindView(search_customer_tv)
    TextView searchCustomerTv;
    @BindView(R.id.search_classfy_ll)
    LinearLayout searchClassfyLl;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.softKeyboardSizeWatchLayout)
    SoftKeyboardSizeWatchLayout softKeyboardSizeWatchLayout;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    SearchPolymerizationAdapter searchPolymerizationAdapter;
    @BindView(R.id.searchLayout)
    LinearLayout searchLayout;


    public static final int SEARCH_PRIORITY_CONTACT = 1;
    public static final int SEARCH_PRIORITY_CHAT_HISTORTY = 2;
    public static final int SEARCH_PRIORITY_TEAM = 3;


    @IntDef({SEARCH_PRIORITY_CONTACT,
            SEARCH_PRIORITY_CHAT_HISTORTY,
            SEARCH_PRIORITY_TEAM,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SEARCH_PRIORITY {

    }

    public static void launch(@NonNull Context context, @SEARCH_PRIORITY int search_priority) {
        if (context == null) return;
        Intent intent = new Intent(context, SearchPolymerizationActivity.class);
        intent.putExtra(KEY_SEARCH_PRIORITY, search_priority);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_polymerization);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchPolymerizationAdapter = new SearchPolymerizationAdapter());
        searchPolymerizationAdapter.setOnItemClickListener(this);
        searchPolymerizationAdapter.setOnItemChildClickListener(this);
        etSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    searchPolymerizationAdapter.clearData();
                    recyclerView.setVisibility(View.GONE);
                    searchClassfyLl.setVisibility(View.VISIBLE);
                    contentEmptyText.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    searchClassfyLl.setVisibility(View.GONE);
                    getData(true);
                }
            }
        });
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        final String keyWord = etSearchName.getText().toString();
        Observable.create(new ObservableOnSubscribe<List<SearchPolymerizationEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SearchPolymerizationEntity>> e) throws Exception {
                if (e.isDisposed()) return;
                List<SearchPolymerizationEntity> result = new ArrayList<SearchPolymerizationEntity>();

                //查询联系人
                ContactDbService contactDbService = new ContactDbService(getLoginUserId());
                RealmResults<ContactDbModel> name = contactDbService.contains("name", keyWord);
                List<GroupContactBean> contactBeen = ListConvertor.convertList(new ArrayList<IConvertModel<GroupContactBean>>(name));
                fiterRobots(contactBeen);
                contactDbService.releaseService();
                List<SearchItemEntity> searchContactItems = convert2SearchItem(contactBeen, keyWord);


                //查询讨论组
                List<GroupEntity> teamByKeyWord = getTeamByKeyWord(keyWord);
                List<SearchItemEntity> searchTeamItems = convertTeam2SearchItem(teamByKeyWord, keyWord);


                //查询聊天记录
                List<MsgIndexRecord> msgindexs = NIMClient.getService(MsgService.class).searchAllSessionBlock(keyWord, 4);
                List<SearchItemEntity> searchMsgItems = convertMsg2SearchItem(msgindexs, keyWord);

                switch (getIntent().getIntExtra(KEY_SEARCH_PRIORITY, 0)) {
                    case SEARCH_PRIORITY_CHAT_HISTORTY: {
                        //添加聊天记录
                        if (searchMsgItems != null && !searchMsgItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_MSG,
                                    "聊天记录", "查看更多聊天记录", searchMsgItems));
                        }
                        //添加讨论组
                        if (searchTeamItems != null && !searchTeamItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_TEAM,
                                    "讨论组", "查看更多讨论组", searchTeamItems));
                        }
                        //添加联系人
                        if (searchContactItems != null && !searchContactItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_CONTACT,
                                    "联系人", "查看更多联系人", searchContactItems));
                        }
                    }
                    break;
                    case SEARCH_PRIORITY_CONTACT: {
                        //添加联系人
                        if (searchContactItems != null && !searchContactItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_CONTACT,
                                    "联系人", "查看更多联系人", searchContactItems));
                        }
                        //添加聊天记录
                        if (searchMsgItems != null && !searchMsgItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_MSG,
                                    "聊天记录", "查看更多聊天记录", searchMsgItems));
                        }

                        //添加讨论组
                        if (searchTeamItems != null && !searchTeamItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_TEAM,
                                    "讨论组", "查看更多讨论组", searchTeamItems));
                        }
                    }
                    break;
                    case SEARCH_PRIORITY_TEAM: {
                        //添加讨论组
                        if (searchTeamItems != null && !searchTeamItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_TEAM,
                                    "讨论组", "查看更多讨论组", searchTeamItems));
                        }
                        //添加联系人
                        if (searchContactItems != null && !searchContactItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_CONTACT,
                                    "联系人", "查看更多联系人", searchContactItems));
                        }
                        //添加聊天记录
                        if (searchMsgItems != null && !searchMsgItems.isEmpty()) {
                            result.add(new SearchPolymerizationEntity(SEARCH_TYPE_MSG,
                                    "聊天记录", "查看更多聊天记录", searchMsgItems));
                        }
                    }
                    break;
                }

                e.onNext(result);
                e.onComplete();
            }


        }).compose(this.<List<SearchPolymerizationEntity>>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchPolymerizationEntity>>() {
                    @Override
                    public void accept(List<SearchPolymerizationEntity> searchPolymerizationEntities) throws Exception {
                        searchPolymerizationAdapter.bindData(true, searchPolymerizationEntities);
                        if (contentEmptyText != null) {
                            contentEmptyText.setVisibility(searchPolymerizationAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                        }
                    }
                });
    }


    /**
     * 根据名称 搜team
     *
     * @param keyWord
     * @return
     */
    private List<GroupEntity> getTeamByKeyWord(String keyWord) {
        List<GroupEntity> teams = new ArrayList<>();
        try {
            Response<ResEntity<List<GroupEntity>>> execute = getChatApi()
                    .groupQueryByName(keyWord)
                    .execute();
            if (execute != null
                    && execute.body() != null
                    && execute.body().result != null) {
                teams.addAll(execute.body().result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return teams;
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
                        searchItemEntity.id2 = imBody.id;
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

    private List<SearchItemEntity> convert2SearchItem(List<GroupContactBean> contactBeen, String keyWord) {
        List<SearchItemEntity> data = new ArrayList<>();
        if (contactBeen != null) {
            for (GroupContactBean item : contactBeen) {
                if (item != null) {
                    CharSequence originalText = item.name;
                    SearchItemEntity searchItemEntity = new SearchItemEntity(SpannableUtils.getTextForegroundColorSpan(originalText, keyWord, foregroundColor), null, item.pic, keyWord);
                    searchItemEntity.classfyType = SEARCH_TYPE_CONTACT;
                    searchItemEntity.type = item.type;
                    searchItemEntity.id = item.accid;
                    data.add(searchItemEntity);
                }
            }
        }
        return data;
    }

    /**
     * 将team 转化成搜索的item
     *
     * @param teams
     * @param keyWord
     * @return
     */
    private List<SearchItemEntity> convertTeam2SearchItem(List<GroupEntity> teams, String keyWord) {
        List<SearchItemEntity> data = new ArrayList<>();
        if (teams != null) {
            for (GroupEntity item : teams) {
                if (item != null) {
                    CharSequence originalText = item.name;
                    SearchItemEntity searchItemEntity = new SearchItemEntity(SpannableUtils.getTextForegroundColorSpan(originalText, keyWord, foregroundColor), null, item.pic, keyWord);
                    searchItemEntity.id = item.tid;
                    searchItemEntity.classfyType = SEARCH_TYPE_TEAM;
                    data.add(searchItemEntity);
                }
            }
        }
        return data;
    }


    /**
     * 过滤掉机器人
     *
     * @param contactBeen
     */
    private List<GroupContactBean> fiterRobots(List<GroupContactBean> contactBeen) {
        return new ListFilter<GroupContactBean>().filter(contactBeen, GroupContactBean.TYPE_ROBOT);
    }

    @OnClick({R.id.tv_search_cancel,
            R.id.search_msg_tv,
            R.id.search_group_tv,
            R.id.search_customer_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                finish();
                break;
            case R.id.search_msg_tv:
                ChatHistortySearchActivity
                        .launch(getContext(),
                                null,
                                null);
                break;
            case R.id.search_group_tv:
                GroupSearchActivity.launch(getContext(),
                        null,
                        GroupListActivity.GROUP_TYPE_MY_JOIN,
                        null);
                break;
            case R.id.search_customer_tv:
                ContactSearchActivity.launch(getContext(),
                        null,
                        null);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        SearchPolymerizationEntity item = searchPolymerizationAdapter.getItem(position);
        if (item == null) return;
        switch (view.getId()) {
            case R.id.search_more_tv:
                switch (item.classfyType) {
                    case SEARCH_TYPE_CONTACT:
                        ContactSearchActivity.launch(getContext(),
                                searchLayout,
                                TextUtils.isEmpty(etSearchName.getText()) ? "" : etSearchName.getText().toString());
                        break;
                    case SEARCH_TYPE_MSG:
                        ChatHistortySearchActivity
                                .launch(getContext(),
                                        etSearchName,
                                        TextUtils.isEmpty(etSearchName.getText()) ? "" : etSearchName.getText().toString());
                        break;
                    case SEARCH_TYPE_TEAM:
                        GroupSearchActivity.launch(getContext(),
                                etSearchName,
                                GroupListActivity.GROUP_TYPE_MY_JOIN,
                                TextUtils.isEmpty(etSearchName.getText()) ? "" : etSearchName.getText().toString());
                        break;
                }
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter instanceof SearchItemAdapter) {
            Object obj = adapter.getItem(position);
            if (obj instanceof ISearchItemEntity) {
                ISearchItemEntity item = (ISearchItemEntity) obj;
                switch (item.classfyType()) {
                    case SEARCH_TYPE_CONTACT:
                        showContactDialogFragment(item.getId(), StringUtils.equalsIgnoreCase(item.getId(), getLoginUserId(), false));
                        break;
                    case SEARCH_TYPE_MSG:
                        switch (item.type()) {
                            case CHAT_TYPE_P2P:
                                ChatActivity.launchP2P(getContext(),
                                        StringUtils.toLowerCase(item.getId()),
                                        TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle().toString(),
                                        item.getId2(),
                                        0);
                                break;
                            case CHAT_TYPE_TEAM:
                                ChatActivity.launchTEAM(getContext(),
                                        item.getId(),
                                        TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle().toString(),
                                        item.getId2(),
                                        0);
                                break;
                        }
                        break;
                    case SEARCH_TYPE_TEAM:
                        if (IMUtils.isMyJionedGroup(item.getId())) {
                            ChatActivity.launchTEAM(getContext(),
                                    item.getId(),
                                    TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle().toString(),
                                    0,
                                    0);
                        } else {
                            GroupDetailActivity.launchTEAM(getContext(), item.getId());
                        }
                        break;
                }
            }

        }
    }

    /**
     * 展示联系人对话框
     *
     * @param accid
     * @param hiddenChatBtn
     */
    public void showContactDialogFragment(String accid, boolean hiddenChatBtn) {
        String tag = ContactDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactDialogFragment.newInstance(accid, "成员资料", hiddenChatBtn)
                .show(mFragTransaction, tag);
    }
}
