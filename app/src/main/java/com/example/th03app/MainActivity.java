package com.example.th03app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.th03app.adapter.ProductAdapter;
import com.example.th03app.database.DatabaseHelper;
import com.example.th03app.model.Product;
import com.example.th03app.utils.InsetsHelper;
import com.example.th03app.utils.MoneyUtils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

/**
 * Màn hình 2: Danh sách sản phẩm và thống kê kho.
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;

    private RecyclerView rvProducts;
    private View layoutEmpty;
    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStockCount;
    private ExtendedFloatingActionButton btnAddProduct;

    /** id sản phẩm vừa thêm, dùng để cuộn tới sản phẩm đó sau khi tải lại danh sách. */
    private int newProductId = -1;

    private final ActivityResultLauncher<Intent> addProductLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    newProductId = result.getData()
                            .getIntExtra(AddProductActivity.EXTRA_NEW_PRODUCT_ID, -1);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_main);
        InsetsHelper.applySystemBarInsets(findViewById(R.id.main), findViewById(R.id.appBar));

        dbHelper = new DatabaseHelper(this);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        rvProducts = findViewById(R.id.rvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        // Nhấn vào một sản phẩm: truyền id sang màn hình chi tiết
        adapter = new ProductAdapter(product -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        // Cuộn xuống thì thu gọn nút Thêm, cuộn lên thì mở rộng lại
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 6 && btnAddProduct.isExtended()) {
                    btnAddProduct.shrink();
                } else if (dy < -6 && !btnAddProduct.isExtended()) {
                    btnAddProduct.extend();
                }
            }
        });

        btnAddProduct.setOnClickListener(v ->
                addProductLauncher.launch(new Intent(this, AddProductActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại mỗi khi quay về màn hình này (sau khi thêm / nhập / bán / xóa)
        loadData();
    }

    private void loadData() {
        List<Product> products = dbHelper.getAllProducts();
        adapter.setProducts(products);

        boolean isEmpty = products.isEmpty();
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvProducts.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        tvTotalProducts.setText(String.valueOf(dbHelper.getProductCount()));
        tvTotalValue.setText(MoneyUtils.formatVnd(dbHelper.getTotalValue()));
        tvLowStockCount.setText(String.valueOf(dbHelper.countLowStock()));

        if (newProductId != -1) {
            int position = adapter.findPositionById(newProductId);
            if (position != -1) {
                rvProducts.scrollToPosition(position);
            }
            btnAddProduct.extend();
            newProductId = -1;
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
