package com.smartsched.scheduler.algorithm;

import com.smartsched.academicyear.entity.AcademicYear;
import com.smartsched.common.enums.RoomType;
import com.smartsched.faculty.entity.Faculty;
import com.smartsched.room.entity.Room;
import com.smartsched.room.repository.RoomRepository;
import com.smartsched.scheduler.config.SchedulerConfig;
import com.smartsched.scheduler.engine.SchedulerState;
import com.smartsched.scheduler.enums.WorkingDay;
import com.smartsched.studentclass.entity.StudentClass;
import com.smartsched.subject.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CandidateGenerator {

    private final ConstraintChecker constraintChecker;
    private final RoomRepository roomRepository;
    private final SchedulerConfig schedulerConfig;
    private final ScheduleScorer scorer;

    public List<ScheduleCandidate> generateCandidates(
            StudentClass studentClass,
            Faculty faculty,
            Subject subject,
            AcademicYear academicYear,
            SchedulerState state
    ) {

        List<ScheduleCandidate> candidates = new ArrayList<>();

        int requiredPeriods = Math.max(
                1,
                subject.getContinuousPeriods()
        );

        /*
         * -----------------------------------------
         * BUILD ROOM LIST
         * -----------------------------------------
         */

        List<Room> rooms = new ArrayList<>();

        if (subject.getRequiresLabRoom()) {

            rooms.addAll(
                    roomRepository.findAll()
                            .stream()
                            .filter(room ->
                                    room.getRoomType() == RoomType.LAB)
                            .toList()
            );

        } else {

            Room preferred =
                    studentClass.getPreferredRoom();

            /*
             * BUGFIX: the preferred room used to be added
             * unconditionally, even when it was a LAB-type room.
             * If a class's "preferred room" was ever configured as
             * a lab (e.g. LAB1/LAB2/LAB101/LAB102), every non-lab
             * (theory) subject for that class would be scheduled
             * into that lab room, since it was the FIRST room in
             * the candidate list and usually scores highest.
             *
             * A preferred room must never be used for a subject
             * that does NOT require a lab if that room itself is a
             * LAB room.
             */
            if (preferred != null
                    && preferred.getRoomType() != RoomType.LAB) {
                rooms.add(preferred);
            }

            roomRepository.findAll()
                    .stream()
                    .filter(room ->
                            preferred == null ||
                                    !room.getId()
                                            .equals(preferred.getId()))
                    .filter(room ->
                            room.getRoomType() != RoomType.LAB)
                    .forEach(rooms::add);
        }

        /*
         * -----------------------------------------
         * CHECK FACULTY WEEKLY CAPACITY ONCE
         * -----------------------------------------
         */

        int currentWeeklyLoad =
                state.getFacultyWeeklyPeriods(
                        faculty.getId()
                );

        if (currentWeeklyLoad + requiredPeriods
                > faculty.getMaxWeeklyHours()) {

            System.out.println(
                    "[REJECT] Weekly Faculty Limit | "
                            + subject.getSubjectCode()
                            + " | "
                            + faculty.getName()
                            + " Assigned="
                            + currentWeeklyLoad
                            + "/"
                            + faculty.getMaxWeeklyHours()
                            + " Required="
                            + requiredPeriods
            );

            return candidates;
        }

        /*
         * -----------------------------------------
         * GENERATE DAY / PERIOD / ROOM
         * -----------------------------------------
         */

        for (WorkingDay day : schedulerConfig.getWorkingDayList()) {

            int facultyDayLoad =
                    state.getFacultyPeriodsForDay(
                            faculty.getId(),
                            day
                    );

            /*
             * The faculty daily limit is checked
             * against the COMPLETE BLOCK.
             *
             * See SchedulerConfig.maxFacultyPeriodsPerDay for why this
             * is a named config value now instead of a hardcoded "5" -
             * that literal used to make periods 6/7/8 practically
             * unreachable for any faculty member already carrying a
             * normal load, regardless of remaining weekly capacity.
             */
            if (facultyDayLoad + requiredPeriods
                    > schedulerConfig.getMaxFacultyPeriodsPerDay()) {

                System.out.println(
                        "[REJECT] Daily Faculty Limit | "
                                + subject.getSubjectCode()
                                + " | "
                                + faculty.getName()
                                + " | "
                                + day
                                + " Current="
                                + facultyDayLoad
                                + " Required="
                                + requiredPeriods
                );

                continue;
            }

            for (
                    int startPeriod = 1;
                    startPeriod <=
                            schedulerConfig.getPeriodsPerDay()
                                    - requiredPeriods
                                    + 1;
                    startPeriod++
            ) {

                /*
                 * -----------------------------------------
                 * LUNCH CHECK
                 * -----------------------------------------
                 */

                boolean crossesLunch =
                        startPeriod
                                <= schedulerConfig.getLunchPeriod()
                                &&
                                startPeriod
                                        + requiredPeriods - 1
                                        >= schedulerConfig.getLunchPeriod();

                if (crossesLunch) {
                    continue;
                }

                /*
                 * -----------------------------------------
                 * THEORY DAILY LIMIT
                 * -----------------------------------------
                 */

                if (!subject.getRequiresLabRoom()) {

                    int subjectDayCount =
                            state.getSubjectCount(
                                    studentClass.getId(),
                                    subject.getId(),
                                    day
                            );

                    if (subjectDayCount >= 2) {

                        System.out.println(
                                "[REJECT] Subject Daily Limit | "
                                        + subject.getSubjectCode()
                                        + " "
                                        + day
                        );

                        continue;
                    }
                }

                /*
                 * -----------------------------------------
                 * PREVENT ADJACENT THEORY PERIODS
                 * -----------------------------------------
                 */

                if (!subject.getRequiresLabRoom()) {

                    if (state.hasAdjacentSubject(
                            studentClass.getId(),
                            subject.getId(),
                            day,
                            startPeriod
                    )) {

                        System.out.println(
                                "[REJECT] Adjacent Subject | "
                                        + subject.getSubjectCode()
                                        + " "
                                        + day
                                        + " P"
                                        + startPeriod
                        );

                        continue;
                    }

                    if (state.hasAdjacentFacultyPeriod(
                            faculty.getId(),
                            day,
                            startPeriod
                    )) {

                        continue;
                    }
                }

                /*
                 * -----------------------------------------
                 * TRY EVERY ROOM
                 * -----------------------------------------
                 */

                for (Room room : rooms) {

                    if (subject.getRequiresLabRoom()
                            && room.getRoomType()
                            != RoomType.LAB) {

                        continue;
                    }

                    if (!subject.getRequiresLabRoom()
                            && room.getRoomType()
                            == RoomType.LAB) {

                        continue;
                    }

                    boolean available = true;

                    /*
                     * -----------------------------------------
                     * CHECK EVERY PERIOD IN THE BLOCK
                     * -----------------------------------------
                     */

                    for (
                            int offset = 0;
                            offset < requiredPeriods;
                            offset++
                    ) {

                        int currentPeriod =
                                startPeriod + offset;

                        if (currentPeriod
                                == schedulerConfig
                                .getLunchPeriod()) {

                            available = false;
                            break;
                        }

                        if (!constraintChecker.canAllocate(
                                studentClass,
                                faculty,
                                room,
                                day,
                                currentPeriod,
                                state
                        )) {

                            System.out.println(
                                    "[REJECT] Clash | "
                                            + subject.getSubjectCode()
                                            + " "
                                            + day
                                            + " P"
                                            + currentPeriod
                            );

                            available = false;
                            break;
                        }
                    }

                    if (!available) {
                        continue;
                    }

                    /*
                     * -----------------------------------------
                     * VALID CANDIDATE
                     * -----------------------------------------
                     */

                    int score =
                            scorer.score(
                                    studentClass,
                                    faculty,
                                    subject,
                                    room,
                                    day,
                                    startPeriod,
                                    state
                            );

                    candidates.add(
                            new ScheduleCandidate(
                                    day,
                                    startPeriod,
                                    requiredPeriods,
                                    room,
                                    score
                            )
                    );
                }
            }
        }

        /*
         * Shuffle first so equal-score candidates don't
         * always produce exactly the same timetable.
         */
        Collections.shuffle(candidates);

        /*
         * Then prefer higher-quality candidates.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                ScheduleCandidate::getScore
                        )
                        .reversed()
        );

        return candidates;
    }
}