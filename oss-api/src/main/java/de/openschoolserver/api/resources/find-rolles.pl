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
		if( $r =~ /education/ ) {
			$forTeachers->{$r} = 1;
		}
	}

}
foreach( sort keys %$hroles )
{
	print "INSERT INTO Enumerates VALUES(NULL,'apiAcl','$_',6);\n";
}
foreach( sort keys %$hroles )
{
	print "INSERT INTO Acls VALUES(NULL,NULL,1,'$_','Y',6);\n";
}

foreach( sort keys %$forTeachers )
{
	print "INSERT INTO Acls VALUES(NULL,NULL,2,'$_','Y',6);\n";
}

