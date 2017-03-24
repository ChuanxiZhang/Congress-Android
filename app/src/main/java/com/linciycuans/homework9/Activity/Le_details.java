package com.linciycuans.homework9.Activity;


/**
 * Created by linciy on 2016/11/17.
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linciycuans.homework9.Information.LegislatorInformation;
import com.linciycuans.homework9.Information.TextProgressBar;
import com.linciycuans.homework9.Jason.JsonFunction;
import com.linciycuans.homework9.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class Le_details extends AppCompatActivity {

    JsonArray obj;
    List<String> list;


    List<String> detail_result = new ArrayList<String>();
    String[] strings;
    int flag = 0;
    String save_result;

    private List<LegislatorInformation> legislatorInfo = new ArrayList<LegislatorInformation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.le_details);
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
            String url = "http://congress.api.sunlightfoundation.com/legislators?apikey=46984d79855840a1bf5bb28498f6f766&bioguide_id=" + getIntent().getExtras().getString("value");
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
            detail_result.add(result.get("bioguide_id").getAsString());
            detail_result.add(result.get("party").getAsString());
            detail_result.add(result.get("title").getAsString() + "." + result.get("last_name").getAsString() + ", " + result.get("first_name").getAsString());
            try {
                detail_result.add(result.get("oc_email").getAsString());
            } catch (UnsupportedOperationException e) {
                detail_result.add("");
            }
            if (result.get("chamber").getAsString().equals("house")) {
                detail_result.add("House");
            } else if (result.get("chamber").getAsString().equals("senate")) {
                detail_result.add("Senate");
            } else if (result.get("chamber").getAsString().equals("joint")) {
                detail_result.add("Joint");
            }
            detail_result.add(result.get("phone").getAsString());
            detail_result.add(getEDate(result.get("term_start").getAsString()));
            detail_result.add(getEDate(result.get("term_end").getAsString()));
            detail_result.add(result.get("office").getAsString());
            detail_result.add(result.get("state").getAsString());

            try {
                detail_result.add(result.get("fax").getAsString());
            } catch (UnsupportedOperationException e) {
                detail_result.add("");
            }

            detail_result.add(getEDate(result.get("birthday").getAsString()));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = new java.util.Date();
            String nowdate = df.format(date);
            try {
                int start = daysBetween(result.get("term_start").getAsString(), nowdate);
                int end = daysBetween(result.get("term_start").getAsString(), result.get("term_end").getAsString());
                detail_result.add(String.valueOf(start * 100 / end));
            } catch (Exception e) {
            }
            try {
                detail_result.add(result.get("facebook_id").getAsString());
            } catch (NullPointerException e) {
                detail_result.add("");
            }
            try {
                detail_result.add(result.get("twitter_id").getAsString());
            } catch (NullPointerException e) {
                detail_result.add("");
            }
            try {
                detail_result.add(result.get("website").getAsString());
            } catch (NullPointerException e) {
                detail_result.add("");
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


        public int daysBetween(String smdate, String bdate) throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(smdate));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(bdate));
            long time2 = cal.getTimeInMillis();
            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(between_days));
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            TextView info;
            ImageView image;
            image = (ImageView) findViewById(R.id.facebook_image);
            assert image != null;
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail_result.get(13).equals("")) {
                        Toast.makeText(getApplicationContext(), "No Facebook",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Uri uri = Uri.parse("http://www.facebook.com/" + detail_result.get(13));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });
            image = (ImageView) findViewById(R.id.tiwiter_image);
            assert image != null;
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail_result.get(13).equals("")) {
                        Toast.makeText(getApplicationContext(), "No Twitter",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Uri uri = Uri.parse("http://www.twitter.com/" + detail_result.get(14));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });
            image = (ImageView) findViewById(R.id.web_image);
            assert image != null;
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail_result.get(13).equals("")) {
                        Toast.makeText(getApplicationContext(), "No Website",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Uri uri = Uri.parse(detail_result.get(15));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });
            TextProgressBar progressBar = (TextProgressBar) findViewById(R.id.progressbar);

            final ImageView Save = (ImageView) findViewById(R.id.le_save);

            try {

                FileInputStream fis = getApplication().openFileInput("le_save.txt");
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
                            out = getApplication().openFileOutput("le_save.txt", Context.MODE_APPEND);
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
                            FileInputStream fis = getApplication().openFileInput("le_save.txt");
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
                            out = getApplication().openFileOutput("le_save.txt", Context.MODE_PRIVATE);
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

            image = (ImageView) findViewById(R.id.people);
            Picasso.with(getApplicationContext()).load("http://theunitedstates.io/images/congress/225x275/" + detail_result.get(0) + ".jpg").into(image);
            info = (TextView) findViewById(R.id.party);
            image = (ImageView) findViewById(R.id.party_image);
            info.setText(detail_result.get(1));
            if (detail_result.get(1).equals("R")) {
                //Picasso.with(getApplicationContext()).load(R.drawable.republic).into(image);
                image.setImageDrawable(getResources().getDrawable(R.drawable.republic));
                info.setText("Republican");
            } else if (detail_result.get(1).equals("D")) {
                //Picasso.with(getApplicationContext()).load(R.drawable.domestic).into(image);
                image.setImageDrawable(getResources().getDrawable(R.drawable.domestic));
                info.setText("Democratic");
            }

            info = (TextView) findViewById(R.id.name_detail_table);
            info.setText(detail_result.get(2));
            info = (TextView) findViewById(R.id.email_detail_table);
            info.setText(detail_result.get(3));
            info = (TextView) findViewById(R.id.chamber_detail_table);
            info.setText(detail_result.get(4));
            info = (TextView) findViewById(R.id.contact_detail_table);
            info.setText(detail_result.get(5));
            info = (TextView) findViewById(R.id.start_detail_table);
            info.setText(detail_result.get(6));
            info = (TextView) findViewById(R.id.end_detail_table);
            info.setText(detail_result.get(7));
            info = (TextView) findViewById(R.id.office_detail_table);
            info.setText(detail_result.get(8));
            info = (TextView) findViewById(R.id.state_detail_table);
            info.setText(detail_result.get(9));
            info = (TextView) findViewById(R.id.fax_detail_table);
            info.setText(detail_result.get(10));
            info = (TextView) findViewById(R.id.birthday_detail_table);
            info.setText(detail_result.get(11));
            progressBar.setProgress(Integer.parseInt(detail_result.get(12)));
            progressBar.setText(Integer.parseInt(detail_result.get(12)));
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
