package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.ReturnRequestDto;
import com.ecommerce.app.entity.*;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.OrderRepository;
import com.ecommerce.app.repository.ReturnRequestRepository;
import com.ecommerce.app.service.NotificationService;
import com.ecommerce.app.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRequestRepository returnRequestRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ReturnRequest create(UUID userId, ReturnRequestDto request) {
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), userId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw ApiException.badRequest("Only delivered orders can be returned");
        }

        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setOrder(order);
        User u = new User();
        u.setId(userId);
        returnRequest.setUser(u);
        returnRequest.setReason(request.getReason());
        returnRequest.setStatus(ReturnRequest.ReturnStatus.REQUESTED);
        returnRequest = returnRequestRepository.save(returnRequest);

        order.setStatus(OrderStatus.RETURN_REQUESTED);
        orderRepository.save(order);

        return returnRequest;
    }

    @Override
    public List<ReturnRequest> listByUser(UUID userId) {
        return returnRequestRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void cancel(UUID userId, UUID returnId) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> ApiException.notFound("Return request not found"));
        if (!returnRequest.getUser().getId().equals(userId)) throw ApiException.forbidden("Not your return request");
        returnRequest.setStatus(ReturnRequest.ReturnStatus.CANCELLED);
        returnRequestRepository.save(returnRequest);
    }

    @Override
    public List<ReturnRequest> listAll() {
        return returnRequestRepository.findAll();
    }

    @Override
    @Transactional
    public void approve(UUID returnId, String remarks) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> ApiException.notFound("Return request not found"));
        returnRequest.setStatus(ReturnRequest.ReturnStatus.APPROVED);
        returnRequest.setAdminRemarks(remarks);
        returnRequestRepository.save(returnRequest);

        notificationService.send(returnRequest.getUser().getId(), "Return Approved",
                "Your return request has been approved.", "ORDER_UPDATE", returnRequest.getOrder().getId().toString());
    }

    @Override
    @Transactional
    public void reject(UUID returnId, String remarks) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> ApiException.notFound("Return request not found"));
        returnRequest.setStatus(ReturnRequest.ReturnStatus.REJECTED);
        returnRequest.setAdminRemarks(remarks);
        returnRequestRepository.save(returnRequest);

        notificationService.send(returnRequest.getUser().getId(), "Return Rejected",
                "Your return request was rejected: " + remarks, "ORDER_UPDATE", returnRequest.getOrder().getId().toString());
    }
}
