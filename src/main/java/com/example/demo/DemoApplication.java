package com.example.demo;

import com.example.demo.data.SampleData;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

//	@Component
//	public class DataLoader implements CommandLineRunner {
//
//		private final ProductRepository productRepository;
//
//		public DataLoader(ProductRepository productRepository) {
//			this.productRepository = productRepository;
//		}
//
//		@Override
//		public void run(String... args) throws Exception {
//			if (productRepository.count() == 0) { // tránh thêm trùng lặp
//				List<Product> products = SampleData.getSampleProducts();
//				productRepository.saveAll(products);
//				System.out.println("Đã thêm dữ liệu sản phẩm mẫu vào database.");
//			} else {
//				System.out.println("Dữ liệu đã tồn tại. Không thêm lại.");
//			}
//		}
//	}
}
