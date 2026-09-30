package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.SupportMessageRequest;
import com.ecommerce.app.dto.request.SupportTicketRequest;
import com.ecommerce.app.entity.SupportMessage;
import com.ecommerce.app.entity.SupportTicket;

import java.util.List;
import java.util.UUID;

public interface SupportService {
    SupportTicket createTicket(UUID userId, SupportTicketRequest request);
    List<SupportTicket> listByUser(UUID userId);
    SupportTicket getTicket(UUID userId, UUID ticketId);
    SupportMessage addMessage(UUID userId, UUID ticketId, SupportMessageRequest request);
}
