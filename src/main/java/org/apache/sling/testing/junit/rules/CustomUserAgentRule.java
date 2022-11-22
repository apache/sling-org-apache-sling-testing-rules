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

import org.apache.sling.testing.clients.SystemPropertiesConfig;
import org.apache.sling.testing.clients.interceptors.UserAgentHolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

@SuppressWarnings("unused")
public class CustomUserAgentRule implements TestRule {

    private final String userAgent;

    private final boolean append;

    public CustomUserAgentRule(String userAgent) {
        this(userAgent, false);
    }

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
