package com.useditemmarket.repository;

import com.useditemmarket.vo.ChatConversationVo;
import com.useditemmarket.vo.ChatMessageVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class ChatRepository extends JdbcVoMapperSupport {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<ChatConversationVo> listConversations(String uid) {
        return jdbcTemplate.query(
                "select t.peer_uid, u.Uname PeerName, max(t.CreatedAt) LastTime from (" +
                        "select ReceiverUID peer_uid, CreatedAt from chat_message where SenderUID = ? " +
                        "union all " +
                        "select SenderUID peer_uid, CreatedAt from chat_message where ReceiverUID = ? " +
                        ") t left join user u on u.UID = t.peer_uid group by t.peer_uid, u.Uname order by LastTime desc",
                (rs, rowNum) -> {
                    ChatConversationVo vo = new ChatConversationVo();
                    vo.setPeerUid(rs.getString("peer_uid"));
                    vo.setPeerName(rs.getString("PeerName"));
                    vo.setLastTime(rs.getString("LastTime"));
                    vo.setLastMessage(findLastMessage(uid, vo.getPeerUid()));
                    return vo;
                },
                uid, uid
        );
    }

    public List<ChatMessageVo> listMessages(String uid, String peerUid) {
        jdbcTemplate.update(
                "update chat_message set IsRead = 1 where SenderUID = ? and ReceiverUID = ?",
                peerUid, uid
        );
        return jdbcTemplate.query(
                "select Id, SenderUID, ReceiverUID, GoodsID, Content, CreatedAt, IsRead from chat_message " +
                        "where (SenderUID = ? and ReceiverUID = ?) or (SenderUID = ? and ReceiverUID = ?) " +
                        "order by Id asc",
                chatMessageMapper(),
                uid, peerUid, peerUid, uid
        );
    }

    public ChatMessageVo sendMessage(String senderUid, String receiverUid, String goodsId, String content) {
        jdbcTemplate.update(
                "insert into chat_message (SenderUID, ReceiverUID, GoodsID, Content, CreatedAt, IsRead) values (?, ?, ?, ?, now(), 0)",
                senderUid, receiverUid, goodsId, content
        );
        Long id = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        return jdbcTemplate.queryForObject(
                "select Id, SenderUID, ReceiverUID, GoodsID, Content, CreatedAt, IsRead from chat_message where Id = ?",
                chatMessageMapper(),
                id
        );
    }

    private String findLastMessage(String uid, String peerUid) {
        List<String> list = jdbcTemplate.query(
                "select Content from chat_message where (SenderUID = ? and ReceiverUID = ?) or (SenderUID = ? and ReceiverUID = ?) order by Id desc limit 1",
                (rs, rowNum) -> rs.getString("Content"),
                uid, peerUid, peerUid, uid
        );
        return list.isEmpty() ? "" : list.get(0);
    }
}
