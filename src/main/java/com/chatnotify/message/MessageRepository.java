package com.chatnotify.message;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m join fetch m.sender where m.room.id = :roomId order by m.sentAt asc")
    List<Message> findByRoomIdOrderBySentAtAsc(@Param("roomId") Long roomId);

    long countByRoomIdAndSenderIdNotAndReadFalse(Long roomId, Long senderId);

    @Modifying
    @Query("update Message m set m.read = true where m.room.id = :roomId and m.sender.id <> :viewerId and m.read = false")
    int markAsRead(@Param("roomId") Long roomId, @Param("viewerId") Long viewerId);
}
