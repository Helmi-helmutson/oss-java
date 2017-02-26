#!/usr/bin/perl

$ROLES=`grep \@RolesAllowed *`;

my $hroles = {};

foreach( split /\n/, $ROLES )
{
	if(	/\@RolesAllowed\("(.*)"\)/ )
	{
		$hroles->{$1} = 1;
	}

}

foreach( sort keys %$hroles )
{
	print "INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','$_');\n";
}
