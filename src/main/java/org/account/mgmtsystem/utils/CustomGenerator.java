package org.account.mgmtsystem.utils;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.security.SecureRandom;

public class CustomGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(
            SharedSessionContractImplementor session, Object obj)
            throws HibernateException {

        return randomString(6);
    }

    public static String generateSecurityPin() {
        SecureRandom random = new SecureRandom();
        return String.format("%04d", random.nextInt(10000));
    }


    private String randomString(int length) {
        final String alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++)
            sb.append(alphanumeric.charAt(rnd.nextInt(alphanumeric.length())));

        return sb.toString();
    }
}
