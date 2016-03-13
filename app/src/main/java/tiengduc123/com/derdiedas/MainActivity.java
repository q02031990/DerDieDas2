package tiengduc123.com.derdiedas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText tv;


    ListView lv;
    DatabaseHelper db;
    String keySearch;


    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    final static private String PREF_KEY_SHORTCUT_ADDED = "PREF_KEY_SHORTCUT_ADDED";

    // File url to download
    private static String file_url = "http://tiengduc123.com/app/Data/Derdiedas.db";
    private static String DB_PATH = "/data/data/tiengduc123.com.derdiedas/databases/Derdiedas.db";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add tab

        //end ađd tab

        tv = (EditText) findViewById(R.id.textView);
        lv = (ListView) findViewById(R.id.listView);

        createShortcutIcon();// tao shortcut

        if(!isOnline()){
            Toast.makeText(getApplicationContext(),"Need connect to the Internet", Toast.LENGTH_LONG).show();
            return;
        }

        db = new DatabaseHelper(this);
        if(!db.checkDBExit()){
            new DownloadFileFromURL().execute(file_url);
        }

        keySearch = "A";
        getBackPressed();
        new napDulieuLenListView().execute(keySearch);

            tv.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String _string = tv.getText().toString();

                    if (_string != "") {
                        new napDulieuLenListView().execute(_string);
                    }
                }
            });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        tv.setSelection(tv.getText().length());
    }

    public boolean getBackPressed(){
        Bundle bu = getIntent().getExtras();
        if (bu != null) {
            String backPressed = bu.getString("backPressed");
            keySearch = bu.getString("keySearch");
            tv.setText(keySearch);
            if(backPressed == "backPressed"){
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                return true;
            }
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return false;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    public void shareForFriend() {
        String message = "Der Die Das Suchen.<br /> http://tiengduc123.com/download/3508/";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(share, "Share to your Friend"));
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            case R.id.MenuUpdateDatabase:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                //Toast.makeText(getApplicationContext(),"Yes Clicked", Toast.LENGTH_LONG).show();
                                new DownloadFileFromURL().execute(file_url);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                //Toast.makeText(getApplicationContext(),"No", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Database will update, all your word will be deleted?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;


            case R.id.MenuShare:
                shareForFriend();
                /*Toast.makeText(getApplicationContext(),"This is MenuShare", Toast.LENGTH_LONG).show();*/
                break;

            case R.id.MenuUpdate:
               Toast.makeText(getApplicationContext(),"The version is newest", Toast.LENGTH_LONG).show();
                break;

            case R.id.MenuAddWord:
                // chuyen man hinh
                Intent it = new Intent(MainActivity.this, addWord.class);
                startActivity(it);
                break;

            case R.id.MenuAbout:
                AlertDialog.Builder builder_MenuAbout = new AlertDialog.Builder(this);
                builder_MenuAbout.setTitle("About Us");
                builder_MenuAbout.setMessage(Html.fromHtml("<p>See more at, \n <a href=\"http://Tiengduc123.com\">Http://www.Tiengduc123.com</a>.</p>"));
                AlertDialog alert = builder_MenuAbout.create();
                alert.show();
        }
        return false;
    }

    private void createShortcutIcon() {

        // Checking if ShortCut was already added
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean shortCutWasAlreadyAdded = sharedPreferences.getBoolean(PREF_KEY_SHORTCUT_ADDED, false);
        if (shortCutWasAlreadyAdded) return;

        Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Deutsch Lernen");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);

        // Remembering that ShortCut was already added
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_KEY_SHORTCUT_ADDED, true);
        editor.commit();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void ChuyenManHinh(String id) {
        if (!isOnline()) {
            Toast.makeText(getApplicationContext(), "You need to connect to the Internet", Toast.LENGTH_LONG).show();
            return;
        }
        Intent it = new Intent(MainActivity.this, showWord.class);
        keySearch = tv.getText().toString();
        it.putExtra("id", id);
        it.putExtra("keySearch", keySearch);
        startActivity(it);
    }

    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                OutputStream output = new FileOutputStream(DB_PATH);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

        }

    }


    class napDulieuLenListView extends AsyncTask<String, Integer, String> {

        ArrayList<woeter> _Cursor;
        @Override
        protected String doInBackground(String... params) {
            //request toi database de lay du lieu
            _Cursor = db.searchWord(params[0]);
            return "" ;// doc noi dung url tra ve string
        }


        protected void onPostExecute(String s) {
            try {
                ListAdapter adapter;

                adapter = new tiengduc123.com.derdiedas.ListAdapter( getApplicationContext(),
                        R.layout.activity_main,
                        _Cursor);
                lv.setAdapter(adapter);

                //load lai list
                lv.invalidateViews();
                lv.refreshDrawableState();

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView _id = (TextView) view.findViewById(R.id.txtID);
                        String GetStringID = _id.getText().toString();
                        ChuyenManHinh(GetStringID);
                    }
                });
            }catch (Exception ex){

            }

        }
    }

}
