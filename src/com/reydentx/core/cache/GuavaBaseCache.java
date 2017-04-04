/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.reydentx.core.config.RConfig;
import java.util.concurrent.TimeUnit;

/**
 * Cache bases on Guava LoadingCache
 *
 * @author ducnt3
 * @param <K> key
 * @param <V> value
 */
public abstract class GuavaBaseCache<K, V> implements RCache<K, V> {

        private LoadingCache<K, V> _innerCache;
        private final String _name;
        private V _emptyValue;
        private int _maximunSize = 1000;
        private int _expireAfterAccess = 24 * 60; // minute

        // Constructor and Init
        /////////////////////////////////////////////////////////
        public GuavaBaseCache(String name) {
                this._name = name;
                initCache();
        }

        private void initCache() {
                // get cache config
                _maximunSize = RConfig.Instance.getInt(this.getClass(), _name, "maximum-size", _maximunSize);
                _expireAfterAccess = RConfig.Instance.getInt(this.getClass(), _name, "expire-after-access", _expireAfterAccess);

                // init cache loader & default value
                CacheLoader<K, V> cacheLoader = declareCacheLoader();
                _emptyValue = declareEmptyValue();

                // init cache
                _innerCache = CacheBuilder.newBuilder()
                        .maximumSize(_maximunSize)
                        .expireAfterAccess(_expireAfterAccess, TimeUnit.MINUTES)
                        .build(cacheLoader);
        }

        // Need to be overrided functions
        /////////////////////////////////////////////////////////
        abstract CacheLoader<K, V> declareCacheLoader();

        abstract V declareEmptyValue();

        // Cache core APIs
        /////////////////////////////////////////////////////////
        @Override
        public void put(K key, V value) {
                _innerCache.put(key, value);
        }

        @Override
        public V get(K key) {
                try {
                        return _innerCache.get(key);
                } catch (Exception ex) {

                }

                return _emptyValue;
        }

        @Override
        public void remove(K key) {
                _innerCache.invalidate(key);
        }
}
