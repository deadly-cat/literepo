package ingvar.android.literepo.examples.view;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ingvar.android.literepo.builder.UriBuilder;
import ingvar.android.literepo.builder.UriQuery;
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

    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        }
    };

    private static final int PERSONS_LOADER = 0;

    @InjectView(R.id.filter_name)
    private EditText filterName;
    @InjectView(R.id.filter_birthday)
    private EditText filterBirthday;
    @InjectView(R.id.list_persons)
    private RecyclerView viewPersons;

    private PersonsAdapter personsAdapter;
    private PersonsCallback personsCallback;

    public void createPerson(View view) {
        final String tag = CreationFragment.class.getSimpleName();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if(prev != null) {
            transaction.remove(prev);
        }
        transaction.addToBackStack(null);

        new CreationFragment().show(transaction, tag);
    }

    public void clearName(View view) {
        filterName.setText("");
    }

    public void clearDate(View view) {
        filterBirthday.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewPersons.setLayoutManager(new LinearLayoutManager(this));
        viewPersons.setHasFixedSize(true);
        viewPersons.addItemDecoration(new DividerItemDecoration(this));
        viewPersons.setAdapter(personsAdapter = new PersonsAdapter(this));

        filterName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                getLoaderManager().restartLoader(PERSONS_LOADER, null, personsCallback);
            }
        });


        filterBirthday.setInputType(InputType.TYPE_NULL);
        filterBirthday.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                getLoaderManager().restartLoader(PERSONS_LOADER, null, personsCallback);
            }
        });
        filterBirthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    openDatePicker();
                }
            }
        });
        filterBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        personsCallback = new PersonsCallback();
        getLoaderManager().initLoader(PERSONS_LOADER, null, personsCallback);
    }

    private class PersonsCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            String name = filterName.getText().toString();
            String birthday = filterBirthday.getText().toString();

            UriBuilder builder = new UriBuilder()
                    .authority(ExampleContract.AUTHORITY)
                    .table(ExampleContract.Person.TABLE_NAME);
            UriQuery query = builder.query();
            if(name != null && !name.isEmpty()) {
                query.like(ExampleContract.Person.Col.NAME, name);
            }
            if(birthday != null && !birthday.isEmpty()) {
                try {
                    Date date = DATE_FORMAT.get().parse(birthday);
                    query.eq(ExampleContract.Person.Col.BIRTHDAY, date.getTime());
                } catch (ParseException e) {}
            }
            return new CursorLoader(MainActivity.this, builder.build(), ExampleContract.Person.PROJECTION, null, null, ExampleContract.Person.SORT);
        }

        @Override
        public void onLoadFinished(Loader loader, Cursor data) {
            data.setNotificationUri(getContentResolver(), ExampleContract.Person.URI);
            personsAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader loader) {
            personsAdapter.swapCursor(null);
        }

    }

    private void openDatePicker() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(filterBirthday.getWindowToken(), 0);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                filterBirthday.setText(DATE_FORMAT.get().format(calendar.getTime()));
            }
        };

        new DatePickerDialog(this, listener, year, month, day).show();
    }

}
