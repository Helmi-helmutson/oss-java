#!/bin/bash

if [ -e "/var/adm/oss/oss-java-update-010" ]
then
	echo "oss-java-update-010 already installed"
        exit 0
fi

export HOME="/root"
echo "ALTER TABLE Devices CHANGE COLUMN `row` roomRow  INTEGER DEFAULT 5;" | mysql OSS
echo "ALTER TABLE Rooms   CHANGE COLUMN `row` roomRows INTEGER DEFAULT 5;" | mysql OSS

touch /var/adm/oss/oss-java-update-010

