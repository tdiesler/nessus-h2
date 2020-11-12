#!/bin/sh

PRG="$0"

# Get absolute path of the HOMEDIR
HOMEDIR=`dirname $PRG`/..
HOMEDIR=`cd $HOMEDIR; pwd`

if [ -z $JAVA_OPTS ]; then
    JAVA_OPTS="-server"
fi

if [ ${JMX_REMOTE:=false} = "true" ]; then

    JAVA_OPTS="$JAVA_OPTS -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"
    JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote"
    
    if [ -n ${JMX_REMOTE_HOST:=localhost} ]; then
        JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=$JMX_REMOTE_HOST"
    fi

    if [ -n ${JMX_REMOTE_PORT:=7091} ]; then
        JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=$JMX_REMOTE_PORT"
        JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.rmi.port=$JMX_REMOTE_PORT"
    fi

    if [ ${JMX_REMOTE_SECURITY:=true} = "false" ]; then
        JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
        JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
    fi
fi

echo "JAVA_OPTS=$JAVA_OPTS"

cd $HOMEDIR
java $JAVA_OPTS -Dlog4j.configuration=file://$HOMEDIR/config/h2.log4j.properties \
     -jar $HOMEDIR/lib/@project.artifactId@-@project.version@.jar "$@" 
