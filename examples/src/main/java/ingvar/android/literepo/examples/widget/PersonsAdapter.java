package ingvar.android.literepo.examples.widget;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ingvar.android.literepo.examples.R;
import ingvar.android.literepo.examples.domain.Person;
import ingvar.android.literepo.examples.storage.Conversion;

/**
 * Created by Igor Zubenko on 2015.03.31.
 */
public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.Holder> {

    private SimpleDateFormat format;
    private WeakReference<Context> contextRef;
    private Cursor cursor;

    public PersonsAdapter(Context context) {
        contextRef = new WeakReference<>(context);
        format = new SimpleDateFormat("d MMMM yyyy");
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_person, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if(!cursor.moveToPosition(position)) {
            throw new IllegalArgumentException("Cursor index of bounds exception!");
        }
        Person person = Conversion.getConverter(Person.class).convert(cursor);

        holder.name.setText(person.getName());
        holder.birthday.setText(format.format(person.getBirthday()));
        //can't use java 8 java.time.
        //So try to use Calendar.
        Calendar date = Calendar.getInstance();
        date.setTime(person.getBirthday());
        int by = date.get(Calendar.YEAR);
        int bm = date.get(Calendar.MONTH);
        date.setTime(new Date(System.currentTimeMillis()));
        int cy = date.get(Calendar.YEAR);
        int cm = date.get(Calendar.MONTH);
        int age = ((cy * 12 + cm) - (by * 12 + bm)) / 12;

        holder.age.setText(Integer.toString(age));
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    protected Context getContext() {
        Context context = contextRef.get();
        if(context == null) {
            throw new IllegalStateException("Context is stale!");
        }
        return context;
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView birthday;
        private TextView age;

        public Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.person_name);
            birthday = (TextView) itemView.findViewById(R.id.person_birthday);
            age = (TextView) itemView.findViewById(R.id.person_age);
        }

    }

}
