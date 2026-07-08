package com.useditemmarket.repository;

import com.useditemmarket.vo.ChatConversationVo;
import com.useditemmarket.vo.ChatMessageVo;
import org.springframework.dao.EmptyResultDataAccessException;
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
                "select c.ConversationKey, c.PeerUID, u.Uname PeerName, c.GoodsID, c.OrderPID, g.Name GoodsName, g.Image GoodsImage, c.LastTime, c.UnreadCount " +
                        "from (" +
                        "select " +
                        "case " +
                        "when ifnull(OrderPID, '') <> '' then concat('ORDER:', OrderPID) " +
                        "when ifnull(GoodsID, '') <> '' then concat('GOODS:', least(SenderUID, ReceiverUID), ':', greatest(SenderUID, ReceiverUID), ':', GoodsID) " +
                        "else concat('USER:', least(SenderUID, ReceiverUID), ':', greatest(SenderUID, ReceiverUID)) " +
                        "end ConversationKey, " +
                        "case when SenderUID = ? then ReceiverUID else SenderUID end PeerUID, " +
                        "GoodsID, OrderPID, max(CreatedAt) LastTime, " +
                        "sum(case when ReceiverUID = ? and IsRead = 0 then 1 else 0 end) UnreadCount " +
                        "from chat_message " +
                        "where SenderUID = ? or ReceiverUID = ? " +
                        "group by ConversationKey, PeerUID, GoodsID, OrderPID" +
                        ") c " +
                        "left join user u on u.UID = c.PeerUID " +
                        "left join marketgoods g on g.GID = c.GoodsID " +
                        "order by c.LastTime desc",
                (rs, rowNum) -> {
                    ChatConversationVo vo = new ChatConversationVo();
                    vo.setConversationKey(rs.getString("ConversationKey"));
                    vo.setPeerUid(rs.getString("PeerUID"));
                    vo.setPeerName(rs.getString("PeerName"));
                    vo.setGoodsId(rs.getString("GoodsID"));
                    vo.setOrderPid(rs.getString("OrderPID"));
                    vo.setGoodsName(rs.getString("GoodsName"));
                    vo.setGoodsImage(rs.getString("GoodsImage"));
                    vo.setLastTime(rs.getString("LastTime"));
                    vo.setLastMessage(findLastMessage(vo.getConversationKey()));
                    vo.setLastSenderUid(findLastSenderUid(vo.getConversationKey()));
                    vo.setUnreadCount(rs.getInt("UnreadCount"));
                    return vo;
                },
                uid, uid, uid, uid
        );
    }

    public List<ChatMessageVo> listMessages(String uid, String conversationKey) {
        String whereClause = conversationWhereClause();
        jdbcTemplate.update(
                "update chat_message set IsRead = 1 where ReceiverUID = ? and " + whereClause,
                bindConversationParams(uid, conversationKey, true)
        );
        return jdbcTemplate.query(
                "select Id, SenderUID, ReceiverUID, GoodsID, OrderPID, Content, CreatedAt, IsRead from chat_message " +
                        "where " + whereClause + " " +
                        "order by Id asc",
                chatMessageMapper(),
                bindConversationParams(uid, conversationKey, false)
        );
    }

    public ChatMessageVo sendMessage(String senderUid, String receiverUid, String goodsId, String orderPid, String content) {
        jdbcTemplate.update(
                "insert into chat_message (SenderUID, ReceiverUID, GoodsID, OrderPID, Content, CreatedAt, IsRead) values (?, ?, ?, ?, ?, now(), 0)",
                senderUid, receiverUid, goodsId, orderPid, content
        );
        Long id = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        return jdbcTemplate.queryForObject(
                "select Id, SenderUID, ReceiverUID, GoodsID, OrderPID, Content, CreatedAt, IsRead from chat_message where Id = ?",
                chatMessageMapper(),
                id
        );
    }

    public void clearGoodsId(String gid) {
        jdbcTemplate.update("update chat_message set GoodsID = null where GoodsID = ?", gid);
    }

    public ChatConversationVo findConversation(String uid, String conversationKey) {
        try {
            return jdbcTemplate.queryForObject(
                    "select c.ConversationKey, c.PeerUID, u.Uname PeerName, c.GoodsID, c.OrderPID, g.Name GoodsName, c.LastTime " +
                            "from (" +
                            "select " +
                            "case " +
                            "when ifnull(OrderPID, '') <> '' then concat('ORDER:', OrderPID) " +
                            "when ifnull(GoodsID, '') <> '' then concat('GOODS:', least(SenderUID, ReceiverUID), ':', greatest(SenderUID, ReceiverUID), ':', GoodsID) " +
                            "else concat('USER:', least(SenderUID, ReceiverUID), ':', greatest(SenderUID, ReceiverUID)) " +
                            "end ConversationKey, " +
                            "case when SenderUID = ? then ReceiverUID else SenderUID end PeerUID, " +
                            "GoodsID, OrderPID, max(CreatedAt) LastTime " +
                            "from chat_message " +
                            "where (SenderUID = ? or ReceiverUID = ?) and " + conversationWhereClause() + " " +
                            "group by ConversationKey, PeerUID, GoodsID, OrderPID" +
                            ") c " +
                            "left join user u on u.UID = c.PeerUID " +
                            "left join marketgoods g on g.GID = c.GoodsID " +
                            "limit 1",
                    (rs, rowNum) -> {
                        ChatConversationVo vo = new ChatConversationVo();
                        vo.setConversationKey(rs.getString("ConversationKey"));
                        vo.setPeerUid(rs.getString("PeerUID"));
                        vo.setPeerName(rs.getString("PeerName"));
                        vo.setGoodsId(rs.getString("GoodsID"));
                        vo.setOrderPid(rs.getString("OrderPID"));
                        vo.setGoodsName(rs.getString("GoodsName"));
                        vo.setLastTime(rs.getString("LastTime"));
                        return vo;
                    },
                    uid, uid, uid,
                    conversationKey, conversationKey, conversationKey, conversationKey, conversationKey, conversationKey
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public int deleteConversation(String uid, String conversationKey) {
        return jdbcTemplate.update(
                "delete from chat_message where (SenderUID = ? or ReceiverUID = ?) and " + conversationWhereClause(),
                uid, uid,
                conversationKey, conversationKey, conversationKey, conversationKey, conversationKey, conversationKey
        );
    }

    private String findLastMessage(String conversationKey) {
        List<String> list = jdbcTemplate.query(
                "select Content from chat_message where " + conversationWhereClause() + " order by Id desc limit 1",
                (rs, rowNum) -> rs.getString("Content"),
                bindConversationParams(null, conversationKey, false)
        );
        return list.isEmpty() ? "" : list.get(0);
    }

    private String findLastSenderUid(String conversationKey) {
        List<String> list = jdbcTemplate.query(
                "select SenderUID from chat_message where " + conversationWhereClause() + " order by Id desc limit 1",
                (rs, rowNum) -> rs.getString("SenderUID"),
                bindConversationParams(null, conversationKey, false)
        );
        return list.isEmpty() ? "" : list.get(0);
    }

    private String conversationWhereClause() {
        return "(" +
                "(? like 'ORDER:%' and OrderPID = substring(?, 7)) or " +
                "(? like 'GOODS:%' and concat('GOODS:', least(SenderUID, ReceiverUID), ':', greatest(SenderUID, ReceiverUID), ':', ifnull(GoodsID, '')) = ?) or " +
                "(? like 'USER:%' and concat('USER:', least(SenderUID, ReceiverUID), ':', greatest(SenderUID, ReceiverUID)) = ?)" +
                ")";
    }

    private Object[] bindConversationParams(String uid, String conversationKey, boolean onlyReceiver) {
        if (onlyReceiver) {
            return new Object[]{uid, conversationKey, conversationKey, conversationKey, conversationKey, conversationKey, conversationKey};
        }
        return new Object[]{conversationKey, conversationKey, conversationKey, conversationKey, conversationKey, conversationKey};
    }
}
