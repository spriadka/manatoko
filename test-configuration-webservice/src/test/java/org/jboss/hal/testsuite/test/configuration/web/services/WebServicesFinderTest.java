package org.jboss.hal.testsuite.test.configuration.web.services;

import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.resources.Names;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.finder.FinderPath;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.STANDALONE;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;

@Manatoko
@Testcontainers
class WebServicesFinderTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, STANDALONE);

    @Inject Console console;

    @Test
    void view() {
        console.finder(NameTokens.CONFIGURATION, new FinderPath().append(Ids.CONFIGURATION, Ids.asId(Names.SUBSYSTEMS)))
                .column(Ids.CONFIGURATION_SUBSYSTEM).selectItem(NameTokens.WEBSERVICES).view();
        console.verify(new PlaceRequest.Builder().nameToken(NameTokens.WEBSERVICES).build());
    }
}
