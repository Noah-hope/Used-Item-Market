package com.TropicalFlavor.controller.api;

import com.TropicalFlavor.dto.ChatSendRequest;
import com.TropicalFlavor.model.AuthContext;
import com.TropicalFlavor.response.ApiResponse;
import com.TropicalFlavor.service.api.ChatService;
import com.TropicalFlavor.vo.ChatConversationVo;
import com.TropicalFlavor.vo.ChatMessageVo;
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

    @GetMapping("/messages/{peerUid}")
    public ApiResponse<List<ChatMessageVo>> messages(@PathVariable String peerUid) {
        return ApiResponse.success(chatService.listMessages(AuthContext.get().getUid(), peerUid));
    }

    @PostMapping("/messages")
    public ApiResponse<ChatMessageVo> send(@RequestBody ChatSendRequest request) {
        return ApiResponse.success("发送成功", chatService.send(AuthContext.get().getUid(), request));
    }
}
