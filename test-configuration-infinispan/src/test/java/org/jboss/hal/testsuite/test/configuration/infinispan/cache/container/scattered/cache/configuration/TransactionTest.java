/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JGROUPS;
import static org.jboss.hal.dmr.ModelDescriptionConstants.TRANSPORT;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.transactionAddress;

@Manatoko
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
class TransactionTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE_TRANSACTION = "scattered-cache-" + Random.name();

    @Container static Browser browser = new Browser();
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and(TRANSPORT, JGROUPS));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION));
        operations.removeIfExists(transactionAddress(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION));
    }

    @Inject CrudOperations crud;
    @Inject Console console;
    @Page ScatteredCachePage page;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION);
        console.verticalNavigation().selectPrimary("scattered-cache-item");
        form = page.getTransactionForm();
    }

    @Test
    void create() throws Exception {
        crud.createSingleton(transactionAddress(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION), form);
    }

    @Test
    void remove() throws Exception {
        crud.deleteSingleton(transactionAddress(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION), form);
    }

    @Test
    void editLocking() throws Exception {
        String currentLocking = operations
                .readAttribute(transactionAddress(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION), "locking")
                .stringValue();
        List<String> lockings = new ArrayList<>(Arrays.asList("PESSIMISTIC", "OPTIMISTIC"));
        lockings.remove(currentLocking);
        crud.update(transactionAddress(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION), form,
                formFragment -> formFragment.select("locking", lockings.get(0)),
                resourceVerifier -> resourceVerifier.verifyAttribute("locking", lockings.get(0)));
    }

    @Test
    void editMode() throws Exception {
        String currentMode = operations.readAttribute(transactionAddress(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION), "mode")
                .stringValue();
        List<String> modes = new ArrayList<>(Arrays.asList("NONE", "BATCH", "NON_XA", "NON_DURABLE_XA", "FULL_XA"));
        modes.remove(currentMode);
        crud.update(transactionAddress(CACHE_CONTAINER, SCATTERED_CACHE_TRANSACTION), form,
                formFragment -> formFragment.select("mode", modes.get(0)),
                resourceVerifier -> resourceVerifier.verifyAttribute("mode", modes.get(0)));
    }
}
