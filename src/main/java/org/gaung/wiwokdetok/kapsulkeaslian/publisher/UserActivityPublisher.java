package org.gaung.wiwokdetok.kapsulkeaslian.publisher;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpUserRegisteredMessage;

public interface UserActivityPublisher {

    void sendUserRegisteredMessage(AmqpUserRegisteredMessage message);
}
