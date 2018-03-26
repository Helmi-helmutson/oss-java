INSERT INTO Users VALUES(1,'Administrator','','sysadmins','Administrator','Main',NOW(),0,0,0,0,'',NULL);
INSERT INTO Users VALUES(2,'tteachers','','teachers','for teachers','Default profile',NOW(),0,0,0,0,'',1);
INSERT INTO Users VALUES(3,'tstudents','','students','for students','Default profile',NOW(),0,0,0,0,'',1);
INSERT INTO Users VALUES(4,'tadministration','','administration','for administration','Default profile',NOW(),0,0,0,0,'',1);
INSERT INTO Users VALUES(5,'tworkstations','','workstations','for workstations','Default profile',NOW(),0,0,0,0,'',1);
INSERT INTO Users VALUES(6,'cephalix','','sysadmins','Administrator','Internal',NOW(),0,0,0,0,'',1);
INSERT INTO Users VALUES(7,'register','','register','Register','Internal',NOW(),0,0,0,0,'',1);
INSERT INTO Groups VALUES(1,'sysadmins','Sysadmins','primary',1);
INSERT INTO Groups VALUES(2,'teachers','Teachers','primary',1);
INSERT INTO Groups VALUES(3,'students','Students','primary',1);
INSERT INTO Groups VALUES(4,'administration','Administration','primary',1);
INSERT INTO Groups VALUES(5,'workstations','Workstations','primary',1);
INSERT INTO Groups VALUES(6,'templates','Templates','primary',1);
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
INSERT INTO HWConfs VALUES(1,"Server","","Server",1);
INSERT INTO HWConfs VALUES(2,"Printer","","Printer",1);
INSERT INTO HWConfs VALUES(3,"BYOD","Privat Devices","BYOD",1);
INSERT INTO HWConfs VALUES(4,'Win10-64-SimpleJoin','Win10 64Bit Simple Join','FatClient',1);
INSERT INTO Partitions values(1,4,'sda1','Boot','WinBoot','no','partimage',NULL,1);
INSERT INTO Partitions values(2,4,'sda2','System','Win10','Simple','partimage',NULL,1);
INSERT INTO HWConfs VALUES(5,'Win10-64-DomainJoin','For devices prepared by sysprep.','FatClient',1);
INSERT INTO Partitions values(3,5,'sda1','Boot','WinBoot','no','partimage',NULL,1);
INSERT INTO Partitions values(4,5,'sda2','System','Win10','Domain','partimage',NULL,1);
INSERT INTO HWConfs VALUES(6,'Win10-64-No-Join','Win10 64Bit No Join','FatClient',1);
INSERT INTO Partitions values(5,6,'sda1','Boot','WinBoot','no','partimage',NULL,1);
INSERT INTO Partitions values(6,6,'sda2','System','Win10','no','partimage',NULL,1);
INSERT INTO Rooms VALUES(1,1,'SERVER_NET','Virtual room for servers','technicalRoom','no',10,10,'#SERVER_NETWORK#',#SERVER_NETMASK#,6);
INSERT INTO Rooms VALUES(2,NULL,'ANON_DHCP','Virtual room for unknown devices','technicalRoom','no',10,10,'#ANON_NETWORK#',#ANON_NETMASK#,6);
INSERT INTO Devices VALUES(1,1,1,6,'#SCHOOL_NETBIOSNAME#','#SCHOOL_SERVER#',NULL,'','',0,0,'','','',0);
INSERT INTO Devices VALUES(2,1,1,6,'schoolserver','#SCHOOL_MAILSERVER#',NULL,'','',0,0,'','','',0);
INSERT INTO Devices VALUES(3,1,1,6,'proxy','#SCHOOL_PROXY#',NULL,'','',0,0,'','','',0);
INSERT INTO Devices VALUES(4,1,1,6,'printserver','#SCHOOL_PRINTSERVER#',NULL,'','',0,0,'','','',0);
INSERT INTO Devices VALUES(5,1,1,6,'backup','#SCHOOL_BACKUP_SERVER#',NULL,'','',0,0,'','','',0);
INSERT INTO Devices VALUES(6,1,1,6,'install','#SCHOOL_SERVER#',NULL,'','',0,0,'','','',0);
INSERT INTO Devices VALUES(7,1,1,6,'timeserver','#SCHOOL_SERVER#',NULL,'','',0,0,'','','',0);
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
INSERT INTO Enumerates VALUES(NULL,'roomControl','allTeachers',1);
INSERT INTO Enumerates VALUES(NULL,'roomControl','teachers',1);
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
INSERT INTO Enumerates VALUES(NULL,'network','#SCHOOL_NETWORK#/#SCHOOL_NETMASK#',1);
#Categories
INSERT INTO Categories Values(1, 'Announcements for all','','announcements','1','N',NOW(),NULL);
INSERT INTO Categories Values(2, 'Announcements for sysadmins','','announcements','1','N',NOW(),NULL);
INSERT INTO Categories Values(3, 'Announcements for teachers','','announcements','1','N',NOW(),NULL);
INSERT INTO Categories Values(4, 'Announcements for students','','announcements','1','N',NOW(),NULL);
INSERT INTO Categories Values(5, 'Announcements for administration','','announcements','1','N',NOW(),NULL);
INSERT INTO Categories Values(6, 'Contacts for all','','contacts','1','N',NOW(),NULL);
INSERT INTO Categories Values(7, 'Contacts for sysadmins','','contacts','1','N',NOW(),NULL);
INSERT INTO Categories Values(8, 'Contacts for teachers','','contacts','1','N',NOW(),NULL);
INSERT INTO Categories Values(9, 'Contacts for students','','contacts','1','N',NOW(),NULL);
INSERT INTO Categories Values(10,'Contacts for administration','','contacts','1','N',NOW(),NULL);
INSERT INTO Categories Values(11,'FAQs for all','','faqs','1','N',NOW(),NULL);
INSERT INTO Categories Values(12,'FAQs for sysadmins','','faqs','1','N',NOW(),NULL);
INSERT INTO Categories Values(13,'FAQs for teachers','','faqs','1','N',NOW(),NULL);
INSERT INTO Categories Values(14,'FAQs for students','','faqs','1','N',NOW(),NULL);
INSERT INTO Categories Values(15,'FAQs for administration','','faqs','1','N',NOW(),NULL);
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

#Acls
INSERT INTO Enumerates VALUES(NULL,'apiAcl','adhoclan.manage',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','adhoclan.search',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','category.add',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','category.delete',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','category.modify',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','category.search',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','device.add',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','device.delete',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','device.manage',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','device.modify',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','device.search',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','education.groups',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','education.guestusers',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','education.proxy',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','education.rooms',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','education.users',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','group.add',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','group.delete',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','group.manage',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','group.modify',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','group.search',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','hwconf.add',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','hwconf.manage',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','hwconf.modify',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','information.add',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','information.delete',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','myself.modify',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','myself.search',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','printers.manage',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','room.add',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','room.delete',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','room.manage',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','room.modify',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','room.search',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','software.add',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','software.delete',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','software.download',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','software.install',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','software.modify',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','software.search',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','sysadmins.translation',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.acls',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.configuration',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.configuration.read',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.enumerates',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.firewall',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.jobs',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.packages',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.proxy',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.register',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.status',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','system.update',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','user.add',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','user.delete',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','user.guestusers',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','user.manage',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','user.modify',6);
INSERT INTO Enumerates VALUES(NULL,'apiAcl','user.search',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'adhoclan.manage','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'adhoclan.search','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'category.add','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'category.delete','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'category.modify','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'category.search','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'device.add','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'device.delete','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'device.manage','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'device.modify','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'device.search','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'education.groups','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'education.guestusers','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'education.proxy','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'education.rooms','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'education.users','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'group.add','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'group.delete','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'group.manage','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'group.modify','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'group.search','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'hwconf.add','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'hwconf.manage','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'hwconf.modify','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'information.add','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'information.delete','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'myself.modify','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'myself.search','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'printers.manage','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'room.add','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'room.delete','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'room.manage','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'room.modify','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'room.search','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'software.add','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'software.delete','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'software.download','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'software.install','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'software.modify','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'software.search','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'sysadmins.translation','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.acls','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.configuration','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.configuration.read','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.enumerates','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.firewall','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.jobs','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.packages','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.proxy','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.register','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.status','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'system.update','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'user.add','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'user.delete','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'user.guestusers','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'user.manage','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'user.modify','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,1,'user.search','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,2,'education.groups','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,2,'education.guestusers','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,2,'education.proxy','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,2,'education.rooms','Y',6);
INSERT INTO Acls VALUES(NULL,NULL,2,'education.users','Y',6);
