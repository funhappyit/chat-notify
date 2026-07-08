package com.chatnotify.room;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    Optional<RoomMember> findByRoomIdAndUserId(Long roomId, Long userId);

    List<RoomMember> findByRoomId(Long roomId);
}
