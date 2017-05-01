package Utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import Halper.MyApplication;
import creadigol.com.tipper.R;
import creadigol.com.tipper.TipperActivity;

public class CommonUtils {

    public static boolean isNetworkAvailable() {
        boolean isNetworkAvailable = false;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) TipperActivity.getInstance()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            isNetworkAvailable = true;
        }

        return isNetworkAvailable;
    }

    public static void showToast(String message) {
//        Toast.makeText(TipperActivity.getInstance(), message, Toast.LENGTH_LONG).show();
        Toast toast = Toast.makeText(TipperActivity.getInstance(), message, Toast.LENGTH_LONG);
        View vieew = toast.getView();
        //  vieew.setBackgroundColor(Color.parseColor("#BD8BDC"));
        vieew.setBackgroundResource(R.drawable.textinputborder);
        toast.setView(vieew);
        toast.show(); //This displays the toast for the specified lenght.
    }
    public static void Toast(String message) {
//        Toast.makeText(TipperActivity.getInstance(), message, Toast.LENGTH_LONG).show();
        Toast toast = Toast.makeText(TipperActivity.getInstance(), message, Toast.LENGTH_SHORT);
        View vieew = toast.getView();
        //  vieew.setBackgroundColor(Color.parseColor("#BD8BDC"));
        vieew.setBackgroundResource(R.drawable.textinputborder);
        toast.setView(vieew);
        toast.show(); //This displays the toast for the specified lenght.
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    public static void showAlertWithNegativeButton(final Context context, String title, String message, DialogInterface.OnClickListener positiveButtonListener) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, android.support.v7.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        // Setting Dialog Title
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Ok", positiveButtonListener);

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //to colse activity
                ((Activity) context).finish();

            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    public static String getCapitalize(String input) {
        if (input != null && input.trim().length() > 0)
            return Character.toString(input.charAt(0)).toUpperCase() + input.substring(1);
        else
            return "";
    }

    public static String getFormatedDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        //String date = formatter.format(calendar.getTime());
        return formatter.format(calendar.getTime());

    }

}
