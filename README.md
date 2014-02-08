Rejistry
========

Pure Java parser for Windows Registry hive files.

Usage
-----
For the time being, see `src/main/java/com/williballenthin/rejistry/Rejistry.java` and compare it with `./test.py`. But, as a user, you'll probably want to use `RegistryHiveFile` with its `getRoot()` method, and then interact with the `RegistryValue` and `RegistryKey` classes.

Building
--------

  - Install [Apache Maven](http://maven.apache.org/)
  - `$mvn package`
  - This creates `./target/Rejistry-1.0-SNAPSHOT.jar`

Dependencies
------------
  - none, this is a pure Java parser

TODO
----
  - Make exceptions more specific
  - Parse security information, including the SKRecord structure
  - Parse out the raw binary of the remaining value types:
     - REG_LINK
     - REG_RESOURCE_LIST
     - REG_FULL_RESOURCE_DESCRIPTOR
     - REG_RESOURCE_REQUIREMENTS_LIST
