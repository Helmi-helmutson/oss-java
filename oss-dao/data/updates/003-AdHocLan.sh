#!/bin/bash
export HOME="/root"

DHCP=$( echo "select id from Enumerates where name='apiAcl' AND value='adhoclan.manage';" | mysql OSS )
if [ "$DHCP" ]; then
	echo "adhoclan.manage is already installed."
	exit 0
fi

echo "INSERT INTO Enumerates values(NULL,'apiAcl','adhoclan.manage',6);"  | mysql OSS 
echo "INSERT INTO Acls values(NULL,NULL,1,'adhoclan.managed','Y',6);"     | mysql OSS 

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
	systemctl restart cephalix-api
else
	systemctl restart oss-api
fi

