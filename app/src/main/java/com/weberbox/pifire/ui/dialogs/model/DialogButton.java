package com.weberbox.pifire.ui.dialogs.model;

import com.weberbox.pifire.ui.dialogs.AbstractDialog;

public class DialogButton {
    private final String title;
    private final int icon;
    private final int colorStateList;
    private final AbstractDialog.OnClickListener onClickListener;

    public DialogButton(String title, int icon, int colorStateList,
                        AbstractDialog.OnClickListener onClickListener) {
        this.title = title;
        this.icon = icon;
        this.colorStateList = colorStateList;
        this.onClickListener = onClickListener;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public int getBackgroundColor() {
        return colorStateList;
    }

    public AbstractDialog.OnClickListener getOnClickListener() {
        return onClickListener;
    }
}
