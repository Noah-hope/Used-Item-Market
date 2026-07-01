package com.TropicalFlavor.service.api.impl;

import com.TropicalFlavor.dto.AdminGoodsReviewRequest;
import com.TropicalFlavor.dto.CategorySaveRequest;
import com.TropicalFlavor.error.BaseException;
import com.TropicalFlavor.model.GoodsStatus;
import com.TropicalFlavor.repository.PlatformRepository;
import com.TropicalFlavor.service.api.AdminOpsService;
import com.TropicalFlavor.vo.CategoryVo;
import com.TropicalFlavor.vo.DashboardVo;
import com.TropicalFlavor.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class AdminOpsServiceImpl extends AbstractApiSupport implements AdminOpsService {
    @Resource
    private PlatformRepository platformRepository;

    @Override
    public List<GoodsVo> listPendingGoods() {
        return platformRepository.listPendingGoods();
    }

    @Override
    public List<GoodsVo> listAllGoods() {
        return platformRepository.listAllAdminGoods();
    }

    @Override
    public GoodsVo reviewGoods(String gid, AdminGoodsReviewRequest request) {
        GoodsVo target = catalogServiceView(gid);
        com.TropicalFlavor.po.MarketGoods goods = requireGoods(gid);
        String action = request == null ? null : request.getAction();
        if ("approve".equalsIgnoreCase(action)) {
            goods.setStatus(GoodsStatus.ACTIVE.name());
            goods.setReviewNote(null);
            if (goods.getPublishedAt() == null) {
                goods.setPublishedAt(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            }
        } else if ("reject".equalsIgnoreCase(action)) {
            goods.setStatus(GoodsStatus.REJECTED.name());
            goods.setReviewNote(request == null ? null : request.getNote());
        } else if ("ban".equalsIgnoreCase(action)) {
            goods.setStatus(GoodsStatus.BANNED.name());
            goods.setNumber(0.0);
            goods.setReviewNote(request == null ? null : request.getNote());
        } else {
            throw new BaseException(400, "未知审核操作");
        }
        if (!goodsDao.ChangeInfo(goods)) {
            throw new BaseException(500, "审核失败");
        }
        return catalogServiceView(gid);
    }

    @Override
    public List<CategoryVo> listCategories() {
        return platformRepository.listAllCategories();
    }

    @Override
    public CategoryVo saveCategory(CategorySaveRequest request) {
        if (request == null) {
            throw new BaseException(400, "请求不能为空");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new BaseException(400, "分类编码不能为空");
        }
        if (request.getLabel() == null || request.getLabel().trim().isEmpty()) {
            throw new BaseException(400, "分类名称不能为空");
        }
        return platformRepository.saveCategory(
                request.getCode().trim(),
                request.getLabel().trim(),
                request.getSortOrder() == null ? 0 : request.getSortOrder(),
                request.getEnabled() == null || request.getEnabled()
        );
    }

    @Override
    public DashboardVo dashboard() {
        return platformRepository.loadDashboard();
    }

    private GoodsVo catalogServiceView(String gid) {
        return GoodsVo.from(requireGoods(gid), salesDao.WhoseGoods(gid));
    }
}
