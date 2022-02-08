package org.jboss.hal.testsuite.test.configuration.web.services.endpoint.configuration;

import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fixtures.WebServicesFixtures;
import org.jboss.hal.testsuite.page.configuration.WebServicesPage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;

@Manatoko
@Testcontainers
class PreHandlerChainHandlerTest {

    private static final String END_POINT_CONFIGURATION_EDIT =
            "endpoint-configuration-to-be-edited-" + RandomStringUtils.randomAlphanumeric(7);

    private static final WebServicesFixtures.HandlerChain PRE_HANDLER_CHAIN_EDIT =
            new WebServicesFixtures.HandlerChain.Builder(END_POINT_CONFIGURATION_EDIT)
                    .handlerChainName("pre-handler-chain-to-be-edited-" + RandomStringUtils.randomAlphanumeric(7))
                    .endpointConfiguration()
                    .preHandlerChain()
                    .build();

    private static final WebServicesFixtures.Handler PRE_HANDLER_CHAIN_HANDLER_CREATE =
            new WebServicesFixtures.Handler.Builder(
                    "pre-handler-chain-handler-to-be-created-" + RandomStringUtils.randomAlphanumeric(7))
                    .className(Random.name())
                    .handlerChain(PRE_HANDLER_CHAIN_EDIT)
                    .build();

    private static final WebServicesFixtures.Handler PRE_HANDLER_CHAIN_HANDLER_DELETE =
            new WebServicesFixtures.Handler.Builder(
                    "pre-handler-chain-handler-to-be-removed-" + RandomStringUtils.randomAlphanumeric(7))
                    .className(Random.name())
                    .handlerChain(PRE_HANDLER_CHAIN_EDIT)
                    .build();

    private static final WebServicesFixtures.Handler PRE_HANDLER_CHAIN_HANDLER_EDIT =
            new WebServicesFixtures.Handler.Builder(
                    "pre-handler-chain-handler-to-be-edited-" + RandomStringUtils.randomAlphanumeric(7))
                    .className(Random.name())
                    .handlerChain(PRE_HANDLER_CHAIN_EDIT)
                    .build();

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(WebServicesFixtures.endpointConfigurationAddress(END_POINT_CONFIGURATION_EDIT));
        operations.add(PRE_HANDLER_CHAIN_EDIT.handlerChainAddress());
        createHandler(operations, PRE_HANDLER_CHAIN_HANDLER_DELETE);
        createHandler(operations, PRE_HANDLER_CHAIN_HANDLER_EDIT);
    }

    private static void createHandler(Operations operations, WebServicesFixtures.Handler handler) throws IOException {
        operations.add(handler.handlerAddress(), Values.of(WebServicesFixtures.CLASS, handler.getClassName()));
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page WebServicesPage page;

    @BeforeEach
    void prepare() {
        page.navigate();
        console.verticalNavigation().selectPrimary(Ids.WEBSERVICES_ENDPOINT_CONFIG_ITEM);
        page.getEndpointConfigurationTable().action(END_POINT_CONFIGURATION_EDIT, "Pre");
        page.getEndpointConfigurationHandlerChainTable()
                .action(PRE_HANDLER_CHAIN_EDIT.getHandlerChainName(), "Handler");
    }

    @Test
    void create() throws Exception {
        crudOperations.create(
                PRE_HANDLER_CHAIN_HANDLER_CREATE.handlerAddress(),
                page.getEndpointConfigurationHandlerTable(), formFragment -> {
                    formFragment.text("name", PRE_HANDLER_CHAIN_HANDLER_CREATE.getName());
                    formFragment.text(WebServicesFixtures.CLASS, PRE_HANDLER_CHAIN_HANDLER_CREATE.getClassName());
                }, resourceVerifier -> {
                    resourceVerifier.verifyExists();
                    resourceVerifier.verifyAttribute(WebServicesFixtures.CLASS,
                            PRE_HANDLER_CHAIN_HANDLER_CREATE.getClassName());
                });
    }

    @Test
    void remove() throws Exception {
        crudOperations.delete(PRE_HANDLER_CHAIN_HANDLER_DELETE.handlerAddress(),
                page.getEndpointConfigurationHandlerTable(), PRE_HANDLER_CHAIN_HANDLER_DELETE.getName());
    }

    @Test
    void editClass() throws Exception {
        page.getEndpointConfigurationHandlerTable().select(PRE_HANDLER_CHAIN_HANDLER_EDIT.getName());
        crudOperations.update(PRE_HANDLER_CHAIN_HANDLER_EDIT.handlerAddress(),
                page.getEndpointConfigurationHandlerForm(), WebServicesFixtures.CLASS);
    }

}
