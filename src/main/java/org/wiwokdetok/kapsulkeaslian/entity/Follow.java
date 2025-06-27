package org.wiwokdetok.kapsulkeaslian.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "follow")
@Entity
public class Follow {

    @EmbeddedId
    private FollowId id;

    @ManyToOne
    @MapsId("idFollower")
    @JoinColumn(name = "id_follower")
    private User follower;

    @ManyToOne
    @MapsId("idFollowing")
    @JoinColumn(name = "id_following")
    private User following;

    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
        this.id = new FollowId(follower.getId(), following.getId());
    }
}
