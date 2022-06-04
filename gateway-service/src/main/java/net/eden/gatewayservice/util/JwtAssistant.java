package net.eden.gatewayservice.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtAssistant {

    // 1.设定密钥
    private static final String SECRET = "Ciallo~";
    // 2.设定算法
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);
    // 3.初始化验证器
    private static final JWTVerifier verifier = JWT.require(algorithm).withIssuer("weiyin").build();


    /**
     * 生成Token并签名
     */
    public static String initToken(Long uid, int expireTime){
        try {
            //token创建时间
            Date now = new Date();
            //token过期时间
            Date expireDate = getAfterDate(now, 0, 0, expireTime, 0, 0, 0);
            //生成token并签名
            String token = JWT.create()
                .withIssuer("weiyin")
                .withIssuedAt(now)
                .withExpiresAt(expireDate)
                .withClaim("uid", uid)
                .sign(algorithm);

            return token;

        } catch (JWTCreationException exception) {
            // System.out.println("token创建失败");
            return null;
        }
    }

    /**
     * 验证 Token 并获得 TokenInfo
     * @param token
     * @return TokenInfo
     */
    public static TokenInfo verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            DecodedJWT jwt = verifier.verify(token);
            Long uid = jwt.getClaim("uid").asLong();
            Date expiresAt = jwt.getExpiresAt();
            return new TokenInfo(uid, expiresAt);

        } catch (JWTVerificationException exception) {
             // System.out.println("token验证失败");
            return null;
        }
    }


    private static Date getAfterDate(Date date, int year, int month, int day, int hour, int minute, int second) {
        if (date == null) {
            date = new Date();
        }

        Calendar cal = new GregorianCalendar();

        cal.setTime(date);
        if (year != 0) {
            cal.add(Calendar.YEAR, year);
        }
        if (month != 0) {
            cal.add(Calendar.MONTH, month);
        }
        if (day != 0) {
            cal.add(Calendar.DATE, day);
        }
        if (hour != 0) {
            cal.add(Calendar.HOUR_OF_DAY, hour);
        }
        if (minute != 0) {
            cal.add(Calendar.MINUTE, minute);
        }
        if (second != 0) {
            cal.add(Calendar.SECOND, second);
        }
        return cal.getTime();
    }

    public static class TokenInfo{
        private Long uid;
        private Date expiresAt;

        public TokenInfo(Long uid,Date expiresAt){
            this.uid = uid;
            this.expiresAt = expiresAt;
        }

        public Long getUid() {
            return uid;
        }

        public Date getExpireTime() {
            return expiresAt;
        }

    }

}
