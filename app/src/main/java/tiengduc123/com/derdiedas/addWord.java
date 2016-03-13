package tiengduc123.com.derdiedas;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Arrays;


public class addWord extends AppCompatActivity {

    Button btn;
    TextView txtArtikel;
    TextView txtWoeter;
    EditText txtDefinition;
    TextView txtID;
    DatabaseHelper db;
    Button btnBack;

    private static char[] SOURCE_CHARACTERS = { 'Ä', 'ä', 'Ö', 'ö', 'Ü', 'ü',
            'ß' };

    // Mang cac ky tu thay the khong dau
    private static char[] DESTINATION_CHARACTERS = { 'a', 'a', 'o', 'o', 'u',
            'u', 's' };

    /**
     * Bo dau 1 ky tu
     *
     * @param ch
     * @return
     */
    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    /**
     * Bo dau 1 chuoi
     *
     * @param s
     * @return
     */
    public static String removeAccent(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public String getID(){
        Bundle bu = getIntent().getExtras();
        if (bu != null) {
            String id = bu.getString("editID");
            return id;
        }
        return "-1";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        txtArtikel = (TextView) findViewById(R.id.txtArtikel);
        txtWoeter = (TextView) findViewById(R.id.txtWoeter);
        txtDefinition = (EditText) findViewById(R.id.txtDefinition);
        txtID = (TextView) findViewById(R.id.txtID);

        btn = (Button) findViewById(R.id.btnAddWord);

        db = new DatabaseHelper(this);

        final String id = getID();
        new NapDuLieuLenLayout().execute(getID());


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (id == "-1") {

                    try {

                        String artikel = txtArtikel.getText().toString();
                        String Woeter = toTitleCase(txtWoeter.getText().toString());
                        String definition = txtDefinition.getText().toString();

                        if (artikel.equalsIgnoreCase("der")) {
                            artikel = "1";
                        } else if (artikel.equalsIgnoreCase("die")) {
                            artikel = "2";
                        } else {
                            artikel = "4";
                        }


                        String Woeter_suchen = removeAccent(txtWoeter.getText().toString());
                        if (db.insertData(artikel, Woeter, Woeter_suchen, definition)) {
                            Toast.makeText(getApplicationContext(), "added your Word", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(addWord.this, MainActivity.class);
                            startActivity(it);
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Could not add this Word", Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    try {

                        String artikel = txtArtikel.getText().toString();
                        String Woeter = toTitleCase(txtWoeter.getText().toString());
                        String definition = txtDefinition.getText().toString();

                        if (artikel.equalsIgnoreCase("der")) {
                            artikel = "1";
                        } else if (artikel.equalsIgnoreCase("die")) {
                            artikel = "2";
                        } else {
                            artikel = "4";
                        }


                        String Woeter_suchen = removeAccent(txtWoeter.getText().toString());
                        if (db.updateData(id, artikel, Woeter, Woeter_suchen, definition)) {
                            Toast.makeText(getApplicationContext(), "saved your Word", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(addWord.this, showWord.class);
                            it.putExtra("id", id);
                            it.putExtra("keySearch", "");
                            startActivity(it);
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Could not save this Word", Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(addWord.this, MainActivity.class);
        startActivity(it);
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
                txtArtikel.setText(a.artikel);
                txtWoeter.setText(a.woeter);
                txtDefinition.setText(a.definition);
                txtID.setText(a.ID);

                btn.setText("Save to Database");

                txtDefinition.requestFocus();
                txtDefinition.setSelection(txtDefinition.getText().length());

            }catch (Exception ex){

            }

        }
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
