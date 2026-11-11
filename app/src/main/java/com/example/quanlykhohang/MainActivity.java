package com.example.quanlykhohang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.ProductAdapter;
import com.example.quanlykhohang.DatabaseHelper;
import com.example.quanlykhohang.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private ProductAdapter adapter;

    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStock;

    private final NumberFormat moneyFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStock = findViewById(R.id.tvLowStock);

        ListView lvProducts = findViewById(R.id.lvProducts);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new ProductAdapter(this, new ArrayList<>());
        lvProducts.setAdapter(adapter);
        lvProducts.setEmptyView(tvEmpty);

        findViewById(R.id.btnAddProduct).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProductActivity.class);
            startActivity(intent);
        });

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product product = adapter.getItem(position);

            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra(
                    ProductDetailActivity.EXTRA_PRODUCT_ID,
                    product.getId()
            );
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        ArrayList<Product> products = databaseHelper.getAllProducts();

        adapter.clear();
        adapter.addAll(products);
        adapter.notifyDataSetChanged();

        tvTotalProducts.setText("Tổng số sản phẩm: " + products.size());

        tvTotalValue.setText(
                "Tổng giá trị kho: " +
                        moneyFormat.format(databaseHelper.getTotalValue()) +
                        " đ"
        );

        tvLowStock.setText(
                "Sản phẩm sắp hết hàng: " + databaseHelper.countLowStock()
        );
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}