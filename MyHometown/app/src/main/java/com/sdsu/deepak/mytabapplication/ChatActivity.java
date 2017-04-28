package com.sdsu.deepak.mytabapplication;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatActivity extends AppCompatActivity implements ContactListForChat.OnFragmentInteractionListener,ChatDetailsFragment.MessageListener, ChatListFrament.OnFragmentInteractionListener{

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private static final String TAG = "Hometown|Chat";
    ArrayList<FireBaseUser> usersAvailableForChat = new ArrayList<>();
    ArrayList<Chat> chatlist = new ArrayList<>();
    ListView chatListView;
    ChatListAdapter chatListAdapter;
    Toolbar toolbar;
    ImageView contactListButton;
    User chatWith;
    String receiverId;
    FireBaseUser sender;
    FireBaseUser receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);




        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        usersAvailableForChat = new ArrayList<>();
        getUserChatList();
        Intent chatIntent = getIntent();
        chatWith = chatIntent.getParcelableExtra("User Data");
        receiverId = chatIntent.getStringExtra("userid");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sender = new FireBaseUser(currentUser.getUid(),currentUser.getDisplayName());
        receiver = new FireBaseUser(receiverId,chatWith.getNickname());
        setUpChat();
    }

    private void setUpChat(){
        ChatDetailsFragment chatDetailsFragment = ChatDetailsFragment.newInstance(sender,receiver);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.chat_details_container,chatDetailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ChatActivity.this,
                                        "You have been signed out.",
                                        Toast.LENGTH_LONG)
                                        .show();
                                // Close activity
                                finish();
                            }
                        });
                break;
            case R.id.list_view:
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.map_view:
                Intent map = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(map);
                finish();
                break;
            case android.R.id.home:
                Intent home = new Intent(ChatActivity.this, MainActivity.class);
                navigateUpTo(home);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        super.onOptionsItemSelected(item);
        return true;
    }

    public ArrayList<FireBaseUser> getUserChatList() {
        Log.i(TAG,"getUserChatList");
        final ArrayList<FireBaseUser> users = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    FireBaseUser user = dataSnapshotChild.getValue(FireBaseUser.class);
                    Log.i(TAG,"FB user"+user.displayName);
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                        usersAvailableForChat.add(user);
                    }
                }
                setUpChatList();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return users;
    }

    private void setUpChatList(){
        Log.i(TAG,"setting up chat list");
        ChatListFrament chatListFrament = ChatListFrament.newInstance(usersAvailableForChat);
        getSupportFragmentManager().beginTransaction().replace(R.id.chat_list_container,chatListFrament).commit();
    }









    public void displayChatUsersList(ArrayList<FireBaseUser> users){
        ContactListForChat contactListForChat = ContactListForChat.newInstance(users);
        getSupportFragmentManager().beginTransaction().replace(R.id.chat_list_container,contactListForChat).commit();
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void startChartWith(FireBaseUsers fireBaseUsers) {

        FireBaseUser fireBaseUser = new FireBaseUser(fireBaseUsers.getUserid(),fireBaseUsers.getName());
        ChatDetailsFragment chatDetailsFragment = ChatDetailsFragment.newInstance(sender,fireBaseUser);
       getSupportFragmentManager().beginTransaction()
               .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left)
               .replace(R.id.chat_details_container,chatDetailsFragment).commit();
    }

}
