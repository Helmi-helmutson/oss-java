#!/usr/bin/perl

$ROLES=`grep \@RolesAllowed *`;

my $hroles = {};
my $forTeachers = {};

foreach( split /\n/, $ROLES )
{
	if(	/\@RolesAllowed\("(.*)"\)/ )
	{
		my $r = $1;
		$hroles->{$1} = 1;
		if( $r =~ /.search/ ) {
			$forTeachers->{$r} = 1;
		}
	}

}
foreach( sort keys %$hroles )
{
	print "INSERT INTO Acls VALUES(NULL,NULL,NULL,'sysadmins','$_',6);\n";
}

foreach( sort keys %$forTeachers )
{
	print "INSERT INTO Acls VALUES(NULL,NULL,NULL,'teachers','$_',6);\n";
}

