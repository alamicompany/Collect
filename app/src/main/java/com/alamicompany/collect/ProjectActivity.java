package com.alamicompany.collect;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity implements  DialogProjectFragment.OnProjectNameUpdated, DialogTakeAction.OnProjectActionTake, DeleteDialog.OnDeleteProjectConfirmation{
    private RecyclerView projectRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String userId;
    private DatabaseReference databaseProjects;
    private ArrayList<Project> projects;

    static  ProjectActivity projectActivityInstance;
    private FirebaseAuth firebaseAuth;
    private String action;
    private DialogProjectFragment dialogProject;
    private String selectedProject;
    FragmentManager fm;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        projectActivityInstance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        /*firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();*/
        RecyclerView projectRecycler = (RecyclerView) findViewById(R.id.projectRecycler);
        //get Reference to database projects/user_id
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        action = bundle.getString("action");
        userId = MainActivity.userId;

        databaseProjects = FirebaseDatabase.getInstance().getReference("projects").child(userId);
        //Fill the list
        projects = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        projectRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(ProjectActivity.this,projects);
        projectRecycler.setAdapter(mAdapter);
        //action = "camera";
        // specify an adapter (see also next example)
        fm = getFragmentManager();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddProject("add");
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseProjects.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                projects.clear();
                Project defaultProject = new Project("1","Wallpaper");
                databaseProjects.child("1").setValue(defaultProject);

                for (DataSnapshot projectsnapshot : dataSnapshot.getChildren()){
                    Project project = projectsnapshot.getValue(Project.class);
                    projects.add(project);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static ProjectActivity getActivityinstance (){

        return projectActivityInstance;
    }

        public ArrayList<Project> getProjects(){
            return this.projects;
        }

        public ArrayList<String> getProjectsNames(){
            ArrayList<String> projectsNames = new ArrayList<String>();
            for (Project project: projects) {
                projectsNames.add(project.getName());
            }
            return  projectsNames;
        }


    public void onClickCalled(String project_id) {
        Intent intentPost = new Intent(ProjectActivity.this, PostActivity.class);
        Bundle bundle = new Bundle();
        if(action.equals("camera")){
            bundle.putString("search", "camera");
            bundle.putString("project_id", project_id);
            intentPost.putExtras(bundle);
            startActivity(intentPost);

        }else {

            bundle.putString("search", "project");
            bundle.putString("project_id", project_id);
            intentPost.putExtras(bundle);
            startActivity(intentPost);
        }
    }
    public void showDialogUpdateProject(String id,String action){
        ArrayList<String> projectNames = getProjectsNames();
        dialogProject = new DialogProjectFragment ();
        Bundle args = new Bundle();
        args.putString("action", action);
        args.putString("project_id", id);
        args.putStringArrayList("projectNames",projectNames);
        dialogProject.setArguments(args);
        dialogProject.show(fm, "Alert Dialog fragment");

    }

    public void showDialogAddProject(String action){
        ArrayList<String> projectNames = getProjectsNames();
        dialogProject = new DialogProjectFragment ();
        Bundle args = new Bundle();
        args.putString("action", action);
        args.putStringArrayList("projectNames",projectNames);
        dialogProject.setArguments(args);
        dialogProject.show(fm, "Alert Dialog fragment");
    }
    public void showDialogDeleteProject(String projectId){
        DeleteDialog deleteProjectDialog = new DeleteDialog();
        Bundle args = new Bundle();
        args.putString("action","project");
        args.putString("id", projectId);
        deleteProjectDialog.setArguments(args);
        deleteProjectDialog.show(fm,"Delete Project");
    }
    public void showDialogListAction(String idProject){
        DialogTakeAction dialogAction = new DialogTakeAction();
        Bundle args = new Bundle();
        args.putString("id",idProject);
        args.putString("target", "project");
        dialogAction.setArguments(args);
        dialogAction.show(fm,"Take Action");
    }



    @Override
    public void onProjectNameUpdated(String id, String name) {

            updateProject(id,name);

    }

    @Override
    public void onProjectAdd(String name) {
        createProject(name);
        mAdapter.notifyDataSetChanged();
    }





    public void createProject(String projectName){
        String projectID = databaseProjects.push().getKey();
        Project project = new Project(projectID,projectName);
        databaseProjects.child(projectID).setValue(project);
    }

    public Boolean updateProject(String projectId, String projectName){

       Project project = new Project(projectId, projectName);
        databaseProjects.child(projectId).setValue(project);
        Toast.makeText(this, "Project name updated", Toast.LENGTH_LONG).show();
        return true;
    }

    public void  deleteProjects(String projectId){
    databaseProjects.child(projectId).removeValue();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_update:
                Toast.makeText(this,"You have clicked Call" ,Toast.LENGTH_LONG).show();

                break;
            case R.id.menu_delete:
                Toast.makeText(this,"You have clicked SMS",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onProjectActionDelete(String idProject) {
        showDialogDeleteProject(idProject);
    }

    @Override
    public void onProjectActionUpdate(String idProject) {
        showDialogUpdateProject(idProject,"update");
    }

    @Override
    public void onDeleteProjectConfirm(String idProject) {
        deleteProjects(idProject);
    }
}
