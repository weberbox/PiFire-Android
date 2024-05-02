package com.weberbox.pifire.ui.dialogs.interfaces;

import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public interface DialogInputTextCallback {
    void onDialogConfirm(@Nullable String type, @NotNull String inputText);
}
