package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.SupportMessageRequest;
import com.ecommerce.app.dto.request.SupportTicketRequest;
import com.ecommerce.app.entity.SupportMessage;
import com.ecommerce.app.entity.SupportTicket;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.SupportMessageRepository;
import com.ecommerce.app.repository.SupportTicketRepository;
import com.ecommerce.app.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final SupportTicketRepository ticketRepository;
    private final SupportMessageRepository messageRepository;

    @Override
    @Transactional
    public SupportTicket createTicket(UUID userId, SupportTicketRequest request) {
        SupportTicket ticket = new SupportTicket();
        User u = new User();
        u.setId(userId);
        ticket.setUser(u);
        ticket.setSubject(request.getSubject());
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        ticket = ticketRepository.save(ticket);

        SupportMessage message = new SupportMessage();
        message.setTicket(ticket);
        message.setSender(u);
        message.setMessage(request.getMessage());
        messageRepository.save(message);

        return ticket;
    }

    @Override
    public List<SupportTicket> listByUser(UUID userId) {
        return ticketRepository.findByUserId(userId);
    }

    @Override
    public SupportTicket getTicket(UUID userId, UUID ticketId) {
        SupportTicket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> ApiException.notFound("Ticket not found"));
        if (!ticket.getUser().getId().equals(userId)) throw ApiException.forbidden("Not your ticket");
        return ticket;
    }

    @Override
    @Transactional
    public SupportMessage addMessage(UUID userId, UUID ticketId, SupportMessageRequest request) {
        SupportTicket ticket = getTicket(userId, ticketId);
        SupportMessage message = new SupportMessage();
        message.setTicket(ticket);
        User u = new User();
        u.setId(userId);
        message.setSender(u);
        message.setMessage(request.getMessage());
        return messageRepository.save(message);
    }
}
