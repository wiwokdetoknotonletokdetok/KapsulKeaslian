package org.gaung.wiwokdetok.kapsulkeaslian.repository;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.Follow;
import org.gaung.wiwokdetok.kapsulkeaslian.model.FollowId;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    @Query("SELECT new org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse(u.id, u.name, u.profilePicture) " +
            "FROM Follow f JOIN f.following u WHERE f.follower = :follower")
    Page<SimpleUserResponse> findFollowingUsers(@Param("follower") User follower, Pageable pageable);

    @Query("SELECT new org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse(u.id, u.name, u.profilePicture) " +
            "FROM Follow f JOIN f.follower u WHERE f.following = :following")
    Page<SimpleUserResponse> findFollowerUsers(@Param("following") User following, Pageable pageable);

    boolean existsByFollowerAndFollowing(User follower, User following);

    void deleteByFollowerAndFollowing(User follower, User following);
}
