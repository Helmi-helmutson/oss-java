USE OSS;

CREATE TABLE IF NOT EXISTS CephalixCustomers (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        recDate      TIMESTAMP,
        name         VARCHAR(50),
        name2        VARCHAR(50),
        description  VARCHAR(128),
        category     VARCHAR(10),
        address1     VARCHAR(50),
        address2     VARCHAR(50),
        city         VARCHAR(50),
        state        VARCHAR(8),
        postalCode   VARCHAR(10),
        country      VARCHAR(25),
        contact      VARCHAR(50),
        telephone    VARCHAR(15),
        iban         VARCHAR(35),
        bic          VARCHAR(12),
	deleted      CHAR(1) DEFAULT 'N',
        PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;


CREATE TABLE IF NOT EXISTS CephalixInstitutes (
	id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	uuid            VARCHAR(16) NOT NULL,
	name            VARCHAR(50) NOT NULL,
	type            VARCHAR(16) NOT NULL,
	domain          VARCHAR(32) DEFAULT NULL,
	locality        VARCHAR(32) DEFAULT NULL,
	state           VARCHAR(32) DEFAULT NULL,
	adminPW         VARCHAR(16) DEFAULT NULL,
	cephalixPW      VARCHAR(16) DEFAULT NULL,
	ipVPN           VARCHAR(16) DEFAULT NULL,
	ipTrNet         VARCHAR(16) DEFAULT NULL,
	nmTrNet         VARCHAR(16) DEFAULT NULL,
	gwTrNet         VARCHAR(16) DEFAULT NULL,
	network         VARCHAR(16) DEFAULT NULL,
	netmask         VARCHAR(16) DEFAULT NULL,
	nmServerNet     VARCHAR(16) DEFAULT NULL,
	ipAdmin         VARCHAR(16) DEFAULT NULL,
	ipMail          VARCHAR(16) DEFAULT NULL,
	ipPrint         VARCHAR(16) DEFAULT NULL,
	ipProxy         VARCHAR(16) DEFAULT NULL,
	ipBackup        VARCHAR(16) DEFAULT NULL,
	anonDhcp        VARCHAR(32) DEFAULT NULL,
	firstRoom       VARCHAR(16) DEFAULT NULL,
	recDate         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deleted         CHAR(1) DEFAULT 'N',
	cephalixcustomer_id     BIGINT UNSIGNED DEFAULT NULL,
	FOREIGN KEY(cephalixcustomer_id)  REFERENCES CephalixCustomers(id),
	PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;

CREATE TABLE IF NOT EXISTS CephalixRegcodes (
        id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
        name         VARCHAR(32) NOT NULL,
        description  VARCHAR(32) DEFAULT NULL,
        recDate      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
        validity     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
        status       enum('PROD','TEST','FREE','DEVEL'),
        cephalixinstitute_id               BIGINT UNSIGNED DEFAULT NULL,
        FOREIGN KEY(cephalixinstitute_id)  REFERENCES CephalixInstitutes(id),
        PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;


CREATE TABLE IF NOT EXISTS CephalixDynDns (
	id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	cephalixregcode_id      BIGINT UNSIGNED NOT NULL,
	hostname        VARCHAR(32)     DEFAULT NULL,
	domain          VARCHAR(32)     DEFAULT NULL,
	ip              VARCHAR(40)     NOT NULL,
	port		INTEGER		DEFAULT 22,
	ts              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
	ro              CHAR(1) DEFAULT 'N',
	FOREIGN KEY(cephalixregcode_id) REFERENCES CephalixRegcodes(id) ON DELETE CASCADE,
	PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;

CREATE TABLE IF NOT EXISTS CephalixOssCares (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       regcode_id      BIGINT UNSIGNED NOT NULL,
       description     VARCHAR(128)    DEFAULT NULL,
       access          VARCHAR(128)    NOT NULL,
       contact         VARCHAR(256)    NOT NULL,
       recDate         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
       validity        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY(regcode_id) REFERENCES CephalixRegcodes(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;

CREATE TABLE IF NOT EXISTS CephalixOssCareMessages (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       cephalixosscare_id      BIGINT UNSIGNED NOT NULL,
       recDate         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
       type            enum('WARNING','REPORT')    NOT NULL,
       description     VARCHAR(128)    NOT NULL,
       text            BLOB            NOT NULL,
       FOREIGN KEY(cephalixosscare_id) REFERENCES CephalixOssCares(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;

CREATE TABLE IF NOT EXISTS CephalixITUsage (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       cephalixinstitute_id    BIGINT UNSIGNED NOT NULL,
       device          VARCHAR(32) NOT NULL,
       counter         BIGINT UNSIGNED NOT NULL,
       FOREIGN KEY(cephalixinstitute_id) REFERENCES CephalixInstitutes(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;

CREATE TABLE IF NOT EXISTS CephalixITUsageAvarage (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       cephalixinstitute_id    BIGINT UNSIGNED NOT NULL,
       device          VARCHAR(32) NOT NULL,
       counter         BIGINT UNSIGNED NOT NULL,
       time            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       counter0        BIGINT UNSIGNED NOT NULL,
       time0           TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
       avarage         BIGINT UNSIGNED NOT NULL,
       FOREIGN KEY(cephalixinstitute_id) REFERENCES CephalixInstitutes(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;

CREATE TABLE IF NOT EXISTS CephalixMappings (
       id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
       cephalixinstitute_id    BIGINT UNSIGNED NOT NULL,
       objectName      VARCHAR(16)     NOT NULL,
       cephalixId      BIGINT UNSIGNED NOT NULL,
       ossId           BIGINT UNSIGNED DEFAULT NULL,
       FOREIGN KEY(cephalixinstitute_id) REFERENCES CephalixInstitutes(id) ON DELETE CASCADE,
       PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci ;

