package com.example.th03app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.TextViewCompat;

import com.example.th03app.database.DatabaseHelper;
import com.example.th03app.model.Product;
import com.example.th03app.utils.CategoryStyle;
import com.example.th03app.utils.InsetsHelper;
import com.example.th03app.utils.MoneyUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

/**
 * Màn hình 4: Chi tiết sản phẩm. Nhận id qua Intent rồi truy vấn lại cơ sở dữ liệu theo id.
 */
public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private DatabaseHelper dbHelper;
    private int productId;
    private Product product;

    private ImageView ivCategoryIcon;
    private TextView tvName;
    private TextView tvCategory;
    private TextView tvStatus;
    private TextView tvPrice;
    private TextView tvLowStockWarning;
    private View layoutQuantityIcon;
    private ImageView ivQuantityIcon;
    private TextView tvQuantity;
    private TextView tvStockValue;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_product_detail);
        InsetsHelper.applySystemBarInsets(findViewById(R.id.main), findViewById(R.id.appBar));

        dbHelper = new DatabaseHelper(this);
        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivCategoryIcon = findViewById(R.id.ivCategoryIcon);
        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvStatus = findViewById(R.id.tvStatus);
        tvPrice = findViewById(R.id.tvPrice);
        tvLowStockWarning = findViewById(R.id.tvLowStockWarning);
        layoutQuantityIcon = findViewById(R.id.layoutQuantityIcon);
        ivQuantityIcon = findViewById(R.id.ivQuantityIcon);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvStockValue = findViewById(R.id.tvStockValue);
        tvDescription = findViewById(R.id.tvDescription);

        Button btnImport = findViewById(R.id.btnImport);
        Button btnSell = findViewById(R.id.btnSell);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDelete = findViewById(R.id.btnDelete);

        btnImport.setOnClickListener(v -> importOne());
        btnSell.setOnClickListener(v -> sellOne());
        btnBack.setOnClickListener(v -> finish()); // MainActivity tải lại trong onResume()
        btnDelete.setOnClickListener(v -> confirmDelete());

        loadProduct();
    }

    /** Truy vấn sản phẩm theo id và hiển thị lên giao diện. */
    private void loadProduct() {
        product = dbHelper.getProductById(productId);
        if (product == null) {
            Toast.makeText(this, R.string.msg_product_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CategoryStyle style = CategoryStyle.of(this, product.getCategory());
        ivCategoryIcon.setImageResource(style.iconRes);
        tvName.setText(product.getName());
        tvCategory.setText(product.getCategory());

        // Trạng thái kinh doanh: chấm xanh lá = đang bán, chấm xám = ngừng bán
        tvStatus.setText(product.isActive() ? R.string.status_active : R.string.status_inactive);
        TextViewCompat.setCompoundDrawableTintList(tvStatus, ColorStateList.valueOf(
                ContextCompat.getColor(this, product.isActive() ? R.color.success : R.color.inactive_dot)));

        tvPrice.setText(MoneyUtils.formatVnd(product.getPrice()));
        tvQuantity.setText(String.valueOf(product.getQuantity()));
        tvStockValue.setText(MoneyUtils.formatVnd(product.getStockValue()));

        // Cảnh báo khi số lượng dưới 5: số lượng màu đỏ + khung cảnh báo
        boolean lowStock = product.isLowStock();
        tvQuantity.setTextColor(ContextCompat.getColor(this,
                lowStock ? R.color.danger : R.color.text_primary));
        ViewCompat.setBackgroundTintList(layoutQuantityIcon, ColorStateList.valueOf(
                ContextCompat.getColor(this, lowStock ? R.color.danger_light : R.color.primary_light)));
        ImageViewCompat.setImageTintList(ivQuantityIcon, ColorStateList.valueOf(
                ContextCompat.getColor(this, lowStock ? R.color.danger : R.color.primary)));
        if (lowStock) {
            tvLowStockWarning.setText(product.getQuantity() == 0
                    ? getString(R.string.warning_out_of_stock)
                    : getString(R.string.warning_low_stock, product.getQuantity()));
            tvLowStockWarning.setVisibility(View.VISIBLE);
        } else {
            tvLowStockWarning.setVisibility(View.GONE);
        }

        String description = product.getDescription();
        if (description == null || description.isEmpty()) {
            tvDescription.setText(R.string.no_description);
            tvDescription.setTextColor(ContextCompat.getColor(this, R.color.text_hint));
        } else {
            tvDescription.setText(description);
            tvDescription.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }
    }

    /** Nhập thêm 1: tăng số lượng, ghi vào CSDL rồi hiển thị lại. */
    private void importOne() {
        if (dbHelper.updateQuantity(productId, product.getQuantity() + 1)) {
            loadProduct();
            pulse(tvQuantity);
        } else {
            showMessage(R.string.error_update_failed);
        }
    }

    /** Bán 1: giảm số lượng; nếu đã bằng 0 thì báo "Hết hàng" và không giảm nữa. */
    private void sellOne() {
        if (product.getQuantity() <= 0) {
            showMessage(R.string.msg_out_of_stock);
            return;
        }
        if (dbHelper.updateQuantity(productId, product.getQuantity() - 1)) {
            loadProduct();
            pulse(tvQuantity);
        } else {
            showMessage(R.string.error_update_failed);
        }
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_message, product.getName()))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> deleteProduct())
                .show();
    }

    /** Xóa sản phẩm khỏi CSDL rồi quay lại danh sách. */
    private void deleteProduct() {
        if (dbHelper.deleteProduct(productId)) {
            Toast.makeText(this, R.string.msg_product_deleted, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showMessage(R.string.error_delete_failed);
        }
    }

    private void showMessage(int messageRes) {
        Snackbar.make(findViewById(R.id.main), messageRes, Snackbar.LENGTH_SHORT).show();
    }

    /** Hiệu ứng "nảy" nhẹ khi số lượng thay đổi. */
    private static void pulse(View view) {
        view.animate().cancel();
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.animate().scaleX(1.25f).scaleY(1.25f).setDuration(110)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(140).start())
                .start();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
