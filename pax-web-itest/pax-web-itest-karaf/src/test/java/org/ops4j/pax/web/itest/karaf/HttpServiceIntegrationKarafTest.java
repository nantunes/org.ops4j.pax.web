/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.itest.karaf;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

@RunWith( PaxExam.class )
public class HttpServiceIntegrationKarafTest extends KarafBaseTest {
  private Bundle installWarBundle;

  @Configuration
  public Option[] config() {
    return jettyConfig();
  }

  @Before
  public void setUp() throws Exception {
    initServletListener( null );

    String bundlePath = "mvn:org.ops4j.pax.web.samples/helloworld-hs/" + getProjectVersion();
    installWarBundle = bundleContext.installBundle( bundlePath );
    installWarBundle.start();

    int failCount = 0;
    while ( installWarBundle.getState() != Bundle.ACTIVE ) {
      Thread.sleep( 500 );
      if ( failCount > 500 ) {
        throw new RuntimeException(
            "Required bundle is never active" );
      }

      failCount++;
    }

    waitForServletListener( 15000l );
  }

  @After
  public void tearDown() throws BundleException {
    if ( installWarBundle != null ) {
      installWarBundle.stop();
      installWarBundle.uninstall();
    }
  }

  @Test
  public void testMultipleResourcesAliasRegisteredForSameName() throws Exception {
    createTestClientForKaraf()
        .doGETandExecuteTest( "http://127.0.0.1:8181/images/logo.png" );

    createTestClientForKaraf()
        .doGETandExecuteTest( "http://127.0.0.1:8181/alt-images/logo.png" );
  }
}
