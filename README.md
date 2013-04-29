ADDIS 2.x
=========

Running
-------

	sudo apt-get install openjdk-7-jdk maven postgresql postgresql-client postgresql-contrib-9.1
	sudo update-alternatives --config java # choose java 7

	sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'develop'"
	sudo -u postgres createuser -S -D -R addis
	sudo -u postgres psql -c "ALTER USER addis WITH PASSWORD 'develop'"

	mvn generate-test-sources
	cd dbms
	./rebuild.sh
	cd ..

	mvn jetty:run

Running from Spring Tool Suite / Eclipse
----------------------------------------

 * Follow the above steps (except mvn jetty:run)

 * Download Spring Tool Suite: http://www.springsource.org/sts

    + Or Eclipse and install the Spring Tools from the market place

 * Add Lombok to STS.ini (or eclipse.ini)

    -javaagent:lombok-0.11.6.jar
    -Xbootclasspath/a:lombok-0.11.6.jar

 * Get the Postgres JDBC driver: http://jdbc.postgresql.org/download.html (9.1 or newer for JDBC 4)

    + Add it to the VMware server (double-click to open overview, then open launch configuration)

 * Import existing Maven projects to add the addis2 project

 * Drag the project onto the VMware server and start the server
