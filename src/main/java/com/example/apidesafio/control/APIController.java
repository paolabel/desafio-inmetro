package com.example.apidesafio.control;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

import com.example.apidesafio.service.DBHandler;
import com.example.apidesafio.service.DateHandler;
import com.example.apidesafio.service.X509CertificateHandler;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class APIController {
    
    @GetMapping("/")
    public String home() {
        return "Escutando na porta 8080";
    }

    @PostMapping("/certificates")
    public ResponseEntity<String> newCertificate(String commonName, String expirationDate) {
        // expirationDate deve estar no formato "DD/MM/YYYYTHH:MM:SS"
        JSONObject responseBody = new JSONObject();

        Date expirationTime = new Date();
        String[] expirationArray = expirationDate.split("T");

        try {
            expirationTime = DateHandler.getDateTime(expirationArray[0], expirationArray[1]);
        } catch(Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }

        if (expirationTime.getTime() < DateHandler.getCurrentMilliseconds()) {
            Exception exception = new Exception("Prazo de validade inválido");
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            X509Certificate certificate= X509CertificateHandler.newSelfSignedCert(commonName, expirationTime);
            DBHandler.insert(certificate);

            String[] fields = X509CertificateHandler.extractFields(certificate);
            responseBody.put("commonName", fields[0]);
            responseBody.put("serialNumber", fields[1]);
            responseBody.put("publicKey", fields[2]);
            responseBody.put("creationDate", fields[3]);
            responseBody.put("expirationDate", fields[4]);

        } catch(Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }
    
    @GetMapping("/certificates")
    public ResponseEntity<String> selectAll() {
        JSONObject responseBody = new JSONObject();

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectAllCerts();
            responseBody = X509CertificateHandler.getResponseBody(selection);

        } catch (Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }

    @DeleteMapping("/certificates")
    public ResponseEntity<String> clearDB() {
        try {
            DBHandler.deleteAll();
        } catch(Exception exception) {
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>("BD foi limpo", HttpStatus.OK);
    }

    @GetMapping("/certificates/{serialNumber}")
    public ResponseEntity<String> selectBySerial(@PathVariable("serialNumber") String serialNumber) {
        JSONObject responseBody = new JSONObject();

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectBySerial(serialNumber);
            responseBody = X509CertificateHandler.getResponseBody(selection);

        } catch (Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }

    @DeleteMapping("/certificates/{serialNumber}")
    public ResponseEntity<String> deleteBySerial(@PathVariable("serialNumber") String serialNumber) {
        try {
            DBHandler.delete(false,serialNumber);
        } catch(Exception exception) {
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        String responseBody = "Certificado com número serial " +serialNumber+ "foi removido";
        return new ResponseEntity<String>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/certificates/name")
    public ResponseEntity<String> selectByName(String name) {
        JSONObject responseBody = new JSONObject();

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectByName(name);
            responseBody = X509CertificateHandler.getResponseBody(selection);

        } catch (Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }
    
    @DeleteMapping("/certificates/name")
    public ResponseEntity<String> deleteByName(String name) {
        try {
            DBHandler.delete(true,name);
        } catch(Exception exception) {
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        String responseBody = "Os certificados de " +name+ "foram removidos";
        return new ResponseEntity<String>(responseBody, HttpStatus.OK);
    }    

    @GetMapping("/certificates/name/interval")
    public ResponseEntity<String> selectValidCertsByName(String name, String startDate, String endDate) {
        // startDate e endDate devem estar no formato "DD/MM/YYYYTHH:MM:SS"

        JSONObject responseBody = new JSONObject();

        try {
            String[] startDateArray = startDate.split("T");
            String[] endDateArray = startDate.split("T");
            Long start_ms = DateHandler.getMilliseconds(startDateArray[0], startDateArray[1]);
            Long end_ms = DateHandler.getMilliseconds(endDateArray[0], endDateArray[1]);
            ArrayList<X509Certificate> selection = DBHandler.selectByNameAndInterval(name, start_ms, end_ms);
            responseBody = X509CertificateHandler.getResponseBody(selection);

        } catch (Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }    

        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }

    @GetMapping("/certificates/valid")
    public ResponseEntity<String> selectValidCertsNow(String date) {

        JSONObject responseBody = new JSONObject();
        Long date_ms = DateHandler.getCurrentMilliseconds();

        if (date != null) {
            String[] dateArray = date.split("T");
            try{
                date_ms = DateHandler.getMilliseconds(dateArray[0], dateArray[1]);
            } catch (Exception exception) {
                responseBody.put("errorMessage", exception.getMessage());
                return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
            }    
        }

        try {
            ArrayList<X509Certificate> selection = DBHandler.selectValidOn(date_ms);
            responseBody = X509CertificateHandler.getResponseBody(selection);

        } catch (Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }    

        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }

    @GetMapping("/certificates/valid/interval")
    public ResponseEntity<String> selectValidCerts(String startDate, String endDate) {
        // startDate e endDate devem estar no formato "DD/MM/YYYYTHH:MM:SS"
        
        String[] startDateArray = startDate.split("T");
        String[] endDateArray = startDate.split("T");

        JSONObject responseBody = new JSONObject();

        try {
            Long start_ms = DateHandler.getMilliseconds(startDateArray[0], startDateArray[1]);
            Long end_ms = DateHandler.getMilliseconds(endDateArray[0], endDateArray[1]);
            ArrayList<X509Certificate> selection = DBHandler.selectValidOn(start_ms, end_ms);
            responseBody = X509CertificateHandler.getResponseBody(selection);

        } catch (Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }    

        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }

    @GetMapping("/certificates/expired")
    public ResponseEntity<String> selectExpiredCertsNow() {
        JSONObject responseBody = new JSONObject();

        Long now_ms = DateHandler.getCurrentMilliseconds();

        try {
            ArrayList<X509Certificate> selection = DBHandler.queryExpiredOn(false, now_ms);
            responseBody = X509CertificateHandler.getResponseBody(selection);

        } catch (Exception exception) {
            responseBody.put("errorMessage", exception.getMessage());
            return new ResponseEntity<String>(responseBody.toString(), HttpStatus.BAD_REQUEST);
        }    

        return new ResponseEntity<String>(responseBody.toString(), HttpStatus.OK);
    }

    @DeleteMapping("/certificates/expired")
    public ResponseEntity<String> deleteExpiredCertsNow() {
        Long now_ms = DateHandler.getCurrentMilliseconds();

        try {
            DBHandler.queryExpiredOn(true, now_ms);
        } catch(Exception exception) {
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        String responseBody = "Os certificados expirados foram removidos";
        return new ResponseEntity<String>(responseBody, HttpStatus.OK);
    } 
}
