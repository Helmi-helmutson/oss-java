#!/bin/bash
HERE=$( pwd )
gradle clean build
tar xf build/distributions/de.openschoolserver.api-1.0-SNAPSHOT.tar 
scp -P 34022 de.openschoolserver.api-1.0-SNAPSHOT/lib/de.openschoolserver.* root@oss40.cephalix.eu:/opt/oss-java/lib/
ssh -p 34022 root@oss40.cephalix.eu systemctl restart oss-api
rm -rf de.openschoolserver.api-1.0-SNAPSHOT/ 
