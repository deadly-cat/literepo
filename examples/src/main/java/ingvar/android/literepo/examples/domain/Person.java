package ingvar.android.literepo.examples.domain;

import java.util.Date;

import ingvar.android.literepo.conversion.annotation.Column;
import ingvar.android.literepo.conversion.annotation.Type;
import ingvar.android.literepo.examples.storage.ExampleContract;

/**
 * Created by Igor Zubenko on 2015.03.27.
 */
public class Person {

    @Column(value = ExampleContract.Person.Col.NAME, type = Type.TEXT, nullable = false)
    private String name;
    @Column(value = ExampleContract.Person.Col.BIRTHDAY, type = Type.DATE, nullable = false)
    private Date birthday;

    public Person() {}

    public Person(String name, Date birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
