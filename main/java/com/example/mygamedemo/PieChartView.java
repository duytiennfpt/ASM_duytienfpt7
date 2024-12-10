package com.example.mygamedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private Paint paint;
    private float expensePercentage = 0f;  // Giá trị tỷ lệ phần trăm chi tiêu
    private float incomePercentage = 0f;   // Giá trị tỷ lệ phần trăm thu nhập

    // Trạng thái hiển thị
    private boolean showIncomeOnly = false;
    private boolean showExpenseOnly = false;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    public void setData(float expense, float income) {
        // Tính toán tỷ lệ phần trăm
        float total = expense + income;
        expensePercentage = (expense / total) * 360;
        incomePercentage = (income / total) * 360;

        invalidate();  // Để vẽ lại view
    }

    public void showOnlyIncome() {
        showIncomeOnly = true;
        showExpenseOnly = false;
        invalidate(); // Vẽ lại
    }

    public void showOnlyExpense() {
        showIncomeOnly = false;
        showExpenseOnly = true;
        invalidate(); // Vẽ lại
    }

    public void showAll() {
        showIncomeOnly = false;
        showExpenseOnly = false;
        invalidate(); // Vẽ lại
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float radius = Math.min(centerX, centerY) - 20;

        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        if (showIncomeOnly) {
            // Vẽ chỉ thu nhập
            paint.setColor(getResources().getColor(R.color.green)); // Màu cho thu nhập
            canvas.drawArc(rectF, -90, incomePercentage, true, paint);

        } else if (showExpenseOnly) {
            // Vẽ chỉ chi tiêu
            paint.setColor(getResources().getColor(R.color.red)); // Màu cho chi tiêu
            canvas.drawArc(rectF, -90, expensePercentage, true, paint);

        } else {
            // Vẽ cả chi tiêu và thu nhập
            // Vẽ phần chi tiêu (Expense)
            paint.setColor(getResources().getColor(R.color.red)); // Màu cho chi tiêu
            canvas.drawArc(rectF, -90, expensePercentage, true, paint);

            // Vẽ phần thu nhập (Income)
            paint.setColor(getResources().getColor(R.color.green)); // Màu cho thu nhập
            canvas.drawArc(rectF, -90 + expensePercentage, incomePercentage, true, paint);
        }
    }
}
