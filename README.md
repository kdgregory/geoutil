A bunch of utilities for working with geographic data and files. This started out
when I wanted to clean and analyze GPS tracks from my various devices. It moved
into its own project when friends became interested. You're welcome to use them,
and I believe all of the calculations are correct, but please don't complain if
my function says that you ran 1.23 miles and your watch says 1.26.

Everything in this library lives under the package `com.kdgregory.geoutil`. Under
that you'll find two packages:

* `lib` for reusable library classes.
* `util` for specific utility programs.

Each of those packages is further divided by the type of data that it deals with.

The project is built using [Maven](http://maven.apache.org/), and is packaged as
an "uber-JAR", containing all dependencies This means that you can run any of the
utilities with a command like this:

```
java -cp target/geoutil-*.jar CLASSNAME
```
