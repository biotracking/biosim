#!/bin/bash
#should be run with 'source config-classpath.sh'
prefix=`pwd`
for x in `ls $prefix/lib/*.jar`
do
        CLASSPATH=$CLASSPATH:$x
done

export CLASSPATH=$CLASSPATH:$prefix/mason:$prefix/src/:$prefix/jni_wrappers/
#export CLASSPATH=$CLASSPATH:/home/hroly/mason:$prefix/src/
