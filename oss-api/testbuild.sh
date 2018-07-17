#!/bin/bash
HERE=$( pwd )
#gradle --offline clean build
gradle clean build
tar xf build/distributions/de.openschoolserver.api-1.0-SNAPSHOT.tar 

if [ "$1" ]; then
        PORT=22
        if [ "$2" ]; then
           PORT=$2
        fi
	scp -P $PORT de.openschoolserver.api-1.0-SNAPSHOT/lib/de.openschoolserver.* root@$1:/opt/oss-java/lib/
	ssh -p $PORT root@$1 systemctl restart oss-api
fi
echo  -n "Do you want to check in (y/n)?"
read Y
if [ "$Y" != "y" ]; then
	exit
fi
cd /data1/OSC/home:varkoly:OSS-4-0/oss-java/
osc up
cd $HERE
if [ -e oss-java ]; then
    rm -r oss-java
fi
mv de.openschoolserver.api-1.0-SNAPSHOT  oss-java
chmod 644 oss-java/lib/*
tar cjf /data1/OSC/home:varkoly:OSS-4-0/oss-java/oss-java.tar.bz2 oss-java
cp ../oss-dao/data/oss-objects.sql /data1/OSC/home:varkoly:OSS-4-0/oss-java/
cp ../oss-dao/data/school-INSERT.sql /data1/OSC/home:varkoly:OSS-4-0/oss-java/
cp ../oss-dao/data/business-INSERT.sql /data1/OSC/home:varkoly:OSS-4-0/oss-java/
cd src/main/java/de/openschoolserver/api/resources/
./find-rolles.pl >> /data1/OSC/home:varkoly:OSS-4-0/oss-java/school-INSERT.sql
./find-rolles.pl >> /data1/OSC/home:varkoly:OSS-4-0/oss-java/business-INSERT.sql
cd ${HERE}
CLASSPATH=$( grep "^CLASSPATH=" oss-java/bin/de.openschoolserver.api )
sed "s#@CLASSPATH@#$CLASSPATH#" start-oss-api > /data1/OSC/home:varkoly:OSS-4-0/oss-java/start-oss-api
cp start-oss-squid-sso /data1/OSC/home:varkoly:OSS-4-0/oss-java/start-oss-squid-sso
#rm -r oss-java
xterm -e git log --raw  &
cd /data1/OSC/home:varkoly:OSS-4-0/oss-java/
osc vc
osc ci
cd $HERE
