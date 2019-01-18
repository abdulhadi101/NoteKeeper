package com.get2abdul101.notekeeper;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    @Test
    public void createNewNote() throws Exception{

        DataManager dm = DataManager.getInstance();
        final CourseInfo course = dm.getCourse("android_async");
        final String noteTitle = "Test note Title";
        final String noteText = "Test text body";

        int noteIndex = dm.createNewNote();
        NoteInfo newNote = dm.getNotes().get(noteIndex);
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);

        NoteInfo compareNotes = dm.getNotes().get(noteIndex);
        assertEquals(course, compareNotes.getCourse());
        assertEquals(noteTitle, compareNotes.getTitle());
        assertEquals(noteText, compareNotes.getText());

    }
}