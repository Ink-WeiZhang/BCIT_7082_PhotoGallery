package ca.wei.comp7082.photogallery2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchActivity extends Activity {

    private static final String EXTRA_START = "StartLimit";
    private static final String EXTRA_END = "EndLimit";
    private static final String EXTRA_KEYWORD = "Keyword";
    private static final int RESULT_SUCCESS = 6000;

    private Date startLimit = new Date();
    private Date endLimit = new Date();
    private String keywords = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        startLimit = (Date)intent.getSerializableExtra(EXTRA_START);
        endLimit = (Date)intent.getSerializableExtra(EXTRA_END);
        keywords = intent.getStringExtra(EXTRA_KEYWORD);

        if (!keywords.equals("")){
            TextView keywordView = findViewById(R.id.txtKeyWord);
            keywordView.setText(keywords);
        }
    }

    public void btnFilterClicked(View view ){
        EditText st = findViewById(R.id.date1);
        EditText et = findViewById(R.id.date2);
        TextView kt = findViewById(R.id.txtKeyWord);
        String date1 = st.getText().toString();
        String date2 = et.getText().toString();
        String key1 = kt.getText().toString();
        DateFormat fmt = new SimpleDateFormat("dd/mm/yy");

        try{
            startLimit = fmt.parse(date1);
            endLimit = fmt.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent output = new Intent();
        output.putExtra(EXTRA_START, startLimit);
        output.putExtra(EXTRA_END, endLimit);
        output.putExtra(EXTRA_KEYWORD, keywords);
        setResult(RESULT_SUCCESS, output);
        finish();
    }


}
