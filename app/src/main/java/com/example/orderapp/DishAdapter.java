package com.example.orderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class DishAdapter extends BaseAdapter {

    public interface OnQuantityChanged {
        void onChange();
    }

    private final Context context;
    private List<Dish> dishes;
    private final Map<String, Integer> quantities;
    private final OnQuantityChanged listener;

    public DishAdapter(Context context, List<Dish> dishes,
                       Map<String, Integer> quantities, OnQuantityChanged listener) {
        this.context = context;
        this.dishes = dishes;
        this.quantities = quantities;
        this.listener = listener;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dishes == null ? 0 : dishes.size();
    }

    @Override
    public Object getItem(int position) {
        return dishes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dish, parent, false);
            vh = new ViewHolder();
            vh.name = convertView.findViewById(R.id.dish_name);
            vh.price = convertView.findViewById(R.id.dish_price);
            vh.qty = convertView.findViewById(R.id.dish_qty);
            vh.minus = convertView.findViewById(R.id.btn_minus);
            vh.plus = convertView.findViewById(R.id.btn_plus);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Dish dish = dishes.get(position);
        vh.name.setText(dish.name);
        vh.price.setText("¥" + dish.price);

        int q = quantities.containsKey(dish.name) ? quantities.get(dish.name) : 0;
        vh.qty.setText(String.valueOf(q));
        vh.minus.setEnabled(q > 0);
        vh.minus.setAlpha(q > 0 ? 1f : 0.35f);

        vh.minus.setOnClickListener(v -> changeQty(dish, -1));
        vh.plus.setOnClickListener(v -> changeQty(dish, 1));

        return convertView;
    }

    private void changeQty(Dish dish, int delta) {
        int current = quantities.containsKey(dish.name) ? quantities.get(dish.name) : 0;
        int next = current + delta;
        if (next < 0) {
            next = 0;
        }
        quantities.put(dish.name, next);
        notifyDataSetChanged();
        listener.onChange();
    }

    private static class ViewHolder {
        TextView name;
        TextView price;
        TextView qty;
        ImageButton minus;
        ImageButton plus;
    }
}
