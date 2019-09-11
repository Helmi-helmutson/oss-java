#!/bin/bash
HERE=$( pwd )
sudo /usr/sbin/update-alternatives --set java /usr/lib64/jvm/jre-1.8.0-openjdk/bin/java
gradle --offline clean build
#gradle clean build
tar xf build/distributions/de.openschoolserver.api-1.0-SNAPSHOT.tar
REPO="/data1/OSC/home:varkoly:OSS-4-0/oss-java"
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
cd ${REPO}
osc up
cd $HERE
if [ -e oss-java ]; then
    rm -r oss-java
fi
mv de.openschoolserver.api-1.0-SNAPSHOT  oss-java
chmod 644 oss-java/lib/*
tar cjf ${REPO}/oss-java.tar.bz2 oss-java
cp ${HERE}/../oss-dao/data/school-INSERT.sql.in   ${HERE}/../oss-dao/data/school-INSERT.sql
cp ${HERE}/../oss-dao/data/business-INSERT.sql.in ${HERE}/../oss-dao/data/business-INSERT.sql
cd ${HERE}/src/main/java/de/openschoolserver/api/resources/
./find-rolles.pl >> ${HERE}/../oss-dao/data/school-INSERT.sql
./find-rolles.pl >> ${HERE}/../oss-dao/data/business-INSERT.sql
cd ${HERE}
cd ../oss-dao/
tar cjf ${REPO}/data.tar.bz2 data
cd ${HERE}
CLASSPATH=$( grep "^CLASSPATH=" oss-java/bin/de.openschoolserver.api )
sed "s#@CLASSPATH@#$CLASSPATH#" start-oss-api > ${REPO}/start-oss-api
rm -r oss-java
xterm -e git log --raw  &
RELEASE=$(cat RELEASE)
RELEASE=$((RELEASE+1))
echo $RELEASE > RELEASE
sed s/@RELEASE@/${RELEASE}/ oss-java.spec > ${REPO}/oss-java.spec
git commit -m "New version" RELEASE

cd ${REPO}/
osc vc
osc ci
cd $HERE
