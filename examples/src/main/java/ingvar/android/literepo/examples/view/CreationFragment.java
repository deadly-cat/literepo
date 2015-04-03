package ingvar.android.literepo.examples.view;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
public class CreationFragment extends RoboDialogFragment {

    @InjectView(R.id.creation_name)
    private EditText name;
    @InjectView(R.id.creation_birthday)
    private EditText birthday;
    @InjectView(R.id.creation_create)
    private Button create;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();// TODO: //birthday.getText()
                Person person = new Person(name.getText().toString(), date);

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
    }

}
