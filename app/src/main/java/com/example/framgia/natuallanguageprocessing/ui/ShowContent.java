package com.example.framgia.natuallanguageprocessing.ui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.framgia.natuallanguageprocessing.R;
import com.example.framgia.natuallanguageprocessing.service.MyService;
import com.example.framgia.natuallanguageprocessing.util.MethodUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowContent extends AppCompatActivity implements View.OnClickListener {
    private GestureDetector gestureDetector;
    private TextView mTxtContent, mTitleContent;
    private int mChap = 1;
    private ImageButton mImgNext, mImgPre;
    private List<String> mListFile = new ArrayList<>();
    private ImageButton mMovableButton;
    private CircleImageView mVoiceSearch, mSearch;
    private Dialog mDialog;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    List<Integer> mListFilter=new ArrayList<>();
    EditText edtNameChap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        initView();
        geAllFileInRaw();
        mTxtContent.setText(getContent(mChap));

    }

    private void initView() {
        mTxtContent = (TextView) findViewById(R.id.content);
        mTxtContent.setMovementMethod(new ScrollingMovementMethod());
        mTitleContent = (TextView) findViewById(R.id.title_content);
        mMovableButton = (ImageButton) findViewById(R.id.movable_button);
        mMovableButton.setImageResource(R.drawable.ic_home);
        mMovableButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    showDialog();
                    return true;
                } else {
                    FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) view.getLayoutParams();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            params.topMargin = (int) event.getRawY() - view.getHeight() * 2;
                            params.leftMargin = (int) event.getRawX() - view.getHeight() / 2;
                            view.setLayoutParams(params);
                    }
                }
                return false;
            }
        });
        mImgPre = (ImageButton) findViewById(R.id.pre_page);
        mImgNext = (ImageButton) findViewById(R.id.next_page);
        mImgPre.setOnClickListener(this);
        mImgNext.setOnClickListener(this);
        //   startService(new Intent(getApplicationContext(), MyService.class));
    }

    private void showDialog() {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.change_story);
        mVoiceSearch = (CircleImageView) mDialog.findViewById(R.id.voice_search);
        mVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
        edtNameChap=(EditText)mDialog.findViewById(R.id.chap_edit);
        mSearch=(CircleImageView)mDialog.findViewById(R.id.button_search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input= edtNameChap.getText().toString();
                if(input.length()>0) {
                    searchChaps(input);
                    if(mListFilter.size()>0){
                        for(int i=0;i<mListFilter.size();i++){
                        }
                    }
                }
            }
        });
        mDialog.show();
        mMovableButton.setVisibility(View.INVISIBLE);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mMovableButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getContent(int i) {
        String content = "";
        mTitleContent.setText(getString(R.string.title) + " " + i);
        String name = mListFile.get(i);
        InputStream in = getResources().openRawResource(getResources().getIdentifier(name, "raw",
            getPackageName()));
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(in, "utf-16"));
            String newLine;
            while ((newLine = buffer.readLine()) != null) {
                content += newLine + "\n";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private void geAllFileInRaw() {
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            mListFile.add(field.getName());
        }
    }

    private void searchChaps(String input){
        String sumText="";
        for(int i=1;i<22;i++){
            sumText=getContent(i);
            if(MethodUtil.checkString(input, sumText)){
                mListFilter.add(i);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pre_page:
                if (mChap <= 1)
                    Toast.makeText(ShowContent.this, "Không thể lùi được nữa", Toast.LENGTH_LONG)
                        .show();
                else {
                    mChap -= 1;
                    mTxtContent.setText(getContent(mChap));
                }
                break;
            case R.id.next_page:
                if (mChap == 21)
                    Toast.makeText(ShowContent.this, "Không còn chương nào nữa", Toast
                        .LENGTH_LONG)
                        .show();
                else {
                    mChap += 1;
                    mTxtContent.setText(getContent(mChap));
                }
                break;
        }
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                getString(R.string.speech_not_supported),
                Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edtNameChap.setText(result.get(0));
                }
                break;
            }
        }
    }
}
