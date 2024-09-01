package com.example.appVersion.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${appstore.keyId}")
    private String keyId;

    @Value("${appstore.issuerId}")
    private String issuerId;

    @Value("${appstore.keyPath}")
    private String keyPath;

    private ECPrivateKey privateKey;

    @PostConstruct
    public void init() throws GeneralSecurityException, IOException {
        log.info("Initializing JwtTokenProvider");
        log.info("Key Path: " + keyPath);
        this.privateKey = loadPrivateKey();
    }

    private ECPrivateKey loadPrivateKey() throws GeneralSecurityException, IOException {
        try (InputStream inputStream = new FileInputStream(keyPath)) {
            log.info("Loading private key from: " + keyPath);
            String privateKeyContent = new String(StreamUtils.copyToByteArray(inputStream))
                    .replaceAll("\\r", "")
                    .replaceAll("\\n", "")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "");

            log.info("Private Key Content Length: " + privateKeyContent.length());
            log.info("Private Key Content: " + privateKeyContent);

            byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);
            log.info("Decoded Key Length: " + decodedKey.length);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory kf = KeyFactory.getInstance("EC");
            ECPrivateKey ecPrivateKey = (ECPrivateKey) kf.generatePrivate(keySpec);
            log.info("Private Key Algorithm: " + ecPrivateKey.getAlgorithm());
            return ecPrivateKey;
        } catch (IOException e) {
            log.info("Failed to load private key: " + e.getMessage());
            throw e;
        } catch (GeneralSecurityException e) {
            log.info("Failed to generate private key: " + e.getMessage());
            throw e;
        }
    }

    public String createToken() throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.ECDSA256(null, privateKey);

        String token = JWT.create()
                .withKeyId(keyId)
                .withIssuer(issuerId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 20 * 60 * 1000))
                .withAudience("appstoreconnect-v1")
                .sign(algorithm);

        log.info("Generated JWT Token: " + token);
        return token;
    }
}
