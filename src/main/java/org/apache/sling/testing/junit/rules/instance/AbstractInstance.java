/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.sling.testing.junit.rules.instance;


import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingClient;
import org.apache.sling.testing.clients.instance.InstanceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractInstance implements Instance {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractInstance.class);
    // Caches a client
    private final Map<String, SlingClient> clientsCache = new ConcurrentHashMap<>(1);


    /**
     * Customizes the builder and returns it. Default implementation does nothing to the builder.
     * This should be overridden in subclasses, if needed. This method will be called internally before building a client.
     * @param builder the builder to customize
     * @return the builder after customization.
     */
    @Override
    public <T extends SlingClient.InternalBuilder> T customize(T builder) {
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends SlingClient> T newClient(Class<T> clientClass, String user, String pass, BuilderCustomizer... customizers) {
        InstanceConfiguration configuration = getConfiguration();
        try {
            T.InternalBuilder<T> builder = getBuilder(clientClass, configuration.getUrl(), user, pass);
            for (BuilderCustomizer customizer: customizers) {
                builder = customizer.customize(builder);
            }
            T client = builder.build();
            clientsCache.put(user + pass, client);
            return client;
        } catch (ClientException e) {
            return null;
        }
    }

    private Class getBuilderClass(Class clientClass) {

        // If the passed class is not a client, nothing to do;
        if (!SlingClient.class.isAssignableFrom(clientClass)) {
            return null;
        }

        // If it's SlingClient, just return the builder
        if (clientClass == SlingClient.class) {
            return SlingClient.Builder.class;
        }

        // First class that is not abstract and extends SlingClient.InternalBuilder is our builder
        for (Class clazz : clientClass.getDeclaredClasses()) {
            if (SlingClient.InternalBuilder.class.isAssignableFrom(clazz)
                    && !Modifier.isAbstract(clazz.getModifiers())) {
                return clazz;
            }
        }

        // Not declared on this class, looking at the parent
        if (null == clientClass.getSuperclass()) {
            return null;
        } else {
            return getBuilderClass(clientClass.getSuperclass());
        }
    }

    protected <B extends SlingClient.InternalBuilder, T extends SlingClient> B getBuilder(Class<T> clientClass, URI url, String user, String password) {
        Class<B> builderClass = getBuilderClass(clientClass);
        B builder;

        try {
            Method create = builderClass.getMethod("create", URI.class, String.class, String.class);
            builder = (B) create.invoke(null, url, user, password);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }

        // return the customized builder
        return customize(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SlingClient newAdminClient(BuilderCustomizer... customizers) {
        return newClient(SlingClient.class, getConfiguration().getAdminUser(), getConfiguration().getAdminPassword(), customizers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends SlingClient> T getClient(Class<T> clientClass, String user, String pass) {
        if (clientsCache.containsKey(user + pass)) {
            try {
                return clientsCache.get(user + pass).adaptTo(clientClass);
            } catch (ClientException e) {
                return null;
            }
        } else {
            return newClient(clientClass, user, pass);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SlingClient getAdminClient() {
        return getAdminClient(SlingClient.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends SlingClient> T getAdminClient(Class<T> clientClass) {
        return getClient(clientClass, getConfiguration().getAdminUser(), getConfiguration().getAdminPassword());
    }
}
