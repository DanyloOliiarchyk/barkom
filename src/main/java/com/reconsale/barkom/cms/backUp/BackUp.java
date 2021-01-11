package com.reconsale.barkom.cms.backUp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.reconsale.barkom.cms.models.Message;
import com.reconsale.barkom.cms.models.Recipe;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BackUp {

    public BackUp() {
    }

    public void saveMessagesToReserveFile(List<Message> messages) {
        List<Message> m = messages;
        String json = new Gson().toJson(m);

        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();

        try {
            FileWriter file = new FileWriter(path + "\\reserveFiles\\messages.txt");
            file.write(json);
            file.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void saveRecipesToReserveFile(List<Recipe> recipes) {
        List<Recipe> r = recipes;
        String json = new Gson().toJson(r);

        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();

        try {
            FileWriter file = new FileWriter(path + "\\reserveFiles\\recipes.txt");
            file.write(json);
            file.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public List<Message> getMessagesFromFile() {
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        List<Message> messagesFromJson = null;

        try {
            FileReader text = new FileReader(path + "\\reserveFiles\\messages.txt");
            Gson gson = new Gson();
            Type type = new TypeToken<List<Message>>() {
            }.getType();
            messagesFromJson = gson.fromJson(text, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return messagesFromJson;
    }

    public List<Recipe> getRecipesFromFile() {
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        List<Recipe> recipesFromJson = null;

        try {
            FileReader text = new FileReader(path + "\\reserveFiles\\recipes.txt");
            Gson gson = new Gson();
            Type type = new TypeToken<List<Recipe>>() {
            }.getType();
            recipesFromJson = gson.fromJson(text, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return recipesFromJson;
    }
}
