#!/bin/bash

SCHEDULER=$( echo "select id from Enumerates where name='apiAcl' AND value='system.addons';" | mysql OSS )
if [ "$SCHEDULER" ]; then
        echo "system.addons is already installed."
        exit 0
fi
echo "INSERT INTO Enumerates values(NULL,'apiAcl','system.addons',6);" | mysql OSS
echo "INSERT INTO Acls values(NULL,NULL,1,'system.addons','Y',6);"     | mysql OSS

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
        systemctl restart cephalix-api
else
        systemctl restart oss-api
fi

