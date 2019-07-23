package pt.it.porto.mydiabetes.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.sync.ServerSync;
import pt.it.porto.mydiabetes.ui.activities.MyDiabetesWebViewActivity;
import pt.it.porto.mydiabetes.ui.activities.SettingsImportExport;

public class RankWebSyncDialog extends DialogFragment {

    ActivateFeatureDialogListener listener;
    Context context;
    Dialog currentShowingDialog;
    String username;
    String password;
    final private int WEBVIEW = 332;
    View v;
    EditText userEdit;
    EditText passEdit;
    Button createAccountB;
    Button login_b;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.context = getActivity();
        currentShowingDialog = getAccountPopUp();
        return currentShowingDialog;
    }

    public Dialog getAccountPopUp(){
        if(context == null){return null;}

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        username = Preferences.getUsername(context);
        password = Preferences.getPassword(context);

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.dialog_account_input, null);
        login_b = v.findViewById(R.id.login_account);
        userEdit = v.findViewById(R.id.username);
        passEdit = v.findViewById(R.id.password);
        userEdit.setText(username);
        passEdit.setText(password);
        createAccountB = v.findViewById(R.id.createNewAccountButton);


        login_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userEdit != null){username = userEdit.getText().toString();}
                if(passEdit != null){password = passEdit.getText().toString();}


                AsyncTask<Void, Void, Void> saveTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        pt.it.porto.mydiabetes.database.Preferences.saveCloudSyncCredentials(context, username, password);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        uploadData();
                    }
                };
                saveTask.execute();
            }
        });




        createAccountB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentShowingDialog.dismiss();
                Intent intent = new Intent(context, MyDiabetesWebViewActivity.class); //MyDiabetesWebViewActivityRank.class);
                ((Activity) context).startActivityForResult(intent, WEBVIEW);
            }
        });
        builder.setView(v);
        return builder.create();
    }

    public void uploadData(){
        currentShowingDialog.dismiss();
        currentShowingDialog = testCredentialsDialog(RankWebSyncDialog.this.context);
        currentShowingDialog.show();
    }

    public void ShowDialogMsg(String msg) {
        new android.app.AlertDialog.Builder(context).setTitle(R.string.information).setMessage(msg).show();
    }

    public Dialog testCredentialsDialog(final Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        try {
            ServerSync.getInstance(context).testCredentials(new ServerSync.ServerSyncListener() {
                @Override
                public void onSyncSuccessful() {
                    ((Activity) RankWebSyncDialog.this.context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentShowingDialog.dismiss();
                            if(context instanceof SettingsImportExport){
                                ((SettingsImportExport)context).showUpload();
                            }
                            ShowDialogMsg(context.getString(R.string.upload_successful));

                        }
                    });
                }

                @Override
                public void onSyncUnSuccessful() {
                    ((Activity) RankWebSyncDialog.this.context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentShowingDialog.dismiss();
                            if(context instanceof SettingsImportExport){
                                ((SettingsImportExport)context).hideUpload();
                            }
                            showLoginError();
                        }
                    });
                }

                @Override
                public void noNetworkAvailable() {
                    ((Activity) RankWebSyncDialog.this.context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentShowingDialog.dismiss();
                            if(context instanceof SettingsImportExport){
                                ((SettingsImportExport)context).hideUpload();
                            }
                            showNoNetwork();
                        }
                    });
                }
            });
        } catch (Exception e) {
            showError();
        }
        progressDialog.setCancelable(false);
        currentShowingDialog = progressDialog;
        return progressDialog;
    }

    private void showLoginError() {
        Dialog userDataPopUp = getAccountPopUp();
        userDataPopUp.show();
        TextView errorText = (userDataPopUp.findViewById(R.id.error));
        errorText.setVisibility(View.VISIBLE);
        currentShowingDialog = userDataPopUp;
    }

    public void showError(Context c) {
        this.context = c;
        Dialog userDataPopUp = getAccountPopUp();
        userDataPopUp.show();
        TextView errorText = ((TextView) userDataPopUp.findViewById(R.id.error));
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(R.string.error_could_not_send_data);
        currentShowingDialog = userDataPopUp;
        currentShowingDialog.setCancelable(true);
    }

    public void showError() {
        Dialog userDataPopUp = getAccountPopUp();
        userDataPopUp.show();
        TextView errorText = ((TextView) userDataPopUp.findViewById(R.id.error));
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(R.string.error_could_not_send_data);
        currentShowingDialog = userDataPopUp;
        currentShowingDialog.setCancelable(true);
    }

    private void showNoNetwork() {
        Dialog userDataPopUp = getAccountPopUp();
        userDataPopUp.show();
        TextView errorText = ((TextView) userDataPopUp.findViewById(R.id.error));
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(R.string.error_login_no_internet);
        currentShowingDialog = userDataPopUp;
        currentShowingDialog.setCancelable(true);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setListener(ActivateFeatureDialogListener listener) {
        this.listener = listener;
    }

    public interface ActivateFeatureDialogListener {
        void useFeature();

        void notUseFeature();
    }

}
