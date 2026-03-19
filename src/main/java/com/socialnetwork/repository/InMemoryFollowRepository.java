package com.socialnetwork.repository;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFollowRepository implements FollowRepository {

    private final Map<String, Set<String>> followMap = new ConcurrentHashMap<>();

    @Override
    public void save(String follower, String followee) {
        followMap.computeIfAbsent(follower, nonUsed -> ConcurrentHashMap.newKeySet()).add(followee);
    }

    @Override
    public Set<String> findFollowees(String follower) {
        return Collections.unmodifiableSet(followMap.getOrDefault(follower, Set.of()));
    }
}
