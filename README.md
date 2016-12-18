# class-checksum
Simple Java class checksum generator.

# Introduction
I make it to detect the change of class structure during app launching time. Although there are many bean comparator or hashing code, I can't find class structure checksum generation code. All method of this class is static because it is executed just one time at static initialization time.

# Feature

* Package scope restriction
* Support all java digest function (Default is MD5)

# History

## version 0.3

* block java primitive types and java.lang types
* fix multi type variable case

## version 0.2

* refactoring to class-base implementation
* don't digest the classes of 'java.\*' packages

## version 0.1

* initial static utility implementation


