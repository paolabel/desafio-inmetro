package com.example.apidesafio.service;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

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

        Date expirationTime = null;

        if (time.equals(null)) {
            expirationTime = DateHandler.getDate(expirationDate);
        } else {
            expirationTime = DateHandler.getDateTime(expirationDate, time);
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

    @GetMapping("/getvalidcerts_if")
    public String selectValidCerts(String startDate, String startTime, String endDate, String endTime) {
        // startDate e endDate devem estar no formato "DD/MM/YYYY"
        // startTime e endTime devem estar no formato "HH:MM:SS"
        
        Long start_ms = DateHandler.getMilliseconds(startDate, startTime);
        Long end_ms = DateHandler.getMilliseconds(endDate, endTime);

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectValidityOnTime(start_ms, end_ms);

            for (X509Certificate certificate : selection) {
                String[] fields = X509CertificateHandler.extractFields(certificate);
                System.out.println("Nome do titular: "+fields[0]+"\nNúmero serial: "+fields[1]
                                +"\nChave pública: "+fields[2]+"\nData de criação: "+fields[3]
                                +"\nPrazo de validade: "+fields[4]+"\n\n");
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não selecionou";
        }    

        return "selecionou";
    }

    @GetMapping("/getvalidcerts_now")
    public String selectValidCertsNow() {
        return "selecionou";
    }
}
