package com.example.apidesafio.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.json.JSONObject;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class X509CertificateHandler {
    
    private final static String SIGNATURE_ALGORITHM = "SHA256WithRSA";
    private final static int SERIAL_NUMBER_LENGHT = 64;
    private final static String KEY_PAIR_GEN_ALGORITHM = "RSA";
    private final static int KEY_SIZE = 2048;

    public static X509Certificate newSelfSignedCert(String commonName, Date expirationTime) throws Exception{

        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        Date creationTime = Calendar.getInstance().getTime();

        String rdn = "CN="+commonName+",O=Default Organisation,L=Default City,C=Default Country";

        X500Name subjectName = new X500Name(rdn);

        BigInteger serialNumber = new BigInteger(SERIAL_NUMBER_LENGHT, new Random());

        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(subjectName,
                serialNumber, creationTime, expirationTime, subjectName, subjectPublicKeyInfo);

        JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM);    

        ContentSigner contentSigner = signerBuilder.setProvider(bcProvider).build(privateKey);

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certificateHolder);

        return certificate;
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException{
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(KEY_PAIR_GEN_ALGORITHM);
        keyGenerator.initialize(KEY_SIZE, new SecureRandom());
        KeyPair keyPair = keyGenerator.generateKeyPair();
    
        return keyPair;
    }

    public static String[] extractFields(X509Certificate certificate) throws Exception{

        String commonName = getCommonName(certificate);
        String serialNumber = certificate.getSerialNumber().toString();
        String publicKey = certificate.getPublicKey().toString();
        String creationTime = certificate.getNotBefore().toString();
        String expirationTime = certificate.getNotAfter().toString();

        String[] fields = new String[]{commonName, serialNumber, publicKey, creationTime, expirationTime};
        return fields;
    }

    public static String getCommonName(X509Certificate certificate) throws Exception{
        X500Name subjectName = new JcaX509CertificateHolder(certificate).getSubject();
        RDN rdn = subjectName.getRDNs(BCStyle.CN)[0];
        String commonName = IETFUtils.valueToString(rdn.getFirst().getValue());
        return commonName;
    }

    public static JSONObject getResponseBody(ArrayList<X509Certificate> certificates) throws Exception {
        JSONObject responseBody = new JSONObject();
        ArrayList<JSONObject> certificateList = new ArrayList<JSONObject>();

        for (X509Certificate certificate : certificates) {
            String[] fields = extractFields(certificate);
            JSONObject certificateJson = new JSONObject();
            certificateJson.put("commonName", fields[0]);
            certificateJson.put("serialNumber", fields[1]);
            certificateJson.put("publicKey", fields[2]);
            certificateJson.put("creationDate", fields[3]);
            certificateJson.put("expirationDate", fields[4]);
            
            certificateList.add(certificateJson);
        }
        responseBody.put("certificates", certificateList);
        return responseBody;
    }

    public static X509Certificate getCertificate(byte[] byteArray) throws CertificateException {
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        CertificateFactory X509Factory = CertificateFactory.getInstance("X.509"); 
        X509Certificate certificate = (X509Certificate)X509Factory.generateCertificate(inputStream);
        return certificate;
    }

    public static byte[] x509ToPEM(X509Certificate cert) throws Exception {
        StringWriter writer = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(cert);
        pemWriter.flush();
        pemWriter.close();
        return writer.toString().getBytes();
    }
}
