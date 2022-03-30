package com.example.calllogs;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import com.example.calllogs.Objects.Call;
import com.example.calllogs.Objects.Contact;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView t1;
    ArrayList<Call> calls = new ArrayList<>();
    ArrayList<Contact> contacts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, PackageManager.PERMISSION_GRANTED);

        

    }


    public void getCall(View view) {

        String output = "";

//        getting info of call logs
        Uri uricall = Uri.parse("content:/call_log/calls");

//           getting info of call logs
        Cursor cursorcall = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        String number;
        String name;
        int callDuration;
        String dateString;
        int dir=-1;
        int index=-1;
        String type=null;

        cursorcall.moveToFirst();

        do {

            Call call = new Call();
            Contact contact = new Contact();

            number = cursorcall.getString(cursorcall.getColumnIndex(CallLog.Calls.NUMBER));
            call.setNumber(number);

            name = cursorcall.getString(cursorcall.getColumnIndex(CallLog.Calls.CACHED_NAME));
            if(name != null)
                call.setName(name);
            else
                call.setName(number);

            callDuration = Integer.parseInt(cursorcall.getString(cursorcall.getColumnIndex(CallLog.Calls.DURATION)));
            call.setDuration(callDuration);

            int dates = cursorcall.getColumnIndex(CallLog.Calls.DATE);
            String callDate = cursorcall.getString(dates);
            long callDayTime = Long.parseLong(callDate);
            call.setDate(callDayTime);

            String callType = cursorcall.getString(cursorcall.getColumnIndex(CallLog.Calls.TYPE));
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = 0;
                    break;
                    case CallLog.Calls.INCOMING_TYPE:
                    dir = 1;
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = 2;
                    break;
            }
            call.setType(dir);


            calls.add(call);


            index = -1;
            for(int i =0;i<contacts.size();i++){
                    if(number.equals(contacts.get(i).getNumber())) {
                        index = i;

                        break;
                    }
                }

            if(index == -1) {
                contact.setNumber(call.getNumber());
                contact.setName(call.getName());
                contacts.add(contact);
                index = contacts.size()-1;
            }

            switch (dir){
                case 0:
                    contacts.get(index).setOutgoingDuration(contacts.get(index).getOutgoingDuration()+callDuration);
                    contacts.get(index).setNumberOfOutgoingCalls(contacts.get(index).getNumberOfOutgoingCalls()+1);
                    contacts.get(index).setLongestDuration(Math.max(contacts.get(index).getLongestDuration(),callDuration));
                    break;
                case 1:
                    contacts.get(index).setIncomingDuration(contacts.get(index).getIncomingDuration()+callDuration);
                    contacts.get(index).setNumberOfIncomingCalls(contacts.get(index).getNumberOfIncomingCalls()+1);
                    contacts.get(index).setLongestDuration(Math.max(contacts.get(index).getLongestDuration(),callDuration));
                case 2:
                    contacts.get(index).setNumberOfMissedCalls(contacts.get(index).getNumberOfMissedCalls()+1);
            }




               /*output += "Number  :  " + number + "\n \n" +
                        "Name  :  " + name + "\n \n" +
                        "Duration  :  " + callDuration + "\n \n" +
                        "Date  :  " + dateString + "\n \n" +
                        "Type  :  " + callType(dir) + "\n \n" +
                        "-----------------------------------------------" + "\n";*/


        }
            while (cursorcall.moveToNext());
            for(int i =0;i< calls.size();i++) {
                output = output+calls.get(i).getName() + " " +calls.get(i).getNumber() +"\n \n"+
                        calls.get(i).getDuration()+ " " + dateString(calls.get(i).getDate())+" "+callType(calls.get(i).getType())+"\n \n" ;
            }

        for(int i = 0; i < contacts.size();i++){
            output = output+ contacts.get(i).getName() + " "+ contacts.get(i).getNumber()+"\n \n"
            + contacts.get(i).getNumberOfOutgoingCalls()+" "+contacts.get(i).getOutgoingDuration()+"\n \n"
            + contacts.get(i).getNumberOfIncomingCalls()+" "+contacts.get(i).getIncomingDuration()+"\n \n"
            + contacts.get(i).getNumberOfTotalCalls()+" "+contacts.get(i).getLongestDuration()+"\n \n"
            + contacts.get(i).getNumberOfMissedCalls()+" "+ contacts.get(i).getTotalDuration()+"\n \n"
            +"-----------------------------------------------" + "\n"
            ;
        }
            t1.setText(output);

    }

    public String callType(int num){
        if(num == 0){
            return "Outgoing";
        }else if (num == 1){
            return "Incoming";
        }else if(num == 2){
            return "missed";
        }else {
            return "other";
        }
    }

    public String dateString(long callDayTime){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        return formatter.format(callDayTime);
    }

    public void sort_function(){

    }
 }
