package com.limemul.easssue.api;

import com.limemul.easssue.api.dto.news.KwdArticleDto;
import com.limemul.easssue.api.dto.news.ArticleListDto;
import com.limemul.easssue.entity.ArticleLog;
import com.limemul.easssue.entity.Kwd;
import com.limemul.easssue.entity.User;
import com.limemul.easssue.entity.UserKwd;
import com.limemul.easssue.jwt.JwtProvider;
import com.limemul.easssue.repo.KwdRepo;
import com.limemul.easssue.service.ArticleLogService;
import com.limemul.easssue.service.ArticleService;
import com.limemul.easssue.service.UserKwdService;
import com.limemul.easssue.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Slf4j
public class NewsApi {

    private final ArticleService articleService;
    private final UserService userService;
    private final ArticleLogService articleLogService;
    private final UserKwdService userKwdService;
    private final KwdRepo kwdRepo;

    /**
     * 인기 기사 리스트 반환
     * 조건: page = 0
     */
    @GetMapping("/popular")
    public ArticleListDto firstPopularNews(){
        log.info("[Starting request] GET /news/popular");
        ArticleListDto result = articleService.getPopularArticle(0);
        log.info("[Finished request] GET /news/popular");
        return result;
    }

    /**
     * 인기 기사 리스트 반환 api
     * 조건: page >= 0
     * firstPopularNews api 필요없을지도..!
     */
    @GetMapping("/popular/page/{page}")
    public ArticleListDto popularNews(@PathVariable Integer page){
        log.info("[Starting request] GET /news/popular/page/{}", page);
        ArticleListDto result = articleService.getPopularArticle(page);
        log.info("[Finished request] GET /news/popular/page/{}", page);
        return result;
    }

    /**
     * 구독 키워드 기사 리스트 반환 api
     */
    @GetMapping("/subscribe/{kwdId}/page/{page}")
    public KwdArticleDto kwdNews(@RequestHeader HttpHeaders headers, @PathVariable Long kwdId, @PathVariable Integer page){
        log.info("[Starting request] GET /news/subscribe/{}/page/{}", kwdId, page);
        Optional<Kwd> targetKwd = kwdRepo.findById(kwdId);
        if (targetKwd.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 키워드입니다.");
        }

        Optional<User> user = JwtProvider.getUserFromJwt(userService, headers);
        if (user.isPresent()){
            log.info("user id is {}", user.get().getId());

            List<UserKwd> userKwdList = userKwdService.getSubscKwdList(user.get());
            Boolean flag = false;
            for( UserKwd userKwd : userKwdList) {
                if (targetKwd.get().equals(userKwd.getKwd())) {
                    flag = true;
                    break;
                }
            }
            log.info("flag is {}", flag);
            if ( !flag ) {
                throw new IllegalArgumentException("유저가 구독하지 않은 키워드입니다.");
            }
        }

        KwdArticleDto result = articleService.getSubsArticle(targetKwd.get(), page);

        log.info("[Finished request] GET /news/subscribe/{}/page/{}", kwdId, page);
        return result;

    }

    /**
     * 선택 키워드의 기사리스트 반환 api
     * 단, 연관키워드 또는 추천키워드 일 경우
     */
    @GetMapping("/recommend/{kwdId}/page/{page}")
    public ArticleListDto recommendNews(@RequestHeader HttpHeaders headers, @PathVariable Long kwdId, @PathVariable Integer page){
        log.info("[Starting request] GET /news/recommend/{}/page/{}", kwdId, page);

        Optional<Kwd> targetKwd = kwdRepo.findById(kwdId);

        if(targetKwd.isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 키워드입니다.");
        }

        ArticleListDto result = articleService.getRecommendedArticle(targetKwd.get(), page);

        log.info("[Finished request] GET /news/recommend/{}/page/{}", kwdId, page);
        return result;
    }


    /**
     * 기사 로그 남기기
     *  [로그인 o] 해당 사용자가 언제 무슨 카테고리의 무슨 기사 읽었는지 로그 남기기
     *  (로그인 했을때만 호출)
     */
    @PostMapping("/log/{articleId}")
    public boolean logReadArticle(@RequestHeader HttpHeaders headers,@PathVariable Long articleId){
        log.info("[Starting request] POST /news/log/{}",articleId);

        //사용자 정보 불러오기
        Optional<User> optionalUser = JwtProvider.getUserFromJwt(userService, headers);

        //로그인 안하면 아무 작업 안함
        if(optionalUser.isEmpty()){
            return false;
        }

        //기사 로그 남기기
        User user = optionalUser.get();
        ArticleLog articleLog = articleLogService.logReadArticle(user, articleId);
        log.info("userId: {}, articleId: {}, clickTime: {}",user.getId(),articleId,articleLog.getClickTime());

        log.info("[Finished request] POST /news/log/{}",articleId);
        return true;
    }

    /**
     * 테스트용 사용자
     */
    private User getUser(Long userId) {
        return userService.getUserByEmail("user"+userId+"@xx.xx");
    }
}
