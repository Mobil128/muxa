package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
    private Handler handler;
    private Random random;

    // Звуковые проигрыватели для каждого направления и для остановки игры
    private MediaPlayer soundUp;
    private MediaPlayer soundDown;
    private MediaPlayer soundLeft;
    private MediaPlayer soundRight;
    private MediaPlayer soundStop;

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

        // Инициализация элементов интерфейса
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

        // Загрузка звуков
        soundUp = MediaPlayer.create(this, R.raw.yuxari);
        soundDown = MediaPlayer.create(this, R.raw.asagi);
        soundLeft = MediaPlayer.create(this, R.raw.sola);
        soundRight = MediaPlayer.create(this, R.raw.saga);
        soundStop = MediaPlayer.create(this, R.raw.sag); // Звук остановки игры

        // Установка значений по умолчанию
        speedInput.setText(String.valueOf(speed));
        gridWidthInput.setText(String.valueOf(gridWidth));
        gridHeightInput.setText(String.valueOf(gridHeight));
        playerXInput.setText("3"); // Установка начальной позиции игрока по умолчанию
        playerYInput.setText("3");

        startButton.setOnClickListener(v -> startGame());
        stopButton.setOnClickListener(v -> stopGame());
    }

    private void startGame() {
        // Получение параметров от пользователя
        gridWidth = Integer.parseInt(gridWidthInput.getText().toString());
        gridHeight = Integer.parseInt(gridHeightInput.getText().toString());
        speed = Integer.parseInt(speedInput.getText().toString()) * 1000; // Конвертируем в миллисекунды
        playerX = Integer.parseInt(playerXInput.getText().toString());
        playerY = Integer.parseInt(playerYInput.getText().toString());

        // Проверка на корректность ввода
        if (playerX < 1 || playerX > gridWidth || playerY < 1 || playerY > gridHeight) {
            Toast.makeText(this, "Başlanğıc koordinatları şəbəkənin daxilində olmalıdır!", Toast.LENGTH_SHORT).show();
            return;
        }

        isPlaying = true;
        commandText.setText("Oyun başladı!");

        // Начинаем выполнение команд
        gameLoop();
    }

    private void gameLoop() {
        if (!isPlaying) return;

        // Генерируем случайную команду (вверх, вниз, влево, вправо)
        String command = generateCommand();
        commandText.setText("Komanda: " + command);

        // Проигрываем звук команды в зависимости от направления
        playSound(command);

        // Обрабатываем команду
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

                // Проверяем, вышел ли игрок за границы
                if (playerX < 1 || playerX > gridWidth || playerY < 1 || playerY > gridHeight) {
                    stopGame(); // Останавливаем игру, если вышел за границы
                } else {
                    // Выводим положение игрока
                    commandText.setText("Komanda: " + command + " - Oyunçu: (" + playerX + ", " + playerY + ")");
                    gameLoop(); // Продолжаем игру
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

    private void stopGame() {
        isPlaying = false;
        soundStop.start(); // Воспроизводим звук остановки игры
        commandText.setText("Oyun dayandırıldı. Oyunçu: (" + playerX + ", " + playerY + ")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundUp != null) {
            soundUp.release(); // Освобождаем ресурсы медиа плеера
            soundUp = null;
        }
        if (soundDown != null) {
            soundDown.release();
            soundDown = null;
        }
        if (soundLeft != null) {
            soundLeft.release();
            soundLeft = null;
        }
        if (soundRight != null) {
            soundRight.release();
            soundRight = null;
        }
        if (soundStop != null) {
            soundStop.release();
            soundStop = null;
        }
    }
}
