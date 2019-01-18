package com.get2abdul101.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_POSITION = "com.get2abdul101.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.get2abdul101.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.get2abdul101.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.get2abdul101.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private Boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private int newPosition;
    private boolean mIsCancelling;
    private String originalNoteCourseId;
    private String originalNoteTitle;
    private String originalNoteText;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);
    outState.putString(ORIGINAL_NOTE_COURSE_ID, originalNoteCourseId);
    outState.putString(ORIGINAL_NOTE_TITLE, originalNoteTitle);
    outState.putString(ORIGINAL_NOTE_TEXT, originalNoteText);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        spinnerCourses =  findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValue();

        if(savedInstanceState == null)
        {
        saveOriginalNoteValue();}
        else
            restoreOriginalNoteValue(savedInstanceState);

        textNoteTitle = findViewById(R.id.text_note_title);
        textNoteText = findViewById(R.id.text_note_body);

        if (!isNewNote)
            displayNote(spinnerCourses, textNoteTitle, textNoteText);
        Log.d(TAG, "onCreate: ");
    }

    private void restoreOriginalNoteValue(Bundle savedInstanceState) {
        originalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        originalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        originalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }


    private void saveOriginalNoteValue() {
        if(isNewNote)
            return;
        originalNoteCourseId = mNote.getCourse().getCourseId();
        originalNoteTitle = mNote.getTitle();
        originalNoteText = mNote.getText();
        Log.d(TAG, "saveOriginalNoteValue: ");
    }


    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> course = DataManager.getInstance().getCourses();
        int courseIndex = course.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            if (isNewNote) {
                DataManager.getInstance().removeNote(newPosition);
            }
            else
                storePreviousNoteValue();
        }
        else
        saveNote();

        Log.d(TAG, "onPause");
    }

    private void storePreviousNoteValue() {
        CourseInfo course = DataManager.getInstance().getCourse(originalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(originalNoteTitle);
        mNote.setText(originalNoteText);


    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        mNote.setTitle(textNoteTitle.getText().toString());
        mNote.setText(textNoteText.getText().toString());

    }

    private void readDisplayStateValue() {
        Intent intent = getIntent();
         newPosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        isNewNote = newPosition == POSITION_NOT_SET;

        if (isNewNote) {
            createNewNote();

        }
            mNote = DataManager.getInstance().getNotes().get(newPosition);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        newPosition = dm.createNewNote();
      //  mNote = dm.getNotes().get(newPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {

            sendMail();
            return true;

            }
        else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMail() {
        CourseInfo course = (CourseInfo) spinnerCourses.getSelectedItem();

        String subject = textNoteTitle.getText().toString();
        String body = "Check out what i learnt\" "
                + course.getTitle() + "\n" + textNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(intent);


    }
}
