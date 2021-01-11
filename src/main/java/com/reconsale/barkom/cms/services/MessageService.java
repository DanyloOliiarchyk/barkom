package com.reconsale.barkom.cms.services;

import com.reconsale.barkom.cms.models.Message;
import com.reconsale.barkom.cms.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void delete(Message message) {
        messageRepository.delete(message);
    }

    public List<Message> getAllByCategory(String category, String filter) {
        if (filter == null || filter.isEmpty()) {
            return messageRepository.findAllByCategory(category);
        } else {
            return messageRepository.findAllByCategoryAndTitle(category, filter);
        }
    }

    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    public void save(Message message) {
        messageRepository.save(message);
    }
}
