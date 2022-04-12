package com.weberbox.pifire.ui.dialogs.model;

import com.weberbox.pifire.ui.dialogs.AbstractDialog;

public class DialogSwipeButton {

    private final String text;
    private final boolean enabled;
    private final AbstractDialog.OnStateChangeListener onStateChangeListener;

    public DialogSwipeButton(String text, boolean enabled, AbstractDialog.OnStateChangeListener
            onStateChangeListener) {
        this.text = text;
        this.enabled = enabled;
        this.onStateChangeListener = onStateChangeListener;
    }

    public String getText() {
        return text;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public AbstractDialog.OnStateChangeListener getOnStateChangeListener() {
        return onStateChangeListener;
    }
}

