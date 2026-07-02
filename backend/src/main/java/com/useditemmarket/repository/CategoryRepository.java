package com.useditemmarket.repository;

import com.useditemmarket.vo.CategoryVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class CategoryRepository extends JdbcVoMapperSupport {
    @Resource
    private JdbcTemplate jdbcTemplate;

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
}
