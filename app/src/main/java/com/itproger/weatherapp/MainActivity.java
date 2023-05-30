package com.itproger.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Поля, что будут ссылаться на объекты из дизайна
    private EditText user_field;
    private Button main_btn;
    private TextView result_info;
    private TextView result_info1;
    private TextView result_info2;


    @Override
    protected void onCreate(Bundle savedInstanceState) { // Сработает при создании Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Устанавливаем ссылки на объекты из дизайна
        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);
        result_info1 = findViewById(R.id.result_info1);
        result_info2 = findViewById(R.id.result_info2);

        // Обработчик нажатия на кнопку
        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Если ничего не ввели в поле, то выдаем всплывающую подсказку
                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                else {
                    // Если ввели, то формируем ссылку для получения погоды
                    String city = user_field.getText().toString();
                    String key = "ac358146a49212a075123b253d62db3c";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    // Запускаем класс для получения погоды
                    new GetURLData().execute(url);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetURLData extends AsyncTask<String, String, String> {

        // Будет выполнено до отправки данных по URL
        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        // Будет выполняться во время подключения по URL
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Создаем URL подключение, а также HTTP подключение
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Создаем объекты для считывания данных из файла
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                // Генерируемая строка
                StringBuilder buffer = new StringBuilder();
                String line = "";

                // Считываем файл и записываем все в строку
                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                // Возвращаем строку
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Закрываем соединения
                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        // Выполняется после завершения получения данных
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Конвертируем JSON формат и выводим данные в текстовом поле
            try {
                JSONObject jsonObject = new JSONObject(result) ;
                result_info.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp"));
                result_info1.setText("Скорость ветра: " + jsonObject.getJSONObject("wind").getInt("speed"));
                result_info2.setText("Влажность воздуха: " + jsonObject.getJSONObject("main").getInt("humidity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
}
