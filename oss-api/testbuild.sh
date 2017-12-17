#!/bin/bash
HERE=$( pwd )
gradle clean build
tar xf build/distributions/de.openschoolserver.api-1.0-SNAPSHOT.tar 
scp  de.openschoolserver.api-1.0-SNAPSHOT/lib/de.openschoolserver.* root@192.168.100.20:/opt/oss-java/lib/
ssh  root@192.168.100.20 systemctl restart oss-api
#scp -P 34022 de.openschoolserver.api-1.0-SNAPSHOT/lib/de.openschoolserver.* root@oss40.cephalix.eu:/opt/oss-java/lib/
#ssh -p 34022 root@oss40.cephalix.eu systemctl restart oss-api
if [ -e oss-java ]; then
    rm -r oss-java
fi
mv de.openschoolserver.api-1.0-SNAPSHOT  oss-java
tar cjf /data1/OSC/home:varkoly:OSS-4-0/oss-java/oss-java.tar.bz2 oss-java
cp ../oss-dao/data/oss-objects.sql /data1/OSC/home:varkoly:OSS-4-0/oss-java/
cp ../oss-dao/data/school-INSERT.sql /data1/OSC/home:varkoly:OSS-4-0/oss-java/
CLASSPATH=$( grep "^CLASSPATH=" oss-java/bin/de.openschoolserver.api )
sed "s#@CLASSPATH@#$CLASSPATH#" start-oss-api > /data1/OSC/home:varkoly:OSS-4-0/oss-java/start-oss-api
rm -r oss-java
