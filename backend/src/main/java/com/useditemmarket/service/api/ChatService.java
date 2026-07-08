package com.useditemmarket.service.api;

import com.useditemmarket.dto.ChatSendRequest;
import com.useditemmarket.vo.ChatConversationVo;
import com.useditemmarket.vo.ChatMessageVo;

import java.util.List;

public interface ChatService {
    List<ChatConversationVo> listConversations(String uid);

    List<ChatMessageVo> listMessages(String uid, String conversationKey);

    ChatMessageVo send(String uid, ChatSendRequest request);

    void deleteConversation(String uid, String conversationKey);
}
