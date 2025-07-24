package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserRankingResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PointServiceImpl implements PointService {

    private final UserService userService;

    private final UserRepository userRepository;

    public PointServiceImpl(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void addPoints(UUID userId, int points) {
        User user = userService.getUserById(userId);

        user.setPoints(user.getPoints() + points);
        userRepository.save(user);
    }

    @Override
    public List<UserRankingResponse> getUserRanking() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "points"));

        return users.stream()
                .map(user -> new UserRankingResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        user.getPoints(),
                        user.getProfilePicture()
                ))
                .toList();

    }

}
