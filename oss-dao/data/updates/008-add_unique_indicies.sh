#!/bin/bash

if [ -e "/var/adm/oss/oss-java-update-008" ]
then
	echo "oss-java-update-008 already installed"
        exit 0
fi

export HOME="/root"
echo "CREATE UNIQUE INDEX users_uid on Users(uid);" | mysql OSS
echo "CREATE UNIQUE INDEX groups_name on Groups(name);" | mysql OSS
echo "CREATE UNIQUE INDEX rooms_name on Rooms(name);" | mysql OSS
echo "CREATE UNIQUE INDEX devices_name on Devices(name);" | mysql OSS

touch /var/adm/oss/oss-java-update-008

