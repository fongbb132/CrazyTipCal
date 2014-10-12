package com.newthinktank.crazytipcal.app;

import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class CrazyTipCal extends ActionBarActivity {

    private static final String TOTAL_BILL = "TOTAL_BILL";
    private static final String CURRENT_TIP = "CURRENT_TIP";
    private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";

    private double billBeforeTip;
    private double tipAmount;
    private double finalBill;

    EditText billBeforeTipET;
    EditText tipAmountET;
    EditText finalBillET;

    private int[] checklistValues = new int[12];

    CheckBox friendlyCheckbox, specialCheckbox, opinionCheckbox;
    RadioGroup availabilityRadioGroup;
    RadioButton availableBadRadio, availableOkRadio, availableGoodRadio;

    Spinner problemsSpinner;
    Button start,pause,reset;

    Chronometer timeWaiting;

    long secondYouWaited = 0;
    TextView timeWaitingTextView;

    SeekBar tipSeekBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crazy_tip_cal);

        billBeforeTipET=(EditText)findViewById(R.id.editText2);
        tipAmountET=(EditText)findViewById(R.id.editText3);
        finalBillET=(EditText)findViewById(R.id.editText4);

        if(savedInstanceState == null){
            billBeforeTip=0.0;
            tipAmount=0.15;
            finalBill=0.0;
        }
        else{
            billBeforeTip=savedInstanceState.getDouble(BILL_WITHOUT_TIP);
            tipAmount=savedInstanceState.getDouble(CURRENT_TIP);
            finalBill=savedInstanceState.getDouble(TOTAL_BILL);
        }

        tipSeekBar = (SeekBar)findViewById(R.id.TipSeekBar);
        tipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);

        billBeforeTipET.addTextChangedListener(billBeforeTipListener);

        friendlyCheckbox = (CheckBox)findViewById(R.id.friendly);
        specialCheckbox = (CheckBox)findViewById(R.id.special);
        opinionCheckbox = (CheckBox)findViewById(R.id.opinion);

        setUpIntroBoxes();

        availabilityRadioGroup=(RadioGroup)findViewById(R.id.available_radiogroup);
        availableBadRadio=(RadioButton)findViewById(R.id.availableBadRadio);
        availableOkRadio=(RadioButton)findViewById(R.id.availableOkRadio);
        availableGoodRadio=(RadioButton)findViewById(R.id.availableGoodRadio);

        addChangeListenerToRadios();

        problemsSpinner = (Spinner)findViewById(R.id.spinner);

        addItemSelectedListenerToSpinner();

        start=(Button)findViewById(R.id.StartButton);
        pause=(Button)findViewById(R.id.pauseButton);
        reset=(Button)findViewById(R.id.resetButton);

        setButtonOnClickListener();

        timeWaiting=(Chronometer)findViewById(R.id.timeWaitingChronometer);

        timeWaitingTextView = (TextView)findViewById(R.id.timeWaitingTextView);



    }

    private void updateTipBaseOnYouWaited(long secondYouWaited){

        checklistValues[9]=(secondYouWaited>10)?-2:2;
        setTipFromWaitressChecklist();
        updateTipAndFinalBill();

    }

    public void setButtonOnClickListener(){

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stopppedMilliseconds = 0;
                String chronoText = timeWaiting.getText().toString();
                String array[] = chronoText.split(":");

                if (array.length==2){
                    stopppedMilliseconds = Integer.parseInt(array[0])*60*1000+Integer.parseInt(array[1])*1000;
                }else if(array.length==3){
                    stopppedMilliseconds=Integer.parseInt(array[0])*60*60*1000+Integer.parseInt(array[1])*60*1000+Integer.parseInt(array[2])*1000;
                }
                timeWaiting.setBase(SystemClock.elapsedRealtime()-stopppedMilliseconds);
                secondYouWaited=Long.parseLong(array[1]);
                updateTipBaseOnYouWaited(secondYouWaited);
                timeWaiting.start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeWaiting.stop();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeWaiting.setBase(SystemClock.elapsedRealtime());
                secondYouWaited=0;
            }
        });

    }

    public void addItemSelectedListenerToSpinner(){
        problemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checklistValues[6] = (problemsSpinner.getSelectedItem().equals("Bad"))?-1:0;
                checklistValues[7] = (problemsSpinner.getSelectedItem().equals("Ok"))?2:0;
                checklistValues[8] = (problemsSpinner.getSelectedItem().equals("Good"))?4:0;
                setTipFromWaitressChecklist();
                updateTipAndFinalBill();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void addChangeListenerToRadios(){
        availabilityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                checklistValues[3] = (availableBadRadio.isChecked())?-1:0;
                checklistValues[4] = (availableOkRadio.isChecked())?2:0;
                checklistValues[5] = (availableGoodRadio.isChecked())?4:0;
                setTipFromWaitressChecklist();
                updateTipAndFinalBill();

            }
        });

    }

    private void setUpIntroBoxes(){
        friendlyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checklistValues[0] = (friendlyCheckbox.isChecked())?4:0;
                setTipFromWaitressChecklist();
                updateTipAndFinalBill();

            }
        });

        specialCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checklistValues[1] = (specialCheckbox.isChecked())?1:0;
                setTipFromWaitressChecklist();
                updateTipAndFinalBill();

            }
        });

        opinionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checklistValues[2] = (opinionCheckbox.isChecked())?2:0;
                setTipFromWaitressChecklist();
                updateTipAndFinalBill();

            }
        });

    }

    private void updateTipAndFinalBill(){
        tipAmount=Double.parseDouble(tipAmountET.getText().toString());
        finalBill=billBeforeTip + (billBeforeTip*tipAmount);
        finalBillET.setText(String.format("%.02f",finalBill));
    }

    private void setTipFromWaitressChecklist(){
        int ChecklistTotal=0;
        for(int item: checklistValues){
            ChecklistTotal += item;

        }

        tipAmountET.setText(String.format("%.02f",ChecklistTotal*0.01));

    }



    private SeekBar.OnSeekBarChangeListener tipSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            tipAmount=(tipSeekBar.getProgress())*0.01;
            tipAmountET.setText(String.format("%.02f",tipAmount));
            updateTipAndFinalBill();
        }
        public void updateTipAndFinalBill(){
            tipAmount=Double.parseDouble(tipAmountET.getText().toString());
            finalBill=billBeforeTip + (billBeforeTip*tipAmount);
            finalBillET.setText(String.format("%.02f",finalBill));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public TextWatcher billBeforeTipListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            try{
                billBeforeTip=Double.parseDouble(charSequence.toString());
            }
            catch (NumberFormatException e){
                billBeforeTip=0.0;
            }

            updateTipAndFinalBill();

        }

        public void updateTipAndFinalBill(){
            tipAmount=Double.parseDouble(tipAmountET.getText().toString());
            finalBill=billBeforeTip + (billBeforeTip*tipAmount);
            finalBillET.setText(String.format("%.02f",finalBill));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putDouble(TOTAL_BILL,finalBill);
        outState.putDouble(CURRENT_TIP,tipAmount);
        outState.putDouble(BILL_WITHOUT_TIP,billBeforeTip);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crazy_tip_cal, menu);
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
