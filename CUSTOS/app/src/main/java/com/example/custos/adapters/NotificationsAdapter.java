package com.example.custos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.custos.EventPopupActivity;
import com.example.custos.NotificationActivity;
import com.example.custos.OtherUserActivity;
import com.example.custos.R;
import com.example.custos.utils.Common;
import com.example.custos.utils.Event;
import com.example.custos.utils.Notifications;
import com.example.custos.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private Context context;
    private List<Notifications> notificationsList;
    private Map<Integer, Object> deletedItems;
    private DatabaseReference databaseReference;
    private String userName, start_date, area, locationname, lat, lng, name, time, description;
    private String imgURL, dateAccept, timeAccept, end_date, currentToken, start_time, end_time, userName2;
    private DatabaseReference notificationsRef, eventRef, notificationRef3;
    private FirebaseUser firebaseUser;
    private Handler handler = new Handler();
    private int eventSerial, tempEventSerial;
    private ArrayList<User> invitedUsers;
    private Random random = new Random();
    private static int ui_flags =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    public NotificationsAdapter(Context context, List<Notifications> notificationsList2) {
        this.context = context;
        this.notificationsList = notificationsList2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_notifications, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.itemView.setLongClickable(true);
        final Notifications notification = notificationsList.get(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);
        notificationsRef = FirebaseDatabase.getInstance().getReference(Common.NOTIFICATIONS)
                .child(firebaseUser.getUid())
                .child("friend_request_notifications");
        eventRef = FirebaseDatabase.getInstance().getReference("user_event");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if (notification.getUID() != null) {
                    if (dataSnapshot.child(notification.getUID()).child(Common.USER_NAME).exists()) {
                        userName = dataSnapshot.child(notification.getUID())
                                .child(Common.USER_NAME).getValue().toString();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (notification.getUID() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION).child(notification.getUID());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getImageURL().equals("default")) {
                        holder.friendImage.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        imgURL = dataSnapshot.child(Common.IMAGE_URL).getValue().toString();
                        Glide.with(context).load(imgURL).into(holder.friendImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        for (int i = 0; i < 3; i++) {
            int randomInteger = random.nextInt(100000);
            eventSerial = randomInteger;
            tempEventSerial = eventSerial;
        }

        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // System.out.println(notification.getUID() + "-------------------------------");
                if (dataSnapshot.exists()) {
                    if (notification.getUID() != null) {
                        if (dataSnapshot.child(notification.getUID()).exists()) {
                            if (dataSnapshot.child(notification.getUID())
                                    .child("request_type")
                                    .getValue()
                                    .toString().equals("receivedFriendRequest")) {
                                holder.friendName.setText(userName + " wants to add you as friend");

                            }
                            if (dataSnapshot.child(notification.getUID())
                                    .child("request_type")
                                    .getValue()
                                    .toString().equals("acceptedFriendRequest")) {
                                holder.friendName.setText(userName + " accepted your friend request");
                            }
                        }
                    }


                    if (notification.getEventId() != null) {
                        if (dataSnapshot.child(notification.getEventId()).exists()) {
                            if (dataSnapshot.child(notification.getEventId())
                                    .child("request_type")
                                    .getValue()
                                    .toString().equals("invite_sent")) {
                                holder.friendName.setText(userName + " has invited you to an event");
                            }
                        }
                    }
                    if (notification.getUserToken() != null) {
                        if (dataSnapshot.child(notification.getUserToken()).exists()) {
                            if (dataSnapshot.child(notification.getUserToken())
                                    .child("request_type")
                                    .getValue()
                                    .toString().equals("declined_invite")) {
                                holder.friendName.setText(userName + " has declined event "+ notification.getEventId());
                            }
                            if (dataSnapshot.child(notification.getUserToken())
                                    .child("request_type")
                                    .getValue()
                                    .toString().equals("accepted_invite")) {
                                holder.friendName.setText(userName + " has accepted event " + notification.getEventId());
                            }

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //        notificationsRef = FirebaseDatabase.getInstance().getReference(Common.NOTIFICATIONS).child(firebaseUser.getUid());
//        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.child(notification.getUID()).child(Common.FRIEND_NAME).getValue().toString().equals(userName)) {
//                    notificationsRef.child(notification.getUID()).child(Common.FRIEND_NAME).setValue(userName);
//                }
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
//                        String image = dataSnapshot2.child(Common.IMAGE_URL).getValue().toString();
//                        if (!dataSnapshot.child(notification.getUID()).child(Common.IMAGE_URL).getValue().toString().equals(image)) {
//                            notificationsRef.child(notification.getUID()).child(Common.IMAGE_URL).setValue(image);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
                .child(firebaseUser.getUid());
        userRef.addListenerForSingleValueEvent(new

                                                       ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                               currentToken = dataSnapshot.child("userToken").getValue().toString();
                                                           }

                                                           @Override
                                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                                           }
                                                       });


        //holder.friendEmail.setText(friends.getFriendEmail());
        holder.date.setText("On " + notification.getRequest_time());
//        holder.friendName.setText(friends.getFriendName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(notification.getUID()).exists()) {
                            if ((dataSnapshot.child(notification.getUID())
                                    .child("request_type")
                                    .getValue()
                                    .toString().equals("receivedFriendRequest")
                                    || dataSnapshot.child(notification.getUID()).child("request_type")
                                    .getValue().toString().equals("acceptedFriendRequest"))
                                    && notification.getEventId().equals("notevent")) {
                                Intent intent = new Intent(context, OtherUserActivity.class);
                                intent.putExtra("userid", notification.getUID());
                                context.startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                notificationRef3 = FirebaseDatabase.getInstance().getReference(Common.NOTIFICATIONS)
                        .child(notification.getUID()).child(Common.REQUEST_NOTIFICATION);
                Calendar calendarAccept = Calendar.getInstance();
                SimpleDateFormat acceptDate = new SimpleDateFormat("dd-MMMM-yyyy");
                Calendar timeAcceptFriend = Calendar.getInstance();
                SimpleDateFormat acceptTime = new SimpleDateFormat("hh:mm a");
                dateAccept = acceptDate.format(calendarAccept.getTime());
                timeAccept = acceptTime.format(timeAcceptFriend.getTime());
                notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(notification.getUserToken()!=null){
                            if(dataSnapshot.child(notification.getUserToken()).exists()){
                                if(dataSnapshot.child(notification.getUserToken())
                                        .child("request_type")
                                        .getValue()
                                        .toString().equals("accepted_invite")
                                        || dataSnapshot.child(notification.getUserToken())
                                        .child("request_type")
                                        .getValue()
                                        .toString().equals("declined_invite")){
                                    Intent intent = new Intent(context, EventPopupActivity.class);
                                    intent.putExtra("eventId", notification.getEventId());
                                    intent.putExtra("userId",notification.getUID());
                                    context.startActivity(intent);
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(notification.getEventId()).exists()) {
                            if (dataSnapshot.child(notification.getEventId())
                                    .child("request_type")
                                    .getValue()
                                    .toString().equals("invite_sent")) {

                                Intent intent = new Intent(context, EventPopupActivity.class);
                                intent.putExtra("eventId", notification.getEventId());
                                intent.putExtra("userId",notification.getUID());
                                context.startActivity(intent);
//                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.Chill);
//                                alertDialog.setTitle("Event Invitation");
//                                alertDialog.setMessage("Do you want to attend this event?");
//                                alertDialog.setIcon(R.drawable.ic_event_yellow_24dp);
//                                alertDialog.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(final DialogInterface dialogInterface, int i) {
//                                        notificationsRef.child(notification.getEventId()).removeValue()
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if (task.isSuccessful()) {
//                                                            Toast.makeText(context, "removed notification", Toast.LENGTH_SHORT).show();
//                                                        } else {
//                                                            Toast.makeText(context, "removed failed", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    }
//                                                });
//                                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.child(firebaseUser.getUid()).child(notification.getEventId()).exists()) {
//                                                    eventRef.child(firebaseUser.getUid()).child(notification.getEventId()).removeValue()
//                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                    Toast.makeText(context, "delete declined event", Toast.LENGTH_SHORT).show();
//                                                                }
//                                                            });
//                                                } else if (!dataSnapshot.child(notification.getUID()).child(notification.getEventId()).exists()) {
//                                                    Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show();
//                                                    dialogInterface.cancel();
//                                                } else {
//                                                    eventRef.child(notification.getUID()).child(notification.getEventId())
//                                                            .child("invited_users").child(firebaseUser.getUid())
//                                                            .child("status").setValue("declined");
//
//                                                    notificationRef3.child(currentToken)
//                                                            .child("request_type").setValue("declined_invite")
//                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                    if (task.isSuccessful()) {
//                                                                        notificationRef3.child(currentToken)
//                                                                                .child("request_time").setValue(dateAccept + " at " + timeAccept);
//                                                                        notificationRef3.child(currentToken)
//                                                                                .child("eventId").setValue(notification.getEventId());
//                                                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance()
//                                                                                .getReference(Common.USER_INFORMATION);
//
//                                                                        databaseReference2.addValueEventListener(new ValueEventListener() {
//                                                                            @Override
//                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                                                String nameCurr = dataSnapshot.child(firebaseUser.getUid())
//                                                                                        .child(Common.USER_NAME).getValue().toString();
//                                                                                notificationRef3.child(currentToken)
//                                                                                        .child(Common.FRIEND_NAME).setValue(nameCurr);
//                                                                                String uidCurr = dataSnapshot.child(firebaseUser.getUid())
//                                                                                        .child(Common.UID).getValue().toString();
//                                                                                notificationRef3.child(currentToken)
//                                                                                        .child(Common.UID).setValue(uidCurr);
//                                                                                String img = dataSnapshot.child(firebaseUser.getUid())
//                                                                                        .child(Common.IMAGE_URL).getValue().toString();
//                                                                                notificationRef3.child(currentToken)
//                                                                                        .child(Common.IMAGE_URL).setValue(img);
//                                                                                String token = dataSnapshot.child(firebaseUser.getUid())
//                                                                                        .child("userToken").getValue().toString();
//                                                                                notificationRef3.child(currentToken)
//                                                                                        .child("userToken").setValue(token);
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                                            }
//                                                                        });
//
//                                                                    }
//                                                                }
//                                                            });
//                                                    dialogInterface.dismiss();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//
//                                    }
//                                });
//                                alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(final DialogInterface dialogInterface, int i) {
//
//
//                                    }
//                                });
//                                AlertDialog alertDialog2 = alertDialog.create();
//
//                                // Set alertDialog "not focusable" so nav bar still hiding:
//                                alertDialog2.getWindow().
//                                        setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//
//                                // Set full-sreen mode (immersive sticky):
//                                alertDialog2.getWindow().getDecorView().setSystemUiVisibility(ui_flags);
//
//                                // Show the alertDialog:
//                                alertDialog2.show();
//
//                                // Set dialog focusable so we can avoid touching outside:
//                                alertDialog2.getWindow().
//                                        clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        final DatabaseReference notificationRef2 = FirebaseDatabase.getInstance().getReference(Common.NOTIFICATIONS);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.Chill);
                alertDialog.setTitle("Delete Notification");
                alertDialog.setMessage("Do you want to remove this notification?");
                alertDialog.setIcon(R.drawable.ic_person_add_black_24dp);
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        notificationRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                notificationRef2.child(firebaseUser.getUid()).child("friend_request_notifications").child(notification.getUID())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(context, "removed notification", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "removed failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if(notification.getEventId() != null){
                            notificationsRef.child(notification.getEventId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(context, "removed notification", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "removed failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        if(notification.getUserToken()!=null){
                            notificationsRef.child(notification.getUserToken()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(context, "removed notification", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "removed failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        // dialogInterface.dismiss();
                    }
                });
                // Create the alertDialog:
                AlertDialog alertDialog2 = alertDialog.create();

                // Set alertDialog "not focusable" so nav bar still hiding:
                alertDialog2.getWindow().
                        setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // Set full-sreen mode (immersive sticky):
                alertDialog2.getWindow().getDecorView().setSystemUiVisibility(ui_flags);

                // Show the alertDialog:
                alertDialog2.show();

                // Set dialog focusable so we can avoid touching outside:
                alertDialog2.getWindow().
                        clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendEmail;
        public ImageView friendImage;
        public TextView friendName;
        public TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //friendEmail = itemView.findViewById(R.id.txt_friends_email);
            friendName = itemView.findViewById(R.id.txt_friend_name_notification);
            date = itemView.findViewById(R.id.txt_friends_date_notification);
            friendImage = itemView.findViewById(R.id.imageNotification);

        }
    }
}
