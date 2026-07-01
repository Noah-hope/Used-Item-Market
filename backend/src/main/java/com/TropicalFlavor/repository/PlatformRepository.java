package com.TropicalFlavor.repository;

import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.vo.AddressVo;
import com.TropicalFlavor.vo.CategoryVo;
import com.TropicalFlavor.vo.ChatConversationVo;
import com.TropicalFlavor.vo.ChatMessageVo;
import com.TropicalFlavor.vo.DashboardVo;
import com.TropicalFlavor.vo.GoodsVo;
import com.TropicalFlavor.vo.WantedVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class PlatformRepository {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public String findSellerUidByGoods(String gid) {
        List<String> list = jdbcTemplate.query(
                "select UID from salegoods where GID = ? limit 1",
                (rs, rowNum) -> rs.getString("UID"),
                gid
        );
        return list.isEmpty() ? null : list.get(0);
    }

    public String findUserName(String uid) {
        List<String> list = jdbcTemplate.query(
                "select Uname from user where UID = ? limit 1",
                (rs, rowNum) -> rs.getString("Uname"),
                uid
        );
        return list.isEmpty() ? null : list.get(0);
    }

    public List<CategoryVo> listEnabledCategories() {
        return jdbcTemplate.query(
                "select Code, Label, SortOrder, Enabled from goods_category where Enabled = 1 order by SortOrder asc, Id asc",
                categoryMapper()
        );
    }

    public List<CategoryVo> listAllCategories() {
        return jdbcTemplate.query(
                "select Code, Label, SortOrder, Enabled from goods_category order by SortOrder asc, Id asc",
                categoryMapper()
        );
    }

    public CategoryVo saveCategory(String code, String label, Integer sortOrder, Boolean enabled) {
        int updated = jdbcTemplate.update(
                "update goods_category set Label = ?, SortOrder = ?, Enabled = ? where Code = ?",
                label, sortOrder, enabled ? 1 : 0, code
        );
        if (updated == 0) {
            jdbcTemplate.update(
                    "insert into goods_category (Code, Label, SortOrder, Enabled) values (?, ?, ?, ?)",
                    code, label, sortOrder, enabled ? 1 : 0
            );
        }
        return jdbcTemplate.queryForObject(
                "select Code, Label, SortOrder, Enabled from goods_category where Code = ?",
                categoryMapper(),
                code
        );
    }

    public List<AddressVo> listAddresses(String uid) {
        return jdbcTemplate.query(
                "select Id, ReceiverName, PhoneNumber, CampusArea, DetailAddress, IsDefault from user_address where UID = ? order by IsDefault desc, Id desc",
                addressMapper(),
                uid
        );
    }

    public AddressVo findAddress(String uid, Long id) {
        List<AddressVo> list = jdbcTemplate.query(
                "select Id, ReceiverName, PhoneNumber, CampusArea, DetailAddress, IsDefault from user_address where UID = ? and Id = ?",
                addressMapper(),
                uid, id
        );
        return list.isEmpty() ? null : list.get(0);
    }

    public AddressVo insertAddress(String uid, AddressVo address) {
        clearDefaultAddress(uid, Boolean.TRUE.equals(address.getDefaultAddress()));
        jdbcTemplate.update(
                "insert into user_address (UID, ReceiverName, PhoneNumber, CampusArea, DetailAddress, IsDefault, CreatedAt) values (?, ?, ?, ?, ?, ?, now())",
                uid, address.getReceiverName(), address.getPhoneNumber(), address.getCampusArea(), address.getDetailAddress(),
                Boolean.TRUE.equals(address.getDefaultAddress()) ? 1 : 0
        );
        Long id = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        return findAddress(uid, id);
    }

    public AddressVo updateAddress(String uid, Long id, AddressVo address) {
        clearDefaultAddress(uid, Boolean.TRUE.equals(address.getDefaultAddress()));
        int updated = jdbcTemplate.update(
                "update user_address set ReceiverName = ?, PhoneNumber = ?, CampusArea = ?, DetailAddress = ?, IsDefault = ? where UID = ? and Id = ?",
                address.getReceiverName(), address.getPhoneNumber(), address.getCampusArea(), address.getDetailAddress(),
                Boolean.TRUE.equals(address.getDefaultAddress()) ? 1 : 0, uid, id
        );
        if (updated == 0) {
            throw new BaseException(404, "地址不存在");
        }
        return findAddress(uid, id);
    }

    public void deleteAddress(String uid, Long id) {
        jdbcTemplate.update("delete from user_address where UID = ? and Id = ?", uid, id);
    }

    public String getAddressSnapshot(String uid, Long id) {
        AddressVo address = findAddress(uid, id);
        if (address == null) {
            throw new BaseException(404, "请选择有效的收货地址");
        }
        return address.getReceiverName() + " / " + address.getPhoneNumber() + " / " + address.getCampusArea() + " / " + address.getDetailAddress();
    }

    public String getDefaultAddressSnapshot(String uid) {
        List<AddressVo> list = jdbcTemplate.query(
                "select Id, ReceiverName, PhoneNumber, CampusArea, DetailAddress, IsDefault from user_address where UID = ? order by IsDefault desc, Id desc limit 1",
                addressMapper(),
                uid
        );
        if (list.isEmpty()) {
            return null;
        }
        AddressVo address = list.get(0);
        return address.getReceiverName() + " / " + address.getPhoneNumber() + " / " + address.getCampusArea() + " / " + address.getDetailAddress();
    }

    private void clearDefaultAddress(String uid, boolean shouldClear) {
        if (shouldClear) {
            jdbcTemplate.update("update user_address set IsDefault = 0 where UID = ?", uid);
        }
    }

    public boolean isFavorite(String uid, String gid) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from favorite_goods where UID = ? and GID = ?",
                Integer.class,
                uid, gid
        );
        return count != null && count > 0;
    }

    public void addFavorite(String uid, String gid) {
        if (!isFavorite(uid, gid)) {
            jdbcTemplate.update(
                    "insert into favorite_goods (UID, GID, CreatedAt) values (?, ?, now())",
                    uid, gid
            );
        }
    }

    public void removeFavorite(String uid, String gid) {
        jdbcTemplate.update("delete from favorite_goods where UID = ? and GID = ?", uid, gid);
    }

    public List<GoodsVo> listFavorites(String uid) {
        return jdbcTemplate.query(
                "select g.GID, g.Name, g.Kind, g.Price, g.Number, g.Image, g.Comment, g.Status, g.DeliveryMode, g.PickupLocation, g.ReviewNote, g.PublishedAt, s.UID SellerUid, u.Uname SellerName " +
                        "from favorite_goods f join marketgoods g on f.GID = g.GID " +
                        "join salegoods s on s.GID = g.GID " +
                        "left join user u on u.UID = s.UID " +
                        "where f.UID = ? and g.Status = 'ACTIVE' order by f.Id desc",
                goodsMapper(),
                uid
        );
    }

    public List<ChatConversationVo> listConversations(String uid) {
        return jdbcTemplate.query(
                "select peer_uid, max(CreatedAt) LastTime from (" +
                        "select ReceiverUID peer_uid, CreatedAt from chat_message where SenderUID = ? " +
                        "union all " +
                        "select SenderUID peer_uid, CreatedAt from chat_message where ReceiverUID = ? " +
                        ") t group by peer_uid order by LastTime desc",
                (rs, rowNum) -> {
                    ChatConversationVo vo = new ChatConversationVo();
                    vo.setPeerUid(rs.getString("peer_uid"));
                    vo.setPeerName(findUserName(vo.getPeerUid()));
                    vo.setLastTime(rs.getString("LastTime"));
                    vo.setLastMessage(findLastMessage(uid, vo.getPeerUid()));
                    return vo;
                },
                uid, uid
        );
    }

    public String findLastMessage(String uid, String peerUid) {
        List<String> list = jdbcTemplate.query(
                "select Content from chat_message where (SenderUID = ? and ReceiverUID = ?) or (SenderUID = ? and ReceiverUID = ?) order by Id desc limit 1",
                (rs, rowNum) -> rs.getString("Content"),
                uid, peerUid, peerUid, uid
        );
        return list.isEmpty() ? "" : list.get(0);
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

    public List<WantedVo> listOpenWanted() {
        List<WantedVo> list = jdbcTemplate.query(
                "select Id, UID, Title, Category, Budget, Keyword, Description, Status, CreatedAt from wanted_post where Status = 'OPEN' order by Id desc",
                wantedMapper()
        );
        fillMatches(list);
        return list;
    }

    public List<WantedVo> listMyWanted(String uid) {
        List<WantedVo> list = jdbcTemplate.query(
                "select Id, UID, Title, Category, Budget, Keyword, Description, Status, CreatedAt from wanted_post where UID = ? order by Id desc",
                wantedMapper(),
                uid
        );
        fillMatches(list);
        return list;
    }

    public WantedVo createWanted(String uid, WantedVo wanted) {
        jdbcTemplate.update(
                "insert into wanted_post (UID, Title, Category, Budget, Keyword, Description, Status, CreatedAt) values (?, ?, ?, ?, ?, ?, 'OPEN', now())",
                uid, wanted.getTitle(), wanted.getCategory(), wanted.getBudget(), wanted.getKeyword(), wanted.getDescription()
        );
        Long id = jdbcTemplate.queryForObject("select last_insert_id()", Long.class);
        WantedVo created = jdbcTemplate.queryForObject(
                "select Id, UID, Title, Category, Budget, Keyword, Description, Status, CreatedAt from wanted_post where Id = ?",
                wantedMapper(),
                id
        );
        fillMatches(Collections.singletonList(created));
        return created;
    }

    public WantedVo closeWanted(String uid, Long id) {
        int updated = jdbcTemplate.update("update wanted_post set Status = 'CLOSED' where UID = ? and Id = ?", uid, id);
        if (updated == 0) {
            throw new BaseException(404, "求购信息不存在");
        }
        WantedVo wanted = jdbcTemplate.queryForObject(
                "select Id, UID, Title, Category, Budget, Keyword, Description, Status, CreatedAt from wanted_post where Id = ?",
                wantedMapper(),
                id
        );
        fillMatches(Collections.singletonList(wanted));
        return wanted;
    }

    public List<GoodsVo> listPendingGoods() {
        return jdbcTemplate.query(
                "select g.GID, g.Name, g.Kind, g.Price, g.Number, g.Image, g.Comment, g.Status, g.DeliveryMode, g.PickupLocation, g.ReviewNote, g.PublishedAt, s.UID SellerUid, u.Uname SellerName " +
                        "from marketgoods g join salegoods s on s.GID = g.GID " +
                        "left join user u on u.UID = s.UID where g.Status = 'PENDING_REVIEW' order by g.GID desc",
                goodsMapper()
        );
    }

    public List<GoodsVo> listAllAdminGoods() {
        return jdbcTemplate.query(
                "select g.GID, g.Name, g.Kind, g.Price, g.Number, g.Image, g.Comment, g.Status, g.DeliveryMode, g.PickupLocation, g.ReviewNote, g.PublishedAt, s.UID SellerUid, u.Uname SellerName " +
                        "from marketgoods g join salegoods s on s.GID = g.GID " +
                        "left join user u on u.UID = s.UID order by g.GID desc",
                goodsMapper()
        );
    }

    public DashboardVo loadDashboard() {
        DashboardVo dashboard = new DashboardVo();
        dashboard.setGoodsPublished(queryLong("select count(*) from marketgoods"));
        dashboard.setGoodsActive(queryLong("select count(*) from marketgoods where Status = 'ACTIVE'"));
        dashboard.setGoodsPending(queryLong("select count(*) from marketgoods where Status = 'PENDING_REVIEW'"));
        dashboard.setTotalOrders(queryLong("select count(*) from traderecord"));
        dashboard.setCompletedOrders(queryLong("select count(*) from traderecord where Status = 'COMPLETED' or (IsSent = 1 and IsGot = 1)"));
        dashboard.setDisabledUsers(queryLong("select count(*) from user where Status = 2"));
        dashboard.setWantedOpen(queryLong("select count(*) from wanted_post where Status = 'OPEN'"));
        return dashboard;
    }

    private Long queryLong(String sql) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class);
        return value == null ? 0L : value;
    }

    private void fillMatches(List<WantedVo> wantedList) {
        for (WantedVo wanted : wantedList) {
            List<GoodsVo> matches = jdbcTemplate.query(
                    "select g.GID, g.Name, g.Kind, g.Price, g.Number, g.Image, g.Comment, g.Status, g.DeliveryMode, g.PickupLocation, g.ReviewNote, g.PublishedAt, s.UID SellerUid, u.Uname SellerName " +
                            "from marketgoods g join salegoods s on s.GID = g.GID " +
                            "left join user u on u.UID = s.UID " +
                            "where g.Status = 'ACTIVE' and g.Number > 0 and (" +
                            "g.Kind = ? or g.Name like concat('%', ?, '%') or g.Comment like concat('%', ?, '%')) " +
                            "order by g.GID desc limit 6",
                    goodsMapper(),
                    wanted.getCategory(), wanted.getKeyword(), wanted.getKeyword()
            );
            wanted.setMatches(matches);
        }
    }

    private RowMapper<GoodsVo> goodsMapper() {
        return (rs, rowNum) -> {
            GoodsVo vo = new GoodsVo();
            setGoodsVo(rs, vo);
            return vo;
        };
    }

    private void setGoodsVo(ResultSet rs, GoodsVo vo) throws SQLException {
        setField(vo, "gid", rs.getString("GID"));
        setField(vo, "name", rs.getString("Name"));
        setField(vo, "category", rs.getString("Kind"));
        setField(vo, "price", rs.getDouble("Price"));
        setField(vo, "stock", rs.getDouble("Number"));
        setField(vo, "image", rs.getString("Image"));
        setField(vo, "comment", rs.getString("Comment"));
        setField(vo, "sellerUid", rs.getString("SellerUid"));
        setField(vo, "sellerName", rs.getString("SellerName"));
        setField(vo, "status", rs.getString("Status"));
        setField(vo, "deliveryMode", rs.getString("DeliveryMode"));
        setField(vo, "pickupLocation", rs.getString("PickupLocation"));
        setField(vo, "reviewNote", rs.getString("ReviewNote"));
        setField(vo, "publishedAt", rs.getString("PublishedAt"));
    }

    private RowMapper<AddressVo> addressMapper() {
        return (rs, rowNum) -> {
            AddressVo vo = new AddressVo();
            vo.setId(rs.getLong("Id"));
            vo.setReceiverName(rs.getString("ReceiverName"));
            vo.setPhoneNumber(rs.getString("PhoneNumber"));
            vo.setCampusArea(rs.getString("CampusArea"));
            vo.setDetailAddress(rs.getString("DetailAddress"));
            vo.setDefaultAddress(rs.getInt("IsDefault") == 1);
            return vo;
        };
    }

    private RowMapper<CategoryVo> categoryMapper() {
        return (rs, rowNum) -> {
            CategoryVo vo = new CategoryVo();
            vo.setCode(rs.getString("Code"));
            vo.setLabel(rs.getString("Label"));
            vo.setSortOrder(rs.getInt("SortOrder"));
            vo.setEnabled(rs.getInt("Enabled") == 1);
            return vo;
        };
    }

    private RowMapper<ChatMessageVo> chatMessageMapper() {
        return (rs, rowNum) -> {
            ChatMessageVo vo = new ChatMessageVo();
            vo.setId(rs.getLong("Id"));
            vo.setSenderUid(rs.getString("SenderUID"));
            vo.setReceiverUid(rs.getString("ReceiverUID"));
            vo.setGoodsId(rs.getString("GoodsID"));
            vo.setContent(rs.getString("Content"));
            vo.setCreatedAt(rs.getString("CreatedAt"));
            vo.setRead(rs.getInt("IsRead") == 1);
            return vo;
        };
    }

    private RowMapper<WantedVo> wantedMapper() {
        return (rs, rowNum) -> {
            WantedVo vo = new WantedVo();
            vo.setId(rs.getLong("Id"));
            vo.setUid(rs.getString("UID"));
            vo.setTitle(rs.getString("Title"));
            vo.setCategory(rs.getString("Category"));
            vo.setBudget(rs.getDouble("Budget"));
            vo.setKeyword(rs.getString("Keyword"));
            vo.setDescription(rs.getString("Description"));
            vo.setStatus(rs.getString("Status"));
            vo.setCreatedAt(rs.getString("CreatedAt"));
            vo.setPublisherName(findUserName(vo.getUid()));
            return vo;
        };
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to set field " + fieldName, ex);
        }
    }
}
