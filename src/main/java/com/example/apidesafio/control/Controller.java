package com.example.apidesafio.control;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

import com.example.apidesafio.service.DBHandler;
import com.example.apidesafio.service.DateHandler;
import com.example.apidesafio.service.X509CertificateHandler;

import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    static final int DATE_FORMAT_LENGHT = 10;
    static final int TIME_FORMAT_LENGHT = 8;
    
    @GetMapping("/")
    public String home() {
        return "início";
    }

    @PostMapping("/newcert")
    public String newCertificate(String name, String expirationDate, String time) {

        Date expirationTime = new Date();

        if (time ==null) {
            expirationTime = DateHandler.getDate(expirationDate);
        } else {
            expirationTime = DateHandler.getDateTime(expirationDate, time);
        }

        if (expirationTime.getTime() < DateHandler.getCurrentMilliseconds()) {
            Exception exception = new Exception("Prazo de validade inválido");
            System.out.println(exception.getMessage());
            return "não foi inserido";
        }

        try {
            X509Certificate certificate= X509CertificateHandler.newSelfSignedCert(name, expirationTime);
            DBHandler.insert(certificate);
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não foi inserido";
        }
        return "certificado novo inserido";
    }

    @DeleteMapping("/removecert")
    public String removeCertificate(BigInteger serialNumber) {
        try {
            DBHandler.delete(serialNumber);
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não foi removido";
        }
        return "certificado removido";
    }

    @DeleteMapping("/cleardb")
    public String clearDB() {
        try {
            DBHandler.deleteAll();
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "BD não foi limpo";
        }
        return "BD foi limpo";
    }

    @GetMapping("/getvalidcerts_if")
    public String selectValidCerts(String startDate, String startTime, String endDate, String endTime) {
        // startDate e endDate devem estar no formato "DD/MM/YYYY"
        // startTime e endTime devem estar no formato "HH:MM:SS"
        
        Long start_ms = DateHandler.getMilliseconds(startDate, startTime);
        Long end_ms = DateHandler.getMilliseconds(endDate, endTime);

        String returnString = "";

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectValidsOnTime(start_ms, end_ms);
            returnString = X509CertificateHandler.getResponseText(selection);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não selecionou";
        }    

        return returnString;
    }

    @GetMapping("/getvalidcerts_now")
    public String selectValidCertsNow() {

        String returnString = "";
        try {
            ArrayList<X509Certificate> selection = DBHandler.selectValidNow();
            returnString = X509CertificateHandler.getResponseText(selection);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não selecionou";
        }    

        return returnString;
    }

    @GetMapping("/showall")
    public String selectAll() {
        String returnString = "";

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectAllCerts();
            returnString = X509CertificateHandler.getResponseText(selection);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não selecionou";
        }

        return returnString;
    }

    @GetMapping("/getcertsbyname")
    public String selectByName(String name) {
        String returnString = "Todos os certificados com o titular "+name+":\n\n";

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectByName(name);
            returnString.concat(X509CertificateHandler.getResponseText(selection));

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não selecionou";
        }

        return returnString;
    }
}
