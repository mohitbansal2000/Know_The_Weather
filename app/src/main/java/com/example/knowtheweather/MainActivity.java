package com.example.knowtheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    EditText editText1;
    TextView textView1;
    TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1=findViewById(R.id.editText1);
        textView1=findViewById(R.id.textView1);
        textView2=findViewById(R.id.textView2);
        hide();
    }

    private void hide(){
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    public void getWeather(View view){
    try{
        DownloadTask task= new DownloadTask();
        String encodcity= URLEncoder.encode(editText1.getText().toString(),"UTF-8");
        task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodcity + "&appid= YOUR_API_KEY");

       //enter your key

        InputMethodManager mgr=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText1.getWindowToken(),0);
    }
    catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(getApplicationContext(),"Could not found at the moment",Toast.LENGTH_SHORT).show();
    }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls){
            String result="";
            URL url;
            HttpsURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection =(HttpsURLConnection) url.openConnection();
                InputStream in =urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                int data = reader.read();
                while(data !=-1){
                    char current = (char)data;
                    result+=current;
                    data =reader.read();
                }
                return result;
            }
            catch(Exception e)
            {   e.printStackTrace();
                textView2.setText("City data not available or invalid city name");
                //Toast.makeText(getApplicationContext(),"Could not found at the moment",Toast.LENGTH_SHORT).show();
                return null;
            }
        }


        @Override
        protected void onPostExecute (String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject1= new JSONObject(s);
                String weatherInfo =jsonObject1.getString("weather");
                String mainData1 =jsonObject1.getString("main");
                String visible =jsonObject1.getString("visibility");
                String mainData="["+mainData1+"]";
                JSONArray arr1=new JSONArray(weatherInfo);
                JSONArray arr2=new JSONArray(mainData);
                String message1="";
                String message2="";
                String message3="Visibility: "+visible+"m";
/*{"coord":{"lon":-0.13,"lat":51.51},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],
        "base":"stations","main":{"temp":286.3,"feels_like":283.56,"temp_min":285.37,"temp_max":287.04,"pressure":1016,"humidity":81},"visibility":10000,
        "wind":{"speed":3.96,"deg":221},"rain":{"1h":0.46},"clouds":{"all":96},"dt":1602099808,"sys":{"type":3,"id":2019646,"country":"GB","sunrise":1602051085,
        "sunset":1602091488},"timezone":3600,"id":2643743,"name":"London","cod":200}*/

                for(int i=0; i<arr1.length();i++)
                {
                    JSONObject jsonPart1 =arr1.getJSONObject(i);
                    String main=jsonPart1.getString("main");
                    String description =jsonPart1.getString("description");
                    if (!main.equals("") && !description.equals(""))
                    { message1 += main + ":" +description +"\r\n"; }
                }

                    JSONObject jsonPart2 =arr2.getJSONObject(0);
                    String temp=jsonPart2.getString("temp");
                    String feels_like =jsonPart2.getString("feels_like");
                    String pressure =jsonPart2.getString("pressure");
                    String humidity =jsonPart2.getString("humidity");
                    String tempstring=String.format("%.1f",Float.parseFloat(temp)-273.15);
                    String tempfeel=String.format("%.1f",Float.parseFloat(feels_like)-273.15);
                    if (!pressure.equals("") && !temp.equals("") && !humidity.equals(""))
                    { message2 += "Temp: " +tempstring
                            +"\u00B0C\nFeels Like: " +tempfeel
                            +"\u00B0C\nHumidity: " + humidity +"%\nPressure: "+pressure+"mb\r\n";
                    }

                if (!message1.equals("") && !message2.equals("")) {
                    textView2.setText(message1+message2+message3);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Could not found at the moment",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not found at the moment",Toast.LENGTH_SHORT).show();
            }
          }
        }
}