package com.example.acer.weatherapplication;
/**Author:Manasa
 * Date:05/11/2018
 **/
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

//initialising text view variables
    TextView txtCityname,txtTemp,txtTemp_min,txtTemp_max,txtMain,txtDescription,txtHumidity,txtAll;
    EditText inputCitytxt;
    Button get_weather_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//  to find the views and to display the results
        txtCityname = (TextView) findViewById(R.id.txtCityname);
        txtTemp = (TextView) findViewById(R.id.txtTemp);
        txtTemp_min = (TextView) findViewById(R.id.txtTemp_min);
        txtTemp_max = (TextView) findViewById(R.id.txtTemp_max);
        txtMain = (TextView) findViewById(R.id.txtMain);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtAll = (TextView) findViewById(R.id.txtAll);

        get_weather_btn = (Button)findViewById(R.id.get_weather_btn);
        inputCitytxt   = (EditText)findViewById(R.id.inputCity);
// to display default halifax data when no data is provided by user in city column
        Jsondata_parsing json_parser = new Jsondata_parsing();
        String urlString = null;
        urlString = json_parser.apiRequest("Halifax");
        new weather().execute(urlString);

        get_weather_btn.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String inputCity = inputCitytxt.getText().toString();
                        Log.d("City Name entered : ", inputCity );
                        Jsondata_parsing JS = new Jsondata_parsing();
                        String urlString = null;
                        urlString = JS.apiRequest(inputCity); // call to method apiRequest in Jsondata_parsind activity to return the string

                        new weather().execute(urlString);
                    }
                });

    }
// asynctaskhelper extends method asynctask to run the operation in back ground thread by avoiding load on main thread.
    private class weather extends AsyncTask<String,Void,String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];
            // Json data parsing of city
            Jsondata_parsing json_parser = new Jsondata_parsing();
            stream = json_parser.getJsonData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String str){
            super.onPostExecute( str);
// if city not found error message is displayed as toast message in home screen UI
            try {
                if (str.contains("city not found")) {

                    Log.d("Invalid city name : ",str);
                    Toast.makeText(getApplicationContext(), "Could not find weather, check city name and try again", Toast.LENGTH_LONG).show();
                    return;
                }
                // Json data parsing using Json objects considering main as object and weather as array , as weather inturn has multiple values lies within
                JSONObject mainJsonObject = (new JSONObject(str)).getJSONObject("main");
                JSONArray weatherJsonArray = (new JSONObject(str)).getJSONArray("weather");
                JSONObject weatherJsonobject = weatherJsonArray.getJSONObject(0);
                // log messages to check the output in terminal for debugging purpose
                Log.d("json err data", str);
                // clouds as json object is parsed
                JSONObject cloudsJsonObject = (new JSONObject(str)).getJSONObject("clouds");
                String city = (new JSONObject(str)).getString("name");
// temperature which is string is present as kelvin metrics which is changes to celsius metrics using formula
                String temp = mainJsonObject.getString("temp");
                double temp1 =  Double.parseDouble(temp); // conversion of string to double
                double temperature = temp1 - 273.15; // formula to convert kelvin to celsius
// similar to temp , temp_min and temp_max are converted to celsius from kelvin
                String tempMin = mainJsonObject.getString("temp_min");
                double temp2 =  Double.parseDouble(tempMin);
                double temp_min = temp2 - 273.15;

                String tempMax = mainJsonObject.getString("temp_max");
                double temp3 =  Double.parseDouble(tempMax);
                double temp_max = temp3 - 273.15;
//rest of the information like description,humidity is retrieved and parsed using Jsonobject
                String main = weatherJsonobject.getString("main");
                String description = weatherJsonobject.getString("description");
                String humidity = mainJsonObject.getString("humidity");
                String all = cloudsJsonObject.getString("all");

                txtCityname.setText(String.format("%s",city));
                txtTemp.setText(String.format("%.2f °C",temperature)); // format of double value of temp to two decimal places
                // and if possible round off to nearest value
                txtTemp_min.setText(String.format("Min :%.2f °C",temp_min));
                txtTemp_max.setText(String.format("Max : %.2f °C",temp_max));
                txtMain.setText(String.format("%s",main));
                txtDescription.setText(String.format("%s",description));
                txtHumidity.setText(String.format("Humidity : %s ",humidity +"%")); // concatenation of percentage at end
                txtAll.setText(String.format("Clouds : %s",all +"%"));

// All log messages helpful for debugging
                Log.d("cityname",city);
                Log.d("temp",String.valueOf(temperature));
                Log.d("temp_min",String.valueOf(temp_min));

                Log.d("temp_max",String.valueOf(temp_max));
                Log.d("main",main);
                Log.d("description",description);

                Log.d("humidity",humidity);
                Log.d("all",all);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


}
