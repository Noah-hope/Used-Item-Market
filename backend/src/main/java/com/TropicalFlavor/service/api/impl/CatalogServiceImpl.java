package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dto.GoodsQuery;
import com.TropicalFlavor.model.GoodsCategory;
import com.TropicalFlavor.model.GoodsSortType;
import com.TropicalFlavor.po.MarketGoods;
import com.TropicalFlavor.response.PageResponse;
import com.TropicalFlavor.service.api.CatalogService;
import com.TropicalFlavor.vo.CategoryVo;
import com.TropicalFlavor.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CatalogServiceImpl extends AbstractApiSupport implements CatalogService {
    @Override
    public PageResponse<GoodsVo> queryGoods(GoodsQuery query) {
        List<MarketGoods> goods = new ArrayList<>(goodsDao.SelectAllActiveGoods());
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
                .map(item -> GoodsVo.from(item, salesDao.WhoseGoods(item.getGID()), platformRepository.findUserName(salesDao.WhoseGoods(item.getGID()))))
                .collect(Collectors.toList());
        return new PageResponse<>(pageList, query.getPage(), query.getPageSize(), total);
    }

    @Override
    public GoodsVo getGoodsDetail(String gid) {
        MarketGoods goods = requireGoods(gid);
        String sellerUid = salesDao.WhoseGoods(gid);
        return GoodsVo.from(goods, sellerUid, platformRepository.findUserName(sellerUid));
    }

    @Override
    public List<CategoryVo> listCategories() {
        return platformRepository.listEnabledCategories();
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
