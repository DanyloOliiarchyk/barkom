package com.reconsale.barkom.cms.services;

import com.reconsale.barkom.cms.models.File;
import com.reconsale.barkom.cms.repositories.FileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

    private FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File getByName(String title){
        return fileRepository.findByTitle(title);
    }


    public List<File> getAll() {
        return fileRepository.findAll();
    }

    public void save(File file) {
        fileRepository.save(file);
    }

    public void deleteAll (){
        fileRepository.deleteAll();
    }
}
