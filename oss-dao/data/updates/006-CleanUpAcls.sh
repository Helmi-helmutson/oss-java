#!/bin/bash
export HOME="/root"

if [ -e "/var/adm/oss/oss-java-update-006" ]
then
echo "oss-java-update-006 already installed"
        exit 0
fi

echo "DELETE FROM Acls WHERE group_id = 2 AND acl LIKE '%.search'" | mysql OSS 
echo "INSERT INTO Acls VALUES(NULL,NULL,2,'myself.search','Y',6)"  | mysql OSS

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
	systemctl restart cephalix-api
else
	systemctl restart oss-api
fi

touch /var/adm/oss/oss-java-update-006
