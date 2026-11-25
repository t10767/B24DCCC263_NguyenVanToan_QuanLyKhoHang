package com.example.th03app.utils;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.example.th03app.R;

/**
 * Icon và màu sắc riêng cho từng loại sản phẩm.
 */
public final class CategoryStyle {

    @DrawableRes
    public final int iconRes;
    @ColorRes
    public final int colorRes;
    @ColorRes
    public final int lightColorRes;

    /** Cùng thứ tự với R.array.product_categories: Điện tử, Thực phẩm, Văn phòng phẩm, Gia dụng, Khác */
    private static final CategoryStyle[] STYLES = {
            new CategoryStyle(R.drawable.ic_category_electronics,
                    R.color.cat_electronics, R.color.cat_electronics_light),
            new CategoryStyle(R.drawable.ic_category_food,
                    R.color.cat_food, R.color.cat_food_light),
            new CategoryStyle(R.drawable.ic_category_office,
                    R.color.cat_office, R.color.cat_office_light),
            new CategoryStyle(R.drawable.ic_category_household,
                    R.color.cat_household, R.color.cat_household_light),
            new CategoryStyle(R.drawable.ic_category_other,
                    R.color.cat_other, R.color.cat_other_light),
    };

    private CategoryStyle(int iconRes, int colorRes, int lightColorRes) {
        this.iconRes = iconRes;
        this.colorRes = colorRes;
        this.lightColorRes = lightColorRes;
    }

    public static CategoryStyle of(Context context, String category) {
        String[] categories = context.getResources().getStringArray(R.array.product_categories);
        for (int i = 0; i < categories.length && i < STYLES.length; i++) {
            if (categories[i].equals(category)) {
                return STYLES[i];
            }
        }
        return STYLES[STYLES.length - 1]; // Không khớp thì dùng kiểu của "Khác"
    }
}
