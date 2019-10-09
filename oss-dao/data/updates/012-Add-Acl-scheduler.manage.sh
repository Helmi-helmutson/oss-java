#!/bin/bash
export HOME="/root/"
echo "UPDATE Acls set Acl='room.dhcp' WHERE Acl='room.dhcpd'" | mysql OSS
SCHEDULER=$( echo "select id from Enumerates where name='apiAcl' AND value='scheduler.manage';" | mysql OSS )
if [ "$SCHEDULER" ]; then
        echo "scheduler.manage is already installed."
        exit 0
fi
echo "INSERT INTO Enumerates values(NULL,'apiAcl','scheduler.manage',6);" | mysql OSS
echo "INSERT INTO Acls values(NULL,NULL,1,'scheduler.manage','Y',6);"     | mysql OSS

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
        systemctl restart cephalix-api
else
        systemctl restart oss-api
fi

