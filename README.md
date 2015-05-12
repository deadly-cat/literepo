# literepo
A small library simplifies working with sqlite in android.

[![License](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)
[![Build Status](https://travis-ci.org/deadly-cat/literepo.svg?branch=master)](https://travis-ci.org/deadly-cat/literepo)
[![Download](https://api.bintray.com/packages/deadly-cat/maven/literepo/images/download.svg) ](https://bintray.com/deadly-cat/maven/literepo/_latestVersion)

###Usage

####Core (builders and content provider)

Add url to Bintray repository in your build.gradle
```groovy
allprojects {
    repositories {
        //... other urls
        maven { url  'http://dl.bintray.com/deadly-cat/maven' }
    }
}
```

Next add dependency for your module
```groovy
dependencies {
    //for core library (contains builders and provider)
    compile 'ingvar.android.literepo:literepo:2.3.2'
    //for conversion library (contains converters and annotations)
    compile 'ingvar.android.literepo:literepo-conversion:2.3.2'
}
```

For creating request to DB you need to use [**UriBuilder**](https://github.com/deadly-cat/literepo/blob/master/literepo/src/main/java/ingvar/android/literepo/builder/UriBuilder.java).
```java
String[] projection = "col1, col2";

Uri request = new UriBuilder()
    .authority("example.provider")
    .table("example_table")
    .query()
        .eq("col1", "42")
        .or().gt("col1", "84")
        .in("col2", Arrays.asList(1, 2, 3, 4))
    .end()
    .build();
    
Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
```

This request equivalent for sql query:
```sql
select col1, col2
from example_table
where (col1 = 42 or col1 > 84) and col2 in (1, 2, 3, 4)
```

Joined queries is same easy:
```java
String[] projection = "col1, col2";

Uri request = new UriBuilder()
    .authority("example.provider")
    .table("example_parent", "p")
    .where()
        .eq("col1", "42")
        .or().gt("col1", "84")
    .end()
    .join("example_child", "c")
        .eq("parent_id", new UriQuery.SqlValue("p._id"))
    .end()
    .build();
    
Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
```

UriQuery.SqlValue used to tell builder what this value is a plain sql. Also you can use UriQuery.raw(String sql, Object... args) method for plain sql queries.

And in the sql it look like this:
```sql
select *
from example_parent as p
join example_child as c on (c.parent_id = p._id)
where (col1 = 42 or col1 > 84)
```

Also you need to extend [**LiteProvider.java**](https://github.com/deadly-cat/literepo/blob/master/literepo/src/main/java/ingvar/android/literepo/LiteProvider.java) and override method **provideOpenHelper()**:
```java
public class ExampleProvider extends LiteProvider {
    
    @Override
    protected SQLiteOpenHelper provideOpenHelper() {
        return new ExampleHelper(getContext());
    }
    
}
```


More examples for builder you can find in the [**unit tests**](https://github.com/deadly-cat/literepo/blob/master/literepo/src/androidTest/java/ingvar/android/literepo/test/)


####Conversion (annotations and converters)

For using conversion library you need to annotate your Entity fields as [@Column](https://github.com/deadly-cat/literepo/blob/master/literepo-conversion/src/main/java/ingvar/android/literepo/conversion/annotation/Column.java).
```java
public class Entity {
    @Column(value = "col_name", type = Type.TEXT, nullable = false)
    private String name;
}
```


Next create converter:
```java
Converter<Entity> converter = ConverterFactory.create(Entity.class);

ContentValues values = converter.convert(entityInstance);

Entity entity = converter.convert(cursor);
```
**Note**: For now converter not so fast because under the hood it use reflection. In the next release it will be replaced for code generation.


Examples for conversion you can find in [**ConverterTest.java**](https://github.com/deadly-cat/literepo/blob/master/literepo-conversion/src/androidTest/java/ingvar/android/literepo/conversion/test/ConverterTest.java)


Example app in the [**example module**](https://github.com/deadly-cat/literepo/tree/master/examples).
