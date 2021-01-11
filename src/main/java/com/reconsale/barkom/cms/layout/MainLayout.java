package com.reconsale.barkom.cms.layout;

import com.reconsale.barkom.cms.menu.MenuCreator;
import com.reconsale.barkom.cms.models.File;
import com.reconsale.barkom.cms.services.FileService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PreserveOnRefresh;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;

@CssImport("./styles/shared-styles.css")
@PreserveOnRefresh
public class MainLayout extends AppLayout {

    private MemoryBuffer memoryBuffer = new MemoryBuffer();
    private Upload upload = new Upload(memoryBuffer);
    private VerticalLayout menu = new VerticalLayout();
    private FileService fileService;

    @Autowired
    public MainLayout(FileService fileService) {
        this.fileService = fileService;
        createHeader();
        createUploader();
    }

    private void createHeader() {
        H1 logo = new H1("Barkom");
        logo.addClassName("logo");
        Icon cutlery = new Icon(VaadinIcon.CUTLERY);
        Anchor logout = new Anchor("logout", "Вийти");
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), cutlery, logo, logout);
        header.addClassName("header");
        header.setWidth("100%");
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        addToNavbar(header);
    }

    private void createUploader() {
        upload.setDropLabel(new Label("Завантажте файл"));
        upload.addFailedListener(e -> {
            Notification notification = new Notification(
                    "Не правильний формат файлу !", 3000,
                    Notification.Position.TOP_START);
            notification.open();
        });
        upload.setMaxFileSize(10000);
        upload.addFileRejectedListener(e -> {
            Notification notification = new Notification(
                    "Не правильний формат файлу !", 3000,
                    Notification.Position.TOP_START);
            notification.open();
        });

        upload.addFinishedListener(e -> {
            InputStream inputStream = memoryBuffer.getInputStream();
            saveFileInDatabase(inputStream);
            menu.removeAll();
            createMenu();
        });

        upload.addAllFinishedListener(e -> UI.getCurrent().navigate(""));

        createMenu();
        addToDrawer(menu);
        addToDrawer(upload);
    }


    private void createMenu() {
        List<File> files = fileService.getAll();
        MenuCreator menuCreator = new MenuCreator();
        Paragraph title = new Paragraph("Список:");
        title.addClassName("branch");
        menu.add(title);
        menu.add(menuCreator.createMenu(files));
        menu.setAlignItems(FlexComponent.Alignment.START);
    }

    private void saveFileInDatabase(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String content = "";

        try {
            while (reader.ready()) {
                content += reader.readLine() + "\n";
            }
            String temporaryElement = "";
            String[] arr = content.split("\n");
            for (int i = 0; i < arr.length; i++) {
                int tempSpaceCount = temporaryElement.length() - temporaryElement.trim().length();
                if ((arr[i].length() - arr[i].trim().length()) - tempSpaceCount > 1) {
                    Notification notification = new Notification(
                            "Не правильний формат файлу !", 3000,
                            Notification.Position.TOP_START);
                    notification.open();
                    return;
                }
                temporaryElement = arr[i];
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            Notification notification = new Notification(
                    "Не правильний формат файлу !", 3000,
                    Notification.Position.TOP_START);
            notification.open();
            return;
        } finally {
            try {
                reader.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        fileService.deleteAll();
        File file = new File();
        file.setTitle(memoryBuffer.getFileName());
        file.setContent(content);
        fileService.save(file);
    }
}