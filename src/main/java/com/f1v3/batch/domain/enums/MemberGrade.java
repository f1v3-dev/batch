package com.f1v3.batch.domain.enums;


public enum MemberGrade {
    BRONZE,
    SILVER,
    GOLD,
    DIAMOND,
    ;

    public String getDescription() {
        return this.name();
    }
}
