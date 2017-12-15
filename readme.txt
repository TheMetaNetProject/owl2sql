Project: owl2sql
Author: Brandon Thai

                            Introduction
                            
OWL2SQL is a command-line tool written in Java that translates a .owl (OWL
ontology) file into a SQL database and uploads it to a MySQL server. The
tool is designed specifically for the MetaNet Project's metaphor repository
ontologies, and its current limitations reflect assumptions made based on
current metaphor repository ontologies.


                            Instructions
                            
1. 	Make owl2sql executable.

   		user@icsi:~$ chmod +x ./owl2sql
   		
2. 	Run owl2sql from a console providing the path of a .owl file as an argument.
	If you wish to input server and user login information through console 
	prompts, use the -C option:
	
		user@icsi:~$ ./owl2sql -C ~/myDir/myOWL.owl
		
3.	Otherwise, provide the information through command-line arguments:

		user@icsi:~$ ./owl2sql -server <server name> -port <port number> -u <username> -p <password> -db <database name> ~/myDir/myOWL.owl
   	
   	If a command-line argument is omitted, the default value for that argument 
   	will be used. To enable error-logging, use the -E option:
   	
		user@icsi:~$ ./owl2sql -E ...
		
4.	The tool will then begin to build the database. No changes are made to the 
	actual database during this time.
	
5.	If the build process is successful, the changes are committed to the 
	database server.

    
For more information, see the OWL2SQL page on the MetaNet Wiki at
https://metaphor.icsi.berkeley.edu/metaphor/index.php/OWL2SQL
