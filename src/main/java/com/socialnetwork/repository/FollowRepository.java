package com.socialnetwork.repository;

import java.util.Set;

public interface FollowRepository {

    void save(String follower, String followee);

    Set<String> findFollowees(String follower);
}
