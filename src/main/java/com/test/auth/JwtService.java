package com.test.auth;

import com.test.data.UserRepo;
import com.test.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.impl.DefaultJwtParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;

/**
 * Created by Luo_xuri on 2017/9/30.
 */
@Service
public class JwtService {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${jwt.key.store}") private String keystore;
    @Value("${jwt.key.pass}") private String keypass;
    @Value("${jwt.key.alias}") private String keyalias;
    @Value("${jwt.cert}") private String cert;

    private UserRepo userRepo;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtService(@Autowired UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @PostConstruct
    private void init() throws Exception {
        char[] pass = keypass.toCharArray();
        KeyStore from = KeyStore.getInstance("JKS", "SUN");
        from.load(new ClassPathResource(keystore).getInputStream(), pass);
        privateKey = (ECPrivateKey) from.getKey(keyalias, pass);

        CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
        X509Certificate x509Cert = (X509Certificate) certificatefactory.generateCertificate(new ClassPathResource(cert).getInputStream());
        publicKey = x509Cert.getPublicKey();
    }

    public String generate(User user){
        return new DefaultJwtBuilder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getName())
                .setExpiration(Date.from(ZonedDateTime.now().plusWeeks(1).toInstant()))
                .signWith(SignatureAlgorithm.ES256, privateKey).compact();
    }

    Optional<JwtAuthentication> parse(String token){
        try {
            Jws<Claims> jws = new DefaultJwtParser().setSigningKey(publicKey).parseClaimsJws(token);
            Claims claims = jws.getBody();
            return userRepo.findByName(claims.getSubject()).map(u -> new JwtAuthentication(u, token, claims));
        }catch (Exception e){
            LOG.error("failed to parse jwt token {}", token, e);
        }
        return empty();
    }











}
