package com.sdsu.deepak.mytabapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsFragment extends Fragment{

    EditText textMessage;
    TextView receiverNameTextView;
    FloatingActionButton sendMessageButton;
    private ArrayList<Chat> chatlist = new ArrayList<>();
    private MessageListener mListener;
    RecyclerView chatListView;
    ChatRecyclerAdapter chatAdapter;
    private static final String TAG = "Hometown|ChatDetails";

    FireBaseUser sender;
    FireBaseUser receiver;

    public ChatDetailsFragment() {
        // Required empty public constructor
    }


    public static ChatDetailsFragment newInstance(FireBaseUser sen, FireBaseUser rec) {
        ChatDetailsFragment fragment = new ChatDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("Sender",sen);
        args.putParcelable("Receiver",rec);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sender = getArguments().getParcelable("Sender");
            receiver = getArguments().getParcelable("Receiver");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_details, container, false);
        textMessage = (EditText) view.findViewById(R.id.edit_text_message);

        chatListView = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        sendMessageButton = (FloatingActionButton) view.findViewById(R.id.fab);
        chatAdapter = new ChatRecyclerAdapter(chatlist);
        chatListView.setAdapter(chatAdapter);
        receiverNameTextView = (TextView) view.findViewById(R.id.receiver_name) ;
        receiverNameTextView.setText(receiver.displayName);
        getMessageFromFirebaseUser(sender.uid,receiver.uid);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textMessage.getText().toString().equalsIgnoreCase("")){
                    textMessage.setError("Enter message");
                } else {
                    sendMessageToFirebaseUser(getContext(), new Chat(sender.displayName,receiver.displayName,
                            sender.uid,receiver.uid, textMessage.getText().toString(),new Date().getTime()));
                    textMessage.setText("");
                }

            }
        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MessageListener) {
            mListener = (MessageListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void sendMessageToFirebaseUser(final Context context, final Chat chat) {
        final String room_type_1 = chat.senderUid + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    getMessageFromFirebaseUser(chat.senderUid, chat.receiverUid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });


        databaseReference.child(Constants.ARG_CHAT_LIST).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                if(dataSnapshot.hasChild(room_type_1)){
                    databaseReference.child(Constants.ARG_CHAT_LIST).child(chat.sender).child(chat.receiver).setValue(new FireBaseUsers(chat.receiverUid,chat.receiver,chat.message));
                } else if(dataSnapshot.hasChild(room_type_2)){
                    databaseReference.child(Constants.ARG_CHAT_LIST).child(chat.sender).child(chat.receiver).setValue(new FireBaseUsers(chat.receiverUid,chat.receiver,chat.message));
                } else {
                    databaseReference.child(Constants.ARG_CHAT_LIST).child(chat.sender).child(chat.receiver).setValue(new FireBaseUsers(chat.receiverUid,chat.receiver,chat.message));
                }}catch (Exception e){
                    Toast.makeText(getActivity(),"Unable to send message",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.i(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            chatAdapter.add(chat);
                            chatAdapter.notifyDataSetChanged();
                            chatListView.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            chatAdapter.add(chat);
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.i(TAG, "getMessageFromFirebaseUser: no such room available");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }

    public interface MessageListener {
        void sendMessage(String message);
    }
}
