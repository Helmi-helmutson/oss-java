#TODO TEST ONLY
DROP DATABASE OSS;

CREATE DATABASE OSS;
use OSS;

CREATE TABLE IF NOT EXISTS `Users` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`uid`		VARCHAR(32) NOT NULL,
	`role`		VARCHAR(16) NOT NULL,
	`sn`	        VARCHAR(64) NOT NULL,
	`givenName`	VARCHAR(64),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `Groups` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`cn`	        VARCHAR(32) NOT NULL,
	`description`	VARCHAR(64) NOT NULL,
	`type`		CHAR(1)     NOT NULL,
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `GroupMember` (
	`user_id`        INT UNSIGNED NOT NULL,
	`group_id`       INT UNSIGNED NOT NULL,
	CONSTRAINT fk_groupmember_user  FOREIGN KEY(user_id)  REFERENCES Users(`id`),
	CONSTRAINT fk_groupmember_group FOREIGN KEY(group_id) REFERENCES Groups(`id`),
	CONSTRAINT pk_groupmember PRIMARY KEY  (`user_id`,`group_id`)
);

CREATE TABLE IF NOT EXISTS `Aliases` (
	`id`	          INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`user_id`         INT UNSIGNED NOT NULL,
	`alias`	          VARCHAR(64) NOT NULL,
	CONSTRAINT fk_aliases_user  FOREIGN KEY(user_id)  REFERENCES Users(`id`),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `HWConfs` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`name`		VARCHAR(32) NOT NULL,
	`description`   VARCHAR(32) NOT NULL,
	`wstype`	VARCHAR(16) NOT NULL,
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `Partitions` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`hwconf_id`     INT UNSIGNED NOT NULL,
	`name`		VARCHAR(32) NOT NULL,
	`description`   VARCHAR(32) NOT NULL,
	`OS`		VARCHAR(16) NOT NULL,
	`join`		VARCHAR(16) NOT NULL,
	`tool`		VARCHAR(16) NOT NULL,
	`format`	VARCHAR(16) NOT NULL,
	CONSTRAINT fk_partitions_hwconfs FOREIGN KEY(hwconf_id) REFERENCES HWConfs(`id`),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `Rooms` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`hwconf_id`     INT UNSIGNED NOT NULL,
	`cn`		VARCHAR(32) NOT NULL,
	`description`	VARCHAR(64) NOT NULL,
	`roomtype`	VARCHAR(16) NOT NULL,
	`rows`		integer  DEFAULT 5,
	`columns`	integer  DEFAULT 5,
	`netMask`	integer  NOT NULL,
	CONSTRAINT fk_rooms_hwconfs FOREIGN KEY(hwconf_id) REFERENCES HWConfs(`id`),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `Devices` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`room_id`       INT UNSIGNED NOT NULL,
	`hwconf_id`     INT UNSIGNED NOT NULL,
	`owner_id`      INT UNSIGNED DEFAULT NULL,
	`cn`		VARCHAR(32) NOT NULL,
	`IP`		VARCHAR(16) NOT NULL,
	`MAC`		VARCHAR(17) NOT NULL,
	`wstype`	VARCHAR(16) NOT NULL,
	`row`		integer  DEFAULT 0,
	`column`	integer  DEFAULT 0,
	CONSTRAINT fk_devices_rooms   FOREIGN KEY(room_id)   REFERENCES Rooms(`id`),
	CONSTRAINT fk_devices_hwconfs FOREIGN KEY(hwconf_id) REFERENCES HWConfs(`id`),
	CONSTRAINT fk_devices_users   FOREIGN KEY(owner_id)  REFERENCES Users(`id`),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `AccessInRoom` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`room_id`       INT UNSIGNED NOT NULL,
	`default_access`	BOOLEAN DEFAULT 0,
	`moment`	TIME    DEFAULT '06:00',
	`all` 		BOOLEAN DEFAULT 0,
	`logon`		BOOLEAN DEFAULT 1,
	`proxy`		BOOLEAN DEFAULT 1,
	`mail`		BOOLEAN DEFAULT 1,
	CONSTRAINT fk_accessinroom_rooms FOREIGN KEY(room_id) REFERENCES Rooms(`id`),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `LoggedOn` (
	`user_id`        INT UNSIGNED NOT NULL,
	`device_id` INT UNSIGNED NOT NULL,
	CONSTRAINT fk_loggedon_users    FOREIGN KEY(user_id)   REFERENCES Users(`id`),
	CONSTRAINT fk_loggedon_devices  FOREIGN KEY(device_id) REFERENCES Devices(`id`),
	CONSTRAINT pk_LoggedOn PRIMARY KEY  (`device_id`, `user_id`)
);

CREATE TABLE IF NOT EXISTS `DefaultPrinters` (
	`room_id`    INT UNSIGNED DEFAULT NULL,
	`device_id`  INT UNSIGNED DEFAULT NULL,
	`printer_id` INT UNSIGNED NOT NULL,
	CONSTRAINT fk_defaultprinters_rooms     FOREIGN KEY(room_id)    REFERENCES Rooms(`id`),
	CONSTRAINT fk_defaultprinters_devices1  FOREIGN KEY(device_id)  REFERENCES Devices(`id`),
	CONSTRAINT fk_defaultprinters_devices2  FOREIGN KEY(printer_id) REFERENCES Devices(`id`),
	CONSTRAINT pk_DefaultPrinters PRIMARY KEY  (`device_id`, `room_id`)
);

CREATE TABLE IF NOT EXISTS `AvailablePrinters` (
	`id`         INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`room_id`    INT UNSIGNED DEFAULT NULL,
	`device_id`  INT UNSIGNED DEFAULT NULL,
	`printer_id` INT UNSIGNED NOT NULL,
	CONSTRAINT fk_availableprinters_rooms    FOREIGN KEY(room_id)    REFERENCES Rooms(`id`),
	CONSTRAINT fk_availableprinters_devices1 FOREIGN KEY(device_id)  REFERENCES Devices(`id`),
	CONSTRAINT fk_availableprinters_devices2 FOREIGN KEY(printer_id) REFERENCES Devices(`id`),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS `Acls` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`object_id`     INT UNSIGNED NOT NULL,
	`target_id`     INT UNSIGNED,
	`targetType`	VARCHAR(32)  NOT NULL,
	`acl`		VARCHAR(32)  NOT NULL,
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS  `Tests` (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`testName` 	VARCHAR(128) NOT NULL,
	`teacher_id`    INT UNSIGNED NOT NULL,
	`room_id`	INT UNSIGNED,
	`testDir`	VARCHAR(128) NOT NULL,
	`currentStep`	VARCHAR(128) NOT NULL,
	`startTime`	DATETIME NOT NULL,
	`endTime`	DATETIME NOT NULL,
	`windowsAccess` BOOLEAN NOT NULL,
	`proxyAccess`   BOOLEAN NOT NULL,
	`DirectInternetAccess` BOOLEAN NOT NULL,
	CONSTRAINT fk_tests_users FOREIGN KEY(teacher_id) REFERENCES Users(`id`),
	CONSTRAINT fk_tests_rooms FOREIGN KEY(room_id)    REFERENCES Rooms(`id`),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS TestFiles (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`test_id`	INT UNSIGNED NOT NULL,
	`user_id`	INT UNSIGNED NOT NULL,
	`getOrPost`	VARCHAR(128) NOT NULL,
      	`fileName`	VARCHAR(256) NOT NULL,
	`dateTime`	DATETIME NOT NULL,
	CONSTRAINT fk_testfiles_tests FOREIGN KEY(test_id) REFERENCES Tests(`id`),
	CONSTRAINT fk_testfiles_users FOREIGN KEY(user_id) REFERENCES Users(`id`),
	PRIMARY KEY  (`id`)
);

CREATE TABLE IF NOT EXISTS TestUsers (
	`test_id`	 INT UNSIGNED NOT NULL,
	`user_id`        INT UNSIGNED NOT NULL,
	`device_id` INT UNSIGNED,
	CONSTRAINT fk_testusers_tests FOREIGN KEY(test_id) REFERENCES Tests(`id`),
	CONSTRAINT fk_testusers_users FOREIGN KEY(user_id) REFERENCES Users(`id`),
	CONSTRAINT fk_testusers_devices FOREIGN KEY(device_id) REFERENCES Devices(`id`),
	CONSTRAINT pk_TestUser PRIMARY KEY (`test_id`,`user_id`)
);

CREATE TABLE IF NOT EXISTS Enumerates (
	`id`		INT UNSIGNED NOT NULL AUTO_INCREMENT,
	`name`		VARCHAR(32)  NOT NULL,
	`value`		VARCHAR(32)  NOT NULL,
	PRIMARY KEY  (`id`)
);


#Create some standard entries


INSERT INTO `Enumerates` VALUES(NULL,'device_type','FatClient');
INSERT INTO `Enumerates` VALUES(NULL,'device_type','ThinClient');
INSERT INTO `Enumerates` VALUES(NULL,'device_type','Printer');
INSERT INTO `Enumerates` VALUES(NULL,'device_type','Switch');
INSERT INTO `Enumerates` VALUES(NULL,'device_type','Router');
INSERT INTO `Enumerates` VALUES(NULL,'role','students');
INSERT INTO `Enumerates` VALUES(NULL,'role','teachers');
INSERT INTO `Enumerates` VALUES(NULL,'role','sysadmins');
INSERT INTO `Enumerates` VALUES(NULL,'role','administration');
INSERT INTO `Enumerates` VALUES(NULL,'role','workstations');
INSERT INTO `Enumerates` VALUES(NULL,'group_type','primary');
INSERT INTO `Enumerates` VALUES(NULL,'group_type','class');
INSERT INTO `Enumerates` VALUES(NULL,'group_type','workgroup');
INSERT INTO `Enumerates` VALUES(NULL,'group_type','guest');


