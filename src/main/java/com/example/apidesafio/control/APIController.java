package com.example.apidesafio.control;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

import com.example.apidesafio.service.DBHandler;
import com.example.apidesafio.service.DateHandler;
import com.example.apidesafio.service.X509CertificateHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class APIController {
    
    @GetMapping("/")
    public String home() {
        return "Escutando na porta 8080";
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

    @PostMapping("/certficates")
    public String newCertificate(String name, String expirationDate, String time) {

        Date expirationTime = new Date();

        if (time == null) {
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
    
    @GetMapping("/certificates")
    @ResponseBody
    public ResponseEntity<String> selectAll() {
        String returnString = "";

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectAllCerts();
            returnString = X509CertificateHandler.getResponseText(selection);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<String>("não selecionou", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(returnString, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/certificates/{serialNumber}")
    public String removeCertificate(@PathVariable("serialNumber") String serialNumber) {
        try {
            DBHandler.delete(serialNumber);
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não foi removido";
        }
        return "certificado removido";
    }

    @GetMapping("/certificates/{serialNumber}")
    public String selectBySerial(@PathVariable("serialNumber") String serialNumber) {
        String returnString = "";

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectBySerial(serialNumber);
            returnString = returnString.concat(X509CertificateHandler.getResponseText(selection));

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não selecionou";
        }

        return returnString;
    }

    @GetMapping("/certificates/{name}")
    public String selectByName(@PathVariable("name") String name) {
        String returnString = "Todos os certificados com o titular "+name+":\n\n";

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectByName(name);
            returnString = returnString.concat(X509CertificateHandler.getResponseText(selection));

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return "não selecionou";
        }

        return returnString;
    }

    @GetMapping("/certificates/valid/interval")
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

    @GetMapping("/certificates/valid/now")
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

}
