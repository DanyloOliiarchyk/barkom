package com.reconsale.barkom.cms.controllers;

import com.reconsale.barkom.cms.models.File;
import com.reconsale.barkom.cms.models.Message;
import com.reconsale.barkom.cms.models.Recipe;
import com.reconsale.barkom.cms.parsers.MessageParser;
import com.reconsale.barkom.cms.services.FileService;
import com.reconsale.barkom.cms.services.MessageService;
import com.reconsale.barkom.cms.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/barkom/")
public class BarkomController {

    private MessageParser messageParser = new MessageParser();

    @Autowired
    private MessageService messageService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "category/{category}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Message> getMessage(@PathVariable("category") String category) {
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Message> messages = this.messageService.getAllByCategory(category, null);

        if (messages.isEmpty()) {
            List<File> files = fileService.getAll();
            File file = files.get(0);
            String content = file.getContent();
            String parentMessage = messageParser.getParentMessage(category, content);
            String queryToBd = parentMessage.trim().replace(" ", "_");
            List<Message> messages2 = this.messageService.getAllByCategory(queryToBd, null);
            if (messages2.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                int i = (int) (Math.random() * (((messages2.size() - 1)) + 1));
                Message message = messages2.get(i);
                if (message == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(message, HttpStatus.OK);
            }
        } else {
            int i = (int) (Math.random() * (((messages.size() - 1)) + 1));
            Message message = messages.get(i);

            if (message == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "product/{product}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Recipe> getRecipe(@PathVariable("product") String product) {
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Recipe> recipes = this.recipeService.getAll(product, null);
        if (recipes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        int i = (int) (Math.random() * (((recipes.size() - 1)) + 1));
        Recipe recipe = recipes.get(i);

        if (recipe == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }
}
