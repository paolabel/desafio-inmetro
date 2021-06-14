package com.example.apidesafio.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import java.math.BigInteger;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class X509CertificateHandler {
    
    private final static String signatureAlgorithm = "SHA256WithRSA";
    private final static int serialNumberLenght = 64;
    private final static String keyPairGenAlgorithm = "RSA";
    private final static int keysize = 2048;

    public static X509Certificate newSelfSignedCert(String name, Date expirationTime) throws Exception{

        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        Date creationTime = Calendar.getInstance().getTime();

        String rnd = "CN="+name+",O=My Organisation,L=My City,C=DE";

        X500Name issuerName = new X500Name(rnd);

        BigInteger serialNumber = new BigInteger(serialNumberLenght, new Random());

        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(issuerName,
                serialNumber, creationTime, expirationTime, issuerName, subjectPublicKeyInfo);

        JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder(signatureAlgorithm);    

        ContentSigner contentSigner = signerBuilder.setProvider(bcProvider).build(privateKey);

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certificateHolder);

        return certificate;
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException{
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(keyPairGenAlgorithm);
        keyGenerator.initialize(keysize, new SecureRandom());
        KeyPair keyPair = keyGenerator.generateKeyPair();
    
        return keyPair;
    }

    public static ArrayList<String> extractFields(X509Certificate certificate){


        ArrayList<String> fields = new ArrayList<String>(5);

        String issuerName = certificate.getIssuerDN().toString();
        fields.add(issuerName);
        String serialNumber = certificate.getSerialNumber().toString();
        fields.add(serialNumber);
        String PublicKey = certificate.getPublicKey().toString();
        fields.add(PublicKey);
        String creationTime = certificate.getNotBefore().toString();
        fields.add(creationTime);
        String expirationTime = certificate.getNotAfter().toString();
        fields.add(expirationTime);

        return fields;
    }

    public static String getCommonName(X509Certificate certificate) throws CertificateEncodingException{
        X500Name subjectName = new JcaX509CertificateHolder(certificate).getSubject();
        RDN rdn = subjectName.getRDNs(BCStyle.CN)[0];
        String commonName = IETFUtils.valueToString(rdn.getFirst().getValue());
        return commonName;
    }
}
