package com.useditemmarket.service.api.impl;

import com.useditemmarket.dto.GoodsQuery;
import com.useditemmarket.exception.BaseException;
import com.useditemmarket.model.GoodsCategory;
import com.useditemmarket.model.GoodsSortType;
import com.useditemmarket.po.MarketGoods;
import com.useditemmarket.po.MarketUser;
import com.useditemmarket.repository.CategoryRepository;
import com.useditemmarket.response.PageResponse;
import com.useditemmarket.service.api.CatalogService;
import com.useditemmarket.vo.CategoryVo;
import com.useditemmarket.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Resource;

@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl extends AbstractApiSupport implements CatalogService {
    @Resource
    private CategoryRepository categoryRepository;

    @Override
    public PageResponse<GoodsVo> queryGoods(GoodsQuery query) {
        List<MarketGoods> goods = new ArrayList<>(goodsDao.SelectAllActiveGoods());
        goods = goods.stream()
                .filter(this::isPublicVisible)
                .collect(Collectors.toList());

        String keyword = query.getKeyword();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String normalized = keyword.trim().toLowerCase(Locale.ROOT);
            goods = goods.stream()
                    .filter(item ->
                            containsIgnoreCase(item.getName(), normalized) ||
                            containsIgnoreCase(item.getKind(), normalized) ||
                            containsIgnoreCase(item.getComment(), normalized))
                    .collect(Collectors.toList());
        }

        String category = GoodsCategory.normalize(query.getCategory());
        if (category != null && !category.trim().isEmpty()) {
            goods = goods.stream()
                    .filter(item -> category.equals(item.getKind()))
                    .collect(Collectors.toList());
        }

        sort(goods, query.getSort());
        long total = goods.size();
        int fromIndex = Math.min(query.getOffset(), goods.size());
        int toIndex = Math.min(fromIndex + query.getPageSize(), goods.size());
        List<GoodsVo> pageList = goods.subList(fromIndex, toIndex).stream()
                .map(this::toGoodsVo)
                .collect(Collectors.toList());
        return new PageResponse<>(pageList, query.getPage(), query.getPageSize(), total);
    }

    @Override
    public GoodsVo getGoodsDetail(String gid) {
        MarketGoods goods = requireGoods(gid);
        if (!isPublicVisible(goods)) {
            throw new BaseException(404, "商品不存在");
        }
        return toGoodsVo(goods);
    }

    @Override
    public List<CategoryVo> listCategories() {
        return categoryRepository.listEnabledCategories();
    }

    private GoodsVo toGoodsVo(MarketGoods goods) {
        String sellerUid = salesDao.WhoseGoods(goods.getGID());
        MarketUser seller = sellerUid == null ? null : userDao.SelectUser(sellerUid);
        return GoodsVo.from(goods, sellerUid, seller == null ? null : seller.getUname());
    }

    private boolean isPublicVisible(MarketGoods goods) {
        if (goods == null) {
            return false;
        }
        if (!"ACTIVE".equals(goods.getStatus())) {
            return false;
        }
        if (goods.getNumber() == null || goods.getNumber() <= 0) {
            return false;
        }
        return !isAdminUid(salesDao.WhoseGoods(goods.getGID()));
    }

    private void sort(List<MarketGoods> goods, GoodsSortType sortType) {
        Comparator<MarketGoods> comparator;
        switch (sortType) {
            case PRICE_ASC:
                comparator = Comparator.comparing(MarketGoods::getPrice);
                break;
            case PRICE_DESC:
                comparator = Comparator.comparing(MarketGoods::getPrice).reversed();
                break;
            case STOCK_ASC:
                comparator = Comparator.comparing(MarketGoods::getNumber);
                break;
            case STOCK_DESC:
                comparator = Comparator.comparing(MarketGoods::getNumber).reversed();
                break;
            case LATEST:
            default:
                comparator = Comparator.comparing(MarketGoods::getGID).reversed();
                break;
        }
        goods.sort(comparator);
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
