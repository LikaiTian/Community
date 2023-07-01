package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

	@PostConstruct
	//该注解标注的方法构造器调用完以后被执行
	public void init(){
		//下面代码用来解决netty启动冲突问题(redis和es底层都使用了netty)
		//see Netty4Utils找到问题
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}


	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
