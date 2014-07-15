/**
 * 
 */
package es.rffsystems;

import es.rffsystems.sremember.R;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;



/**
 * @author ruben
 *
 */
public class SRemember extends ListActivity {
	public static final int INSERT_ID = Menu.FIRST;
	
	// Used to create numbered note titles.
	private int mNoteNumber = 1;
	private NotesDbAdapter mDbHelper;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case INSERT_ID:
            createNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void createNote() {
        String noteName = "Note " + mNoteNumber++;
        mDbHelper.createNote(noteName, "");
        fillData();
    }
    
    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllNotes();
        startManagingCursor(c);

        String[] from = new String[] { NotesDbAdapter.KEY_TITLE };
        int[] to = new int[] { R.id.text1 };
        
        // Now create an array adapter and set it to display using our row.
        
        /* takes a database Cursor and binds it to fields provided in the layout. 
         * These fields define the row elements of our list (in this case we use 
         * the text1 field in our notes_row.xml layout), so this allows us to easily
         *  populate the list with entries from our database.
         */
        
        /*
         * To do this we have to provide a mapping from the title field in the returned 
         * Cursor, to our text1 TextView, which is done by defining two arrays: 
         * the first a string array with the list of columns to map from (just "title" 
         * in this case, from the constant NotesDbAdapter.KEY_TITLE) and, the second, 
         * an int array containing references to the views that we'll bind the data 
         * into (the R.id.text1 TextView).
         */
        SimpleCursorAdapter notes =
            new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);
        setListAdapter(notes);
    }
   
}
