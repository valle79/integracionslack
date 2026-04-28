package pe.edu.vallegrande.slack.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.slack.model.Message;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MessageServiceTest {

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService();
    }

    @Test
    void testGetAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        assertNotNull(messages);
        assertEquals(2, messages.size());
    }

    @Test
    void testCreateMessage() {
        Message newMessage = new Message();
        newMessage.setContent("Test message");
        newMessage.setAuthor("Test User");

        Message created = messageService.createMessage(newMessage);

        assertNotNull(created.getId());
        assertNotNull(created.getTimestamp());
        assertEquals("Test message", created.getContent());
    }

    @Test
    void testGetMessageById() {
        Optional<Message> message = messageService.getMessageById(1L);
        assertTrue(message.isPresent());
        assertEquals("Bienvenido al sistema", message.get().getContent());
    }

    @Test
    void testGetMessageByIdNotFound() {
        Optional<Message> message = messageService.getMessageById(999L);
        assertFalse(message.isPresent());
    }
}
