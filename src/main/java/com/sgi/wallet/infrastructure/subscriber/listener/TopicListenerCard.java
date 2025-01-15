package com.sgi.wallet.infrastructure.subscriber.listener;

import com.sgi.wallet.infrastructure.annotations.KafkaController;
import com.sgi.wallet.infrastructure.subscriber.events.OrchestratorEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;


@KafkaController
@Slf4j
public class TopicListenerCard {

    @KafkaListener(
            groupId = "${app.name}",
            topics = OrchestratorEventResponse.TOPIC
    )


    private void orchestratorResult(OrchestratorEventResponse orchestratorEventResponse) {

    }
}
