package com.f1v3.batch.service;

import com.f1v3.batch.domain.PendingProduct;
import com.f1v3.batch.dummy.PendingProductDummy;
import com.f1v3.batch.dummy.UniqueProductDummy;
import com.f1v3.batch.repository.pendingproduct.PendingProductBulkRepository;
import com.f1v3.batch.repository.pendingproduct.PendingProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PendingProductService {

    private final PendingProductBulkRepository pendingProductBulkRepository;
    private final PendingProductRepository pendingProductRepository;

    public void createTwoMillionMixedStatusProducts() {
        List<PendingProduct> products = PendingProductDummy.createTwoMillionMixedStatusProducts();
        saveProductsInBatches(products);
    }

    public void createMillionPendingProducts() {
        List<PendingProduct> products = PendingProductDummy.createPendingProducts();
        saveProductsInBatches(products);
    }

    private void saveProductsInBatches(List<PendingProduct> products) {
        int batchSize = 10_000;
        int totalBatches = (int) Math.ceil((double) products.size() / batchSize);

        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, products.size());
            List<PendingProduct> batch = products.subList(start, end);


            pendingProductBulkRepository.bulkInsertWithPreparedStatement(batch);
            log.info("Inserted batch {}/{} ({} - {})", i + 1, totalBatches, start + 1, end);
        }
    }

    public void createUniqueProducts() {
        List<PendingProduct> products = UniqueProductDummy.createExactly64625UniqueProducts();
        saveProductsInBatches(products);
    }

    @Transactional(readOnly = true)
    public List<PendingProduct> findByNameAndDescription(String keyword) {

        // TODO: 단순 LIKE %keyword% 검색
        return pendingProductRepository.findByNameAndDescription(keyword);
    }

    @Transactional(readOnly = true)
    public List<PendingProduct> findByNameAndDescriptionV2(String keyword) {

        // TODO: FULLTEXT INDEX 검색
        return pendingProductRepository.findByNameAndDescriptionV2(keyword);
    }
}
