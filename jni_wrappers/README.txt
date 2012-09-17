This subdirectory of biosim contains swig[1] generated JNI[2]
code. The makefiles have only been tested on a linux machine
running Ubuntu. If you are running biosim on another platform,
and are familiar with the details of compiling C++ libraries
for use in java programs with JNI and/or swig, you should be
able to compile these libraries by hand. If you're running
biosim on an linux Ubuntu machine, you should be able to use
the provided makefiles by first running make in the C++ 
library folder, and then in the folder containing the java
wrappers (core/util/annwrapper/ann/, and core/util/annwrapper
respectively for the library used by core.util.FastKNN). These
features are experimental at the moment, so YMMV.

[1] http://www.swig.org/
[2] http://en.wikipedia.org/wiki/Java_Native_Interface
