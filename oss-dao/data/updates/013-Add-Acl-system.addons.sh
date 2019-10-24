#!/bin/bash
export HOME="/root/"
SYSTEMADDON=$( echo "select id from Enumerates where name='apiAcl' AND value='system.addons';" | mysql OSS )
if [ -z "$SYSTEMADDON" ]; then
       echo "INSERT INTO Enumerates values(NULL,'apiAcl','system.addons',6);" | mysql OSS
fi
SYSTEMADDON=$( echo "select id from Acls where acl='system.addons' AND group_id=1;" | mysql OSS )
if [ -z "$SYSTEMADDON" ]; then
       echo "INSERT INTO Acls values(NULL,NULL,1,'system.addons','Y',6);"     | mysql OSS
fi

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
        systemctl restart cephalix-api
else
        systemctl restart oss-api
fi


