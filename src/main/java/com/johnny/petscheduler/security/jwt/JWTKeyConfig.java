package com.johnny.petscheduler.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * This configuration class reads & loads private and public keys
 * from .pem files upon Spring Boot startup.
 */
@Configuration
public class JWTKeyConfig {
    @Value("${jwt.private.key.path}")
    private String pvtKeyPath;

    @Value("${jwt.public.key.path}")
    private String publicKeyPath;

    /**
     * Reads and loads a Private Key from a (PKCS8 .pem file) for the signing
     * of JWT tokens.
     *
     * @implNote PKCS8 format
     *      This application only supports PKCS8 pem files
     *
     * @return privateKey
     *      An EC PrivateKey object
     */
    @Bean
    public PrivateKey jwtPrivateKey() throws Exception {
        String pem = Files.readString(Path.of(pvtKeyPath))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("EC").generatePrivate(keySpec);
    }

    /**
     * Reads and loads a Public Key from a .pem file for the verification
     * of the signatures in JWT tokens.
     *
     * @return publicKey
     *      A EC publicKey object
     */
    @Bean
    public PublicKey jwtPublicKey() throws Exception {
        String pem = Files.readString(Path.of(publicKeyPath))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("EC").generatePublic(keySpec);
    }
}
