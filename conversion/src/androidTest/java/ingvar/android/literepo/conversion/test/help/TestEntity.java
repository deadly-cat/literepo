package ingvar.android.literepo.conversion.test.help;

import java.util.Date;

import ingvar.android.literepo.conversion.annotation.Column;
import ingvar.android.literepo.conversion.annotation.Type;

/**
 * Created by Igor Zubenko on 2015.04.03.
 */
public class TestEntity {

    @Column(type = Type.INTEGER)
    public Integer integer;
    @Column(type = Type.REAL)
    public Float real;
    @Column(value = Contract.STRING, nullable = false)
    public String string;
    @Column(type = Type.BLOB)
    public byte[] blob;
    @Column(nullable = false, type = Type.DATE)
    public Date date;

    public TestEntity() {}

}
