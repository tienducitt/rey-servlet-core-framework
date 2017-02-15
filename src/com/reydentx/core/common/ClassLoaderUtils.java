/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 *
 * @author ducnt3
 */
public class ClassLoaderUtils {

        private static Class[] EMPTY_CLASS_ARR = {};
        
        public static Set<Class<? extends Object>> getAllClass(String packagePath) {
                List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
                classLoadersList.add(ClasspathHelper.contextClassLoader());
                classLoadersList.add(ClasspathHelper.staticClassLoader());
                Reflections reflections = new Reflections(new ConfigurationBuilder()
                        .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                        .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packagePath))));

                Set<Class<? extends Object>> classes = reflections.getSubTypesOf(Object.class);

                return classes;
        }

        public static Class[] getClasses(String packageName) {
                try {
                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        assert classLoader != null;
                        String path = packageName.replace('.', '/');
                        Enumeration<URL> resources = classLoader.getResources(path);
                        List<File> dirs = new ArrayList<File>();
                        while (resources.hasMoreElements()) {
                                URL resource = resources.nextElement();
                                dirs.add(new File(resource.getFile()));
                        }
                        ArrayList<Class> classes = new ArrayList<Class>();
                        for (File directory : dirs) {
                                classes.addAll(findClasses(directory, packageName));
                        }
                        return classes.toArray(new Class[classes.size()]);
                } catch (Exception ex) {
                        
                }
                
                return EMPTY_CLASS_ARR;
        }

        /**
         * Recursive method used to find all classes in a given directory and subdirs.
         *
         * @param directory The base directory
         * @param packageName The package name for classes found inside the base directory
         * @return The classes
         * @throws ClassNotFoundException
         */
        private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
                List<Class> classes = new ArrayList<Class>();
                if (!directory.exists()) {
                        return classes;
                }
                File[] files = directory.listFiles();
                for (File file : files) {
                        if (file.isDirectory()) {
                                assert !file.getName().contains(".");
                                classes.addAll(findClasses(file, packageName + "." + file.getName()));
                        } else if (file.getName().endsWith(".class")) {
                                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                        }
                }
                return classes;
        }
}
