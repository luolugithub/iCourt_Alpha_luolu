package com.icourt.alpha.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.gson.JsonArray;
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

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description 添加个人联系人
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/18
 * version 2.0.0
 */

public class CustomerPersonCreateActivity extends BaseActivity {

    public static final int CREATE_CUSTOMER_ACTION = 1;//创建联系人
    public static final int UPDATE_CUSTOMER_ACTION = 2;//编辑联系人

    private static final int SELECT_GROUP_REQUEST = 1;//选择团队
    private static final int SELECT_OTHER_REQUEST = 2;//选择其他信息
    int action;
    boolean isAddOrEdit = false;//false:编辑 ; true:新建
    ContactDeatilBean contactDeatilBean, newAddContactDetailBean;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.activity_add_person_contact_name_textview)
    EditText activityAddPersonContactNameTextview;
    @BindView(R.id.activity_add_person_contact_sex_textview)
    TextView activityAddPersonContactSexTextview;
    @BindView(R.id.activity_add_person_contact_sex_arrow_image)
    ImageView activityAddPersonContactSexArrowImage;
    @BindView(R.id.activity_add_person_contact_sex_layout)
    LinearLayout activityAddPersonContactSexLayout;
    @BindView(R.id.activity_add_person_contact_oldname_textview)
    EditText activityAddPersonContactOldnameTextview;
    @BindView(R.id.activity_add_person_contact_group_textview)
    TextView activityAddPersonContactGroupTextview;
    @BindView(R.id.activity_add_person_contact_group_arrow_right_view)
    ImageView activityAddPersonContactGroupArrowRightView;
    @BindView(R.id.activity_add_person_contact_oldname_layout)
    LinearLayout activityAddPersonContactOldnameLayout;
    @BindView(R.id.activity_add_person_contact_add_oldname_layout)
    LinearLayout activityAddPersonContactAddOldnameLayout;
    @BindView(R.id.activity_add_person_contact_oldname_parent_layout)
    LinearLayout activityAddPersonContactOldnameParentLayout;
    @BindView(R.id.activity_add_person_contact_phone_layout)
    LinearLayout activityAddPersonContactPhoneLayout;
    @BindView(R.id.activity_add_person_contact_add_phone_layout)
    LinearLayout activityAddPersonContactAddPhoneLayout;
    @BindView(R.id.activity_add_person_contact_phone_parent_layout)
    LinearLayout activityAddPersonContactPhoneParentLayout;
    @BindView(R.id.activity_add_person_contact_email_layout)
    LinearLayout activityAddPersonContactEmailLayout;
    @BindView(R.id.activity_add_person_contact_add_email_layout)
    LinearLayout activityAddPersonContactAddEmailLayout;
    @BindView(R.id.activity_add_person_contact_email_parent_layout)
    LinearLayout activityAddPersonContactEmailParentLayout;
    @BindView(R.id.activity_add_person_contact_address_layout)
    LinearLayout activityAddPersonContactAddressLayout;
    @BindView(R.id.activity_add_person_contact_add_address_layout)
    LinearLayout activityAddPersonContactAddAddressLayout;
    @BindView(R.id.activity_add_person_contact_address_parent_layout)
    LinearLayout activityAddPersonContactAddressParentLayout;
    @BindView(R.id.activity_add_person_contact_enterprise_layout)
    LinearLayout activityAddPersonContactEnterpriseLayout;
    @BindView(R.id.activity_add_person_contact_add_enterprise_layout)
    LinearLayout activityAddPersonContactAddEnterpriseLayout;
    @BindView(R.id.activity_add_person_contact_enterprise_parent_layout)
    LinearLayout activityAddPersonContactEnterpriseParentLayout;
    @BindView(R.id.activity_add_person_contact_parer_layout)
    LinearLayout activityAddPersonContactParerLayout;
    @BindView(R.id.activity_add_person_contact_add_parer_layout)
    LinearLayout activityAddPersonContactAddParerLayout;
    @BindView(R.id.activity_add_person_contact_parer_parent_layout)
    LinearLayout activityAddPersonContactParerParentLayout;
    @BindView(R.id.activity_add_person_contact_im_layout)
    LinearLayout activityAddPersonContactImLayout;
    @BindView(R.id.activity_add_person_contact_add_im_layout)
    LinearLayout activityAddPersonContactAddImLayout;
    @BindView(R.id.activity_add_person_contact_im_parent_layout)
    LinearLayout activityAddPersonContactImParentLayout;
    @BindView(R.id.activity_add_person_contact_date_layout)
    LinearLayout activityAddPersonContactDateLayout;
    @BindView(R.id.activity_add_person_contact_add_date_layout)
    LinearLayout activityAddPersonContactAddDateLayout;
    @BindView(R.id.activity_add_person_contact_date_parent_layout)
    LinearLayout activityAddPersonContactDateParentLayout;
    @BindView(R.id.activity_add_person_contact_liaisons_layout)
    LinearLayout activityAddPersonContactLiaisonsLayout;
    @BindView(R.id.activity_add_person_contact_add_liaisons_layout)
    LinearLayout activityAddPersonContactAddLiaisonsLayout;
    @BindView(R.id.activity_add_contact_liaisons_parent_layout)
    LinearLayout activityAddContactLiaisonsParentLayout;
    @BindView(R.id.activity_add_person_contact_impress_edittext)
    EditText activityAddPersonContactImpressEdittext;

    private LayoutInflater layoutInflater;
    private LinkedHashMap<Integer, View> oldNameMap, phoneMap, addressMap, emailMap, paperMap, imAccountMap, dateMap, enterpriseMap, liaisonsMap;
    private int oldNameKey = 0, phoneKey = 0, addressKey = 0, emailKey = 0, paperKey = 0, imAccountKey = 0, dateKey = 0, enterpriseKey = 0, liaisonsKey = 0;
    private final String[] sexStrArr = {"男", "女"};
    private ArrayList<SelectGroupBean> groupBeanList;
    private ArrayList<CustomerEntity> liaisonsList;
    private List<ContactDeatilBean> oldLiaisonsList;
    CustomerDbService customerDbService;


    @IntDef({CREATE_CUSTOMER_ACTION,
            UPDATE_CUSTOMER_ACTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CUSTOMER_ACTION {

    }

    public static void launch(@NonNull Context context, @NonNull ContactDeatilBean contactDeatilBean, @CUSTOMER_ACTION int action) {
        if (context == null) return;
        Intent intent = new Intent(context, CustomerPersonCreateActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("contactDeatilBean", contactDeatilBean);
        context.startActivity(intent);
    }

    /**
     * 选择负责团队
     *
     * @param activity
     * @param groupBeenList
     */
    public static void launchSetResultFromGroup(@NonNull Activity activity, @NonNull List<SelectGroupBean> groupBeenList) {
        if (activity == null) return;
        Intent intent = new Intent(activity, CustomerPersonCreateActivity.class);
        intent.putExtra("groupBeenList", (Serializable) groupBeenList);
        activity.setResult(RESULT_OK, intent);
    }

    /**
     * 选择联络人
     *
     * @param activity
     * @param action
     * @param customerEntity
     */
    public static void launchSetResultFromLiaison(@NonNull Activity activity, @NonNull String action, @NonNull CustomerEntity customerEntity) {
        if (activity == null) return;
        Intent intent = new Intent(activity, CustomerPersonCreateActivity.class);
        intent.setAction(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("customerEntity", customerEntity);
        activity.setResult(RESULT_OK, intent);
    }

    /**
     * 选择属性tag标签
     *
     * @param activity
     * @param action
     * @param position
     * @param tagName
     */
    public static void launchSetResultFromTag(@NonNull Activity activity, @NonNull String action, @NonNull int position, @NonNull String tagName) {
        if (activity == null) return;
        Intent intent = new Intent(activity, CustomerPersonCreateActivity.class);
        intent.setAction(action);
        intent.putExtra("position", position);
        intent.putExtra("tag", tagName);
        activity.setResult(RESULT_OK, intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_contact_create_layout);
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
        oldNameMap = new LinkedHashMap<>();
        addressMap = new LinkedHashMap<>();
        enterpriseMap = new LinkedHashMap<>();
        phoneMap = new LinkedHashMap<>();
        emailMap = new LinkedHashMap<>();
        paperMap = new LinkedHashMap<>();
        imAccountMap = new LinkedHashMap<>();
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
    }

    //新建联系人view
    private void createCustomerSetData() {
        isAddOrEdit = true;
        setTitle("新建个人联系人");
        if (groupBeanList == null) {
            groupBeanList = new ArrayList<>();
        }
        getGroupList();
    }

    //编辑联系人view
    private void updateCustomerSetData() {
        if (contactDeatilBean != null) {
            isAddOrEdit = false;
            if (contactDeatilBean.getContact() != null) {
                setTitle(contactDeatilBean.getContact().getName());
                getLiaisons(contactDeatilBean.getContact().getPkid());
                activityAddPersonContactNameTextview.setText(contactDeatilBean.getContact().getName());
                if ("P".equals(contactDeatilBean.getContact().getContactType())) {
                    activityAddPersonContactSexTextview.setText(contactDeatilBean.getContact().getSex());
                }
                if (!TextUtils.isEmpty(contactDeatilBean.getContact().getImpression())) {
                    activityAddPersonContactImpressEdittext.setText(contactDeatilBean.getContact().getImpression());
                }
            }
            if (contactDeatilBean.getGroups() != null) {
                if (contactDeatilBean.getGroups().size() > 0) {
                    if (groupBeanList == null) {
                        groupBeanList = new ArrayList<>();
                    }
                    StringBuffer stringBuffer = new StringBuffer();
                    for (ContactDeatilBean.GroupsBean groupBean : contactDeatilBean.getGroups()) {
                        stringBuffer.append(groupBean.getGroupName() + ",");
                        SelectGroupBean g = new SelectGroupBean();
                        g.groupId = (groupBean.getGroupId());
                        g.groupName = (groupBean.getGroupName());
                        groupBeanList.add(g);
                    }
                    activityAddPersonContactGroupTextview.setText(stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
                } else {
                    activityAddPersonContactGroupTextview.setText("");
                }
            } else {
                activityAddPersonContactGroupTextview.setText("");
            }
            if (contactDeatilBean.getUsedNames() != null) {
                for (ContactDeatilBean.UsedNamesBean usedNamesBean : contactDeatilBean.getUsedNames()) {
                    addOldNameItemView(usedNamesBean);
                }
            }

            if (contactDeatilBean.getAddresses() != null) {
                for (ContactDeatilBean.AddressesBean addressesBean : contactDeatilBean.getAddresses()) {
                    addAddressItemView(addressesBean);
                }
            }

            if (contactDeatilBean.getTels() != null) {
                for (ContactDeatilBean.TelsBean telsBean : contactDeatilBean.getTels()) {
                    addPhoneItemView(telsBean);
                }
            }

            if (contactDeatilBean.getMails() != null) {
                for (ContactDeatilBean.MailsBean mailsBean : contactDeatilBean.getMails()) {
                    addEmailView(mailsBean);
                }
            }
            if (contactDeatilBean.getCompanies() != null) {
                for (ContactDeatilBean.CompaniesBean companiesBean : contactDeatilBean.getCompanies()) {
                    addEnterpriseView(companiesBean);
                }
            }
            if (contactDeatilBean.getCertificates() != null) {
                for (ContactDeatilBean.CertificatesBean certificatesBean : contactDeatilBean.getCertificates()) {
                    addParerView(certificatesBean);
                }
            }

            if (contactDeatilBean.getIms() != null) {
                for (ContactDeatilBean.ImsBean imsBean : contactDeatilBean.getIms()) {
                    addImAccountView(imsBean);
                }
            }

            if (contactDeatilBean.getDates() != null) {
                for (ContactDeatilBean.DateBean dateBean : contactDeatilBean.getDates()) {
                    addDateView(dateBean);
                }
            }
        }
    }

    @OnClick({R.id.titleAction, R.id.activity_add_person_contact_sex_layout,
            R.id.activity_add_person_contact_group_textview, R.id.activity_add_person_contact_add_oldname_layout,
            R.id.activity_add_person_contact_add_address_layout, R.id.activity_add_person_contact_add_phone_layout,
            R.id.activity_add_person_contact_add_email_layout, R.id.activity_add_person_contact_add_enterprise_layout,
            R.id.activity_add_person_contact_add_parer_layout, R.id.activity_add_person_contact_add_im_layout,
            R.id.activity_add_person_contact_add_date_layout, R.id.activity_add_person_contact_add_liaisons_layout})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.titleAction://完成

                if (action == UPDATE_CUSTOMER_ACTION) {
                    if (contactDeatilBean != null) {
                        updateContact();
                    }
                } else if (action == CREATE_CUSTOMER_ACTION) {
                    String name = activityAddPersonContactNameTextview.getText().toString().trim();
                    if (!TextUtils.isEmpty(name)) {
                        addContact();
                    } else {
                        showTopSnackBar("请填写联系人名称");
                    }
                }

                break;
            case R.id.activity_add_person_contact_sex_layout://选择性别
                change_sex();
                break;
            case R.id.activity_add_person_contact_group_textview://选择团队
                GroupSelectActivity.launchForResult(CustomerPersonCreateActivity.this, groupBeanList, SELECT_GROUP_REQUEST);
                break;
            case R.id.activity_add_person_contact_add_oldname_layout://增加曾用名
                addOldNameItemView(null);
                break;
            case R.id.activity_add_person_contact_add_address_layout://增加地址
                addAddressItemView(null);
                break;
            case R.id.activity_add_person_contact_add_phone_layout://增加电话
                addPhoneItemView(null);
                break;
            case R.id.activity_add_person_contact_add_email_layout://增加邮箱
                addEmailView(null);
                break;
            case R.id.activity_add_person_contact_add_enterprise_layout://增加工作单位
                addEnterpriseView(null);
                break;
            case R.id.activity_add_person_contact_add_parer_layout://增加个人证件
                addParerView(null);
                break;
            case R.id.activity_add_person_contact_add_im_layout://增加即时通讯帐号
                addImAccountView(null);
                break;
            case R.id.activity_add_person_contact_add_date_layout://增加重要日期
                addDateView(null);
                break;
            case R.id.activity_add_person_contact_add_liaisons_layout://增加联络人
                String pkid = null;
                if (!isAddOrEdit) {
                    pkid = contactDeatilBean.getContact().getPkid();
                }
                SelectLiaisonActivity.launchForResult(this, Const.SELECT_LIAISONS_TAG_ACTION, pkid, liaisonsList, SELECT_OTHER_REQUEST);
                break;
        }
    }

    /**
     * 选择性别
     */
    public void change_sex() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //定义一个AlertDialog

        builder.setItems(sexStrArr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                activityAddPersonContactSexTextview.setText(sexStrArr[arg1]);
            }
        });
        builder.show();
    }

    /**
     * 添加曾用名itemview
     */
    private void addOldNameItemView(final ContactDeatilBean.UsedNamesBean usedNameBean) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        ImageView arrowBottom = (ImageView) view.findViewById(R.id.activity_add_contact_item_arrow_bottom);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
        EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);
        valuenameText.setFocusableInTouchMode(true);
        valuenameText.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        oldNameKey += 1;
        oldNameMap.put(oldNameKey, view);
        final int addressTag = oldNameKey;
        deleteView.setVisibility(View.VISIBLE);

        keynameText.setText("曾用名");
        if (usedNameBean != null) {
            valuenameText.setText(usedNameBean.getItemValue());
        }
        activityAddPersonContactOldnameLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactOldnameLayout.removeView(view);
                oldNameMap.remove(addressTag);
                if (usedNameBean != null) {
                    contactDeatilBean.getUsedNames().remove(usedNameBean);
                }
            }
        });
        arrowBottom.setVisibility(View.GONE);

    }

    /**
     * 添加地址itemview
     */
    private void addAddressItemView(final ContactDeatilBean.AddressesBean addressBean) {
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
        if (addressBean != null) {
            keynameText.setText(addressBean.getItemSubType());
            valuenameText.setText(addressBean.getItemValue());
        } else {
            keynameText.setText("工作");
        }
        activityAddPersonContactAddressLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactAddressLayout.removeView(view);
                addressMap.remove(addressTag);
                if (addressBean != null) {
                    contactDeatilBean.getAddresses().remove(addressBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectCustomerTagActivity.launchForResult(CustomerPersonCreateActivity.this, Const.SELECT_ADDRESS_TAG_ACTION, addressTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST, SelectCustomerTagActivity.PERSON_SELECT_TYPE);
            }
        });
    }

    /**
     * 添加电话itemview
     */
    private void addPhoneItemView(final ContactDeatilBean.TelsBean telBean) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
//        ImageView arrowBottom = (ImageView) view.findViewById(R.id.activity_add_contact_item_arrow_bottom);
        EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);
        valuenameText.setFocusableInTouchMode(true);
        valuenameText.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        phoneKey += 1;
        phoneMap.put(phoneKey, view);
        final int phoneTag = phoneKey;
        deleteView.setVisibility(View.VISIBLE);
        if (telBean != null) {
            keynameText.setText(telBean.getItemSubType());
            valuenameText.setText(telBean.getItemValue());
        } else {
            keynameText.setText("主要");
        }
        activityAddPersonContactPhoneLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactPhoneLayout.removeView(view);
                phoneMap.remove(phoneTag);
                if (telBean != null) {
                    contactDeatilBean.getTels().remove(telBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCustomerTagActivity.launchForResult(CustomerPersonCreateActivity.this, Const.SELECT_PHONE_TAG_ACTION, phoneTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST, SelectCustomerTagActivity.PERSON_SELECT_TYPE);
            }
        });
    }

    /**
     * 添加邮箱itemview
     */
    private void addEmailView(final ContactDeatilBean.MailsBean mailBean) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
        ImageView arrowBottom = (ImageView) view.findViewById(R.id.activity_add_contact_item_arrow_bottom);
        EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);
        valuenameText.setFocusableInTouchMode(true);
        valuenameText.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        emailKey += 1;
        emailMap.put(emailKey, view);
        final int emailTag = emailKey;
        deleteView.setVisibility(View.VISIBLE);
        if (mailBean != null) {
            keynameText.setText(mailBean.getItemSubType());
            valuenameText.setText(mailBean.getItemValue());
        } else {
            keynameText.setText("主要");
        }
        activityAddPersonContactEmailLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactEmailLayout.removeView(view);
                emailMap.remove(emailTag);
                if (mailBean != null) {
                    contactDeatilBean.getMails().remove(mailBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCustomerTagActivity.launchForResult(CustomerPersonCreateActivity.this, Const.SELECT_EMAIL_TAG_ACTION, emailTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST, SelectCustomerTagActivity.PERSON_SELECT_TYPE);
            }
        });
    }

    /**
     * 添加工作单位itemview
     */
    private void addEnterpriseView(final ContactDeatilBean.CompaniesBean company) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
        ImageView arrowBottom = (ImageView) view.findViewById(R.id.activity_add_contact_item_arrow_bottom);
        EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);
        valuenameText.setFocusableInTouchMode(true);
        valuenameText.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        enterpriseKey += 1;
        enterpriseMap.put(enterpriseKey, view);
        final int enterpriseTag = enterpriseKey;
        deleteView.setVisibility(View.VISIBLE);
        if (company != null) {
            keynameText.setText((String) company.getItemSubType());
            valuenameText.setText((String) company.getItemValue());
        } else {
            keynameText.setText("主要");
        }
        activityAddPersonContactEnterpriseLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactEnterpriseLayout.removeView(view);
                enterpriseMap.remove(enterpriseTag);
                if (company != null) {
                    contactDeatilBean.getCompanies().remove(company);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCustomerTagActivity.launchForResult(CustomerPersonCreateActivity.this, Const.SELECT_ENTERPRISE_TAG_ACTION, enterpriseTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST, SelectCustomerTagActivity.PERSON_SELECT_TYPE);
            }
        });
    }

    /**
     * 添加证件itemview
     */
    private void addParerView(final ContactDeatilBean.CertificatesBean certificate) {
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
        if (certificate != null) {
            keynameText.setText(certificate.getItemSubType());
            valuenameText.setText(certificate.getItemValue());
        } else {
            keynameText.setText("身份证");
        }
        activityAddPersonContactParerLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactParerLayout.removeView(view);
                paperMap.remove(paperTag);
                if (certificate != null) {
                    contactDeatilBean.getCertificates().remove(certificate);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCustomerTagActivity.launchForResult(CustomerPersonCreateActivity.this, Const.SELECT_PAPERS_TAG_ACTION, paperTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST, SelectCustomerTagActivity.PERSON_SELECT_TYPE);
            }
        });
    }

    /**
     * 添加即时通讯itemview
     */
    private void addImAccountView(final ContactDeatilBean.ImsBean imBean) {
        final View view = layoutInflater.inflate(R.layout.add_contact_item_layout, null);
        ImageView deleteView = (ImageView) view.findViewById(R.id.activity_add_contact_item_delete_view);
        final TextView keynameText = (TextView) view.findViewById(R.id.activity_add_contact_item_keyname_text);
        EditText valuenameText = (EditText) view.findViewById(R.id.activity_add_contact_item_valuename_text);
        valuenameText.setFocusableInTouchMode(true);
        valuenameText.requestFocus();
        SystemUtils.showSoftKeyBoard(this);
        imAccountKey += 1;
        imAccountMap.put(imAccountKey, view);
        final int imAccountTag = imAccountKey;
        deleteView.setVisibility(View.VISIBLE);
        if (imBean != null) {
            keynameText.setText(imBean.getItemSubType());
            valuenameText.setText(imBean.getItemValue());
        } else {
            keynameText.setText("微信");
        }
        activityAddPersonContactImLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactImLayout.removeView(view);
                imAccountMap.remove(imAccountTag);
                if (imBean != null) {
                    contactDeatilBean.getIms().remove(imBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCustomerTagActivity.launchForResult(CustomerPersonCreateActivity.this, Const.SELECT_IM_TAG_ACTION, imAccountTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST, SelectCustomerTagActivity.PERSON_SELECT_TYPE);
            }
        });
    }

    /**
     * 添加重要日期itemview
     */
    private void addDateView(final ContactDeatilBean.DateBean dateBean) {
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
            keynameText.setText("生日");
        }
        activityAddPersonContactDateLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactDateLayout.removeView(view);
                dateMap.remove(dateTag);
                if (dateBean != null) {
                    contactDeatilBean.getDates().remove(dateBean);
                }
            }
        });
        keynameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCustomerTagActivity.launchForResult(CustomerPersonCreateActivity.this, Const.SELECT_DATE_TAG_ACTION, dateTag, keynameText.getText().toString().trim(), SELECT_OTHER_REQUEST, SelectCustomerTagActivity.PERSON_SELECT_TYPE);
            }
        });
        valuenameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtils.hideSoftKeyBoard(CustomerPersonCreateActivity.this);
                showDatePicker(valuenameText);
            }
        });
    }

    /**
     * 添加联络人itemview
     */
    private void addLiaisonsItemView(final CustomerEntity customerEntity) {
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
        activityAddPersonContactLiaisonsLayout.addView(view);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityAddPersonContactLiaisonsLayout.removeView(view);
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
                SelectCustomerTagActivity.launchForResult(CustomerPersonCreateActivity.this, Const.SELECT_RELATION_TAG_ACTION, liaisonsList.indexOf(customerEntity) + 1, null, SELECT_OTHER_REQUEST, SelectCustomerTagActivity.PERSON_SELECT_TYPE);
            }
        });
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

    private void setSelectGroupText() {
        if (groupBeanList != null) {
            if (groupBeanList.size() > 0) {
                StringBuffer stringBuffer = new StringBuffer();
                for (SelectGroupBean groupBean : groupBeanList) {
                    stringBuffer.append(groupBean.groupName + ",");
                }
                activityAddPersonContactGroupTextview.setText(stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
                if (!isAddOrEdit) {
                    updataGroup();
                }
            } else {
                activityAddPersonContactGroupTextview.setText("");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_GROUP_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                groupBeanList = (ArrayList<SelectGroupBean>) data.getSerializableExtra("groupBeenList");
                setSelectGroupText();
            }
        } else if (requestCode == SELECT_OTHER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String action = data.getAction();
                int position = data.getIntExtra("position", -1);
                String tagName = data.getStringExtra("tag");
                if (action.equals(Const.SELECT_PHONE_TAG_ACTION)) {//电话标签
                    ((TextView) phoneMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_EMAIL_TAG_ACTION)) {//邮箱标签
                    ((TextView) emailMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_ADDRESS_TAG_ACTION)) {//地址标签
                    ((TextView) addressMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_PAPERS_TAG_ACTION)) {//证件标签
                    ((TextView) paperMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_IM_TAG_ACTION)) {//即时通讯标签
                    ((TextView) imAccountMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_DATE_TAG_ACTION)) {//日期标签
                    ((TextView) dateMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_ENTERPRISE_TAG_ACTION)) {//工作单位标签
                    ((TextView) enterpriseMap.get(position).findViewById(R.id.activity_add_contact_item_keyname_text)).setText(tagName);
                } else if (action.equals(Const.SELECT_LIAISONS_TAG_ACTION)) {//选择联络人
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
                            selectGroupBean.groupName = groupBean.getName();
                            selectGroupBean.groupId = groupBean.getPkId();
                            groupBeanList.add(selectGroupBean);
                        }
                        activityAddPersonContactGroupTextview.setText(stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
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
     * 更新数据库
     */
    private void saveContactToDB() {
        dismissLoadingDialog();
        ContactDeatilBean.ContactBean contactBean = null;
        if (newAddContactDetailBean != null) {
            contactBean = newAddContactDetailBean.getContact();
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
            finish();
        }
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
                showTopSnackBar("修改团队失败");
            }
        });
    }

    /**
     * 修改联系人
     */
    private void updateContact() {
        String json = getUpdateContactJson();
        if (TextUtils.isEmpty(json)) return;
        showLoadingDialog(null);
        getApi().customerUpdate(RequestUtils.createJsonBody(json)).enqueue(new SimpleCallBack<List<ContactDeatilBean>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<ContactDeatilBean>>> call, Response<ResEntity<List<ContactDeatilBean>>> response) {
                dismissLoadingDialog();
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
     * 添加联系人
     */
    private void addContact() {
        showLoadingDialog(null);
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        requestBodyMap.put("contactvo", RequestUtils.createJsonBody(getAddContactJson()));
        getApi().customerCreate(RequestUtils.createJsonBody(getAddContactJson())).enqueue(new SimpleCallBack<List<ContactDeatilBean>>() {
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
     * 获取修改团队json
     */
    private String getUpdateGroupJson() {
        if (groupBeanList != null) {
            if (groupBeanList.size() > 0) {
                JsonObject jsonObject = new JsonObject();
                if (contactDeatilBean != null) {
                    if (contactDeatilBean.getContact() != null) {
                        jsonObject.addProperty("contactId", contactDeatilBean.getContact().getPkid());
                    }
                }
                JsonArray jsonArray = new JsonArray();
                for (SelectGroupBean groupBean : groupBeanList) {
                    jsonArray.add(groupBean.groupId);
                }
                jsonObject.add("groupIds", jsonArray);
                return jsonObject.toString();
            }
        }
        return null;
    }

    /**
     * 获取添加个人联系人json
     *
     * @return
     */
    private String getAddContactJson() {
        String json = null;
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject contactObject = new JSONObject();
            if (contactDeatilBean != null) {
                contactObject.put("pkid", contactDeatilBean.getContact().getPkid());
            }
            contactObject.put("contactType", "P");
            contactObject.put("name", activityAddPersonContactNameTextview.getText().toString().trim());
            contactObject.put("sex", activityAddPersonContactSexTextview.getText().toString().trim());
            contactObject.put("impression", activityAddPersonContactImpressEdittext.getText().toString().trim());
            jsonObject.put("contact", contactObject);
            if (oldNameMap.size() > 0) {
                JSONArray oldNameArr = getItemMapJsonArr(oldNameMap, "usedNames");
                jsonObject.put("usedNames", oldNameArr);
            }
            if (addressMap.size() > 0) {
                JSONArray addreeArr = getItemMapJsonArr(addressMap, "addresses");
                jsonObject.put("addresses", addreeArr);
            }
            if (phoneMap.size() > 0) {

                JSONArray phoneArr = getItemMapJsonArr(phoneMap, "tels");
                if (phoneArr != null) {
                    jsonObject.put("tels", phoneArr);
                } else
                    return "";

            }
            if (emailMap.size() > 0) {
                JSONArray emailArr = getItemMapJsonArr(emailMap, "mails");
                if (emailArr != null)
                    jsonObject.put("mails", emailArr);
                else
                    return "";
            }
            if (imAccountMap.size() > 0) {
                JSONArray imArr = getItemMapJsonArr(imAccountMap, "ims");
                if (imArr != null)
                    jsonObject.put("ims", imArr);

            }
            if (dateMap.size() > 0) {
                JSONArray dateArr = getItemMapJsonArr(dateMap, "dates");
                if (dateArr != null)
                    jsonObject.put("dates", dateArr);
            }
            if (paperMap.size() > 0) {
                JSONArray paperArr = getItemMapJsonArr(paperMap, "certificates");
                if (paperArr != null)
                    jsonObject.put("certificates", paperArr);
            }
            if (enterpriseMap.size() > 0) {
                JSONArray addreeArr = getItemMapJsonArr(enterpriseMap, "companies");
                if (addreeArr != null)
                    jsonObject.put("companies", addreeArr);

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
                            companiesObject.put("itemValue", activityAddPersonContactNameTextview.getText().toString().trim());
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
                            companiesObject.put("itemValue", activityAddPersonContactNameTextview.getText().toString().trim());
                            companiesArr.put(companiesObject);
                            parentObject.put("persons", companiesArr);
                            addedCompaniesArr.put(parentObject);
                        }
                    }
                    jsonObject.put("addedPersons", addedPersonsArr);
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
     * 获取更新个人联系人json
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
            }
            contactObject.put("contactType", "P");
            contactObject.put("name", activityAddPersonContactNameTextview.getText().toString().trim());
            contactObject.put("sex", activityAddPersonContactSexTextview.getText().toString().trim());
            contactObject.put("impression", activityAddPersonContactImpressEdittext.getText().toString().trim());
            jsonObject.put("contact", contactObject);
            if (oldNameMap.size() > 0) {
                JSONArray oldNameArr = getItemMapJsonArr(oldNameMap, "usedNames");
                jsonObject.put("usedNames", oldNameArr);
            }
            if (addressMap.size() > 0) {
                JSONArray addreeArr = getItemMapJsonArr(addressMap, "addresses");
                jsonObject.put("addresses", addreeArr);
            }
            if (phoneMap.size() > 0) {

                JSONArray phoneArr = getItemMapJsonArr(phoneMap, "tels");
                if (phoneArr != null) {
                    jsonObject.put("tels", phoneArr);
                } else return "";

            }
            if (emailMap.size() > 0) {
                JSONArray emailArr = getItemMapJsonArr(emailMap, "mails");
                if (emailArr != null)
                    jsonObject.put("mails", emailArr);
                else return "";
            }
            if (imAccountMap.size() > 0) {
                JSONArray imArr = getItemMapJsonArr(imAccountMap, "ims");
                if (imArr != null)
                    jsonObject.put("ims", imArr);

            }
            if (dateMap.size() > 0) {
                JSONArray dateArr = getItemMapJsonArr(dateMap, "dates");
                if (dateArr != null)
                    jsonObject.put("dates", dateArr);
            }
            if (paperMap.size() > 0) {
                JSONArray paperArr = getItemMapJsonArr(paperMap, "certificates");
                if (paperArr != null)
                    jsonObject.put("certificates", paperArr);
            }
            if (enterpriseMap.size() > 0) {
                JSONArray addreeArr = getItemMapJsonArr(enterpriseMap, "companies");
                if (liaisonsList != null) {
                    if (liaisonsList.size() > 0) {
                        for (CustomerEntity customerEntity : liaisonsList) {
                            addreeArr.put(getAddLiaisonsJsonObject(customerEntity));
                        }
                    }
                }
                if (addreeArr != null)
                    jsonObject.put("companies", addreeArr);

            } else {
                if (liaisonsList != null) {
                    if (liaisonsList.size() > 0) {
                        JSONArray companiesArr = new JSONArray();
                        for (CustomerEntity customerEntity : liaisonsList) {
                            companiesArr.put(getAddLiaisonsJsonObject(customerEntity));
                        }
                        jsonObject.put("companies", companiesArr);
                    }
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
                    if ("tels".equals(type)) {
                        if (!TextFormater.isMobileNO(value)) {
                            showTopSnackBar("请输入正确的手机号号码");
                            return null;
                        }
                    } else if ("mails".equals(type)) {
                        if (!TextFormater.isMailNO(value)) {
                            showTopSnackBar("请输入正确的邮箱地址");
                            return null;
                        }
                    }
                    JSONObject itemObject = new JSONObject();
                    itemObject.put("itemSubType", key);
                    itemObject.put("itemValue", value);
                    if (contactDeatilBean != null) {
                        if (contactDeatilBean.getContact() != null) {
                            itemObject.put("contactPkid", contactDeatilBean.getContact().getPkid());
                        }
                    }
                    addreeArr.put(itemObject);
                }
            }
            return addreeArr;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return addreeArr;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customerDbService != null) {
            customerDbService.releaseService();
        }
    }
}
