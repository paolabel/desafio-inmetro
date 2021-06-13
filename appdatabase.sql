CREATE TABLE IF NOT EXISTS Certificates ( 
	serial_number	INTEGER PRIMARY KEY NOT NULL,
	certificate		BLOB UNIQUE NOT NULL,
	issuer_name 	VARCHAR(30) NOT NULL,
	not_before	 	VARCHAR(20) NOT NULL,
    not_after 		VARCHAR(20) NOT NULL
);