package org.keycloak.federation.ldap.idm.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class LDAPObject {

    private static final Logger logger = Logger.getLogger(LDAPObject.class);

    private String uuid;
    private LDAPDn dn;
    private String rdnAttributeName;

    private final List<String> objectClasses = new LinkedList<>();

    // NOTE: names of read-only attributes are lower-cased to avoid case sensitivity issues
    private final List<String> readOnlyAttributeNames = new LinkedList<>();

    private final Map<String, Set<String>> attributes = new HashMap<>();

    // Copy of "attributes" containing lower-cased keys
    private final Map<String, Set<String>> lowerCasedAttributes = new HashMap<>();


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LDAPDn getDn() {
        return dn;
    }

    public void setDn(LDAPDn dn) {
        this.dn = dn;
    }

    public List<String> getObjectClasses() {
        return objectClasses;
    }

    public void setObjectClasses(Collection<String> objectClasses) {
        this.objectClasses.clear();
        this.objectClasses.addAll(objectClasses);
    }

    public List<String> getReadOnlyAttributeNames() {
        return readOnlyAttributeNames;
    }

    public void addReadOnlyAttributeName(String readOnlyAttribute) {
        readOnlyAttributeNames.add(readOnlyAttribute.toLowerCase());
    }

    public String getRdnAttributeName() {
        return rdnAttributeName;
    }

    public void setRdnAttributeName(String rdnAttributeName) {
        this.rdnAttributeName = rdnAttributeName;
    }

    public void setSingleAttribute(String attributeName, String attributeValue) {
        Set<String> asSet = new LinkedHashSet<>();
        asSet.add(attributeValue);
        setAttribute(attributeName, asSet);
    }

    public void setAttribute(String attributeName, Set<String> attributeValue) {
        attributes.put(attributeName, attributeValue);
        lowerCasedAttributes.put(attributeName.toLowerCase(), attributeValue);
    }

    // Case-insensitive
    public String getAttributeAsString(String name) {
        Set<String> attrValue = lowerCasedAttributes.get(name.toLowerCase());
        if (attrValue == null || attrValue.size() == 0) {
            return null;
        } else if (attrValue.size() > 1) {
            logger.warnf("Expected String but attribute '%s' has more values '%s' on object '%s' . Returning just first value", name, attrValue, dn);
        }

        return attrValue.iterator().next();
    }

    // Case-insensitive. Return null if there is not value of attribute with given name or set with all values otherwise
    public Set<String> getAttributeAsSet(String name) {
        Set<String> values = lowerCasedAttributes.get(name.toLowerCase());
        return (values == null) ? null : new LinkedHashSet<>(values);
    }


    public Map<String, Set<String>> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!getClass().isInstance(obj)) {
            return false;
        }

        LDAPObject other = (LDAPObject) obj;

        return getUuid() != null && other.getUuid() != null && getUuid().equals(other.getUuid());
    }

    @Override
    public int hashCode() {
        int result = getUuid() != null ? getUuid().hashCode() : 0;
        result = 31 * result + (getUuid() != null ? getUuid().hashCode() : 0);
        return result;
    }
}
