package tiengduc123.com.derdiedas;

import android.content.Context;
import android.content.Intent;

import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class showWord extends AppCompatActivity {

    Button btnBack;
    Button btnEdit;

    TextView txtDefinition;
    TextView txtWord;
    TextView txtID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_word);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        txtDefinition = (TextView) findViewById(R.id.txtDefinition);
        txtWord = (TextView) findViewById(R.id.txtWord);
        txtID = (TextView) findViewById(R.id.txtID);

        if(isOnline()) {
            new NapDuLieuLenLayout().execute(getID());
        }else{
            Toast.makeText(getApplicationContext(),"Need Connect To Internet",Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(),"Click auf der Wort zum Hören",Toast.LENGTH_SHORT).show();

        txtWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtWord.getText().toString();
                if (str.startsWith("der")) {
                    SpeakWord("der");
                    str = str.replace("der", "").trim();
                } else if (str.startsWith("die")) {
                    SpeakWord("die");
                    str = str.replace("die", "").trim();
                } else {
                    SpeakWord("das");
                    str = str.replace("das", "").trim();
                }
                SpeakWord(str);
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(showWord.this, addWord.class);
                it.putExtra("editID", txtID.getText().toString());
                startActivity(it);
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public String getID(){
        Bundle bu = getIntent().getExtras();
        if (bu != null) {
            String id = bu.getString("id");
            return id;
        }
        return "1";
    }

    public String getKeySearch(){
        Bundle bu = getIntent().getExtras();
        if (bu != null) {
            String id = bu.getString("keySearch");
            return id;
        }
        return "1";
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(showWord.this, MainActivity.class);
        it.putExtra("backPressed", "backPressed");
        it.putExtra("keySearch", getKeySearch());
        startActivity(it);
    }

    public void SpeakWord(String str){
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource("http://tiengduc123.com/wp-content/plugins/dict-search/1.php?lang=de&word=" + str);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mp.stop();
    }

    class NapDuLieuLenLayout extends AsyncTask<String, Integer, String> {

        woeter a;
        @Override
        protected String doInBackground(String... params) {

            //request toi database de lay du lieu
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            a = db.searchWordByID(params[0]);
            return "" ;// doc noi dung url tra ve string
        }


        protected void onPostExecute(String s) {
            try {
                txtWord.setText(a.artikel + " " + a.woeter);
                txtDefinition.setText(a.definition);
                txtID.setText(a.ID);
            }catch (Exception ex){

            }

        }
    }
}
