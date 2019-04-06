package com.example.tested.guessthesoccerplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> soccerURLs = new ArrayList<String>();
    ArrayList<String> soccerNames = new ArrayList<String>();
    int ChoosenSoccer = 0;
    ImageView imageView;
    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return  myBitmap;

            }catch (Exception e){
                e.printStackTrace();
                return  null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection  urlConnection = null;
            String result = "";

            try {

                url = new URL(urls[0]);
                urlConnection =(HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                return result;

            } catch (Exception e){
                e.printStackTrace();
                return  null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);


        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("https://www.biography.com/news/best-soccer-players-of-all-time-world-cup").get();
            String[] splitResult = result.split("http://www.w3.org/1999/xlink");
            Pattern p = Pattern.compile("img src =\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()){
                soccerURLs.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()){
                soccerNames.add(m.group(1));
            }
            Random random = new Random();
            ChoosenSoccer = random.nextInt(soccerURLs.size());

            ImageDownloader imageTast = new ImageDownloader();
            Bitmap soccerImg = imageTast.execute(soccerURLs.get(ChoosenSoccer)).get();
            imageView.setImageBitmap(soccerImg);


            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;

            for (int i=0; i<4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] =soccerNames.get(ChoosenSoccer);
                }else {
                    incorrectAnswerLocation = random.nextInt(soccerURLs.size());

                    while (incorrectAnswerLocation == ChoosenSoccer){

                        incorrectAnswerLocation = random.nextInt(soccerURLs.size());

                    }

                    answers[i] = soccerNames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);



        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
