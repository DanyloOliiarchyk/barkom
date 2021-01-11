package com.reconsale.barkom.cms.views;

import com.reconsale.barkom.cms.backUp.BackUp;
import com.reconsale.barkom.cms.forms.RecipeForm;
import com.reconsale.barkom.cms.layout.MainLayout;
import com.reconsale.barkom.cms.models.Recipe;
import com.reconsale.barkom.cms.services.RecipeService;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Route(value = "products", layout = MainLayout.class)
@PageTitle("Barkom")
@PreserveOnRefresh
public class RecipeView extends VerticalLayout implements HasUrlParameter<String> {

    private RecipeForm form;
    private Grid<Recipe> grid = new Grid<>(Recipe.class);
    private TextField filter = new TextField();
    private final RecipeService service;
    private String category;
    private BackUp backUp = new BackUp();
    private HorizontalLayout currentBranch = new HorizontalLayout();

    @Autowired
    public RecipeView(RecipeService service) {
        this.service = service;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new RecipeForm();
        form.addListener(RecipeForm.SaveEvent.class, this::saveRecipe);
        form.addListener(RecipeForm.DeleteEvent.class, this::deleteRecipe);
        form.addListener(RecipeForm.CancelEvent.class, e -> closeEditor());

        VerticalLayout content = new VerticalLayout(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void deleteRecipe(RecipeForm.DeleteEvent event) {
        service.delete(event.getRecipe());
        updateList();
        closeEditor();
    }

    private void saveRecipe(RecipeForm.SaveEvent event) {
        if (event.getRecipe().getStatus() == null){
            event.getRecipe().setStatus(Recipe.Status.Редагується);
        }

        if (event.getRecipe().getTitle().isEmpty()){
            event.getRecipe().setTitle("Без назви");
        }

        if (event.getRecipe().getText().isEmpty()){
            event.getRecipe().setText("Без рецепта");
        }
        event.getRecipe().setCategory(category);
        service.save(event.getRecipe());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setRecipe(null);
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
        grid.removeColumnByKey("imageUrl");

        grid.addColumn(TemplateRenderer.<Recipe>of("<div style='white-space:normal'>[[item.title]]</div>")
                .withProperty("title", Recipe::getTitle))
                .setHeader("Назва").setFlexGrow(1).setWidth("250px");
        grid.addColumn(TemplateRenderer.<Recipe>of("<div style='white-space:normal'>[[item.text]]</div>")
                .withProperty("text", Recipe::getText))
                .setHeader("Рецепт").setFlexGrow(1).setWidth("500px");
        grid.addColumn(recipe -> recipe.getStartDate() + " --- " + recipe.getEndDate()).setHeader("Період").setAutoWidth(true);
        grid.addColumn(recipe -> recipe.getStatus()).setHeader("Статус").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.asSingleSelect().addValueChangeListener(e -> editRecipe(e.getValue()));
    }

    private void editRecipe(Recipe recipe) {
        if (recipe == null) {
            closeEditor();
        } else {
            form.setRecipe(recipe);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private HorizontalLayout getToolBar() {
        filter.setClearButtonVisible(true);
        filter.setPlaceholder("Знайти...");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> updateList());

        Button addNew = new Button("Добавити рецепт", click -> addRecipe());
        HorizontalLayout toolbar = new HorizontalLayout(filter, addNew, currentBranch);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addRecipe() {
        grid.asSingleSelect().clear();
        editRecipe(new Recipe());
    }

    public void updateList() {
        backUp.saveRecipesToReserveFile(service.getAll());
        grid.setItems(service.getAll(category, filter.getValue()));
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
