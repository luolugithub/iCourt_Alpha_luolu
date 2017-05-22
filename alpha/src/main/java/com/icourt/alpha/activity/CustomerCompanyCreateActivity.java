package com.icourt.alpha.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.db.dbmodel.CustomerDbModel;
import com.icourt.alpha.db.dbservice.CustomerDbService;
import com.icourt.alpha.entity.bean.ContactDeatilBean;
import com.icourt.alpha.entity.bean.CustomerEntity;
import com.icourt.alpha.entity.bean.GroupBean;
import com.icourt.alpha.entity.bean.SelectGroupBean;
import com.icourt.alpha.entity.event.UpdateCustomerEvent;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.TextFormater;
import com.icourt.api.RequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 新建、编辑企业联系人
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/19
 * version 2.0.0
 */

public class CustomerCompanyCreateActivity extends BaseActivity {
    public static final int CREATE_CUSTOMER_ACTION = 1;//创建联系人
    public static final int UPDATE_CUSTOMER_ACTION = 2;//编辑联系人

    private static final int SELECT_GROUP_REQUEST = 1;//选择团队
    private static final int SELECT_OTHER_REQUEST = 2;//选择其他信息
    int action;
    boolean isAddOrEdit = false;//false:编辑 ; true:新建
    @BindView(R.id.activity_add_group_contact_enterprise_name_edittext)
    EditText activityAddGroupContactEnterpriseNameEdittext;
    @BindView(R.id.activity_add_group_contact_group_textview)
    TextView activityAddGroupContactGroupTextview;
    @BindView(R.id.activity_add_person_contact_group_arrow_right_view)
    ImageView activityAddPersonContactGroupArrowRightView;
    @BindView(R.id.activity_add_group_contact_address_layout)
    LinearLayout activityAddGroupContactAddressLayout;
    @BindView(R.id.activity_add_group_contact_add_address_layout)
    LinearLayout activityAddGroupContactAddAddressLayout;
    @BindView(R.id.activity_add_group_contact_email_layout)
    LinearLayout activityAddGroupContactEmailLayout;
    @BindView(R.id.activity_add_group_contact_add_email_layout)
    LinearLayout activityAddGroupContactAddEmailLayout;
    @BindView(R.id.activity_add_group_contact_date_layout)
    LinearLayout activityAddGroupContactDateLayout;
    @BindView(R.id.activity_add_group_contact_add_date_layout)
    LinearLayout activityAddGroupContactAddDateLayout;
    @BindView(R.id.activity_add_group_contact_paper_layout)
    LinearLayout activityAddGroupContactPaperLayout;
    @BindView(R.id.activity_add_group_contact_add_paper_layout)
    LinearLayout activityAddGroupContactAddPaperLayout;
    @BindView(R.id.activity_add_group_contact_liaisons_layout)
    LinearLayout activityAddGroupContactLiaisonsLayout;
    @BindView(R.id.activity_add_group_contact_add_liaisons_layout)
    LinearLayout activityAddGroupContactAddLiaisonsLayout;
    @BindView(R.id.activity_add_group_contact_impression_edittext)
    EditText activityAddGroupContactImpressionEdittext;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    private LayoutInflater layoutInflater;
    private LinkedHashMap<Integer, View> addressMap, emailMap, paperMap, dateMap, liaisonsMap;
    private int addressKey = 0, emailKey = 0, paperKey = 0, dateKey = 0, liaisonsKey = 0;
    private ArrayList<SelectGroupBean> groupBeanList;
    private ArrayList<CustomerEntity> liaisonsList;
    private boolean isCanAddContact = true;
    private ContactDeatilBean contactDeatilBean, newAddContactDetailBean;
    private List<ContactDeatilBean> oldLiaisonsList;
    CustomerDbService customerDbService;

    @IntDef({CREATE_CUSTOMER_ACTION,
            UPDATE_CUSTOMER_ACTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CUSTOMER_ACTION {

    }

    public static void launch(@NonNull Context context, @NonNull ContactDeatilBean contactDeatilBean, @CUSTOMER_ACTION int action) {
        if (context == null) return;
        Intent intent = new Intent(context, CustomerCompanyCreateActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("contactDeatilBean", contactDeatilBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company_contact_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        customerDbService = new CustomerDbService(getLoginUserId());
        layoutInflater = LayoutInflater.from(this);
        action = getIntent().getIntExtra("action", -1);
        contactDeatilBean = (ContactDeatilBean) getIntent().getSerializableExtra("contactDeatilBean");
        liaisonsList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(this);
        addressMap = new LinkedHashMap<>();
        emailMap = new LinkedHashMap<>();
        paperMap = new LinkedHashMap<>();
        dateMap = new LinkedHashMap<>();
        liaisonsMap = new LinkedHashMap<>();
        switch (action) {
            case CREATE_CUSTOMER_ACTION:
                createCustomerSetData();
                break;
            case UPDATE_CUSTOMER_ACTION:
                updateCustomerSetData();
                break;
        }
        activityAddGroupContactEnterpriseNameEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { //此处为失去焦点时的处理内容
                    checkName(activityAddGroupContactEnterpriseNameEdittext.getText().toString().trim());
                }
            }
        });
    }

    //新建联系人view
    private void createCustomerSetData() {
        isAddOrEdit = true;
        setTitle("新建企业联系人");
        if (groupBeanList == null) {
            groupBeanList = new ArrayList<>();
        }
        getGroupList();
    }

    //编辑联系人view
    private void updateCustomerSetData() {
        if (contactDeatilBean != null) {
            isCanAddContact = false;
            isAddOrEdit = false;
            if (contactDeatilBean.getContact() != null) {
                setTitle(contactDeatilBean.getContact().getName());
                getLiaisons(contactDeatilBean.getContact().getPkid());
                activityAddGroupContactEnterpriseNameEdittext.setText(contactDeatilBean.getContact().getName());
            }
            if (contactDeatilBean.getGroups() != null) {
                if (contactDeatilBean.getGroups().size() > 0) {
                    if (groupBeanList == null) {
                        groupBeanList = new ArrayList<>();
                    }
                    StringBuffer stringBuffer = new StringBuffer();
                    for (ContactDeatilBean.GroupsBean groupBean : contactDeatilBean.getGroups()) {
                        SelectGroupBean g = new SelectGroupBean();
                        g.groupId = (groupBean.getGroupId());
                        g.groupName = (groupBean.getGroupName());
                        groupBeanList.add(g);
                        stringBuffer.append(groupBean.getGroupName() + ",");
                    }
                    activityAddGroupContactGroupTextview.setText(stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
                } else {
                    activityAddGroupContactGroupTextview.setText("");
                }
            } else {
                activityAddGroupContactGroupTextview.setText("");
            }
            if (contactDeatilBean.getAddresses() != null) {
                for (ContactDeatilBean.AddressesBean addressesBean : contactDeatilBean.getAddresses()) {
                    addAddressItemView(addressesBean);
                }
            }
            if (contactDeatilBean.getMails() != null) {
                for (ContactDeatilBean.MailsBean mailsBean : contactDeatilBean.getMails()) {
                    addEmailItemView(mailsBean);
                }
            }

            if (contactDeatilBean.getDates() != null) {
                for (ContactDeatilBean.DateBean dateBean : contactDeatilBean.getDates()) {
                    addDateItemView(dateBean);
                }
            }

            if (contactDeatilBean.getCertificates() != null) {
                for (ContactDeatilBean.CertificatesBean certificatesBean : contactDeatilBean.getCertificates()) {
                    addParperItemView(certificatesBean);
                }
            }
        }
    }

    @OnClick({R.id.titleAction, R.id.activity_add_group_contact_group_textview,
            R.id.activity_add_group_contact_add_address_layout, R.id.activity_add_group_contact_add_email_layout,
            R.id.activity_add_group_contact_add_date_layout, R.id.activity_add_group_contact_add_paper_layout,
            R.id.activity_add_group_contact_add_liaisons_layout})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        SystemUtils.hideSoftKeyBoard(this);
        switch (v.getId()) {
            case R.id.titleAction://完成
                if (isCanAddContact) {
                    String name = activityAddGroupContactEnterpriseNameEdittext.getText().toString().trim();
                    if (!TextUtils.isEmpty(name)) {
                        addContact();
                    } else {
                        showTopSnackBar("请填写联系人名称");
                    }
                } else {
                    if (contactDeatilBean != null) {
                        updateContact();
                    } else {
                        showTopSnackBar(R.string.person_contact_warn_top_text);
                    }
                }
                break;
            case R.id.activity_add_group_contact_group_textview://选择团队
                if (groupBeanList != null)
                    GroupSelectActivity.launchForResult(CustomerCompanyCreateActivity.this, groupBeanList, SELECT_GROUP_REQUEST);
                break;
            case R.id.activity_add_group_contact_add_address_layout://增加地址
                addAddressItemView(null);
                break;
            case R.id.activity_add_group_contact_add_email_layout://增加邮箱
                addEmailItemView(null);
                break;
            case R.id.activity_add_group_contact_add_date_layout://增加重要日期
                addDateItemView(null);
                break;
            case R.id.activity_add_group_contact_add_paper_layout://增加机构证件
                addParperItemView(null);
                break;
            case R.id.activity_add_group_contact_add_liaisons_layout://增加联络人
                String pkid = null;
                if (!isAddOrEdit) {
                    pkid = contactDeatilBean.getContact().getPkid();
                }
                SelectLiaisonActivity.launchForResult(this, Const.SELECT_ENTERPRISE_LIAISONS_TAG_ACTION, pkid, liaisonsList, SELECT_OTHER_REQUEST);
                break;
        }
    }

    /**
     * 添加地址itemview
     */
    private void addAddressItemView(final ContactDeatilBean.AddressesBean addressesBean) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
        EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);
        valuenameText.setFocusableInTouchMode(true);
        valuenameText.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        addressKey += 1;
        addressMap.put(addressKey, view);
        final int addressTag = addressKey;
        deleteView.setVisibility(View.VISIBLE);
        if (addressesBean != null) {
            keynameText.setText(addressesBean.getItemSubType());
            valuenameText.setText(addressesBean.getItemValue());
        } else {
            keynameText.setText("办公");
        }
        activityAddGroupContactAddressLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddGroupContactAddressLayout.removeView(view);
                addressMap.remove(addressTag);
                if (addressesBean != null) {
                    contactDeatilBean.getAddresses().remove(addressesBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectCustomerTagActivity.launchForResult(CustomerCompanyCreateActivity.this, Const.SELECT_ADDRESS_TAG_ACTION, addressTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST);
            }
        });
    }

    /**
     * 添加邮箱itemview
     */
    private void addEmailItemView(final ContactDeatilBean.MailsBean mailsBean) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
        EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);
        valuenameText.setFocusableInTouchMode(true);
        valuenameText.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        emailKey += 1;
        emailMap.put(emailKey, view);
        final int emailTag = emailKey;
        deleteView.setVisibility(View.VISIBLE);
        if (mailsBean != null) {
            keynameText.setText(mailsBean.getItemSubType());
            valuenameText.setText(mailsBean.getItemValue());
        } else {
            keynameText.setText("主要");
        }
        activityAddGroupContactEmailLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddGroupContactEmailLayout.removeView(view);
                emailMap.remove(emailTag);
                if (mailsBean != null) {
                    contactDeatilBean.getMails().remove(mailsBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectCustomerTagActivity.launchForResult(CustomerCompanyCreateActivity.this, Const.SELECT_EMAIL_TAG_ACTION, emailTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST);
            }
        });
    }

    /**
     * 添加日期itemview
     */
    private void addDateItemView(final ContactDeatilBean.DateBean dateBean) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
        final EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);
        valuenameText.setFocusable(false);
        valuenameText.setFocusableInTouchMode(false);
        dateKey += 1;
        dateMap.put(dateKey, view);
        final int dateTag = dateKey;
        deleteView.setVisibility(View.VISIBLE);
        if (dateBean != null) {
            keynameText.setText(dateBean.getItemSubType());
            valuenameText.setText(dateBean.getItemValue());
        } else {
            keynameText.setText("注册日期");
        }
        activityAddGroupContactDateLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddGroupContactDateLayout.removeView(view);
                dateMap.remove(dateTag);
                if (dateBean != null) {
                    contactDeatilBean.getDates().remove(dateBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SelectCustomerTagActivity.launchForResult(CustomerCompanyCreateActivity.this, Const.SELECT_DATE_TAG_ACTION, dateTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST);
            }
        });
        valuenameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtils.hideSoftKeyBoard(CustomerCompanyCreateActivity.this);
                showDatePicker(valuenameText);
            }
        });
    }

    /**
     * 添加证件itemview
     */
    private void addParperItemView(final ContactDeatilBean.CertificatesBean certificatesBean) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
        EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);

        valuenameText.setFocusableInTouchMode(true);
        valuenameText.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        paperKey += 1;
        paperMap.put(paperKey, view);
        final int paperTag = paperKey;
        deleteView.setVisibility(View.VISIBLE);
        if (certificatesBean != null) {
            keynameText.setText(certificatesBean.getItemSubType());
            valuenameText.setText(certificatesBean.getItemValue());
        } else {
            keynameText.setText("营业执照");
        }
        activityAddGroupContactPaperLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddGroupContactPaperLayout.removeView(view);
                paperMap.remove(paperTag);
                if (certificatesBean != null) {
                    contactDeatilBean.getCertificates().remove(certificatesBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectCustomerTagActivity.launchForResult(CustomerCompanyCreateActivity.this, Const.SELECT_PAPERS_TAG_ACTION, paperTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST);
            }
        });
    }

    /**
     * 添加联络人itemview
     */
    private void addLiaisonsItemView(final CustomerEntity customerEntity) {
        if (customerEntity != null) {
            final View view = layoutInflater.inflate(R.layout.add_contact_liaisons_item_layout, null);
            ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_liaisons_item_delete_view);
            final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_liaisons_item_name_text);
            final TextView setRelationText = (TextView) view.findViewById(R.id.activity_add_contact_liaisons_item_relation_text);
            ImageView photoView = (ImageView) view.findViewById(R.id.activity_add_contact_liaisons_item_photoview);

            liaisonsKey += 1;
            liaisonsMap.put(liaisonsKey, view);
            final int liaisonTag = liaisonsKey;

            liaisonsList.add(customerEntity);
            deleteView.setVisibility(View.VISIBLE);
            setRelationText.setVisibility(View.VISIBLE);
            keynameText.setText(customerEntity.name);
            if (!TextUtils.isEmpty(customerEntity.itemSubType)) {
                setRelationText.setText(customerEntity.itemSubType);
            } else {
                setRelationText.setHint("设置关系");
            }

            if ("P".equals(customerEntity.contactType)) {
                if ("女".equals(customerEntity.sex)) {
                    photoView.setImageResource(R.mipmap.female);
                } else {
                    photoView.setImageResource(R.mipmap.male);
                }
            } else {
                photoView.setImageResource(R.mipmap.company);
            }
            activityAddGroupContactLiaisonsLayout.addView(view);
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityAddGroupContactLiaisonsLayout.removeView(view);
                    if (liaisonsList.contains(customerEntity))
                        liaisonsList.remove(customerEntity);
                    if (oldLiaisonsList != null) {
                        if (oldLiaisonsList.contains(customerEntity))
                            oldLiaisonsList.remove(customerEntity);
                    }
                    liaisonsKey -= 1;
                }
            });

            setRelationText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectCustomerTagActivity.launchForResult(CustomerCompanyCreateActivity.this, Const.SELECT_RELATION_TAG_ACTION, liaisonsList.indexOf(customerEntity) + 1, null, SELECT_OTHER_REQUEST);
                }
            });
        }
    }

    /**
     * 显示日期选择器
     */
    private void showDatePicker(final EditText textView) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String time = null;
                String month = null, day = null;
                if (i1 + 1 < 10) {
                    month = "0" + (i1 + 1);
                } else {
                    month = (i1 + 1) + "";
                }
                if (i2 < 10) {
                    day = "0" + i2;
                } else {
                    day = i2 + "";
                }
                time = i + "年" + month + "月" + day + "日";
                textView.setText(time);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * 更新数据库
     */
    private void saveContactToDB() {
        dismissLoadingDialog();
        ContactDeatilBean.ContactBean contactBean = null;
        if (newAddContactDetailBean != null) {
            if (newAddContactDetailBean.company != null)
                contactBean = newAddContactDetailBean.company.contact;
        } else if (contactDeatilBean != null) {
            contactBean = contactDeatilBean.getContact();
        }
        if (contactBean != null) {
            CustomerEntity customerEntity = null;
            CustomerDbModel customerDbModel = customerDbService.queryFirst("pkid", contactBean.getPkid());
            if (customerDbModel != null) {
                customerEntity = customerDbModel.convert2Model();
            }
            if (customerEntity == null) {
                customerEntity = new CustomerEntity();
            }
            customerEntity.pkid = contactBean.getPkid();
            customerEntity.name = contactBean.getName();
            customerEntity.contactType = contactBean.getContactType();
            customerEntity.sex = contactBean.getSex();
            customerEntity.impression = contactBean.getImpression();
            customerEntity.title = contactBean.getTitle();
            customerDbService.insertOrUpdate(customerEntity.convert2Model());
            EventBus.getDefault().post(new UpdateCustomerEvent(customerEntity));
        } else {
            EventBus.getDefault().post(new UpdateCustomerEvent(null));
        }
        finish();
    }

    private void setSelectGroupText() {
        if (groupBeanList != null) {
            if (groupBeanList.size() > 0) {
                StringBuffer stringBuffer = new StringBuffer();
                for (SelectGroupBean groupBean : groupBeanList) {
                    stringBuffer.append(groupBean.groupName + ",");
                }
                activityAddGroupContactGroupTextview.setText(stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
                if (!isAddOrEdit) {
                    updataGroup();
                }
            } else {
                activityAddGroupContactGroupTextview.setText("");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_GROUP_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                groupBeanList = (ArrayList<SelectGroupBean>) data.getSerializableExtra("groups");
                setSelectGroupText();
            }
        } else if (requestCode == SELECT_OTHER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String action = data.getAction();
                int position = data.getIntExtra("position", -1);
                String tagName = data.getStringExtra("tag");
                if (action.equals(Const.SELECT_ENTERPRISE_EMAIL_TAG_ACTION)) {//邮箱标签
                    ((TextView) emailMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_ENTERPRISE_ADDRESS_TAG_ACTION)) {//地址标签
                    ((TextView) addressMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_ENTERPRISE_PARPER_TAG_ACTION)) {//证件标签
                    ((TextView) paperMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_ENTERPRISE_DATE_TAG_ACTION)) {//日期标签
                    ((TextView) dateMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_ENTERPRISE_LIAISONS_TAG_ACTION)) {//选择联络人
                    addLiaisonsItemView((CustomerEntity) data.getSerializableExtra("customerEntity"));
                } else if (action.equals(Const.SELECT_RELATION_TAG_ACTION)) {
                    ((TextView) liaisonsMap.get(position).findViewById(R.id.activity_add_contact_liaisons_item_relation_text)).setText(tagName);
                    liaisonsList.get(position - 1).itemSubType = tagName;
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取修改团队json
     */
    private String getUpdateGroupJson() {
        try {
            if (groupBeanList != null) {
                if (groupBeanList.size() > 0) {
                    JSONObject jsonObject = new JSONObject();
                    if (contactDeatilBean != null) {
                        if (contactDeatilBean.getContact() != null) {
                            jsonObject.put("contactId", contactDeatilBean.getContact().getPkid());
                        }
                    }
                    JSONArray idArr = new JSONArray();
                    for (SelectGroupBean groupBean : groupBeanList) {
                        idArr.put(groupBean.groupId);
                    }
                    jsonObject.put("groupIds", idArr);
                    return jsonObject.toString();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改团队信息
     */
    private void updataGroup() {
        getApi().customerGroupInfoUpdate(RequestUtils.createJsonBody(getUpdateGroupJson())).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {

            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                showTopSnackBar("修改团队失败,请稍后重试。");
            }
        });
    }

    /**
     * 检测填写的姓名是否有重名
     */
    private void checkName(String name) {
        getApi().companyCheckReName(name).enqueue(new SimpleCallBack<JsonObject>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonObject>> call, Response<ResEntity<JsonObject>> response) {
                if (response.body().result != null) {
                    if (response.body().result.has("count")) {
                        int count = response.body().result.get("count").getAsInt();
                        if (count > 0) {
                            isCanAddContact = false;
                            showTopSnackBar(R.string.group_contact_warn_top_text);
                        }
                    }
                }
            }
        });
    }

    /**
     * 添加企业联系人
     */
    private void addContact() {
        showLoadingDialog(null);
        getApi().customerCompanyCreate(RequestUtils.createJsonBody(getAddContactJson())).enqueue(new SimpleCallBack<List<ContactDeatilBean>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<ContactDeatilBean>>> call, Response<ResEntity<List<ContactDeatilBean>>> response) {
                dismissLoadingDialog();
                List<ContactDeatilBean> list = response.body().result;
                if (list != null) {
                    newAddContactDetailBean = list.get(0);
                }
                if (newAddContactDetailBean != null) {
                    saveContactToDB();
                }
            }

            @Override
            public void onFailure(Call<ResEntity<List<ContactDeatilBean>>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 修改联系人
     */
    private void updateContact() {
        showLoadingDialog(null);
        getApi().customerUpdate(RequestUtils.createJsonBody(getUpdateContactJson())).enqueue(new SimpleCallBack<List<ContactDeatilBean>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<ContactDeatilBean>>> call, Response<ResEntity<List<ContactDeatilBean>>> response) {
                List<ContactDeatilBean> list = response.body().result;
                if (list != null) {
                    contactDeatilBean = list.get(0);
                }
                if (contactDeatilBean != null) {
                    saveContactToDB();
                }
            }

            @Override
            public void onFailure(Call<ResEntity<List<ContactDeatilBean>>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 获取负责团队列表
     */
    private void getGroupList() {
        getApi().lawyerGroupListQuery().enqueue(new SimpleCallBack<List<GroupBean>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<GroupBean>>> call, Response<ResEntity<List<GroupBean>>> response) {
                List<GroupBean> myGroups = response.body().result;
                StringBuffer stringBuffer = new StringBuffer();
                if (myGroups != null) {
                    if (myGroups.size() > 0) {
                        for (GroupBean groupBean : myGroups) {
                            stringBuffer.append(groupBean.getName() + ",");
                            SelectGroupBean selectGroupBean = new SelectGroupBean();
                            selectGroupBean.groupId = groupBean.getPkId();
                            selectGroupBean.groupName = groupBean.getName();
                            groupBeanList.add(selectGroupBean);
                        }
                        activityAddGroupContactGroupTextview.setText(stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
                    }
                }
            }
        });
    }

    /**
     * 获取企业联络人
     */
    private void getLiaisons(String id) {
        getApi().liaisonsQuery(id).enqueue(new SimpleCallBack<List<ContactDeatilBean>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<ContactDeatilBean>>> call, Response<ResEntity<List<ContactDeatilBean>>> response) {
                oldLiaisonsList = response.body().result;
                if (oldLiaisonsList != null) {
                    setLiaison(oldLiaisonsList);
                }
            }
        });
    }

    private void setLiaison(List<ContactDeatilBean> oldLiaisonsList) {
        if (oldLiaisonsList != null) {
            for (ContactDeatilBean deatilBean : oldLiaisonsList) {
                ContactDeatilBean.ContactBean contactBean = deatilBean.getContact();
                CustomerEntity p = new CustomerEntity();
                if (deatilBean.getCompanies() != null) {
                    if (deatilBean.getCompanies().size() > 0) {
                        p.pkid = deatilBean.getCompanies().get(0).getContactPkid();
                    } else {
                        p.pkid = contactBean.getPkid();
                    }
                } else {
                    p.pkid = contactBean.getPkid();
                }
                p.contactPkid = contactBean.getPkid();
                p.name = contactBean.getName();
                p.contactType = contactBean.getContactType();
                p.sex = contactBean.getSex();
                p.impression = contactBean.getImpression();
                p.title = contactBean.getTitle();
                if (deatilBean.getCompanies() != null) {
                    if (deatilBean.getCompanies().size() > 0) {
                        p.itemSubType = ((String) deatilBean.getCompanies().get(0).getItemSubType());
                    }
                }
                addLiaisonsItemView(p);
            }
        }
    }

    /**
     * 获取添加机构联系人json
     *
     * @return
     */
    private String getAddContactJson() {
        String json = null;
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject companyObject = new JSONObject();
            JSONObject contactObject = new JSONObject();
            if (contactDeatilBean != null) {
                contactObject.put("pkid", contactDeatilBean.getContact().getPkid());
            }
            contactObject.put("contactType", "C");
            contactObject.put("name", activityAddGroupContactEnterpriseNameEdittext.getText().toString().trim());
            contactObject.put("impression", activityAddGroupContactImpressionEdittext.getText().toString().trim());
            companyObject.put("contact", contactObject);
            if (addressMap.size() > 0) {
                JSONArray addreeArr = getItemMapJsonArr(addressMap, "addresses");
                companyObject.put("addresses", addreeArr);
            }
            if (emailMap.size() > 0) {
                JSONArray emailArr = getItemMapJsonArr(emailMap, "mails");
                if (emailArr != null)
                    companyObject.put("mails", emailArr);
                else
                    return "";

            }
            if (dateMap.size() > 0) {
                JSONArray dateArr = getItemMapJsonArr(dateMap, "dates");
                companyObject.put("dates", dateArr);
            }
            if (paperMap.size() > 0) {
                JSONArray paperArr = getItemMapJsonArr(paperMap, "certificates");
                companyObject.put("certificates", paperArr);
            }
            if (groupBeanList != null) {
                if (groupBeanList.size() > 0) {
                    JSONArray groupArr = new JSONArray();
                    for (SelectGroupBean groupBean : groupBeanList) {
                        JSONObject itemObject = new JSONObject();
                        itemObject.put("groupId", groupBean.groupId);
                        itemObject.put("groupName", groupBean.groupName);
                        groupArr.put(itemObject);
                    }
                    jsonObject.put("groups", groupArr);
                }
            }
            jsonObject.put("company", companyObject);
            if (liaisonsList != null) {
                if (liaisonsList.size() > 0) {
                    JSONArray addedPersonsArr = new JSONArray();
                    JSONArray addedCompaniesArr = new JSONArray();
                    for (CustomerEntity customerEntity : liaisonsList) {
                        if (TextUtils.equals("P", customerEntity.contactType)) {
                            JSONObject addedPersonsObject = new JSONObject();
                            JSONObject addedcontactObject = new JSONObject();
                            addedcontactObject.put("contactType", "P");
                            addedcontactObject.put("pkid", customerEntity.pkid);
                            addedPersonsObject.put("contact", addedcontactObject);

                            JSONArray companiesArr = new JSONArray();
                            JSONObject companiesObject = new JSONObject();
                            companiesObject.put("pkid", customerEntity.pkid);
                            companiesObject.put("resvTxt1", customerEntity.title);
                            companiesObject.put("itemSubType", customerEntity.itemSubType);
                            companiesObject.put("itemValue", activityAddGroupContactEnterpriseNameEdittext.getText().toString().trim());
                            companiesArr.put(companiesObject);
                            addedPersonsObject.put("companies", companiesArr);
                            addedPersonsArr.put(addedPersonsObject);
                        } else if (TextUtils.equals("C", customerEntity.contactType)) {
                            JSONObject parentObject = new JSONObject();
                            JSONObject companyChildObject = new JSONObject();
                            JSONObject addedcontactObject = new JSONObject();
                            addedcontactObject.put("contactType", "C");
                            addedcontactObject.put("pkid", customerEntity.pkid);
                            companyChildObject.put("contact", addedcontactObject);
                            parentObject.put("company", companyChildObject);

                            JSONArray companiesArr = new JSONArray();
                            JSONObject companiesObject = new JSONObject();
                            companiesObject.put("pkid", customerEntity.pkid);
                            companiesObject.put("resvTxt1", customerEntity.title);
                            companiesObject.put("itemSubType", customerEntity.itemSubType);
                            companiesObject.put("itemValue", activityAddGroupContactEnterpriseNameEdittext.getText().toString().trim());
                            companiesArr.put(companiesObject);
                            parentObject.put("persons", companiesArr);
                            addedCompaniesArr.put(parentObject);
                        }
                    }
                    if (addedPersonsArr.length() > 0)
                        jsonObject.put("addedPersons", addedPersonsArr);
                    if (addedCompaniesArr.length() > 0)
                        jsonObject.put("addedCompanies", addedCompaniesArr);
                }
            }


            json = jsonObject.toString();
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 获取修改联系人json
     *
     * @return
     */
    private String getUpdateContactJson() {
        String json = null;
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject contactObject = new JSONObject();
            if (contactDeatilBean != null) {
                contactObject.put("pkid", contactDeatilBean.getContact().getPkid());
            } else {
            }
            contactObject.put("name", activityAddGroupContactEnterpriseNameEdittext.getText().toString().trim());
            contactObject.put("contactType", "C");
            contactObject.put("impression", activityAddGroupContactImpressionEdittext.getText().toString().trim());

            jsonObject.put("contact", contactObject);
            if (addressMap.size() > 0) {
                JSONArray addreeArr = getItemMapJsonArr(addressMap, "addresses");
                jsonObject.put("addresses", addreeArr);
            }
            if (emailMap.size() > 0) {
                JSONArray emailArr = getItemMapJsonArr(emailMap, "mails");
                if (emailArr != null)
                    jsonObject.put("mails", emailArr);
                else
                    return "";
            }
            if (dateMap.size() > 0) {
                JSONArray dateArr = getItemMapJsonArr(dateMap, "dates");
                jsonObject.put("dates", dateArr);
            }
            if (paperMap.size() > 0) {
                JSONArray paperArr = getItemMapJsonArr(paperMap, "certificates");
                jsonObject.put("certificates", paperArr);
            }

            if (liaisonsList != null) {
                if (liaisonsList.size() > 0) {
                    JSONArray companiesArr = new JSONArray();
                    for (CustomerEntity customerEntity : liaisonsList) {
                        companiesArr.put(getAddLiaisonsJsonObject(customerEntity));
                    }
                    jsonObject.put("companies", companiesArr);
                }
            }
            if (groupBeanList != null) {
                if (groupBeanList.size() > 0) {
                    JSONArray groupArr = new JSONArray();
                    for (SelectGroupBean groupBean : groupBeanList) {
                        JSONObject itemObject = new JSONObject();
                        itemObject.put("groupId", groupBean.groupId);
                        itemObject.put("groupName", groupBean.groupName);
                        groupArr.put(itemObject);
                    }
                    jsonObject.put("groups", groupArr);
                }
            }

            json = jsonObject.toString();
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 获取添加联络人Json
     */
    private JSONObject getAddLiaisonsJsonObject(CustomerEntity customerEntity) {
        try {
            if (customerEntity != null) {
                JSONObject addedPersonsObject = new JSONObject();
                addedPersonsObject.put("contactPkid", customerEntity.pkid);
                addedPersonsObject.put("itemSubType", customerEntity.itemSubType);
                addedPersonsObject.put("itemType", "COMPANY");
                addedPersonsObject.put("resvTxt2", contactDeatilBean.getContact().getPkid());
                return addedPersonsObject;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取每个item的json
     *
     * @return
     */
    private JSONArray getItemMapJsonArr(Map<Integer, View> map, String type) {
        JSONArray addreeArr = new JSONArray();
        try {
            for (Map.Entry<Integer, View> entry : map.entrySet()) {
                String key = ((TextView) entry.getValue().findViewById(R.id.activity_add_contact_item_keyname_text)).getText().toString().trim();
                String value = ((TextView) entry.getValue().findViewById(R.id.activity_add_contact_item_valuename_text)).getText().toString().trim();
                if (value != null && value.length() > 0) {
                    if ("mails".equals(type)) {
                        if (!TextFormater.isMailNO(value)) {
                            showTopSnackBar("请输入正确的邮箱地址");
                            return null;
                        }
                    }
                    JSONObject itemObject = new JSONObject();
                    itemObject.put("itemSubType", key);
                    itemObject.put("itemValue", value);
                    addreeArr.put(itemObject);
                }
            }
            return addreeArr;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return addreeArr;
    }
}
