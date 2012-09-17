all: libannwrapper.dylib ann

#libannwrapper.so: simple_ann.o annwrapper_wrap.o  ann/src/*.o
#	g++ -shared simple_ann.o annwrapper_wrap.o ann/src/*.o -o libannwrapper.so
libannwrapper.dylib: simple_ann.o annwrapper_wrap.o ann/lib/libANN.a
	g++ -shared simple_ann.o annwrapper_wrap.o ann/lib/libANN.a -o libannwrapper.dylib
simple_ann.o: simple_ann.h simple_ann.cpp 
	g++ -c simple_ann.cpp -Iann/include/
annwrapper_wrap.o: annwrapper_wrap.cxx
	g++ -c annwrapper_wrap.cxx -Iann/include/ -I/System/Library/Frameworks/JavaVM.framework/Headers
annwrapper_wrap.cxx: annwrapper.i
	swig -c++ -java -package core.util.annwrapper annwrapper.i
clean:
	rm *.o annwrapper_wrap.cxx annwrapper.java annwrapperJNI.java SimpleANN.java 
