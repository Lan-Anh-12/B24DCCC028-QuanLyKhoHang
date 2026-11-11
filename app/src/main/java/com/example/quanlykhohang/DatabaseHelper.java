package com.example.quanlykhohang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.quanlykhohang.Product;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT NOT NULL UNIQUE, " +
                        "password TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE products (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "price INTEGER NOT NULL CHECK(price > 0), " +
                        "quantity INTEGER NOT NULL CHECK(quantity >= 0), " +
                        "category TEXT NOT NULL, " +
                        "active INTEGER NOT NULL CHECK(active IN (0, 1)), " +
                        "description TEXT)"
        );

        // Dùng trực tiếp db đang được tạo, không mở lại database.
        insertUser(db, "admin", "123456");

        insertProduct(db, new Product(
                0, "Chuột không dây", 150000, 20,
                "Điện tử", true, "Chuột không dây tiện dụng"
        ));

        insertProduct(db, new Product(
                0, "Bàn phím cơ", 450000, 3,
                "Điện tử", true, "Bàn phím cơ"
        ));

        insertProduct(db, new Product(
                0, "Sổ tay A5", 25000, 40,
                "Văn phòng phẩm", true, "Sổ ghi chép khổ A5"
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Phiên bản hiện tại là 1.
        // Khi tăng phiên bản, bổ sung lệnh nâng cấp bảng tại đây.
    }

    private long insertUser(SQLiteDatabase db,
                            String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        return db.insert("users", null, values);
    }

    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE username = ? AND password = ?",
                new String[]{username, password}
        )) {
            return cursor.moveToFirst();
        }
    }

    private long insertProduct(SQLiteDatabase db, Product product) {
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("quantity", product.getQuantity());
        values.put("category", product.getCategory());
        values.put("active", product.isActive() ? 1 : 0);
        values.put("description", product.getDescription());

        return db.insert("products", null, values);
    }

    public long insertProduct(Product product) {
        return insertProduct(getWritableDatabase(), product);
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM products ORDER BY id DESC", null
        )) {
            while (cursor.moveToNext()) {
                products.add(readProduct(cursor));
            }
        }

        return products;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT * FROM products WHERE id = ?",
                new String[]{String.valueOf(id)}
        )) {
            if (cursor.moveToFirst()) {
                return readProduct(cursor);
            }
        }

        return null;
    }

    public int updateQuantity(int id, int quantity) {
        if (quantity < 0) {
            return 0;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);

        return db.update(
                "products",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteProduct(int id) {
        return getWritableDatabase().delete(
                "products",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(price * quantity), 0) FROM products",
                null
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
        }

        return 0;
    }

    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM products WHERE quantity < ?",
                new String[]{"5"}
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return 0;
    }

    private Product readProduct(Cursor cursor) {
        return new Product(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getLong(cursor.getColumnIndexOrThrow("price")),
                cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getInt(cursor.getColumnIndexOrThrow("active")) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow("description"))
        );
    }
}