package com.reconsale.barkom.cms.menu;

import com.reconsale.barkom.cms.models.DBCategory;
import com.reconsale.barkom.cms.models.File;
import com.reconsale.barkom.cms.services.DBCategoryService;
import com.reconsale.barkom.cms.views.MessageView;
import com.reconsale.barkom.cms.views.RecipeView;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuCreator {

    public MenuCreator() {
    }

    public VerticalLayout createMenu(List<File> files) {
        VerticalLayout menu = new VerticalLayout();
        if (files.size() != 0) {
            List<Category> categoryList = orderingCollection(files);
            for (Category c : categoryList) {
                menu.add(createSections(c, new Details()));
            }
        }
        return menu;
    }

    private Category fill(Category cat, List<Category> list) {
        for (Category category : list) {
            if (category.getName().startsWith("-")) {
                cat.addProduct(category.getName());
            } else {
                cat.addSubCategory(category);
            }
        }
        return cat;
    }

    private Map<Integer, List<Category>> fileContentSeparator(List<File> f) {
        Map<Integer, List<Category>> sections = new HashMap<>();

        List<File> files = f;
        String[] content = files.get(0).getContent().split("\n");
        for (String inputString : content) {
            int spaceCount = getSpaceCount(inputString);
            Category category = new Category();
            category.setName(inputString.trim());
            List<Category> categories = sections.get(spaceCount);
            if (categories != null) {
                categories.add(category);
            } else {
                ArrayList<Category> cat = new ArrayList<>();
                cat.add(category);
                sections.put(spaceCount, cat);
            }
        }
        return sections;
    }

    private Details createSections(Category category, Details details) {
        if (!category.getSubCategories().isEmpty()) {
            for (Category c : category.getSubCategories()) {
                c.setParentName(category.getName());
                Details details1 = createSections(c, new Details());
                details.addContent(details1);
            }
            VerticalLayout layout = new VerticalLayout();
            for (String product : category.getProducts()) {
                RouterLink routerLink = new RouterLink(product.replaceAll("-", ""), RecipeView.class, product.trim()
                        .replaceAll(" ", "_")
                        .replaceAll("-", ""));
                layout.add(routerLink);
            }
            details.addContent(layout);
            details.setSummary(new RouterLink(category.getName(), MessageView.class, category.getName().trim().replaceAll(" ", "_")));
            return details;
        } else {
            details.setSummary(new RouterLink(category.getName(), MessageView.class, category.getName().trim().replaceAll(" ", "_")));
            VerticalLayout layout = new VerticalLayout();
            for (String product : category.getProducts()) {
                RouterLink routerLink = new RouterLink(product.replaceAll("-", ""), RecipeView.class, product.trim()
                        .replaceAll(" ", "_")
                        .replaceAll("-", ""));
                layout.add(routerLink);
            }
            details.addContent(layout);
        }
        return details;
    }


    private List<Category> orderingCollection(List<File> f) {
        List<Category> result = new ArrayList<>();
        Map<Integer, List<Category>> sections = fileContentSeparator(f);
        for (int i = 0; i < sections.size(); i++) {
            if (sections.size() == 1 && sections.get(i) != null) {
                List<Category> firstCategoryList = sections.get(i);
                for (Category c : firstCategoryList) {
                    result.add(c);
                }
            }
            if (sections.size() - i > 1 && sections.get(i) != null) {
                List<Category> firstCategoryList = sections.get(i);
                List<Category> secondCategoryList = sections.get(i + 1);

                for (int j = 0; j < firstCategoryList.size(); j++) {
                    Category category = firstCategoryList.get(j);
                    if (secondCategoryList != null) {
                        if (firstCategoryList.size() - j > 1 && firstCategoryList.get(j + 1) != null) {
                            Category secondCategory = firstCategoryList.get(j + 1);
                            List<Category> contents = secondCategoryList.stream()
                                    .filter(c -> c.getId() > category.getId())
                                    .filter(c -> c.getId() < secondCategory.getId())
                                    .collect(Collectors.toList());
                            fill(category, contents);
                        } else {
                            List<Category> collectList = secondCategoryList.stream()
                                    .filter(c -> c.getId() > category.getId())
                                    .collect(Collectors.toList());
                            fill(category, collectList);
                        }
                    }
                    if (i == 0) {
                        result.add(category);
                    }
                }
            }
        }
        return result;
    }

    private static int getSpaceCount(String inputString) {
        int spaceCount = inputString.length() - inputString.trim().length();
        return spaceCount;
    }
}
