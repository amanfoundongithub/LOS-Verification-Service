package com.loan_org.verification_service.infrastructure.messaging;

import com.loan_org.verification_service.api.dto.ProcessRequest;
import com.loan_org.verification_service.core.VerificationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final VerificationOrchestrator orchestrator;

    @RabbitListener(queues = RabbitMQConfig.INBOUND_VERIFICATION_QUEUE)
    public void processQueue(ProcessRequest request) {
        orchestrator.handleVerification(request);
    }


}
