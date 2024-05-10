package com.example.bigtesi.data;

import java.time.LocalDate;
public class workoutData {
    private String sentTime;
    private LocalDate workoutDate;
    private String workoutName;
    private String weight;
    private int workoutSets;
    private int workoutReps;
    private String workoutTime;

    public workoutData() {}

    public workoutData(String workoutName, String sentTime, String weight, int workoutSets, int workoutReps, String workoutTime) {
        this.sentTime = sentTime;
        this.workoutDate = LocalDate.now();
        this.workoutName = workoutName;
        this.weight = weight;
        this.workoutSets = workoutSets;
        this.workoutReps = workoutReps;
        this.workoutTime = workoutTime;
    }

    public workoutData(String workoutDate, String workoutName, String sentTime, String weight, String workoutSets, String workoutReps, String workoutTime) {
        this.workoutDate = LocalDate.parse(workoutDate);
        this.workoutName = workoutName;
        this.sentTime = sentTime;
        this.weight = weight;
        this.workoutSets = Integer.parseInt(workoutSets);
        this.workoutReps = Integer.parseInt(workoutReps);
        this.workoutTime = workoutTime;
    }

    @Override
    public String toString() {
        return "workoutData{" +
                "workoutName='" + workoutName + '\'' +
                ", workoutDate=" + workoutDate +
                ", weight='" + weight + '\'' +
                ", workoutSets=" + workoutSets +
                ", workoutReps=" + workoutReps +
                ", workoutTime='" + workoutTime + '\'' +
                '}';
    }

    public String getWorkoutName() {
        return workoutName;
    }
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }
    public String getWorkoutDate() {
        return workoutDate.toString();
    }
    public void setWorkoutDateLocal(LocalDate date) {
        this.workoutDate = date;

    }
    public void setWorkoutDate(String date){
        this.workoutDate = LocalDate.parse(date);
    }
    public String getSentTime() {
        return sentTime;
    }
    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }
    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public int getWorkoutSets() {
        return workoutSets;
    }
    public void setWorkoutSets(int workoutSets) {
        this.workoutSets = workoutSets;
    }
    public int getWorkoutReps() {
        return workoutReps;
    }
    public void setWorkoutReps(int workoutReps) {
        this.workoutReps = workoutReps;
    }
    public String getWorkoutTime() {
        return workoutTime;
    }
    public void setWorkoutTime(String workoutTime) {
        this.workoutTime = workoutTime;
    }
}
