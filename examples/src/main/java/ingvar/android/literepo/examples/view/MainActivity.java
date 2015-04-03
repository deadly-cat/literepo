package ingvar.android.literepo.examples.view;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SearchView;

import ingvar.android.literepo.builder.UriBuilder;
import ingvar.android.literepo.examples.R;
import ingvar.android.literepo.examples.storage.ExampleContract;
import ingvar.android.literepo.examples.widget.DividerItemDecoration;
import ingvar.android.literepo.examples.widget.PersonsAdapter;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by Igor Zubenko on 2015.03.26.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity {

    private static final int PERSONS_LOADER = 0;

    @InjectView(R.id.filter_name)
    private SearchView filterName;
    @InjectView(R.id.filter_birthday)
    private EditText filterBirthday;
    @InjectView(R.id.list_persons)
    private RecyclerView viewPersons;

    private PersonsAdapter personsAdapter;
    private PersonsCallback personsCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewPersons = (RecyclerView) findViewById(R.id.list_persons);
        viewPersons.setLayoutManager(new LinearLayoutManager(this));
        viewPersons.setHasFixedSize(true);
        viewPersons.addItemDecoration(new DividerItemDecoration(this));
        viewPersons.setAdapter(personsAdapter = new PersonsAdapter(this));

        filterName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override public boolean onQueryTextChange(String newText) {
                getLoaderManager().restartLoader(PERSONS_LOADER, null, personsCallback);
                return false;
            }
        });

        filterBirthday.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                getLoaderManager().restartLoader(PERSONS_LOADER, null, personsCallback);
            }
        });

        personsCallback = new PersonsCallback();
        getLoaderManager().initLoader(PERSONS_LOADER, null, personsCallback);
    }

    private class PersonsCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            String name = filterName.getQuery().toString();
            String birthday = filterBirthday.getText().toString();

            UriBuilder builder = new UriBuilder()
                    .authority(ExampleContract.AUTHORITY)
                    .table(ExampleContract.Person.TABLE_NAME);
            if(name != null && !name.isEmpty()) {
                builder.like(ExampleContract.Person.Col.NAME, name);
            }
            if(birthday != null && !birthday.isEmpty()) {
                //TODO: add birthday filter
            }
            //TODO: add age filter
            return new CursorLoader(MainActivity.this, builder.build(), ExampleContract.Person.PROJECTION, null, null, ExampleContract.Person.SORT);
        }

        @Override
        public void onLoadFinished(Loader loader, Cursor data) {
            personsAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            personsAdapter.swapCursor(null);
        }

    }


}
