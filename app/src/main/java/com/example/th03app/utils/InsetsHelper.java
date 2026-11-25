package com.example.th03app.utils;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Từ Android 15 ứng dụng luôn vẽ tràn viền (edge-to-edge). Lớp này đẩy nội dung
 * tránh thanh trạng thái, thanh điều hướng và bàn phím ảo.
 */
public final class InsetsHelper {

    private InsetsHelper() {
    }

    /**
     * @param root   view gốc của màn hình
     * @param header phần đầu có nền màu, kéo dài lên phía sau thanh trạng thái (có thể null)
     */
    public static void applySystemBarInsets(View root, @Nullable View header) {
        // Giữ lại padding ban đầu trong XML rồi cộng thêm kích thước thanh hệ thống
        final int rootLeft = root.getPaddingLeft();
        final int rootTop = root.getPaddingTop();
        final int rootRight = root.getPaddingRight();
        final int rootBottom = root.getPaddingBottom();
        final int headerTop = header != null ? header.getPaddingTop() : 0;

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout()
                    | WindowInsetsCompat.Type.ime());
            if (header != null) {
                header.setPadding(header.getPaddingLeft(), headerTop + bars.top,
                        header.getPaddingRight(), header.getPaddingBottom());
                v.setPadding(rootLeft + bars.left, rootTop,
                        rootRight + bars.right, rootBottom + bars.bottom);
            } else {
                v.setPadding(rootLeft + bars.left, rootTop + bars.top,
                        rootRight + bars.right, rootBottom + bars.bottom);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
