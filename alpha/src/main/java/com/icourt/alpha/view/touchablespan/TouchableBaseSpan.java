/*
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.icourt.alpha.view.touchablespan;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.activity.WebViewActivity;
import com.icourt.alpha.utils.SnackbarUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.utils.ToastUtils;
import com.icourt.alpha.widget.dialog.AlertListDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class TouchableBaseSpan extends ClickableSpan {

    public static final int TYPE_URL = 1;
    public static final int TYPE_PHONE = 2;

    @IntDef({TYPE_URL, TYPE_PHONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LinkType {

    }

    private Context mContext;

    private String mText;

    private String mUserName;

    private int mLinkType;

    public boolean touched = false;

    public TouchableBaseSpan(Context context, @LinkType int linkType, String text, String userName) {
        this.mContext = context;
        this.mText = text;
        this.mLinkType = linkType;
        this.mUserName = userName;
    }

    /**
     * Draw the links background and set whether or not we want it to be underlined or bold
     *
     * @param ds the link
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setUnderlineText(false);
        ds.setColor(touched ? ContextCompat.getColor(mContext, R.color.alpha_font_color_orange) : ContextCompat.getColor(mContext, R.color.alpha_font_color_orange));
        ds.bgColor = touched ? ContextCompat.getColor(mContext, R.color.blanchedalmond) : Color.TRANSPARENT;
    }

    /**
     * This TouchableSpan has been clicked.
     *
     * @param widget TextView containing the touchable span
     */
    @Override
    public void onClick(View widget) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TouchableMovementMethod.touched = false;
            }
        }, 500);

        if (mLinkType == TYPE_URL) {
            WebViewActivity.launch(mContext, mText);
        } else if (mLinkType == TYPE_PHONE) {
            final List<String> menuList = new ArrayList<>();
            menuList.add(mContext.getString(R.string.str_call));
            menuList.add(mContext.getString(R.string.str_copy));
            menuList.add(mContext.getString(R.string.str_add_to_contacts));
            new AlertListDialog.ListBuilder(mContext)
                    .setDividerColorRes(R.color.alpha_divider_color)
                    .setDividerHeightRes(R.dimen.alpha_height_divider)
                    .setItems(menuList.toArray(new String[menuList.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String actionName = menuList.get(which);
                            if (TextUtils.equals(actionName, mContext.getString(R.string.str_call))) {
                                //跳转到拨号界面，同时传递电话号码
                                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mText));
                                mContext.startActivity(dialIntent);
                            } else if (TextUtils.equals(actionName, mContext.getString(R.string.str_copy))) {
                                msgActionCopy(mText);
                            } else if (TextUtils.equals(actionName, mContext.getString(R.string.str_add_to_contacts))) {
                                Intent intent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
                                intent.setType("vnd.android.cursor.dir/person");
                                intent.setType("vnd.android.cursor.item/contact");
                                intent.setType("vnd.android.cursor.dir/raw_contact");
                                if (!TextUtils.isEmpty(mUserName))
                                    intent.putExtra(android.provider.ContactsContract.Intents.Insert.NAME, mUserName);
                                if (!TextUtils.isEmpty(mText)) {
                                    intent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, mText);
                                }
                                mContext.startActivity(intent);
                            }
                        }
                    }).show();
        }
    }

    /**
     * This TouchableSpan has been long clicked.
     *
     * @param widget TextView containing the touchable span
     */
    public void onLongClick(View widget) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TouchableMovementMethod.touched = false;
            }
        }, 500);
    }

    public boolean isTouched() {
        return touched;
    }

    /**
     * Specifiy whether or not the link is currently touched
     *
     * @param touched
     */
    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    /**
     * 消息复制
     *
     * @param charSequence
     */
    private void msgActionCopy(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) return;
        SystemUtils.copyToClipboard(mContext, "msg", charSequence);
        if (mContext instanceof Activity) {
            SnackbarUtils.showTopSnackBar((Activity) mContext, mContext.getString(R.string.str_copy_success));
        } else {
            ToastUtils.showToast(mContext.getString(R.string.str_copy_success));
        }
    }
}
