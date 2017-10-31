package com.icourt.alpha.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.blog.www.guideview.Component;
import com.icourt.alpha.R;


/**
 * Created by binIoter on 16/6/17.
 */
public class SimpleComponent implements Component {

    private OnViewClick onViewClick;

    @Override
    public View getView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.layout_timing_guide, null);
        view.findViewById(R.id.kown_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClick != null) {
                    onViewClick.onClick(v);
                }
            }
        });
        return view;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_TOP;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_END;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return -3;
    }


    public interface OnViewClick {
        void onClick(View view);
    }

    public void setOnViewClick(OnViewClick onViewClick) {
        this.onViewClick = onViewClick;
    }
}
