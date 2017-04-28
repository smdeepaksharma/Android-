 package com.sdsu.deepak.mytabapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;


 /**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatListFrament.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatListFrament#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListFrament extends Fragment implements AdapterView.OnItemClickListener{

     private static final String USERS_AVAILABLE = "Users Available";
     private ArrayList<FireBaseUser> availableUsersList = new ArrayList<>();
     private ArrayList<FireBaseUsers> previousChats = new ArrayList<>();
     ListView availUsersListView;
     ChatListAdapter chatListAdapter;
     private static final String TAG = "Hometown|ChatList";


    private OnFragmentInteractionListener mListener;

    public ChatListFrament() {
        // Required empty public constructor
    }

    public static ChatListFrament newInstance(ArrayList<FireBaseUser> users) {
        ChatListFrament fragment = new ChatListFrament();
        Bundle args = new Bundle();
        args.putParcelableArrayList(USERS_AVAILABLE,users);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           availableUsersList = getArguments().getParcelableArrayList(USERS_AVAILABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG,"Setting up chat list view...");
        View view = inflater.inflate(R.layout.fragment_chat_list_frament, container, false);
        availUsersListView = (ListView) view.findViewById(R.id.list_chat);
        availUsersListView.setOnItemClickListener(this);
        chatListAdapter = new ChatListAdapter(getActivity(),previousChats);
        availUsersListView.setAdapter(chatListAdapter);
        populateUserChatList();
        return view;
    }

    private void populateUserChatList(){
        Log.i(TAG,"Populating chat list");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        getChatList(currentUser.getDisplayName());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

     @Override
     public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
         FireBaseUsers selectedUser = (FireBaseUsers) adapterView.getItemAtPosition(i);
         mListener.startChartWith(selectedUser);

     }

     public void getChatList(final String sender) {
         final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
         databaseReference.child(Constants.ARG_CHAT_LIST).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(dataSnapshot.hasChild(sender)){
                     databaseReference.child(Constants.ARG_CHAT_LIST).child(sender).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                             while (dataSnapshots.hasNext()) {
                                 DataSnapshot dataSnapshotChild = dataSnapshots.next();
                                 FireBaseUsers firebaseUsers = dataSnapshotChild.getValue(FireBaseUsers.class);
                                 addItemToPreviousChat(firebaseUsers);
                             }
                         }
                         @Override
                         public void onCancelled(DatabaseError databaseError) {}
                     });
                 }
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {}
         });

     }


     private void addItemToPreviousChat(FireBaseUsers prevChatUser){
         previousChats.add(prevChatUser);
         chatListAdapter.notifyDataSetChanged();

     }



     public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

         void startChartWith(FireBaseUsers fireBaseUsers);

    }
}
