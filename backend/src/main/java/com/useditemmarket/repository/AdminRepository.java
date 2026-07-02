package com.useditemmarket.repository;

import com.useditemmarket.vo.DashboardVo;
import com.useditemmarket.vo.GoodsVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class AdminRepository extends JdbcVoMapperSupport {
    @Resource
    private JdbcTemplate jdbcTemplate;

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
        dashboard.setGoodsPublished(queryLong(jdbcTemplate, "select count(*) from marketgoods"));
        dashboard.setGoodsActive(queryLong(jdbcTemplate, "select count(*) from marketgoods where Status = 'ACTIVE'"));
        dashboard.setGoodsPending(queryLong(jdbcTemplate, "select count(*) from marketgoods where Status = 'PENDING_REVIEW'"));
        dashboard.setTotalOrders(queryLong(jdbcTemplate, "select count(*) from traderecord"));
        dashboard.setCompletedOrders(queryLong(jdbcTemplate, "select count(*) from traderecord where Status = 'COMPLETED' or (IsSent = 1 and IsGot = 1)"));
        dashboard.setDisabledUsers(queryLong(jdbcTemplate, "select count(*) from user where Status = 2"));
        dashboard.setWantedOpen(queryLong(jdbcTemplate, "select count(*) from wanted_post where Status = 'OPEN'"));
        return dashboard;
    }
}
