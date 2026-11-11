package com.example.quanlykhohang;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.DatabaseHelper;
import com.example.quanlykhohang.Product;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends Activity {

    public static final String EXTRA_PRODUCT_ID = "product_id";

    private DatabaseHelper databaseHelper;
    private int productId;

    private TextView tvDetailName;
    private TextView tvDetailCategory;
    private TextView tvDetailPrice;
    private TextView tvDetailQuantity;
    private TextView tvInventoryValue;
    private TextView tvActive;
    private TextView tvDescription;
    private TextView tvDetailWarning;

    private final NumberFormat moneyFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        databaseHelper = new DatabaseHelper(this);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvInventoryValue = findViewById(R.id.tvInventoryValue);
        tvActive = findViewById(R.id.tvActive);
        tvDescription = findViewById(R.id.tvDescription);
        tvDetailWarning = findViewById(R.id.tvDetailWarning);

        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);

        findViewById(R.id.btnImport)
                .setOnClickListener(v -> changeQuantity(1));

        findViewById(R.id.btnSell)
                .setOnClickListener(v -> changeQuantity(-1));

        findViewById(R.id.btnBack)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnDelete)
                .setOnClickListener(v -> confirmDelete());

        loadProduct();
    }

    private void loadProduct() {
        Product product = databaseHelper.getProductById(productId);

        if (product == null) {
            Toast.makeText(
                    this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        tvDetailName.setText(product.getName());
        tvDetailCategory.setText("Loại: " + product.getCategory());

        tvDetailPrice.setText(
                "Giá bán: " + moneyFormat.format(product.getPrice()) + " đ"
        );

        tvDetailQuantity.setText(
                "Số lượng tồn: " + product.getQuantity()
        );

        BigInteger inventoryValue = BigInteger.valueOf(product.getPrice())
                .multiply(BigInteger.valueOf(product.getQuantity()));

        tvInventoryValue.setText(
                "Giá trị tồn kho: " + moneyFormat.format(inventoryValue) + " đ"
        );

        tvActive.setText(
                "Trạng thái: " +
                        (product.isActive()
                                ? "Đang kinh doanh"
                                : "Ngừng kinh doanh")
        );

        String description = product.getDescription();

        tvDescription.setText(
                "Mô tả: " +
                        (description == null || description.trim().isEmpty()
                                ? "Chưa có mô tả"
                                : description)
        );

        boolean lowStock = product.getQuantity() < 5;

        tvDetailQuantity.setTextColor(
                lowStock ? Color.RED : Color.DKGRAY
        );

        tvDetailWarning.setVisibility(
                lowStock ? View.VISIBLE : View.GONE
        );
    }

    private void changeQuantity(int change) {
        Product product = databaseHelper.getProductById(productId);

        if (product == null) {
            loadProduct();
            return;
        }

        if (change < 0 && product.getQuantity() == 0) {
            Toast.makeText(
                    this, "Hết hàng", Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (change > 0 && product.getQuantity() == Integer.MAX_VALUE) {
            Toast.makeText(
                    this, "Số lượng đã đạt giới hạn", Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int newQuantity = product.getQuantity() + change;

        int updated = databaseHelper.updateQuantity(productId, newQuantity);

        if (updated > 0) {
            loadProduct();
        } else {
            Toast.makeText(
                    this, "Không thể cập nhật số lượng", Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int deleted = databaseHelper.deleteProduct(productId);

                    if (deleted > 0) {
                        Toast.makeText(
                                this,
                                "Đã xóa sản phẩm",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    } else {
                        Toast.makeText(
                                this,
                                "Không thể xóa sản phẩm",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}