#!/bin/sh

HOST=`/sbin/ifconfig eth0 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}'`

# Check if this docker container is running an AWS instance
NODE_HOST=$(curl -s -m 2 169.254.169.254/latest/meta-data/local-ipv4)
if [ -z ${NODE_HOST} ]; then
    echo "Local host not found"
else
    echo "Got this instances host: $NODE_HOST"
    export AKKA_HOST=$NODE_HOST
fi

TCP_PROPERTY="-Dakka.remote.netty.tcp"

# Configure AKKA to run in a docker container
JAVA_OPTS="$JAVA_OPTS $TCP_PROPERTY.bind-hostname=$HOST"
JAVA_OPTS="$JAVA_OPTS $TCP_PROPERTY.bind-port=2551"

echo "JAVA_OPTS: $JAVA_OPTS"

#         _,-._
#        ; ___ :
#    ,--' (. .) '--.__    ,-------------------------------------------.
#  _;      |||        \   |                                           |
# '._,-----''';=.____,"   |   $1 is the jarrr, $2 arrre the arrrrgs   |
#   /// < o>   |##|       |                                           |
#   (o        \`--'       //`-----------------------------------------'
#  ///\ >>>>  _\ <<<<    //
# --._>>>>>>>><<<<<<<<  /
# ___() >>>[||||]<<<<
# `--'>>>>>>>><<<<<<<
#      >>>>>>><<<<<<
#        >>>>><<<<<
#         >>ctr<<

exec java ${JAVA_OPTS} -jar $1 $2
