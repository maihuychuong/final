package com.example.demo.data;

import com.example.demo.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SampleData {
    public static List<Product> getSampleProducts() {
        List<Product> products = new ArrayList<>();

        products.add(Product.builder()
                .name("iPhone 14 Pro Max")
                .brand("Apple")
                .price(new BigDecimal("29990000"))
                .discountPercent(10)
                .stock(100)
                .description("Điện thoại Apple iPhone 14 Pro Max mới nhất")
                .imageUrl("iphone14promax.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Samsung Galaxy S23 Ultra")
                .brand("Samsung")
                .price(new BigDecimal("27990000"))
                .discountPercent(15)
                .stock(80)
                .description("Điện thoại Samsung Galaxy S23 Ultra mạnh mẽ")
                .imageUrl("galaxys23ultra.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Xiaomi Redmi Note 12")
                .brand("Xiaomi")
                .price(new BigDecimal("5790000"))
                .discountPercent(5)
                .stock(150)
                .description("Điện thoại Xiaomi Redmi Note 12 giá tốt")
                .imageUrl("redminote12.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("OPPO Reno 8 Pro")
                .brand("OPPO")
                .price(new BigDecimal("8990000"))
                .discountPercent(8)
                .stock(60)
                .description("Điện thoại OPPO Reno 8 Pro cấu hình ổn định")
                .imageUrl("opporeno8pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Vivo X80 Pro")
                .brand("Vivo")
                .price(new BigDecimal("19990000"))
                .discountPercent(12)
                .stock(40)
                .description("Điện thoại Vivo X80 Pro camera siêu nét")
                .imageUrl("vivox80pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Google Pixel 7")
                .brand("Google")
                .price(new BigDecimal("18990000"))
                .discountPercent(7)
                .stock(30)
                .description("Điện thoại Google Pixel 7 Android thuần")
                .imageUrl("pixel7.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Sony Xperia 1 IV")
                .brand("Sony")
                .price(new BigDecimal("24990000"))
                .discountPercent(10)
                .stock(20)
                .description("Điện thoại Sony Xperia 1 IV màn hình 4K")
                .imageUrl("xperia1iv.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("OnePlus 11")
                .brand("OnePlus")
                .price(new BigDecimal("17990000"))
                .discountPercent(9)
                .stock(50)
                .description("Điện thoại OnePlus 11 hiệu năng mạnh")
                .imageUrl("oneplus11.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Asus ROG Phone 6")
                .brand("Asus")
                .price(new BigDecimal("20990000"))
                .discountPercent(13)
                .stock(25)
                .description("Điện thoại Asus ROG Phone 6 chơi game đỉnh")
                .imageUrl("rogphone6.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Realme GT Neo 3")
                .brand("Realme")
                .price(new BigDecimal("7490000"))
                .discountPercent(6)
                .stock(70)
                .description("Điện thoại Realme GT Neo 3 giá tốt hiệu năng ổn")
                .imageUrl("realmegtneo3.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Huawei P50 Pro")
                .brand("Huawei")
                .price(new BigDecimal("19990000"))
                .discountPercent(10)
                .stock(45)
                .description("Điện thoại Huawei P50 Pro camera Leica")
                .imageUrl("huaweip50pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Nokia G50")
                .brand("Nokia")
                .price(new BigDecimal("5290000"))
                .discountPercent(5)
                .stock(60)
                .description("Điện thoại Nokia G50 pin khỏe, giá rẻ")
                .imageUrl("nokiag50.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Motorola Edge 30")
                .brand("Motorola")
                .price(new BigDecimal("8990000"))
                .discountPercent(8)
                .stock(30)
                .description("Điện thoại Motorola Edge 30 thiết kế đẹp")
                .imageUrl("motorolaedge30.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Realme 10 Pro+")
                .brand("Realme")
                .price(new BigDecimal("7490000"))
                .discountPercent(7)
                .stock(50)
                .description("Điện thoại Realme 10 Pro+ cấu hình mạnh")
                .imageUrl("realme10proplus.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Sony Xperia 10 IV")
                .brand("Sony")
                .price(new BigDecimal("10990000"))
                .discountPercent(12)
                .stock(25)
                .description("Điện thoại Sony Xperia 10 IV gọn nhẹ")
                .imageUrl("xperia10iv.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Samsung Galaxy A54")
                .brand("Samsung")
                .price(new BigDecimal("8490000"))
                .discountPercent(9)
                .stock(75)
                .description("Điện thoại Samsung Galaxy A54 màn hình đẹp")
                .imageUrl("galaxya54.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Xiaomi Mi 13")
                .brand("Xiaomi")
                .price(new BigDecimal("17990000"))
                .discountPercent(14)
                .stock(40)
                .description("Điện thoại Xiaomi Mi 13 cấu hình khủng")
                .imageUrl("mi13.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Infinix Zero 5G 2023")
                .brand("Infinix")
                .price(new BigDecimal("4990000"))
                .discountPercent(5)
                .stock(100)
                .description("Điện thoại Infinix Zero 5G 2023 pin khủng")
                .imageUrl("infinixzero5g2023.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Google Pixel 7 Pro")
                .brand("Google")
                .price(new BigDecimal("23990000"))
                .discountPercent(10)
                .stock(20)
                .description("Điện thoại Google Pixel 7 Pro camera tuyệt vời")
                .imageUrl("pixel7pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Asus Zenfone 9")
                .brand("Asus")
                .price(new BigDecimal("15990000"))
                .discountPercent(11)
                .stock(35)
                .description("Điện thoại Asus Zenfone 9 nhỏ gọn")
                .imageUrl("zenfone9.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Vivo Y35")
                .brand("Vivo")
                .price(new BigDecimal("5690000"))
                .discountPercent(6)
                .stock(80)
                .description("Điện thoại Vivo Y35 pin trâu")
                .imageUrl("vivoy35.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Realme Narzo 60X")
                .brand("Realme")
                .price(new BigDecimal("3990000"))
                .discountPercent(4)
                .stock(90)
                .description("Điện thoại Realme Narzo 60X giá rẻ")
                .imageUrl("narzo60x.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Samsung Galaxy Z Fold4")
                .brand("Samsung")
                .price(new BigDecimal("41990000"))
                .discountPercent(18)
                .stock(10)
                .description("Điện thoại màn hình gập Samsung Galaxy Z Fold4")
                .imageUrl("galaxyzfold4.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("iPhone SE 2022")
                .brand("Apple")
                .price(new BigDecimal("10990000"))
                .discountPercent(5)
                .stock(50)
                .description("Điện thoại Apple iPhone SE 2022 nhỏ gọn")
                .imageUrl("iphonese2022.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Oppo A96")
                .brand("Oppo")
                .price(new BigDecimal("6490000"))
                .discountPercent(7)
                .stock(70)
                .description("Điện thoại Oppo A96 thiết kế đẹp")
                .imageUrl("oppoa96.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Xiaomi Poco X5 Pro")
                .brand("Xiaomi")
                .price(new BigDecimal("8990000"))
                .discountPercent(10)
                .stock(60)
                .description("Điện thoại Xiaomi Poco X5 Pro hiệu năng tốt")
                .imageUrl("pocox5pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Realme 9i 5G")
                .brand("Realme")
                .price(new BigDecimal("4290000"))
                .discountPercent(5)
                .stock(100)
                .description("Điện thoại Realme 9i 5G pin tốt")
                .imageUrl("realme9i5g.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Motorola Moto G31")
                .brand("Motorola")
                .price(new BigDecimal("3790000"))
                .discountPercent(4)
                .stock(90)
                .description("Điện thoại Motorola Moto G31 giá tốt")
                .imageUrl("motorolamotoG31.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Samsung Galaxy M14")
                .brand("Samsung")
                .price(new BigDecimal("3990000"))
                .discountPercent(6)
                .stock(85)
                .description("Điện thoại Samsung Galaxy M14 pin khỏe")
                .imageUrl("galaxym14.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Realme C35")
                .brand("Realme")
                .price(new BigDecimal("3690000"))
                .discountPercent(5)
                .stock(75)
                .description("Điện thoại Realme C35 pin khủng, thiết kế đẹp")
                .imageUrl("realmec35.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Infinix Hot 20")
                .brand("Infinix")
                .price(new BigDecimal("3490000"))
                .discountPercent(4)
                .stock(80)
                .description("Điện thoại Infinix Hot 20 màn hình lớn")
                .imageUrl("infinixhot20.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Samsung Galaxy A14")
                .brand("Samsung")
                .price(new BigDecimal("3990000"))
                .discountPercent(6)
                .stock(90)
                .description("Điện thoại Samsung Galaxy A14 giá rẻ, pin khỏe")
                .imageUrl("galaxya14.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("iPhone 13")
                .brand("Apple")
                .price(new BigDecimal("21990000"))
                .discountPercent(8)
                .stock(55)
                .description("Điện thoại Apple iPhone 13 vẫn mạnh mẽ")
                .imageUrl("iphone13.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Xiaomi Redmi Note 11")
                .brand("Xiaomi")
                .price(new BigDecimal("4290000"))
                .discountPercent(5)
                .stock(100)
                .description("Điện thoại Xiaomi Redmi Note 11 cấu hình tốt")
                .imageUrl("redminote11.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Realme 11x")
                .brand("Realme")
                .price(new BigDecimal("4990000"))
                .discountPercent(7)
                .stock(70)
                .description("Điện thoại Realme 11x cấu hình ổn định")
                .imageUrl("realme11x.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Samsung Galaxy S22 Ultra")
                .brand("Samsung")
                .price(new BigDecimal("26990000"))
                .discountPercent(10)
                .stock(40)
                .description("Điện thoại Samsung Galaxy S22 Ultra cao cấp")
                .imageUrl("galaxys22ultra.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Vivo V27 Pro")
                .brand("Vivo")
                .price(new BigDecimal("10990000"))
                .discountPercent(9)
                .stock(50)
                .description("Điện thoại Vivo V27 Pro camera đẹp")
                .imageUrl("vivov27pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Oppo Find X6 Pro")
                .brand("Oppo")
                .price(new BigDecimal("21990000"))
                .discountPercent(12)
                .stock(30)
                .description("Điện thoại Oppo Find X6 Pro flagship")
                .imageUrl("oppofindx6pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Motorola Edge 40 Pro")
                .brand("Motorola")
                .price(new BigDecimal("15990000"))
                .discountPercent(8)
                .stock(20)
                .description("Điện thoại Motorola Edge 40 Pro mạnh mẽ")
                .imageUrl("motorolaedge40pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Nokia X30 5G")
                .brand("Nokia")
                .price(new BigDecimal("7590000"))
                .discountPercent(6)
                .stock(45)
                .description("Điện thoại Nokia X30 5G thân thiện môi trường")
                .imageUrl("nokiax305g.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Asus Zenfone 10")
                .brand("Asus")
                .price(new BigDecimal("15990000"))
                .discountPercent(10)
                .stock(35)
                .description("Điện thoại Asus Zenfone 10 nhỏ gọn, mạnh mẽ")
                .imageUrl("zenfone10.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Realme GT 2 Pro")
                .brand("Realme")
                .price(new BigDecimal("14990000"))
                .discountPercent(11)
                .stock(40)
                .description("Điện thoại Realme GT 2 Pro hiệu năng cao")
                .imageUrl("realmegt2pro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Samsung Galaxy S21 FE")
                .brand("Samsung")
                .price(new BigDecimal("14990000"))
                .discountPercent(9)
                .stock(50)
                .description("Điện thoại Samsung Galaxy S21 FE phổ thông")
                .imageUrl("galaxys21fe.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        products.add(Product.builder()
                .name("Xiaomi Mi 12T Pro")
                .brand("Xiaomi")
                .price(new BigDecimal("13990000"))
                .discountPercent(10)
                .stock(40)
                .description("Điện thoại Xiaomi Mi 12T Pro hiệu năng cao")
                .imageUrl("mi12tpro.jpg")
                .createdAt(LocalDateTime.now())
                .build());

        return products;
    }

}
