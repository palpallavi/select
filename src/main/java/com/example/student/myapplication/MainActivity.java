
package com.example.student.myapplication;


import android.speech.tts.TextToSpeech;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.InputDevice;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.student.myapplication.R;
import android.os.Vibrator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.support.v4.content.ContextCompat.getSystemService;


class Vibration implements Runnable
{
    String morseCode;
    Context mContext;
    private static final String TAG = "Vibration";

    public void run()
    {
        vibrateMorse(morseCode);
    }

    public Vibration(String morseCode, Context mContext)
    {
        this.morseCode = morseCode;
        Log.d(TAG,"Message" + morseCode);
        this.mContext=mContext;
    }

    protected void vibrateFor(long time)
    {
        Vibrator v = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        v.vibrate(time);
    }




    protected void vibrateMorse(String morseCode)
    {
        long dot, dash, word_gap, letter_gap, symb_gap;
        dot = 200;
        dash = 600;
        symb_gap = 600;
        letter_gap = 800;
        word_gap = 1000;
        int j = 1;
        for (int i = 0; i < morseCode.length(); i++)
        {
            if (morseCode.charAt(i) == ' ')
            {
                //giving word gap
                try
                {
                    Thread.sleep(word_gap);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            if (morseCode.charAt(i) == '/')
            {
                //giving word gap
                try
                {
                    Thread.sleep(letter_gap);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }


            else if(morseCode.charAt(i) == '.')
            {
                vibrateFor(dot);
                try
                {
                    Thread.sleep(symb_gap);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            else if(morseCode.charAt(i) == '-')
            {
                vibrateFor(dash);
                try
                {
                    long dash_gap = symb_gap + 300;
                    Thread.sleep(dash_gap);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

}


public class MainActivity extends Activity implements TextToSpeech.OnInitListener, View.OnClickListener
{
    private final int REQUEST_SPEECH_RECOGNIZER = 3000;
    private TextView mTextView;
    private final String mQuestion = "Please speak";
    private String mAnswer = "";
    private TextView textView;
    private TextToSpeech tts;
    private EditText txtText;
    public HashMap<Character, String> morse = new HashMap<Character, String>();




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView =findViewById(R.id.mTextView);
        textView = findViewById(R.id.textView);
        tts = new TextToSpeech(this, this);
        txtText = (EditText) findViewById(R.id.editText);
        morse.put('a', ".-/");      // '/' represents letter gap
        morse.put('b', "-.../");
        morse.put('c',  "-.-/");
        morse.put('d',  "-../");
        morse.put('e',    "./");
        morse.put('f', "..-./");
        morse.put('g',  "--./");
        morse.put('h', "..../");
        morse.put('i',   "../");
        morse.put('j', ".---/");
        morse.put('k',   "-./");
        morse.put('l', ".-../");
        morse.put('m',   "--/");
        morse.put('n',   "-./");
        morse.put('o',  "---/");
        morse.put('p', ".--./");
        morse.put('q', "--.-/");
        morse.put('r', ".-./");
        morse.put('s',  ".../");
        morse.put('t',   "-/");
        morse.put('u',  "..-/");
        morse.put('v', "...-/");
        morse.put('w',  ".--/");
        morse.put('x', "-..-/");
        morse.put('y', "-.--/");
        morse.put('z', "--../");
        morse.put('1', ".----/");
        morse.put('2',"..---/");
        morse.put('3', "...--/");
        morse.put('4', "....-/");
        morse.put('5', "...../");
        morse.put('6', "-..../");
        morse.put('7', "--.../");
        morse.put('8', "---../");
        morse.put('9', "----./");
        morse.put('0', "-----/");
    }



    protected void vibrateText(String text)
    {
        int len = text.length();
        String morseCode = "";
        for (int i = 0; i < len; i++)
        {
            if (text.charAt(i) == ' ')
                morseCode = morseCode + " ";
            else
                morseCode = morseCode + getMorseCode(text.charAt(i));
        }
        textView.setText(morseCode);
        Vibration thread = new Vibration(morseCode, this);
        Thread t=new Thread(thread);
        t.start();
    }

    protected String getMorseCode(char s) {
        if (s == ' ')
            return " ";
        return (morse.get(s));
    }



    @Override
    public void onDestroy()
    {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public void onInit(int status)
    {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA  || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Log.e("TTS", "This Language is not supported");
            }

        } else
            {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut()
    {

        String text = txtText.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }



    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.clickButton:
            {
                startSpeechRecognizer();
                break;
            }
            case R.id.ttsButton:
            {
                speakOut();
                break;
            }

        }
    }


    void morseDelay(int millis)
    {
        long initial= System.currentTimeMillis();
        long current=millis;
        while(current-initial<=millis)
        {
            current=  System.currentTimeMillis();
        }
    }

    private void startSpeechRecognizer()
    {
        Intent intent = new Intent
                (RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mQuestion);
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH_RECOGNIZER)
        {
            if (resultCode == RESULT_OK)
            {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mAnswer = results.get(0).toLowerCase();
                mTextView.setText(mAnswer);
                vibrateText(mAnswer);

            }
        }
    }

}

