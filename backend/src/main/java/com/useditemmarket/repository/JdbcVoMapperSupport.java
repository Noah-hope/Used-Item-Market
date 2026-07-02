package com.useditemmarket.repository;

import com.useditemmarket.vo.AddressVo;
import com.useditemmarket.vo.CategoryVo;
import com.useditemmarket.vo.ChatMessageVo;
import com.useditemmarket.vo.GoodsVo;
import com.useditemmarket.vo.WantedVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class JdbcVoMapperSupport {
    protected RowMapper<GoodsVo> goodsMapper() {
        return (rs, rowNum) -> {
            GoodsVo vo = new GoodsVo();
            setGoodsVo(rs, vo);
            return vo;
        };
    }

    protected void setGoodsVo(ResultSet rs, GoodsVo vo) throws SQLException {
        vo.setGid(rs.getString("GID"));
        vo.setName(rs.getString("Name"));
        vo.setCategory(rs.getString("Kind"));
        vo.setPrice(rs.getDouble("Price"));
        vo.setStock(rs.getDouble("Number"));
        vo.setImage(rs.getString("Image"));
        vo.setComment(rs.getString("Comment"));
        vo.setSellerUid(rs.getString("SellerUid"));
        vo.setSellerName(rs.getString("SellerName"));
        vo.setStatus(rs.getString("Status"));
        vo.setDeliveryMode(rs.getString("DeliveryMode"));
        vo.setPickupLocation(rs.getString("PickupLocation"));
        vo.setReviewNote(rs.getString("ReviewNote"));
        vo.setPublishedAt(rs.getString("PublishedAt"));
    }

    protected RowMapper<AddressVo> addressMapper() {
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

    protected RowMapper<CategoryVo> categoryMapper() {
        return (rs, rowNum) -> {
            CategoryVo vo = new CategoryVo();
            vo.setCode(rs.getString("Code"));
            vo.setLabel(rs.getString("Label"));
            vo.setSortOrder(rs.getInt("SortOrder"));
            vo.setEnabled(rs.getInt("Enabled") == 1);
            return vo;
        };
    }

    protected RowMapper<ChatMessageVo> chatMessageMapper() {
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

    protected RowMapper<WantedVo> wantedMapper() {
        return (rs, rowNum) -> {
            WantedVo vo = new WantedVo();
            vo.setId(rs.getLong("Id"));
            vo.setUid(rs.getString("UID"));
            vo.setPublisherName(rs.getString("PublisherName"));
            vo.setTitle(rs.getString("Title"));
            vo.setCategory(rs.getString("Category"));
            vo.setBudget(rs.getDouble("Budget"));
            vo.setKeyword(rs.getString("Keyword"));
            vo.setDescription(rs.getString("Description"));
            vo.setStatus(rs.getString("Status"));
            vo.setCreatedAt(rs.getString("CreatedAt"));
            return vo;
        };
    }

    protected Long queryLong(JdbcTemplate jdbcTemplate, String sql) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class);
        return value == null ? 0L : value;
    }
}
