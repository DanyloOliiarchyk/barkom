package com.reconsale.barkom.cms.views;

import com.reconsale.barkom.cms.layout.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Barkom")
public class StartView extends VerticalLayout {

    @Autowired
    public StartView() {
    }
}
