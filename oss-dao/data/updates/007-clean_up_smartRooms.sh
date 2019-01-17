#!/bin/bash

for i in $( echo "SELECT Rooms.id FROM Rooms LEFT JOIN (RoomInCategories, GroupInCategories, Groups) ON ( RoomInCategories.room_id = Rooms.id AND RoomInCategories.category_id = GroupInCategories.category_id AND GroupInCategories.group_id = Groups.id ) where Groups.groupType='class';" |  mysql OSS; )
do
	if [ "$i" = "id" ]; then
		continue
	fi
	echo "DELETE FROM Rooms WHERE id = $i;" | mysql OSS
done

for i in $( echo "select c.id from Categories as c LEFT JOIN GroupInCategories as gc on gc.category_id = c.id LEFT JOIN Groups as g on gc.group_id =g.id  where c.categoryType='smartRoom' and g.groupType = 'class';" | mysql OSS )
do
	if [ "$i" = "id" ]; then
		continue
	fi
	echo "UPDATE Categories set categoryType='informations' WHERE id = $i;" | mysql OSS
done

