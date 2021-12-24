package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    MemberJpaRepository repository;

    @BeforeEach
    public void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void selectAll() throws Exception {
        List<Member> result = repository.findAll();

        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    public void searchBuilder() throws Exception {
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setTeamName("teamB");
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        List<MemberTeamDto> result = repository.searchBuilder(condition);
        assertThat(result).extracting("username").containsExactly("member4");
    }

    @Test
    public void search() throws Exception {
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setTeamName("teamB");
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        List<MemberTeamDto> result = repository.search(condition);
        assertThat(result).extracting("username").containsExactly("member4");
    }
}