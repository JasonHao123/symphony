package jason.app.symphony.security.comp.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.acls.model.ObjectIdentity;

import jason.app.symphony.security.comp.entity.AclClass;
import jason.app.symphony.security.comp.entity.AclEntry;
import jason.app.symphony.security.comp.entity.AclObjectIdentity;
import jason.app.symphony.security.comp.entity.AclSid;

public interface AclDao {

    List<ObjectIdentity> findChildren(Serializable identifier, String type);

    AclObjectIdentity getObjectIdentity(String type, Serializable identifier);

    void createObjectIdentity(AclObjectIdentity identity);

    List<AclSid> findAclSidList(Boolean valueOf, String sidName);

    AclSid createAclSid(AclSid sid2);

    List<AclClass> findAclClassList(String type);

    AclClass createAclClass(AclClass clazz);

    void deleteEntries(AclObjectIdentity oidPrimaryKey);

    void deleteObjectIdentity(AclObjectIdentity oidPrimaryKey);

    void createEntries(List<AclEntry> entries);

    boolean updateObjectIdentity(AclObjectIdentity aclObject);

    AclSid findAclSid(String principal);

}
