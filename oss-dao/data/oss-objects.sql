#TEST ONLY
#DROP DATABASE OSS;

CREATE DATABASE OSS;
USE OSS;

CREATE TABLE IF NOT EXISTS Users (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        uid          VARCHAR(32) NOT NULL,
        uuid         VARCHAR(64) DEFAULT NULL,
        role         VARCHAR(16) NOT NULL,
        sureName     VARCHAR(64) NOT NULL,
        givenName    VARCHAR(64),
        birthDay     DATE NOT NULL,
	fsQuota	     INTEGER DEFAULT 0,
	fsQuotaUsed  INTEGER DEFAULT 0,
	msQuota	     INTEGER DEFAULT 0,
	msQuotaUsed  INTEGER DEFAULT 0,
        PRIMARY KEY(id)
);

INSERT INTO Users VALUES(1,'admin','','sysadmins','Administrator','Main',NOW(),0,0,0,0);
INSERT INTO Users VALUES(2,'tteachers','','teachers','for teachers','Default profile',NOW(),0,0,0,0);
INSERT INTO Users VALUES(3,'tstudents','','students','for students','Default profile',NOW(),0,0,0,0);
INSERT INTO Users VALUES(4,'tadministration','','administrations','for administration','Default profile',NOW(),0,0,0,0);
INSERT INTO Users VALUES(5,'tworkstations','','workstations','for workstations','Default profile',NOW(),0,0,0,0);

CREATE TABLE IF NOT EXISTS Groups (
        id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        name        VARCHAR(32) NOT NULL,
        description VARCHAR(64) NOT NULL,
        groupType   VARCHAR(16) NOT NULL,
        PRIMARY KEY(id)
);

INSERT INTO Groups VALUES(1,'sysadmins','Sysadmins','primary');
INSERT INTO Groups VALUES(2,'teachers','Teachers','primary');
INSERT INTO Groups VALUES(3,'students','Students','primary');
INSERT INTO Groups VALUES(4,'administration','Administration','primary');
INSERT INTO Groups VALUES(5,'workstations','Workstations','primary');
INSERT INTO Groups VALUES(6,'templates','Templates','primary');

CREATE TABLE IF NOT EXISTS GroupMember (
        user_id        BIGINT UNSIGNED NOT NULL,
        group_id       BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(user_id)  REFERENCES Users(id)  ON DELETE CASCADE,
	FOREIGN KEY(group_id) REFERENCES Groups(id) ON DELETE CASCADE,
	PRIMARY KEY(user_id,group_id)
);

INSERT INTO GroupMember VALUES(1,1);
INSERT INTO GroupMember VALUES(2,6);
INSERT INTO GroupMember VALUES(3,6);
INSERT INTO GroupMember VALUES(4,6);
INSERT INTO GroupMember VALUES(5,6);
INSERT INTO GroupMember VALUES(2,2);
INSERT INTO GroupMember VALUES(3,3);
INSERT INTO GroupMember VALUES(4,4);
INSERT INTO GroupMember VALUES(5,5);

CREATE TABLE IF NOT EXISTS Aliases (
        id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        user_id         BIGINT UNSIGNED NOT NULL,
        alias           VARCHAR(64) NOT NULL,
        FOREIGN KEY(user_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS HWConfs (
        id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        name          VARCHAR(32) NOT NULL,
        description   VARCHAR(64) DEFAULT "",
        deviceType    VARCHAR(16) NOT NULL,
        PRIMARY KEY(id)
);
INSERT INTO HWConfs VALUES(1,"Server","","Server");
INSERT INTO HWConfs VALUES(2,"Printer","","Printer");

CREATE TABLE IF NOT EXISTS Partitions (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        hwconf_id    BIGINT UNSIGNED NOT NULL,
        name         VARCHAR(32) DEFAULT NULL,
        description  VARCHAR(64) DEFAULT NULL,
        OS           VARCHAR(16) DEFAULT NULL,
        joinType     VARCHAR(16) DEFAULT NULL,
        tool         VARCHAR(16) DEFAULT NULL,
        format       VARCHAR(16) DEFAULT NULL,
        FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id),
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Rooms (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        hwconf_id    BIGINT UNSIGNED DEFAULT NULL,
        name         VARCHAR(32) NOT NULL,
        description  VARCHAR(64) DEFAULT "",
        roomType     VARCHAR(16) NOT NULL,
        rows         INTEGER  DEFAULT 5,
        places       INTEGER  DEFAULT 5,
        startIP      VARCHAR(16) NOT NULL,
        netMask      INTEGER  NOT NULL,
        FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id),
        PRIMARY KEY(id)
);

INSERT INTO Rooms VALUES(1,1,'SERVER_NET','Virtual room for servers','VirtualRoom',10,10,'#SERVER_NETWORK#',#SERVER_NETMASK#);
INSERT INTO Rooms VALUES(2,NULL,'ANON_DHCP','Virtual room for unknown devices','VirtualRoom',10,10,'#ANON_NETWORK#',#ANON_NETMASK#);

CREATE TABLE IF NOT EXISTS Devices (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED NOT NULL,
        hwconf_id    BIGINT UNSIGNED DEFAULT NULL,
        owner_id     BIGINT UNSIGNED DEFAULT NULL,
        name         VARCHAR(32) NOT NULL,
        IP           VARCHAR(16) NOT NULL,
        WLANIP       VARCHAR(16) DEFAULT '',
        MAC          VARCHAR(17) DEFAULT '',
        WLANMAC      VARCHAR(17) DEFAULT '',
        row          INTEGER  DEFAULT 0,
        place        INTEGER  DEFAULT 0,
        FOREIGN KEY(room_id)   REFERENCES Rooms(id),
        FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id),
        FOREIGN KEY(owner_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

INSERT INTO Devices VALUES(1,1,1,NULL,'admin','#SCHOOL_SERVER#',NULL,'','',0,0);
INSERT INTO Devices VALUES(2,1,1,NULL,'schoolserver','#SCHOOL_MAILSERVER#',NULL,'','',0,0);
INSERT INTO Devices VALUES(3,1,1,NULL,'proxy','#SCHOOL_PROXY#',NULL,'','',0,0);
INSERT INTO Devices VALUES(4,1,1,NULL,'printserver','#SCHOOL_PRINTSERVER#',NULL,'','',0,0);
INSERT INTO Devices VALUES(5,1,1,NULL,'backup','#SCHOOL_BACKUP_SERVER#',NULL,'','',0,0);

CREATE TABLE IF NOT EXISTS AccessInRoom (
        id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id        BIGINT UNSIGNED NOT NULL,
        accessType     VARCHAR(8) NOT NULL,
        pointInTime    CHAR(5) DEFAULT '06:00',
	monday         CHAR(1) DEFAULT 'Y',
	tusday         CHAR(1) DEFAULT 'Y',
	wednesday      CHAR(1) DEFAULT 'Y',
	thursday       CHAR(1) DEFAULT 'Y',
	friday         CHAR(1) DEFAULT 'Y',
	saturday       CHAR(1) DEFAULT 'N',
	sunday         CHAR(1) DEFAULT 'N',
	holiday        CHAR(1) DEFAULT 'N',
        direct          CHAR(1) DEFAULT 'N',
        login           CHAR(1) DEFAULT 'Y',
        proxy           CHAR(1) DEFAULT 'Y',
        printing        CHAR(1) DEFAULT 'Y',
        portal          CHAR(1) DEFAULT 'Y',
	action          VARCHAR(32) DEFAULT '',
        FOREIGN KEY(room_id) REFERENCES Rooms(id),
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS LoggedOn (
        user_id      BIGINT UNSIGNED NOT NULL,
        device_id    BIGINT UNSIGNED NOT NULL,
        FOREIGN KEY(user_id)   REFERENCES Users(id)   ON DELETE CASCADE,
        FOREIGN KEY(device_id) REFERENCES Devices(id) ON DELETE CASCADE,
        PRIMARY KEY(device_id, user_id)
);

CREATE TABLE IF NOT EXISTS DefaultPrinter (
        room_id      BIGINT UNSIGNED NOT NULL,
        device_id    BIGINT UNSIGNED NOT NULL,
        printer_id   BIGINT UNSIGNED NOT NULL,
        FOREIGN KEY(room_id)    REFERENCES Rooms(id),
        FOREIGN KEY(device_id)  REFERENCES Devices(id),
        FOREIGN KEY(printer_id) REFERENCES Devices(id),
        PRIMARY KEY(device_id, room_id)
);

CREATE TABLE IF NOT EXISTS AvailablePrinters (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED DEFAULT NULL,
        device_id    BIGINT UNSIGNED DEFAULT NULL,
        printer_id   BIGINT UNSIGNED NOT NULL,
        FOREIGN KEY(room_id)    REFERENCES Rooms(id),
        FOREIGN KEY(device_id)  REFERENCES Devices(id),
        FOREIGN KEY(printer_id) REFERENCES Devices(id),
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Acls (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        user_id      BIGINT UNSIGNED DEFAULT NULL,
        group_id     BIGINT UNSIGNED DEFAULT NULL,
        role         VARCHAR(16) DEFAULT NULL,
        acl          VARCHAR(32) NOT NULL,
        FOREIGN KEY(user_id)  REFERENCES Users(id),
        FOREIGN KEY(group_id) REFERENCES Groups(id),
        PRIMARY KEY(id)
);

INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','category.add');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','category.modify');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','category.search');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','device.add');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','device.delete');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','device.manage');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','device.search');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.add');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.delete');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.manage');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.modify');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.search');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.add');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.delete');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.manage');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.modify');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.search');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','sysadmins');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.add');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.delete');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.manage');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.modify');
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.search');

CREATE TABLE IF NOT EXISTS  Tests (
        id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        testName      VARCHAR(128) NOT NULL,
        teacher_id    BIGINT UNSIGNED NOT NULL,
        room_id       BIGINT UNSIGNED,
        testDir       VARCHAR(128) NOT NULL,
        currentStep   VARCHAR(128) NOT NULL,
        startTime     DATETIME NOT NULL,
        endTime       DATETIME NOT NULL,
        login         CHAR(1) NOT NULL DEFAULT 'Y',
        proxy         CHAR(1) NOT NULL DEFAULT 'N',
        direct        CHAR(1) NOT NULL DEFAULT 'N',
        portal        CHAR(1) NOT NULL DEFAULT 'N',
        FOREIGN KEY(teacher_id) REFERENCES Users(id) ON DELETE SET NULL,
        FOREIGN KEY(room_id)    REFERENCES Rooms(id) ON DELETE SET NULL,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS TestFiles (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        test_id      BIGINT UNSIGNED NOT NULL,
        user_id      BIGINT UNSIGNED NOT NULL,
        getOrPost    VARCHAR(128) NOT NULL,
        fileName     VARCHAR(256) NOT NULL,
        dateTime     DATETIME NOT NULL,
        FOREIGN KEY(test_id) REFERENCES Tests(id) ON DELETE CASCADE,
        FOREIGN KEY(user_id) REFERENCES Users(id) ON DELETE SET NULL,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS TestUsers (
        test_id      BIGINT UNSIGNED NOT NULL,
        user_id      BIGINT UNSIGNED NOT NULL,
        device_id    BIGINT UNSIGNED,
        FOREIGN KEY(test_id) REFERENCES   Tests(id)   ON DELETE CASCADE,
        FOREIGN KEY(user_id) REFERENCES   Users(id)   ON DELETE SET NULL,
        FOREIGN KEY(device_id) REFERENCES Devices(id) ON DELETE SET NULL,
        PRIMARY KEY(test_id,user_id)
);

CREATE TABLE IF NOT EXISTS Enumerates (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        name         VARCHAR(32)  NOT NULL,
        value        VARCHAR(32)  NOT NULL,
        PRIMARY KEY(id)
);

INSERT INTO Enumerates VALUES(NULL,'deviceType','FatClient');
INSERT INTO Enumerates VALUES(NULL,'deviceType','ManagedMobileDevice');
INSERT INTO Enumerates VALUES(NULL,'deviceType','MobileDevice');
INSERT INTO Enumerates VALUES(NULL,'deviceType','Printer');
INSERT INTO Enumerates VALUES(NULL,'deviceType','Router');
INSERT INTO Enumerates VALUES(NULL,'deviceType','Server');
INSERT INTO Enumerates VALUES(NULL,'deviceType','Switch');
INSERT INTO Enumerates VALUES(NULL,'deviceType','ThinClient');
INSERT INTO Enumerates VALUES(NULL,'role','students');
INSERT INTO Enumerates VALUES(NULL,'role','teachers');
INSERT INTO Enumerates VALUES(NULL,'role','sysadmins');
INSERT INTO Enumerates VALUES(NULL,'role','administration');
INSERT INTO Enumerates VALUES(NULL,'role','workstations');
INSERT INTO Enumerates VALUES(NULL,'groupType','primary');
INSERT INTO Enumerates VALUES(NULL,'groupType','class');
INSERT INTO Enumerates VALUES(NULL,'groupType','workgroup');
INSERT INTO Enumerates VALUES(NULL,'groupType','guest');
INSERT INTO Enumerates VALUES(NULL,'roomType','ClassRoom');
INSERT INTO Enumerates VALUES(NULL,'roomType','ComputerRoom');
INSERT INTO Enumerates VALUES(NULL,'roomType','Library');
INSERT INTO Enumerates VALUES(NULL,'roomType','Laboratory');
INSERT INTO Enumerates VALUES(NULL,'roomType','VirtualRoom');
INSERT INTO Enumerates VALUES(NULL,'roomType','WlanAccess');
INSERT INTO Enumerates VALUES(NULL,'roomType','AdHocAccess');
INSERT INTO Enumerates VALUES(NULL,'accessType','DEFAULT');
INSERT INTO Enumerates VALUES(NULL,'accessType','FW');
INSERT INTO Enumerates VALUES(NULL,'accessType','ACT');
INSERT INTO Enumerates VALUES(NULL,'licenseType','NONE');
INSERT INTO Enumerates VALUES(NULL,'licenseType','FILE');
INSERT INTO Enumerates VALUES(NULL,'licenseType','XML');
INSERT INTO Enumerates VALUES(NULL,'licenseType','CMD');

#Some additional config tables
CREATE TABLE IF NOT EXISTS UserConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	user_id      BIGINT UNSIGNED NOT NULL,
	keyword      VARCHAR(64) NOT NULL,
	value        VARCHAR(128) NOT NULL,
	PRIMARY KEY(id)
);
CREATE UNIQUE INDEX UserConfigIndex on UserConfig(user_id,keyword);

CREATE TABLE IF NOT EXISTS UserMConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        user_id      BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        PRIMARY KEY(id)
);
CREATE UNIQUE INDEX UserMConfigIndex on UserMConfig(user_id,keyword,value);

CREATE TABLE IF NOT EXISTS GroupConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        group_id     BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        PRIMARY KEY(id)
);
CREATE UNIQUE INDEX GroupConfigIndex on GroupConfig(group_id,keyword);

CREATE TABLE IF NOT EXISTS GroupMConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        group_id     BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        PRIMARY KEY(id)
);
CREATE UNIQUE INDEX GroupMConfigIndex on GroupMConfig(group_id,keyword,value);

CREATE TABLE IF NOT EXISTS DeviceConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        device_id    BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        PRIMARY KEY(id)
);
CREATE UNIQUE INDEX DeviceConfigIndex on DeviceConfig(device_id,keyword);

CREATE TABLE IF NOT EXISTS DeviceMConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        device_id    BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        PRIMARY KEY(id)
);
CREATE UNIQUE INDEX DeviceMConfigIndex on DeviceMConfig(device_id,keyword);

CREATE TABLE IF NOT EXISTS RoomConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        PRIMARY KEY(id)
);
CREATE UNIQUE INDEX RoomConfigIndex on RoomConfig(room_id,keyword);

CREATE TABLE IF NOT EXISTS RoomMConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        PRIMARY KEY(id)
);
CREATE UNIQUE INDEX RoomMConfigIndex on RoomMConfig(room_id,keyword,value);

CREATE TABLE IF NOT EXISTS Sessions (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        user_id      BIGINT UNSIGNED NOT NULL,
        room_id      BIGINT UNSIGNED DEFAULT NULL,
        device_id    BIGINT UNSIGNED DEFAULT NULL,
        createdate   TIMESTAMP NOT NULL,
        ip           VARCHAR(30),
        token        VARCHAR(60),
        FOREIGN KEY(user_id)   REFERENCES Users(id)   ON DELETE CASCADE,
        FOREIGN KEY(room_id)   REFERENCES Rooms(id),
	FOREIGN KEY(device_id) REFERENCES Devices(id),
        PRIMARY KEY(id)
 );

CREATE TABLE IF NOT EXISTS Responses (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	session_id   BIGINT UNSIGNED NOT NULL,
	code	VARCHAR(64) NOT NULL,
	value   VARCHAR(1024) NOT NULL,
	FOREIGN KEY(session_id) REFERENCES Sessions(id) ON DELETE CASCADE,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Software (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	name        VARCHAR(32) NOT NULL,
	description VARCHAR(64) DEFAULT NULL,
	weigth      INTEGER DEFAULT 0,
	manuell     CHAR(1) DEFAULT 'N',
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS SoftwareVersions (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        software_id    BIGINT UNSIGNED,
	version        VARCHAR(32) NOT NULL,
        FOREIGN KEY(software_id)    REFERENCES Software(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS SoftwareStatus (
        version_id         BIGINT UNSIGNED NOT NULL,
        device_id          BIGINT UNSIGNED NOT NULL,
	status             VARCHAR(32) NOT NULL,
	FOREIGN KEY(version_id)  REFERENCES SoftwareVersions(id) ON DELETE CASCADE,
	FOREIGN KEY(device_id)   REFERENCES Devices(id)          ON DELETE CASCADE,
	PRIMARY KEY(version_id,device_id)
);

CREATE TABLE IF NOT EXISTS SoftwareLicenses (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        software_id    BIGINT UNSIGNED,
	licenseType    VARCHAR(4) DEFAULT 'CMD',
	count          INTEGER DEFAULT 1,
	value          VARCHAR(1024) NOT NULL,
        FOREIGN KEY(software_id)    REFERENCES Software(id)     ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS LicenseToDevice (
        license_id         BIGINT UNSIGNED NOT NULL,
        device_id          BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(license_id)  REFERENCES SoftwareLicenses(id) ON DELETE CASCADE,
	FOREIGN KEY(device_id)   REFERENCES Devices(id)          ON DELETE CASCADE,
	PRIMARY KEY(license_id,device_id)
);

CREATE TABLE IF NOT EXISTS Categories (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	name         VARCHAR(32) NOT NULL,
	description  VARCHAR(64) NOT NULL,
        owner_id     BIGINT UNSIGNED DEFAULT NULL,
        FOREIGN KEY(owner_id)  REFERENCES Users(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS DeviceInCategories (
        device_id          BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(device_id)    REFERENCES Devices(id)     ON DELETE CASCADE,
	FOREIGN KEY(category_id)  REFERENCES Categories(id)  ON DELETE CASCADE,
	PRIMARY KEY(device_id,category_id)
);

CREATE TABLE IF NOT EXISTS GroupInCategories (
        group_id           BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(group_id)     REFERENCES Groups(id)     ON DELETE CASCADE,	
	FOREIGN KEY(category_id)  REFERENCES Categories(id) ON DELETE CASCADE,
	PRIMARY KEY(group_id,category_id)
);

CREATE TABLE IF NOT EXISTS HWConfInCategories (
        hwconf_id          BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(hwconf_id)    REFERENCES HWConfs(id)    ON DELETE CASCADE,
	FOREIGN KEY(category_id)  REFERENCES Categories(id) ON DELETE CASCADE,
	PRIMARY KEY(hwconf_id,category_id)
);

CREATE TABLE IF NOT EXISTS RoomInCategories (
        room_id            BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(room_id)      REFERENCES Rooms(id)      ON DELETE CASCADE,
	FOREIGN KEY(category_id)  REFERENCES Categories(id) ON DELETE CASCADE,
	PRIMARY KEY(room_id,category_id)
);

CREATE TABLE IF NOT EXISTS SoftwareInCategories (
        software_id        BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(software_id)  REFERENCES Software(id)   ON DELETE CASCADE,
	FOREIGN KEY(category_id)  REFERENCES Categories(id) ON DELETE CASCADE,
	PRIMARY KEY(software_id,category_id)
);

CREATE TABLE IF NOT EXISTS SoftwareRemovedFromCategories (
        software_id        BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(software_id)  REFERENCES Software(id)   ON DELETE CASCADE,
	FOREIGN KEY(category_id)  REFERENCES Categories(id) ON DELETE CASCADE,
	PRIMARY KEY(software_id,category_id)
);

CREATE TABLE IF NOT EXISTS UserInCategories (
        user_id            BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(user_id)      REFERENCES Users(id)      ON DELETE CASCADE,
	FOREIGN KEY(category_id)  REFERENCES Categories(id) ON DELETE CASCADE,
	PRIMARY KEY(user_id,category_id)
);

CREATE TABLE IF NOT EXISTS AssignedSoftware (
        software_id        BIGINT UNSIGNED NOT NULL,
        device_id          BIGINT UNSIGNED DEFAULT NULL,
        room_id            BIGINT UNSIGNED DEFAULT NULL,
        hwconf_id          BIGINT UNSIGNED DEFAULT NULL,
        category_id        BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(software_id)  REFERENCES Software(id),
	FOREIGN KEY(device_id)    REFERENCES Devices(id),
	FOREIGN KEY(room_id)      REFERENCES Rooms(id),
	FOREIGN KEY(hwconf_id)    REFERENCES HWConfs(id),
	FOREIGN KEY(category_id)  REFERENCES Categories(id),
	PRIMARY KEY(software_id,device_id,room_id,hwconf_id,category_id)  -- dieser key passt nicht. elemente dürfen nicht null sein
);

