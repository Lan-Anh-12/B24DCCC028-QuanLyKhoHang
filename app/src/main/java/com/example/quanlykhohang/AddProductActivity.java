package com.example.quanlykhohang;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.DatabaseHelper;
import com.example.quanlykhohang.Product;

public class AddProductActivity extends Activity {

    private EditText edtName;
    private EditText edtPrice;
    private EditText edtQuantity;
    private EditText edtDescription;

    private Spinner spCategory;
    private Switch swActive;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        databaseHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtDescription = findViewById(R.id.edtDescription);
        spCategory = findViewById(R.id.spCategory);
        swActive = findViewById(R.id.swActive);

        ArrayAdapter<CharSequence> categoryAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.product_categories,
                        android.R.layout.simple_spinner_item
                );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategory.setAdapter(categoryAdapter);

        findViewById(R.id.btnSaveProduct)
                .setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Tên sản phẩm không được bỏ trống");
            edtName.requestFocus();
            return;
        }

        long price;

        try {
            price = Long.parseLong(priceText);

            if (price <= 0) {
                edtPrice.setError("Giá bán phải lớn hơn 0");
                return;
            }
        } catch (NumberFormatException e) {
            edtPrice.setError("Nhập giá bán nguyên hợp lệ");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);

            if (quantity < 0) {
                edtQuantity.setError("Số lượng không được âm");
                return;
            }
        } catch (NumberFormatException e) {
            edtQuantity.setError("Nhập số lượng nguyên hợp lệ");
            return;
        }

        String category = spCategory.getSelectedItem().toString();

        Product product = new Product(
                0,
                name,
                price,
                quantity,
                category,
                swActive.isChecked(),
                description
        );

        long result = databaseHelper.insertProduct(product);

        if (result != -1) {
            Toast.makeText(
                    this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT
            ).show();

            // Đóng màn hình thêm, quay lại MainActivity đang có.
            finish();
        } else {
            Toast.makeText(
                    this, "Không thể thêm sản phẩm", Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}