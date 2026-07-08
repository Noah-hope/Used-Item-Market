package com.useditemmarket.controller.api;

import com.useditemmarket.dto.ChatSendRequest;
import com.useditemmarket.model.AuthContext;
import com.useditemmarket.response.ApiResponse;
import com.useditemmarket.service.api.ChatService;
import com.useditemmarket.vo.ChatConversationVo;
import com.useditemmarket.vo.ChatMessageVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Resource
    private ChatService chatService;

    @GetMapping("/conversations")
    public ApiResponse<List<ChatConversationVo>> conversations() {
        return ApiResponse.success(chatService.listConversations(AuthContext.get().getUid()));
    }

    @GetMapping("/messages/{conversationKey}")
    public ApiResponse<List<ChatMessageVo>> messages(@PathVariable String conversationKey) {
        return ApiResponse.success(chatService.listMessages(AuthContext.get().getUid(), conversationKey));
    }

    @PostMapping("/messages")
    public ApiResponse<ChatMessageVo> send(@RequestBody ChatSendRequest request) {
        return ApiResponse.success("发送成功", chatService.send(AuthContext.get().getUid(), request));
    }

    @DeleteMapping("/conversations/{conversationKey}")
    public ApiResponse<Void> deleteConversation(@PathVariable String conversationKey) {
        chatService.deleteConversation(AuthContext.get().getUid(), conversationKey);
        return ApiResponse.success("会话已删除", null);
    }
}
