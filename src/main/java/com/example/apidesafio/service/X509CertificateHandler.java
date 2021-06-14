package com.example.apidesafio.service;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
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

    public static X509Certificate newSelfSignedCert(String name, Date expirationTime) throws Exception{

        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        Date creationTime = Calendar.getInstance().getTime();

        String rnd = "CN="+name+",O=My Organisation,L=My City,C=DE";

        X500Name issuerName = new X500Name(rnd);

        BigInteger serialNumber = new BigInteger(SERIAL_NUMBER_LENGHT, new Random());

        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(issuerName,
                serialNumber, creationTime, expirationTime, issuerName, subjectPublicKeyInfo);

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

        String issuerName = getCommonName(certificate);
        String serialNumber = certificate.getSerialNumber().toString();
        String publicKey = certificate.getPublicKey().toString();
        String creationTime = certificate.getNotBefore().toString();
        String expirationTime = certificate.getNotAfter().toString();

        String[] fields = new String[]{issuerName, serialNumber, publicKey, creationTime, expirationTime};
        return fields;
    }

    public static String getCommonName(X509Certificate certificate) throws CertificateEncodingException{
        X500Name subjectName = new JcaX509CertificateHolder(certificate).getSubject();
        RDN rdn = subjectName.getRDNs(BCStyle.CN)[0];
        String commonName = IETFUtils.valueToString(rdn.getFirst().getValue());
        return commonName;
    }

    public static String getResponseText(ArrayList<X509Certificate> certificates) throws Exception {
        String response = "";

        for (X509Certificate certificate : certificates) {
            String[] fields = extractFields(certificate);
            response = response.concat("Nome do titular: "+fields[0]+"\nNúmero serial: "+fields[1]
                            +"\nChave pública: "+fields[2]+"\nData de criação: "+fields[3]
                            +"\nVálido até: "+fields[4]+"\n\n");              
        }
        return response;
    }

    public static X509Certificate getCertificate(byte[] byteArray) throws CertificateException {
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        CertificateFactory X509Factory = CertificateFactory.getInstance("X.509"); 
        X509Certificate certificate = (X509Certificate)X509Factory.generateCertificate(inputStream);
        return certificate;
    }

    public static byte[] x509ToPEM(final X509Certificate cert) throws Exception {
        StringWriter writer = new StringWriter();
        JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(cert);
        pemWriter.flush();
        pemWriter.close();
        return writer.toString().getBytes();
    }
}
