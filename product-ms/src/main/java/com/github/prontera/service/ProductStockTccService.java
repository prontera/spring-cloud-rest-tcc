package com.github.prontera.service;

import com.github.prontera.Shift;
import com.github.prontera.controller.StatusCode;
import com.github.prontera.domain.Product;
import com.github.prontera.domain.ProductStockTcc;
import com.github.prontera.domain.type.TccStatus;
import com.github.prontera.event.ProductStockCancellationEvent;
import com.github.prontera.exception.ReservationExpireException;
import com.github.prontera.persistence.CrudMapper;
import com.github.prontera.persistence.ProductMapper;
import com.github.prontera.persistence.ProductStockTccMapper;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * @author Zhao Junjian
 */
@Service
public class ProductStockTccService extends CrudServiceImpl<ProductStockTcc> implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductStockTccService.class);

    // Autowired
    private ApplicationContext context;

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductStockTccMapper tccMapper;

    @Autowired
    public ProductStockTccService(CrudMapper<ProductStockTcc> mapper) {
        super(mapper);
    }

    public ProductStockTcc trying(long productId) {
        return trying(productId, 15);
    }

    public ProductStockTcc trying(long productId, long expireSeconds) {
        Preconditions.checkArgument(productId > 0);
        final Product product = productService.find(productId);
        if (product == null) {
            Shift.fatal(StatusCode.PRODUCT_NOT_EXISTS);
        }
        return trying(product, expireSeconds);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductStockTcc trying(Product product, long expireSeconds) {
        Preconditions.checkNotNull(product);
        Preconditions.checkNotNull(product.getId());
        Preconditions.checkArgument(expireSeconds > 0);
        final int isLock = productMapper.consumeStock(product.getId());
        if (isLock == 0) {
            Shift.fatal(StatusCode.INSUFFICIENT_PRODUCT);
        }
        final ProductStockTcc tcc = new ProductStockTcc();
        // 每次下单默认只能1个
        tcc.setStock(1);
        tcc.setStatus(TccStatus.TRY);
        tcc.setProductId(product.getId());
        tcc.setExpireTime(OffsetDateTime.now().plusSeconds(expireSeconds));
        persistNonNullProperties(tcc);
        return tcc;
    }

    /**
     * 本资源回收策略为定时轮询数据库, 存在资源竞争与重复计算的嫌疑, 待后续版本优化
     */
    @Scheduled(fixedRate = 1000)
    public void autoCancelTrying() {
        // 获取过期的资源
        final Set<ProductStockTcc> reservations = tccMapper.selectExpireReservation(100);
        for (ProductStockTcc res : reservations) {
            context.publishEvent(new ProductStockCancellationEvent(res));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long id) {
        Preconditions.checkNotNull(id);
        final ProductStockTcc balanceTcc = super.find(id);
        // 无法获取说明不存在或者是已经被补偿
        if (balanceTcc == null) {
            throw new ReservationExpireException("resource " + id + " has been cancelled or does not exist at all");
        }
        cancelReservation(balanceTcc);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(ProductStockTcc res) {
        Preconditions.checkNotNull(res);
        Preconditions.checkNotNull(res.getId());
        Preconditions.checkNotNull(res.getProductId());
        Preconditions.checkNotNull(res.getStatus());
        // 只能补偿在TRY阶段
        if (res.getStatus() == TccStatus.TRY) {
            // 依赖行锁, 必须开启事务
            final int isSucceedInDeleting = tccMapper.deleteTryingById(res.getId());
            // 删除成功后才能进行补偿
            if (isSucceedInDeleting == 1) {
                final int isSuccessful = productMapper.returnReservedStock(res.getProductId());
                if (isSuccessful == 0) {
                    throw new IllegalStateException("product stock reservation id " + res.getId() + " was succeeded in deleting, but failed to make compensation for product id " + res.getProductId());
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmReservation(Long id) {
        Preconditions.checkNotNull(id);
        final ProductStockTcc balanceTcc = super.find(id);
        // 无法获取说明不存在或者是已经被补偿
        if (balanceTcc == null) {
            throw new ReservationExpireException("resource " + id + " has been cancelled or does not exist at all");
        }
        // 如果为Try阶段则进行确认
        if (balanceTcc.getStatus() == TccStatus.TRY) {
            final int isSuccessful = tccMapper.updateToConfirmationById(id);
            // 如果返回0则说明已经被撤销
            if (isSuccessful == 0) {
                throw new ReservationExpireException("resource " + id + " has been cancelled");
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
