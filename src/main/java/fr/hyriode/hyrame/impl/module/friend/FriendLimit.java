package fr.hyriode.hyrame.impl.module.friend;

import fr.hyriode.api.rank.type.HyriPlayerRankType;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/04/2022 at 12:51
 */
public enum FriendLimit {

    PLAYER(HyriPlayerRankType.PLAYER, 15),
    VIP(HyriPlayerRankType.VIP, 20),
    VIP_PLUS(HyriPlayerRankType.VIP_PLUS, 30),
    EPIC(HyriPlayerRankType.EPIC, 50),
    PARTNER(HyriPlayerRankType.PARTNER, 100);

    private final HyriPlayerRankType associatedRank;
    private final int maxFriends;

    FriendLimit(HyriPlayerRankType associatedRank, int maxFriends) {
        this.associatedRank = associatedRank;
        this.maxFriends = maxFriends;
    }

    public static int getMaxFriends(HyriPlayerRankType rankType) {
        for (FriendLimit limit : values()) {
            if (limit.getAssociatedRank() == rankType) {
                return limit.getMaxFriends();
            }
        }
        return -1;
    }

    public HyriPlayerRankType getAssociatedRank() {
        return this.associatedRank;
    }

    public int getMaxFriends() {
        return this.maxFriends;
    }
}
