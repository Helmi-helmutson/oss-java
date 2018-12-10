#!/bin/bash
export HOME="/root"

DHCP=$( echo "select id from Enumerates where name='apiAcl' AND value='room.dhcp';" | mysql OSS )
if [ "$DHCP" ]; then
	echo "room.dhcp is already installed."
	exit 0
fi

echo "INSERT INTO Enumerates values(NULL,'apiAcl','room.dhcp',6);"  | mysql OSS 
echo "INSERT INTO Acls values(NULL,NULL,1,'room.dhcpd','Y',6);"     | mysql OSS 

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
	systemctl restart cephalix-api
else
	systemctl restart oss-api
fi

