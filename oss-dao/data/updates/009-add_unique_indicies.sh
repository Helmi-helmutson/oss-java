#!/bin/bash

if [ -e "/var/adm/oss/oss-java-update-009" ]
then
	echo "oss-java-update-009 already installed"
        exit 0
fi

export HOME="/root"
echo "CREATE UNIQUE INDEX printers_name on Printers(name);" | mysql OSS
echo "CREATE UNIQUE INDEX hwconfs_name on HWConfs(name);" | mysql OSS
echo "CREATE UNIQUE INDEX softwares_name on Softwares(name);" | mysql OSS

touch /var/adm/oss/oss-java-update-009

