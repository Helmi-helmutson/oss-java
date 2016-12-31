#TODO TEST ONLY
DROP DATABASE OSS;

CREATE DATABASE OSS;
use OSS;

CREATE TABLE IF NOT EXISTS Users (
        id           INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        uid          VARCHAR(32) NOT NULL,
        role         VARCHAR(16) NOT NULL,
        surName      VARCHAR(64) NOT NULL,
        givenName    VARCHAR(64),
        PRIMARY KEY  (id)
);

INSERT INTO Users VALUES(NULL,'admin','sysadmins','Administrator',NULL);
INSERT INTO Users VALUES(NULL,'tteachers','teachers','TTeachers',NULL);
INSERT INTO Users VALUES(NULL,'tstudents','tstudents','TStudents',NULL);
INSERT INTO Users VALUES(NULL,'tworkstations','sysadmins','TSysadmins',NULL);

CREATE TABLE IF NOT EXISTS Groups (
        id          INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        name        VARCHAR(32) NOT NULL,
        description VARCHAR(64) NOT NULL,
        groupType   CHAR(1)     NOT NULL,
        PRIMARY KEY  (id)
);

INSERT INTO Groups VALUES(NULL,'sysadmins','Sysadmins','primary');
INSERT INTO Groups VALUES(NULL,'teachers','Teachers','primary');
INSERT INTO Groups VALUES(NULL,'students','Students','primary');
INSERT INTO Groups VALUES(NULL,'administration','Administration','primary');
INSERT INTO Groups VALUES(NULL,'workstations','Workstations','primary');
INSERT INTO Groups VALUES(NULL,'templates','Templates','primary');

CREATE TABLE IF NOT EXISTS GroupMember (
        user_id        INTEGER UNSIGNED NOT NULL,
        group_id       INTEGER UNSIGNED NOT NULL,
        CONSTRAINTEGER fk_groupmember_user  FOREIGN KEY(user_id)  REFERENCES Users(id),
        CONSTRAINTEGER fk_groupmember_group FOREIGN KEY(group_id) REFERENCES Groups(id),
        CONSTRAINTEGER pk_groupmember PRIMARY KEY  (user_id,group_id)
);

CREATE TABLE IF NOT EXISTS Aliases (
        id              INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        user_id         INTEGER UNSIGNED NOT NULL,
        alias           VARCHAR(64) NOT NULL,
        CONSTRAINTEGER fk_aliases_user  FOREIGN KEY(user_id)  REFERENCES Users(id),
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS HWConfs (
        id            INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        name          VARCHAR(32) NOT NULL,
        description   VARCHAR(32) NOT NULL,
        deviceType    VARCHAR(16) NOT NULL,
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS Partitions (
        id           INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        hwconf_id    INTEGER UNSIGNED NOT NULL,
        name         VARCHAR(32) NOT NULL,
        description  VARCHAR(32) NOT NULL,
        OS           VARCHAR(16) NOT NULL,
        join         VARCHAR(16) NOT NULL,
        tool         VARCHAR(16) NOT NULL,
        format       VARCHAR(16) NOT NULL,
        CONSTRAINTEGER fk_partitions_hwconfs FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id),
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS Rooms (
        id           INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        hwconf_id    INTEGER UNSIGNED NOT NULL,
        name         VARCHAR(32) NOT NULL,
        description  VARCHAR(64) NOT NULL,
        roomType     VARCHAR(16) NOT NULL,
        rows         INTEGER  DEFAULT 5,
        columns      INTEGER  DEFAULT 5,
        startIP      VARCHAR(16) NOT NULL,
        netMask      INTEGER  NOT NULL,
        CONSTRAINTEGER fk_rooms_hwconfs FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id),
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS Devices (
        id           INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      INTEGER UNSIGNED NOT NULL,
        hwconf_id    INTEGER UNSIGNED DEFAULT NULL,
        owner_id     INTEGER UNSIGNED DEFAULT NULL,
        name         VARCHAR(32) NOT NULL,
        IP           VARCHAR(16) NOT NULL,
        WLANIP       VARCHAR(16) DEFAULT '',
        MAC          VARCHAR(17) NOT NULL,
        WLANMAC      VARCHAR(17) DEFAULT '',
        deviceType   VARCHAR(16) NOT NULL,
        row          INTEGER  DEFAULT 0,
        column       INTEGER  DEFAULT 0,
        CONSTRAINTEGER fk_devices_rooms   FOREIGN KEY(room_id)   REFERENCES Rooms(id),
        CONSTRAINTEGER fk_devices_hwconfs FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id),
        CONSTRAINTEGER fk_devices_users   FOREIGN KEY(owner_id)  REFERENCES Users(id),
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS AccessInRoom (
        id             INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id        INTEGER UNSIGNED NOT NULL,
        defaultAccess  BOOLEAN DEFAULT 0,
        pointInTime    TIME    DEFAULT '06:00',
        direct         BOOLEAN DEFAULT 0,
        logon          BOOLEAN DEFAULT 1,
        proxy          BOOLEAN DEFAULT 1,
        pronting       BOOLEAN DEFAULT 1,
        portal         BOOLEAN DEFAULT 1,
        CONSTRAINTEGER fk_accessinroom_rooms FOREIGN KEY(room_id) REFERENCES Rooms(id),
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS LoggedOn (
        user_id      INTEGER UNSIGNED NOT NULL,
        device_id    INTEGER UNSIGNED NOT NULL,
        CONSTRAINTEGER fk_loggedon_users    FOREIGN KEY(user_id)   REFERENCES Users(id),
        CONSTRAINTEGER fk_loggedon_devices  FOREIGN KEY(device_id) REFERENCES Devices(id),
        CONSTRAINTEGER pk_LoggedOn PRIMARY KEY  (device_id, user_id)
);

CREATE TABLE IF NOT EXISTS DefaultPrinters (
        room_id      INTEGER UNSIGNED DEFAULT NULL,
        device_id    INTEGER UNSIGNED DEFAULT NULL,
        printer_id   INTEGER UNSIGNED NOT NULL,
        CONSTRAINTEGER fk_defaultprinters_rooms     FOREIGN KEY(room_id)    REFERENCES Rooms(id),
        CONSTRAINTEGER fk_defaultprinters_devices1  FOREIGN KEY(device_id)  REFERENCES Devices(id),
        CONSTRAINTEGER fk_defaultprinters_devices2  FOREIGN KEY(printer_id) REFERENCES Devices(id),
        CONSTRAINTEGER pk_DefaultPrinters PRIMARY KEY  (device_id, room_id)
);

CREATE TABLE IF NOT EXISTS AvailablePrinters (
        id           INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      INTEGER UNSIGNED DEFAULT NULL,
        device_id    INTEGER UNSIGNED DEFAULT NULL,
        printer_id   INTEGER UNSIGNED NOT NULL,
        CONSTRAINTEGER fk_availableprinters_rooms    FOREIGN KEY(room_id)    REFERENCES Rooms(id),
        CONSTRAINTEGER fk_availableprinters_devices1 FOREIGN KEY(device_id)  REFERENCES Devices(id),
        CONSTRAINTEGER fk_availableprinters_devices2 FOREIGN KEY(printer_id) REFERENCES Devices(id),
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS Acls (
        id           INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        object_id    INTEGER UNSIGNED NOT NULL,
        target_id    INTEGER UNSIGNED,
        targetType   VARCHAR(32)  NOT NULL,
        acl          VARCHAR(32)  NOT NULL,
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS  Tests (
        id            INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        testName      VARCHAR(128) NOT NULL,
        teacher_id    INTEGER UNSIGNED NOT NULL,
        room_id       INTEGER UNSIGNED,
        testDir       VARCHAR(128) NOT NULL,
        currentStep   VARCHAR(128) NOT NULL,
        startTime     DATETIME NOT NULL,
        endTime       DATETIME NOT NULL,
        windowsAccess BOOLEAN NOT NULL,
        proxyAccess   BOOLEAN NOT NULL,
        DirectInternetAccess BOOLEAN NOT NULL,
        CONSTRAINTEGER fk_tests_users FOREIGN KEY(teacher_id) REFERENCES Users(id),
        CONSTRAINTEGER fk_tests_rooms FOREIGN KEY(room_id)    REFERENCES Rooms(id),
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS TestFiles (
        id           INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        test_id      INTEGER UNSIGNED NOT NULL,
        user_id      INTEGER UNSIGNED NOT NULL,
        getOrPost    VARCHAR(128) NOT NULL,
        fileName     VARCHAR(256) NOT NULL,
        dateTime     DATETIME NOT NULL,
        CONSTRAINTEGER fk_testfiles_tests FOREIGN KEY(test_id) REFERENCES Tests(id),
        CONSTRAINTEGER fk_testfiles_users FOREIGN KEY(user_id) REFERENCES Users(id),
        PRIMARY KEY  (id)
);

CREATE TABLE IF NOT EXISTS TestUsers (
        test_id      INTEGER UNSIGNED NOT NULL,
        user_id      INTEGER UNSIGNED NOT NULL,
        device_id    INTEGER UNSIGNED,
        CONSTRAINTEGER fk_testusers_tests FOREIGN KEY(test_id) REFERENCES Tests(id),
        CONSTRAINTEGER fk_testusers_users FOREIGN KEY(user_id) REFERENCES Users(id),
        CONSTRAINTEGER fk_testusers_devices FOREIGN KEY(device_id) REFERENCES Devices(id),
        CONSTRAINTEGER pk_TestUser PRIMARY KEY (test_id,user_id)
);

CREATE TABLE IF NOT EXISTS Enumerates (
        id           INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
        name         VARCHAR(32)  NOT NULL,
        value        VARCHAR(32)  NOT NULL,
        PRIMARY KEY  (id)
);

INSERT INTO Enumerates VALUES(NULL,'deviceType','FatClient');
INSERT INTO Enumerates VALUES(NULL,'deviceType','ThinClient');
INSERT INTO Enumerates VALUES(NULL,'deviceType','Printer');
INSERT INTO Enumerates VALUES(NULL,'deviceType','Switch');
INSERT INTO Enumerates VALUES(NULL,'deviceType','Router');
INSERT INTO Enumerates VALUES(NULL,'role','students');
INSERT INTO Enumerates VALUES(NULL,'role','teachers');
INSERT INTO Enumerates VALUES(NULL,'role','sysadmins');
INSERT INTO Enumerates VALUES(NULL,'role','administration');
INSERT INTO Enumerates VALUES(NULL,'role','workstations');
INSERT INTO Enumerates VALUES(NULL,'groupType','primary');
INSERT INTO Enumerates VALUES(NULL,'groupType','class');
INSERT INTO Enumerates VALUES(NULL,'groupType','workgroup');
INSERT INTO Enumerates VALUES(NULL,'groupType','guest');
INSERT INTO Enumerates VALUES(NULL,'groupType,'ClassRoom');
INSERT INTO Enumerates VALUES(NULL,'groupType,'ComputerRoom');
INSERT INTO Enumerates VALUES(NULL,'groupType,'Library');
INSERT INTO Enumerates VALUES(NULL,'groupType,'Laboratory');


#Some additional config tables
CREATE TABLE IF NOT EXISTS UserConfig (
        user_id      INTEGER UNSIGNED NOT NULL,
        key          VARCHAR(256) NOT NULL,
        value        VARCHAR(256) NOT NULL,
        CONSTRAINTEGER pk_userconfig PRIMARY KEY  (user_id,key,value)
);

CREATE TABLE IF NOT EXISTS UserMConfig (
        user_id      INTEGER UNSIGNED NOT NULL,
        key          VARCHAR(256) NOT NULL,
        value        VARCHAR(256) NOT NULL,
        CONSTRAINTEGER pk_usermconfig PRIMARY KEY  (user_id,key)
);

CREATE TABLE IF NOT EXISTS GroupConfig (
        group_id     INTEGER UNSIGNED NOT NULL,
        key          VARCHAR(256) NOT NULL,
        value        VARCHAR(256) NOT NULL,
        CONSTRAINTEGER pk_groupconfig PRIMARY KEY  (group_id,key,value)
);

CREATE TABLE IF NOT EXISTS GroupMConfig (
        group_id     INTEGER UNSIGNED NOT NULL,
        key          VARCHAR(256) NOT NULL,
        value        VARCHAR(256) NOT NULL,
        CONSTRAINTEGER pk_groupmconfig PRIMARY KEY  (group_id,key)
);

CREATE TABLE IF NOT EXISTS DeviceConfig (
        device_id    INTEGER UNSIGNED NOT NULL,
        key          VARCHAR(256) NOT NULL,
        value        VARCHAR(256) NOT NULL,
        CONSTRAINTEGER pk_deviceconfig PRIMARY KEY  (device_id,key,value)
);

CREATE TABLE IF NOT EXISTS DeviceMConfig (
        device_id    INTEGER UNSIGNED NOT NULL,
        key          VARCHAR(256) NOT NULL,
        value        VARCHAR(256) NOT NULL,
        CONSTRAINTEGER pk_devicemconfig PRIMARY KEY  (workstation_id,key)
);

CREATE TABLE IF NOT EXISTS RoomConfig (
        room_id      INTEGER UNSIGNED NOT NULL,
        key          VARCHAR(256) NOT NULL,
        value        VARCHAR(256) NOT NULL,
        CONSTRAINTEGER pk_roomconfig PRIMARY KEY  (room_id,key,value)
);

CREATE TABLE IF NOT EXISTS RoomMConfig (
        room_id      INTEGER UNSIGNED NOT NULL,
        key          VARCHAR(256) NOT NULL,
        value        VARCHAR(256) NOT NULL,
        CONSTRAINTEGER pk_roommconfig PRIMARY KEY  (room_id,key)
);


