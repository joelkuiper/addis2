ADDIS 2.x
=========

Running
-------

	sudo apt-get install openjdk-7-jdk maven postgresql postgresql-client postgresql-contrib-9.1
	sudo update-alternatives --config java # choose java 7

	sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'develop'"
	sudo -u postgres createuser -S -D -R addis 
	sudo -u postgres psql -c "ALTER USER addis WITH PASSWORD 'develop'"

	cd dbms
	./rebuild.sh
	cd ..

	mvn jetty:run
