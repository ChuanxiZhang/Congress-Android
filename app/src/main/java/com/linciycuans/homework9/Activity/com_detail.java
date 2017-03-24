package com.linciycuans.homework9.Activity;


/**
 * Created by linciy on 2016/11/18.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linciycuans.homework9.Information.LegislatorInformation;
import com.linciycuans.homework9.Jason.JsonFunction;
import com.linciycuans.homework9.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.util.logging.LogRecord;


public class com_detail extends AppCompatActivity {

    JsonArray obj;
    List<String> list;
    String[] strings;
    int flag = 0;
    String save_result;

    List<String> detail_result = new ArrayList<String>();

    private List<LegislatorInformation> legislatorInfo = new ArrayList<LegislatorInformation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_detail);
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
            String url = "http://congress.api.sunlightfoundation.com/committees?apikey=46984d79855840a1bf5bb28498f6f766&committee_id=" + getIntent().getExtras().getString("value");
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
            detail_result.add(result.get("committee_id").getAsString());


            detail_result.add(result.get("name").getAsString());
            if (result.get("chamber").getAsString().equals("house")) {
                detail_result.add("House");
            } else if (result.get("chamber").getAsString().equals("senate")) {
                detail_result.add("Senate");
            } else if (result.get("chamber").getAsString().equals("joint")) {
                detail_result.add("Joint");
            }
            try {
                detail_result.add(result.get("parent_committee_id").getAsString());
            } catch (NullPointerException e) {
                detail_result.add("N.A.");
            }
            try {
                detail_result.add(result.get("phone").getAsString());
            } catch (NullPointerException e) {
                detail_result.add("N.A.");
            }
            try {
                detail_result.add(result.get("office").getAsString());
            } catch (NullPointerException e) {
                detail_result.add("N.A.");
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            final ImageView Save = (ImageView) findViewById(R.id.com_save);

            try {
                FileInputStream fis = getApplication().openFileInput("com_save.txt");
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
                        //Picasso.with(getApplicationContext()).load(R.drawable.yellow).into(Save);
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
                        //Picasso.with(getApplicationContext()).load(R.drawable.yellow).into(Save);
                        Save.setImageDrawable(getResources().getDrawable(R.drawable.yellow));
                        FileOutputStream out = null;
                        try {
                            //out= getApplication().openFileOutput("le_save.txt", Context.MODE_PRIVATE);
                            out = getApplication().openFileOutput("com_save.txt", Context.MODE_APPEND);
                            String s = detail_result.get(0) + " ";
                            out.write(s.getBytes("UTF-8"));
                            //out.write("".getBytes());
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
                        //Picasso.with(getApplicationContext()).load(R.drawable.star).into(Save);
                        try {
                            FileInputStream fis = getApplication().openFileInput("com_save.txt");
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
                            out = getApplication().openFileOutput("com_save.txt", Context.MODE_PRIVATE);
                            //out = getApplication().openFileOutput("le_save.txt", Context.MODE_APPEND);
                            String s = save_result;
                            out.write(s.getBytes("UTF-8"));
                            //out.write("".getBytes());
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
            ImageView image;
            info = (TextView) findViewById(R.id.comid_detail_table);
            info.setText(detail_result.get(0));
            info = (TextView) findViewById(R.id.comname_detail_table);
            info.setText(detail_result.get(1));
            info = (TextView) findViewById(R.id.comchamber_detail_table);
            info.setText(detail_result.get(2));
            image = (ImageView) findViewById(R.id.chamber_image);
            if (detail_result.get(2).equals("house")) {
                //Picasso.with(getApplicationContext()).load(R.drawable.house).into(image);
                image.setImageDrawable(getResources().getDrawable(R.drawable.house));
            } else {
                //Picasso.with(getApplicationContext()).load(R.drawable.senate).into(image);
                image.setImageDrawable(getResources().getDrawable(R.drawable.senate));
            }
            info = (TextView) findViewById(R.id.comparent_detail_table);
            info.setText(detail_result.get(3));
            info = (TextView) findViewById(R.id.comcontact_detail_table);
            info.setText(detail_result.get(4));
            info = (TextView) findViewById(R.id.comoffice_detail_table);
            info.setText(detail_result.get(5));

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
