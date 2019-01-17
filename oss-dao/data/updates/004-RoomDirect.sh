#!/bin/bash
export HOME="/root"

DIRECT=$( echo "select id from Enumerates where name='apiAcl' AND value='room.direct';" | mysql OSS )
if [ "$DIRECT" ]; then
	echo "room.direct is already installed."
	exit 0
fi

echo "INSERT INTO Enumerates values(NULL,'apiAcl','room.direct',6);"  | mysql OSS 
echo "INSERT INTO Acls values(NULL,NULL,1,'room.direct','Y',6);"     | mysql OSS 
echo "INSERT INTO Acls values(NULL,NULL,2,'room.direct','Y',6);"     | mysql OSS 

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
	systemctl restart cephalix-api
else
	systemctl restart oss-api
fi

