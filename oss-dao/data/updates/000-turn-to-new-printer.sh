#!/bin/bash

if [ -d /opt/oss-java/tmp/adaptDBForPrinters ]; then
	echo "Already run"
	exit 0
fi
if [ ! -e /var/log/oss-api.log ]; then
	echo "New installation"
	exit 0
fi
export HOME="/root"

PRINTERS=$( echo "SHOW TABLES LIKE 'Printers';" | mysql OSS 2>&1 )
if [ "$PRINTERS" ]; then
	echo "Ooops"
	exit 0
fi

systemctl stop oss-api
mkdir -p 700 /opt/oss-java/tmp/adaptDBForPrinters
cd /opt/oss-java/tmp/adaptDBForPrinters
echo "SELECT id,name FROM Devices where hwconf_id=2" | mysql OSS > printers
sed -i /id/d printers
mysqldump --no-create-info OSS AvailablePrinters > AvailablePrinters.sql
mysqldump --no-create-info OSS DefaultPrinter    > DefaultPrinter.sql
echo "DROP TABLE AvailablePrinters" | mysql OSS;
echo "DROP TABLE DefaultPrinter" | mysql OSS;
echo "CREATE TABLE IF NOT EXISTS Printers (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        device_id    BIGINT UNSIGNED NOT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
        name         VARCHAR(32) NOT NULL,
        FOREIGN KEY(device_id)  REFERENCES Devices(id),
        FOREIGN KEY(creator_id) REFERENCES Users(id),
        PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;
" |  mysql OSS;

echo "CREATE TABLE IF NOT EXISTS DefaultPrinter (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED DEFAULT NULL,
        device_id    BIGINT UNSIGNED DEFAULT NULL,
        printer_id   BIGINT UNSIGNED NOT NULL,
        FOREIGN KEY(room_id)    REFERENCES Rooms(id),
        FOREIGN KEY(device_id)  REFERENCES Devices(id),
        FOREIGN KEY(printer_id) REFERENCES Printers(id),
        PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;
" | mysql OSS;

echo "CREATE TABLE IF NOT EXISTS AvailablePrinters (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED DEFAULT NULL,
        device_id    BIGINT UNSIGNED DEFAULT NULL,
        printer_id   BIGINT UNSIGNED NOT NULL,
        FOREIGN KEY(room_id)    REFERENCES Rooms(id),
        FOREIGN KEY(device_id)  REFERENCES Devices(id),
        FOREIGN KEY(printer_id) REFERENCES Printers(id),
        PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;
" | mysql OSS;

gawk '{ print "INSERT INTO Printers VALUES(" $1 "," $1 ",1,\"" $2 "\");" }' printers > printers.sql
mysql OSS < printers.sql
mysql OSS < AvailablePrinters.sql
mysql OSS < DefaultPrinter.sql

echo "UPDATE Devices LEFT JOIN Users ON Devices.owner_id = Users.id set owner_id=NULL WHERE Users.role='sysadmins' OR Users.role='internal';" | mysql OSS;
systemctl daemon-reload
systemctl stop oss-api

