#!/bin/bash
export HOME="/root"

echo "UPDATE  SoftwareStatus AS ss LEFT JOIN SoftwareVersions AS sv ON ss.softwareversion_id = sv.id LEFT JOIN Softwares AS s ON sv.software_id = s.id SET ss.status='MI' WHERE s.manually = 'Y';" | mysql OSS 

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
	systemctl restart cephalix-api
else
	systemctl restart oss-api
fi

