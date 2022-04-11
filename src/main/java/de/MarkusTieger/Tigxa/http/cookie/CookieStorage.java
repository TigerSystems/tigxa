package de.MarkusTieger.Tigxa.http.cookie;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yubico.client.v2.VerificationResponse;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.gui.window.PasswordWindow;
import lombok.Setter;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.*;
import java.util.function.Function;

public class CookieStorage {

    private final File configRoot;

    public CookieStorage(File configRoot) {
        this.configRoot = configRoot;
    }

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CookieData.class, new CookieData.CookieDataSerializer())
            .setPrettyPrinting().create();

    @Setter
    private char[] pwd = null;

    @Setter
    private VerificationResponse yubi = null;

    public void load() {

        File cookieFile = new File(configRoot, "tigxa-cookies.json");

        if (cookieFile.exists()) {

            try {
                FileInputStream reader = new FileInputStream(cookieFile);

                char c = new String(new byte[] {(byte)reader.read()}, StandardCharsets.UTF_8).charAt(0);
                char c2 = new String(new byte[] {(byte)reader.read()}, StandardCharsets.UTF_8).charAt(0);

                byte[] bytes = reader.readAllBytes();

                reader.close();

                if(c2 == '1'){
                    bytes = decryptPWD(bytes);
                    if(bytes == null){
                        buckets.clear();
                        return;
                    }
                }
                if(c == '1'){
                    bytes = decryptYUBI(bytes);
                    if(bytes == null){
                        buckets.clear();
                        return;
                    }
                }

                String str = new String(bytes, StandardCharsets.UTF_8);
                CookieData data = GSON.fromJson(str, CookieData.class);

                buckets.clear();
                if(data != null) buckets.putAll(data.buckets);

            } catch (Throwable e) {
                e.printStackTrace();
            }

        }

    }

    private byte[] decryptYUBI(final byte[] bytes) {

        Function<VerificationResponse, byte[]> verify = (yubi) -> {

        try {

            if(!yubi.isOk()) throw new IOException("Valid Response required!");

            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(new String(yubi.getPublicId()).substring(0, 12).toCharArray(), Browser.FULL_NAME.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

            CookieStorage.this.yubi = yubi;

            return cipher.doFinal(bytes);
        } catch (Throwable e){
            return null;
        }
        };

        return PasswordWindow.requestYUBI("Cookie-Storage Unlock - [ Yubi-Key Auth ]", verify);
    }

    private byte[] decryptPWD(final byte[] bytes) {

        Function<char[], byte[]> verify = (pwd) -> {

            try {
                byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                IvParameterSpec ivspec = new IvParameterSpec(iv);

                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(pwd, Browser.FULL_NAME.getBytes(), 65536, 256);
                SecretKey tmp = factory.generateSecret(spec);
                SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

                CookieStorage.this.pwd = pwd;

                return cipher.doFinal(bytes);
            } catch (Throwable e){
                return null;
            }
        };

        return PasswordWindow.requestPWD("Cookie-Storage Unlock - [ Password Auth ]", verify);
    }

    public void save() {

        File cookieFile = new File(configRoot, "tigxa-cookies.json");

        if (!cookieFile.exists()) {
            try {
                cookieFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream writer = new FileOutputStream(cookieFile);
            CookieData data = new CookieData();
            data.buckets = buckets;

            String str = GSON.toJson(data);

            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

            if(yubi == null || !yubi.isOk()) {
                writer.write("0".getBytes(StandardCharsets.UTF_8));
            } else {
                writer.write("1".getBytes(StandardCharsets.UTF_8));

                byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                IvParameterSpec ivspec = new IvParameterSpec(iv);

                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(yubi.getPublicId().toCharArray(), Browser.FULL_NAME.getBytes(), 65536, 256);
                SecretKey tmp = factory.generateSecret(spec);
                SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

                bytes = cipher.doFinal(bytes);
            }

            if(pwd == null || pwd.length == 0){
                writer.write("0".getBytes(StandardCharsets.UTF_8));
            } else {
                writer.write("1".getBytes(StandardCharsets.UTF_8));

                byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                IvParameterSpec ivspec = new IvParameterSpec(iv);

                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(pwd, Browser.FULL_NAME.getBytes(), 65536, 256);
                SecretKey tmp = factory.generateSecret(spec);
                SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

                bytes = cipher.doFinal(bytes);
            }

            writer.write(bytes);

            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private static final PlatformLogger logger =
            PlatformLogger.getLogger(CookieStorage.class.getName());


    private static final int MAX_BUCKET_SIZE = 50;
    private static final int TOTAL_COUNT_LOWER_THRESHOLD = 3000;
    private static final int TOTAL_COUNT_UPPER_THRESHOLD = 4000;


    /**
     * The mapping from domain names to cookie buckets.
     * Each cookie bucket stores the cookies associated with the
     * corresponding domain. Each cookie bucket is represented
     * by a Map<Cookie,Cookie> to facilitate retrieval of a cookie
     * by another cookie with the same name, domain, and path.
     */
    private final Map<String, Map<Cookie, Cookie>> buckets =
            new HashMap<String, Map<Cookie, Cookie>>();

    /**
     * The total number of cookies currently in the store.
     */
    private int totalCount = 0;


    /**
     * Returns the currently stored cookie with the same name, domain, and
     * path as the given cookie.
     */
    Cookie get(Cookie cookie) {
        Map<Cookie, Cookie> bucket = buckets.get(cookie.getDomain());
        if (bucket == null) {
            return null;
        }
        Cookie storedCookie = bucket.get(cookie);
        if (storedCookie == null) {
            return null;
        }
        if (storedCookie.hasExpired()) {
            bucket.remove(storedCookie);
            totalCount--;
            log("Expired cookie removed by get", storedCookie, bucket);
            return null;
        }
        return storedCookie;
    }


    /**
     * Returns all the currently stored cookies that match the given query.
     */
    List<Cookie> get(String hostname, String path, boolean secureProtocol,
                     boolean httpApi) {
        if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
            logger.finest("hostname: [{0}], path: [{1}], "
                    + "secureProtocol: [{2}], httpApi: [{3}]", hostname, path, secureProtocol, httpApi);
        }

        ArrayList<Cookie> result = new ArrayList<Cookie>();

        String domain = hostname;
        while (domain.length() > 0) {
            Map<Cookie, Cookie> bucket = buckets.get(domain);
            if (bucket != null) {
                find(result, bucket, hostname, path, secureProtocol, httpApi);
            }
            int nextPoint = domain.indexOf('.');
            if (nextPoint != -1) {
                domain = domain.substring(nextPoint + 1);
            } else {
                break;
            }
        }

        Collections.sort(result, new GetComparator());

        long currentTime = System.currentTimeMillis();
        for (Cookie cookie : result) {
            cookie.setLastAccessTime(currentTime);
        }

        logger.finest("result: {0}", result);
        return result;
    }

    /**
     * Finds all the cookies that are stored in the given bucket and
     * match the given query.
     */
    private void find(List<Cookie> list, Map<Cookie, Cookie> bucket,
                      String hostname, String path, boolean secureProtocol,
                      boolean httpApi) {
        Iterator<Cookie> it = bucket.values().iterator();
        while (it.hasNext()) {
            Cookie cookie = it.next();
            if (cookie.hasExpired()) {
                it.remove();
                totalCount--;
                log("Expired cookie removed by find", cookie, bucket);
                continue;
            }

            if (cookie.getHostOnly()) {
                if (!hostname.equalsIgnoreCase(cookie.getDomain())) {
                    continue;
                }
            } else {
                if (!Cookie.domainMatches(hostname, cookie.getDomain())) {
                    continue;
                }
            }

            if (!Cookie.pathMatches(path, cookie.getPath())) {
                continue;
            }

            if (cookie.getSecureOnly() && !secureProtocol) {
                continue;
            }

            if (cookie.getHttpOnly() && !httpApi) {
                continue;
            }

            list.add(cookie);
        }
    }

    private static final class GetComparator implements Comparator<Cookie> {
        @Override
        public int compare(Cookie c1, Cookie c2) {
            int d = c2.getPath().length() - c1.getPath().length();
            if (d != 0) {
                return d;
            }
            return c1.getCreationTime().compareTo(c2.getCreationTime());
        }
    }

    /**
     * Stores the given cookie.
     */
    void put(Cookie cookie) {
        Map<Cookie, Cookie> bucket = buckets.get(cookie.getDomain());
        if (bucket == null) {
            bucket = new LinkedHashMap<Cookie, Cookie>(20);
            buckets.put(cookie.getDomain(), bucket);
        }
        if (cookie.hasExpired()) {
            log("Cookie expired", cookie, bucket);
            if (bucket.remove(cookie) != null) {
                totalCount--;
                log("Expired cookie removed by put", cookie, bucket);
            }
        } else {
            if (bucket.put(cookie, cookie) == null) {
                totalCount++;
                log("Cookie added", cookie, bucket);
                if (bucket.size() > MAX_BUCKET_SIZE) {
                    purge(bucket);
                }
                if (totalCount > TOTAL_COUNT_UPPER_THRESHOLD) {
                    purge();
                }
            } else {
                log("Cookie updated", cookie, bucket);
            }
        }
        save();
    }

    /**
     * Removes excess cookies from a given bucket.
     */
    private void purge(Map<Cookie, Cookie> bucket) {
        logger.finest("Purging bucket: {0}", bucket.values());

        Cookie earliestCookie = null;
        Iterator<Cookie> it = bucket.values().iterator();
        while (it.hasNext()) {
            Cookie cookie = it.next();
            if (cookie.hasExpired()) {
                it.remove();
                totalCount--;
                log("Expired cookie removed", cookie, bucket);
            } else {
                if (earliestCookie == null || cookie.getLastAccessTime()
                        < earliestCookie.getLastAccessTime()) {
                    earliestCookie = cookie;
                }
            }
        }
        if (bucket.size() > MAX_BUCKET_SIZE) {
            bucket.remove(earliestCookie);
            totalCount--;
            log("Excess cookie removed", earliestCookie, bucket);
        }
        save();
    }

    /**
     * Removes excess cookies globally.
     */
    private void purge() {
        logger.finest("Purging store");

        Queue<Cookie> removalQueue = new PriorityQueue<Cookie>(totalCount / 2,
                new CookieStorage.RemovalComparator());

        for (Map.Entry<String, Map<Cookie, Cookie>> entry : buckets.entrySet()) {
            Map<Cookie, Cookie> bucket = entry.getValue();
            Iterator<Cookie> it = bucket.values().iterator();
            while (it.hasNext()) {
                Cookie cookie = it.next();
                if (cookie.hasExpired()) {
                    it.remove();
                    totalCount--;
                    log("Expired cookie removed", cookie, bucket);
                } else {
                    removalQueue.add(cookie);
                }
            }
        }

        while (totalCount > TOTAL_COUNT_LOWER_THRESHOLD) {
            Cookie cookie = removalQueue.remove();
            Map<Cookie, Cookie> bucket = buckets.get(cookie.getDomain());
            if (bucket != null) {
                bucket.remove(cookie);
                totalCount--;
                log("Excess cookie removed", cookie, bucket);
            }
        }
        save();
    }

    private static final class RemovalComparator implements Comparator<Cookie> {
        @Override
        public int compare(Cookie c1, Cookie c2) {
            return (int) (c1.getLastAccessTime() - c2.getLastAccessTime());
        }
    }

    /**
     * Logs a cookie event.
     */
    private void log(String message, Cookie cookie,
                     Map<Cookie, Cookie> bucket) {
        if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
            logger.finest("{0}: {1}, bucket size: {2}, total count: {3}",
                    message, cookie, bucket.size(), totalCount);
        }
    }
}
