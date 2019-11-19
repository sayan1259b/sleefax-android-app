package com.prithviraj8.copycatandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PdfInfo extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    int ShopsCnt=0;

//    String colorType;

    TextView colorsTV,bwTV;
    Button done;
    Spinner pageSizeSpinner;
    View h,v;

//    String pdf_url;
    String pdf_url;
    int copy,resultCode,requestCode;
    String colour, pagesize;
    ArrayList<String> pdfURL = new ArrayList<>();
    EditText copies;
    String URI = new String();
    String fileType, orientation;
    String username,email;

    ArrayList<String> storeID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_info);
//        getSupportActionBar().hide();

        colorsTV = findViewById(R.id.Pdf_Colors);
        bwTV = findViewById(R.id.Pdf_Black_White);
        done = findViewById(R.id.Pdf_done);
        copies = findViewById(R.id.PDF_copies);
        pageSizeSpinner = findViewById(R.id.sizeSpinner);
        h = findViewById(R.id.h);
        v = findViewById(R.id.v);
        final String[] items = new String[]{"A4", "A3", "A2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        pageSizeSpinner.setAdapter(adapter);
        pagesize = items[pageSizeSpinner.getSelectedItemPosition()];


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        pdf_url = extras.getString("PdfURL");
//        pdf_url = extras.getParcelable("PdfURL");
        pdf_url = extras.getString("PdfURL");
        fileType = extras.getString("FileType");
        username = extras.getString("username");
        email = extras.getString("email");
        requestCode = extras.getInt("RequestCode");
        resultCode = extras.getInt("ResultCode");

        pdfURL.add((pdf_url));


        colorsTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0xFA9A0A,0xD15DF8});
                Log.d("Colors","Pressed");

                colour = ("Colors");

                colorsTV.setBackgroundResource(R.drawable.colors_border);
                bwTV.setBackgroundResource(R.drawable.black_white_view_backgroud);
                return false;
            }
        });

        bwTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[] {0x000000,0x616061});
                Log.d("Black/White","Pressed");

                colour = ("Black/White");

                bwTV.setBackgroundResource(R.drawable.b_w_border);
                colorsTV.setBackgroundResource(R.drawable.colors_view_background);
                return false;
            }
        });


        h.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                h.setBackgroundResource(R.drawable.orientation_after_clicked);
                v.setBackgroundResource(R.drawable.orientation);
                orientation = "h";

                return false;
            }
        });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                v.setBackgroundResource(R.drawable.orientation_after_clicked);
                h.setBackgroundResource(R.drawable.orientation);
                orientation = "v";
                return false;
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copy = (Integer.parseInt(copies.getText().toString()));
                 getShopsCount();

            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


    private void getShopsCount(){
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("Getting Shops","1");
                if(dataSnapshot.getKey().equals("Stores")){
                    ShopsCnt = (int) dataSnapshot.getChildrenCount();
                    for(DataSnapshot ids: dataSnapshot.getChildren()){
                        storeID.add(ids.getKey());
                    }

                }

//               new Handler().postDelayed(new Runnable() {
//                   @Override
//                   public void run() {
                if(ShopsCnt == 0){
                    Toast.makeText(getApplicationContext(),"Finding Shops.",Toast.LENGTH_SHORT);
                }
                if(ShopsCnt > 0 && pdfURL.size()>0) {
                    Intent intent = new Intent(PdfInfo.this, ShopsActivity.class);
                    Bundle extras = new Bundle();
//                       Log.d("PDFFS",pdfURL.get(0));
                    extras.putStringArrayList("URLS", pdfURL);
//                  extras.putParcelableArrayList("URLS", pdfURL);
                    extras.putInt("Copies", copy);
                    extras.putString("ColorTypes", colour);
                    extras.putInt("ShopCount", ShopsCnt);
                    extras.putString("FileType", fileType);
                    extras.putStringArrayList("StoreID", storeID);
                    extras.putString("PageSize", pagesize);
                    extras.putString("Orientation", orientation);
                    extras.putInt("RequestCode", requestCode);
                    extras.putInt("ResultCode", resultCode);

                    intent.putExtras(extras);
                    startActivity(intent);
                }
//                   }
//               },100);




            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}


