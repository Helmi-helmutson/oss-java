#!/bin/bash
export HOME="/root"



TYPE=$( echo "describe CephalixInstitutes" | mysql OSS | grep type | gawk '{ print $2 }'  )
if [ "$TYPE" ]
then
	echo "alter table CephalixInstitutes change COLUMN type instituteType VARCHAR(16) default 'school';" | mysql OSS
fi

NETWORK=$( echo "describe CephalixInstitutes" | mysql OSS | grep network | gawk '{ print $2 }'  )
if [ "$TYPE" ]
then
	echo "alter table CephalixInstitutes change COLUMN network internalNetwork VARCHAR(32) default NULL;" | mysql OSS
fi

STATUS=$( echo "describe CephalixInstitutes" | mysql OSS | grep status | gawk '{ print $2 }'  )
if [ "$TYPE" ]
then
	echo "alter table CephalixInstitutes change COLUMN status instituteStatus enum('PROD','TEST','FREE','DEVEL');" | mysql OSS
fi

NETWOR=$( echo "describe CephalixInstitutes" | mysql OSS | grep network | gawk '{ print $2 }'  )
if [ "$NETWORK" ]
then
	echo "alter table CephalixInstitutes change COLUMN netowrk internalNetwork VARCHAR(32) default NULL;" | mysql OSS
fi

TYPE=$( echo "describe CephalixNotices" | mysql OSS | grep type | gawk '{ print $2 }'  )
if [ "$TYPE" ]
then
	echo "alter table CephalixNotices change COLUMN type noticeType VARCHAR(16);" | mysql OSS
fi

DONE=$( echo "describe CephalixTickets" | mysql OSS | grep done | gawk '{ print $2 }'  )
if [ "$DONE" ]
then
	echo "alter table CephalixTickets change COLUMN done ticketStatus char(1) default 'N';" | mysql OSS
fi
STATUS=$( echo "describe CephalixTickets" | mysql OSS | grep status | gawk '{ print $2 }'  )
if [ "$STATUS" ]
then
	echo "alter table CephalixTickets change COLUMN status ticketStatus CHAR(1) DEFAULT 'N';" | mysql OSS
fi

TYPE=$( echo "describe CephalixTickets" | mysql OSS | grep type | gawk '{ print $2 }'  )
if [ "$TYPE" ]
then
	echo "alter table CephalixTickets change COLUMN type ticketType VARCHAR(16) default 'Error';" | mysql OSS
fi

TYPE=$( echo "describe CephalixArticles" | mysql OSS | grep type | gawk '{ print $2 }'  )
if [ "$TYPE" ]
then
	echo "alter table CephalixArticles change COLUMN type articleType CHAR(1) DEFAULT 'I';" | mysql OSS
fi

TYPE=$( echo "describe CephalixRepositories" | mysql OSS | grep type | gawk '{ print $2 }'  )
if [ "$TYPE" ]
then
	echo "alter table CephalixRepositories change COLUMN type repositoryType VARCHAR(16);" | mysql OSS
fi

TYPE=$( echo "describe CephalixOssCareMessages" | mysql OSS | grep type | gawk '{ print $2 }'  )
if [ "$TYPE" ]
then
	echo "alter table CephalixOssCareMessages change COLUMN type careMessageType VARCHAR(16);" | mysql OSS
fi

