package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.LayoutNavItemBinding;

@SuppressWarnings("unused")
public class NavListItem extends RelativeLayout {

    private TextView navTitleTv;
    private ImageView navIconIv;
    private View navSelectedV;
    private boolean adjustText;

    public NavListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NavListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public NavListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutNavItemBinding binding = LayoutNavItemBinding.inflate(
                LayoutInflater.from(context), this, true);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavListItem);

            String navTitle = a.getString(R.styleable.NavListItem_nav_title);
            int navIcon = a.getResourceId(R.styleable.NavListItem_nav_icon, R.drawable.ic_grill);
            boolean navSelected = a.getBoolean(R.styleable.NavListItem_nav_selected, false);
            adjustText = a.getBoolean(R.styleable.NavListItem_nav_adjust_text, false);

            navTitleTv = binding.navItemTitle;
            navIconIv = binding.navItemIcon;
            navSelectedV = binding.navItemSelectedIndicator;

            navTitleTv.setText(navTitle != null ? navTitle : "");
            navIconIv.setImageResource(navIcon);

            setNavSelected(navSelected);

            a.recycle();
        }
    }

    public String getNavTitle() {
        return navTitleTv.getText().toString();
    }

    public void setNavTitle(String text) {
        navTitleTv.setText(text);
    }

    public void setNavIcon(int icon) {
        navIconIv.setImageResource(icon);
    }

    public void setNavSelected(boolean navSelected) {
        if (navSelected) {
            if (navSelectedV.getVisibility() == INVISIBLE) navSelectedV.setVisibility(VISIBLE);
            if (adjustText) {
                navIconIv.setAlpha(1f);
                navTitleTv.setAlpha(1f);
                navTitleTv.setTypeface(null, Typeface.BOLD);
            }
        } else {
            if (navSelectedV.getVisibility() == VISIBLE) navSelectedV.setVisibility(INVISIBLE);
            if (adjustText) {
                navIconIv.setAlpha(0.7f);
                navTitleTv.setAlpha(0.7f);
                navTitleTv.setTypeface(null, Typeface.NORMAL);
            } else {
                navTitleTv.setTypeface(null, Typeface.BOLD);
            }
        }
    }
}
