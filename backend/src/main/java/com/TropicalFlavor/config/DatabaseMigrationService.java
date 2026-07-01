package com.TropicalFlavor.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class DatabaseMigrationService {
    @Resource
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrate() {
        ensureUserTable();
        ensureMarketGoodsTable();
        ensureTradeRecordTable();
        ensureExtraTables();
        seedCategories();
    }

    private void ensureUserTable() {
        addColumnIfMissing("user", "StudentNo", "varchar(32) default null");
        addColumnIfMissing("user", "RealName", "varchar(32) default null");
        addColumnIfMissing("user", "CampusVerified", "tinyint default 1");
        addColumnIfMissing("user", "Avatar", "varchar(1000) default null");
        addColumnIfMissing("user", "Bio", "varchar(500) default null");
        jdbcTemplate.update("update user set StudentNo = substring(UID, 5) where StudentNo is null and UID like 'NORM%'");
        jdbcTemplate.update("update user set RealName = Uname where RealName is null");
        jdbcTemplate.update("update user set CampusVerified = 1 where CampusVerified is null");
    }

    private void ensureMarketGoodsTable() {
        jdbcTemplate.update("alter table marketgoods modify column Image mediumtext");
        addColumnIfMissing("marketgoods", "Status", "varchar(32) default 'ACTIVE'");
        addColumnIfMissing("marketgoods", "DeliveryMode", "varchar(32) default 'SELF_PICKUP'");
        addColumnIfMissing("marketgoods", "PickupLocation", "varchar(255) default null");
        addColumnIfMissing("marketgoods", "CampusOnly", "tinyint default 1");
        addColumnIfMissing("marketgoods", "ReviewNote", "varchar(255) default null");
        addColumnIfMissing("marketgoods", "PublishedAt", "varchar(32) default null");
        jdbcTemplate.update("update marketgoods set Status = 'ACTIVE' where Status is null and Number > 0");
        jdbcTemplate.update("update marketgoods set Status = 'OFF_SHELF' where Status is null and Number <= 0");
        jdbcTemplate.update("update marketgoods set DeliveryMode = 'SELF_PICKUP' where DeliveryMode is null");
        jdbcTemplate.update("update marketgoods set CampusOnly = 1 where CampusOnly is null");
        jdbcTemplate.update("update marketgoods set PublishedAt = date_format(now(), '%Y-%m-%d %H:%i:%s') where PublishedAt is null");
    }

    private void ensureTradeRecordTable() {
        addColumnIfMissing("traderecord", "Status", "varchar(32) default 'PENDING_CONTACT'");
        addColumnIfMissing("traderecord", "DeliveryMode", "varchar(32) default 'SELF_PICKUP'");
        addColumnIfMissing("traderecord", "PickupLocation", "varchar(255) default null");
        addColumnIfMissing("traderecord", "AppointmentTime", "varchar(64) default null");
        addColumnIfMissing("traderecord", "Remark", "varchar(255) default null");
        addColumnIfMissing("traderecord", "AddressSnapshot", "varchar(500) default null");
        jdbcTemplate.update("update traderecord set Status = 'PENDING_CONTACT' where Status is null and ifnull(IsSent,0) = 0 and ifnull(IsGot,0) = 0");
        jdbcTemplate.update("update traderecord set Status = 'PENDING_PICKUP' where Status is null and ifnull(IsSent,0) = 1 and ifnull(IsGot,0) = 0");
        jdbcTemplate.update("update traderecord set Status = 'COMPLETED' where Status is null and ifnull(IsSent,0) = 1 and ifnull(IsGot,0) = 1");
        jdbcTemplate.update("update traderecord set DeliveryMode = 'SELF_PICKUP' where DeliveryMode is null");
    }

    private void ensureExtraTables() {
        jdbcTemplate.update("create table if not exists user_address (" +
                "Id bigint primary key auto_increment," +
                "UID char(18) not null," +
                "ReceiverName varchar(32) not null," +
                "PhoneNumber varchar(20) not null," +
                "CampusArea varchar(64) not null," +
                "DetailAddress varchar(255) not null," +
                "IsDefault tinyint default 0," +
                "CreatedAt datetime default current_timestamp" +
                ")");
        jdbcTemplate.update("create table if not exists favorite_goods (" +
                "Id bigint primary key auto_increment," +
                "UID char(18) not null," +
                "GID varchar(10) not null," +
                "CreatedAt datetime default current_timestamp," +
                "unique key uk_favorite_uid_gid (UID, GID)" +
                ")");
        jdbcTemplate.update("create table if not exists chat_message (" +
                "Id bigint primary key auto_increment," +
                "SenderUID char(18) not null," +
                "ReceiverUID char(18) not null," +
                "GoodsID varchar(10) default null," +
                "Content varchar(1000) not null," +
                "CreatedAt datetime default current_timestamp," +
                "IsRead tinyint default 0" +
                ")");
        jdbcTemplate.update("create table if not exists goods_category (" +
                "Id bigint primary key auto_increment," +
                "Code varchar(64) not null," +
                "Label varchar(64) not null," +
                "SortOrder int default 0," +
                "Enabled tinyint default 1," +
                "unique key uk_goods_category_code (Code)" +
                ")");
        jdbcTemplate.update("create table if not exists wanted_post (" +
                "Id bigint primary key auto_increment," +
                "UID char(18) not null," +
                "Title varchar(100) not null," +
                "Category varchar(64) not null," +
                "Budget double default 0," +
                "Keyword varchar(64) default null," +
                "Description varchar(500) default null," +
                "Status varchar(32) default 'OPEN'," +
                "CreatedAt datetime default current_timestamp" +
                ")");
    }

    private void seedCategories() {
        insertCategoryIfMissing("TEXTBOOK", "教材", 10);
        insertCategoryIfMissing("DIGITAL", "数码", 20);
        insertCategoryIfMissing("DORMITORY", "宿舍用品", 30);
        insertCategoryIfMissing("SPORTS", "运动器材", 40);
        insertCategoryIfMissing("DAILY", "日用品", 50);
        insertCategoryIfMissing("OTHER", "其他", 60);
    }

    private void insertCategoryIfMissing(String code, String label, int sortOrder) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from goods_category where Code = ?",
                Integer.class,
                code
        );
        if (count == null || count == 0) {
            jdbcTemplate.update(
                    "insert into goods_category (Code, Label, SortOrder, Enabled) values (?, ?, ?, 1)",
                    code, label, sortOrder
            );
        }
    }

    private void addColumnIfMissing(String tableName, String columnName, String ddl) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.columns where table_schema = database() and table_name = ? and column_name = ?",
                Integer.class,
                tableName, columnName
        );
        if (count == null || count == 0) {
            jdbcTemplate.update("alter table " + tableName + " add column " + columnName + " " + ddl);
        }
    }
}
