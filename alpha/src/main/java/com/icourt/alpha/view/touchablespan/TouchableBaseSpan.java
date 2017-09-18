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

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TouchableBaseSpan extends ClickableSpan {

    public static final int TYPE_URL = 1;
    public static final int TYPE_PHONE = 2;

    @IntDef({TYPE_URL, TYPE_PHONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LinkType {

    }

    private Context mContext;

    private String mText;

    private int mLinkType;

    public boolean touched = false;

    public TouchableBaseSpan(Context context, @LinkType int linkType, String text) {
        this.mContext = context;
        this.mText = text;
        this.mLinkType = linkType;
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
//        ds.setFakeBoldText(link.isBold());
        ds.setColor(touched ? Color.BLUE : Color.GREEN);
        ds.bgColor = touched ? Color.BLUE : Color.TRANSPARENT;
//        if(link.getTypeface() != null)
//            ds.setTypeface(link.getTypeface());
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

        } else if (mLinkType == TYPE_PHONE) {

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

}
