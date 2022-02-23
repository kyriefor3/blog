package com.zxf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxf.mapper.ArticleMapper;
import com.zxf.mapper.ArticleTagVoMapper;
import com.zxf.mapper.ArticleVoMapper;
import com.zxf.pojo.*;
import com.zxf.service.*;
import com.zxf.utils.AuthorThreadLocal;
import com.zxf.vo.ArticleTagVo;
import com.zxf.vo.ArticleVo;
import com.zxf.vo.Result;
import com.zxf.vo.params.ArticleBody;
import com.zxf.vo.params.ArticleParams;
import com.zxf.vo.params.PageParams;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private ThreadPoolService threadPoolService;

    @Autowired
    private ArticleVoMapper articleVoMapper;

    @Autowired
    private ArticleTagVoMapper articleTagVoMapper;

    @Override
    public Result listArticle(PageParams params) {

        Integer page = params.getPage();
        Integer pageSize = params.getPageSize();
        Page<Article> Ipage = new Page<>(page,pageSize);

        //根据是否置顶和创建时间进行倒序
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("weight")
                .orderByDesc("create_date");
        Page<Article> articlePage = articleMapper.selectPage(Ipage, wrapper);

        List<Article> records = articlePage.getRecords();

        for (Article article:records) {
            Long id = article.getId();
            List<Tag> tags = tagService.findTagsByArticleId(id);
            Category category = categoryService.findCategoryByArticleId(id);
            Author author = authorService.findAuthorByArticleId(id);
            article.setTags(tags);
            article.setCategory(category);
            article.setAuthor(author);
        }

        return Result.success(records);
    }

    @Override
    @Transactional
    public Result findArticleById(Long id) {
        //根据文章id查找article
        Article article = articleMapper.selectById(id);
        //根据文章id查找author
        Author author = authorService.findAuthorByArticleId(id);
        article.setAuthor(author);
        //根据文章id查找tags
        List<Tag> tags = tagService.findTagsByArticleId(id);
        article.setTags(tags);
        //根据文章id查找category
        Category category = categoryService.findCategoryByArticleId(id);
        article.setCategory(category);
        //根据文章id查找comments
        List<Comment> comments = commentsService.findCommentsByArticleId(id);
        article.setComments(comments);

        //文章查看之后，文章的阅读数应该增加1
        //涉及到对数据库修改，该在方法上或者类上开启事务@Traditional

        /*Article articleNew = new Article();
        articleNew.setViewCounts(article.getViewCounts()+1);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id).eq("view_counts",article.getViewCounts());
        articleMapper.update(articleNew, wrapper);*/

        threadPoolService.updateArticleViewCount(articleMapper,article);

        return Result.success(article);
    }

    @Override
    @Transactional//涉及到表的新增操作，需要开启事务
    public Result publishArticle(ArticleParams articleParams) {
        //因为增加新的文章记录，会导致某些和article有关的表增加数据
        //但是因为之间设计的pojo类，涉及的id字段都用对应的类来表示了
        //这里为了不影响之前的代码，重写一个ArticleVo，用来和article表一一对应
        ArticleBody body = articleParams.getBody();

        String content = body.getContent();
        String contentHtml = body.getContentHtml();

        String summary = articleParams.getSummary();
        String title = articleParams.getTitle();
        Category category = articleParams.getCategory();
        List<Tag> tags = articleParams.getTags();


        //装载文章表
        ArticleVo articleVo = new ArticleVo();
        articleVo.setCommentCounts(0);
        articleVo.setCreateDate(new Date().getTime());
        articleVo.setSummary(summary);
        articleVo.setContent(content);
        articleVo.setContentHtml(contentHtml);
        articleVo.setTitle(title);
        articleVo.setViewCounts(0);
        articleVo.setWeight(0);

        Author author = AuthorThreadLocal.get();
        articleVo.setAuthorId(author.getId());
        articleVo.setCategoryId(category.getId());

        articleVoMapper.insert(articleVo);

        //对于article---tag来说，增加article，会对article，以及article_tag表产生影响
        ArticleTagVo articleTagVo = new ArticleTagVo();
        for (Tag tag:tags){
            Integer tagId = tag.getId();

            articleTagVo.setId(null);
            //注意：每一次插入成功之后，MP会默认将生成的id进行回写，
            // 所以下一次插入会导致id重复。所以需要将id置为null

            articleTagVo.setArticleId(articleVo.getId());
            articleTagVo.setTagId(tagId);
            articleTagVoMapper.insert(articleTagVo);
        }

        //返回生成的新增的文章id
        return Result.success(articleVo.getId());
    }
}

