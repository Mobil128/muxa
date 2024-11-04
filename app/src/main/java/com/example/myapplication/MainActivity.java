package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
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

    private Button cancelButton; // Новая кнопка для отмены подготовки
    private EditText gridWidthInput;
    private EditText gridHeightInput;
    private EditText speedInput;
    private EditText playerXInput;
    private EditText playerYInput;
    private TextView commandText;
    private CheckBox musicCheckBox;
    private GameView gameView;

    private int gridWidth = 10 ;// По умолчанию 5
    private int gridHeight = 10; // По умолчанию 5
    private int playerX;
    private int playerY;
    private int speed = 1; // По умолчанию 4.5 секунды
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
    private MediaPlayer backgroundMusic; // Фоновая музыка

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

        cancelButton = findViewById(R.id.cancelButton); // Инициализация кнопки отмены
        gridWidthInput = findViewById(R.id.gridWidthInput);
        gridHeightInput = findViewById(R.id.gridHeightInput);
        speedInput = findViewById(R.id.speedInput);
        playerXInput = findViewById(R.id.playerXInput);
        playerYInput = findViewById(R.id.playerYInput);
        commandText = findViewById(R.id.commandText);
        gameView = findViewById(R.id.gameView); // Инициализация GameView

        musicCheckBox = findViewById(R.id.musicCheckBox);

// Проверка состояния чекбокса при изменении
        musicCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!backgroundMusic.isPlaying()) {
                    backgroundMusic.start(); // Запустить фоновую музыку
                }
            } else {
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.pause(); // Поставить фоновую музыку на паузу
                }
            }
        });



        handler = new Handler();
        random = new Random();

        // Звуки для направлений и остановки игры
        soundUp = MediaPlayer.create(this, R.raw.yuxari);
        soundDown = MediaPlayer.create(this, R.raw.asagi);
        soundLeft = MediaPlayer.create(this, R.raw.sola);
        soundRight = MediaPlayer.create(this, R.raw.saga);
        soundStop = MediaPlayer.create(this, R.raw.sag);
        countdownSound = MediaPlayer.create(this, R.raw.countdown1); // Звук отсчета

        // Фоновая музыка
        backgroundMusic = MediaPlayer.create(this, R.raw.muxa__fon); // Файл фоновой музыки
        backgroundMusic.setLooping(true); // Зацикливание музыки
        backgroundMusic.setVolume(0.2f, 0.2f); // Устанавливаем уровень громкости фоновой музыки

        // Установка значений по умолчанию
        speedInput.setText(String.valueOf(speed));
        gridWidthInput.setText(String.valueOf(gridWidth));
        gridHeightInput.setText(String.valueOf(gridHeight));
        playerXInput.setText("5");
        playerYInput.setText("5");

        startButton.setOnClickListener(v -> prepareGame());

        cancelButton.setOnClickListener(v -> cancelPreparation()); // Обработчик нажатия кнопки отмены
    }

    // Подготовка к началу игры с отсчетом
    private void prepareGame() {
        commandText.setText("Hazırlıq...");
        countdownSound.start(); // Воспроизвести звук отсчета

        isStoppedByButton = false; // Сбрасываем флаг ручной остановки

        handler.postDelayed(this::startGame, 5000); // Задержка в 5 секунд перед стартом
    }

    // Метод для отмены подготовки
    private void cancelPreparation() {
        handler.removeCallbacksAndMessages(null); // Удалить все запланированные действия
        commandText.setText("Oyun dayandı"); // Обновление текста на экране
        isPlaying = false; // Установка флага, чтобы избежать старта игры
    }

    private void startGame() {
        // Получение параметров от пользователя
        try {
            gridWidth = Integer.parseInt(gridWidthInput.getText().toString());
            gridHeight = Integer.parseInt(gridHeightInput.getText().toString());
            speed = Integer.parseInt(speedInput.getText().toString()) * 1500;
            playerX = Integer.parseInt(playerXInput.getText().toString());
            playerY = Integer.parseInt(playerYInput.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Все поля должны содержать целые числа!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка значений перед запуском игры
        if (gridWidth <= 1 || gridHeight <= 1 || speed < 0 ||
                playerX < 1 || playerX > gridWidth || playerY < 1 || playerY > gridHeight) {
            Toast.makeText(this, "Пожалуйста, проверьте параметры сетки и игрока!", Toast.LENGTH_SHORT).show();
            return;
        }

        isPlaying = true;
        commandText.setText("Oyun başladı!");
        gameView.setGrid(gridWidth, gridHeight); // Установите сетку в GameView
        gameView.setPlayerPosition(playerX, playerY); // Установите позицию игрока в GameView
        // backgroundMusic.start(); // Запускаем фоновую музыку
        gameLoop();
    }

    private void stopGame(boolean stoppedByButton) {
        isPlaying = false;
        isStoppedByButton = stoppedByButton; // Устанавливаем флаг ручной остановки, если это так
        stopSounds(); // Останавливаем звуки направлений
        soundStop.start(); // Звук остановки игры
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
                    gameView.setPlayerPosition(playerX, playerY); // Обновляем позицию игрока на экране
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

    private void stopSounds() {
        if (soundUp.isPlaying()) soundUp.pause();
        if (soundDown.isPlaying()) soundDown.pause();
        if (soundLeft.isPlaying()) soundLeft.pause();
        if (soundRight.isPlaying()) soundRight.pause();
    }



  /*  // Запуск фоновой музыки, когда экран активен
    @Override
    protected void onResume() {
        super.onResume();
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.start(); // Запускаем фоновую музыку
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.pause(); // Ставим на паузу, если игра не активна
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundUp.release();
        soundDown.release();
        soundLeft.release();
        soundRight.release();
        soundStop.release();
        countdownSound.release(); // Освобождение ресурсов
        backgroundMusic.release(); // Освобождение ресурсов фоновой музыки
    }
}
