package com.example.apidesafio.service;

import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    
    @PostMapping("/newcert")
    public String newCertificate(String name, String expirationDate) {

        String[] dateArray = expirationDate.split("/");
        Calendar expirationCalendar = Calendar.getInstance();
        int expirationYear = Integer.parseInt(dateArray[2]);
        int expirationMonth = Integer.parseInt(dateArray[1]);
        int expirationDay = Integer.parseInt(dateArray[0]);
        expirationCalendar.set(expirationYear, expirationMonth, expirationDay);
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

}
