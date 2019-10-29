package com.example.mlimplementaion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button clear,hint,send;
    TextToSpeech textToSpeech;
    EditText name,say;
    private TextView txtSpeechInput;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Button suggestion1,suggestion2,suggestion3;
     ArrayList<FirebaseTextMessage> conversation=new ArrayList<FirebaseTextMessage>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//// hide the action bar
//        getActionBar().hide();

        init();
        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversation.clear();

            }
        });
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHint();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage(send.getText().toString());
            }
        });
        suggestion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage(suggestion1.toString());
            }
        });
        suggestion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage(suggestion2.toString());

            }
        });
        suggestion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage(suggestion1.toString());

            }
        });

    }
    private void init(){
clear=findViewById(R.id.clear);
hint=findViewById(R.id.hint);
send=findViewById(R.id.send);
name=findViewById(R.id.name);
say=findViewById(R.id.text_to_send);
suggestion1=findViewById(R.id.output1);
        suggestion2=findViewById(R.id.output2);
        suggestion3=findViewById(R.id.output3);
    }
    private void addMessage(String text){
        conversation.add(FirebaseTextMessage.createForRemoteUser(
                text, System.currentTimeMillis(), name.getText().toString()));
    }
    private void getHint(){
        FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
        smartReply.suggestReplies(conversation)
                .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                    @Override
                    public void onSuccess(SmartReplySuggestionResult result) {
                        if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                            // The conversation's language isn't supported, so the
                            // the result doesn't contain any suggestions.
                            Toast.makeText(getApplicationContext(),"Language Not Supported",Toast.LENGTH_SHORT).show();
                        } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                            // Task completed successfully
                            // ...
                            suggestion1.setText(result.getSuggestions().get(0).getText());
                            Object o ="text" ;
                            suggestion2.setText(result.getSuggestions().get(1).getText());
                            suggestion3.setText(result.getSuggestions().get(2).getText());

                            //Speak TO respond
                            String tospeek=result.getSuggestions().get(0).getText();
                            textToSpeech.speak(tospeek,TextToSpeech.QUEUE_FLUSH,null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();

                        // ...
                    }
                });
    }
    /**
     * Showing google speech input dialog
     * */
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

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    say.setText(result.get(0));
                }
                break;
            }

        }
    }

    public void Listing(View view) {
        UserName();
        addMessage(send.getText().toString());
        getHint();
        promptSpeechInput();
    }
    private void UserName(){
        name.setText("Avinash");
    }
}
