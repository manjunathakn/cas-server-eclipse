/*
 * Copyright 2010 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/index.html
 */
package org.jasig.cas.client.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Helper class with reflection utility methods.
 *
 * @author Marvin S. Addison
 * @version $Revision: 20701 $
 * @since 3.1.11
 *
 */
public final class ReflectUtils {

    private ReflectUtils() {
        // private constructor to prevent instanciation.
    }

    /**
     * Attempts to create a class from a String.
     * @param className the name of the class to create.
     * @return the class.  CANNOT be NULL.
     * @throws IllegalArgumentException if the className does not exist.
     */
    public static Class loadClass(final String className) throws IllegalArgumentException {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(className + " class not found.");
        }
    }


    /**
     * Creates a new instance of the given class by passing the given arguments
     * to the constructor.
     * @param className Name of class to be created.
     * @param args Constructor arguments.
     * @return New instance of given class.
     */
    public static Object newInstance(final String className, final Object[] args) {
        try {
            return newInstance(Class.forName(className), args);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(className + " not found");
        }
    }
    
    /**
     * Creates a new instance of the given class by passing the given arguments
     * to the constructor.
     * @param clazz Class of instance to be created.
     * @param args Constructor arguments.
     * @return New instance of given class.
     */
    public static Object newInstance(final Class clazz, final Object[] args) {
        final Class[] argClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argClasses[i] = args[i].getClass();
        }
        try {
            return clazz.getConstructor(argClasses).newInstance(args);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Error creating new instance of " + clazz, e);
        }
    }

    /**
     * Gets the property descriptor for the named property on the given class.
     * @param clazz Class to which property belongs.
     * @param propertyName Name of property.
     * @return Property descriptor for given property or null if no property with given
     * name exists in given class.
     */
    public static PropertyDescriptor getPropertyDescriptor(final Class clazz, final String propertyName) {
        try {
            return getPropertyDescriptor(Introspector.getBeanInfo(clazz), propertyName);
        } catch (final IntrospectionException e) {
            throw new RuntimeException("Failed getting bean info for " + clazz, e);
        }
    }

    /**
     * Gets the property descriptor for the named property from the bean info describing
     * a particular class to which property belongs.
     * @param info Bean info describing class to which property belongs.
     * @param propertyName Name of property.
     * @return Property descriptor for given property or null if no property with given
     * name exists.
     */
    public static PropertyDescriptor getPropertyDescriptor(final BeanInfo info, final String propertyName) {
        for (int i = 0; i < info.getPropertyDescriptors().length; i++) {
            final PropertyDescriptor pd = info.getPropertyDescriptors()[i];
            if (pd.getName().equals(propertyName)) {
                return pd;
            }
        }
        return null;
    }

    /**
     * Sets the given property on the target javabean using bean instrospection.
     * @param propertyName Property to set.
     * @param value Property value to set.
     * @param target Target java bean on which to set property.
     */
    public static void setProperty(final String propertyName, final Object value, final Object target) {
        try {
            setProperty(propertyName, value, target, Introspector.getBeanInfo(target.getClass()));
        } catch (final IntrospectionException e) {
            throw new RuntimeException("Failed getting bean info on target javabean " + target, e);
        }
    }

    /**
     * Sets the given property on the target javabean using bean instrospection.
     * @param propertyName Property to set.
     * @param value Property value to set.
     * @param target Target javabean on which to set property.
     * @param info BeanInfo describing the target javabean.
     */
    public static void setProperty(final String propertyName, final Object value, final Object target, final BeanInfo info) {
        try {
            final PropertyDescriptor pd = getPropertyDescriptor(info, propertyName);
            pd.getWriteMethod().invoke(target, new Object[] { value });
        } catch (final InvocationTargetException e) {
            throw new RuntimeException("Error setting property " + propertyName, e.getCause());
        } catch (final Exception e) {
            throw new RuntimeException("Error setting property " + propertyName, e);
        }
    }
}
