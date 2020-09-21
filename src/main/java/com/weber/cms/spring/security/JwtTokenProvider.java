package com.weber.cms.spring.security;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.weber.cms.config.AppConstants;
import com.weber.cms.user.UserService;
import com.weber.cms.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.ttl}")
    private Duration ttl;

    private static final String BEARER_STRING = "Bearer ";

    private final UserService userService;
//    private static final SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

    @Autowired
    public JwtTokenProvider(UserService userService) {
        this.userService = userService;
    }

    public String createCookieTokenString(Authentication authentication) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + ttl.toMillis());
        String jwt = createToken(authentication, expirationDate);
        return AppConstants.JWT_COOKIE_NAME + "=" + jwt + ";Max-Age=" + ttl.toSeconds()+";Path=/";
    }

    public String createToken(User user) {
        return createToken(user, null);
    }

    public String createToken(User user,  Date expirationTime) {
        if (user != null) {
            Claims claims = Jwts.claims().setSubject(user.getId().toString());
            claims.put("perms", user.getPermissions());
            claims.put("username", user.getUsername());
            claims.put("firstName", user.getFirstName());
            claims.put("lastName", user.getLastName());

            Date now = new Date();

            Date validity = expirationTime;

            if (validity == null) {
                validity = new Date(now.getTime() + ttl.toMillis());
            }

            return Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact();
        }
        return null;
    }

    public String createToken(Authentication authentication) {
        return createToken(authentication, null);
    }

    public String createToken(Authentication authentication, Date date) {
        if (authentication != null
            && authentication.isAuthenticated()
            && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return createToken(user, date);
        }

        return null;

    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = getJwtTokenFromRequest(request);
        if (token != null && validateToken(token)) {
            return getAuthentication(token);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        if (username != null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (userDetails != null && userDetails.getUsername() != null) {
                return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            }
        }
        return null;
    }

    public String getUsername(String token) {
        Jws<Claims> jws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        if(jws != null) {
            Claims claims = jws.getBody();
            if (claims != null) {
                return claims.get("username", String.class);
            }
        }
        return null;
    }

    public Date getExpireTime(String token) {
        return Jwts.parser().parseClaimsJwt(token).getBody().getExpiration();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Blah");
//            throw new Run("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return false;
    }

    public String getJwtTokenFromRequest(HttpServletRequest request) {
        String response = getJwtTokenFromAuthHeader(request);
        if (response == null) {
            response = getJwtTokenFromCookie(request);
        }
        return response;
    }

    public String getJwtTokenFromAuthHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER_STRING)) {
            String[] segments =  bearerToken.split(BEARER_STRING);
            if(segments.length > 1) {
                return segments[1];
            }
        }
        return null;
    }

    public String getJwtTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AppConstants.JWT_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
