package vn.fastfood.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import vn.fastfood.entity.MonAn;
import vn.fastfood.repository.MonAnRepository;

@Service
public class ChatbotService {
        private static final int MAX_REPLY_LENGTH = 600;

        private final ChatClient chatClient;
        private final MonAnRepository monAnRepository;
        private final KhuyenMaiService khuyenMaiService;

        public ChatbotService(OpenAiChatModel chatModel,
                        MonAnRepository monAnRepository,
                        KhuyenMaiService khuyenMaiService) {
                this.chatClient = ChatClient.create(chatModel);
                this.monAnRepository = monAnRepository;
                this.khuyenMaiService = khuyenMaiService;
        }

        public String ask(String message) {
                List<MonAn> menuItems = monAnRepository.findListMonAn();
                khuyenMaiService.applyKhuyenMai(menuItems);

                String menuContext = menuItems.stream()
                                .map(this::toMenuLine)
                                .collect(Collectors.joining("\n"));

                String prompt = """
                                Bạn là Jollibug AI, trợ lý tư vấn món trên trang menu.
                                Chỉ trả lời dựa trên dữ liệu menu bên dưới.
                                Nếu khách hỏi ngoài phạm vi menu, hãy nói ngắn gọn rằng bạn chỉ hỗ trợ tư vấn món.

                                Cách trả lời trong khung chat nhỏ:
                                - Viết tiếng Việt có dấu.
                                - Tối đa 2 món gợi ý mỗi lần.
                                - Mỗi món theo mẫu: Tên món - Giá - Lý do ngắn.
                                - Không dùng markdown, không dùng dấu **, không đánh số 1 2 3.
                                - Không copy mô tả dài của món ăn.
                                - Toàn bộ câu trả lời tối đa 450 ký tự.
                                - Cuối câu hỏi khách muốn xem thêm loại món nào.
                                - QUAN TRỌNG: Hãy tách câu trả lời thành các dòng riêng biệt, mỗi ý chính một dòng để dễ đọc.

                                Dữ liệu menu:
                                %s
                                """
                                .formatted(menuContext);

                String reply = chatClient.prompt()
                                .system(prompt)
                                .user(message)
                                .call()
                                .content();

                return formatReplyForDisplay(reply);
        }

        private String toMenuLine(MonAn item) {
                String category = item.getDanhMuc() != null ? item.getDanhMuc().getTenDM() : "Khác";
                long price = item.isHasGiamGia() ? item.getGiaGiam() : item.getGia();
                return String.format("- %s | %s | %dd",
                                item.getTenMon(),
                                category,
                                price);
        }

        /**
         * Format reply để hiển thị dễ đọc hơn
         * - Xóa markdown formatting
         * - Tách thành các dòng rõ ràng
         * - Thêm spacing hợp lý
         */
        private String formatReplyForDisplay(String reply) {
                if (reply == null || reply.isBlank()) {
                        return "Mình chưa có gợi ý phù hợp. Bạn muốn xem món gà, burger hay combo?";
                }

                // Bước 1: Xóa markdown formatting
                String cleaned = reply
                                .replace("**", "")
                                .replace("*", "");

                // Bước 2: Xóa danh số (1. 2. 3.)
                cleaned = cleaned.replaceAll("(?m)^\\s*\\d+[\\.)]\\s*", "");

                // Bước 3: Normalize spaces nhưng GIỮ ngắt dòng tự nhiên
                String[] lines = cleaned.split("\n");
                StringBuilder formatted = new StringBuilder();

                for (String line : lines) {
                        String trimmed = line
                                        .replaceAll("\\s+", " ")
                                        .replaceAll("\\s*\\|\\s*", " - ")
                                        .trim();

                        if (!trimmed.isEmpty()) {
                                // Thêm vào nếu không rỗng
                                if (formatted.length() > 0) {
                                        formatted.append("\n");
                                }
                                formatted.append(trimmed);
                        }
                }

                String result = formatted.toString();

                // Bước 4: Nếu quá dài, cắt ngắn nhưng giữ ngắt dòng
                if (result.length() > MAX_REPLY_LENGTH) {
                        result = shortenWhilePreservingLineBreaks(result, MAX_REPLY_LENGTH);
                }

                return result;
        }

        /**
         * Cắt ngắn text nhưng giữ ngắt dòng để dễ đọc
         */
        private String shortenWhilePreservingLineBreaks(String text, int maxLength) {
                if (text.length() <= maxLength) {
                        return text;
                }

                String[] lines = text.split("\n");
                StringBuilder result = new StringBuilder();

                for (String line : lines) {
                        if ((result.length() + line.length() + 1) > maxLength) {
                                // Nếu thêm dòng này sẽ vượt quá, dừng lại
                                if (result.length() > 0) {
                                        result.append("...");
                                }
                                break;
                        }

                        if (result.length() > 0) {
                                result.append("\n");
                        }
                        result.append(line);
                }

                return result.toString();
        }

        /**
         * Cắt ngắn text đơn giản
         */
        private String shorten(String text, int maxLength) {
                if (text == null || text.length() <= maxLength) {
                        return text;
                }
                int cutAt = text.lastIndexOf(' ', maxLength - 3);
                if (cutAt < maxLength / 2) {
                        cutAt = maxLength - 3;
                }
                return text.substring(0, cutAt).trim() + "...";
        }
}
