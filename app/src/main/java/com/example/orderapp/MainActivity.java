package com.example.orderapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String[] categoryNames = {"热菜", "凉菜", "主食", "饮品"};
    private final List<List<Dish>> menu = new ArrayList<>();
    private final Map<String, Integer> quantities = new HashMap<>();

    private ListView listView;
    private TextView totalText;
    private DishAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildMenu();

        listView = findViewById(R.id.list_dishes);
        totalText = findViewById(R.id.total_text);
        TabLayout tabs = findViewById(R.id.tab_layout);
        findViewById(R.id.btn_submit).setOnClickListener(v -> submitOrder());

        adapter = new DishAdapter(this, new ArrayList<>(), quantities, this::updateTotal);
        listView.setAdapter(adapter);

        for (String name : categoryNames) {
            tabs.addTab(tabs.newTab().setText(name));
        }
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.setDishes(menu.get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // 默认选中第一个分类
        adapter.setDishes(menu.get(0));
        updateTotal();
    }

    private void buildMenu() {
        menu.add(Arrays.asList(
                d("宫保鸡丁", 28), d("鱼香肉丝", 26), d("麻婆豆腐", 18),
                d("红烧肉", 32), d("清蒸鲈鱼", 45)));
        menu.add(Arrays.asList(
                d("拍黄瓜", 12), d("凉拌木耳", 15), d("口水鸡", 22), d("皮蛋豆腐", 16)));
        menu.add(Arrays.asList(
                d("米饭", 3), d("馒头", 2), d("扬州炒饭", 16), d("牛肉面", 20), d("饺子", 18)));
        menu.add(Arrays.asList(
                d("可乐", 5), d("橙汁", 8), d("酸梅汤", 6), d("啤酒", 10), d("矿泉水", 3)));
    }

    private static Dish d(String name, int price) {
        return new Dish(name, price);
    }

    private void updateTotal() {
        int total = 0;
        for (List<Dish> category : menu) {
            for (Dish dish : category) {
                total += getQty(dish.name) * dish.price;
            }
        }
        totalText.setText("¥" + total);
    }

    private int getQty(String name) {
        return quantities.containsKey(name) ? quantities.get(name) : 0;
    }

    private void submitOrder() {
        List<String> lines = new ArrayList<>();
        int total = 0;
        for (List<Dish> category : menu) {
            for (Dish dish : category) {
                int q = getQty(dish.name);
                if (q > 0) {
                    lines.add(dish.name + "  ×" + q + "   ¥" + (q * dish.price));
                    total += q * dish.price;
                }
            }
        }

        if (lines.isEmpty()) {
            Toast.makeText(this, R.string.empty_order, Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        sb.append('\n').append("合计：¥").append(total);

        new AlertDialog.Builder(this)
                .setTitle(R.string.order_preview)
                .setMessage(sb.toString())
                .setPositiveButton(R.string.confirm_order, (dialog, which) -> {
                    quantities.clear();
                    adapter.notifyDataSetChanged();
                    updateTotal();
                    Toast.makeText(this, R.string.submit_success, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.continue_order, null)
                .show();
    }
}
