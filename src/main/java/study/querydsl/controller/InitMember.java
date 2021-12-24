package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {

    private final InitMamberService initMamberService;

    @PostConstruct
    public void init() {
        initMamberService.init();
    }

    @Component
    static class InitMamberService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            Team teamA = new Team("TeamA");
            Team teamB = new Team("teamB");

            em.persist(teamA);
            em.persist(teamB);

            for (int i = 1; i < 100; i++) {
                Team selectedTeam = i%2 ==0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, selectedTeam));
            }
        }
    }

}
