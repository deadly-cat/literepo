# literepo
A small library simplifies working with sqlite in android.

[![Build Status](https://travis-ci.org/deadly-cat/literepo.svg?branch=master)](https://travis-ci.org/deadly-cat/literepo)
[![License](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

Core library:

[![Download](https://api.bintray.com/packages/deadly-cat/maven/literepo/images/download.svg) ](https://bintray.com/deadly-cat/maven/literepo/_latestVersion)

Conversion library:

[![Download](https://api.bintray.com/packages/deadly-cat/maven/literepo-conversion/images/download.svg) ](https://bintray.com/deadly-cat/maven/literepo-conversion/_latestVersion)

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
    compile 'ingvar.android.literepo:literepo:2.0.0'
    //for conversion library (contains converters and annotations)
    compile 'ingvar.android.literepo:literepo-conversion:2.0.0'
}
```

For creating request to DB you need to use [**UriBuilder**](https://github.com/deadly-cat/literepo/blob/master/literepo/src/main/java/ingvar/android/literepo/builder/UriBuilder.java).
```java
String[] projection = "col1, col2";

Uri request = new UriBuilder()
    .authority("example.provider")
    .table("example_table")
    .eq("col1", "42")
    .or().gt("col1", "84")
    .in("col2", Arrays.asList(1, 2, 3, 4))
    .build();
    
Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
```

This request equivalent for sql query:
```sql
select col1, col2
from example_table
where (col1 = 42 or col1 > 84) and col2 in (1, 2, 3, 4)
```
Don't try to use Uri for large lists. Instead use selection and selectionArgs arguments of query method.

**Note**: In this release builder cannot build joins, but in next releases this functionality can be added.


Also you need to extend [**LiteProvider.java**](https://github.com/deadly-cat/literepo/blob/master/literepo/src/main/java/ingvar/android/literepo/LiteProvider.java) and override method **provideOpenHelper()**:
```java
public class ExampleProvider extends LiteProvider {
    
    @Override
    protected SQLiteOpenHelper provideOpenHelper() {
        return new ExampleHelper(getContext());
    }
    
}
```


More examples for builder you can find in the [**BuildersTest.java**](https://github.com/deadly-cat/literepo/blob/master/literepo/src/androidTest/java/ingvar/android/literepo/test/BuildersTest.java)


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
