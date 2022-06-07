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
package org.jboss.hal.testsuite.test.configuration.ee;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.page.configuration.EEPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.PATH;
import static org.jboss.hal.dmr.ModelDescriptionConstants.RELATIVE_TO;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26_1;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.GLOBAL_DIRECTORY_UPDATE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.globalDirectoryAddress;
import static org.jboss.hal.testsuite.fixtures.PathsFixtures.JBOSS_SERVER_DATA_DIR;

@Manatoko
@Testcontainers
class GlobalDirectoryUpdateTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26_1, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(globalDirectoryAddress(GLOBAL_DIRECTORY_UPDATE), Values.of(NAME, GLOBAL_DIRECTORY_UPDATE)
                .and(PATH, Random.name())
                .and(RELATIVE_TO, JBOSS_SERVER_DATA_DIR));
    }

    @Inject Console console;
    @Inject CrudOperations crud;
    @Page EEPage page;
    FormFragment form;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.EE_GLOBAL_MODULES_ITEM);
        form = page.getGlobalDirectoryForm();
    }

    @Test
    void update() throws Exception {
        crud.update(globalDirectoryAddress(GLOBAL_DIRECTORY_UPDATE), form, PATH, Random.name());
    }
}
