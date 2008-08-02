package org.toobs.data.base;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EntityPermission {

  private static final Log log = LogFactory.getLog(EntityPermission.class);
  
  public static final int PERMISSION_NONE       = 0;

  public static final int PERMISSION_READ_ALL   = 1;
  public static final int PERMISSION_CREATE_ALL = 2;
  public static final int PERMISSION_UPDATE_ALL = 4;
  public static final int PERMISSION_DELETE_ALL = 8;

  public static final int PERMISSION_READ_OWN   = 16;
  public static final int PERMISSION_CREATE_OWN = 32;
  public static final int PERMISSION_UPDATE_OWN = 64;
  public static final int PERMISSION_DELETE_OWN = 128;
  /*
  public static final int PERMISSION_ALL        = 255;
  */

  public static final int PERMISSION_READ_BUS_OWNED   = 256;
  public static final int PERMISSION_CREATE_BUS_OWNED = 512;
  public static final int PERMISSION_UPDATE_BUS_OWNED = 1024;
  public static final int PERMISSION_DELETE_BUS_OWNED = 2048;
  
  public static final int PERMISSION_ALL        = 4095;
  
  private int permission;
  private String entityName;
  private String securityMode;
  private String personId;
  private String creatorId;
  private String entityGuid;
  private String personBusinessId;
  private String objectBusinessId;
  private String businessTemplateId;
  
  public EntityPermission(String entityName, int permission, String securityMode, String personId, String entityGuid) {
    this.entityName = entityName;
    this.permission = permission;
    this.securityMode = securityMode;
    this.personId = personId;
    this.entityGuid = entityGuid;
  }

  public boolean hasRead() {
    return hasReadAll() || hasReadOwn() || hasReadBusiness();
  }
  
  public boolean hasReadAll() {
    if ((permission & PERMISSION_READ_ALL) == PERMISSION_READ_ALL) {
      log.info("Read All Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
      return true;
    }
    return false;
  }
  
  public boolean hasReadOwn() {
    if (creatorId != null) {
      if ((permission & PERMISSION_READ_OWN) == PERMISSION_READ_OWN && creatorId.equals(personId)) {
        log.info("Read Own Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
        return true;
      }
    } else if (entityGuid == null) {
      if ((permission & PERMISSION_READ_OWN) == PERMISSION_READ_OWN) {
        log.info("Read Own Filtered Access granted on " + entityName + " to personId:" + personId);
        return true;
      }
    }
    return false;
  }
  
  public boolean hasReadBusiness() {
    if (objectBusinessId != null) {
      if ((permission & PERMISSION_READ_BUS_OWNED) == PERMISSION_READ_BUS_OWNED && (objectBusinessId.equals(personBusinessId) || objectBusinessId.equals(businessTemplateId))) {
        log.info("Read Business Owned Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
        return true;
      }
    } else if (entityGuid == null) {
      if ((permission & PERMISSION_READ_BUS_OWNED) == PERMISSION_READ_BUS_OWNED) {
        log.info("Read Business Owned Filtered Access granted on " + entityName + " to personId:" + personId);
        return true;
      }
    }
    return false;
  }

  public boolean hasCreate() {
    if ((permission & PERMISSION_CREATE_ALL) == PERMISSION_CREATE_ALL) {
      log.info("Create All Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
      return true;
    }
    if (creatorId != null) {
      if ((permission & PERMISSION_CREATE_OWN) == PERMISSION_CREATE_OWN && creatorId.equals(personId)) {
        log.info("Create Own Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
        return true;
      }
    }
    if (objectBusinessId != null) {
      if ((permission & PERMISSION_CREATE_BUS_OWNED) == PERMISSION_CREATE_BUS_OWNED && (objectBusinessId.equals(personBusinessId) || objectBusinessId.equals(businessTemplateId))) {
        log.info("Create Business Owned Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
        return true;
      }
    }
    return false;
  }
  
  public boolean hasUpdate() {
    if ((permission & PERMISSION_UPDATE_ALL) == PERMISSION_UPDATE_ALL) {
      log.info("Update All Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
      return true;
    }
    if (creatorId != null) {
      if ((permission & PERMISSION_UPDATE_OWN) == PERMISSION_UPDATE_OWN && creatorId.equals(personId)) {
        log.info("Update Own Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
        return true;
      }
    }
    if (objectBusinessId != null) {
      if ((permission & PERMISSION_UPDATE_BUS_OWNED) == PERMISSION_UPDATE_BUS_OWNED && (objectBusinessId.equals(personBusinessId) || objectBusinessId.equals(businessTemplateId))) {
        log.info("Update Business Owned Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
        return true;
      }
    }
    return false;
  }

  public boolean hasDelete() {
    if ((permission & PERMISSION_DELETE_ALL) == PERMISSION_DELETE_ALL) {
      log.info("Delete All Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
      return true;
    }
    if (creatorId != null) {
      if ((permission & PERMISSION_DELETE_OWN) == PERMISSION_DELETE_OWN && creatorId.equals(personId)) {
        log.info("Delete Own Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
        return true;
      }
    }
    if (objectBusinessId != null) {
      if ((permission & PERMISSION_DELETE_BUS_OWNED) == PERMISSION_DELETE_BUS_OWNED && (objectBusinessId.equals(personBusinessId) || objectBusinessId.equals(businessTemplateId))) {
        log.info("Delete Business Owned Access granted on " + entityName + " guid:" + entityGuid + " to personId:" + personId);
        return true;
      }
    }
    return false;
  }

  public String getCreatorId() {
    return creatorId;
  }
  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }
  
  public String getEntityGuid() {
    return entityGuid;
  }
  
  public String getEntityName() {
    return entityName;
  }
  
  public int getPermission() {
    return permission;
  }
  
  public String getPersonId() {
    return personId;
  }
  
  public String getSecurityMode() {
    return securityMode;
  }

  public String getObjectBusinessId() {
    return objectBusinessId;
  }

  public void setObjectBusinessId(String objectBusinessId) {
    this.objectBusinessId = objectBusinessId;
  }

  public String getPersonBusinessId() {
    return personBusinessId;
  }

  public void setPersonBusinessId(String personBusinessId) {
    this.personBusinessId = personBusinessId;
  }

  public String getBusinessTemplateId() {
    return businessTemplateId;
  }

  public void setBusinessTemplateId(String businessTemplateId) {
    this.businessTemplateId = businessTemplateId;
  }
}
