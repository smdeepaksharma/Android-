package com.sdsu.deepak.mytabapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class ContactListForChat extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<FireBaseUser> usersAvailableForChat;
    private static final String CHAT_USERS = "ChatUsers";
    private static final String TAG = "Hometown|ContactList";
    ListView contactListView;
    ChatListAdapter chatListAdapter;


    public ContactListForChat() {
        // Required empty public constructor
    }


    public static ContactListForChat newInstance(ArrayList<FireBaseUser> availableUsers) {
        ContactListForChat fragment = new ContactListForChat();
        Bundle args = new Bundle();
        args.putParcelableArrayList(CHAT_USERS,availableUsers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
        if (getArguments() != null) {
            usersAvailableForChat = getArguments().getParcelableArrayList(CHAT_USERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_contact_list_for_chat, container, false);

        contactListView = (ListView) view.findViewById(R.id.contact_chat_list);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
