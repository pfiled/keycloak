package org.keycloak.util.ldap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.directory.server.kerberos.shared.crypto.encryption.KerberosKeyFactory;
import org.apache.directory.server.kerberos.shared.keytab.Keytab;
import org.apache.directory.server.kerberos.shared.keytab.KeytabEntry;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

/**
 * Helper utility for creating Keytab files.
 *
 * @author Josef Cacek
 */
public class KerberosKeytabCreator {

    // Public methods --------------------------------------------------------

    /**
     * The main.
     *
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length != 3) {
            System.out.println("Kerberos keytab generator");
            System.out.println("-------------------------");
            System.out.println("Arguments missing or invalid. Required arguments are: <principalName> <passPhrase> <outputKeytabFile>");
            System.out.println("Example of usage:");
            System.out.println("java -jar embedded-ldap/target/embedded-ldap.jar keytabCreator HTTP/localhost@KEYCLOAK.ORG httppassword /tmp/http.keytab");
        } else {
            final File keytabFile = new File(args[2]);
            createKeytab(args[0], args[1], keytabFile);
            System.out.println("Keytab file was created: " + keytabFile.getAbsolutePath() + ", principal: " + args[0] + ", passphrase: " + args[1]);
        }
    }

    // Just for the reflection purposes
    public static void execute(String[] args, Properties defaultProperties) throws Exception {
        main(args);
    }

    /**
     * Creates a keytab file for given principal.
     *
     * @param principalName
     * @param passPhrase
     * @param keytabFile
     * @throws IOException
     */
    public static void createKeytab(final String principalName, final String passPhrase, final File keytabFile)
            throws IOException {
        final KerberosTime timeStamp = new KerberosTime();
        final int principalType = 1; // KRB5_NT_PRINCIPAL

        final Keytab keytab = Keytab.getInstance();
        final List<KeytabEntry> entries = new ArrayList<KeytabEntry>();
        for (Map.Entry<EncryptionType, EncryptionKey> keyEntry : KerberosKeyFactory.getKerberosKeys(principalName, passPhrase)
                .entrySet()) {
            System.out.println("Adding keytab entry of type: " + keyEntry.getKey().getName());
            final EncryptionKey key = keyEntry.getValue();
            final byte keyVersion = (byte) key.getKeyVersion();
            entries.add(new KeytabEntry(principalName, principalType, timeStamp, keyVersion, key));
        }
        keytab.setEntries(entries);
        keytab.write(keytabFile);
    }
}
