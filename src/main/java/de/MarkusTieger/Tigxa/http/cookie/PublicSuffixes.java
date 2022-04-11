package de.MarkusTieger.Tigxa.http.cookie;

import java.io.*;
import java.net.IDN;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PublicSuffixes {

    private static final PlatformLogger logger =
            PlatformLogger.getLogger(PublicSuffixes.class.getName());


    /**
     * Public suffix list rule types.
     */
    private enum Rule {
        SIMPLE_RULE,
        WILDCARD_RULE,
        EXCEPTION_RULE,
    }


    /**
     * The mapping from top-level domain names to public suffix list rules.
     */
    private static final Map<String, PublicSuffixes.Rules> rulesCache = new ConcurrentHashMap<>();


    /**
     * The public suffix list file.
     */
    @SuppressWarnings("removal")
    private static final File pslFile = AccessController.doPrivileged((PrivilegedAction<File>)
            () -> new File(System.getProperty("java.home"), "lib/security/public_suffix_list.dat"));


    /*
     * Determines whether the public suffix list file is available.
     */
    @SuppressWarnings("removal")
    private static final boolean pslFileExists = AccessController.doPrivileged(
            (PrivilegedAction<Boolean>) () -> {
                if (!pslFile.exists()) {
                    logger.warning("Resource not found: " +
                            "lib/security/public_suffix_list.dat");
                    return false;
                }
                return true;
            });


    /**
     * The private default constructor. Ensures non-instantiability.
     */
    private PublicSuffixes() {
        throw new AssertionError();
    }


    /**
     * Returns whether the public suffix list file is available.
     */
    static boolean pslFileExists() {
        return pslFileExists;
    }


    /**
     * Determines if a domain is a public suffix.
     */
    static boolean isPublicSuffix(String domain) {
        if (domain.length() == 0) {
            return false;
        }

        if (!pslFileExists()) {
            return false;
        }

        PublicSuffixes.Rules rules = PublicSuffixes.Rules.getRules(domain);
        return rules != null && rules.match(domain);
    }

    private static class Rules {

        private final Map<String, PublicSuffixes.Rule> rules = new HashMap<>();

        private Rules(InputStream is) throws IOException {
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);

            String line;
            int type = reader.read();
            while (type != -1 && (line = reader.readLine()) != null) {
                PublicSuffixes.Rule rule;
                if (line.startsWith("!")) {
                    line = line.substring(1);
                    rule = PublicSuffixes.Rule.EXCEPTION_RULE;
                } else if (line.startsWith("*.")) {
                    line = line.substring(2);
                    rule = PublicSuffixes.Rule.WILDCARD_RULE;
                } else {
                    rule = PublicSuffixes.Rule.SIMPLE_RULE;
                }
                try {
                    line = IDN.toASCII(line, IDN.ALLOW_UNASSIGNED);
                } catch (Exception ex) {
                    logger.warning(String.format("Error parsing rule: [%s]", line), ex);
                    continue;
                }
                rules.put(line, rule);
                type = reader.read();
            }
            if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
                logger.finest("rules: {0}", toLogString(rules));
            }
        }

        static PublicSuffixes.Rules getRules(String domain) {
            String tld = getTopLevelDomain(domain);
            if (tld.isEmpty()) {
                return null;
            }
            return rulesCache.computeIfAbsent(tld, k -> createRules(tld));
        }

        private static String getTopLevelDomain(String domain) {
            domain = IDN.toUnicode(domain, IDN.ALLOW_UNASSIGNED);
            int n = domain.lastIndexOf('.');
            if (n == -1) {
                return domain;
            }
            return domain.substring(n + 1);
        }

        private static PublicSuffixes.Rules createRules(String tld) {
            try (InputStream pubSuffixStream = getPubSuffixStream()) {
                if (pubSuffixStream == null) {
                    return null;
                }
                ZipInputStream zis = new ZipInputStream(pubSuffixStream);
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    if (ze.getName().equals(tld)) {
                        return new PublicSuffixes.Rules(zis);
                    } else {
                        ze = zis.getNextEntry();
                    }
                }
            } catch (IOException ex) {
                logger.warning("Unexpected error", ex);
            }
            return null;
        }

        private static InputStream getPubSuffixStream() {
            @SuppressWarnings("removal")
            InputStream is = AccessController.doPrivileged(
                    (PrivilegedAction<InputStream>) () -> {
                        try {
                            return new FileInputStream(pslFile);
                        } catch (FileNotFoundException ex) {
                            logger.warning("Resource not found: " +
                                    "lib/security/public_suffix_list.dat");
                            return null;
                        }
                    }
            );
            return is;
        }

        boolean match(String domain) {
            PublicSuffixes.Rule rule = rules.get(domain);
            if (rule == PublicSuffixes.Rule.EXCEPTION_RULE) {
                return false;
            } else if (rule == PublicSuffixes.Rule.SIMPLE_RULE || rule == PublicSuffixes.Rule.WILDCARD_RULE) {
                return true;
            } else {
                int pos = domain.indexOf('.') + 1;
                if (pos == 0) {
                    pos = domain.length();
                }
                String parent = domain.substring(pos);
                return rules.get(parent) == PublicSuffixes.Rule.WILDCARD_RULE;
            }
        }
    }

    /**
     * Converts a map of rules to a string suitable for displaying
     * in the log.
     */
    private static String toLogString(Map<String, PublicSuffixes.Rule> rules) {
        if (rules.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, PublicSuffixes.Rule> entry : rules.entrySet()) {
            sb.append(String.format("%n    "));
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

}
