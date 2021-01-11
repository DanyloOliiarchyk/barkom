package com.reconsale.barkom.cms.forms;

import com.reconsale.barkom.cms.emoji.EmojiList;
import com.reconsale.barkom.cms.models.Recipe;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RecipeForm extends VerticalLayout {

    private TextArea title = new TextArea("Назва");
    private TextArea text = new TextArea("Рецепт");
    private ComboBox<Recipe.Status> status = new ComboBox<>("Статус");
    private DatePicker startDate = new DatePicker("Початок дії:");
    private DatePicker endDate = new DatePicker("Кінець дії:");
    private TextField imageUrl = new TextField("Ссилка на картинку");

    private HorizontalLayout titleAndSmiles = new HorizontalLayout(title, createEmojiPickerForTitleField());
    private VerticalLayout titleAndStatus = new VerticalLayout(titleAndSmiles, status);
    private VerticalLayout startAndEndDate = new VerticalLayout(startDate, endDate);
    private HorizontalLayout textAndSmiles = new HorizontalLayout(text, createEmojiPickerForTextField());
    private VerticalLayout textArea = new VerticalLayout(textAndSmiles);

    private HorizontalLayout imageFrame = new HorizontalLayout();
    private HorizontalLayout imageContent = new HorizontalLayout(imageUrl, imageFrame);

    private Button save = new Button("Зберегти");
    private Button delete = new Button("Видалити");
    private Button cancel = new Button("Скасувати");

    private EmojiList emojiList = new EmojiList();
    private List<String> smileAndPeopleList = emojiList.getSmileAndPeopleList();
    private List<String> animalAndNatureList = emojiList.getAnimalAndNatureList();
    private List<String> foodAndDrinkList = emojiList.getFoodAndDrinkList();
    private List<String> activitiesList = emojiList.getActivitiesList();
    private List<String> travelAndPlacesList = emojiList.getTravelAndPlacesList();
    private List<String> objectsList = emojiList.getObjectsList();
    private List<String> symbolsList = emojiList.getSymbolsList();
    private List<String> flagsList = emojiList.getFlagsList();

    Binder<Recipe> binder;

    @Autowired
    public RecipeForm() {
        addClassName("barkom-form");
        this.binder = new Binder<>(Recipe.class);
        binder.bindInstanceFields(this);
        text.setMaxLength(1000);
        text.setWidth("430px");
        text.setHeight("100px");
        title.setHeight("100px");
        title.setMaxLength(500);
        status.setItems(Recipe.Status.values());

        imageUrl.setWidth("320px");
        imageUrl.addValueChangeListener(e -> {
            imageFrame.removeAll();
            if (!imageUrl.isEmpty()) {
                Image image = new Image(imageUrl.getValue(), "Неможливо відобразити");
                image.setWidth("100px");
                image.setHeight("100px");
                imageFrame.add(image);
            }
        });

        textArea.add(imageContent);

        HorizontalLayout content = new HorizontalLayout(titleAndStatus, textArea, startAndEndDate, createButtonsLayout());
        content.setAlignItems(Alignment.CENTER);
        add(content);
    }

    public void setRecipe(Recipe recipe) {
        binder.setBean(recipe);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        save.setWidth("130px");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.setWidth("130px");
        cancel.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        cancel.setWidth("130px");
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new RecipeForm.DeleteEvent(this, binder.getBean())));
        cancel.addClickListener(event -> fireEvent(new RecipeForm.CancelEvent(this)));
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new VerticalLayout(save, cancel, delete);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new RecipeForm.SaveEvent(this, binder.getBean()));
        }
    }

    // Events
    public static abstract class RecipeFormEvent extends ComponentEvent<RecipeForm> {
        private Recipe recipe;

        protected RecipeFormEvent(RecipeForm source, Recipe recipe) {
            super(source, false);
            this.recipe = recipe;
        }

        public Recipe getRecipe() {
            return recipe;
        }
    }

    public static class SaveEvent extends RecipeForm.RecipeFormEvent {
        SaveEvent(RecipeForm source, Recipe recipe) {
            super(source, recipe);
        }
    }

    public static class DeleteEvent extends RecipeForm.RecipeFormEvent {
        DeleteEvent(RecipeForm source, Recipe recipe) {
            super(source, recipe);
        }
    }

    public static class CancelEvent extends RecipeForm.RecipeFormEvent {
        CancelEvent(RecipeForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    //Emoji methods
    private Component createEmojiPickerForTitleField() {
        Dialog emojiMenu = new Dialog();
        emojiMenu.setWidth("520px");
        emojiMenu.setHeight("300px");

        Icon close = new Icon(VaadinIcon.CLOSE);
        close.setColor("red");
        Button closeButton = new Button("Закрити", close);
        closeButton.addClickShortcut(Key.ESCAPE);
        closeButton.addClickListener(event -> emojiMenu.close());

        Button smileyAndPeople = new Button(VaadinIcon.SMILEY_O.create());
        Button animalAndNature = new Button(VaadinIcon.GLOBE.create());
        Button foodAndDrink = new Button(VaadinIcon.CROSS_CUTLERY.create());
        Button activities = new Button(VaadinIcon.GOLF.create());
        Button travelAndPlaces = new Button(VaadinIcon.BUILDING_O.create());
        Button objects = new Button(VaadinIcon.LIGHTBULB.create());
        Button symbols = new Button(VaadinIcon.DOLLAR.create());
        Button flags = new Button(VaadinIcon.FLAG_O.create());

        smileyAndPeople.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : smileAndPeopleList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> title.setValue(title.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        animalAndNature.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : animalAndNatureList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> title.setValue(title.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        foodAndDrink.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : foodAndDrinkList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> title.setValue(title.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        activities.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : activitiesList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> title.setValue(title.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        travelAndPlaces.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : travelAndPlacesList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> title.setValue(title.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        objects.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : objectsList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> title.setValue(title.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        symbols.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : symbolsList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> title.setValue(title.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        flags.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : flagsList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> title.setValue(title.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

        Icon smile = new Icon(VaadinIcon.SMILEY_O);
        smile.setColor("black");
        Button emojiButton = new Button("", smile);
        emojiButton.setHeight("30px");
        emojiButton.setWidth("30px");
        emojiButton.addClickListener(event -> emojiMenu.open());
        return emojiButton;
    }

    private Component createEmojiPickerForTextField() {
        Dialog emojiMenu = new Dialog();
        emojiMenu.setWidth("520px");
        emojiMenu.setHeight("300px");

        Icon close = new Icon(VaadinIcon.CLOSE);
        close.setColor("red");
        Button closeButton = new Button("Закрити", close);
        closeButton.addClickShortcut(Key.ESCAPE);
        closeButton.addClickListener(event -> emojiMenu.close());

        Button smileyAndPeople = new Button(VaadinIcon.SMILEY_O.create());
        Button animalAndNature = new Button(VaadinIcon.GLOBE.create());
        Button foodAndDrink = new Button(VaadinIcon.CROSS_CUTLERY.create());
        Button activities = new Button(VaadinIcon.GOLF.create());
        Button travelAndPlaces = new Button(VaadinIcon.BUILDING_O.create());
        Button objects = new Button(VaadinIcon.LIGHTBULB.create());
        Button symbols = new Button(VaadinIcon.DOLLAR.create());
        Button flags = new Button(VaadinIcon.FLAG_O.create());

        smileyAndPeople.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : smileAndPeopleList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> text.setValue(text.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        animalAndNature.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : animalAndNatureList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> text.setValue(text.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        foodAndDrink.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : foodAndDrinkList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> text.setValue(text.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        activities.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : activitiesList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> text.setValue(text.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        travelAndPlaces.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : travelAndPlacesList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> text.setValue(text.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        objects.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : objectsList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> text.setValue(text.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        symbols.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : symbolsList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> text.setValue(text.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        flags.addClickListener(e -> {
            emojiMenu.removeAll();
            emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

            for (String emoji : flagsList) {
                Button button = new Button(emoji);
                button.addClickListener(event -> text.setValue(text.getValue() + emoji));
                button.setWidth("20px");
                emojiMenu.add(button);
            }
        });

        emojiMenu.add(smileyAndPeople, animalAndNature, foodAndDrink, activities, travelAndPlaces, objects, symbols, flags, closeButton);

        Icon smile = new Icon(VaadinIcon.SMILEY_O);
        smile.setColor("black");
        Button emojiButton = new Button("", smile);
        emojiButton.setHeight("30px");
        emojiButton.setWidth("30px");
        emojiButton.addClickListener(event -> emojiMenu.open());
        return emojiButton;
    }
}
