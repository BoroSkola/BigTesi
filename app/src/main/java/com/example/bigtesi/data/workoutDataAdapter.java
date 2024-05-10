package com.example.bigtesi.data;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigtesi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class workoutDataAdapter extends RecyclerView.Adapter<workoutDataAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "workoutDataAdapter";
    private ArrayList<workoutData> mWorkoutData = new ArrayList<>();
    private ArrayList<workoutData> mWorkoutDataAll = new ArrayList<>();
    private Context context;
    private int lastPosition = -1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public workoutDataAdapter(ArrayList<workoutData> mWorkoutData, Context context) {
        this.mWorkoutData = mWorkoutData;
        this.mWorkoutDataAll = mWorkoutData;
        this.context = context;
    }


    @Override
    public Filter getFilter() {
        return workoutFilter;
    }

    private Filter workoutFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<workoutData> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.count = mWorkoutDataAll.size();
                results.values = mWorkoutDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (workoutData item : mWorkoutDataAll) {
                    if (item.getWorkoutDate().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mWorkoutData = (ArrayList<workoutData>) results.values;
            notifyDataSetChanged();
        }
    };

    @NonNull
    @Override
    public workoutDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.fragment_naplo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull workoutDataAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        workoutData currentData = mWorkoutData.get(position);
        Log.d(TAG, "onBindViewHolder: " + currentData);
        holder.bindTo(currentData);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirm(v, position);
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.editButton.getText().equals("Szerkesztés")){
                    holder.setEditableTrue();
                }else{
                    editItem(holder, position);
                    holder.setEditableFalse();
                }
            }
        });

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.naplo_anim);
            holder.itemView.startAnimation(animation);
        }
    }

    public void deleteItem(int position){
        Log.d(TAG, "deleteItem: " + db);
        db.collection("UserWorkouts")
                .document(user.getUid())
                .collection("workouts")
                .document(mWorkoutData.get(position).getSentTime().toString())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Item successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting Item", e);
                    }
                });
    }

    public void deleteConfirm(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Megerősítés");
        builder.setMessage("Biztosan törölni szeretnéd ezt a gyakorlatot?");
        builder.setPositiveButton("Igen",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "DeleteItemOnPositiveClick: yes" );
                        deleteItem(position);
                        mWorkoutData.remove(position);
                        notifyItemRemoved(position);
                    }
                });
        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "DeleteItemOnNegativeClick: no" );
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void editItem(@NonNull workoutDataAdapter.ViewHolder holder, int position){
        db.collection("UserWorkouts")
                .document(user.getUid())
                .collection("workouts")
                .document(mWorkoutData.get(position).getSentTime().toString())
                .update(
                        "workoutName", holder.mWorkoutName.getText().toString(),
                        "workoutReps", Integer.parseInt(holder.mWorkoutReps.getText().toString()),
                        "workoutSets", Integer.parseInt(holder.mWorkoutSets.getText().toString()),
                        "workoutTime", holder.mWorkoutTime.getText().toString(),
                        "weight", holder.mWeight.getText().toString()
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Item successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating Item", e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mWorkoutData.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mWorkoutName;
        private TextView mWeight;
        private TextView mWorkoutSets;
        private TextView mWorkoutReps;
        private TextView mWorkoutTime;
        private Button deleteButton;

        private Button editButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mWorkoutName = itemView.findViewById(R.id.workoutName);
            mWeight = itemView.findViewById(R.id.weight);
            mWorkoutSets = itemView.findViewById(R.id.workoutSets);
            mWorkoutReps = itemView.findViewById(R.id.workoutReps);
            mWorkoutTime = itemView.findViewById(R.id.workoutTime);
            deleteButton = itemView.findViewById(R.id.deleteNaploItem);
            editButton = itemView.findViewById(R.id.editNaploItem);

        }

        public void bindTo(workoutData currentData) {
            mWorkoutName.setText(currentData.getWorkoutName());
            mWeight.setText(currentData.getWeight());
            mWorkoutSets.setText(String.valueOf(currentData.getWorkoutSets()));
            mWorkoutReps.setText(String.valueOf(currentData.getWorkoutReps()));
            mWorkoutTime.setText(currentData.getWorkoutTime());

        }

        public void setEditableTrue(){
            mWorkoutName.setEnabled(true);
            mWeight.setEnabled(true);
            mWorkoutSets.setEnabled(true);
            mWorkoutReps.setEnabled(true);
            mWorkoutTime.setEnabled(true);
            editButton.setText("Küldés");
        }

        public void setEditableFalse(){
            mWorkoutName.setEnabled(false);
            mWeight.setEnabled(false);
            mWorkoutSets.setEnabled(false);
            mWorkoutReps.setEnabled(false);
            mWorkoutTime.setEnabled(false);
            editButton.setText("Szerkesztés");
        }
        
    }
}
