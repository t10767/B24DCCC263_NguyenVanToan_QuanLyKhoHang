package com.example.th03app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.th03app.R;
import com.example.th03app.model.Product;
import com.example.th03app.utils.CategoryStyle;
import com.example.th03app.utils.MoneyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách sản phẩm. Danh sách ở đây chỉ chứa kết quả truy vấn
 * từ SQLite để hiển thị, không phải nơi lưu dữ liệu chính.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    /** Vị trí của sản phẩm có id cho trước trong danh sách, -1 nếu không có. */
    public int findPositionById(int productId) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == productId) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Context context = holder.itemView.getContext();

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(context.getString(R.string.price_label,
                MoneyUtils.formatVnd(product.getPrice())));

        // Icon và nhãn loại sản phẩm được tô màu theo loại
        CategoryStyle style = CategoryStyle.of(context, product.getCategory());
        ColorStateList categoryColor = ColorStateList.valueOf(
                ContextCompat.getColor(context, style.colorRes));
        ColorStateList categoryLight = ColorStateList.valueOf(
                ContextCompat.getColor(context, style.lightColorRes));
        holder.ivCategoryIcon.setImageResource(style.iconRes);
        ImageViewCompat.setImageTintList(holder.ivCategoryIcon, categoryColor);
        ViewCompat.setBackgroundTintList(holder.layoutCategoryIcon, categoryLight);
        holder.tvCategory.setText(product.getCategory());
        holder.tvCategory.setTextColor(categoryColor);
        ViewCompat.setBackgroundTintList(holder.tvCategory, categoryLight);

        // Sắp hết hàng: số lượng màu đỏ + nhãn cảnh báo
        boolean lowStock = product.isLowStock();
        int quantityColor = ContextCompat.getColor(context,
                lowStock ? R.color.danger : R.color.text_primary);
        holder.tvQuantity.setText(String.valueOf(product.getQuantity()));
        holder.tvQuantity.setTextColor(quantityColor);
        holder.tvQuantityLabel.setTextColor(lowStock ? quantityColor
                : ContextCompat.getColor(context, R.color.text_secondary));
        ViewCompat.setBackgroundTintList(holder.layoutQuantity, ColorStateList.valueOf(
                ContextCompat.getColor(context, lowStock ? R.color.danger_light : R.color.surface_variant)));
        holder.tvLowStock.setVisibility(lowStock ? View.VISIBLE : View.GONE);

        holder.tvInactive.setVisibility(product.isActive() ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final View layoutCategoryIcon;
        final ImageView ivCategoryIcon;
        final TextView tvName;
        final TextView tvPrice;
        final View layoutQuantity;
        final TextView tvQuantity;
        final TextView tvQuantityLabel;
        final TextView tvCategory;
        final TextView tvLowStock;
        final TextView tvInactive;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutCategoryIcon = itemView.findViewById(R.id.layoutCategoryIcon);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            layoutQuantity = itemView.findViewById(R.id.layoutQuantity);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvQuantityLabel = itemView.findViewById(R.id.tvQuantityLabel);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLowStock = itemView.findViewById(R.id.tvLowStock);
            tvInactive = itemView.findViewById(R.id.tvInactive);
        }
    }
}
