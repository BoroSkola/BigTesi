package com.example.bigtesi.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigtesi.R;
import com.example.bigtesi.data.workoutData;
import com.example.bigtesi.data.workoutDataAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;


public class NaploFragment extends Fragment {
    public static final String TAG = "Naplo";
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;
    private ChildEventListener childEventListener;
    private RecyclerView mRecyclerView;
    private ArrayList<workoutData> mWorkoutItemsData;
    private workoutDataAdapter mAdapter;
    private Button dateSetButton;
    private View view;
    private Context context;
    static private LocalDate sendTime = LocalDate.now();
    public static void setSendTime(LocalDate sendTime) {
        NaploFragment.sendTime = sendTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_workout,container,false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mWorkoutItemsData = new ArrayList<>();
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        mAdapter = new workoutDataAdapter(mWorkoutItemsData,context);
        mRecyclerView.setAdapter(mAdapter);
        dateSetButton = (Button) view.findViewById(R.id.dateSet);
        dateSetButton.setOnClickListener(v -> {
            DatePickerFragment datePicker = new DatePickerFragment();
            datePicker.setTargetFragment(NaploFragment.this, 0);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            datePicker.show(fm, "datepicker");
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
     }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        queryData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it.
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            LocalDate time = LocalDate.of(year, month + 1, dayOfMonth);
            Log.d(TAG, "onDateSet: " + time.toString());
            Intent intent = new Intent();
            intent.putExtra("date", time);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
    }

    public void queryData(){
        mWorkoutItemsData.clear();
        Log.d(TAG, "Time: " + sendTime);
        db.collection("UserWorkouts")
                .document(user.getUid())
                .collection("workouts")
                .orderBy("workoutDate", Query.Direction.DESCENDING)
                .whereEqualTo("workoutDate", sendTime.toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        workoutData item = documentSnapshot.toObject(workoutData.class);
                        mWorkoutItemsData.add(item);
                    }
                    dateSetButton.setText(sendTime.toString());
                    mAdapter.notifyDataSetChanged();
                    Log.d(TAG, "queryData: " + mWorkoutItemsData);
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == 0){
            sendTime = (LocalDate) data.getSerializableExtra("date");
            queryData();
        }
    }
}