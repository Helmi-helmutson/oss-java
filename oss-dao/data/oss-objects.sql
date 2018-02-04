#TEST ONLY
#DROP DATABASE OSS;

CREATE DATABASE OSS;
USE OSS;

CREATE TABLE IF NOT EXISTS Translations(
        id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	lang	VARCHAR(2)   NOT NULL DEFAULT 'DE',
	string  VARCHAR(512) NOT NULL,
        value   VARCHAR(512),
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
        surName      VARCHAR(64) NOT NULL,
        givenName    VARCHAR(64),
        birthDay     DATE NOT NULL,
	fsQuota	     INTEGER DEFAULT 0,
	fsQuotaUsed  INTEGER DEFAULT 0,
	msQuota	     INTEGER DEFAULT 0,
	msQuotaUsed  INTEGER DEFAULT 0,
	initialPassword  VARCHAR(32) DEFAULT '',
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Groups (
        id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        name        VARCHAR(32) NOT NULL,
        description VARCHAR(64) NOT NULL,
        groupType   VARCHAR(16) NOT NULL,
        owner_id    BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(owner_id)  REFERENCES Users(id),
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS GroupMember (
        user_id        BIGINT UNSIGNED NOT NULL,
        group_id       BIGINT UNSIGNED NOT NULL,
	FOREIGN KEY(user_id)  REFERENCES Users(id)  ON DELETE CASCADE,
	FOREIGN KEY(group_id) REFERENCES Groups(id) ON DELETE CASCADE,
	PRIMARY KEY(user_id,group_id)
);

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

CREATE TABLE IF NOT EXISTS Devices (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id      BIGINT UNSIGNED NOT NULL,
        hwconf_id    BIGINT UNSIGNED DEFAULT NULL,
        owner_id     BIGINT UNSIGNED DEFAULT NULL,
        name         VARCHAR(32) NOT NULL,
        IP           VARCHAR(16) NOT NULL,
        wlanIp       VARCHAR(16) DEFAULT '',
        MAC          VARCHAR(17) DEFAULT '',
        wlanMac      VARCHAR(17) DEFAULT '',
        row          INTEGER  DEFAULT 0,
        place        INTEGER  DEFAULT 0,
	serial       VARCHAR(16) DEFAULT '',
	inventary    VARCHAR(16) DEFAULT '',
	locality     VARCHAR(16) DEFAULT '',
        FOREIGN KEY(room_id)   REFERENCES Rooms(id)   ON DELETE RESTRICT,
        FOREIGN KEY(hwconf_id) REFERENCES HWConfs(id) ON DELETE RESTRICT,
        FOREIGN KEY(owner_id)  REFERENCES Users(id)   ON DELETE CASCADE,
        PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS AccessInRooms (
        id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        room_id        BIGINT UNSIGNED NOT NULL,
        accessType     VARCHAR(3) NOT NULL,
        pointInTime    CHAR(5) DEFAULT '06:00',
	monday         CHAR(1) DEFAULT 'Y',
	tuesday        CHAR(1) DEFAULT 'Y',
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
        acl          VARCHAR(32) NOT NULL,
        allowed      CHAR(1) NOT NULL DEFAULT 'Y',
        creator_id   BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(creator_id)  REFERENCES Users(id),
        FOREIGN KEY(user_id)  REFERENCES Users(id)  ON DELETE CASCADE,
        FOREIGN KEY(group_id) REFERENCES Groups(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

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

# status C -> current this is the most recent version and does exists on the server and can be installed
# status R -> replaced this version does not exists on the server but is installed on some clients
# status D -> this is an older version which does exists on the server and can be installed
CREATE TABLE IF NOT EXISTS SoftwareVersions (
        id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        software_id    BIGINT UNSIGNED,
	version        VARCHAR(32) NOT NULL,
	status         VARCHAR(1)  NOT NULL,
        FOREIGN KEY(software_id)    REFERENCES Softwares(id) ON DELETE CASCADE,
        PRIMARY KEY(id)
);

# status I  -> installed
# status IS -> installation scheduled
# status US -> update scheduled
# status MD -> manuell deinstalled
# status DS -> deinstallation scheduled
# status DF -> deinstallation failed
# status IF -> installation failed
# status FR -> installed version is frozen: This must not be updated.
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

CREATE TABLE IF NOT EXISTS PositiveList (
	id BIGINT   UNSIGNED NOT NULL AUTO_INCREMENT,
        owner_id    BIGINT UNSIGNED DEFAULT NULL,
        name        VARCHAR(32) NOT NULL,
        subject     VARCHAR(32) NOT NULL,
        description VARCHAR(64) NOT NULL,
	FOREIGN KEY(owner_id)  REFERENCES Users(id) ON DELETE SET NULL,
        PRIMARY KEY(id)
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
       description     VARCHAR(128)    NOT NULL,
       startTime       timestamp       NOT NULL DEFAULT '0000-00-00 00:00:00',
       endTime         timestamp       DEFAULT NULL,
       exitCode        INT             DEFAULT NULL,
       PRIMARY KEY(id)
);

