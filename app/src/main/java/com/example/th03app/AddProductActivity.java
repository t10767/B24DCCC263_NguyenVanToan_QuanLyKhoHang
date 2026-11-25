package com.example.th03app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.th03app.database.DatabaseHelper;
import com.example.th03app.model.Product;
import com.example.th03app.utils.InsetsHelper;
import com.example.th03app.utils.SimpleTextWatcher;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Màn hình 3: Thêm sản phẩm mới vào cơ sở dữ liệu.
 */
public class AddProductActivity extends AppCompatActivity {

    public static final String EXTRA_NEW_PRODUCT_ID = "extra_new_product_id";

    private TextInputLayout tilName;
    private TextInputLayout tilPrice;
    private TextInputLayout tilQuantity;
    private EditText etName;
    private EditText etPrice;
    private EditText etQuantity;
    private Spinner spCategory;
    private MaterialSwitch swActive;
    private EditText etDescription;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_add_product);
        InsetsHelper.applySystemBarInsets(findViewById(R.id.main), findViewById(R.id.appBar));

        dbHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tilName = findViewById(R.id.tilName);
        tilPrice = findViewById(R.id.tilPrice);
        tilQuantity = findViewById(R.id.tilQuantity);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        spCategory = findViewById(R.id.spCategory);
        swActive = findViewById(R.id.swActive);
        etDescription = findViewById(R.id.etDescription);
        Button btnSave = findViewById(R.id.btnSave);

        // Spinner loại sản phẩm: Điện tử, Thực phẩm, Văn phòng phẩm, Gia dụng, Khác
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.product_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Người dùng sửa ô nào thì xóa lỗi của ô đó
        etName.addTextChangedListener(SimpleTextWatcher.onChanged(() -> tilName.setError(null)));
        etPrice.addTextChangedListener(SimpleTextWatcher.onChanged(() -> tilPrice.setError(null)));
        etQuantity.addTextChangedListener(SimpleTextWatcher.onChanged(() -> tilQuantity.setError(null)));

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
        boolean active = swActive.isChecked();
        String description = etDescription.getText().toString().trim();

        EditText firstInvalid = null;

        // Tên sản phẩm: không được để trống
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_name_empty));
            firstInvalid = etName;
        }

        // Giá bán: phải là số lớn hơn 0
        long price = parseLongOrDefault(priceText, -1);
        if (priceText.isEmpty()) {
            tilPrice.setError(getString(R.string.error_price_empty));
            if (firstInvalid == null) firstInvalid = etPrice;
        } else if (price <= 0) {
            tilPrice.setError(getString(R.string.error_price_invalid));
            if (firstInvalid == null) firstInvalid = etPrice;
        }

        // Số lượng: số nguyên không âm
        int quantity = parseIntOrDefault(quantityText, -1);
        if (quantityText.isEmpty()) {
            tilQuantity.setError(getString(R.string.error_quantity_empty));
            if (firstInvalid == null) firstInvalid = etQuantity;
        } else if (quantity < 0) {
            tilQuantity.setError(getString(R.string.error_quantity_invalid));
            if (firstInvalid == null) firstInvalid = etQuantity;
        }

        if (firstInvalid != null) {
            firstInvalid.requestFocus();
            showMessage(R.string.error_check_input);
            return;
        }

        Product product = new Product(name, price, quantity, category, active, description);
        long newId = dbHelper.insertProduct(product);
        if (newId == -1) {
            showMessage(R.string.error_save_failed);
            return;
        }

        // Màn hình này đóng ngay nên dùng Toast để thông báo vẫn hiện ở màn hình danh sách
        Toast.makeText(this, getString(R.string.msg_product_added, name), Toast.LENGTH_SHORT).show();
        Intent result = new Intent();
        result.putExtra(EXTRA_NEW_PRODUCT_ID, (int) newId);
        setResult(RESULT_OK, result);
        finish(); // Quay về màn hình danh sách, danh sách sẽ tải lại trong onResume()
    }

    /** Thông báo hiện ngay phía trên nút Lưu. */
    private void showMessage(int messageRes) {
        Snackbar.make(findViewById(R.id.main), messageRes, Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.bottomBar)
                .show();
    }

    private static long parseLongOrDefault(String text, long defaultValue) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int parseIntOrDefault(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
