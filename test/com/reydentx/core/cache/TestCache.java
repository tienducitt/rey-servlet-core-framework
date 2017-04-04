/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.cache;

import com.google.common.cache.CacheLoader;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author ducnt3
 */
public class TestCache extends GuavaBaseCache<Integer, String> {

        public static TestCache INSTANCE = new TestCache("mainCache");

        public TestCache(String name) {
                super(name);
        }

        @Override
        CacheLoader<Integer, String> declareCacheLoader() {
                return new CacheLoader<Integer, String>() {

                        @Override
                        public String load(Integer k) throws Exception {
                                return String.valueOf(k);
                        }
                };
        }

        @Override
        String declareEmptyValue() {
                return StringUtils.EMPTY;
        }

        //~~~~~~~~~~~~~~~~~~ test app ~~~~~~~~~~~~~~~~~~~~~~~~~~`
        public static void main(String[] args) {
                INSTANCE.get(1);
                INSTANCE.put(2, "3");

                System.out.println(INSTANCE.get(1));
                System.out.println(INSTANCE.get(2));
        }
}
