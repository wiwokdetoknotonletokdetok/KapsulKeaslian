package org.gaung.wiwokdetok.kapsulkeaslian.publisher;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpUserRegisteredMessage;

public interface UserRegisteredPublisher {

    void sendUserRegisteredMessage(AmqpUserRegisteredMessage message);
}
