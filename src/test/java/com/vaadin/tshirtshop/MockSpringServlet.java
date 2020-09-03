package com.vaadin.tshirtshop;

import com.github.mvysny.kaributesting.v10.MockVaadinHelper;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;

/**
 * @author mavi
 */
class MockSpringServlet extends SpringServlet {
    @NotNull
    public final Routes routes;
    @NotNull
    public final ApplicationContext ctx;
    @NotNull
    public final Function0<UI> uiFactory;

    public MockSpringServlet(@NotNull Routes routes, @NotNull ApplicationContext ctx, @NotNull Function0<UI> uiFactory) {
        super(ctx, false);
        this.ctx = ctx;
        this.routes = routes;
        this.uiFactory = uiFactory;
    }

    @Override
    protected DeploymentConfiguration createDeploymentConfiguration() throws ServletException {
        MockVaadinHelper.INSTANCE.mockFlowBuildInfo(this);
        return super.createDeploymentConfiguration();
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        final VaadinServletService service = new SpringVaadinServletService(this, deploymentConfiguration, ctx) {
            @Override
            protected boolean isAtmosphereAvailable() {
                return false;
            }

            @Override
            public String getMainDivId(VaadinSession session, VaadinRequest request) {
                return "ROOT-1";
            }

            @Override
            protected VaadinSession createVaadinSession(VaadinRequest request) {
                return new MockSpringVaadinSession(this, uiFactory);
            }
        };
        service.init();
        routes.register((VaadinServletContext) service.getContext());
        return service;
    }
}
