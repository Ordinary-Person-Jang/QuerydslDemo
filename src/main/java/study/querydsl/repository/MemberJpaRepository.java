package study.querydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

import static org.springframework.util.StringUtils.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public void save(Member member) {
        em.persist(member);
    }

    public List<Member> findAll() {
        return queryFactory
                .selectFrom(member)
                .fetch();

    }
    public List<Member> findByUsername(String username) {
        return queryFactory
                .selectFrom(member)
                .where(member.username.eq(username))
                .fetch();
    }

    public List<MemberTeamDto> searchBuilder(MemberSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(condition.getUsername())) {
            builder.and(member.username.eq(condition.getUsername()));
        }
        if (hasText(condition.getTeamName())) {
            builder.and(team.teamname.eq(condition.getTeamName()));
        }
        if (condition.getAgeLoe() != null) {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }
        if (condition.getAgeGoe() != null) {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("userId"),
                        member.username,
                        member.age,
                        team.id,
                        team.teamname.as("teamName")
                ))
                .from(member)
                .where(builder)
                .fetch();
    }

    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id.as("userId"),
                        member.username,
                        member.age,
                        team.id,
                        team.teamname.as("teamName")
                ))
                .from(member)
                .where(usernameEq(condition.getUsername()),
                       teamnameEq(condition.getTeamName()),
//                       AgeBetween(condition.getAgeGoe(), condition.getAgeLoe())
                        AgeGoe(condition.getAgeGoe()),
                        AgeLoe(condition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression AgeBetween(Integer ageGoe, Integer ageLoe) {
        return AgeGoe(ageGoe).and(AgeLoe(ageLoe));
    }


    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? member.username.eq(username) : null;
    }
    private BooleanExpression teamnameEq(String teamname) {
        return hasText(teamname) ? team.teamname.eq(teamname) : null;
    }
    private BooleanExpression AgeGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }
    private BooleanExpression AgeLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }
}


