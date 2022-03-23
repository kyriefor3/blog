### 表结构

~~~sql
CREATE TABLE `ms_article` (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `comment_counts` int(0) NULL DEFAULT NULL COMMENT '评论数量',
  `create_date` bigint(0) NULL DEFAULT NULL COMMENT '创建时间',
  `summary` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '简介',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `content_html` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `view_counts` int(0) NULL DEFAULT NULL COMMENT '浏览数量',
  `weight` int(0) NOT NULL COMMENT '是否置顶',
  `author_id` bigint(0) NULL DEFAULT NULL COMMENT '作者id',
  `category_id` int(0) NULL DEFAULT NULL COMMENT '类别id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


CREATE TABLE `ms_category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `ms_tag`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `tag_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


CREATE TABLE `ms_article_tag`  (
  `id` BIGINT(0) NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT(0) NOT NULL,
  `tag_id` BIGINT(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  INDEX `tag_id`(`tag_id`) USING BTREE
) ENGINE = INNODB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

CREATE TABLE `ms_author`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `create_date` BIGINT(0) NULL DEFAULT NULL COMMENT '注册时间',
  `account` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号',
  `password` VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `admin` bit(1) NULL DEFAULT NULL COMMENT '是否管理员',
  `deleted` bit(1) NULL DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

CREATE TABLE `ms_comment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_date` bigint(0) NOT NULL,
  `article_id` int(0) NOT NULL,
  `author_id` bigint(0) NOT NULL,
  `parent_id` bigint(0) NOT NULL,

  `level` int(0) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
~~~





统一标准：返回数据的格式

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":
}
~~~

~~~java
package com.zxf.vo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private boolean success;

    private Integer code;

    private String msg;

    private Object data;


    public static Result success(Object data) {
        return new Result(true,200,"success",data);
    }
    public static Result fail(Integer code, String msg) {
        return new Result(false,code,msg,null);
    }
}
~~~







### 1.注册

#### 1.注册

接口url：/register

请求方式：POST

请求参数：

（1）参数名

| 参数名   | 参数类型 | 说明 |
| -------- | -------- | ---- |
| account  | string   | 账号 |
| password | string   | 密码 |
| nickname | string   | 昵称 |

（2）参数位置

以JSON数据格式，封装在请求体中

服务端返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":"token"
}
~~~



实现逻辑：

客户端提交数据

服务端检查用户是否存在，如果存在，返回错误信息。

如果不存在，将用户信息存储在Mysql数据库中，根据id生成token，存在redis中，并返回给客户端。



（1）controller---RegisterController

~~~java
package com.zxf.controller;

import com.zxf.service.RegisterService;
import com.zxf.vo.Result;
import com.zxf.vo.params.RegisterParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping
    public Result register(@RequestBody RegisterParams registerParams){
        Result result = registerService.register(registerParams);
        return result;
    }
}
~~~



（2）service---RegisterService--->依赖AuthorService(完成查询Author的功能)

~~~java
//===================
//		service
//===================
package com.zxf.service;

public interface RegisterService {
    Result register(RegisterParams registerParams);
}

public interface AuthorService {
    /**
     * 根据account，查询author
     */
    Author findAuthorByAccount(String account);

    /**
     * 保存用户
     */
    void save(Author author);
}

//==========================
//		serviceimpl
//==========================
package com.zxf.service.impl;

@Service
@Transactional//注册功能涉及到对数据库进行添加操作，所以需要开启事务
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 注册功能
     * 根据account查询用户，是否存在
     *      存在，返回错误信息
     *      不存在，
     *          1.将用户信息保存在mysql
     *          2.用id生成token，返回客户端，并保存在redis中
     *                                              key:TOKEN_xxx
     *                                              value:JSON形式的author信息
     */
    @Override
    public Result register(RegisterParams registerParams) {
        String account = registerParams.getAccount();
        String password = registerParams.getPassword();
        String nickname = registerParams.getNickname();

        Author author = authorService.findAuthorByAccount(account);

        //用户存在，返回错误信息
        if(author!=null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }

        //用户不存在
        // 保存用户数据，其中密码要经过加密
        String salt = "zxf2kyrie!@#zzz";//加密盐
        Author authorNew = new Author();
        authorNew.setAccount(account);
        authorNew.setPassword(DigestUtils.md5Hex(password+salt));
        authorNew.setNickname(nickname);
        authorService.save(authorNew);//1.mp生成id策略：雪花算法 2.mp插入操作后会自动进行id回写

        // 生成token，并存在redis中
        String token = JWTUtils.createToken(authorNew.getId());
        redisTemplate.opsForValue()
                .set("TOKEN_"+token, JSON.toJSONString(authorNew),100, TimeUnit.DAYS);//设置过期时间100天
        return Result.success(token);
    }
}



package com.zxf.service.impl;

@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private AuthorMapper authorMapper;

    @Override
    public Author findAuthorByAccount(String account) {
        QueryWrapper<Author> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        Author author = authorMapper.selectOne(wrapper);
        return author;

    }

    @Override
    public void save(Author author) {
        authorMapper.insert(author);
    }
}

~~~



（3）mapper---AuthorMapper

~~~java
public interface AuthorMapper extends BaseMapper<Author> {
}
~~~



（4）pojo

~~~java
@Data
public class Author {
    private Long id;
    private Long createDate;
    private String account;
    private String password;
    private String nickname;
    private String avatar;
    private Integer admin;
    private Integer deleted;
}
~~~



（5）utils---JWTUtils（用来完成token的生成和解析）

~~~java
package com.zxf.utils;

public class JWTUtils {
    private static final String jwtToken = "123456Zxf666!@#$$";

    /**
     * 这里只由用户的id生成token
     * 当然也可以使用多个数据
     * @param userId
     * @return
     */
    public static String createToken(Long userId){
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",userId);
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, jwtToken) // 签发算法，秘钥为jwtToken
                .setClaims(claims) // body数据，要唯一，自行设置
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));// 一天的有效时间
        String token = jwtBuilder.compact();
        return token;
    }

    public static Map<String, Object> checkToken(String token){
        try {
            Jwt parse = Jwts.parser().setSigningKey(jwtToken).parse(token);
            return (Map<String, Object>) parse.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}

~~~



#### 2.获取用户信息

需求：注册成功之后，客户端会立刻带着token访问服务端，要求获取该用户的数据，进行登录，包括要展示用户的nickname，password，...



接口url：authors/currentAuthor

请求方式：GET

请求参数：

（1）参数名

| 参数名        | 参数类型 | 说明  |
| ------------- | -------- | ----- |
| authorization | string   | token |

（2）参数位置

请求头

服务端返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":{"id":1,"account":"zxf","nickname":"duoshuaio"}
}
~~~



实现逻辑：解析token，获得Author的id；去数据库中查找，返回Author对象；最后返回。

TIP：token解析的工作已经交给登录拦截器LoginInterceptor做了，可以去看2.2节。



（0）现在配置中，增加拦截器的拦截路径，也就是这个请求的url：（/users/currentUser）

~~~java
package com.zxf.config;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test")
                .addPathPatterns("/authors/currentAuthor");//添加拦截路径
    }
}

~~~



（1）controller

~~~java
package com.zxf.controller;

@RestController
@RequestMapping("authors")
public class AuthorController {

    @GetMapping("currentAuthor")
    public Result currentAuthor(@RequestHeader String authorization){

        //登录拦截器已经做了token解析的工作，并且把对象放在了ThreadLocal当中
        Author author = AuthorThreadLocal.get();

        //因为最后AuthorThreadLocal中的Author对象会被删除，所以这里进行复制
        Author author1 = new Author();
        BeanUtils.copyProperties(author,author1);
        return Result.success(author1);
    }
}

~~~





### 2.登录

#### 1.登录

接口url：/login

请求方式：POST

请求参数：

（1）参数名

| 参数名   | 参数类型 | 说明 |
| -------- | -------- | ---- |
| account  | string   | 账号 |
| password | string   | 密码 |

（2）参数位置

以JSON数据格式，封装在请求体中

服务端返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":"token"
}
~~~



实现逻辑：

客户端提交数据

服务端：根据account检查该用户是否存在

不存在，直接返回错误信息

存在，则根据id生成token返回客户端，并将token存储在redis中。

（1）controller

~~~java
@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result login(@RequestBody LoginParams loginParams){
        Result result = loginService.login(loginParams);
        return result;
    }

}
~~~

（2）service

~~~java
//===================
//		service
//===================
public interface LoginService {
    Result login(LoginParams loginParams);
}

//===================
//		serviceimpl
//===================
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result login(LoginParams loginParams) {
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        String salt = "zxf2kyrie!@#zzz";//加密盐
        String password_actual = DigestUtils.md5Hex(password + salt);

        Author author = authorService.findAuthor(account,password_actual);

        //用户不存在，返回错误信息
        if (author==null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }

        //用户存在，根据id生成token，返回客户端，并保存在redis中
        String token = JWTUtils.createToken(author.getId());
        redisTemplate.opsForValue()
                .set("TOKEN_"+token, JSON.toJSONString(author),100, TimeUnit.DAYS);
        return Result.success(token);
    }
}
~~~



##### 1.登录拦截器

需求：

有一些资源，必须要是登录状态下，才能进行访问。（例如评论，点赞，...等）

客户端发起对这些资源的访问请求，必须携带token。服务端在接收到这些请求之后，都需要进行token的验证，验证成功之后才能去访问相应的资源。

实现：拦截器。

开发步骤：

（1）编写一个拦截器

~~~java
package com.zxf.handler;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 登录拦截器
     * 对需要登录才能访问的资源进行拦截，检查token的合法性
     * @param request 请求对象
     * @param response 响应对象
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String token = request.getHeader("Authorization");

        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        //（1）检查token是否为空
        if(StringUtils.isBlank(token)){
            returnError(response,ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
            return false;
        }


        //（2）根据token合法性
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if(stringObjectMap == null){
            returnError(response,ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
            return false;
        }

        String authorJson =  redisTemplate.opsForValue().get("TOKEN_"+token);
        if (StringUtils.isBlank(authorJson)) {
            returnError(response,ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
            return false;
        }

        //解析出author对象
        Author author = JSON.parseObject(authorJson, Author.class);
        if (author == null){
            returnError(response,ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
            return false;
        }

        return true;
    }

    /**
     * 返回错误信息
     */
    public void returnError(HttpServletResponse response,int code,String msg) throws IOException {
        Result result = Result.fail(code,msg);
        //响应
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JSON.toJSONString(result));
    }
}

~~~



（2）编写配置类

~~~java
package com.zxf.config;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)//添加自定义拦截器
                .addPathPatterns("/test");//添加拦截路径
    }
}

~~~



##### 2.ThreadLocal

需求：在登录拦截器中已经把Author对象给解析出来了，怎么把对象带给后面的Controller

实现：ThreadLocal，可以实现在一个线程之间共享对象。

开发步骤：

（1）编写一个AuthorThreadLocal类

~~~java
package com.zxf.utils;

public class AuthorThreadLocal {

    private AuthorThreadLocal(){}
    //线程变量隔离
    private static final ThreadLocal<Author> LOCAL = new ThreadLocal<>();

    public static void put(Author author){
        LOCAL.set(author);
    }
    public static Author get(){
        return LOCAL.get();
    }
    public static void remove(){
        LOCAL.remove();
    }
}

~~~



（2）在登录拦截器中，将解析出来的Author对象放进ThreadLocal中。

TIP：拦截器结束后，一定要将该对象删除，否则有造成内存泄露的风险。

~~~java
package com.zxf.handler;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //......

        //解析出author对象
        Author author = JSON.parseObject(authorJson, Author.class);
        if (author == null){
            returnError(response,ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
            return false;
        }


        //把author对象放进ThreadLocal
        AuthorThreadLocal.put(author);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //一定要在结束后删除，否则会有内存泄露风险
        AuthorThreadLocal.remove();
    }

}

~~~



#### 2.退出登录

接口url：/logout

请求方式：GET

请求参数：

（1）参数名

| 参数名称      | 参数类型 | 说明            |
| ------------- | -------- | --------------- |
| Authorization | string   | 头部信息(TOKEN) |

（2）参数位置

请求头

返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":null
}
~~~



实现逻辑：

退出登录，本质上就是就是将redis中的token清除。



（1）controller

```java
package com.zxf.controller;

import com.zxf.service.LogoutService;
import com.zxf.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logout")
public class LogoutController {

    @Autowired
    private LogoutService logoutService;

    @GetMapping
    public Result logout(@RequestHeader String authorization){

        Result result = logoutService.logout(authorization);

        return result;
    }
}
```



（2）service

~~~java
//===================
//		service
//===================
public interface LogoutService {

    Result logout(String authorization);
}

//===================
//		serviceimpl
//===================
@Service
public class LogoutServiceImpl implements LogoutService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Result logout(String authorization) {
        System.out.println(redisTemplate.delete(authorization));
        return (redisTemplate.delete("TOKEN_" + authorization))?
                Result.success(null):Result.fail(ErrorCode.LOGOUT_FAIL.getCode(),ErrorCode.LOGOUT_FAIL.getMsg());
    }
}
~~~



### 3.首页-展示

#### 1.文章列表

url：/articles

请求方式：POST

请求参数：

（1）参数名

| 参数名称 | 参数类型 | 说明           |
| -------- | -------- | -------------- |
| page     | int      | 当前页数       |
| pageSize | int      | 每页显示的数量 |

（2）参数位置

请求体



返回数据：

~~~java
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": [
        {
            "id": 1,
            "title": "xxx",
            "summary": "xxxxxx。",
            "commentCounts": 2,
            "viewCounts": 54,
            "weight": 1,
            "createDate": "2609-06-26",
            "author": {"id":1,"nickname":"duoshuaio"},
            "tags": [
                {
                    "id": 1,
                    "avatar": null,
                    "tagName": "spring"
                },
                {
                    "id": 2,
                    "avatar": null,
                    "tagName": "java"
                }
            ],
            "categorys": {"id":1,"categoryName":"教程"}
        },
        
        {
            "id":2
            "title": "xxx",
            ...
        }
    ]
}
~~~





实现逻辑：

本质上就是对文章表进行分页查询，但是因为也会涉及文章的标签、分类、作者等信息，所以需要查询多张表。

![image-20220218181107215](C:\Users\kyrie\AppData\Roaming\Typora\typora-user-images\image-20220218181107215.png)

~~~java
public class Article {
    private Long id;
    private Integer commentCounts;
    private Long createDate;
    private String summary;
    private String content;
    private String contentHtml;
    private String title;
    private Integer viewCounts;
    private Integer weight;//以上部分，可以直接通过ms_article表 查
    
    private AuthorVo authorVo;//需要联合author表
    private CategoryVo categoryVo;//联合category表
    private List<Tag> tags;//联合tag表
}

~~~

![image-20220218182654623](C:\Users\kyrie\AppData\Roaming\Typora\typora-user-images\image-20220218182654623.png)

~~~java
public class Author {
    private Long id;
    private Long createDate;
    private String account;
    private String password;
    private String nickname;
    private String avatar;
    private Integer admin;
    private Integer deleted;
}
~~~

![image-20220218182737998](C:\Users\kyrie\AppData\Roaming\Typora\typora-user-images\image-20220218182737998.png)

~~~java
public class Category {
    private Integer id;
    private String avatar;
    private String categoryName;
    private String description;
}
~~~

![image-20220218182812919](C:\Users\kyrie\AppData\Roaming\Typora\typora-user-images\image-20220218182812919.png)

~~~java
public class Tag {
    private Integer id;
    private String avatar;
    private String tagName;
    private String description;
}
~~~



文章表<<<--->分类表

所以：根据文章查找分类，只需要在文章表和分类表进行联查即可

文章表<<<--->>>标签表

所以：根据文章查找标签，需要中间一张表加入战斗

![image-20220218193850810](C:\Users\kyrie\AppData\Roaming\Typora\typora-user-images\image-20220218193850810.png)

文章表<<<--->作者表

所以：根据文章查找作者，只需要在文章表和作者表进行联查即可





（1）controller

~~~java
package com.zxf.controller;

@RestController
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    public Result listArticle(@RequestBody PageParams params){
        return articleService.listArticle(params);
    }
}

~~~



（2）service

ArticleService --依赖--> AuthorService --依赖--> AuthorMapper

​						--依赖--> CategoryService --依赖--> CategoryMapper

​						--依赖--> TagService --依赖--> TagMapper

~~~java
//============================
//	service
//============================
package com.zxf.service;

public interface ArticleService {
    Result listArticle(PageParams params);
}


public interface AuthorService {
    /**
     * 根据文章id查找author
     */
    Author findAuthorByArticleId(Long articleId);
}


public interface CategoryService {
    /**
     * 根据文章id查找category
     */
    Category findCategoryByArticleId(Long articleId);
}

public interface TagService {

    /**
     *根据文章id查询Tag
     */
    List<Tag> findTagsByArticleId(Long articleId);
}

//============================
//	serviceimpl
//============================
package com.zxf.service.impl;

//1.ArticleServiceImpl
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
}

//2.AuthorServiceImpl
@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private AuthorMapper authorMapper;

    @Override
    public Author findAuthorByArticleId(Long articleId) {
        Author author = authorMapper.findAuthorByArticleId(articleId);
        return author;
    }
}

//3.CategoryServiceImpl
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public Category findCategoryByArticleId(Long articleId) {
        Category category = categoryMapper.findCategoryByArticleId(articleId);
        return category;
    }
}

//4.TagServiceImpl
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> findTagsByArticleId(Long articleId) {
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);
        return tags;
    }
}

~~~



（3）interface mapper

~~~java
package com.zxf.mapper;
//1.ArticleMapper
public interface ArticleMapper extends BaseMapper<Article> {
}

//2.AuthorMapper
public interface AuthorMapper extends BaseMapper<Author> {
    /**
     * 根据文章id查找author具体信息
     * TIP：此操作涉及到两张表的联查，所以需要使用mapper.xml文件
     */
    Author findAuthorByArticleId(Long articleId);
}

//3.CategoryMapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据文章id查询category的具体信息
     * TIP：此操作涉及到两张表的联查，所以需要使用mapper.xml文件
     */
    Category findCategoryByArticleId(Long articleId);

}

//4.TagMapper
public interface TagMapper extends BaseMapper<Tag> {
    /**
     *根据文章id查询Tag的具体对象
     * TIP：此操作涉及到两张表的联查，所以需要使用mapper.xml文件
     */
    List<Tag> findTagsByArticleId(Long id);
}

~~~

mapper.xml

~~~xml
<!------------------------------------>
<!--	AuthorMapper.xml		-->
<!------------------------------------>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.zxf.mapper.AuthorMapper">

    <select id="findAuthorByArticleId" parameterType="Long" resultType="com.zxf.pojo.Author">
        select
            id,create_date,account,password,nickname,avatar,admin,deleted
        from
            `ms_author`
        where
                id =
                (
                    select author_id
                    from `ms_article`
                    where id = #{articleId}
                )
    </select>
</mapper>


<!------------------------------------>
<!--	CategoryMapper.xml		-->
<!------------------------------------>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.zxf.mapper.CategoryMapper">

    <select id="findCategoryByArticleId" parameterType="Long" resultType="com.zxf.pojo.Category">
        select
            id,avatar,category_name,description
        from
            `ms_category`
        where
                id =
                (
                select category_id
                from `ms_article`
                where id = #{articleId}
                )
    </select>
</mapper>


<!------------------------------------>
<!--	TagMapper.xml			-->
<!------------------------------------>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zxf.mapper.TagMapper">

    <select id="findTagsByArticleId" parameterType="Long" resultType="com.zxf.pojo.Tag">
        select
               *
        from
            `ms_tag`
        where
              id in(
            select
                `tag_id`
            from
                `ms_article_tag`
            where article_id = #{articleId}
            );
    </select>
</mapper>
~~~

（4）pojo

（5）配置MP分页插件

~~~java
package com.zxf.config;

@Configuration
public class AppConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
~~~



#### 2.最热标签

url：/tags/hot

请求方式：GET

请求参数：无

服务端返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":{"id":1,"tagName":"spring"}
}
~~~



实现逻辑：最热标签，实际上就是被使用次数最多的标签，需要知道每个标签被使用过多少次，也就是对标签的引用，分组进行计数。

sql：

~~~sql
select 
	* 
from 
	ms_tag
where 
	id in
	(
		select tag_id
		from ms_article_tag
		group by tag_id
		order by count(*) Desc
	)
limit 2
~~~



（1）controller

~~~java
package com.zxf.controller;

@RestController
@RequestMapping("tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("hot")
    public Result hotTags(){
        int limit = 2;
        Result res = tagService.hotTags(limit);
        return res;
    }

}

~~~



（2）service

~~~java
//============================
//	service
//============================
package com.zxf.service;

public interface TagService {

    /**
     * 最热标签
     */
    Result hotTags(Integer limit);
}


//============================
//	serviceimpl
//============================
package com.zxf.service.impl;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public Result hotTags(Integer limit) {
        List<Tag> tags = tagMapper.findHotTags(limit);
        return Result.success(tags);
    }
}


~~~



（3）mapper

~~~java
package com.zxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxf.pojo.Tag;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 最热标签
     * TIP：此操作涉及到两张表的联查，所以需要使用mapper.xml文件
     */
    List<Tag> findHotTags(Integer limit);
}

~~~

~~~xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.zxf.mapper.TagMapper">

    <select id="findHotTags" parameterType="Integer" resultType="com.zxf.pojo.Tag">
        select
            id,avatar,tag_name
        from
            ms_tag
        where
                id in
                (
                    select tag_id
                    from ms_article_tag
                    group by tag_id
                    order by count(*) Desc
                )
            limit #{limit}
    </select>
</mapper>
~~~





#### 3.最热文章

本质：按照文章阅读量进行倒叙排序

#### 4.最新文章

本质：按照文章创建时间进行倒叙排序



### 4.导航-标签

#### 1.所有标签

url：/tags/detail

请求方式：GET

请求参数：无

服务端返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":[
        {"id":1,"tagName":"java"},
        {"id":2,"tagName":"spring"}
        ]
}
~~~

实现逻辑：本质上就是查询所有标签。



#### 2.某标签下所有文章

url：/category/detail/{id}

请求方式：GET

请求参数：

（1）参数名：id

（2）参数位置：路径中

服务端返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":[
        {文章对象1},
        {文章对象2},
        ...
        ]
}
~~~

实现逻辑：根据标签id，找到对应的所有文章id，然后全部查找出来就行了。



### 5.导航-分类

#### 1.所有分类

url：/categorys

请求方式：GET

请求参数：无

服务端返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":[
        {"id":1,"categoryName":"歌曲"},
        {"id":2,"categoryName":"技术教程"}
}
~~~

实现逻辑：本质上就是查询所有分类。



#### 2.某分类下所有文章

url：/categorys/detail/{id}

请求方式：GET

请求参数：

（1）参数名：id

（2）参数位置：路径中

服务端返回数据：

~~~java
{
    "success":true,
    "code":200,
    "msg":"信息",
    "data":[
        {文章对象1},
        {文章对象2},
        ...
        ]
}
~~~

实现逻辑：根据分类id，找到对应的所有文章id，然后全部查找出来就行了。



### 6.导航-文章归档

接口url：/articles

请求方式：POST

请求参数：

（1）参数名

| 参数名称 | 参数类型 | 说明 |
| -------- | -------- | ---- |
| year     | string   | 年   |
| month    | string   | 月   |

（2）参数位置

方法体

返回数据：

~~~java
{
    "success": true, 
    "code": 200, 
    "msg": "success", 
    "data": [
        {文章对象1},
        {文章对象2},
        ...
    		]
        
}

~~~



### xxx.统一异常处理

需求：当服务端代码发生异常时，客户端就是接收到不好的接收消息。

所以为了客户端的友好体验，需要进行异常处理。也就是当发生异常时，交给客户端一些友好的提示消息，例如"服务器正忙"。



实现：handler

~~~java
package com.mszlu.blog.handler;

import com.mszlu.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//对加了@controller注解的方法进行处理 Aop的实现
@ControllerAdvice
public class AllExceptionHandler {

    //进行异常处理，处理Exception.class的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody //返回json数据如果不加就返回页面了
    public Result doException(Exception ex) {
        //e.printStackTrace();是打印异常的堆栈信息，指明错误原因，
        // 其实当发生异常时，通常要处理异常，这是编程的好习惯，所以e.printStackTrace()可以方便你调试程序！
        ex.printStackTrace();
        return Result.fail(-999,"系统异常");

    }
}
~~~



### xxx.统一AOP日志

本质：就是为每一个Controller（或者其他的资源）在访问之前以及之后，加上一点日志信息。

实现原理：AOP

回顾：AOP的开发（1）原始方法（2）额外功能（3）切入点（4）组装切面

实现步骤：

（1）原始方法--->加上注解

（2）额外功能

~~~java
package com.zxf.common.aop;

@Component
@Aspect
@Slf4j//记录日志
public class LogAspect {
	//切入点：使用注解方式定义切入点
    @Pointcut("@annotation(com.zxf.common.aop.LogAnnotation)")
    public void pt(){}

    @Around("pt()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = joinPoint.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        recordLog(joinPoint, time);
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        log.info("=====================log start================================");
        log.info("module:{}",logAnnotation.module());
        log.info("operation:{}",logAnnotation.operation());

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}",className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        String params = JSON.toJSONString(args[0]);
        log.info("params:{}",params);

        //获取request 设置IP地址
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.info("ip:{}", IpUtils.getIpAddr(request));


        log.info("excute time : {} ms",time);
        log.info("=====================log end================================");
    }

}


//==============
//	注解方式AOP	
//==============
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    String module() default "";

    String operation() default "";
}
~~~

几个工具类

~~~java
//===============================
//	HttpContextUtils
//===============================
package com.zxf.utils;
/**
 * HttpServletRequest
 *
 */
public class HttpContextUtils {

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

}

//===============================
//	IpUtils
//===============================

package com.zxf.utils;

/**
 * 获取Ip
 *
 */
@Slf4j
public class IpUtils {

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null, unknown = "unknown", seperator = ",";
        int maxLength = 15;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || unknown.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.error("IpUtils ERROR ", e);
        }

        // 使用代理，则获取第一个IP地址
        if (StringUtils.isEmpty(ip) && ip.length() > maxLength) {
            int idx = ip.indexOf(seperator);
            if (idx > 0) {
                ip = ip.substring(0, idx);
            }
        }

        return ip;
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getIpAddr() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        return getIpAddr(request);
    }
}

~~~



### 7.文章详情页

文章详情页=文章具体内容+author+tag+category+comment

所以这里需要对Article类加上一个属性`List<Comment>`

~~~java
//===========================
//	Article
//===========================
@Data
public class Article {
    private Long id;
    private Integer commentCounts;
    private Long createDate;
    private String summary;
    private String content;
    private String contentHtml;
    private String title;
    private Integer viewCounts;
    private Integer weight;

    @TableField(select = false)
    private Author author;
    @TableField(select = false)
    private Category category;
    @TableField(select = false)
    private List<Tag> tags;
    @TableField(select = false)
    private List<Comment> comments;//增加comments
}

//===========================
//	Comment
//===========================
@Data
public class Comment {

    private Long id;

    private String content;

    private Long createDate;

    //注：对于评论来说，只可能通过文章去展示评论，所以这个对象实际上基本上就是null
    //这里为了表结构和对象一一对应还是先写上，便于理解
    @TableField(select = false)
    private Article article;
    @TableField(select = false)
    private Author author;
    @TableField(select = false)
    private List<Comment> comments;//子评论

    private Integer level;

}
~~~

![image-20220219164856635](C:\Users\kyrie\AppData\Roaming\Typora\typora-user-images\image-20220219164856635.png)



#### 1.文章下面的评论列表

url：/comments/article/{id}

请求方式：GET

请求参数：

（1）参数名

| 参数名称 | 参数类型 | 说明               |
| -------- | -------- | ------------------ |
| id       | long     | 文章id（路径参数） |

（2）参数位置

路径参数

返回数据：

~~~java
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": [
        {评论对象1},
        {评论对象2},
        ...
    ]
}


//评论对象的结构
{
    "id":1,
    "author":{作者对象},
    "content":"xxxxxx",
    "createDate":"2021-xx",
    "level":1,
    "toAuthor":{作者对象},
    "children":[
        {子评论对象1},
        {子评论对象2},
        ...
    ]    
}
~~~



（1）controller

~~~java
package com.zxf.controller;

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    @GetMapping("article/{id}")
    public Result comments(@PathVariable Integer id){
        Result result = commentsService.comments(id);
        return result;
    }
}
~~~



（2）service

CommentService --依赖--> AuthorService（通过comment id 查找 author）

​							--依赖--> CommentMapper（查找子评论）

~~~java
//===========================
//	CommentService
//===========================
package com.zxf.service;

public interface CommentsService {
    Result comments(Integer id);
}

//===========================
//	CommentServiceImpl
//===========================
package com.zxf.service.impl;

@Service
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private AuthorService authorService;

    @Override
    public Result comments(Integer id) {
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
}


//===========================
//	AuthorService
//===========================
package com.zxf.service;

public interface AuthorService {
    /**
     * 根据comment的id，查找作者信息
     */
    Author findAuthorByCommentId(Long commentId);
}


//===========================
//	CommentServiceImpl
//===========================
package com.zxf.service.impl;

@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private AuthorMapper authorMapper;
    
    @Override
    public Author findAuthorByCommentId(Long commentId) {
        Author author = authorMapper.findAuthorByCommentId(commentId);
        return author;
    }
}

~~~



（3）mapper

~~~java
//===========================
//	CommentMapper
//===========================
package com.zxf.mapper;

public interface CommentMapper extends BaseMapper<Comment> {
}

//===========================
//	AuthorMapper
//===========================
package com.zxf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxf.pojo.Author;

public interface AuthorMapper extends BaseMapper<Author> {
    /**
     * 根据comment id 查找author具体信息
     */
    Author findAuthorByCommentId(Long commentId);
}
~~~

~~~xml
<!-------------------------->
<!--AuthorMapper		-->                            
<!--------------------------->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zxf.mapper.AuthorMapper">
    
    <select id="findAuthorByCommentId" parameterType="Long" resultType="com.zxf.pojo.Author">
        select
            id,create_date,account,password,nickname,avatar,admin,deleted
        from
            `ms_author`
        where
                id =
                (
                    select author_id
                    from `ms_comment`
                    where id = #{commentId}
                )
    </select>
</mapper>
~~~



#### 2.文章具体内容

url：/articles/view/{id}

请求方式：POST

请求参数：

（1）参数名

| 参数名称 | 参数类型 | 说明               |
| -------- | -------- | ------------------ |
| id       | long     | 文章id（路径参数） |

（2）参数位置

路径参数

返回数据：

~~~java
{
    success: true, 
    code: 200, 
    msg: "success",
    data: {文章对象}
}
~~~



（1）controller

~~~java
package com.zxf.controller;

@RestController
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable Long id){
        return articleService.findArticleById(id);
    }

}
~~~



（2）service

**ArticleService** 

- --依赖--> TagService（查找文章id对应的tags）

- --依赖--> CategoryService（查找文章id对应的category）

- --依赖--> AuthorService（查找文章id对应的author）
- --依赖--> CommentService（查找文章id对应的comments）

~~~java
//========================
//	articleservice
//========================
package com.zxf.service;
public interface ArticleService {

    Result findArticleById(Long id);
}
//========================
//	articleserviceimpl
//========================
package com.zxf.service.impl;
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

    @Override
    @Traditional
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
        /*
        但是写在这里不好，因为对数据库的更新操作会影响把已经查到的数据传回给客户端
        也就是说更新不完成，就无法return
        而且万一更新出错还会直接导致客户端接受不到数据
        
        如何解决？--->线程池技术
        Article articleNew = new Article();
        articleNew.setViewCounts(article.getViewCounts()+1);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id).eq("view_counts",article.getViewCounts());
        articleMapper.update(articleNew, wrapper);
        */
        return Result.success(article);
    }
}

//========================
//	TagService
//========================
package com.zxf.service;
public interface TagService {

    /**
     *根据文章id查询Tag的具体对象
     */
    List<Tag> findTagsByArticleId(Long articleId);
}

//========================
//	TagServiceImpl
//========================
package com.zxf.service.impl;
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> findTagsByArticleId(Long articleId) {
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);
        return tags;
    }
}

//...
~~~



##### 1.线程池技术

上面的代码中，因为对数据库的更新操作会影响把已经查到的数据传回给客户端。也就是说数据库更新不完成，就无法return；而且万一更新出错还会直接导致客户端接收不到数据。这显然是我们不想看到的，那么如何解决呢？

------>线程池技术        

思想：单独开辟一个线程，用来完成数据库的更新。这样就不会影响把已经查到的数据传回给客户端了。

开发步骤：（倒着想）

~~~java
class ArticleServiceImpl{
    
    //某线程服务
    @Autowired
    private ThreadPoolService threadPoolService;
    
     public Result findArticleById(Long id) {
         //...获得article完成各种装载
         
         //开辟一个线程(服务)完成数据的更新
         threadPoolService.updateViewCounts(articleMapper,article);
         
         //返回
         return Result.success(article);
     }
}
~~~

--->ThreadPoolService

~~~java
package com.zxf.service;
@Service
public class ThreadPoolService {
    //期望此操作在线程池执行不会影响原有主线程
    //这里线程池不了解可以去看JUC并发编程
    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {

        Long id = article.getId();
        Integer viewCounts = article.getViewCounts();
        Article articleUpdate = new Article();
        articleUpdate.setViewCounts(viewCounts+1);
        QueryWrapper<Article> updateWrapper = new QueryWrapper<>();
        //根据id更新
        updateWrapper.eq("id",id).
            eq("view_counts",viewCounts);//改之前再确认这个值有没有被其他线程抢先修改，类似于CAS操作 cas加自旋，加个循环就是cas

        //实体类加更新条件
        articleMapper.update(articleUpdate,updateWrapper);
        try {
            Thread.sleep(5000);
            System.out.println("更新完成了");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
~~~

--->注入Executor类

~~~java
package com.zxf.config;
@Configuration
@EnableAsync //开启多线程
public class ThreadPoolConfig {

    @Bean("taskExecutor")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(5);
        // 设置最大线程数
        executor.setMaxPoolSize(20);
        //配置队列大小
        executor.setQueueCapacity(Integer.MAX_VALUE);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("码神之路博客项目");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        executor.initialize();
        return executor;
    }
}
~~~



#### 3.评论

TIP：评论功能是必须要登录状态才能访问，所以需要将url加入到登录拦截器中。

~~~java
@Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截test接口，后续实际遇到需要拦截的接口时，在配置为真正的拦截接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test").addPathPatterns("/comments/create/change");
    }
~~~



url：/comments/create/change

请求方式：POST

请求参数：

（1）参数名称

| 参数名称  | 参数类型 | 说明           |
| --------- | -------- | -------------- |
| articleId | long     | 评论的文章     |
| content   | string   | 评论的内容     |
| parentId  | long     | 评论的哪个评论 |

（2）参数位置

请求体

返回数据：

~~~java
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": null
}
~~~



（1）controller



（2）service



（3）mapper



（4）pojo



### 8.发布文章

url：/articles/publish

请求方式：POST

请求参数：

（1）请求参数

| 请求参数 | 请求类型                | 说明               |
| -------- | ----------------------- | ------------------ |
| id       | long                    | 文章id（编辑有值） |
| title    | string                  | 文章标题           |
| body     | string                  | 文章内容           |
| category | object                  | 文章类别           |
| summary  | string                  | 文章概述           |
| tags     | [{标签1},{标签2},{...}] | 文章标签           |

（2）参数位置

请求体

返回数据：

~~~java
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": {"id":12232323}//生成的文章id
}
~~~



（1）controller

~~~java
package com.zxf.controller;

@RestController
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping("publish")
    //发布文章，需要先登录，所以需要将路径加到登录拦截器中
    public Result publishArticle(@RequestBody ArticleParams articleParams){
        return articleService.publishArticle(articleParams);
    }
}

//===========================
//	参数对象ArticleParams
//===========================
package com.zxf.vo.params;
@Data
public class ArticleParams {

    private ArticleBody body;
    //TIP:因为前端写文章使用markdown编辑器，
    // 所以不仅有String content，还会有String bodyHtml

    private Category category;

    private String summary;

    private List<Tag> tags;

    private String title;
}

//===========================
//	ArticleBody
//===========================
package com.zxf.vo.params;
@Data
public class ArticleBody {
    private String content;
    private String contentHtml;
}

~~~



（2）service

~~~java
//===========================
//	ArticleService
//===========================
package com.zxf.service;

public interface ArticleService {

    Result publishArticle(ArticleParams articleParams);
}

//===========================
//	ArticleServiceImpl
//===========================
package com.zxf.service.impl;

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


        //装载article表
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
            //坑：每一次插入成功之后，MP会默认将生成的id进行回写，
            // 所以下一次插入会导致id重复。所以需要将id置为null

            articleTagVo.setArticleId(articleVo.getId());
            articleTagVo.setTagId(tagId);
            articleTagVoMapper.insert(articleTagVo);
        }

        //返回生成的新增的文章id
        return Result.success(articleVo.getId());
    }
}
~~~



（3）mapper

~~~java
package com.zxf.mapper;

public interface ArticleVoMapper extends BaseMapper<ArticleVo> {
}

public interface ArticleTagVoMapper extends BaseMapper<ArticleTagVo> {
}
~~~



（4）pojo--->讲究的是一一对应

~~~java
//===========================
//	ArticleVo
//===========================
package com.zxf.vo;
@Data
@TableName("ms_article")
public class ArticleVo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer commentCounts;
    private Long createDate;
    private String summary;
    private String content;
    private String contentHtml;
    private String title;
    private Integer viewCounts;
    private Integer weight;


    private Long authorId;

    private Integer categoryId;

}

//===========================
//	ArticleTagVo
//===========================
package com.zxf.vo;
@Data
@TableName("ms_article_tag")
public class ArticleTagVo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private Integer tagId;
}
~~~

