[<img src="http://sling.apache.org/res/logos/sling.png"/>](http://sling.apache.org)

 [![Build Status](https://builds.apache.org/buildStatus/icon?job=sling-org-apache-sling-testing-rules-1.8)](https://builds.apache.org/view/S-Z/view/Sling/job/sling-org-apache-sling-testing-rules-1.8) [![Test Status](https://img.shields.io/jenkins/t/https/builds.apache.org/view/S-Z/view/Sling/job/sling-org-apache-sling-testing-rules-1.8.svg)](https://builds.apache.org/view/S-Z/view/Sling/job/sling-org-apache-sling-testing-rules-1.8/test_results_analyzer/) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.sling/org.apache.sling.testing.rules/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.apache.sling%22%20a%3A%22org.apache.sling.testing.rules%22) [![JavaDocs](https://www.javadoc.io/badge/org.apache.sling/org.apache.sling.testing.rules.svg)](https://www.javadoc.io/doc/org.apache.sling/org.apache.sling.testing.rules) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0) [![testing](https://sling.apache.org/badges/group-testing.svg)](https://github.com/apache/sling-aggregator/blob/master/docs/groups/testing.md)

# Apache Sling Testing Rules

This module is part of the [Apache Sling](https://sling.apache.org) project.

The Sling Http Testing Rules allow writing integration tests easily. They are primarily meant to be used for tests that use http against 
a Sling instance and make use of the `org.apache.sling.testing.clients` which offer a simple, immutable and extendable way of working 
with specialized http clients.

The junit rules incorporate boiler-plate logic that is shared in tests and take the modern approach of using junit rules rather than 
inheritance. The `SlingRule` (for methods) or `SlingClassRule` are base rules, chaining other rules like `TestTimeoutRule`, 
`TestDescriptionRule`, `FilterRule`. The `SlingInstanceRule` extends that and starts a Sling instance if needed and also allows 
instantiating a `SlingClient` pointing to the instance - base url, credentials, etc.
    

## <a name="starting"></a> Starting an Integration Test
Starting an integration is very simple out of the box, but is very extendable, both by combining or configuring the junit rules and by 
using the versatile `SlingClient` (which can be extended or adapted by calling `adaptTo(MyClient.class)` without losing the client 
configuration)
 
### Simple Example using SlingInstanceRule

```java   
    public class MySimpleIT {
    
        @ClassRule
        public static SlingInstanceRule instanceRule = new SlingInstanceRule();
    
        @Rule
        public SlingRule methodRule = new SlingRule(); // will configure test timeout, description, etc.
    
        @Test
        public void testChangeOSGiConfig() {
           SlingClient client = instanceRule.getAdminClient();
           client.createNode("/content/myNode", "nt:unstructured");
           Assert.assertTrue("Node should be there", client.exists("/content/myNode"));
        }
            
    } 
```
 
### Example using SlingInstanceRule and the clients

```java   
    public class MyOSGiIT {
        @ClassRule
        public static SlingInstanceRule instanceRule = new SlingInstanceRule();
    
        @Rule
        public SlingRule methodRule = new SlingRule(); // will configure test timeout, description, etc.
    
        @Test
        public void testChangeOSGiConfig() {
           OsgiConsoleClient osgiClient = instanceRule.getAdminClient(OsgiConsoleClient.class);
           // Save osgi config for pid (to be restored later) 
           InstanceConfig osgiConfig = new OsgiInstanceConfig(osgiClient, "MYPID").save();
           // edit the config for this test
           osgiClient.editConfigurationWithWait(20, "MYPID", null, myMap);
           SlingHttpResponse response = osgiClient.adaptTo(MyClient.class).myClientMethod();
           response.checkContentContains("my expected content");
           osgiConfig.restore();
        }
            
    } 
```
