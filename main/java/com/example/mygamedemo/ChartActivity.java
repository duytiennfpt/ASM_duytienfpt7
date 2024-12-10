package com.example.mygamedemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mygamedemo.databinding.ActivityChartBinding;
import com.example.mygamedemo.databinding.ActivityDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ChartActivity extends AppCompatActivity {
    private PieChartView pieChartView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private int totalIncome = 0;
    private int totalExpense = 0;

    private TextView totalBalance, incomeAmount, expenseAmount;
    private Button btnIncome, btnExpense, btnReset;

    private LinearLayout menuHome, menuExpense, menuIncome, menuChart, menuSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // Khởi tạo View
        pieChartView = findViewById(R.id.pieChartView);
        btnIncome = findViewById(R.id.btn_income);
        btnExpense = findViewById(R.id.btn_expense);
        btnReset = findViewById(R.id.btn_reset); // Nút Reset mới
        totalBalance = findViewById(R.id.totalBalance);
        incomeAmount = findViewById(R.id.incomeAmount);
        expenseAmount = findViewById(R.id.expenseAmount);

        // Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Menu
        menuHome = findViewById(R.id.menu_home);
        menuExpense = findViewById(R.id.menu_expense);
        menuIncome = findViewById(R.id.menu_income);
        menuChart = findViewById(R.id.menu_chart);
        menuSetting = findViewById(R.id.menu_setting);

        setupMenu();
        setupButtonListeners();
        loadData();
    }

    private void setupButtonListeners() {
        btnIncome.setOnClickListener(v -> showIncomeView());
        btnExpense.setOnClickListener(v -> showExpenseView());
        btnReset.setOnClickListener(v -> resetChartView()); // Listener cho nút Reset
    }

    private void showIncomeView() {
        // Hiển thị Income
        incomeAmount.setVisibility(View.VISIBLE);
        findViewById(R.id.incomePercentage).setVisibility(View.VISIBLE);

        // Ẩn Expense
        expenseAmount.setVisibility(View.GONE);
        findViewById(R.id.expensePercentage).setVisibility(View.GONE);

        // Cập nhật PieChartView và Total Balance
        pieChartView.setData(0, totalIncome); // Chỉ hiển thị Income
        totalBalance.setText("Total Balance: " + totalIncome);
        pieChartView.invalidate();
    }

    private void showExpenseView() {
        // Hiển thị Expense
        expenseAmount.setVisibility(View.VISIBLE);
        findViewById(R.id.expensePercentage).setVisibility(View.VISIBLE);

        // Ẩn Income
        incomeAmount.setVisibility(View.GONE);
        findViewById(R.id.incomePercentage).setVisibility(View.GONE);

        // Cập nhật PieChartView và Total Balance
        pieChartView.setData(totalExpense, 0); // Chỉ hiển thị Expense
        totalBalance.setText("Total Balance: " + totalExpense);
        pieChartView.invalidate();
    }

    private void resetChartView() {
        // Hiển thị lại cả Income và Expense
        incomeAmount.setVisibility(View.VISIBLE);
        findViewById(R.id.incomePercentage).setVisibility(View.VISIBLE);
        expenseAmount.setVisibility(View.VISIBLE);
        findViewById(R.id.expensePercentage).setVisibility(View.VISIBLE);

        // Cập nhật PieChartView về trạng thái ban đầu
        pieChartView.setData(totalExpense, totalIncome);
        totalBalance.setText("Total Balance: " + (totalIncome - totalExpense));
        pieChartView.invalidate();
    }


    private void loadData() {
        if (firebaseFirestore == null || firebaseAuth == null || firebaseAuth.getUid() == null) {
            Log.e("ChartActivity", "FirebaseFirestore hoặc FirebaseAuth chưa được khởi tạo.");
            return;
        }

        firebaseFirestore.collection("Expenses")
                .document(firebaseAuth.getUid())
                .collection("Note")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot ds : task.getResult()) {
                                try {
                                    int amount = Integer.parseInt(ds.getString("amount"));
                                    if ("Income".equals(ds.getString("type"))) {
                                        totalIncome += amount;
                                    } else if ("Expense".equals(ds.getString("type"))) {
                                        totalExpense += amount;
                                    }
                                } catch (NumberFormatException | NullPointerException e) {
                                    Log.e("ChartActivity", "Lỗi khi xử lý dữ liệu: " + e.getMessage());
                                }
                            }
                            // Cập nhật PieChartView và Total Balance ban đầu
                            resetChartView();
                        } else {
                            Log.e("ChartActivity", "Không thể tải dữ liệu từ Firestore: " + task.getException());
                        }
                    }
                });
    }

    private void setupMenu() {
        menuHome.setOnClickListener(v -> selectMenu(menuHome, DashboardActivity.class));
        menuExpense.setOnClickListener(v -> selectMenu(menuExpense, ExpenseActivity.class));
        menuIncome.setOnClickListener(v -> selectMenu(menuIncome, IncomeActivity.class));
        menuChart.setOnClickListener(v -> selectMenu(menuChart, ChartActivity.class));
        menuSetting.setOnClickListener(v -> selectMenu(menuSetting, SettingActivity.class));

        // Mặc định chọn Chart
        selectMenu(menuChart, null);
    }

    private void selectMenu(LinearLayout selectedMenu, Class<?> activityClass) {
        resetMenuStyles();

        ImageView icon = (ImageView) selectedMenu.getChildAt(0);
        TextView text = (TextView) selectedMenu.getChildAt(1);
        icon.setColorFilter(Color.RED);
        text.setTextColor(Color.RED);

        if (activityClass != null && activityClass != this.getClass()) {
            startActivity(new Intent(this, activityClass));
        }
    }

    private void resetMenuStyles() {
        resetMenu(menuHome);
        resetMenu(menuExpense);
        resetMenu(menuIncome);
        resetMenu(menuChart);
        resetMenu(menuSetting);
    }

    private void resetMenu(LinearLayout menu) {
        ImageView icon = (ImageView) menu.getChildAt(0);
        TextView text = (TextView) menu.getChildAt(1);
        icon.setColorFilter(Color.BLACK);
        text.setTextColor(Color.BLACK);
    }
}
