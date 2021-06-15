package com.example.apidesafio.service;

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
                    +"serial_number	    TEXT PRIMARY KEY NOT NULL,\n"
                    +"certificate		BLOB UNIQUE NOT NULL,\n"
                    +"common_name 	    TEXT NOT NULL,\n"
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

    public static boolean insert(X509Certificate certificate) throws Exception{
        boolean isInserted = false;
        String sql = "INSERT INTO Certificates(serial_number, certificate, common_name,"
                    +" creation_ms, expiration_ms) VALUES (?, ?, ?, ?, ?)";  

            String commonName = X509CertificateHandler.getCommonName(certificate);

            Connection dbConnection = connect();
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setString(1, certificate.getSerialNumber().toString());
            statement.setBytes(2, X509CertificateHandler.x509ToPEM(certificate));
            statement.setString(3, commonName);
            statement.setLong(4, certificate.getNotBefore().getTime());
            statement.setLong(5, certificate.getNotAfter().getTime());
            
            isInserted = statement.execute();

        return isInserted;
    }

    public static boolean delete(boolean name, String queryParam) throws Exception{
        String sql = "DELETE FROM Certificates WHERE ";

        if (name){
            sql = sql.concat("common_name = ?");
        } else {
            sql = sql.concat("serial_number = ?");
        }    

        boolean isDeleted = false;

            Connection dbConnection = connect();
            PreparedStatement statement = dbConnection.prepareStatement(sql);
            statement.setString(1, queryParam);
            isDeleted = statement.execute();
    
        return isDeleted;
    }

    public static void deleteAll() throws Exception{
        String sql = "DELETE FROM Certificates";

        Connection dbConnection = connect();
        Statement statement = dbConnection.createStatement();
        statement.execute(sql);

    }

    public static ArrayList<X509Certificate> selectValidOn(Long start_ms, Long end_ms) throws Exception {
        if (start_ms > end_ms) {
            throw new Exception("Seleção com período de tempo inválido");
        }

        String query =  "SELECT certificate from Certificates "
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

    public static ArrayList<X509Certificate> selectValidOn(Long day_ms) throws Exception {
        String query = "SELECT certificate from Certificates "
                        +"WHERE creation_ms < ? AND expiration_ms > ?";

        Connection dbConnection = connect();
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setLong(1, day_ms);
        statement.setLong(2, day_ms);
        ResultSet results = statement.executeQuery();
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    public static ArrayList<X509Certificate> queryExpiredOn(boolean delete, Long day_ms) throws Exception {
        String query = "";

        if (delete) {
            query = query.concat("DELETE ");
        } else {
            query = query.concat("SELECT ");
        }
        query = query.concat("certificate from Certificates "
                            +"WHERE expiration_ms < ?");

        Connection dbConnection = connect();
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setLong(1, day_ms);
        ResultSet results = statement.executeQuery();
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    public static ArrayList<X509Certificate> selectByName(String commonName) throws Exception{
        String query = "SELECT certificate from Certificates "
                        +"WHERE common_name = ?"; 

        Connection dbConnection = connect();
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, commonName);
        ResultSet results = statement.executeQuery();
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    public static ArrayList<X509Certificate> selectByNameAndInterval(String name, Long start_ms, Long end_ms) throws Exception{
        if (start_ms > end_ms) {
            throw new Exception("Seleção com período de tempo inválido");
        }
        String query = "SELECT certificate from Certificates "
                        +"WHERE common_name = ? AND ? > creation_ms AND ? < expiration_ms"; 

        Connection dbConnection = connect();
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, name);
        statement.setLong(2, end_ms);
        statement.setLong(3, start_ms);
        ResultSet results = statement.executeQuery();
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    public static ArrayList<X509Certificate> selectBySerial(String serialNumber) throws Exception{
        String query = "SELECT certificate from Certificates "
                        +"WHERE serial_number = ?"; 

        Connection dbConnection = connect();
        PreparedStatement statement = dbConnection.prepareStatement(query);
        statement.setString(1, serialNumber);
        ResultSet results = statement.executeQuery();
        ArrayList<X509Certificate> selection = getFromResultSet(results);

        return selection;
    }

    private static ArrayList<X509Certificate> getFromResultSet(ResultSet results) throws Exception {
        ArrayList<X509Certificate> selection = new ArrayList<X509Certificate>();  

        while(results.next()){
            byte[] certificateBytes = results.getBytes("certificate");
            X509Certificate certificate = X509CertificateHandler.getCertificate(certificateBytes);
            selection.add(certificate);
        }
        return selection;
    }

}
