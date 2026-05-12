package vn.fastfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.fastfood.entity.ChatRecord;

@Repository
public interface ChatRecordRepository extends JpaRepository<ChatRecord, Long> {

    /** Lấy toàn bộ tin nhắn của một conversation, sắp theo thời gian */
    List<ChatRecord> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    /** Lấy conversationId duy nhất (danh sách khách đang chat với nhân viên) */
    @Query("SELECT DISTINCT r.conversationId FROM ChatRecord r ORDER BY r.conversationId")
    List<String> findDistinctConversationIds();

    /** Lấy tin nhắn cuối cùng của mỗi conversation (dùng cho sidebar staff) */
    ChatRecord findTop1ByConversationIdOrderByCreatedAtDesc(String conversationId);
}
