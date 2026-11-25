package com.example.th03app.model;

/**
 * Lớp Product biểu diễn một sản phẩm trong kho.
 */
public class Product {

    /** Số lượng dưới ngưỡng này được coi là "Sắp hết hàng". */
    public static final int LOW_STOCK_THRESHOLD = 5;

    private int id;
    private String name;
    private long price;          // Giá bán (VNĐ)
    private int quantity;        // Số lượng tồn kho
    private String category;     // Loại sản phẩm
    private boolean active;      // true: đang kinh doanh, false: ngừng kinh doanh
    private String description;  // Mô tả (có thể rỗng)

    public Product() {
    }

    /** Dùng khi tạo sản phẩm mới (chưa có id, id do SQLite tự sinh). */
    public Product(String name, long price, int quantity, String category,
                   boolean active, String description) {
        this(0, name, price, quantity, category, active, description);
    }

    /** Dùng khi đọc sản phẩm từ cơ sở dữ liệu. */
    public Product(int id, String name, long price, int quantity, String category,
                   boolean active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    /** Sản phẩm sắp hết hàng khi số lượng nhỏ hơn 5. */
    public boolean isLowStock() {
        return quantity < LOW_STOCK_THRESHOLD;
    }

    /** Giá trị tồn kho = giá bán × số lượng. */
    public long getStockValue() {
        return price * quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
