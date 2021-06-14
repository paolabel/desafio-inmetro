package com.example.apidesafio.service;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    
    // adicionar checagem de comprimento de expirationDate e time
    @PostMapping("/newcert")
    public String newCertificate(String name, String expirationDate, String time) {

        // expirationDate no formato DD/MM/YYYY
        String[] dateArray = expirationDate.split("/");
        Calendar expirationCalendar = Calendar.getInstance();
        int expirationYear = Integer.parseInt(dateArray[2]);
        int expirationMonth = Integer.parseInt(dateArray[1]);
        int expirationDay = Integer.parseInt(dateArray[0]);

        // time no formato HH:MM:SS
        if (time != null) {
            String[] timeArray = time.split(":");
            int expirationHour = Integer.parseInt(timeArray[0]);
            int expirationMin = Integer.parseInt(timeArray[1]);
            int expirationSec = Integer.parseInt(timeArray[2]);
            expirationCalendar.set(expirationYear, expirationMonth, expirationDay, expirationHour, expirationMin, expirationSec);
        } else {
            expirationCalendar.set(expirationYear, expirationMonth, expirationDay);
        }

        Date expirationTime = expirationCalendar.getTime();

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

    @DeleteMapping("removecert")
    public String removeCertificate(BigInteger serialNumber){
        try {
            DBHandler.delete(serialNumber);
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não foi removido";
        }
        return "certificado removido";
    }

}
