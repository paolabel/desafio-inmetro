package com.example.apidesafio.service;

import java.security.cert.X509Certificate;
import java.sql.*;

public class DBHandler {

    static String dbPath = "jdbc:sqlite:data/appdatabase.db";

    public static Connection connect() {
        Connection connection = null;

        try {

            connection = DriverManager.getConnection(dbPath);
            
            
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        } 

        return connection;
    }

    public static void createCertTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Certificates (\n"
                    +"serial_number	    INTEGER PRIMARY KEY NOT NULL,\n"
                    +"certificate		BLOB UNIQUE NOT NULL,\n"
                    +"name 	            VARCHAR(30) NOT NULL,\n"
                    +"not_before	 	VARCHAR(20) NOT NULL,\n"
                    +"not_after 		VARCHAR(20) NOT NULL\n"
                    +");";

        try {
            Connection dbConnection = connect();
            Statement statement = dbConnection.createStatement();
            statement.execute(sql);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    public static boolean insert(X509Certificate certificate) {
        boolean isInserted = false;
        String sql = "INSERT INTO Certificates(serial_number, certificate, name,"
                    +" not_before, not_after) VALUES (?, ?, ?, ?, ?)";
        try {

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        return isInserted;
    }
}
