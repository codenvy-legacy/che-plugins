/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.runner.client;

import com.google.common.io.Resources;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

/**
 * The class contains methods which are used in several test classes.
 *
 * @author Dmitry Shnurenko
 * @author Andrey Plotnikov
 */
public class TestUtil {

    /**
     * Method returns value of field from this class or its superclass by name.
     *
     * @param object
     *         object from class which need to get field value
     * @param fieldName
     *         field name which need to get
     * @return value of field by name
     * @throws Exception
     */
    public static <T> Object getFieldValueByName(@NotNull T object, @NotNull String fieldName) throws Exception {
        Field field;
        try {
            field = object.getClass().getDeclaredField(fieldName);

            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            field = object.getClass().getSuperclass().getDeclaredField(fieldName);

            field.setAccessible(true);
        }
        return field.get(object);
    }

    /**
     * Do method from superclass by name.
     *
     * @param object
     *         object from super class which need to invoke method
     * @param name
     *         name of method which need to invoke
     * @param arg
     *         argument of method
     * @throws Exception
     */
    public static <T> void invokeMethodByName(@NotNull T object, @NotNull String name, Object arg) throws Exception {
        Method method = object.getClass().getDeclaredMethod(name, Object.class);

        method.setAccessible(true);

        method.invoke(object, arg);
    }

    /**
     * Do method from superclass by name.
     *
     * @param object
     *         object from super class which need to invoke method
     * @param name
     *         name of method which need to invoke
     * @param typeArg
     *         type of method argument
     * @param arg
     *         argument of method
     * @throws Exception
     */
    public static <T> void invokeMethodByName(@NotNull T object,
                                              @NotNull String name,
                                              Class typeArg,
                                              Object arg) throws Exception {
        Method method = object.getClass().getDeclaredMethod(name, typeArg);

        method.setAccessible(true);

        method.invoke(object, arg);
    }

    /**
     * Methods returns value of field from object using index. Index is number field in class.
     *
     * @param object
     *         object from which need to get value of field
     * @param index
     *         index of field for which need get value
     * @return value of field by index
     * @throws Exception
     */
    public static <T> Object getFieldValueByIndex(@NotNull T object, @Min(value=0) int index) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();

        Field field = fields[index];

        field.setAccessible(true);

        return field.get(object);
    }

    /**
     * Returns string content representation by following path.
     *
     * @param clazz
     *         class which uses this method
     * @param path
     *         path to content which need to read
     * @return string representation of content which located by current path
     */
    @NotNull
    public static String getContentByPath(@NotNull Class clazz, @NotNull String path) throws IOException {
        return Resources.toString(Resources.getResource(clazz, path), Charset.defaultCharset());
    }
}