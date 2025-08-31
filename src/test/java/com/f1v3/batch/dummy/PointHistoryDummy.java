package com.f1v3.batch.dummy;

import com.f1v3.batch.domain.PointHistory;
import com.f1v3.batch.domain.enums.EarnReason;
import com.f1v3.batch.domain.enums.SpendReason;
import com.f1v3.batch.domain.enums.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class PointHistoryDummy {

    public static List<PointHistory> createList(long count) {
        List<PointHistory> list = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            TransactionType transactionType = i % 2 == 0 ? TransactionType.EARN : TransactionType.USE;
            PointHistory.Builder builder = new PointHistory.Builder()
                    .memberId(i % 1000)
                    .points(100)
                    .balanceAfter((int) (1000 + i))
                    .transactionType(transactionType);

            if (transactionType == TransactionType.EARN) {
                builder.earnReason(EarnReason.MISSION);
            } else {
                builder.spendReason(SpendReason.GACHA);
            }

            list.add(builder.build());
        }

        return list;
    }
}
