package com.zxf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxf.mapper.CommentMapper;
import com.zxf.mapper.CommentVoMapper;
import com.zxf.pojo.Author;
import com.zxf.pojo.Comment;
import com.zxf.service.AuthorService;
import com.zxf.service.CommentsService;
import com.zxf.utils.AuthorThreadLocal;
import com.zxf.vo.CommentVo;
import com.zxf.vo.Result;
import com.zxf.vo.params.CommentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentVoMapper commentVoMapper;

    @Autowired
    private AuthorService authorService;

    @Override
    public Result comments(Long id) {
        //实际上就是需要通过文章id获取Comment，单表查询，MP可以胜任
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", id).eq("level",1);
        List<Comment> comments = commentMapper.selectList(wrapper);

        /*
        暂时还没想好怎么完成评论的评论功能
        怎么样实现多层的评论
         */

        //这里实现一下只有双层评论的情况
        for (Comment comment1 : comments) {
            //Integer level1 = comment1.getLevel();
            //if (level1 == 1) {
                Long id1 = comment1.getId();
                //装载Author需要AuthorService
                Author author1 = authorService.findAuthorByCommentId(id1);
                comment1.setAuthor(author1);
                //装载List<Comment>
                QueryWrapper<Comment> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("parent_id", id1);
                List<Comment> commentsChildren1 = commentMapper.selectList(wrapper1);

                if(commentsChildren1 != null){
                    for (Comment comment2 : commentsChildren1) {
                        Integer level2 = comment2.getLevel();
                        if (level2 == 2) {
                            Long id2 = comment2.getId();
                            //装载Author需要AuthorService
                            Author author2 = authorService.findAuthorByCommentId(id2);
                            comment2.setAuthor(author2);
                            //装载List<Comment>
                            //这里第二层不会再有子评论，所以直接装载null
                            comment2.setComments(null);
                        }
                    }
                    comment1.setComments(commentsChildren1);
                }else{
                    comment1.setComments(null);
                }
            //}
        }
        return Result.success(comments);
    }

    @Override
    public List<Comment> findCommentsByArticleId(Long id) {
        Result comments = comments(id);
        Object obj =comments.getData();
        List<Comment> list = null;
        if(obj instanceof List){
            list = (List) obj;
        }
        //System.out.println("list =" + list);
        return list;
    }

    @Override
    @Transactional
    public Result createComment(CommentParams commentParams) {
        Long articleId = commentParams.getArticleId();
        String content = commentParams.getContent();
        Long parentId = commentParams.getParentId();

        CommentVo commentVo = new CommentVo();
        commentVo.setContent(content);
        commentVo.setCreateDate(new Date().getTime());
        commentVo.setArticleId(articleId);
        commentVo.setParentId(parentId);
        //需要登录的资源，肯定要经过登录拦截器，所以AuthorThreadLocal中肯定存放着author
        Author author = AuthorThreadLocal.get();
        System.out.println(author);
        commentVo.setAuthorId(author.getId());

        if(parentId == 0){
            commentVo.setLevel(1);
        }else{
            commentVo.setLevel(2);
        }
        commentVoMapper.insert(commentVo);

        return Result.success(commentVo);
    }
}
