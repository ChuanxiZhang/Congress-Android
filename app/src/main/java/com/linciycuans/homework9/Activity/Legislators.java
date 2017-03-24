package com.linciycuans.homework9.Activity;


/**
 * Created by linciy on 2016/11/17.
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Legislators extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyLetterView.OnTouchingLetterChangedListener {

    private GoogleApiClient client;
    private TabHost tabHost;
    private List<LegislatorInformation> legislatorInfo = new ArrayList<LegislatorInformation>();
    private List<LegislatorInformation> legislatorInfo1 = new ArrayList<LegislatorInformation>();
    private List<LegislatorInformation> legislatorInfo2 = new ArrayList<LegislatorInformation>();
    JsonArray obj;
    List<String> list;
    ListView listView;
    ListView listView1;
    ListView listView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        View stateTab = (View) LayoutInflater.from(this).inflate(R.layout.tab_style, null);
        TextView text0 = (TextView) stateTab.findViewById(R.id.tab_label);
        text0.setText("BY STATES");


        View houseTab = (View) LayoutInflater.from(this).inflate(R.layout.tab_style, null);
        TextView text1 = (TextView) houseTab.findViewById(R.id.tab_label);
        text1.setText("HOUSE");

        View senateTab = (View) LayoutInflater.from(this).inflate(R.layout.tab_style, null);
        TextView text2 = (TextView) senateTab.findViewById(R.id.tab_label);
        text2.setText("SENATE");

        tabHost = (TabHost) this.findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(stateTab).setContent(R.id.linearLayout));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(houseTab).setContent(R.id.linearLayout2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(senateTab).setContent(R.id.linearLayout3));
        MyLetterView myView = (MyLetterView) findViewById(R.id.myView);
        myView.setOnTouchingLetterChangedListener(this);


        new MyAsyncTask().execute();

    }

    public void onTouchingLetterChanged(String s) {
        // TODO Auto-generated method stub
        if (alphaIndexer(s) >=0) {
            int position = alphaIndexer(s);
            listView.setSelection(position);
        }
        if (alphaIndexer1(s) >= 0) {
            int position1 = alphaIndexer1(s);
            listView1.setSelection(position1);
        }
        if (alphaIndexer2(s) >=0) {
            int position2 = alphaIndexer2(s);
            listView2.setSelection(position2);
        }
    }

    public int alphaIndexer(String s) {
        int i = 0;
        for (; i < legislatorInfo.size(); i++) {
            if (legislatorInfo.get(i).getOrder().startsWith(s)) {
                break;
            }
        }
        return i;
    }

    public int alphaIndexer1(String s) {
        int i = 0;
        for (; i < legislatorInfo1.size(); i++) {
            if (legislatorInfo1.get(i).getName().startsWith(s)) {
                break;
            }
        }
        return i;
    }

    public int alphaIndexer2(String s) {
        int i = 0;
        for (; i < legislatorInfo2.size(); i++) {
            if (legislatorInfo2.get(i).getName().startsWith(s)) {
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
        protected String doInBackground(String... strings) {
            Gson gson = new Gson();
            String url = "http://congress.api.sunlightfoundation.com/legislators?apikey=46984d79855840a1bf5bb28498f6f766&per_page=all&order=state_name__asc";
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

            for (int i = 0; i < list.size(); i++) {
                JsonParser parser = new JsonParser();
                JsonObject result = parser.parse(list.get(i)).getAsJsonObject();
                LegislatorInformation info = new LegislatorInformation();
                info.setName(result.get("last_name").getAsString() + ", " + result.get("first_name").getAsString());
                info.setOrder(result.get("state_name").getAsString());
                try {
                    info.setDetails("(" + result.get("party").getAsString() + ")" + result.get("state_name").getAsString() + " - district:" + result.get("district").getAsString());
                } catch (UnsupportedOperationException e) {
                    info.setDetails("(" + result.get("party").getAsString() + ")" + result.get("state_name").getAsString() + " - district:0");
                } catch (NullPointerException e) {
                    info.setDetails("()" + result.get("state_name").getAsString() + " - district:0");
                }
                info.setImgId(result.get("bioguide_id").getAsString());
                legislatorInfo.add(info);

                if (result.get("chamber").getAsString().equals("house")) {
                    info.setName(result.get("last_name").getAsString() + ", " + result.get("first_name").getAsString());
                    try {
                        info.setDetails("(" + result.get("party").getAsString() + ")" + result.get("state_name").getAsString() + " - district:" + result.get("district").getAsString());
                    } catch (UnsupportedOperationException e) {
                        info.setDetails("(" + result.get("party").getAsString() + ")" + result.get("state_name").getAsString() + " - district:0");
                    } catch (NullPointerException e) {
                        info.setDetails("()" + result.get("state_name").getAsString() + " - district:0");
                    }
                    info.setImgId(result.get("bioguide_id").getAsString());
                    legislatorInfo1.add(info);
                }
                if (result.get("chamber").getAsString().equals("senate")) {
                    info.setName(result.get("last_name").getAsString() + ", " + result.get("first_name").getAsString());
                    try {
                        info.setDetails("(" + result.get("party").getAsString() + ")" + result.get("state_name").getAsString() + " - district:" + result.get("district").getAsString());
                    } catch (UnsupportedOperationException e) {
                        info.setDetails("(" + result.get("party").getAsString() + ")" + result.get("state_name").getAsString() + " - district:0");
                    } catch (NullPointerException e) {
                        info.setDetails("()" + result.get("state_name").getAsString() + " - district:0");
                    }
                    info.setImgId(result.get("bioguide_id").getAsString());
                    legislatorInfo2.add(info);
                }
            }
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
                    Intent intent = new Intent(Legislators.this, Le_details.class);
                    Bundle b = new Bundle();
                    b.putString("value", legislatorInfo1.get(position).getImgId());
                    intent.putExtras(b);
                    startActivity(intent);

                }
            });
            listView2 = (ListView) findViewById(R.id.third_list);
            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Legislators.this, Le_details.class);
                    Bundle b = new Bundle();
                    b.putString("value", legislatorInfo2.get(position).getImgId());
                    intent.putExtras(b);
                    startActivity(intent);

                }
            });
            listView.setAdapter(new ListViewAdapter(legislatorInfo));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView a = (TextView) view.findViewById(R.id.title);
                    Intent intent = new Intent(Legislators.this, Le_details.class);
                    Bundle b = new Bundle();
                    b.putString("value", legislatorInfo.get(position).getImgId());
                    intent.putExtras(b);
                    startActivity(intent);

                }
            });
            listView1.setAdapter(new ListViewAdapter(legislatorInfo1));
            listView2.setAdapter(new ListViewAdapter(legislatorInfo2));
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
            LayoutInflater inflater = (LayoutInflater) Legislators.this
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
        getMenuInflater().inflate(R.menu.title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.linciycuans.homework9/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.linciycuans.homework9/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}


