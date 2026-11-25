package com.example.th03app.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Định dạng tiền VNĐ có dấu chấm phân cách hàng nghìn, ví dụ: 1.350.000 đ
 */
public final class MoneyUtils {

    private MoneyUtils() {
    }

    public static String formatVnd(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,##0", symbols);
        return format.format(amount) + " đ";
    }
}
