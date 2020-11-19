A bunch of utilities for working with geographic data and files. This started out
when I wanted to clean and analyze GPS tracks from my various devices. It moved
into its own project when friends became interested. You're welcome to use them,
and I believe all of the calculations are correct, but please don't complain if
my function says that you ran 1.23 miles and your watch says 1.26.

There are two sub-projects:

* `lib` for reusable library classes.
* `util` for specific utility programs.

The package structure starts with `com.kdgregory.geoutil`, followed by `lib` or
`util`. The `lib` package has several sub-packages:

* `core` contains core geographic functionality such as distance calculations and
  tools for managing lists of geographic points.
* `gpx` is an (incomplete) data model for the GPX file format.
* `kml` is an (incomplete) data model for the KML file format.

To build, run `mvn package` from the top-level directory (or `mvn install` if you
plan to use the library in your own code).

The utilities are packaged as a single "uber-JAR"; to run them, use the following
command (replacing `CLASSNAME` with the fully-qualified classname of the utility
that you want to run):

```
java -cp util/target/geoutil-*.jar CLASSNAME
```

All of the utilities will exit with a usage message if you invoke them without
command-line parameters. **BEWARE** that most of the utilities overwrite the
original file; make a backup if you don't want this to happen.
