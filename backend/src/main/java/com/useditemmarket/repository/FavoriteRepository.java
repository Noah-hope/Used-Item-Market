package com.useditemmarket.repository;

import com.useditemmarket.vo.GoodsVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class FavoriteRepository extends JdbcVoMapperSupport {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<GoodsVo> listFavorites(String uid) {
        return jdbcTemplate.query(
                "select g.GID, g.Name, g.Kind, g.Price, g.Number, g.Image, g.Comment, g.Status, g.DeliveryMode, g.PickupLocation, g.ReviewNote, g.PublishedAt, s.UID SellerUid, u.Uname SellerName " +
                        "from favorite_goods f join marketgoods g on f.GID = g.GID " +
                        "join salegoods s on s.GID = g.GID " +
                        "left join user u on u.UID = s.UID " +
                        "where f.UID = ? order by f.Id desc",
                goodsMapper(),
                uid
        );
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

    public void deleteByGid(String gid) {
        jdbcTemplate.update("delete from favorite_goods where GID = ?", gid);
    }

    private boolean isFavorite(String uid, String gid) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from favorite_goods where UID = ? and GID = ?",
                Integer.class,
                uid, gid
        );
        return count != null && count > 0;
    }
}
