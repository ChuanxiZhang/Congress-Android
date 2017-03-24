package com.linciycuans.homework9.Activity;


/**
 * Created by linciy on 2016/11/17.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linciycuans.homework9.Information.LegislatorInformation;
import com.linciycuans.homework9.Information.MyLetterView;
import com.linciycuans.homework9.Jason.JsonFunction;
import com.linciycuans.homework9.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Favorites extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,MyLetterView.OnTouchingLetterChangedListener {


    private GoogleApiClient client;
    private TabHost tabHost;
    private String[] strings;
    private String[] bistrings;
    private String[] costrings;
    private List<LegislatorInformation> legislatorInfo = new ArrayList<LegislatorInformation>();
    private List<LegislatorInformation> legislatorInfo1 = new ArrayList<LegislatorInformation>();
    private List<LegislatorInformation> legislatorInfo2 = new ArrayList<LegislatorInformation>();
    JsonArray obj;
    List<String> list;
    JsonArray obj1;
    List<String> list1;
    JsonArray obj2;
    List<String> list2;
    ListView listView;
    ListView listView1;
    ListView listView2;
    public static Favorites instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fa_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        View leTab = (View) LayoutInflater.from(this).inflate(R.layout.tab_style, null);
        TextView text0 = (TextView) leTab.findViewById(R.id.tab_label);
        text0.setText("LEGISTATORS");

        View biTab = (View) LayoutInflater.from(this).inflate(R.layout.tab_style, null);
        TextView text1 = (TextView) biTab.findViewById(R.id.tab_label);
        text1.setText("BILLS");

        View comTab = (View) LayoutInflater.from(this).inflate(R.layout.tab_style, null);
        TextView text2 = (TextView) comTab.findViewById(R.id.tab_label);
        text2.setText("COMMITTEES");

        tabHost = (TabHost) this.findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(leTab).setContent(R.id.linearLayout));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(biTab).setContent(R.id.linearLayout2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(comTab).setContent(R.id.linearLayout3));
        MyLetterView myView = (MyLetterView) findViewById(R.id.myView);
        myView.setOnTouchingLetterChangedListener(this);
        instance=this;
        new MyAsyncTask().execute();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.refreshFriend");
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }


    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.refreshFriend"))
            {
                legislatorInfo.clear();
                legislatorInfo1.clear();
                legislatorInfo2.clear();
                new MyAsyncTask().execute();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

    public void onTouchingLetterChanged(String s) {
        // TODO Auto-generated method stub
        if (alphaIndexer(s) >= 0) {
            int position = alphaIndexer(s);
            listView.setSelection(position);
        }
    }

    public int alphaIndexer(String s) {
        int i = 0;
        for (; i < legislatorInfo.size(); i++) {
            if (legislatorInfo.get(i).getName().startsWith(s)) {
                break;
            }
        }
        return i;
    }

    public class MyAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... stringsa) {
            try {
                FileInputStream fis = getApplication().openFileInput("le_save.txt");
                FileInputStream bif = getApplication().openFileInput("bi_save.txt");
                FileInputStream cof = getApplication().openFileInput("com_save.txt");

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ByteArrayOutputStream bibos = new ByteArrayOutputStream();
                ByteArrayOutputStream cobos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                byte[] buffer1 = new byte[1024];
                byte[] buffer2 = new byte[1024];

                int length;
                int length1;
                int length2;
                while ((length = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, length);
                }

                while ((length1 = bif.read(buffer1)) != -1) {
                    bibos.write(buffer1, 0, length1);
                }

                while ((length2 = cof.read(buffer2)) != -1) {
                    cobos.write(buffer2, 0, length2);
                }

                bif.close();
                cof.close();
                bibos.close();
                fis.close();
                bos.close();
                cobos.close();


                String result = new String(bos.toByteArray(), "GB2312");
                String biresult = new String(bibos.toByteArray(), "GB2312");
                String coresult = new String(cobos.toByteArray(), "GB2312");

                coresult = coresult.trim();
                biresult = biresult.trim();
                result = result.trim();


                strings = result.split(" ");
                bistrings = biresult.split(" ");
                costrings = coresult.split(" ");

            } catch (Exception e) {
            }

            Gson gson = new Gson();
            for (int i = 0; i < strings.length; i++) {

                if (strings[i] != "") {
                    String url = "http://congress.api.sunlightfoundation.com/legislators?apikey=46984d79855840a1bf5bb28498f6f766&bioguide_id=" + strings[i];
                    obj = JsonFunction.getJsonContent(url);
                    list = setList(obj);
                    outputJson(list);
                }
            }

            for (int j = 0; j < bistrings.length; j++) {

                if (bistrings[j] != "") {
                    String url = "http://congress.api.sunlightfoundation.com/bills?apikey=46984d79855840a1bf5bb28498f6f766&bill_id=" + bistrings[j];
                    obj1 = JsonFunction.getJsonContent(url);
                    list1 = setList(obj1);
                    outputJson1(list1);
                }
            }

            for (int k = 0; k < costrings.length; k++) {

                if (costrings[k] != "") {
                    String url = "http://congress.api.sunlightfoundation.com/committees?apikey=46984d79855840a1bf5bb28498f6f766&committee_id=" + costrings[k];
                    obj2 = JsonFunction.getJsonContent(url);
                    list2 = setList(obj2);
                    outputJson2(list2);
                }
            }
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
            LegislatorInformation info = new LegislatorInformation();
            info.setName(result.get("last_name").getAsString() + ", " + result.get("first_name").getAsString());

            try {
                info.setDetails("(" + result.get("party").getAsString() + ")" + result.get("state_name").getAsString() + " - district:" + result.get("district").getAsString());
            } catch (UnsupportedOperationException e) {
                info.setDetails("(" + result.get("party").getAsString() + ")" + result.get("state_name").getAsString() + " - district:0");
            } catch (NullPointerException e) {
                info.setDetails("()" + result.get("state_name").getAsString() + " - district:0");
            }
            info.setImgId(result.get("bioguide_id").getAsString());
            legislatorInfo.add(info);
            Collections.sort(legislatorInfo, new Comparator<LegislatorInformation>() {
                @Override
                public int compare(LegislatorInformation lhs, LegislatorInformation rhs) {
                    if (lhs.getName().compareTo(rhs.getName()) > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }

        private void outputJson1(List<String> list) {
            JsonParser parser = new JsonParser();
            JsonObject result = parser.parse(list.get(0)).getAsJsonObject();
            LegislatorInformation info = new LegislatorInformation();
            info.setName(result.get("bill_id").getAsString());
            try {
                info.setDetails(result.get("short_title").getAsString());
            } catch (UnsupportedOperationException e) {
                info.setDetails(result.get("official_title").getAsString());
            }
            info.setImgId(result.get("introduced_on").getAsString());
            legislatorInfo1.add(info);
            Collections.sort(legislatorInfo1, new Comparator<LegislatorInformation>() {
                @Override
                public int compare(LegislatorInformation lhs, LegislatorInformation rhs) {
                    if (lhs.getName().compareTo(rhs.getName()) > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }

        private void outputJson2(List<String> list) {
            JsonParser parser = new JsonParser();
            JsonObject result = parser.parse(list.get(0)).getAsJsonObject();
            LegislatorInformation info = new LegislatorInformation();
            info.setName(result.get("committee_id").getAsString());
            info.setDetails(result.get("name").getAsString());
            info.setImgId(result.get("chamber").getAsString());
            legislatorInfo2.add(info);
            Collections.sort(legislatorInfo2, new Comparator<LegislatorInformation>() {
                @Override
                public int compare(LegislatorInformation lhs, LegislatorInformation rhs) {
                    if (lhs.getName().compareTo(rhs.getName()) > 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            listView = (ListView) findViewById(R.id.first_list);
            listView1 = (ListView) findViewById(R.id.second_list);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView a = (TextView) view.findViewById(R.id.title);
                    Intent intent = new Intent(Favorites.this, Bi_details.class);
                    Bundle b = new Bundle();
                    b.putString("value", legislatorInfo1.get(position).getName());
                    intent.putExtras(b);
                    startActivity(intent);

                }
            });
            listView2 = (ListView) findViewById(R.id.third_list);
            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView a = (TextView) view.findViewById(R.id.title);
                    Intent intent = new Intent(Favorites.this, com_detail.class);
                    Bundle b = new Bundle();
                    b.putString("value", legislatorInfo2.get(position).getName());
                    intent.putExtras(b);
                    startActivity(intent);

                }
            });
            listView.setAdapter(new ListViewAdapter(legislatorInfo));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView a = (TextView) view.findViewById(R.id.title);
                    Intent intent = new Intent(Favorites.this, Le_details.class);
                    Bundle b = new Bundle();
                    b.putString("value", legislatorInfo.get(position).getImgId());
                    intent.putExtras(b);
                    startActivity(intent);

                }
            });
            listView1.setAdapter(new BiListViewAdapter(legislatorInfo1));
            listView2.setAdapter(new ComListViewAdapter(legislatorInfo2));
        }

    }


    public class ListViewAdapter extends BaseAdapter {
        View[] itemViews;

        public ListViewAdapter(List<LegislatorInformation> legislatorInfo) {
            itemViews = new View[legislatorInfo.size()];
            for (int i = 0; i < legislatorInfo.size(); i++) {
                LegislatorInformation getInfo = (LegislatorInformation) legislatorInfo.get(i);
                itemViews[i] = makeItemView(
                        getInfo.getName(), getInfo.getDetails(), getInfo.getImgId()
                );
            }
        }

        protected void notifyChange() {
            notifyDataSetChanged();
        }

        public int getCount() {
            return itemViews.length;
        }

        public View getItem(int position) {
            return itemViews[position];
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                int a = position;
                return itemViews[position];
            }
            return itemViews[position];
        }

        private View makeItemView(String strTitle, String strText, String resId) {
            LayoutInflater inflater = (LayoutInflater) Favorites.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.item, null);
            TextView title = (TextView) itemView.findViewById(R.id.title);
            title.setText(strTitle);
            TextView text = (TextView) itemView.findViewById(R.id.info);
            text.setText(strText);
            ImageView image = (ImageView) itemView.findViewById(R.id.img);
            Picasso.with(getApplicationContext()).load("http://theunitedstates.io/images/congress/225x275/" + resId + ".jpg").into(image);
            return itemView;
        }
    }


    public class BiListViewAdapter extends BaseAdapter {
        View[] itemViews;

        public BiListViewAdapter(List<LegislatorInformation> legislatorInfo) {
            itemViews = new View[legislatorInfo.size()];
            for (int i = 0; i < legislatorInfo.size(); i++) {
                LegislatorInformation getInfo = (LegislatorInformation) legislatorInfo.get(i);
                itemViews[i] = makeItemView(
                        getInfo.getName(), getInfo.getDetails(), getInfo.getImgId()
                );
            }
        }


        public int getCount() {
            return itemViews.length;
        }

        public View getItem(int position) {
            return itemViews[position];
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                int a = position;
                return itemViews[position];
            }
            return itemViews[position];
        }

        private View makeItemView(String strTitle, String strText, String resId) {
            LayoutInflater inflater = (LayoutInflater) Favorites.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.bill_items, null);
            TextView title = (TextView) itemView.findViewById(R.id.bill_name);
            title.setText(strTitle);
            TextView text = (TextView) itemView.findViewById(R.id.bill_title);
            text.setText(strText);
            TextView introduced = (TextView) itemView.findViewById(R.id.bill_introduced);
            introduced.setText(resId);
            return itemView;
        }
    }


    public class ComListViewAdapter extends BaseAdapter {
        View[] itemViews;

        public ComListViewAdapter(List<LegislatorInformation> legislatorInfo) {
            itemViews = new View[legislatorInfo.size()];
            for (int i = 0; i < legislatorInfo.size(); i++) {
                LegislatorInformation getInfo = (LegislatorInformation) legislatorInfo.get(i);
                itemViews[i] = makeItemView(
                        getInfo.getName(), getInfo.getDetails(), getInfo.getImgId()
                );
            }
        }


        public int getCount() {
            return itemViews.length;
        }

        public View getItem(int position) {
            return itemViews[position];
        }

        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                int a = position;
                return itemViews[position];
            }
            return itemViews[position];
        }

        private View makeItemView(String strTitle, String strText, String resId) {
            LayoutInflater inflater = (LayoutInflater) Favorites.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.bill_items, null);
            TextView title = (TextView) itemView.findViewById(R.id.bill_name);
            title.setText(strTitle);
            TextView text = (TextView) itemView.findViewById(R.id.bill_title);
            text.setText(strText);
            TextView chamber = (TextView) itemView.findViewById(R.id.bill_introduced);
            chamber.setText(resId);
            return itemView;
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.legislators) {
            Intent intent = new Intent(this, Legislators.class);
            this.startActivity(intent);
            finish();
        } else if (id == R.id.bills) {
            Intent intent = new Intent(this, Bills.class);
            this.startActivity(intent);
            finish();
        } else if (id == R.id.committees) {
            Intent intent = new Intent(this, Committees.class);
            this.startActivity(intent);
            finish();
        } else if (id == R.id.favorites) {
            Intent intent = new Intent(this, Favorites.class);
            this.startActivity(intent);
            finish();
        } else if (id == R.id.about_me) {
            Intent intent = new Intent(this, About.class);
            this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.linciycuans.homework9/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.linciycuans.homework9/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


}


