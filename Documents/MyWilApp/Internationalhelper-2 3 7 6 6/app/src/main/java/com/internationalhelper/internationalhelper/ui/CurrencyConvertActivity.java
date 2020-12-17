package com.internationalhelper.internationalhelper.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.internationalhelper.internationalhelper.R;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CurrencyConvertActivity extends AppCompatActivity {

    EditText editText_usd;
    Button button_convert,button_clear;
    ArrayList<String> fromList;
    ArrayList<String> toList;
    Spinner fromSpinner,toSpinner;
    TextView tv_converted,tv_result;
    String URL="http://www.apilayer.net/api/live?access_key=9f1e562b408d4cd68945eac4e4cfcd29";

    String URL2 = "http://api.currencylayer.com/live?access_key=87ce39398667d1fa1c45d057c0873eb4";

    ArrayList<String> cur = new ArrayList<String>();

    String jsonData = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_convert);

        loadData();
        cur.add("USDCAD");
        cur.add("USDHKD");
        cur.add("USDINR");
        cur.add("USDLKR");
        cur.add("USDPHP");
        cur.add("USDAUD");
        cur.add("USDCNY");
        cur.add("USDGBP");


        button_clear=findViewById(R.id.btn_clear);
        fromSpinner=findViewById(R.id.spinner_from);
        toSpinner=findViewById(R.id.spinner_to);
        tv_converted=findViewById(R.id.txt_converted);
        tv_result=findViewById(R.id.txt_result);
        button_convert=findViewById(R.id.btn_convert);
        editText_usd=findViewById(R.id.edt_doller);
        fromList=new ArrayList<>();
        toList=new ArrayList<>();
//        fromList.add("USDUSD");
//        fromList.add("USDINR");
//        fromList.add("USDEUR");
//        fromList.add("USDCAD");
//        fromList.add("USDAUD");
        fromList.add("Canada");
        fromList.add("Hong Khong");
        fromList.add("India");
        fromList.add("Sri Lanka");
        fromList.add("Phillippines");
        fromList.add("Australia");
        fromList.add("China");
        fromList.add("United Kingdom");
        ArrayAdapter<String> fromAdapter=new ArrayAdapter<String>(CurrencyConvertActivity.this,android.R.layout.simple_spinner_dropdown_item,fromList);
        fromSpinner.setAdapter(fromAdapter);
//        toList.add("USDUSD");
//        toList.add("USDINR");
//        toList.add("USDEUR");
//        toList.add("USDCAD");
//        toList.add("USDAUD");
        toList.add("Canada");
        toList.add("Hong Khong");
        toList.add("India");
        toList.add("Sri Lanka");
        toList.add("Phillippines");
        toList.add("Australia");
        toList.add("China");
        toList.add("United Kingdom");
        ArrayAdapter<String> toAdapter=new ArrayAdapter<String>(CurrencyConvertActivity.this,android.R.layout.simple_spinner_dropdown_item,toList);
        toSpinner.setAdapter(toAdapter);

        button_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usd=editText_usd.getText().toString().trim();
                if (usd.equals("")){
                    editText_usd.setError("Please enter USD");
                }else if(fromSpinner.getSelectedItemPosition() == toSpinner.getSelectedItemPosition()){
                    Toast.makeText(CurrencyConvertActivity.this, "Please select a different currency", Toast.LENGTH_SHORT).show();
                }
                else {
                    convertCurrency(usd);
                }
            }
        });


        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_usd.setText("");
                tv_converted.setText("");
                tv_result.setText("");
                tv_result.setText("0");
                tv_converted.setText("Result");
            }
        });



    }

    private void loadData() {
        StringRequest request = new StringRequest(URL2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonData = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }

    private void convertCurrency(String toConvert) {

        double convData = Double.parseDouble(toConvert);

        try {
            JSONObject json = new JSONObject(jsonData);
            JSONObject quotes = json.getJSONObject("quotes");
            Log.i("NIK", "From Currency => " + cur.get(fromSpinner.getSelectedItemPosition()));
            double usd = convData/quotes.getDouble(cur.get(fromSpinner.getSelectedItemPosition()));
            Log.i("NIK", "USD Data => " + usd);

            double conversion = usd * quotes.getDouble(cur.get(toSpinner.getSelectedItemPosition()));
            Log.i("NIK", "To Currency => " + cur.get(toSpinner.getSelectedItemPosition()));
            Log.i("NIK", "USD Data => " + conversion);
            tv_converted.setText("Result: "+toSpinner.getSelectedItem().toString());
            tv_result.setText(String.valueOf(conversion));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        /*StringRequest request = new StringRequest(URL2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("NIK", response);
                try {
                    double convData = Double.parseDouble(toConvert);

                    JSONObject json = new JSONObject(response);
                    JSONObject quotes = json.getJSONObject("quotes");
                    double usd = convData/quotes.getDouble(cur.get(fromSpinner.getSelectedItemPosition()));
                    double conversion = usd * quotes.getDouble(cur.get(toSpinner.getSelectedItemPosition()));
                    tv_result.setText(String.valueOf(conversion));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        queue.add(request);*/

    }

    private void convertUsd(String usd) {
        StringRequest request=new StringRequest(Request.Method.GET,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("NIK",response);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject quotes= json.getJSONObject("quotes");
                    //double usd=Double.valueOf(editText_usd.getText().toString());
                    double multiplier=Double.valueOf(quotes.get(toSpinner.getSelectedItem().toString()).toString());
                    double result =  multiplier*Integer.parseInt(usd);
                    tv_converted.setText("Result: "+toSpinner.getSelectedItem().toString());
                    tv_result.setText(String.valueOf("₹ "+result));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
            }
        });
        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}