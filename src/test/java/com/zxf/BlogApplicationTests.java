package com.zxf;


import com.zxf.mapper.ArticleMapper;
import com.zxf.mapper.ArticleTagVoMapper;
import com.zxf.pojo.Article;
import com.zxf.service.ArticleService;
import com.zxf.service.CommentsService;
import com.zxf.utils.Pojo2VoUtils;
import com.zxf.vo.ArticleTagVo;
import com.zxf.vo.ArticleVo;
import com.zxf.vo.Result;
import com.zxf.vo.params.PageParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@SpringBootTest

class BlogApplicationTests {

	@Autowired
	private ArticleMapper articleMapper;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private CommentsService commentsService;

	@Autowired
	private ArticleTagVoMapper articleTagVoMapper;
	@Test
	void contextLoads() {
	}

	@Test
	void testArticleService(){

		articleService.listArticle(new PageParams(1, 2));
	}

	@Test
	void generateDateLong(){
		long time = new Date().getTime();
		System.out.println(time);
	}

	@Test
	void testCommentService(){
		Result comments = commentsService.comments(1L);
	}

	@Test
	@Transactional
	void testArticleTagVoMapper(){
		ArticleTagVo articleTagVo = new ArticleTagVo();
		articleTagVo.setArticleId(1L);
		for(Integer i=1;i<=2;i++){
			articleTagVo.setId(null);
			articleTagVo.setTagId(i);
		}

//		int i = 1/0;
	}

	@Test
	void testPojo2Vo() throws InstantiationException, IllegalAccessException {
		ArticleVo articleVo = new ArticleVo();
		articleVo.setId(1L);
		articleVo.setWeight(2);

		Article article = Pojo2VoUtils.pojo2PojoVo(articleVo, Article.class);

		System.out.println("article = " + article);
	}
}
