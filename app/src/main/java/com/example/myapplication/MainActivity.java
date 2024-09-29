package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button stopButton;
    private EditText gridWidthInput;
    private EditText gridHeightInput;
    private EditText speedInput;
    private EditText playerXInput;
    private EditText playerYInput;
    private TextView commandText;

    private int gridWidth = 5; // По умолчанию 5
    private int gridHeight = 5; // По умолчанию 5
    private int playerX;
    private int playerY;
    private int speed = 3; // По умолчанию 3 секунды
    private boolean isPlaying;
    private boolean isStoppedByButton = false; // Новый флаг для ручной остановки
    private Handler handler;
    private Random random;

    // Звуковые проигрыватели для каждого направления и для остановки игры
    private MediaPlayer soundUp;
    private MediaPlayer soundDown;
    private MediaPlayer soundLeft;
    private MediaPlayer soundRight;
    private MediaPlayer soundStop;
    private MediaPlayer countdownSound; // Звук для отсчета

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация элементов интерфейса и медиа плееров
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        gridWidthInput = findViewById(R.id.gridWidthInput);
        gridHeightInput = findViewById(R.id.gridHeightInput);
        speedInput = findViewById(R.id.speedInput);
        playerXInput = findViewById(R.id.playerXInput);
        playerYInput = findViewById(R.id.playerYInput);
        commandText = findViewById(R.id.commandText);

        handler = new Handler();
        random = new Random();

        // Звуки для направлений и остановки игры
        soundUp = MediaPlayer.create(this, R.raw.yuxari);
        soundDown = MediaPlayer.create(this, R.raw.asagi);
        soundLeft = MediaPlayer.create(this, R.raw.sola);
        soundRight = MediaPlayer.create(this, R.raw.saga);
        soundStop = MediaPlayer.create(this, R.raw.sag);
        countdownSound = MediaPlayer.create(this, R.raw.countdown); // Звук отсчета

        // Установка значений по умолчанию
        speedInput.setText(String.valueOf(speed));
        gridWidthInput.setText(String.valueOf(gridWidth));
        gridHeightInput.setText(String.valueOf(gridHeight));
        playerXInput.setText("3");
        playerYInput.setText("3");

        startButton.setOnClickListener(v -> prepareGame());
        stopButton.setOnClickListener(v -> stopGame(true)); // Остановка по нажатию кнопки
    }

    // Подготовка к началу игры с отсчетом
    private void prepareGame() {
        commandText.setText("Hazırlıq...");
        countdownSound.start(); // Воспроизвести звук отсчета

        isStoppedByButton = false; // Сбрасываем флаг ручной остановки

        handler.postDelayed(this::startGame, 5000); // Задержка в 5 секунд перед стартом
    }

    private void startGame() {
        // Получение параметров от пользователя
        try {
            gridWidth = Integer.parseInt(gridWidthInput.getText().toString());
            gridHeight = Integer.parseInt(gridHeightInput.getText().toString());
            speed = Integer.parseInt(speedInput.getText().toString()) * 1000;
            playerX = Integer.parseInt(playerXInput.getText().toString());
            playerY = Integer.parseInt(playerYInput.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Все поля должны содержать целые числа!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка корректности ввода
        if (gridWidth <= 1) {
            Toast.makeText(this, "Ширина сетки должна быть больше 1!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gridHeight <= 1) {
            Toast.makeText(this, "Высота сетки должна быть больше 1!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (speed < 0) {
            Toast.makeText(this, "Скорость должна быть положительным числом!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (playerX < 1 || playerX > gridWidth || playerY < 1 || playerY > gridHeight) {
            Toast.makeText(this, "Начальные координаты должны быть в пределах сетки!", Toast.LENGTH_SHORT).show();
            return;
        }

        isPlaying = true;
        commandText.setText("Oyun başladı!");
        gameLoop();
    }

    private void stopGame(boolean stoppedByButton) {
        isPlaying = false;
        isStoppedByButton = stoppedByButton; // Устанавливаем флаг ручной остановки, если это так
        soundStop.start();
        commandText.setText("Oyun dayandırıldı. Oyunçu: (" + playerX + ", " + playerY + ")");

        // Если игра не остановлена вручную, перезапускаем через 5 секунд
        if (!isStoppedByButton) {
            handler.postDelayed(this::prepareGame, 5000);
        }
    }

    private void gameLoop() {
        if (!isPlaying) return;

        String command = generateCommand();
        commandText.setText("Komanda: " + command);
        playSound(command);

        handler.postDelayed(() -> {
            if (isPlaying) {
                switch (command) {
                    case "Yuxarı":
                        playerY--;
                        break;
                    case "Aşağı":
                        playerY++;
                        break;
                    case "Sola":
                        playerX--;
                        break;
                    case "Sağa":
                        playerX++;
                        break;
                }

                if (playerX < 1 || playerX > gridWidth || playerY < 1 || playerY > gridHeight) {
                    stopGame(false); // Останавливаем игру, если игрок вышел за границы
                } else {
                    commandText.setText("Komanda: " + command + " - Oyunçu: (" + playerX + ", " + playerY + ")");
                    gameLoop();
                }
            }
        }, speed);
    }

    private String generateCommand() {
        String[] commands = {"Yuxarı", "Aşağı", "Sola", "Sağa"};
        return commands[random.nextInt(commands.length)];
    }

    private void playSound(String command) {
        switch (command) {
            case "Yuxarı":
                soundUp.start();
                break;
            case "Aşağı":
                soundDown.start();
                break;
            case "Sola":
                soundLeft.start();
                break;
            case "Sağa":
                soundRight.start();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundUp != null) soundUp.release();
        if (soundDown != null) soundDown.release();
        if (soundLeft != null) soundLeft.release();
        if (soundRight != null) soundRight.release();
        if (soundStop != null) soundStop.release();
        if (countdownSound != null) countdownSound.release();
    }
}
