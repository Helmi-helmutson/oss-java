#!/bin/bash
export HOME="/root"

SoftwareFullNames=$( echo "SHOW TABLES LIKE 'SoftwareFullNames';" | mysql OSS 2>&1 )
if [ "$SoftwareFullNames" ]; then
        echo "SoftwareFullNames already installed"
        exit 0
fi

echo "CREATE TABLE IF NOT EXISTS SoftwareFullNames (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        software_id    BIGINT UNSIGNED,
        fullName       VARCHAR(64) NOT NULL,
        FOREIGN KEY(software_id)    REFERENCES Softwares(id),
        PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;
" | mysql OSS

if [ -e /usr/lib/systemd/system/cephalix-api.service ]; then
	systemctl restart cephalix-api
else
	systemctl restart oss-api
fi

