package com.reconsale.barkom.cms.views;

import com.reconsale.barkom.cms.backUp.BackUp;
import com.reconsale.barkom.cms.forms.MessageForm;
import com.reconsale.barkom.cms.layout.MainLayout;
import com.reconsale.barkom.cms.models.Message;
import com.reconsale.barkom.cms.services.MessageService;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Route(value = "categories", layout = MainLayout.class)
@PageTitle("Barkom")
@PreserveOnRefresh
public class MessageView extends VerticalLayout implements HasUrlParameter<String> {

    private MessageForm form;
    private Grid<Message> grid = new Grid<>(Message.class);
    private TextField filter = new TextField();
    private final MessageService service;
    private String category;
    private BackUp backUp = new BackUp();
    private HorizontalLayout currentBranch = new HorizontalLayout();

    @Autowired
    public MessageView(MessageService service) {
        this.service = service;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new MessageForm();
        form.addListener(MessageForm.SaveEvent.class, this::saveMessage);
        form.addListener(MessageForm.DeleteEvent.class, this::deleteMessage);
        form.addListener(MessageForm.CancelEvent.class, e -> closeEditor());

        VerticalLayout content = new VerticalLayout(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void deleteMessage(MessageForm.DeleteEvent event) {
        service.delete(event.getMessage());
        updateList();
        closeEditor();
    }

    private void saveMessage(MessageForm.SaveEvent event) {
        if (event.getMessage().getStatus() == null) {
            event.getMessage().setStatus(Message.Status.Редагується);
        }

        if (event.getMessage().getTitle().isEmpty()) {
            event.getMessage().setTitle("Без назви");
        }

        if (event.getMessage().getText().isEmpty()) {
            event.getMessage().setText("Без повідомлення");
        }
        event.getMessage().setCategory(category);
        service.save(event.getMessage());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setMessage(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void configureGrid() {
        grid.addClassName("barkom-grid");
        grid.setSizeFull();

        grid.removeColumnByKey("category");
        grid.removeColumnByKey("id");
        grid.removeColumnByKey("text");
        grid.removeColumnByKey("title");
        grid.removeColumnByKey("startDate");
        grid.removeColumnByKey("endDate");
        grid.removeColumnByKey("status");

        grid.addColumn(TemplateRenderer.<Message>of("<div style='white-space:normal'>[[item.title]]</div>")
                .withProperty("title", Message::getTitle))
                .setHeader("Назва").setFlexGrow(1).setWidth("250px");
        grid.addColumn(TemplateRenderer.<Message>of("<div style='white-space:normal'>[[item.text]]</div>")
                .withProperty("text", Message::getText))
                .setHeader("Повідомлення").setFlexGrow(1).setWidth("500px");
        grid.addColumn(message -> message.getStartDate() + " --- " + message.getEndDate()).setHeader("Період").setAutoWidth(true);
        grid.addColumn(message -> message.getStatus()).setHeader("Статус").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.asSingleSelect().addValueChangeListener(e -> editMessage(e.getValue()));
    }

    private void editMessage(Message message) {
        if (message == null) {
            closeEditor();
        } else {
            form.setMessage(message);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private HorizontalLayout getToolBar() {
        filter.setClearButtonVisible(true);
        filter.setPlaceholder("Знайти...");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> updateList());

        Button addNew = new Button("Добавити повідомлення", click -> addMessage());
        HorizontalLayout toolbar = new HorizontalLayout(filter, addNew, currentBranch);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addMessage() {
        grid.asSingleSelect().clear();
        editMessage(new Message());
    }

    public void updateList() {
        backUp.saveMessagesToReserveFile(service.getAll());
        grid.setItems(service.getAllByCategory(category, filter.getValue()));
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        closeEditor();
        String result = "";
        try {
            result = URLDecoder.decode(parameter, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        currentBranch.removeAll();
        Paragraph paragraph = new Paragraph(result.replaceAll("_", " ") + ":");
        paragraph.addClassName("branch");
        currentBranch.add(paragraph);
        category = result;
        updateList();
    }
}
