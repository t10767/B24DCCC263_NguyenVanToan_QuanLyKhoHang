package com.example.th03app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.th03app.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý cơ sở dữ liệu SQLite warehouse.db gồm 2 bảng: users và products.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // ----- Bảng users -----
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // ----- Bảng products -----
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_ACTIVE = "active";
    private static final String COLUMN_DESCRIPTION = "description";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, "
            + COLUMN_PASSWORD + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PRICE + " INTEGER NOT NULL, "
            + COLUMN_QUANTITY + " INTEGER NOT NULL, "
            + COLUMN_CATEGORY + " TEXT NOT NULL, "
            + COLUMN_ACTIVE + " INTEGER NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PRODUCTS);

        // Chèn dữ liệu mẫu. Trong onCreate() phải dùng đối tượng db được truyền vào,
        // không được gọi getWritableDatabase() (sẽ bị lỗi gọi đệ quy).
        insertUser(db, "admin", "123456");

        insertProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", true,
                "Chuột không dây kết nối 2.4GHz, dùng pin AA"));
        insertProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", true,
                "Bàn phím cơ switch xanh, có đèn nền"));
        insertProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", true,
                "Sổ tay bìa cứng khổ A5, 200 trang"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // =====================================================================
    // Bảng users
    // =====================================================================

    /** Trả về true nếu có tài khoản khớp tên đăng nhập và mật khẩu. */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS
                        + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password});
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    /** Thêm tài khoản mới, trả về id vừa thêm (-1 nếu lỗi, ví dụ trùng username). */
    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // =====================================================================
    // Bảng products
    // =====================================================================

    /** Thêm sản phẩm, trả về id vừa thêm (-1 nếu lỗi). */
    public long insertProduct(Product product) {
        return insertProduct(getWritableDatabase(), product);
    }

    private long insertProduct(SQLiteDatabase db, Product product) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_QUANTITY, product.getQuantity());
        values.put(COLUMN_CATEGORY, product.getCategory());
        values.put(COLUMN_ACTIVE, product.isActive() ? 1 : 0);
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    /** Lấy toàn bộ sản phẩm (danh sách rỗng nếu chưa có sản phẩm nào). */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null,
                null, null, COLUMN_ID + " ASC");
        try {
            while (cursor.moveToNext()) {
                products.add(cursorToProduct(cursor));
            }
        } finally {
            cursor.close();
        }
        return products;
    }

    /** Lấy một sản phẩm theo id, trả về null nếu không tồn tại. */
    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursorToProduct(cursor);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    /** Cập nhật số lượng tồn kho, trả về true nếu cập nhật thành công. */
    public boolean updateQuantity(int id, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, quantity);
        int rows = db.update(TABLE_PRODUCTS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        return rows > 0;
    }

    /** Xóa sản phẩm theo id, trả về true nếu xóa thành công. */
    public boolean deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_PRODUCTS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        return rows > 0;
    }

    /** Tổng số sản phẩm trong kho. */
    public int getProductCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    /** Tổng giá trị kho: SUM(price * quantity). Trả về 0 khi kho rỗng. */
    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT IFNULL(SUM(" + COLUMN_PRICE + " * " + COLUMN_QUANTITY
                + "), 0) FROM " + TABLE_PRODUCTS, null);
        try {
            return cursor.moveToFirst() ? cursor.getLong(0) : 0;
        } finally {
            cursor.close();
        }
    }

    /** Số sản phẩm sắp hết hàng (quantity < 5). */
    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS
                        + " WHERE " + COLUMN_QUANTITY + " < ?",
                new String[]{String.valueOf(Product.LOW_STOCK_THRESHOLD)});
        try {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        } finally {
            cursor.close();
        }
    }

    /** Chuyển dòng hiện tại của Cursor thành đối tượng Product. */
    private Product cursorToProduct(Cursor cursor) {
        int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
        return new Product(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVE)) == 1,
                cursor.isNull(descriptionIndex) ? "" : cursor.getString(descriptionIndex));
    }
}
