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
package org.apache.sling.testing.junit.rules;

import org.apache.http.client.HttpClient;
import org.apache.sling.testing.clients.SlingClient;
import org.apache.sling.testing.clients.SystemPropertiesConfig;
import org.apache.sling.testing.clients.interceptors.UserAgentHolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Junit rule to overwrite or append the user-agent of all {@link SlingClient}
 * http requests with a custom {@link String}.
 * </p>
 * May be used on a per class or per test basis.
 * </p>
 * (In case the
 * user-agent of the {@link HttpClient} is tampered with manually changes will not be applied.)
 */
@SuppressWarnings("unused")
public class CustomUserAgentRule implements TestRule {

    private final String userAgent;

    private final boolean append;

    /**
     * Overwrite the user-agent with a custom {@link String}.
     * @param userAgent the new user-agent
     */
    public CustomUserAgentRule(String userAgent) {
        this(userAgent, false);
    }

    /**
     * Modify or overwrite the user-agent.
     * @param userAgent the desired user-agent
     * @param append whether it should just be appended to the current one
     */
    public CustomUserAgentRule(String userAgent, boolean append) {
        this.userAgent = userAgent;
        this.append = append;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                starting();
                try {
                    base.evaluate();
                } finally {
                    finished();
                }
            }
        };
    }

    protected void starting() {
        String newUserAgent = userAgent;
        if (append) {
            String currentUserAgent = UserAgentHolder.get();
            if (currentUserAgent == null) {
                // Use default user-agent instead
                currentUserAgent = SystemPropertiesConfig.getDefaultUserAgent();
            }
            newUserAgent = currentUserAgent + " " + userAgent;
        }
        UserAgentHolder.set(newUserAgent);
    }

    protected void finished() {
        UserAgentHolder.reset();
    }
}
