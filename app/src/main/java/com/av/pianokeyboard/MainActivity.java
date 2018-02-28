package com.av.pianokeyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.HashMap;
 

public class MainActivity extends AppCompatActivity implements MidiDriver.OnMidiStartListener,
        View.OnTouchListener  {
    private byte[] event;
    private int[] config;
    private Button c3,db3,d3,eb3,e3,f3,gb3,g3,ab3,a3,bb3,b3,c4;
    private MidiDriver midiDriver;
    HashMap<String,Integer> tunesMap=new HashMap<>();
    private int base_Octave=12;
    private Button up_octave,down_octave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c3 = findViewById(R.id.c3);
        d3 = findViewById(R.id.d3);
        e3 = findViewById(R.id.e3);
        f3 = findViewById(R.id.f3);
        g3 = findViewById(R.id.g3);
        a3 = findViewById(R.id.a3);
        b3 = findViewById(R.id.b3);
        c4 = findViewById(R.id.c4);
        db3 = findViewById(R.id.db3);
        eb3 = findViewById(R.id.eb3);
        gb3 = findViewById(R.id.gb3);
        ab3 = findViewById(R.id.ab3);
        bb3 = findViewById(R.id.bb3);
        c3.setOnTouchListener(this);
        d3.setOnTouchListener(this);
        e3.setOnTouchListener(this);
        f3.setOnTouchListener(this);
        g3.setOnTouchListener(this);
        a3.setOnTouchListener(this);
        b3.setOnTouchListener(this);
        c4.setOnTouchListener(this);
        db3.setOnTouchListener(this);
        eb3.setOnTouchListener(this);
        gb3.setOnTouchListener(this);
        ab3.setOnTouchListener(this);
        bb3.setOnTouchListener(this);


        // Instantiate the driver.
        midiDriver = new MidiDriver();

        // Set the listener.
        midiDriver.setOnMidiStartListener(this);
        tunesMap=new HashMap<>();
        tunesMap.put("c3", 60);//(byte)0x3C);
        tunesMap.put("db3",61);

        tunesMap.put("d3",62);
        tunesMap.put("eb3",63);

        tunesMap.put("e3",64);
        tunesMap.put("f3",65);
        tunesMap.put("gb3",66);

        tunesMap.put("g3",67);
        tunesMap.put("ab3",68);

        tunesMap.put("a3",69);
        tunesMap.put("bb3",70);


        tunesMap.put("b3",71);
        tunesMap.put("c4",72);

        up_octave=findViewById(R.id.up_octave_btn);
        up_octave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(HashMap.Entry entry: tunesMap.entrySet()){
                    int n= (int) entry.getValue();
                    String k= (String) entry.getKey();
                    tunesMap.put(k,n+base_Octave);   //Tech dept
                }
            }
        });
        down_octave=findViewById(R.id.down_octave_btn);
        down_octave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(HashMap.Entry entry: tunesMap.entrySet()){
                    int n= (int) entry.getValue();
                    String k= (String) entry.getKey();
                    tunesMap.put(k,n-base_Octave);   //Tech dept
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();

        // Get the configuration.
        config = midiDriver.config();

        // Print out the details.
        Log.d(this.getClass().getName(), "maxVoices: " + config[0]);
        Log.d(this.getClass().getName(), "numChannels: " + config[1]);
        Log.d(this.getClass().getName(), "sampleRate: " + config[2]);
        Log.d(this.getClass().getName(), "mixBufferSize: " + config[3]);
    }
    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }

    @Override
    public void onMidiStart() {
        Log.d(this.getClass().getName(), "onMidiStart()");
    }

    private void playNote(int tune) {
        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        String tun=Integer.toHexString(tune);
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x02);  // 0x90 = note On, 0x00 = channel 1
        event[1] = (byte) Integer.parseInt(tun,16);  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
       // midiDriver.write(event);

    }

    private void stopNote(int tune) {
        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        String tun= Integer.toHexString(tune);
        event = new byte[3];
        event[0] = (byte) (0x80 | 0x02);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = (byte) Integer.parseInt(tun,16);  // 0x3C = middle C
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(this.getClass().getName(), "Motion event: " + event);
        String id=v.getTag()+"";
        if(tunesMap.containsKey(id)){
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(this.getClass().getName(), "MotionEvent.ACTION_DOWN");
                playNote(tunesMap.get(String.valueOf(id)));
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(this.getClass().getName(), "MotionEvent.ACTION_UP");
                stopNote(tunesMap.get(String.valueOf(id)));
            }
        }

        return false;    }

    public static int toHex(int n) {
        return Integer.valueOf(String.valueOf(n), 16);
    }

}
