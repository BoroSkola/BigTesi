package com.example.bigtesi.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bigtesi.HomeActivity;
import com.example.bigtesi.LoginActivity;
import com.example.bigtesi.NotificationHelper;
import com.example.bigtesi.R;
import com.example.bigtesi.data.workoutData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MaiFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Mai";
    private LinearLayout cardContainer;
    private Context context;
    private int eTNumber = 0;
    private View cardViewLayout;
    private CardView cardView;
    private LinearLayout linearLayout;
    private int views=0;
    private View view;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private NotificationHelper notificationHelper;

    public MaiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mai, container, false);
        cardContainer = view.findViewById(R.id.maiCardContainer);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        notificationHelper = new NotificationHelper(context);
        Button newExerciseButton = (Button) view.findViewById(R.id.butAdd);
        newExerciseButton.setOnClickListener(v -> {
            createCard();
            views++;
        });
        Button sendWorkoutButton = (Button) view.findViewById(R.id.sendWorkout);
        sendWorkoutButton.setOnClickListener(v -> {
            sendConfirm();
        });
        return view;
    }

    public void createCard() {
        cardViewLayout = LayoutInflater.from(context).inflate(R.layout.mai_card_view, cardContainer, false);
        cardView = cardViewLayout.findViewById(R.id.cardView);
        cardView.setContentPadding(20, 20, 20, 20);
        cardView.setCardElevation(4);
        cardView.setRadius(8);

        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        String[] textFields = {"Gyakorlat neve: ", "Súly: ", "Kör: ", "Ismétlés: ", "Ismétlés idő: "};
        for (int i = 0; i < 5; i++) {
            EditText editText = new EditText(context);
            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,

                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            editTextParams.setMargins(16, 16, 16, 16);
            editText.setId(eTNumber);
            editText.setLayoutParams(editTextParams);
            if (i == 2 || i == 3) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            editText.setHint(textFields[i]);

            linearLayout.addView(editText);
            Log.d(TAG, "id: " + editText.getId());
            eTNumber++;
        }
        cardView.addView(linearLayout);
        cardContainer.addView(cardViewLayout, cardContainer.getChildCount() - 1);
    }

    /**
     *
     * TODO: workout küldésnél a napló áljon át a mai dátumra
     */
    public void sendWorkout() {
        EditText eTName;
        EditText eTWeight;
        EditText eTSets;
        EditText eTReps;
        EditText eTTime;
        workoutData data = new workoutData();
        for (int i = 0; i < eTNumber; i = i + 5) {
            eTName = view.findViewById(i);
            eTWeight = view.findViewById(i + 1);
            eTSets = view.findViewById(i + 2);
            eTReps = view.findViewById(i + 3);
            eTTime = view.findViewById(i + 4);
            if(eTName.getText().toString().equals("") || eTWeight.getText().toString().equals("") || eTSets.getText().toString().equals("") || eTReps.getText() .toString().equals("") || eTTime.getText() .toString().equals("")){
                Toast toast = Toast.makeText(getActivity(), R.string.emptyText, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }else{
                LocalDateTime sentTime = LocalDateTime.now();
                data.setSentTime(sentTime.toString());
                data.setWorkoutDateLocal(LocalDate.now());
                data.setWorkoutName(eTName.getText().toString());
                data.setWeight(eTWeight.getText().toString());
                data.setWorkoutSets(Integer.parseInt(eTSets.getText().toString()));
                data.setWorkoutReps(Integer.parseInt(eTReps.getText().toString()));
                data.setWorkoutTime(eTTime.getText().toString());
                db.collection("UserWorkouts")
                        .document(user.getUid())
                        .collection("workouts")
                        .document(sentTime.toString())
                        .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "DataSaveOnComplete: Yippeee");
                                }else{
                                    Log.d(TAG, "DataSaveOnComplete: awww");
                                }
                            }
                        });
            }
        }
        cardView.removeView(linearLayout);
        cardContainer.removeViews(1,views);
        views = 0;
        eTNumber = 0;
        notificationHelper.send("Gratulálok a mai teljesítményhez!");
    }

    public void sendConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Megerősítés");
        builder.setMessage("Biztosan felszeretné tölteni?");
        builder.setPositiveButton("Igen",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NaploFragment.setSendTime(LocalDate.now());
                        sendWorkout();
                    }
                });
        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onNegativeClick: no" );
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }
}