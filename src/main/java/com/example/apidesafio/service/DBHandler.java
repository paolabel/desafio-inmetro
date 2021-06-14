package com.example.apidesafio.service;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.ArrayList;



public class DBHandler {

    static String DB_PATH = "jdbc:sqlite:data/appdatabase.db";

    public static Connection connect() {
        Connection connection = null;

        try {

            connection = DriverManager.getConnection(DB_PATH);
               
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
                    +"creation_ms	 	INTEGER NOT NULL,\n"
                    +"expiration_ms 	INTEGER NOT NULL\n"
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
                    +" creation_ms, expiration_ms) VALUES (?, ?, ?, ?, ?)";  

        try {
            String commonName = X509CertificateHandler.getCommonName(certificate);

            Connection dbConnection = connect();
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setInt(1, certificate.getSerialNumber().intValue());
            statement.setObject(2, certificate);
            statement.setString(3, commonName);
            statement.setLong(4, certificate.getNotBefore().getTime());
            statement.setLong(5, certificate.getNotAfter().getTime());
            
            isInserted = statement.execute();

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
        return isInserted;
    }

    public static boolean delete(BigInteger serialNumber) {
        String sql = "DELETE FROM Certificates WHERE serial_number = ?";

        boolean isDeleted = false;

        try{
            Connection dbConnection = connect();
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setInt(1, serialNumber.intValue());
            isDeleted = statement.execute();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    
        return isDeleted;
    }

    public static ArrayList<X509Certificate> selectValidsOnTime(Long start_ms, Long end_ms) throws Exception {
        if (start_ms > end_ms) {
            throw new Exception("Seleção com período de tempo inválido");
        }

        String query =  "SELECT certificate from Certificates"
                        +"WHERE ? > creation_ms AND ? < expiration_ms";

        Connection dbConnection = connect();
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setLong(1, end_ms);
        statement.setLong(2, start_ms);
        ResultSet results = statement.executeQuery();
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    public static ArrayList<X509Certificate> selectAllCerts() throws Exception {
        String query = "SELECT certificate from Certificates";

        Connection dbConnection = connect();
        Statement statement = dbConnection.createStatement();
        ResultSet results = statement.executeQuery(query);
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    public static ArrayList<X509Certificate> selectValidNow() throws Exception {
        String query = "SELECT certificate from Certificates"
                        +"WHERE creation_ms < ? AND expiration_ms > ?";

        Long now_ms = DateHandler.getCurrentMilliseconds();

        Connection dbConnection = connect();
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setLong(1, now_ms);
        statement.setLong(2, now_ms);
        ResultSet results = statement.executeQuery();
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    public static ArrayList<X509Certificate> selectByName(String name) throws Exception{
        String query = "SELECT certificate from Certificates"
                        +"WHERE name = ?"; 

        Connection dbConnection = connect();
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, name);
        ResultSet results = statement.executeQuery();
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    private static ArrayList<X509Certificate> getFromResultSet(ResultSet results) throws Exception {
        ArrayList<X509Certificate> selection = new ArrayList<X509Certificate>();  
        while(results.next()){
            InputStream certificateData = results.getBinaryStream("certificate");
            X509Certificate certificate = X509CertificateHandler.getCertificate(certificateData);
            selection.add(certificate);
        }
        return selection;
    }
}
