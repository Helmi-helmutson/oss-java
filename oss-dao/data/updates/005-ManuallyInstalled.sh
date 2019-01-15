#!/bin/bash
export HOME="/root"


if [ -e "/var/adm/oss/oss-java-update-005" ]
then
echo "oss-java-update-005 already installed"
        exit 0
fi

echo "UPDATE  SoftwareStatus AS ss LEFT JOIN SoftwareVersions AS sv ON ss.softwareversion_id = sv.id LEFT JOIN Softwares AS s ON sv.software_id = s.id SET ss.status='MI' WHERE s.manually = 'Y' AND ss.status='I';" | mysql OSS 

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
	systemctl restart cephalix-api
else
	systemctl restart oss-api
fi

touch /var/adm/oss/oss-java-update-005
