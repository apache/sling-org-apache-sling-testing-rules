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

import org.apache.sling.testing.clients.SlingClient;
import org.apache.sling.testing.clients.instance.InstanceConfiguration;
import org.apache.sling.testing.clients.instance.InstanceSetup;
import org.junit.rules.TestRule;

import java.beans.Customizer;

public interface Instance extends TestRule, BuilderCustomizer {

    Instance withRunMode(String runMode);

    Instance orDefault(InstanceConfiguration instanceConfiguration);

    InstanceConfiguration getConfiguration();

    /**
     * Return <strong>a new client</strong> pointing to the instance corresponding to this {{AbstractInstance}}
     * if it was never created with this username and password, or the existing client if it has been created before
     *
     * @param clientClass the class of the returned client
     * @param user the username used in the client
     * @param pass the password used in the client
     * @param <T> the type of the returned client
     * @return a new client extending {{SlingClient}}
     */
    <T extends SlingClient> T getClient(Class<T> clientClass, String user, String pass);

    /**
     * Return <strong>a new client</strong> pointing to the instance corresponding to this {{AbstractInstance}},
     * with the admin user and password, if it was not previously created, or the existing client if it has been created before
     *
     * See {@link InstanceSetup#INSTANCE_CONFIG_ADMINUSER} and {@link InstanceSetup#INSTANCE_CONFIG_ADMINPASSWORD}
     *
     * @return a new {{SlingClient}}
     */
    SlingClient getAdminClient();

    /**
     * Return <strong>a new client</strong> pointing to the instance corresponding to this {{AbstractInstance}},
     * with the admin user and password, if it was not previously created, or the existing client if it has been created before
     *
     * See {@link InstanceSetup#INSTANCE_CONFIG_ADMINUSER} and {@link InstanceSetup#INSTANCE_CONFIG_ADMINPASSWORD}
     *
     * @param clientClass the class of the returned client
     * @param <T> the class of the returned client
     * @return a new client extending on {{SlingClient}}
     */
    <T extends SlingClient> T getAdminClient(Class<T> clientClass);



    /**
     * Return <strong>a new client</strong> pointing to the instance corresponding to this {{AbstractInstance}},
     * replacing the internally-cached client with this username and password
     *
     * @param clientClass the class of the returned client
     * @param user the username used in the client
     * @param pass the password used in the client
     * @param <T> the type of the returned client
     * @return a new client extending {{SlingClient}}
     */
    <T extends SlingClient> T newClient(Class<T> clientClass, String user, String pass, BuilderCustomizer... customizers);

    /**
     * Return <strong>a new client</strong> pointing to the instance corresponding to this {{AbstractInstance}},
     * replacing the internally-cached client with this username and password
     *
     * See {@link InstanceSetup#INSTANCE_CONFIG_ADMINUSER} and {@link InstanceSetup#INSTANCE_CONFIG_ADMINPASSWORD}
     *
     * @return a new {{SlingClient}}
     */
    SlingClient newAdminClient(BuilderCustomizer... customizers);

}
