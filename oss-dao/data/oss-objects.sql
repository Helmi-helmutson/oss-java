#TEST ONLY
#DROP DATABASE OSS;

CREATE DATABASE OSS;
USE OSS;


CREATE TABLE IF NOT EXISTS Translations(
        id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	lang	VARCHAR(2)   NOT NULL DEFAULT 'DE',
	string  VARCHAR(256) NOT NULL,
        value   VARCHAR(256),
	PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS MissedTranslations(
        id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	lang	VARCHAR(2)   NOT NULL DEFAULT 'DE',
	string  VARCHAR(250) NOT NULL,
	PRIMARY KEY  (id)
);

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
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

INSERT INTO Users VALUES(1,'Administrator','','sysadmins','Administrator','Main',NOW(),0,0,0,0,NULL);
INSERT INTO Users VALUES(2,'tteachers','','teachers','for teachers','Default profile',NOW(),0,0,0,0,1);
INSERT INTO Users VALUES(3,'tstudents','','students','for students','Default profile',NOW(),0,0,0,0,1);
INSERT INTO Users VALUES(4,'tadministration','','administrations','for administration','Default profile',NOW(),0,0,0,0,1);
INSERT INTO Users VALUES(5,'tworkstations','','workstations','for workstations','Default profile',NOW(),0,0,0,0,1);
INSERT INTO Users VALUES(6,'cephalix','','sysadmins','Administrator','Internal',NOW(),0,0,0,0,1);

CREATE TABLE IF NOT EXISTS Groups (
        id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        name        VARCHAR(32) NOT NULL,
        description VARCHAR(64) NOT NULL,
        groupType   VARCHAR(16) NOT NULL,
        owner_id    BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(owner_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

INSERT INTO Groups VALUES(1,'sysadmins','Sysadmins','primary',1);
INSERT INTO Groups VALUES(2,'teachers','Teachers','primary',1);
INSERT INTO Groups VALUES(3,'students','Students','primary',1);
INSERT INTO Groups VALUES(4,'administration','Administration','primary',1);
INSERT INTO Groups VALUES(5,'workstations','Workstations','primary',1);
INSERT INTO Groups VALUES(6,'templates','Templates','primary',1);

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
INSERT INTO GroupMember VALUES(6,1);

CREATE TABLE IF NOT EXISTS Aliases (
        id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        user_id         BIGINT UNSIGNED NOT NULL,
        alias           VARCHAR(64) NOT NULL,
        FOREIGN KEY(user_id)  REFERENCES Users(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS HWConfs (
        id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        name          VARCHAR(32) NOT NULL,
        description   VARCHAR(64) DEFAULT "",
        deviceType    VARCHAR(16) NOT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);
INSERT INTO HWConfs VALUES(1,"Server","","Server",1);
INSERT INTO HWConfs VALUES(2,"Printer","","Printer",1);
INSERT INTO HWConfs VALUES(3,"BYOD","Privat Devices","BYOD",1);

CREATE TABLE IF NOT EXISTS Partitions (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        hwconf_id    BIGINT UNSIGNED NOT NULL,
        name         VARCHAR(32) DEFAULT NULL,
        description  VARCHAR(64) DEFAULT NULL,
        OS           VARCHAR(16) DEFAULT NULL,
        joinType     VARCHAR(16) DEFAULT NULL,
        tool         VARCHAR(16) DEFAULT NULL,
        format       VARCHAR(16) DEFAULT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Rooms (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        hwconf_id    BIGINT UNSIGNED DEFAULT NULL,
        name         VARCHAR(32) NOT NULL,
        description  VARCHAR(64) DEFAULT "",
        roomType     VARCHAR(16) NOT NULL,
        roomControl  VARCHAR(16) NOT NULL,
        rows         INTEGER  DEFAULT 5,
        places       INTEGER  DEFAULT 5,
        startIP      VARCHAR(16) DEFAULT NULL,
        netMask      INTEGER  DEFAULT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id) ON DELETE RESTRICT,
        PRIMARY KEY(id)
);

INSERT INTO Rooms VALUES(1,1,'SERVER_NET','Virtual room for servers','technicalRoom','no_control',10,10,'#SERVER_NETWORK#',#SERVER_NETMASK#,6);
INSERT INTO Rooms VALUES(2,NULL,'ANON_DHCP','Virtual room for unknown devices','technicalRoom','no_control',10,10,'#ANON_NETWORK#',#ANON_NETMASK#,6);

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
	serial       VARCHAR(16) DEFAULT '',
	inventary    VARCHAR(16) DEFAULT '',
	locality     VARCHAR(16) DEFAULT '',
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
        FOREIGN KEY(room_id)   REFERENCES Rooms(id)   ON DELETE RESTRICT,
        FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id) ON DELETE RESTRICT,
        FOREIGN KEY(owner_id)  REFERENCES Users(id)   ON DELETE CASCADE,
        PRIMARY KEY(id)
);

INSERT INTO Devices VALUES(1,1,1,NULL,'admin','#SCHOOL_SERVER#',NULL,'','',0,0,'','','',1);
INSERT INTO Devices VALUES(2,1,1,NULL,'schoolserver','#SCHOOL_MAILSERVER#',NULL,'','',0,0,'','','',1);
INSERT INTO Devices VALUES(3,1,1,NULL,'proxy','#SCHOOL_PROXY#',NULL,'','',0,0,'','','',1);
INSERT INTO Devices VALUES(4,1,1,NULL,'printserver','#SCHOOL_PRINTSERVER#',NULL,'','',0,0,'','','',1);
INSERT INTO Devices VALUES(5,1,1,NULL,'backup','#SCHOOL_BACKUP_SERVER#',NULL,'','',0,0,'','','',1);

CREATE TABLE IF NOT EXISTS AccessInRooms (
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
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        FOREIGN KEY(room_id) REFERENCES Rooms(id) ON DELETE CASCADE,
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
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED DEFAULT NULL,
        device_id    BIGINT UNSIGNED DEFAULT NULL,
        printer_id   BIGINT UNSIGNED NOT NULL,
        FOREIGN KEY(room_id)    REFERENCES Rooms(id)   ON DELETE CASCADE,
        FOREIGN KEY(device_id)  REFERENCES Devices(id) ON DELETE CASCADE,
        FOREIGN KEY(printer_id) REFERENCES Devices(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS AvailablePrinters (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED DEFAULT NULL,
        device_id    BIGINT UNSIGNED DEFAULT NULL,
        printer_id   BIGINT UNSIGNED NOT NULL,
        FOREIGN KEY(room_id)    REFERENCES Rooms(id)   ON DELETE CASCADE,
        FOREIGN KEY(device_id)  REFERENCES Devices(id) ON DELETE CASCADE,
        FOREIGN KEY(printer_id) REFERENCES Devices(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Acls (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        user_id      BIGINT UNSIGNED DEFAULT NULL,
        group_id     BIGINT UNSIGNED DEFAULT NULL,
        role         VARCHAR(16) DEFAULT NULL,
        acl          VARCHAR(32) NOT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        FOREIGN KEY(user_id)  REFERENCES Users(id)  ON DELETE CASCADE,
        FOREIGN KEY(group_id) REFERENCES Groups(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','category.add',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','category.modify',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','category.search',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','device.add',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','device.delete',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','device.manage',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','device.search',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.add',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.delete',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.manage',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.modify',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','group.search',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.add',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.delete',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.manage',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.modify',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','room.search',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','sysadmins',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','software.add',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','software.delete',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','software.download',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','software.install',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','software.modify',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','software.search',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.add',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.delete',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.manage',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.modify',6);
INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','user.search',6);

CREATE TABLE IF NOT EXISTS  Tests (
        id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        testName      VARCHAR(128) NOT NULL,
        teacher_id    BIGINT UNSIGNED,
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
        user_id      BIGINT UNSIGNED,
        getOrPost    VARCHAR(128) NOT NULL,
        fileName     VARCHAR(256) NOT NULL,
        dateTime     DATETIME NOT NULL,
        FOREIGN KEY(test_id) REFERENCES Tests(id) ON DELETE CASCADE,
        FOREIGN KEY(user_id) REFERENCES Users(id) ON DELETE SET NULL,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS TestUsers (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        test_id      BIGINT UNSIGNED NOT NULL,
        user_id      BIGINT UNSIGNED,
        device_id    BIGINT UNSIGNED,
        FOREIGN KEY(test_id) REFERENCES   Tests(id)   ON DELETE CASCADE,
        FOREIGN KEY(user_id) REFERENCES   Users(id)   ON DELETE SET NULL,
        FOREIGN KEY(device_id) REFERENCES Devices(id) ON DELETE SET NULL,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Enumerates (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        name         VARCHAR(32)  NOT NULL,
        value        VARCHAR(32)  NOT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

INSERT INTO Enumerates VALUES(NULL,'deviceType','FatClient',1);
INSERT INTO Enumerates VALUES(NULL,'deviceType','Printer',1);
INSERT INTO Enumerates VALUES(NULL,'deviceType','Router',1);
INSERT INTO Enumerates VALUES(NULL,'deviceType','Server',1);
INSERT INTO Enumerates VALUES(NULL,'deviceType','Switch',1);
INSERT INTO Enumerates VALUES(NULL,'deviceType','ThinClient',1);
INSERT INTO Enumerates VALUES(NULL,'deviceType','BYOD',1);
INSERT INTO Enumerates VALUES(NULL,'role','students',1);
INSERT INTO Enumerates VALUES(NULL,'role','teachers',1);
INSERT INTO Enumerates VALUES(NULL,'role','sysadmins',1);
INSERT INTO Enumerates VALUES(NULL,'role','administration',1);
INSERT INTO Enumerates VALUES(NULL,'role','workstations',1);
INSERT INTO Enumerates VALUES(NULL,'groupType','primary',1);
INSERT INTO Enumerates VALUES(NULL,'groupType','class',1);
INSERT INTO Enumerates VALUES(NULL,'groupType','workgroup',1);
INSERT INTO Enumerates VALUES(NULL,'groupType','guest',1);
INSERT INTO Enumerates VALUES(NULL,'roomControl','inRoom',1);
INSERT INTO Enumerates VALUES(NULL,'roomControl','no',1);
INSERT INTO Enumerates VALUES(NULL,'roomControl','allTeacher',1);
INSERT INTO Enumerates VALUES(NULL,'roomControl','teacher',1);
INSERT INTO Enumerates VALUES(NULL,'roomType','ClassRoom',1);
INSERT INTO Enumerates VALUES(NULL,'roomType','ComputerRoom',1);
INSERT INTO Enumerates VALUES(NULL,'roomType','Library',1);
INSERT INTO Enumerates VALUES(NULL,'roomType','Laboratory',1);
INSERT INTO Enumerates VALUES(NULL,'roomType','WlanAccess',1);
INSERT INTO Enumerates VALUES(NULL,'roomType','AdHocAccess',1);
INSERT INTO Enumerates VALUES(NULL,'roomType','TechnicalRoom',1);
INSERT INTO Enumerates VALUES(NULL,'accessType','DEFAULT',1);
INSERT INTO Enumerates VALUES(NULL,'accessType','FW',1);
INSERT INTO Enumerates VALUES(NULL,'accessType','ACT',1);
INSERT INTO Enumerates VALUES(NULL,'licenseType','NONE',1);
INSERT INTO Enumerates VALUES(NULL,'licenseType','FILE',1);
INSERT INTO Enumerates VALUES(NULL,'licenseType','CMD',1);
INSERT INTO Enumerates VALUES(NULL,'categoryType','software',1);
INSERT INTO Enumerates VALUES(NULL,'categoryType','virtualRoom',1);

#Some additional config tables
CREATE TABLE IF NOT EXISTS OSSConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	objectType   VARCHAR(12)     NOT NULL,
        objectId     BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS OSSMConfig (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	objectType   VARCHAR(12)     NOT NULL,
        objectId     BIGINT UNSIGNED NOT NULL,
        keyword      VARCHAR(64) NOT NULL,
        value        VARCHAR(128) NOT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS RoomSmartControlls (
	id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED NOT NULL,
        user_id      BIGINT UNSIGNED NOT NULL,
        startTime    DATE NOT NULL,
        endTime      DATE NOT NULL,
        FOREIGN KEY(room_id) REFERENCES   Rooms(id)   ON DELETE CASCADE,
        FOREIGN KEY(user_id) REFERENCES   Users(id)   ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Sessions (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        user_id      BIGINT UNSIGNED NOT NULL,
        room_id      BIGINT UNSIGNED DEFAULT NULL,
        device_id    BIGINT UNSIGNED DEFAULT NULL,
        createdate   TIMESTAMP NOT NULL,
        ip           VARCHAR(30),
        token        VARCHAR(60),
        FOREIGN KEY(user_id)   REFERENCES Users(id)   ON DELETE CASCADE,
        FOREIGN KEY(room_id)   REFERENCES Rooms(id)   ON DELETE SET NULL,
	FOREIGN KEY(device_id) REFERENCES Devices(id) ON DELETE SET NULL,
        PRIMARY KEY(id)
 );

CREATE TABLE IF NOT EXISTS OssResponses (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	session_id   BIGINT UNSIGNED NOT NULL,
	code	VARCHAR(64) NOT NULL,
	value   VARCHAR(1024) NOT NULL,
	FOREIGN KEY(session_id) REFERENCES Sessions(id) ON DELETE CASCADE,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Softwares (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	name        VARCHAR(32) NOT NULL,
	description VARCHAR(64) DEFAULT NULL,
	weight      INTEGER DEFAULT 0,
	manually    CHAR(1) DEFAULT 'N',
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);


CREATE TABLE IF NOT EXISTS SoftwareVersions (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        software_id    BIGINT UNSIGNED,
	version        VARCHAR(32) NOT NULL,
        FOREIGN KEY(software_id)    REFERENCES Softwares(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

# status I  -> installed
# status IS -> installation scheduled
# status MD -> manuell deinstalled
# status DS -> deinstallation scheduled
# status DF -> deinstallation failed
# status IF -> installation failed
CREATE TABLE IF NOT EXISTS SoftwareStatus (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        version_id         BIGINT UNSIGNED NOT NULL,
        device_id          BIGINT UNSIGNED NOT NULL,
	status             VARCHAR(2) NOT NULL,
	FOREIGN KEY(version_id)  REFERENCES SoftwareVersions(id) ON DELETE CASCADE,
	FOREIGN KEY(device_id)   REFERENCES Devices(id)          ON DELETE CASCADE,
	PRIMARY KEY(id)
);

# licenseType C -> command line license
# licenseType F -> file license in this case the value contains the license file name
CREATE TABLE IF NOT EXISTS SoftwareLicenses (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        software_id    BIGINT UNSIGNED,
	licenseType    CHAR(1) DEFAULT 'C',
	count          INTEGER DEFAULT 1,
	value          VARCHAR(1024) NOT NULL,
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        FOREIGN KEY(software_id)    REFERENCES Softwares(id)     ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS LicenseToDevice (
        license_id         BIGINT UNSIGNED NOT NULL,
        device_id          BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(license_id)  REFERENCES SoftwareLicenses(id) ON DELETE CASCADE,
	FOREIGN KEY(device_id)   REFERENCES Devices(id)          ON DELETE CASCADE,
	PRIMARY KEY(license_id,device_id)
);

CREATE TABLE IF NOT EXISTS Announcements (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        owner_id   BIGINT UNSIGNED DEFAULT NULL,
        validFrom  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        validUntil DATETIME,
        keywords   VARCHAR(128) NOT NULL,
        title      VARCHAR(128) NOT NULL,
        abstract   BLOB,
        text       BLOB,
        issuer     VARCHAR(128),
        FOREIGN KEY(owner_id)  REFERENCES Users(id) ON DELETE CASCADE,
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS HaveSeen (
        user_id            BIGINT UNSIGNED DEFAULT NULL,
        announcement_id    BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(user_id)           REFERENCES Users(id)         ON DELETE CASCADE,
	FOREIGN KEY(announcement_id)   REFERENCES Announcements(id) ON DELETE CASCADE,
	PRIMARY KEY(announcement_id,user_id)
);

CREATE TABLE IF NOT EXISTS FAQs (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        owner_id   BIGINT UNSIGNED DEFAULT NULL,
        issue      VARCHAR(128) default NULL,
        title      VARCHAR(128) NOT NULL,
        abstract   BLOB,
        text       BLOB,
        FOREIGN KEY(owner_id)  REFERENCES Users(id) ON DELETE SET NULL,
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS Contacts (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        owner_id   BIGINT UNSIGNED DEFAULT NULL,
        issue      VARCHAR(128) default NULL,
        name       VARCHAR(128) default NULL,
        phone      VARCHAR(128) default NULL,
        email      VARCHAR(128) default NULL,
        title      VARCHAR(128) default NULL,
        FOREIGN KEY(owner_id)  REFERENCES Users(id) ON DELETE SET NULL,
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS Categories (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	name         VARCHAR(32) NOT NULL,
	description  VARCHAR(64) DEFAULT NULL,
	categoryType VARCHAR(16) DEFAULT NULL,
        owner_id     BIGINT UNSIGNED DEFAULT NULL,
	studentsOnly CHAR(1) DEFAULT 'N',
        FOREIGN KEY(owner_id)  REFERENCES Users(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

INSERT INTO Categories Values(1, 'Announcements for all','','announcements','1','N');
INSERT INTO Categories Values(2, 'Announcements for sysadmins','','announcements','1','N');
INSERT INTO Categories Values(3, 'Announcements for teachers','','announcements','1','N');
INSERT INTO Categories Values(4, 'Announcements for students','','announcements','1','N');
INSERT INTO Categories Values(5, 'Announcements for administration','','announcements','1','N');
INSERT INTO Categories Values(6, 'Contacts for all','','contacts','1','N');
INSERT INTO Categories Values(7, 'Contacts for sysadmins','','contacts','1','N');
INSERT INTO Categories Values(8, 'Contacts for teachers','','contacts','1','N');
INSERT INTO Categories Values(9, 'Contacts for students','','contacts','1','N');
INSERT INTO Categories Values(10,'Contacts for administration','','contacts','1','N');
INSERT INTO Categories Values(11,'FAQs for all','','faqs','1','N');
INSERT INTO Categories Values(12,'FAQs for sysadmins','','faqs','1','N');
INSERT INTO Categories Values(13,'FAQs for teachers','','faqs','1','N');
INSERT INTO Categories Values(14,'FAQs for students','','faqs','1','N');
INSERT INTO Categories Values(15,'FAQs for administration','','faqs','1','N');

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

INSERT INTO GroupInCategories Values(1,1);
INSERT INTO GroupInCategories Values(2,1);
INSERT INTO GroupInCategories Values(3,1);
INSERT INTO GroupInCategories Values(4,1);
INSERT INTO GroupInCategories Values(1,6);
INSERT INTO GroupInCategories Values(2,6);
INSERT INTO GroupInCategories Values(3,6);
INSERT INTO GroupInCategories Values(4,6);
INSERT INTO GroupInCategories Values(1,11);
INSERT INTO GroupInCategories Values(2,11);
INSERT INTO GroupInCategories Values(3,11);
INSERT INTO GroupInCategories Values(4,11);
INSERT INTO GroupInCategories Values(1,2);
INSERT INTO GroupInCategories Values(1,7);
INSERT INTO GroupInCategories Values(1,12);
INSERT INTO GroupInCategories Values(2,3);
INSERT INTO GroupInCategories Values(2,8);
INSERT INTO GroupInCategories Values(2,13);
INSERT INTO GroupInCategories Values(3,4);
INSERT INTO GroupInCategories Values(3,9);
INSERT INTO GroupInCategories Values(3,14);
INSERT INTO GroupInCategories Values(4,5);
INSERT INTO GroupInCategories Values(4,10);
INSERT INTO GroupInCategories Values(4,15);

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
	FOREIGN KEY(software_id)  REFERENCES Softwares(id)   ON DELETE CASCADE,
	FOREIGN KEY(category_id)  REFERENCES Categories(id) ON DELETE CASCADE,
	PRIMARY KEY(software_id,category_id)
);

CREATE TABLE IF NOT EXISTS SoftwareRemovedFromCategories (
        software_id        BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(software_id)  REFERENCES Softwares(id)   ON DELETE CASCADE,
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

CREATE TABLE IF NOT EXISTS AnnouncementInCategories (
        announcement_id    BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(announcement_id) REFERENCES Announcements(id) ON DELETE CASCADE,
	FOREIGN KEY(category_id)     REFERENCES Categories(id)    ON DELETE CASCADE,
	PRIMARY KEY(announcement_id,category_id)
);

CREATE TABLE IF NOT EXISTS FAQInCategories (
        faq_id    BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(faq_id) REFERENCES FAQs(id) ON DELETE CASCADE,
	FOREIGN KEY(category_id)     REFERENCES Categories(id)    ON DELETE CASCADE,
	PRIMARY KEY(faq_id,category_id)
);

CREATE TABLE IF NOT EXISTS ContactInCategories (
        contact_id    BIGINT UNSIGNED NOT NULL,
        category_id        BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(contact_id) REFERENCES Contacts(id) ON DELETE CASCADE,
	FOREIGN KEY(category_id)     REFERENCES Categories(id)    ON DELETE CASCADE,
	PRIMARY KEY(contact_id,category_id)
);

#TABLES for CEPHALIX
CREATE TABLE IF NOT EXISTS CephalixInstitutes (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       cn              VARCHAR(16) NOT NULL,
       name            VARCHAR(32) NOT NULL,
       type            VARCHAR(16) NOT NULL,
       domain          VARCHAR(32) NOT NULL,
       locality        VARCHAR(32) NOT NULL,
       state           VARCHAR(32) NOT NULL,
       adminPW         VARCHAR(16) NOT NULL,
       cephalixPW      VARCHAR(16) NOT NULL,
       ipVPN           VARCHAR(16) DEFAULT NULL,
       ipTrNet         VARCHAR(16) DEFAULT NULL,
       nmTrNet         VARCHAR(16) DEFAULT NULL,
       gwTrNet         VARCHAR(16) DEFAULT NULL,
       network         VARCHAR(16) NOT NULL,
       netmask         VARCHAR(16) NOT NULL,
       nmServerNet     VARCHAR(16) NOT NULL,
       ipAdmin         VARCHAR(16) NOT NULL,
       ipMail          VARCHAR(16) NOT NULL,
       ipPrint         VARCHAR(16) NOT NULL,
       ipProxy         VARCHAR(16) NOT NULL,
       ipBackup        VARCHAR(16) NOT NULL,
       anonDhcp        VARCHAR(32) NOT NULL,
       firstRoom       VARCHAR(16) NOT NULL,
       PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS CephalixITUsage (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       institute_id    BIGINT UNSIGNED NOT NULL,
       device          VARCHAR(32) NOT NULL,
       counter         BIGINT UNSIGNED NOT NULL,
       FOREIGN KEY(institute_id) REFERENCES CephalixInstitutes(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS CephalixITUsageAvarage (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       institute_id    BIGINT UNSIGNED NOT NULL,
       device          VARCHAR(32) NOT NULL,
       counter         BIGINT UNSIGNED NOT NULL,
       time            timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       counter0        BIGINT UNSIGNED NOT NULL,
       time0           timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
       avarage         BIGINT UNSIGNED NOT NULL,
       FOREIGN KEY(institute_id) REFERENCES CephalixInstitutes(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS CephalixMappings (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       institute_id    BIGINT UNSIGNED NOT NULL,
       objectName      VARCHAR(16)     NOT NULL,
       cephalixId      BIGINT UNSIGNED NOT NULL,
       ossId           BIGINT UNSIGNED DEFAULT NULL,
       FOREIGN KEY(institute_id) REFERENCES CephalixInstitutes(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Jobs (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       institute_id    BIGINT UNSIGNED DEFAULT NULL,
       description     VARCHAR(128)    NOT NULL,
       startTime       timestamp       NOT NULL DEFAULT '0000-00-00 00:00:00',
       FOREIGN KEY(institute_id) REFERENCES CephalixInstitutes(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
);

