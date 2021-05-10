package com.trbj.sty.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.trbj.sty.Adapters.RecycleViewAdapterCourses;
import com.trbj.sty.Adapters.RecycleViewAdapterDataBookTopic;
import com.trbj.sty.Models.Annotations;
import com.trbj.sty.Models.Courses;
import com.trbj.sty.R;
import com.trbj.sty.Shareds.SharedPreferenceSubjectsUser;
import com.trbj.sty.Shareds.SharedPreferenceUserData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataBookTopicActivity extends AppCompatActivity {

    RecyclerView recyclerView_data_book_note;
    RecycleViewAdapterDataBookTopic recycleViewAdapterDataBookTopic;

    FirebaseAuth firebaseAuthB;
    FirebaseFirestore firebaseFirestoreB;
    FirebaseCrashlytics firebaseCrashlyticsB;

    SharedPreferenceUserData sharedPreferenceUserData;
    SharedPreferenceSubjectsUser sharedPreferenceSubjectsUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_book_topic);

        firebaseAuthB = FirebaseAuth.getInstance();
        firebaseFirestoreB = FirebaseFirestore.getInstance();
        firebaseCrashlyticsB= FirebaseCrashlytics.getInstance();

        recyclerView_data_book_note = findViewById(R.id.recycle_view_data_book_topic_note);
        recyclerView_data_book_note.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        sharedPreferenceUserData = new SharedPreferenceUserData(this);
        sharedPreferenceSubjectsUser = new SharedPreferenceSubjectsUser(this);

        loadCourseNote();

    }

    @Override
    protected void onStart() {
        super.onStart();
        recycleViewAdapterDataBookTopic.startListening();
        FirebaseUser firebaseUser = firebaseAuthB.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.reload();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        recycleViewAdapterDataBookTopic.startListening();
    }

    private String emailUser(){
        String emailUser=sharedPreferenceUserData.getEmailUser();
        return emailUser;
    }

    private String loadNameTopic(){
        String nameSubject=sharedPreferenceSubjectsUser.getNameTopic();
        return nameSubject;
    }

    private String date() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = simpleDateFormat.format(new Date());
        return currentDateandTime;
    }

    private String loadIdSubject(){
        String idSubject = sharedPreferenceSubjectsUser.getSubjectsUser();
        return idSubject;
    }

    private void loadCourseNote(){
        try{

            String[] idUser = emailUser().split("\\@");

            if (idUser[1].equals("estudiantesunibague.edu.co") || idUser[1].equals("unibague.edu.co")) {
                Query query = firebaseFirestoreB.collection("Unibague").document(emailUser()).collection("Course").document(loadIdSubject()).collection(loadNameTopic());
                FirestoreRecyclerOptions<Annotations> fireStoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Annotations>().setQuery(query,Annotations.class).build();
                recycleViewAdapterDataBookTopic = new RecycleViewAdapterDataBookTopic(fireStoreRecyclerOptions);
                recyclerView_data_book_note.setAdapter(recycleViewAdapterDataBookTopic);
            } else {
                Query query = firebaseFirestoreB.collection("Usuario").document(emailUser()).collection("Course").document(loadIdSubject()).collection(loadNameTopic());
                FirestoreRecyclerOptions<Annotations> fireStoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Annotations>().setQuery(query,Annotations.class).build();
                recycleViewAdapterDataBookTopic = new RecycleViewAdapterDataBookTopic(fireStoreRecyclerOptions);
                recyclerView_data_book_note.setAdapter(recycleViewAdapterDataBookTopic);
            }

        }catch (Exception e){
            Log.w("TAG", "loadDataSubjects in failed", e);
            firebaseCrashlyticsB.log("loadDataSubjects");
            firebaseCrashlyticsB.recordException(e);

        }
    }
}