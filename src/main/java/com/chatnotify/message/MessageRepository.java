package com.chatnotify.message;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m join fetch m.sender where m.room.id = :roomId order by m.sentAt asc")
    List<Message> findByRoomIdOrderBySentAtAsc(@Param("roomId") Long roomId);
}
