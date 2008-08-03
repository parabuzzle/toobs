package org.toobsframework.data.beanutil;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
//import org.toobsframework.data.base.PermissionedObject;
import org.toobsframework.biz.collections.ICollectionCreator;
import org.toobsframework.biz.validation.IValidator;
//import org.toobsframework.exception.BaseException;
import org.toobsframework.data.ICollectionLoader;
import org.toobsframework.data.IObjectLoader;
import org.toobsframework.data.beanutil.converter.StringToDateConverter;
import org.toobsframework.exception.PermissionException;
import org.toobsframework.exception.ValidationException;
import org.toobsframework.servlet.ContextHelper;
import org.toobsframework.util.Configuration;
import org.toobsframework.util.constants.PlatformConstants;
import org.toobsframework.util.string.StringResource;



@SuppressWarnings("unchecked")
public class BeanMonkey {

  /**
   * To get the logger instance
   */
  private static Log log = LogFactory.getLog(BeanMonkey.class);

  private static BeanFactory beanFactory = BeanMonkey.getBeanFactoryInstance();
  
  private static Class objectClass;
  
  static {
    String objectClassProperty = "";
    try {
      objectClassProperty = Configuration.getInstance().getProperty("toobs.beanmonkey.objectClass"); 
      objectClass = Class.forName(objectClassProperty);
    } catch (ClassNotFoundException e) {
      log.error("Class for toobs.beanmonkey.objectClass property: " + objectClassProperty + " not found");
    }
  }

  /**
   * A utility method that just returns a new instance of a bean factory object.
   * @return
   */
  public static BeanFactory getBeanFactoryInstance()
  {
    return ContextHelper.getInstance().getWebApplicationContext().getParentBeanFactory();    
  }
  
  public static void populate(Object bean, Map properties, Collection errorMessages) 
  throws IllegalAccessException, InvocationTargetException, ValidationException, PermissionException {

    IValidator v = null;
    String className = bean.getClass().getSimpleName();
    String validatorName = className + "Validator";
    if (beanFactory.containsBean(validatorName)) {
      v = (IValidator) beanFactory.getBean(validatorName);
    } else {
      log.warn("No validator " + validatorName + " for " + className);
    }
    if (v != null) {
      v.prePopulate(bean, properties);
    }

    populate(bean, properties, true);
    
    Errors e = new BindException(bean, className.substring(0, className.length()-4));
    
    
    if (properties.containsKey("MultipartValidationError")) {
      String docProperty = (String)properties.get("MultipartValidationField");
      try {
        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(bean, docProperty);
        if (descriptor != null) {
          BeanUtils.setProperty(bean, docProperty, null);
          e.rejectValue(docProperty, 
              docProperty +"."+(String)properties.get("MultipartValidationError"), (String)properties.get("MultipartValidationMessage"));
        }
      } catch (NoSuchMethodException nsm) {
      }
    }
    
    if (v != null) {
      if (e.getAllErrors() == null || e.getAllErrors().size() == 0) {
        v.prepare(bean, properties);
        v.validate(bean, e);
      }
    }
    
    if (e.getAllErrors() != null && e.getAllErrors().size() > 0) {
      errorMessages.addAll(e.getAllErrors());
    }
  }
  
  public static void populate(Object bean, Map properties) throws IllegalAccessException, InvocationTargetException, ValidationException, PermissionException {

    IValidator v = null;
    String className = bean.getClass().getSimpleName();
    String validatorName = className + "Validator";
    if (beanFactory.containsBean(validatorName)) {
      v = (IValidator) beanFactory.getBean(validatorName);
    } else {
      log.warn("No validator " + validatorName + " for " + className);
    }
    
    if (v != null) {
      v.prePopulate(bean, properties);
    }
    
    populate(bean, properties, true);
    
    String objectName = (String) properties.get("returnObjectType") == null ? className
        : (String) properties.get("returnObjectType");
    
    Errors e = new BindException(bean, objectName);

    if (properties.containsKey("MultipartValidationError")) {
      String docProperty = (String)properties.get("MultipartValidationField");
      try {
        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(bean, docProperty);
        if (descriptor != null) {
          BeanUtils.setProperty(bean, docProperty, null);
          e.rejectValue(docProperty, 
              docProperty +"."+(String)properties.get("MultipartValidationError"), (String)properties.get("MultipartValidationMessage"));
        }
      } catch (NoSuchMethodException nsm) {
      }
    }
    
    if (v != null) {
      if (e.getAllErrors() == null || e.getAllErrors().size() == 0) {
        v.prepare(bean, properties);
        v.validate(bean, e);
        if (e.getAllErrors() == null || e.getAllErrors().size() == 0) {
          v.audit(bean, properties);
        }
      }
    }
    
    if (e.getAllErrors() != null && e.getAllErrors().size() > 0) {
      throw new ValidationException(e);
    }
  }

  public static Collection populateCollection(String beanClazz, String indexPropertyName, Map properties, boolean noload, boolean errorMode, Collection errorMessages)
    throws IllegalAccessException, InvocationTargetException, ValidationException, ClassNotFoundException, InstantiationException, PermissionException {
    
    String className = beanClazz.substring(beanClazz.lastIndexOf(".") + 1);
    
    IValidator v = null;
    String validatorName = className + "Validator";
    if (beanFactory.containsBean(validatorName)) {
      v = (IValidator) beanFactory.getBean(validatorName);
    } else {
      log.warn("No validator for " + className);
    }

    Collection validationErrors = new ArrayList();
    Collection returnObjs = populateCollection(v, beanClazz, indexPropertyName, properties, true, noload);
        
    if (v == null) {
      return returnObjs;
    }
    
    String objectName = (String) properties.get("returnObjectType") == null ? className
        : (String) properties.get("returnObjectType");

    /* Validate Collection level properties - size() > 0 etc */
    Errors e = new BindException(returnObjs, objectName);
    v.validateCollection(returnObjs, e);
    if (e.getAllErrors() != null && e.getAllErrors().size() > 0) {
      validationErrors.add(e);
      //return returnObjs;
    } else {
    
      Iterator it = returnObjs.iterator();
      while (it.hasNext()) {
        Object bean = it.next();
        
        v.prepare(bean, properties);
        boolean doCreate = v.doCreateCollectionMember(bean);
        if (!doCreate) { 
          it.remove();
          continue;
        }
        
        e = new BindException(bean, objectName);
        v.validate(bean, e);
        
        /* Safe bean in DoItRunner
        validationErrorObjects.add(v.getSafeBean(bean, properties));
        */
        if (e.getAllErrors() != null && e.getAllErrors().size() > 0) {
          validationErrors.add(e);
        } else if (errorMode) {
          it.remove();
        }
      }
    }
    
    if(validationErrors.size() > 0){
      if (errorMessages != null) {
        Iterator errIter = validationErrors.iterator();
        while (errIter.hasNext()) {
          Errors err = (Errors)errIter.next();
          errorMessages.addAll(err.getAllErrors());
        }        
      } else {
        throw new ValidationException(validationErrors);
      }
    }
    /*
    if(validationErrors.size() > 0){
      //properties.put("ValidationErrors", validationErrors);
      //properties.put("ValidationErrorObject", validationErrorObjects);
      throw new ValidationException(validationErrors);
    }
    */
    //If there are no errors, null out the errorsObjects to conserve memory.
    //validationErrorObjects = null;
    
    return returnObjs;
  }

  public static Collection populateCollection(IValidator v, String beanClazz, String indexPropertyName,
      Map properties, boolean validate, boolean noload) throws IllegalAccessException,
      InvocationTargetException, ValidationException, 
      ClassNotFoundException, InstantiationException, PermissionException {

    // Do nothing unless all arguments have been specified
    if ((beanClazz == null) || (properties == null) || (indexPropertyName == null)) {
      log.warn("Proper parameters not present.");
      return null;
    }
    
    ArrayList returnObjs = new ArrayList();

    Object[] indexProperty = (Object[]) properties.get(indexPropertyName);
    if (indexProperty == null){
      log.warn("indexProperty [" + indexProperty + "] does not exist in the map.");
      return returnObjs;
    }
    
    Class beanClass = Class.forName(beanClazz);
    
    String beanClazzName = beanClass.getSimpleName(); //  beanClazz.substring(beanClazz.lastIndexOf(".") + 1);
    IObjectLoader odao = null;
    ICollectionLoader cdao = null;
    if (objectClass.isAssignableFrom(beanClass)) {
      odao = (IObjectLoader) beanFactory.getBean(Introspector.decapitalize(beanClazzName.substring(0,beanClazzName.length()-4)) + "Dao");
      if (odao == null) {
        throw new InvocationTargetException(new Exception("Object DAO class " + Introspector.decapitalize(beanClazzName) + "Dao could not be loaded"));
      }
    } else {
      cdao = (ICollectionLoader) beanFactory.getBean(Introspector.decapitalize(beanClazzName.substring(0,beanClazzName.length()-4)) + "Dao");
      if (cdao == null) {
        throw new InvocationTargetException(new Exception("Collection DAO class " + Introspector.decapitalize(beanClazzName) + "Dao could not be loaded"));
      }
    }
    
    boolean namespaceStrict = properties.containsKey("namespaceStrict");
    
    for(int index = 0; index < indexProperty.length; index++) {
      String guid = (String)indexProperty[index];
      
      boolean newBean = false;
      Object bean = null;
      if (!noload && guid != null && guid.length() > 0) {
        bean = (odao != null) ? odao.load(guid) : cdao.load(Integer.parseInt(guid));
      } 
      if (bean == null) {
        bean = Class.forName(beanClazz).newInstance();
        newBean = true;
      }
      if (v != null) {
        v.prePopulate(bean, properties);
      }
      if (log.isDebugEnabled()) {
        log.debug("BeanMonkey.populate(" + bean + ", " + properties + ")");
      }
  
      Errors e = null;
  
      if (validate) {
        String beanClassName = null;
        beanClassName = bean.getClass().getName();
        beanClassName = beanClassName
            .substring(beanClassName.lastIndexOf(".") + 1);
        e = new BindException(bean, beanClassName);
      }
  
      String namespace = null;
      if (properties.containsKey("namespace") && !"".equals(properties.get("namespace"))) {
        namespace = (String)properties.get("namespace") + ".";
      }

      // Loop through the property name/value pairs to be set
      Iterator names = properties.keySet().iterator();
      while (names.hasNext()) {
  
        // Identify the property name and value(s) to be assigned
        String name = (String) names.next();
        if (name == null || (indexPropertyName.equals(name) && !noload) || (namespaceStrict && !name.startsWith(namespace))) {
          continue;
        }

        Object value = null;
        if(properties.get(name) == null) {
          log.warn("Property [" + name + "] does not have a value in the map.");
          continue;
        }
        if(properties.get(name).getClass().isArray() && index < ((Object[])properties.get(name)).length){
          value = ((Object[])properties.get(name))[index];
        } else if(properties.get(name).getClass().isArray()) {
          value = ((Object[])properties.get(name))[0];
        } else {
          value = properties.get(name);          
        }
        if (namespace != null) {
          name = name.replace(namespace, "");
        }
  
        PropertyDescriptor descriptor = null;
        Class type = null; // Java type of target property
        try {
          descriptor = PropertyUtils.getPropertyDescriptor(bean, name);
          if (descriptor == null) {
            continue; // Skip this property setter
          }
        } catch (NoSuchMethodException nsm) {
          continue; // Skip this property setter
        } catch (IllegalArgumentException iae) {
          continue; // Skip null nested property
        }
        if (descriptor.getWriteMethod() == null) {
          if (log.isDebugEnabled()) {
            log.debug("Skipping read-only property");
          }
          continue; // Read-only, skip this property setter
        }
        type = descriptor.getPropertyType();
        String className = type.getName();
  
        try {
          value = evaluatePropertyValue(name, className, namespace, value, properties, bean);
        } catch (NoSuchMethodException nsm) {
          continue;
        }
  
        try {
          BeanUtils.setProperty(bean, name, value);
        } catch (ConversionException ce) { 
          log.error("populate - exception [bean:" + bean.getClass().getName() + " name:" + name + " value:" + value + "] ");
          if (validate) {
            e.rejectValue(name, name + ".conversionError", ce.getMessage());
          } else {
            throw new ValidationException(bean, className, name, ce.getMessage());
          }
        } catch (Exception be) {
          log.error("populate - exception [bean:" + bean.getClass().getName() + " name:" + name + " value:" + value + "] ");
          if (validate) {
            e.rejectValue(name, name + ".error", be.getMessage());
          } else {
            throw new ValidationException(bean, className, name, be.getMessage());
          }
        }
      }
      /*
      if (newBean && cdao != null) {
        BeanUtils.setProperty(bean, "id", -1);
      }
      */
      if (validate && e.getErrorCount() > 0) {
        throw new ValidationException(e);
      }
    
      returnObjs.add(bean);
    }
    
    return returnObjs;
  }
  
  public static void populate(Object bean, Map properties, boolean validate)
      throws IllegalAccessException, InvocationTargetException,
      ValidationException, PermissionException {

    // Do nothing unless both arguments have been specified
    if ((bean == null) || (properties == null)) {
      return;
    }
    if (log.isDebugEnabled()) {
      log.debug("BeanMonkey.populate(" + bean + ", " + properties + ")");
    }

    Errors e = null;
    if (validate) {
      String beanClassName = null;
      beanClassName = bean.getClass().getName();
      beanClassName = beanClassName
          .substring(beanClassName.lastIndexOf(".") + 1);
      e = new BindException(bean, beanClassName);
    }
    
    String namespace = null;
    if (properties.containsKey("namespace") && !"".equals(properties.get("namespace"))) {
      namespace = (String)properties.get("namespace") + ".";
    }
    
    // Loop through the property name/value pairs to be set
    Iterator names = properties.keySet().iterator();
    while (names.hasNext()) {

      // Identify the property name and value(s) to be assigned
      String name = (String) names.next();
      if (name == null) {
        continue;
      }
      Object value = properties.get(name);
      if (namespace != null) {
        name = name.replace(namespace, "");
      }
      
      PropertyDescriptor descriptor = null;
      Class type = null; // Java type of target property
      try {
        descriptor = PropertyUtils.getPropertyDescriptor(bean, name);
        if (descriptor == null) {
          continue; // Skip this property setter
        }
      } catch (NoSuchMethodException nsm) {
        continue; // Skip this property setter
      } catch (IllegalArgumentException iae) {
        continue; // Skip null nested property
      }
      
      if (descriptor.getWriteMethod() == null) {
        if (log.isDebugEnabled()) {
          log.debug("Skipping read-only property");
        }
        continue; // Read-only, skip this property setter
      }
      type = descriptor.getPropertyType();
      String className = type.getName();

      try {
        value = evaluatePropertyValue(name, className, namespace,
            value, properties, bean);
      } catch (NoSuchMethodException nsm) {
        continue;
      }

      try {
        if(value != null) {
          BeanUtils.setProperty(bean, name, value);
        }
      } catch (ConversionException ce) { 
        log.error("populate - exception [bean:" + bean.getClass().getName() + " name:" + name + " value:" + value + "] ");
        if (validate) {
          e.rejectValue(name, name + ".conversionError", ce.getMessage());
        } else {
          throw new ValidationException(bean, className, name, ce.getMessage());
        }
      } catch (Exception be) {
        log.error("populate - exception [bean:" + bean.getClass().getName() + " name:" + name + " value:" + value + "] ");
        if (validate) {
          e.rejectValue(name, name + ".error", be.getMessage());
        } else {
          throw new ValidationException(bean, className, name, be.getMessage());
        }
      }
    }
    if (validate && e.getErrorCount() > 0) {
      throw new ValidationException(e);
    }
  }

  private static Object evaluatePropertyValue(String name, String className, String namespace,
      Object value, Map properties, Object bean)
      throws InvocationTargetException, IllegalAccessException,
      NoSuchMethodException, PermissionException {
    // Perform the assignment for this property
    if (className.startsWith(Configuration.getInstance().getProperty("toobs.beanmonkey.dataPackage"))) {
      className = className.substring(className.lastIndexOf(".") + 1);
      
      IObjectLoader odao = null;
      ICollectionLoader cdao = null;
      Object daoObject = beanFactory.getBean(Introspector.decapitalize(className) + "Dao");
      if (daoObject == null) {
        throw new InvocationTargetException(new Exception("DAO class " + Introspector.decapitalize(className) + "Dao could not be loaded"));
      }
      if (daoObject instanceof IObjectLoader) {
        odao = (IObjectLoader)daoObject;
      } else {
        cdao = (ICollectionLoader)daoObject;
      }
      
      String guid = null;
      if (value != null && value.getClass().isArray()) {
        if(properties.containsKey(PlatformConstants.MULTI_ACTION_INSTANCE)) {
          Object[] oldValue = (Object[]) value;
          Integer instance = (Integer)properties.get(PlatformConstants.MULTI_ACTION_INSTANCE);
          if (oldValue.length >= instance + 1) {
            guid = ((String[])oldValue)[instance];
          } else {
            throw new RuntimeException("Instance " + instance + " not found in " + oldValue + " for: " + name + " class: " + className + " in: " + bean);
          }
        } else {
          guid = ((String[]) value)[0];
        }
      } else {
        guid = (String) value;
      }
      if (guid != null && guid.length() > 0) {
        String personId = (String) properties.get("personId");
        try {
          if (odao != null) {
            value = odao.load(guid);
          } else {
            value = cdao.load(Integer.parseInt(guid));
          }
        } catch (PermissionException pe) {
          log.error("PermissionException loading object " + className + "." + name + " with guid " + guid + " by person " + personId );
          throw pe;
        } finally { }
      } else {
        value = null;
      }
    } else if ((className.equals("java.util.ArrayList") || className
        .equals("java.util.List"))
        && !(value instanceof java.util.List)) {
      Object[] values = null;
      if (value != null && value.getClass().isArray()) {
        values = ((Object[]) value);
      } else {
        values = new Object[1];
        values[0] = (Object) value;
      }
      value = new ArrayList();
      for (int aa = 0; aa < values.length; aa++) {
        if (!"".equals(values[aa]))
          ((ArrayList) value).add(values[aa]);
      }
      if (((ArrayList) value).size() == 0)
        value = null;
    } else if (className.equals("java.util.Collection")) {
      className = null;
      String typeProp = (namespace != null ? namespace : "") + name + "-Type";
      if (properties.get(typeProp) != null && properties.get(typeProp).getClass().isArray()) {
        className = ((String[]) properties.get(typeProp))[0];
      } else {
        className = (String) properties.get(typeProp);
      }

      if (className == null) {
        throw new InvocationTargetException(new Exception(
            "Missing collection type for " + name));
      }
      
      IObjectLoader odao = null;
      ICollectionLoader cdao = null;
      Object daoObject = beanFactory.getBean(Introspector.decapitalize(className) + "Dao");
      if (daoObject == null) {
        throw new InvocationTargetException(new Exception("DAO class " + Introspector.decapitalize(className) + "Dao could not be loaded"));
      }
      if (daoObject instanceof IObjectLoader) {
        odao = (IObjectLoader)daoObject;
      } else {
        cdao = (ICollectionLoader)daoObject;
      }
      
      Object[] guids = null;
      if (value != null && value.getClass().isArray()) {
        guids = ((Object[]) value);
      } else if (value != null && value instanceof ArrayList) {
        guids = new Object[((ArrayList)value).size()];
        for (int i = 0; i < guids.length; i++) {
          guids[i] = (Object)((ArrayList)value).get(i);
        }
      } else {
        guids = new Object[1];
        guids[0] = (Object) value;
      }

      String personId = (String) properties.get("personId");

      java.util.Collection valueList = (java.util.Collection) PropertyUtils.getProperty(bean, name);
      valueList.clear();
      
      for (int i = 0; i < guids.length; i++) {
        if (odao != null) {
          if (((String)guids[i]).length() == 0) continue;
          value = odao.load((String)guids[i]);
        } else if (cdao != null) {
          value = cdao.load((Integer)guids[i]);
        }
        if (value != null) {
          valueList.add(value);
        } else {
          if (beanFactory.containsBean(className + "CollectionCreator")) {
            ICollectionCreator collectionCreator = (ICollectionCreator)beanFactory.getBean(className + "CollectionCreator");
            try {
              collectionCreator.addCollectionElements(guids[i], valueList, properties, personId, namespace);
            } catch (Exception e) {
              throw new InvocationTargetException(e);
            }
          }
        }
      }
      value = valueList;
    } else if(className.equals("java.lang.String") && value != null && value.getClass().isArray() && ((Object[]) value).length > 1 && properties.containsKey(PlatformConstants.MULTI_ACTION_INSTANCE)) {
      Object[] oldValue = (Object[]) value;
      Integer instance = (Integer)properties.get(PlatformConstants.MULTI_ACTION_INSTANCE);
      if (oldValue.length >= instance + 1) {
        value = oldValue[instance];
      } else {
        throw new RuntimeException("Instance " + instance + " not found in " + oldValue + " for: " + name + " class: " + className + " in: " + bean);
      }
    } else if(className.equals("java.lang.String") && value != null && value.getClass().isArray() && ((Object[]) value).length > 1) {
      Object[] oldValue = (Object[]) value;
      String newValue = new String();
      for (int i = 0; i < oldValue.length; i++) {
        if(i > 0) {
        newValue = newValue + ";";
        }
        newValue = newValue + oldValue[i];
      }
      value = newValue;
    } else if((className.equals("java.lang.Integer") || className.equals("java.lang.Boolean")) && value != null) {
      if (value.getClass().isArray() && ((Object[]) value).length == 1 && ((Object[]) value)[0].equals("")) {
        value = null;
      } else if (!value.getClass().isArray() && String.valueOf(value).equals("")) {
        value = null;
      }
    } else if(className.equals("java.util.Date") && value != null) {
      if (value.getClass().isArray()) {
        value = StringToDateConverter.convert(value);
      }
    }
    
    return value;
  }

  public static Method findMethod(Class clazz, String methodName, Class paramType) {
    return MethodUtils.getAccessibleMethod(clazz, methodName, paramType);
  }
  
  public static Method findMethod(Class clazz, String methodName, Class[] paramTypes) {
    return MethodUtils.getAccessibleMethod(clazz, methodName, paramTypes);
  }
  
  public static String concatPropertyList(Object bean, String propertyList,
      boolean quoted) throws NoSuchMethodException, InvocationTargetException,
      IllegalAccessException {
    StringBuffer sb = new StringBuffer();
    concatPropertyList(bean, propertyList, sb, quoted);
    if (log.isDebugEnabled()) {
      log.debug("Concat result: " + sb.toString().trim());
    }
    return sb.toString().trim();
  }

  public static void concatPropertyList(Object bean, String propertyList,
      StringBuffer result, boolean quoted) throws NoSuchMethodException,
      InvocationTargetException, IllegalAccessException {
    if (bean == null || propertyList == null || result == null) {
      return;
    }

    StringTokenizer st = new StringTokenizer(propertyList, ",");
    while (st.hasMoreTokens()) {
      String name = st.nextToken();
      Object value = PropertyUtils.getProperty(bean, name);
      if (value instanceof String && value != null) {
        result.append(" ");
        if (quoted)
          result.append("\"");
        result.append(value);
        if (quoted)
          result.append("\"");
      } else if (value instanceof Collection && value != null) {
        Iterator valIter = ((Collection) value).iterator();
        while (valIter.hasNext()) {
          concatPropertyList(valIter.next(), "displayName", result, quoted);
          // result.append(" " + ConvertUtils.convert(valIter.next()));
        }
      } else if (value != null) {
        result.append(" ");
        if (quoted)
          result.append("\"");
        result.append(ConvertUtils.convert(value));
        if (quoted)
          result.append("\"");
      }
    }
  }

  public static String getSummary(String input, int length) {
    if (input == null) {
      return input;
    }

    String strippedString = StringResource.stripTags(input, false);

    if (strippedString == null || strippedString.length() <= length - 3) {
      return strippedString;// + "..."
    }

    return strippedString.substring(0, length - 3) + "...";
  }

  public static Object getPropertyValue(Object bean, String propertyPath) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Object value = null;
    try {
      value = PropertyUtils.getProperty(bean, propertyPath);
    } catch (NestedNullException nne) { }
    return value;
  }
}
