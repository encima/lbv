package ms.gwillia.lbv;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends Activity {


    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH = 12222;
    Map<String, String> pkgAppsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppsList = getInstalledApps(true);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        // hide the action bar and ask for input on startup
        getActionBar().hide();

        promptSpeechInput();


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Receiving speech input
     * */
//        @TODO: let user select from apps when multiple matches are found
     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String term = result.get(0).toLowerCase();
                    Log.i(getString(R.string.app_name), term);

                    boolean found = false;
                    Iterator<Map.Entry<String, String>> iterator = pkgAppsList.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String,String> pairs = (Map.Entry<String,String>)iterator.next();
                        String p =  pairs.getValue().toLowerCase();
                        String app = pairs.getKey().toLowerCase();
//                        search for app in name and package, as well as if a user spells it out letter by letter
                        if((app.contains(term) || p.contains(term) || app.contains(term.replaceAll(" ", ""))) && found == false) {
                            Log.i(getString(R.string.app_name), app + "--->" + p);
                            Intent intent;
                            PackageManager manager = getPackageManager();
                            try {
                                found = true;
                                intent = manager.getLaunchIntentForPackage(p);
                                if (intent == null)
                                    throw new PackageManager.NameNotFoundException();
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                startActivity(intent);
//                                launch first app to match and exit
                                break;
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.e(getString(R.string.app_name), e.toString());
                                txtSpeechInput.setText("Sorry, '" + result.get(0) + "' not found");
                            }
                        }
                    }
                    if(found == false) {
                        txtSpeechInput.setText("Sorry, '" + result.get(0) + "' not found");
                    }
                }
                break;
            }

        }
    }

    private Map<String, String> getInstalledApps(boolean getSysPackages) {
        Map<String, String> res = new HashMap<String, String>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }

            res.put(p.applicationInfo.loadLabel(getPackageManager()).toString(), p.packageName);
        }
        return res;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
