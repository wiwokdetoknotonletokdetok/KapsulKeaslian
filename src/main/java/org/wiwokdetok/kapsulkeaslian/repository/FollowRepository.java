package org.wiwokdetok.kapsulkeaslian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.wiwokdetok.kapsulkeaslian.entity.Follow;
import org.wiwokdetok.kapsulkeaslian.entity.FollowId;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.SimpleUserResponse;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    @Query("SELECT new org.wiwokdetok.kapsulkeaslian.model.SimpleUserResponse(u.id, u.name, u.profilePicture) " +
            "FROM Follow f JOIN f.following u WHERE f.follower = :follower")
    List<SimpleUserResponse> findFollowingUsers(@Param("follower") User follower);

    @Query("SELECT new org.wiwokdetok.kapsulkeaslian.model.SimpleUserResponse(u.id, u.name, u.profilePicture) " +
            "FROM Follow f JOIN f.follower u WHERE f.following = :following")
    List<SimpleUserResponse> findFollowerUsers(@Param("following") User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    void deleteByFollowerAndFollowing(User follower, User following);
}
