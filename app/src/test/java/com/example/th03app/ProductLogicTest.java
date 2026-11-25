package com.example.th03app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.th03app.model.Product;
import com.example.th03app.utils.MoneyUtils;

import org.junit.Test;

/**
 * Kiểm thử logic thuần Java (chạy trên máy tính, không cần thiết bị).
 */
public class ProductLogicTest {

    @Test
    public void lowStock_whenQuantityBelowFive() {
        assertTrue(new Product("A", 1000, 0, "Khác", true, "").isLowStock());
        assertTrue(new Product("A", 1000, 4, "Khác", true, "").isLowStock());
        assertFalse(new Product("A", 1000, 5, "Khác", true, "").isLowStock());
        assertFalse(new Product("A", 1000, 20, "Khác", true, "").isLowStock());
    }

    @Test
    public void stockValue_isPriceTimesQuantity() {
        Product keyboard = new Product("Bàn phím cơ", 450000, 3, "Điện tử", true, "");
        assertEquals(1350000L, keyboard.getStockValue());
    }

    @Test
    public void formatVnd_usesDotThousandsSeparator() {
        assertEquals("0 đ", MoneyUtils.formatVnd(0));
        assertEquals("25.000 đ", MoneyUtils.formatVnd(25000));
        assertEquals("450.000 đ", MoneyUtils.formatVnd(450000));
        assertEquals("5.350.000 đ", MoneyUtils.formatVnd(5350000));
    }
}
