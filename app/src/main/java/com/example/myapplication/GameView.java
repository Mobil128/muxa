package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class GameView extends View {

    private int gridWidth = 5;
    private int gridHeight = 5;
    private int playerX = 1;
    private int playerY = 1;
    private int cellWidth;
    private int cellHeight;
    private Paint gridPaint;
    private Bitmap playerBitmap; // Добавляем Bitmap для мухи

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(0xFF000000); // Черный цвет для сетки
        gridPaint.setStrokeWidth(5); // Толщина линий

        // Загружаем изображение мухи из ресурсов
        playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.muha);
    }

    public void setGrid(int width, int height) {
        this.gridWidth = width;
        this.gridHeight = height;
        invalidate(); // Перерисовать экран
    }

    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        invalidate(); // Перерисовать экран
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
      //  cellWidth = w / gridWidth;
       // cellHeight = h / gridHeight;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int w =getWidth();
        int h =getHeight();
        cellWidth = w / gridWidth;
        cellHeight = h / gridHeight;
        // Рисуем сетку
        for (int i = 1; i < gridWidth; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, getHeight(), gridPaint);
        }
        for (int i = 1; i < gridHeight; i++) {
            canvas.drawLine(0, i * cellHeight, getWidth(), i * cellHeight, gridPaint);
        }

        // Рисуем муху (игрока) в клетке
        int playerLeft = (playerX - 1) * cellWidth;
        int playerTop = (playerY - 1) * cellHeight;

        // Масштабируем изображение мухи, чтобы она соответствовала размеру ячейки
        Bitmap scaledPlayerBitmap = Bitmap.createScaledBitmap(playerBitmap, cellWidth, cellHeight, false);

        // Рисуем изображение мухи в клетке
        canvas.drawBitmap(scaledPlayerBitmap, playerLeft, playerTop, null);
    }
}
