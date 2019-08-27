#!/bin/bash


if [ -e "/var/adm/oss/oss-java-update-011" ]
then
echo "oss-java-update-011 already installed"
        exit 0
fi
export HOME="/root/"

echo "DELETE ss1 FROM SoftwareStatus ss1 INNER JOIN SoftwareStatus ss2 WHERE ss1.id < ss2.id AND ss1.softwareversion_id = ss2.softwareversion_id AND ss1.device_id = ss2.device_id;" | mysql OSS
echo "ALTER TABLE SoftwareStatus ADD UNIQUE sv_d (softwareversion_id,device_id);" | mysql OSS
systemctl restart oss-api

touch /var/adm/oss/oss-java-update-011

