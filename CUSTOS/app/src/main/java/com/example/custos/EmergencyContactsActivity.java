
package com.example.custos;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class EmergencyContactsActivity extends AppCompatActivity {

    private String m_Text = "";
    boolean checkEdit = false;
    public SearchView searchView;
    final String ALPHABET = "123456789abcdefghjkmnpqrstuvwxyz";


    boolean deleting = false;
    boolean editing = false;
    boolean duplicate = false;
    final String ec = "emergency_contacts";
    int checkEC = 0;

    DatabaseReference datta3;

    public EmergencyContactsActivity() {

    }

    // TODO: Rename and change types and number of parameters
    public static EmergencyContactsActivity newInstance() {
        EmergencyContactsActivity fragment = new EmergencyContactsActivity();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.emergency_contacts);

        ViewGroup layout = (ViewGroup) findViewById(R.id.listContact);





        datta3 = FirebaseDatabase.getInstance().getReference("Users").child("jdoe11");


        datta3.addValueEventListener(new ValueEventListener() {
        String info = "";
        String number = "";
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());
            if(!dataSnapshot.hasChild(ec))
            {
            datta3 = FirebaseDatabase.getInstance().getReference("Users").child("jdoe11").child(ec);



            }
            else
            {

                int nameequal = dataSnapshot.toString().indexOf("name=");
                int comma = dataSnapshot.toString().indexOf(", phone_number");


                info = dataSnapshot.toString().substring(nameequal + 5, comma);



                int phonenumberequala = dataSnapshot.toString().indexOf("phone_number=");
                int end = dataSnapshot.toString().indexOf("}}");

                number = dataSnapshot.toString().substring(phonenumberequala + 13, end);

                System.out.println(info);
                System.out.println(number);
                generateButton(info  + ": +" + number);
            }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }




        });



    }





    public void editButton(final Button button, final String name) {
        ViewGroup layout = (ViewGroup) findViewById(R.id.listContact);

        AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContactsActivity.this);
        builder.setTitle(name);
        builder.setCancelable(false);
        editing = true;

        View viewInflated = LayoutInflater.from(EmergencyContactsActivity.this).inflate(R.layout.blank_page, (ViewGroup) layout, false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        final EditText input2 = (EditText) viewInflated.findViewById(R.id.input2);
        builder.setView(viewInflated);

        String titleph = name.substring(name.indexOf('+')+1);


        //     System.out.println(titlename + " testing " + titleph);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String str = input2.getText().toString().replaceAll("[^\\d]", "").trim();

                System.out.println("TESTING PHONE: " + str);

                if (input.getText().toString().trim().length() <= 3) {
                    Toast.makeText(EmergencyContactsActivity.this, "Name is too short", Toast.LENGTH_SHORT).show();
                    //  dialog.dismiss();
                    editButton(button, name);


                }



                else if(str.length() != 10)
                {

                    Toast.makeText(EmergencyContactsActivity.this, "Enter only 10 digits", Toast.LENGTH_SHORT).show();
                    //  dialog.dismiss();

                    editButton(button, name);

                }


                else
                {

                    if (editing) {




                        datta3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                for (DataSnapshot Users : dataSnapshot2.getChildren()) {

                                    int nameequal = Users.toString().indexOf("name=");
                                    int comma = Users.toString().indexOf(", phone_number");
                                    String contact = Users.toString().substring(nameequal + 5);
//                                            contact = contact.substring(0,contact.indexOf(','));
                                    System.out.println(Users.toString());
                                    int keypos = Users.toString().indexOf("key =");
                                    int keystop = Users.toString().indexOf(", value");
                                    String key = Users.toString().substring(keypos + 5,keystop);
                                    key = key.trim();
                                    System.out.println("KEY : " + key);
                                    datta3 = FirebaseDatabase.getInstance().getReference("Users").child("jdoe11").child(ec).child(key);
                                 //   datta3 = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child("contacts").child(key); /////////////
                                    String titlename = name.substring(0,name.indexOf(':'));
                                    System.out.println("");
                                    System.out.println(contact + ":" + titlename);
                                    System.out.println("");
                                    if(contact.contains(titlename))
                                    {
                                        datta3 = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child(ec).child(key).child("name");

                                        datta3.setValue(input.getText().toString());
                                        datta3 = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child(ec).child(key).child("phone_number");
                                        datta3.setValue(str);
                                        break;
                                    }


                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }






                    dialog.dismiss();
                    m_Text = input.getText().toString() + ": +" + input2.getText().toString();
                    button.setText(m_Text);
                }





            }
        });

        builder.show();
        datta3 = FirebaseDatabase.getInstance().getReference("Users").child("jdoe11").child(ec);
    }




    public void generateButton(String title) {
        ImageView imageView = new ImageView(EmergencyContactsActivity.this);

        ViewGroup layout = (ViewGroup) findViewById(R.id.contactscroller);
        imageView.setImageResource(R.drawable.line);


        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        final Button btnTag = new Button(EmergencyContactsActivity.this );

        btnTag.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnTag.setBackgroundColor(Color.parseColor("#1D1D1D"));
        btnTag.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        btnTag.setText(title);
        btnTag.setTextColor(Color.WHITE);

        buttonAction(btnTag);

        layout.addView(btnTag);


    }




    public void buttonAction(final Button button) {

        int colon = button.getText().toString().indexOf(":");
        final String personName = button.getText().toString();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContactsActivity.this);
                builder.setCancelable(false);

                builder.setTitle((button.getText().toString()))
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        })
                        .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                editButton(button,);
                                

//
//                                String ph = button.getText().toString().replaceAll("[^\\d]", "");
//                                ph = ph.trim();
//
//                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ph, null)));
                            }
                        })
                        .setNegativeButton("Text", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String ph = button.getText().toString().replaceAll("[^\\d]", "");
                                ph = ph.trim();


                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setData(Uri.parse("sms:"+ph));
                                startActivityForResult(sendIntent , 0);

                            }
                        });



                builder.create();
                builder.show();

            }
        });


//        button.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//
//                AlertDialog.Builder builder2 = new AlertDialog.Builder(EmergencyContactsActivity.this);
//                builder2.setCancelable(false);
//                builder2.setTitle((button.getText().toString()))
//                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//
//                            }
//                        })
//                        .setNeutralButton("Call", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//
//                                String ph = button.getText().toString().replaceAll("[^\\d]", "");
//                                ph = ph.trim();
//
//                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ph, null)));
//                            }
//                        })
//                        .setNegativeButton("Text", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                String ph = button.getText().toString().replaceAll("[^\\d]", "");
//                                ph = ph.trim();
//
//
//                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                                sendIntent.setData(Uri.parse("sms:"+ph));
//                                startActivityForResult(sendIntent , 0);
//
//                            }
//
//                        });
//
//                builder2.create();
//                builder2.show();
//
//
//                return false;
//            }
//        });


    }

















}//end












/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.contactpage, container, false);

        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.contactscroller);
        //LinearLayout
        final Button contactAdder = (Button) view.findViewById(R.id.button33);
        contactAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addContactToLayout(contactAdder, layout);
            }


        });


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            final public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                {
                    Intent intent = new Intent(v.getContext(), MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    EmergencyContactsActivity.this.finish();
                    return true;
                }
                return false;
            }
        } );


        //////testing db


        final ArrayList<String> listShow = new ArrayList<String>();
        final ArrayList<String> listShow2 = new ArrayList<String>();
        datta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (deleting == false && editing == false) {

                    for (DataSnapshot Users : dataSnapshot.getChildren()) {

                        int nameequal = Users.toString().indexOf("name=");
                        int comma = Users.toString().indexOf(", phone_number");
                        String contact = Users.toString().substring(nameequal + 5, comma);




                        if (!contact.equals("donotdeletethis")) {
                            listShow.add(contact);
                        }


                        //   System.out.println(contact);    //seeing output of names

                        int phonenumberequala = Users.toString().indexOf("phone_number=");
                        int end = Users.toString().indexOf("} }");
                        String number = Users.toString().substring(phonenumberequala + 13, end);


                        if (!number.equals("donotdeletethis")) {
                            listShow2.add(number);
                        }


                        //      System.out.println(number);     //seeing output of #s
                    }


                    for (int i = 0; i < listShow.size(); i -= -1) {


                        generateButton(listShow.get(i) + ": +" + listShow2.get(i), layout);

                    }
                }

                listShow.clear();
                listShow2.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        deleting = false;


        //TODO somewhere below be able to update,modifly, and delete user


        return view;
    }


    public void addContactToLayout(final Button button, final LinearLayout layout) {


        AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContactsActivity.this);
        builder.setTitle("Enter Contact Information");
        builder.setCancelable(false);

        View viewInflated = LayoutInflater.from(EmergencyContactsActivity.this).inflate(R.layout.blank_page, EmergencyContactsActivity.this, false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        final EditText input2 = (EditText) viewInflated.findViewById(R.id.input2);
        builder.setView(viewInflated);


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String str = input2.getText().toString().replaceAll("[^\\d]", "");
                str = str.trim();
                System.out.println("TESTING PHONE: " + str);

                if (input.getText().toString().trim().length() <= 3) {
                    Toast.makeText(EmergencyContactsActivity.this, "Name is too short", Toast.LENGTH_SHORT).show();
                    //  dialog.dismiss();

                    addContactToLayout(button, layout);

                }



                else if(str.length() != 10)
                {

                    Toast.makeText(EmergencyContactsActivity.this, "Enter only 10 digits", Toast.LENGTH_SHORT).show();
                    //  dialog.dismiss();

                    addContactToLayout(button, layout);

                }

                else {

                    dialog.dismiss();


                    m_Text = input.getText().toString() + ": +" + str;
                    // System.out.println("test");
                    ImageView imageView = new ImageView(layout.getContext());


                    imageView.setImageResource(R.drawable.line);


                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                    final Button btnTag = new Button(layout.getContext());

                    btnTag.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    btnTag.setBackgroundColor(Color.parseColor("#1B1B1B"));
                    btnTag.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    btnTag.setText(m_Text);
                    btnTag.setTextColor(Color.WHITE);




                    //TODO temporary username till we figure what to do
                    final String username = getRandomWord(20);

                    deleting = true;
                    editing = true;

                    if(deleting && editing)
                    {


                        datta.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                datta = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child("contacts");

                                for (DataSnapshot Users : dataSnapshot.getChildren()) {

                                    int nameequal = Users.toString().indexOf("name=");
                                    int comma = Users.toString().indexOf(", phone_number");
                                    String contact = Users.toString().substring(nameequal + 5); //works cause firebase can't handle comma lmao
                                    //contact = contact.substring(0,input.getText().toString().length());

                                    System.out.println("CONTACT: " + contact);


                                    System.out.println("INPUT: " +  input.getText().toString());


//                                    if(input.getText().toString().equals(contact))
//                                    {
//
//                                        duplicate = true;
//                                        Toast.makeText(getActivity(), "Duplicate Contact", Toast.LENGTH_SHORT).show();
//
//                                       break;
//
//                                    }


                                }




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


                    datta = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child("contacts").child(username);

                    datta.child("name").setValue(input.getText().toString());
                    datta.child("phone_number").setValue(str);

                    buttonAction(btnTag);

                    layout.addView(btnTag);






                }





            }



        });
        duplicate = false;
        datta = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child("contacts");

        builder.show();



    }

    String getRandomWord(int length) {
        String r = "";
        for(int i = 0; i < length; i++) {
            r += (char)(Math.random() * 26 + 97);
        }
        return r;
    }





    public void generateButton(String title, LinearLayout layout) {
        ImageView imageView = new ImageView(layout.getContext());


        imageView.setImageResource(R.drawable.line);


        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        final Button btnTag = new Button(layout.getContext());

        btnTag.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnTag.setBackgroundColor(Color.parseColor("#1D1D1D"));
        btnTag.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        btnTag.setText(title);
        btnTag.setTextColor(Color.WHITE);

        buttonAction(btnTag);

        layout.addView(btnTag);


    }


    public void buttonAction(final Button button) {

        int colon = button.getText().toString().indexOf(":");
        final String personName = button.getText().toString();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContactsActivity.this);
                builder.setCancelable(false);

                builder.setTitle((button.getText().toString()))
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        })
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                deleteButton(button);
                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                editButton(button, personName);

                            }
                        });



                builder.create();
                builder.show();

            }
        });


        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                AlertDialog.Builder builder2 = new AlertDialog.Builder(EmergencyContactsActivity.this);
                builder2.setCancelable(false);
                builder2.setTitle((button.getText().toString()))
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        })
                        .setNeutralButton("Call", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String ph = button.getText().toString().replaceAll("[^\\d]", "");
                                ph = ph.trim();

                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ph, null)));
                            }
                        })
                        .setNegativeButton("Text", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String ph = button.getText().toString().replaceAll("[^\\d]", "");
                                ph = ph.trim();
//                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                                sendIntent.setData(Uri.parse("sms:"));
//                                startActivity(sendIntent);

                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setData(Uri.parse("sms:"+ph));
                                startActivityForResult(sendIntent , 0);

                            }

                        });

                builder2.create();
                builder2.show();


                return false;
            }
        });


    }


    public void deleteButton(final Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContactsActivity.this);
        builder.setTitle("Are you sure?");
        builder.setCancelable(false);


        String test = button.getText().toString();
        int colon = test.indexOf(":");
        test = test.substring(0, colon);

        System.out.println(test);

        final String delName = test;
        deleting = true;


        if (deleting) {


            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    datta.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                            for (DataSnapshot Users : dataSnapshot2.getChildren()) {

                                int nameequal = Users.toString().indexOf("name=");
                                int comma = Users.toString().indexOf(", phone_number");
                                String contact = Users.toString().substring(nameequal + 5);

//                                contact = contact.substring(0, delName.length());
                                System.out.println("CONTACT: " + contact);
                                System.out.println("WHAT I WANT TO DELETE: "+ delName);
                                if (contact.contains(delName)){
                                    Users.getRef().removeValue();
                                    break;
                                }


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    button.setVisibility(View.GONE);


                }
            });


        }


        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });
        builder.show();
//        boolean build = false;
//        while(builder != null && build == false)
//        {
//
//        }




    }



    public void editButton(final Button button, final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyContactsActivity.this);
        builder.setTitle(name);
        builder.setCancelable(false);
        editing = true;

        View viewInflated = LayoutInflater.from(EmergencyContactsActivity.this).inflate(R.layout.blank_page, , false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        final EditText input2 = (EditText) viewInflated.findViewById(R.id.input2);
        builder.setView(viewInflated);

        String titleph = name.substring(name.indexOf('+')+1);


        //     System.out.println(titlename + " testing " + titleph);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String str = input2.getText().toString().replaceAll("[^\\d]", "").trim();

                System.out.println("TESTING PHONE: " + str);

                if (input.getText().toString().trim().length() <= 3) {
                    Toast.makeText(EmergencyContactsActivity.this, "Name is too short", Toast.LENGTH_SHORT).show();
                    //  dialog.dismiss();
                    editButton(button, name);


                }



                else if(str.length() != 10)
                {

                    Toast.makeText(EmergencyContactsActivity.this, "Enter only 10 digits", Toast.LENGTH_SHORT).show();
                    //  dialog.dismiss();

                    editButton(button, name);

                }


                else
                {

                    if (editing) {




                        datta.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                for (DataSnapshot Users : dataSnapshot2.getChildren()) {

                                    int nameequal = Users.toString().indexOf("name=");
                                    int comma = Users.toString().indexOf(", phone_number");
                                    String contact = Users.toString().substring(nameequal + 5);
//                                            contact = contact.substring(0,contact.indexOf(','));
                                    System.out.println(Users.toString());
                                    int keypos = Users.toString().indexOf("key =");
                                    int keystop = Users.toString().indexOf(", value");
                                    String key = Users.toString().substring(keypos + 5,keystop);
                                    key = key.trim();
                                    System.out.println("KEY : " + key);
                                    datta = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child("contacts").child(key);
                                    String titlename = name.substring(0,name.indexOf(':'));
                                    System.out.println("");
                                    System.out.println(contact + ":" + titlename);
                                    System.out.println("");
                                    if(contact.contains(titlename))
                                    {
                                        datta = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child("contacts").child(key).child("name");

                                        datta.setValue(input.getText().toString());
                                        datta = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child("contacts").child(key).child("phone_number");
                                        datta.setValue(str);
                                        break;
                                    }


                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }






                    dialog.dismiss();
                    m_Text = input.getText().toString() + ": +" + input2.getText().toString();
                    button.setText(m_Text);
                }





            }
        });

        builder.show();
        datta = FirebaseDatabase.getInstance().getReference("Users").child("rlpham18").child("contacts");
    }
}






/*
package com.example.custos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class EmergencyContactsActivity extends AppCompatActivity {

    LinearLayout contact_group;
    LinearLayout contact_group2;
    LinearLayout contact_group3;
    LinearLayout contact_group4;
    TextView label_no;
    TextView label_no2;
    TextView label_no3;
    TextView label_no4;
    ArrayList<LinearLayout> contact_groups;
    ArrayList<TextView> number_labels;
    int add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contacts);
        add = 0;
        contact_groups = new ArrayList<LinearLayout>();
        number_labels = new ArrayList<TextView>();
        contact_group = findViewById(R.id.contact_group);
        contact_group2 = findViewById(R.id.contact_group2);
        contact_group3 = findViewById(R.id.contact_group3);
        contact_group4 = findViewById(R.id.contact_group4);
        label_no = findViewById(R.id.label_no_1);
        label_no2 = findViewById(R.id.label_no_2);
        label_no3 = findViewById(R.id.label_no_3);
        label_no4 = findViewById(R.id.label_no_4);

        contact_groups.add(contact_group);
        contact_groups.add(contact_group2);
        contact_groups.add(contact_group3);
        contact_groups.add(contact_group4);
        number_labels.add(label_no);
        number_labels.add(label_no2);
        number_labels.add(label_no3);
        number_labels.add(label_no4);
        for(int i = 0; i < contact_groups.size(); i++) {
            LinearLayout cg;
            TextView tv;
            if(i != 0) {
                cg = contact_groups.get(i);
                tv = number_labels.get(i);
                cg.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
            }
        }


    }

    public void onClick(View view) {
        startActivity(new Intent(this, SafetyPinActivity.class));
    }

    public void addContact(View view) {
        add++;
        if(add == 4) {
            add = 0;
        }
        System.out.println(add);
        contact_groups.get(add).setVisibility(View.VISIBLE);
        number_labels.get(add).setVisibility(View.VISIBLE);

    }
}

*/
