package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
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
                        AgeGoe(condition.getAgeGoe()),
                        AgeLoe(condition.getAgeLoe())
                )
                .fetch();
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

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> results = queryFactory
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
                        AgeGoe(condition.getAgeGoe()),
                        AgeLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
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
                        AgeGoe(condition.getAgeGoe()),
                        AgeLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(member.count())
                .from(member)
                .where(usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        AgeGoe(condition.getAgeGoe()),
                        AgeLoe(condition.getAgeLoe())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex2(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory
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
                        AgeGoe(condition.getAgeGoe()),
                        AgeLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(usernameEq(condition.getUsername()),
                        teamnameEq(condition.getTeamName()),
                        AgeGoe(condition.getAgeGoe()),
                        AgeLoe(condition.getAgeLoe())
                );

        return PageableExecutionUtils.getPage(content, pageable,()-> countQuery.fetchOne());
    }
}
