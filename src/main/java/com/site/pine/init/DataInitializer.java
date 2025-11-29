package com.site.pine.init;

import com.site.pine.entity.group.GroupCategoryList;
import com.site.pine.repository.group.GroupCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final GroupCategoryRepository groupCategoryRepository;

    public DataInitializer(GroupCategoryRepository groupCategoryRepository) {
        this.groupCategoryRepository = groupCategoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 이미 데이터가 있으면 초기화하지 않음
        if (groupCategoryRepository.count() == 0) {
            groupCategoryRepository.save(new GroupCategoryList(1, "애니메이션 & 코스프레", "Anime & Cosplay"));
            groupCategoryRepository.save(new GroupCategoryList(2, "미술 / 예술", "Art"));
            groupCategoryRepository.save(new GroupCategoryList(3, "비즈니스 & 금융", "Business & Finance"));
            groupCategoryRepository.save(new GroupCategoryList(4, "수집품 & 기타 취미", "Collectibles & Other Hobbies"));
            groupCategoryRepository.save(new GroupCategoryList(5, "교육 & 직업 / 경력", "Education & Career"));
            groupCategoryRepository.save(new GroupCategoryList(6, "패션 & 뷰티", "Fashion & Beauty"));
            groupCategoryRepository.save(new GroupCategoryList(7, "음식 & 음료", "Food & Drinks"));
            groupCategoryRepository.save(new GroupCategoryList(8, "게임", "Games"));
            groupCategoryRepository.save(new GroupCategoryList(9, "건강", "Health"));
            groupCategoryRepository.save(new GroupCategoryList(10, "집 & 정원", "Home & Garden"));
            groupCategoryRepository.save(new GroupCategoryList(11, "인문학 & 법률", "Humanities & Law"));
            groupCategoryRepository.save(new GroupCategoryList(12, "정체성 & 인간관계", "Identity & Relationships"));
            groupCategoryRepository.save(new GroupCategoryList(13, "인터넷 문화", "Internet Culture"));
            groupCategoryRepository.save(new GroupCategoryList(14, "영화 & TV", "Movies & TV"));
            groupCategoryRepository.save(new GroupCategoryList(15, "음악", "Music"));
            groupCategoryRepository.save(new GroupCategoryList(16, "자연 & 야외 활동", "Nature & Outdoors"));
            groupCategoryRepository.save(new GroupCategoryList(17, "뉴스 & 정치", "News & Politics"));
            groupCategoryRepository.save(new GroupCategoryList(18, "장소 & 여행", "Places & Travel"));
            groupCategoryRepository.save(new GroupCategoryList(19, "대중문화", "Pop Culture"));
            groupCategoryRepository.save(new GroupCategoryList(20, "질문 & 이야기", "Q&As & Stories"));
            groupCategoryRepository.save(new GroupCategoryList(21, "독서 & 글쓰기", "Reading & Writing"));
            groupCategoryRepository.save(new GroupCategoryList(22, "과학", "Sciences"));
            groupCategoryRepository.save(new GroupCategoryList(23, "공포 / 오싹한 것", "Spooky"));
            groupCategoryRepository.save(new GroupCategoryList(24, "스포츠", "Sports"));
            groupCategoryRepository.save(new GroupCategoryList(25, "기술 / 테크", "Technology"));
            groupCategoryRepository.save(new GroupCategoryList(26, "자동차 / 탈것", "Vehicles"));
            groupCategoryRepository.save(new GroupCategoryList(27, "웰니스 / 자기관리", "Wellness"));
            groupCategoryRepository.save(new GroupCategoryList(28, "성인 콘텐츠", "Adult Content"));
            groupCategoryRepository.save(new GroupCategoryList(29, "성인 주제 / 성숙한 주제", "Mature Topics"));
            groupCategoryRepository.save(new GroupCategoryList(30, "자유 / 기타", "Free / Etc"));
        }
    }
}