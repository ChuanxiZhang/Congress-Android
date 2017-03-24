package com.linciycuans.homework9.Activity;

/**
 * Created by linciy on 2016/11/18.
 */
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linciycuans.homework9.Information.LegislatorInformation;
import com.linciycuans.homework9.Jason.JsonFunction;
import com.linciycuans.homework9.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class Bi_details extends AppCompatActivity {

    JsonArray obj;
    List<String> list;

    List<String> detail_result = new ArrayList<>();
    String[] strings;
    int flag = 0;
    String save_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bi_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        new MyAsyncTask().execute();
    }


    public class MyAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Gson gson = new Gson();
            String url = "http://congress.api.sunlightfoundation.com/bills?apikey=46984d79855840a1bf5bb28498f6f766&bill_id=" + getIntent().getExtras().getString("value");
            obj = JsonFunction.getJsonContent(url);
            list = setList(obj);
            outputJson(list);


            return "";
        }

        private List<String> setList(JsonArray info) {

            List<String> listItems = new ArrayList<String>();
            Iterator list = info.iterator();
            while (list.hasNext()) {
                JsonElement lItems = (JsonElement) list.next();
                listItems.add(lItems.toString());
            }
            return listItems;
        }

        private void outputJson(List<String> list) {
            JsonParser parser = new JsonParser();
            JsonObject result = parser.parse(list.get(0)).getAsJsonObject();
            detail_result.add(result.get("bill_id").getAsString());
            try {
                detail_result.add(result.get("short_title").getAsString());
            } catch (UnsupportedOperationException e) {
                detail_result.add(result.get("official_title").getAsString());
            }
            detail_result.add(result.get("bill_type").getAsString().toUpperCase());
            detail_result.add(result.getAsJsonObject("sponsor").get("title").getAsString() + "." + result.getAsJsonObject("sponsor").get("last_name").getAsString() + ", " + result.getAsJsonObject("sponsor").get("first_name").getAsString());
            if (result.get("chamber").getAsString().equals("house")) {
                detail_result.add("House");
            } else if (result.get("chamber").getAsString().equals("senate")) {
                detail_result.add("Senate");
            } else if (result.get("chamber").getAsString().equals("joint")) {
                detail_result.add("Joint");
            }
            if (result.getAsJsonObject("history").get("active").getAsString().equals("true")) {
                detail_result.add("Active");
            } else if (result.getAsJsonObject("history").get("active").getAsString().equals("false")) {
                detail_result.add("New");
            }


            detail_result.add(getEDate(result.get("introduced_on").getAsString()));
            detail_result.add(result.getAsJsonObject("urls").get("congress").getAsString());
            try {
                detail_result.add(result.getAsJsonObject("last_version").get("version_name").getAsString());
            } catch (NullPointerException e) {
                detail_result.add("");
            }
            try {
                detail_result.add(result.getAsJsonObject("last_version").getAsJsonObject("urls").get("html").getAsString());
            } catch (NullPointerException e) {
                detail_result.add("None");
            }


        }

        public String getEDate(String str) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            ParsePosition pos = new ParsePosition(0);
            Date strtodate = formatter.parse(str, pos);
            String j = strtodate.toString();
            String[] k = j.split(" ");
            return k[1].toUpperCase() + " " + k[2] + "," + k[5];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            final ImageView Save = (ImageView) findViewById(R.id.bi_save);

            try {
                FileInputStream fis = getApplication().openFileInput("bi_save.txt");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, length);
                }
                fis.close();
                bos.close();
                String save_result = new String(bos.toByteArray(), "GB2312");
                strings = save_result.split(" ");
                for (int i = 0; i < strings.length; i++) {
                    if (strings[i].equals(detail_result.get(0))) {
                        Save.setImageDrawable(getResources().getDrawable(R.drawable.yellow));
                        flag = 1;
                    } else {
                        flag = 0;
                    }

                }
            } catch (Exception e) {
            }
            assert Save != null;
            Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag == 0) {
                        Save.setImageDrawable(getResources().getDrawable(R.drawable.yellow));
                        FileOutputStream out = null;
                        try {
                            out = getApplication().openFileOutput("bi_save.txt", Context.MODE_APPEND);
                            String s = detail_result.get(0) + " ";
                            out.write(s.getBytes("UTF-8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        flag = 1;

                    } else if (flag == 1) {
                        Save.setImageDrawable(getResources().getDrawable(R.drawable.star));
                        try {
                            FileInputStream fis = getApplication().openFileInput("bi_save.txt");
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) != -1) {
                                bos.write(buffer, 0, length);
                            }
                            fis.close();
                            bos.close();
                            save_result = new String(bos.toByteArray(), "GB2312");
                            save_result = save_result.replace(detail_result.get(0) + " ", "");
                        } catch (Exception e) {
                        }

                        FileOutputStream out = null;
                        try {
                            out = getApplication().openFileOutput("bi_save.txt", Context.MODE_PRIVATE);
                            String s = save_result;
                            out.write(s.getBytes("UTF-8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        flag = 0;
                    }
                }

            });


            TextView info;
            info = (TextView) findViewById(R.id.id_detail_table);
            info.setText(detail_result.get(0));
            info = (TextView) findViewById(R.id.title_detail_table);
            info.setText(detail_result.get(1));
            info = (TextView) findViewById(R.id.type_detail_table);
            info.setText(detail_result.get(2));
            info = (TextView) findViewById(R.id.sponser_detail_table);
            info.setText(detail_result.get(3));
            info = (TextView) findViewById(R.id.chamber_detail_table);
            info.setText(detail_result.get(4));
            info = (TextView) findViewById(R.id.status_detail_table);
            info.setText(detail_result.get(5));
            info = (TextView) findViewById(R.id.introduced_detail_table);
            info.setText(detail_result.get(6));
            info = (TextView) findViewById(R.id.congress_detail_table);
            info.setText(detail_result.get(7));
            info = (TextView) findViewById(R.id.version_detail_table);
            info.setText(detail_result.get(8));
            info = (TextView) findViewById(R.id.url_detail_table);
            info.setText(detail_result.get(9));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.setAction("action.refreshFriend");
            sendBroadcast(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
