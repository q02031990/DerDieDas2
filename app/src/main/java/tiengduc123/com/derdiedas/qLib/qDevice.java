package tiengduc123.com.derdiedas.qLib;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by qadmin on 12.12.15.
 */
public class qDevice {
    Activity _Activity;

    public qDevice(Activity a){
        _Activity = a;
    }

    public void shareForFriend() {
        String message = "Deutsch Lernen Mit Video.<br /> https://play.google.com/store/apps/details?id=com.kabam.marvelbattle";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        _Activity.startActivity(Intent.createChooser(share, "Share to your Friend"));
    }



}
