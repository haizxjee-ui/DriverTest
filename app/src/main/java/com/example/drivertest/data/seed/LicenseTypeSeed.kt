package com.example.drivertest.data.seed

import com.example.drivertest.data.local.entity.LicenseTypeEntity

object LicenseTypeSeed {

    val data = listOf(
        LicenseTypeEntity(
            licenseTypeID = 1,
            name = "B",
            examTime = 20,
            totalQuestion = 30,
            passingScore = 27,
            description = "Xe ô tô chở người đến 08 chỗ ngồi(không kể ghế lái); xe tải và xe " +
                    "chuyên dụng có tổng trọng tải thiết kế đến 3.500kg; xe hạng B có rơ moóc " +
                    "có tổng khối lượng thiết kế đến 750kg"
        ),

        LicenseTypeEntity(
            licenseTypeID = 2,
            name = "C1",
            examTime = 22,
            totalQuestion = 35,
            passingScore = 32,
            description = "Xe ô tô tải, xe ô tô chuyên dụng có tổng khối lượng thiết kế từ " +
                    "3.500kg đến 7.500kg; các loại xe ô tô tải quy định cho giấy phép lái xe " +
                    "hạng C1 có rơ moóc có tổng khối lượng thiết kế đến 750kg; xe hạng B"
        ),

        LicenseTypeEntity(
            licenseTypeID = 3,
            name = "C",
            examTime = 24,
            totalQuestion = 40,
            passingScore = 36,
            description = "Xe ô tô tải, xe ô tô chuyên dụng có tổng khối lượng thiết kế trên" +
                    " 7.500kg; xe hạng C kéo rơ moóc có tổng khối lượng thiết kế đến 750kg; " +
                    "xe hạng B và hạng C1"
        ),

        LicenseTypeEntity(
            licenseTypeID = 4,
            name = "D",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "Xe ô tô chở người(kể cả xe buýt) từ 29 chỗ ngồi trở lên " +
                    "(không kể ghế lái); xe hạng D kéo rơ moóc có tổng khối lượng thiết kế đến " +
                    "750kg; xe các hạng B,C1,C,D1,D2"
        ),

        LicenseTypeEntity(
            licenseTypeID = 5,
            name = "BE",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "Xe ô tô các hạng tương ứng kéo rơ moóc"
        ),

        LicenseTypeEntity(
            licenseTypeID = 6,
            name = "D1",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "Xe ô tô chở người từ 08 chỗ ngồi trở lên(không kể ghế lái) đến 16 " +
                    "chỗ ngồi(không kể ghế lái), xe hạng D1 kéo rơ moóc có tổng khối lượng thiết " +
                    "kế đến 750kg; xe các hạng B,C1,C"
        ),

        LicenseTypeEntity(
            licenseTypeID = 7,
            name = "D2",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "xe ô tô chở người(kể cả xe buýt) từ 16 chỗ ngồi trở lên " +
                    "(không kể ghế lái) đến 29 chỗ ngồi(không kể ghế lái), xe hạng D2 kéo rơ moóc " +
                    "có tổng khối lượng thiết kế đến 750kg; xe các hạng B,C1,C,D1"
        ),

        LicenseTypeEntity(
            licenseTypeID = 8,
            name = "C1E",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "xe ô tô các hạng tương ứng kéo rơ moóc"
        ),

        LicenseTypeEntity(
            licenseTypeID = 9,
            name = "CE",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "Xe tô tô các hạng tương ứng kéo rơ moóc"
        ),

        LicenseTypeEntity(
            licenseTypeID = 10,
            name = "D1E",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "Xe tô tô các hạng tương ứng kéo rơ moóc"
        ),

        LicenseTypeEntity(
            licenseTypeID = 11,
            name = "D2E",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "Xe tô tô các hạng tương ứng kéo rơ moóc"
        ),

        LicenseTypeEntity(
            licenseTypeID = 12,
            name = "DE",
            examTime = 26,
            totalQuestion = 45,
            passingScore = 41,
            description = "Xe tô tô các hạng tương ứng kéo rơ moóc"
        )

    )
}