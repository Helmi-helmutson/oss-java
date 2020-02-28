#!/bin/bash
export HOME="/root"


DONE=$( echo "describe CephalixTickets" | mysql OSS | grep done | gawk '{ print $2 }'  )
if [ "$DONE" ]
then
	echo "alter table CephalixTickets change COLUMN done status char(1) default 'N';" | mysql OSS
fi
