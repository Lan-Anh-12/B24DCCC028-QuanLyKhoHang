package com.example.quanlykhohang;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.quanlykhohang.R;
import com.example.quanlykhohang.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class ProductAdapter extends ArrayAdapter<Product> {

    private final LayoutInflater inflater;

    private final NumberFormat moneyFormat =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
        inflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvPrice;
        TextView tvCategory;
        TextView tvQuantity;
        TextView tvWarning;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.item_product, parent, false
            );

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvCategory = convertView.findViewById(R.id.tvCategory);
            holder.tvQuantity = convertView.findViewById(R.id.tvQuantity);
            holder.tvWarning = convertView.findViewById(R.id.tvWarning);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = getItem(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(
                "Giá: " + moneyFormat.format(product.getPrice()) + " đ"
        );
        holder.tvCategory.setText("Loại: " + product.getCategory());
        holder.tvQuantity.setText("Tồn kho: " + product.getQuantity());

        boolean lowStock = product.getQuantity() < 5;

        // Luôn cập nhật cả hai trạng thái vì ListView tái sử dụng dòng.
        holder.tvQuantity.setTextColor(
                lowStock ? Color.RED : Color.DKGRAY
        );

        holder.tvWarning.setVisibility(
                lowStock ? View.VISIBLE : View.GONE
        );

        return convertView;
    }
}