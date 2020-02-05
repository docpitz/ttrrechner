package de.ssp.ttr_rechner;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LicenseActivity extends AppCompatActivity {

    private static final String LICENSES_HTML_PATH = "file:///android_asset/licenses.html";
    protected @BindView(R.id.webView) WebView webView;
    protected @BindView(R.id.toolbar) Toolbar tbToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license_activity);
        ButterKnife.bind(this);
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(LICENSES_HTML_PATH);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}