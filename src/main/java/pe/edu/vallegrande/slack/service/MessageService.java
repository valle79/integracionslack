package pe.edu.vallegrande.slack.service;

import org.springframework.stereotype.Service;
import pe.edu.vallegrande.slack.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {

    private final List<Message> messages = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public MessageService() {
        // Datos iniciales
        messages.add(new Message(idGenerator.getAndIncrement(), 
                "Bienvenido al sistema", "Sistema", LocalDateTime.now()));
        messages.add(new Message(idGenerator.getAndIncrement(), 
                "Integración Jenkins-Slack activa", "Admin", LocalDateTime.now()));
    }

    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public Message createMessage(Message message) {
        message.setId(idGenerator.getAndIncrement());
        message.setTimestamp(LocalDateTime.now());
        messages.add(message);
        return message;
    }

    public Optional<Message> getMessageById(Long id) {
        return messages.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }
}
