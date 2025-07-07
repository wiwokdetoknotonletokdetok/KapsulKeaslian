package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserRankingResponse;

import java.util.List;
import java.util.UUID;

public interface PointService {

    void addPoints(UUID userId, int points);

    List<UserRankingResponse> getUserRanking();
}
