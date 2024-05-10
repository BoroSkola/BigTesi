package com.example.bigtesi.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bigtesi.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestTomegFragment extends Fragment {
    private static final String TAG = "Tomeg";
    private FirebaseUser user;
    private FirebaseFirestore db;
    private HashMap<LocalDate, Double> weights = new HashMap<>();
    private View view;
    private EditText weightToSend;
    private Button sendButton;

    private int chartDescriptionColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // on below line we are initializing our graph view.
        view = inflater.inflate(R.layout.fragment_testtomeg, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
       sendButton = view.findViewById(R.id.sendWeight);
        weightToSend = view.findViewById(R.id.weightET);
        weightToSend.setEnabled(true);
        sendButton.setEnabled(true);
        queryData();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weightToSend.getText().toString().equals("")){
                    Toast toast = Toast.makeText(getActivity(), "Add meg hány kiló vagy!", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    sendWeightData();
                    sendButton.setEnabled(false);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public void sendWeightData(){
        weightToSend = view.findViewById(R.id.weightET);
        Map<String, Object> data = new HashMap<>();
        data.put("date", LocalDate.now().toString());
        data.put("userWeight", weightToSend.getText().toString());

        db.collection("UserWeights")
                .document(user.getUid())
                .collection("weights")
                .document(LocalDate.now().toString())
                .set(data).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "DataSaveOnComplete: Yippeee");
                    }else{
                        Log.d(TAG, "DataSaveOnComplete: awww");
                    }
                });

        weightToSend.setEnabled(false);
        queryData();
    }

    public void queryData(){
        db.collection("UserWeights")
                .document(user.getUid())
                .collection("weights")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(15)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                Log.d(TAG, "onTestTomegGraphDataQuery: még nincs adat");
                                weightToSend.setEnabled(true);
                                sendButton.setEnabled(true);
                            }else{
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    weights.put(LocalDate.parse(documentSnapshot.getData().get("date").toString()),
                                            Double.parseDouble(documentSnapshot.getData().get("userWeight").toString()));
                                }
                                drawGraph(view);
                                Log.d(TAG, "queryData: " + weights);
                            }

                        }else{
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
        Log.d(TAG, "queryData: " + weights);
    }

    public void drawGraph(View view){
        Date now = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        BarChart chart = view.findViewById(R.id.idGraphView);
        chart.clear();

        List<BarEntry> entries = new ArrayList<BarEntry>();

        Collections.reverse(Arrays.asList(weights));

        weights.forEach((key,value) -> {
            entries.add(new BarEntry(key.getDayOfYear() , value.floatValue()));
        });

        BarDataSet dataSet = new BarDataSet(entries, "Label");

        chart.getLegend().setTextSize(13);
        chart.getXAxis().setTextSize(13);
        chart.getAxisLeft().setTextSize(13);

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                chart.getXAxis().setTextColor(Color.WHITE);
                chart.getAxisLeft().setTextColor(Color.WHITE);
                chart.getLegend().setTextColor(Color.WHITE);
                chart.getDescription().setText("");
                dataSet.setValueTextColor(Color.WHITE);
                dataSet.setValueTextSize(11);
                dataSet.setColor(Color.parseColor("#D0BCFF"));
                dataSet.setLabel("Testtömeg kg-ban");
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                chart.getXAxis().setTextColor(Color.BLACK);
                chart.getAxisLeft().setTextColor(Color.BLACK);
                chart.getDescription().setText("");
                dataSet.setColor(Color.parseColor("#6750a5"));
                dataSet.setLabel("Testtömeg kg-ban");
                break;
        }

        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.invalidate();
        chart.notifyDataSetChanged();
    }
}