package ingvar.android.literepo.examples.view;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import ingvar.android.literepo.examples.R;
import ingvar.android.literepo.examples.domain.Person;
import ingvar.android.literepo.examples.storage.Conversion;
import ingvar.android.literepo.examples.storage.ExampleContract;
import roboguice.fragment.provided.RoboDialogFragment;
import roboguice.inject.InjectView;

/**
 * Created by Igor Zubenko on 2015.03.27.
 */
public class CreationFragment extends RoboDialogFragment implements DatePickerDialog.OnDateSetListener {

    @InjectView(R.id.creation_name)
    private EditText name;
    @InjectView(R.id.creation_birthday)
    private EditText birthday;
    @InjectView(R.id.creation_create)
    private Button create;

    private Date mBirthday;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBirthday = new Date();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = new Person(name.getText().toString(), mBirthday);

                try {
                    ContentResolver resolver = getActivity().getContentResolver();
                    resolver.insert(ExampleContract.Person.URI,
                                    Conversion.getConverter(Person.class).convert(person));
                    resolver.notifyChange(ExampleContract.Person.URI, null);

                    dismiss();
                } catch (SQLiteConstraintException e) {
                    Toast.makeText(getActivity(), getString(R.string.exception_name_already_used), Toast.LENGTH_LONG).show();
                }
            }
        });

        birthday.setInputType(InputType.TYPE_NULL);
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    openDatePicker();
                }
            }
        });
    }

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
        mBirthday = calendar.getTime();

        birthday.setText(MainActivity.DATE_FORMAT.get().format(mBirthday));
    }

    private void openDatePicker() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(birthday.getWindowToken(), 0);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getActivity(), CreationFragment.this, year, month, day).show();
    }

}
