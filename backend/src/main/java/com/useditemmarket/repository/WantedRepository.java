package com.useditemmarket.repository;

import com.useditemmarket.exception.BaseException;
import com.useditemmarket.vo.GoodsVo;
import com.useditemmarket.vo.WantedVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Repository
public class WantedRepository extends JdbcVoMapperSupport {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<WantedVo> listOpenWanted() {
        List<WantedVo> list = jdbcTemplate.query(
                "select w.Id, w.UID, u.Uname PublisherName, w.Title, w.Category, w.Budget, w.Keyword, w.Description, w.Status, w.CreatedAt " +
                        "from wanted_post w left join user u on u.UID = w.UID where w.Status = 'OPEN' order by w.Id desc",
                wantedMapper()
        );
        fillMatches(list);
        return list;
    }

    public List<WantedVo> listMyWanted(String uid) {
        List<WantedVo> list = jdbcTemplate.query(
                "select w.Id, w.UID, u.Uname PublisherName, w.Title, w.Category, w.Budget, w.Keyword, w.Description, w.Status, w.CreatedAt " +
                        "from wanted_post w left join user u on u.UID = w.UID where w.UID = ? order by w.Id desc",
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
                "select w.Id, w.UID, u.Uname PublisherName, w.Title, w.Category, w.Budget, w.Keyword, w.Description, w.Status, w.CreatedAt " +
                        "from wanted_post w left join user u on u.UID = w.UID where w.Id = ?",
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
                "select w.Id, w.UID, u.Uname PublisherName, w.Title, w.Category, w.Budget, w.Keyword, w.Description, w.Status, w.CreatedAt " +
                        "from wanted_post w left join user u on u.UID = w.UID where w.Id = ?",
                wantedMapper(),
                id
        );
        fillMatches(Collections.singletonList(wanted));
        return wanted;
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
}
