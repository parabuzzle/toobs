// license-header java merge-point
//
// Attention: Generated code! Do not modify by hand!
// Generated by: ValueObject.vsl in andromda-java-cartridge.
//
package org.toobsframework.data.base;

/**
 * 
 */
public class PermissionInfoVO
    implements java.io.Serializable
{
    /**
     * The serial version UID of this class. Needed for serialization.
     */
    private static final long serialVersionUID = 5078824969220952356L;

    public PermissionInfoVO()
    {
        this.entityName = null;
        this.entity = null;
    }

    public PermissionInfoVO(java.lang.String entityName, java.lang.Object entity)
    {
        this.entityName = entityName;
        this.entity = entity;
    }

    /**
     * Copies constructor from other PermissionInfoVO
     *
     * @param otherBean, cannot be <code>null</code>
     * @throws java.lang.NullPointerException if the argument is <code>null</code>
     */
    public PermissionInfoVO(PermissionInfoVO otherBean)
    {
        this(otherBean.getEntityName(), otherBean.getEntity());
    }

    /**
     * Copies all properties from the argument value object into this value object.
     */
    public void copy(PermissionInfoVO otherBean)
    {
        if (otherBean != null)
        {
            this.setEntityName(otherBean.getEntityName());
            this.setEntity(otherBean.getEntity());
        }
    }

    private java.lang.String entityName;

    /**
     * 
     */
    public java.lang.String getEntityName()
    {
        return this.entityName;
    }

    public void setEntityName(java.lang.String entityName)
    {
        this.entityName = entityName;
    }

    private java.lang.Object entity;

    /**
     * 
     */
    public java.lang.Object getEntity()
    {
        return this.entity;
    }

    public void setEntity(java.lang.Object entity)
    {
        this.entity = entity;
    }

}