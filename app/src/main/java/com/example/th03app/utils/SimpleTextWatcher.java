package com.example.th03app.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * TextWatcher rút gọn: chỉ chạy một hành động mỗi khi nội dung ô nhập thay đổi.
 * Ví dụ: editText.addTextChangedListener(SimpleTextWatcher.onChanged(this::hideError));
 */
public final class SimpleTextWatcher implements TextWatcher {

    private final Runnable action;

    private SimpleTextWatcher(Runnable action) {
        this.action = action;
    }

    public static TextWatcher onChanged(Runnable action) {
        return new SimpleTextWatcher(action);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        action.run();
    }
}
