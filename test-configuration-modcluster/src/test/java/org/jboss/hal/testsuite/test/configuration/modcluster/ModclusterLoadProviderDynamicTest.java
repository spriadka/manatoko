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
package org.jboss.hal.testsuite.test.configuration.modcluster;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.EmptyState;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.ModclusterPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.DEFAULT;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LISTENER;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.HA;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.HISTORY;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.PROXY_DYNAMIC_LP;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.loadProviderDynamicAddress;
import static org.jboss.hal.testsuite.fixtures.ModclusterFixtures.proxyAddress;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
class ModclusterLoadProviderDynamicTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(HA);

    @Container static Browser browser = new Browser();

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(proxyAddress(PROXY_DYNAMIC_LP), Values.of(LISTENER, DEFAULT));
        operations.add(loadProviderDynamicAddress(PROXY_DYNAMIC_LP));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page ModclusterPage page;
    FormFragment form;

    @BeforeEach
    void setUp() {
        page.navigate(NAME, PROXY_DYNAMIC_LP);
        console.verticalNavigation().selectPrimary("load-provider-dynamic-item");
        form = page.getLoadProviderDynamicForm();
    }

    @Test
    void noSimpleProvider() {
        console.verticalNavigation().selectPrimary("load-provider-simple-item");
        EmptyState empty = page.getLoadProviderSimpleEmpty();
        assertTrue(empty.getRoot().isDisplayed());
    }

    @Test
    void reset() throws Exception {
        crud.reset(loadProviderDynamicAddress(PROXY_DYNAMIC_LP), form);
    }

    @Test
    void update() throws Exception {
        crud.update(loadProviderDynamicAddress(PROXY_DYNAMIC_LP), form, HISTORY, Random.number());
    }
}
